package com.example.avanto.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avanto.data.model.User;
import com.example.avanto.databinding.FragmentUserBinding;
import com.example.avanto.ui.activity.SignActivity;
import com.example.avanto.ui.stateholder.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private TextView userTextName, userTextEmail, userTextPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userTextName = binding.userTextUserName;
        userTextEmail = binding.userTextUserEmail;
        userTextPhone = binding.userTextUserPhone;
        Button signOut = binding.userButtonUserSignOut;

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            userViewModel.loadUserData(userID);

            userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    userTextName.setText(user.getUserName());
                    userTextEmail.setText(currentUser.getEmail());
                    userTextPhone.setText(user.getUserPhoneNumber());
                }
            });
        }

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignActivity.class);
                startActivity(intent);
            }
        });
    }
}