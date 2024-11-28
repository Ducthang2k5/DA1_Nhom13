package com.example.abc.store.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tanphuong.milktea.drink.data.model.Ingredient;
import com.tanphuong.milktea.drink.data.model.RealIngredient;
import com.tanphuong.milktea.store.data.model.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class StoreFetcher {
    private static final String TAG = "StoreFetcher";

    public static void fetchStores(@NonNull List<Ingredient> ingredients, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("store").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override

            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Store> stores = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        List<RealIngredient> storage = new ArrayList<>();
                        List refs = (List) document.get("storage");
                        for (Object object : refs) {
                            Map values = (Map) object;
                            DocumentReference ingredientRef = (DocumentReference) values.get("ingredient");
                            float quantity = ((Double) values.get("quantity")).floatValue();
                            for (Ingredient ingredient : ingredients) {
                                if (!ingredient.getId().equals(ingredientRef.getId())) {
                                    continue;
                                }
                                storage.add(new RealIngredient(
                                        ingredientRef.getId(),
                                        ingredient.getName(),
                                        ingredient.getUnit(),
                                        ingredient.getPricePerUnit(),
                                        quantity

                                ));
                            }
                        }


                        stores.add(new Store(
                                document.getId(),
                                document.getString("name"),
                                document.getString("address"),
                                document.getString("phone_number"),
                                document.getString("cover_image"),
                                document.getDouble("latitude"),
                                document.getDouble("longitude"),
                                storage
                        ));





                    }
                    callback.onLoaded(stores);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    callback.onFailed();
                }
            }
        });
    }
    public interface Callback{
        void onLoaded(List<Store> stores);

        void onFailed();

    }
}




