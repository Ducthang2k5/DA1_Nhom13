package com.example.abc.bill.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.abc.authorization.data.UserFactory;
import com.example.abc.authorization.data.model.User;
import com.example.abc.bill.data.model.Bill;
import com.example.abc.bill.data.model.BillStatus;
import com.example.abc.bill.data.model.PaymentMethod;
import com.example.abc.drink.data.IngredientFetcher;
import com.example.abc.drink.data.MilkTeaFetcher;
import com.example.abc.drink.data.ToppingFetcher;
import com.example.abc.drink.data.model.IceGauge;
import com.example.abc.drink.data.model.Ingredient;
import com.example.abc.drink.data.model.MilkTea;
import com.example.abc.drink.data.model.MilkTeaOrder;
import com.example.abc.drink.data.model.RealIngredient;
import com.example.abc.drink.data.model.Size;
import com.example.abc.drink.data.model.SugarGauge;
import com.example.abc.shipment.data.model.Shipper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static com.example.abc.bill.data.model.PaymentMethod.CASH;

public class BillFetcher {
    private static final String TAG = "BillFactory";
    PaymentMethod paymentMethod = PaymentMethod.CASH;
    public static List<Bill> fakeBills() {
        List<Bill> bills = new ArrayList<>();
        bills.add(new Bill("BIll1", new ArrayList<>(), new User(), new Shipper(), PaymentMethod.CASH, BillStatus.ON_GOING, new Date()));
        return bills;
    }

    public static void fetchBills(Callback callback) {
        IngredientFetcher.fetchIngredients(new IngredientFetcher.Callback() {
            @Override
            public void onLoaded(List<Ingredient> ingredients) {
                // Dựa theo dữ liệu nguyên liệu, lấy tiếp dữ liệu về topping
                ToppingFetcher.fetchToppings(ingredients, new ToppingFetcher.Callback() {
                    @Override
                    public void onLoaded(List<RealIngredient> toppings) {
                        // Dựa theo dữ liệu nguyên liệu, lấy tiếp dữ liệu về trà sữa
                        MilkTeaFetcher.fetchMilkTeas(ingredients, new MilkTeaFetcher.Callback() {
                            @Override
                            public void onLoaded(List<MilkTea> milkTeas) {
                                User user = UserFactory.getCurrentUser();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference userRef = db.collection("users").document(user.getId());
                                db.collection("bill")
                                        .whereEqualTo("user", userRef)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    List<Bill> bills = new ArrayList<>();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        Bill bill = new Bill();
                                                        // Thêm dữ liệu order
                                                        List<MilkTeaOrder> orders = new ArrayList<>();
                                                        List refs = (List) document.get("orders");
                                                        for (Object object : refs) {
                                                            Map values = (Map) object;
                                                            MilkTeaOrder order = new MilkTeaOrder();
                                                            // Lấy dữ liệu trà sữa của order này
                                                            DocumentReference milkTeaRef = (DocumentReference) values.get("milk_tea");
                                                            for (MilkTea milkTea : milkTeas) {
                                                                if (milkTeaRef.getId().equals(milkTea.getId())) {
                                                                    order.setMilkTea(milkTea);
                                                                    break;
                                                                }
                                                            }
                                                            // Lấy dữ liệu topping của order này
                                                            List<DocumentReference> toppingRefs = (List<DocumentReference>) values.get("toppings");
                                                            List<RealIngredient> orderToppings = new ArrayList<>();
                                                            for (DocumentReference toppingRef : toppingRefs) {
                                                                for (RealIngredient topping : toppings) {
                                                                    if (toppingRef.getId().equals(topping.getId())) {
                                                                        orderToppings.add(topping);
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                            Log.d(TAG, "onComplete: TOPPINGS " + toppingRefs.size());
                                                            order.setToppings(orderToppings);
                                                            // Lấy ra các dữ liệu còn lại
                                                            order.setNote((String) values.get("note"));
                                                            order.setSize(Size.fromString((String) values.get("size")));
                                                            order.setSugarGauge(SugarGauge.fromString((String) values.get("sugar_gauge")));
                                                            order.setIceGauge(IceGauge.fromString((String) values.get("ice_gauge")));
                                                            order.setQuantity(((Long) values.get("quantity")).intValue());
                                                            order.calculateCost();
                                                            orders.add(order);
                                                        }
                                                        bill.setOrders(orders);
                                                        // Các dữ liệu còn lại
                                                        bill.setId(document.getId());
                                                        bill.setDate(document.getDate("created_date"));
                                                        bill.setStatus(BillStatus.fromString(document.getString("status")));
                                                        bill.calculateTotalPrice();
                                                        bills.add(bill);
                                                    }callback.onLoaded(bills);
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                    callback.onFailed();
                                                }
                                            }
                                        });
                            }

                            @Override
                            public void onFailed() {
                            }
                        });
                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }

            @Override
            public void onFailed() {
            }
        });
    }

    public interface Callback {
        void onLoaded(List<Bill> bills);

        void onFailed();
    }
}
