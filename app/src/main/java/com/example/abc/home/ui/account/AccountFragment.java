package com.example.abc.home.ui.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.abc.R;
import com.example.abc.authorization.data.UserFetcher;
import com.example.abc.authorization.data.model.User;
import com.example.abc.authorization.ui.SignInActivity;
import com.example.abc.databinding.FragmentAccountBinding;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private String originalPhoneNumber; // Lưu trữ số điện thoại ban đầu

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        UserFetcher.fetchUserInfo(new UserFetcher.Callback() {
            @Override
            public void onLoaded(User userInfo) {
                binding.tvUserName.setText(userInfo.getUserName());
                binding.tvPhoneNumber.setText(userInfo.getPhoneNumber());
                binding.tvEmail.setText(userInfo.getEmail());
                binding.tvAddress.setText(userInfo.getAddress());

                // Lưu trữ số điện thoại ban đầu từ dữ liệu người dùng
                originalPhoneNumber = userInfo.getPhoneNumber();
            }

            @Override
            public void onFailed() {
            }
        });

        binding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        binding.btnEdtInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditUserInfoDialog();
            }
        });

        return root;
    }

    private void showEditUserInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_user_info, null);
        builder.setView(dialogView)
                .setTitle("Sửa thông tin")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etUsername = dialogView.findViewById(R.id.et_edit_username);
                        EditText etPhone = dialogView.findViewById(R.id.et_edit_phone);
                        EditText etEmail = dialogView.findViewById(R.id.et_edit_email);
                        EditText etAddress = dialogView.findViewById(R.id.et_edit_address);

                        String editedUsername = etUsername.getText().toString();
                        String editedPhone = etPhone.getText().toString();
                        String editedEmail = etEmail.getText().toString();
                        String editedAddress = etAddress.getText().toString();

                        // Cập nhật thông tin người dùng chỉ với các trường đã điền
                        if (!editedUsername.isEmpty()) {
                            binding.tvUserName.setText(editedUsername);
                        }
                        if (!editedEmail.isEmpty()) {
                            binding.tvEmail.setText(editedEmail);
                        }
                        if (!editedAddress.isEmpty()) {
                            binding.tvAddress.setText(editedAddress);
                        }

                        // Kiểm tra và cập nhật thông tin số điện thoại chỉ khi có sự thay đổi và hợp lệ
                        if (!editedPhone.equals(originalPhoneNumber) && !editedPhone.isEmpty()) {
                            if (!isValidPhoneNumber(editedPhone)) {
                                // Hiển thị thông báo lỗi nếu số điện thoại không hợp lệ
                                Toast.makeText(getActivity(), "Số điện thoại không hợp lệ, mời nhập lại", Toast.LENGTH_SHORT).show();
                                return; // Ngưng việc cập nhật nếu số điện thoại không hợp lệ
                            } else {
                                // Cập nhật UI với số điện thoại mới
                                binding.tvPhoneNumber.setText(editedPhone);
                                // Cập nhật thông tin số điện thoại trong Firebase
                                updatePhoneNumberInFirebase(editedPhone);
                            }
                        }

                        // Cap nhat firebase với các thông tin khác
                        updateUserInfoInFirebase(editedUsername, editedEmail, editedAddress);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateUserInfoInFirebase(String editedUsername, String editedEmail, String editedAddress) {
        // Cập nhật thông tin người dùng (trừ số điện thoại) trong Firebase
    }

    private void updatePhoneNumberInFirebase(String editedPhone) {
        // Cập nhật thông tin số điện thoại trong Firebase
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Kiểm tra xem số điện thoại có chứa toàn ký tự số và có ít nhất 10 chữ số hay không
        return phoneNumber.matches("[0-9]+") && phoneNumber.length() >= 10;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
