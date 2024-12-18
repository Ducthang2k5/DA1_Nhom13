package com.example.abc.home.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.abc.bill.data.BillFetcher;
import com.example.abc.bill.data.model.Bill;
import com.example.abc.bill.ui.adapter.BillHistoryAdapter;
import com.example.abc.databinding.FragmentOrderBinding;

import java.util.List;

public class OrderHistoryFragment extends Fragment {
    private FragmentOrderBinding binding;
    private BillHistoryAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BillFetcher.fetchBills(new BillFetcher.Callback() {
            @Override
            public void onLoaded(List<Bill> bills) {
                adapter = new BillHistoryAdapter(bills);
                binding.rlHistoryBill.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.rlHistoryBill.setAdapter(adapter);
            }

            @Override
            public void onFailed() {

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}