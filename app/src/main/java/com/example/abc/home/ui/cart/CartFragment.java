package com.example.abc.home.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.abc.bill.data.MilkTeaOrderFactory;
import com.example.abc.bill.ui.BillConfirmActivity;
import com.example.abc.bill.ui.adapter.MilkTeaOrderAdapter;
import com.example.abc.databinding.FragmentCartBinding;
import com.example.abc.drink.data.model.MilkTeaOrder;

import java.util.List;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private MilkTeaOrderAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        List<MilkTeaOrder> orders = MilkTeaOrderFactory.showCart();
        adapter = new MilkTeaOrderAdapter(orders, new MilkTeaOrderAdapter.Callback() {
            @Override
            public void onIncreaseQuantity(MilkTeaOrder order) {
                boolean isSuccess = MilkTeaOrderFactory.addOneMoreOrder(order);
                if (isSuccess) {
                    adapter.notifyDataSetChanged();
                    calculateTotalPrice();
                }
            }

            @Override
            public void onDecreaseQuantity(MilkTeaOrder order) {
                boolean isSuccess = MilkTeaOrderFactory.subtractOneOrder(order);
                if (isSuccess) {
                    adapter.notifyDataSetChanged();
                    calculateTotalPrice();
                }
            }
        });
        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BillConfirmActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        List<MilkTeaOrder> orders = MilkTeaOrderFactory.showCart();
        if (orders.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.clCart.setVisibility(View.GONE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.clCart.setVisibility(View.VISIBLE);
            binding.rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvCart.setAdapter(adapter);
            calculateTotalPrice();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void calculateTotalPrice() {
        binding.btnPay.setText("Thanh toán " + MilkTeaOrderFactory.estimatePrice() + "đ");
    }
}