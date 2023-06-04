package com.example.avanto.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.avanto.data.model.User;
import com.example.avanto.databinding.FragmentUserBinding;
import com.example.avanto.ui.activity.SignActivity;
import com.example.avanto.ui.stateholder.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private TextView userTextName;
    private TextView userTextPhone;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userTextName = binding.userTextUserName;
        TextView userTextEmail = binding.userTextUserEmail;
        userTextPhone = binding.userTextUserPhone;
        Button signOut = binding.userButtonUserSignOut;

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            userViewModel.loadUserData(userID);

            userTextEmail.setText(currentUser.getEmail());
            userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), this::updateUserDetails);
        }

        signOut.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SignActivity.class);
            startActivity(intent);
        });
    }

    private void updateUserDetails(User user) {
        if (user != null) {
            userTextName.setText(user.getUserName());
            userTextPhone.setText(user.getUserPhoneNumber());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}