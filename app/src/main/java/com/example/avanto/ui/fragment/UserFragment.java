package com.example.avanto.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.avanto.R;
import com.example.avanto.data.model.User;
import com.example.avanto.databinding.FragmentUserBinding;
import com.example.avanto.ui.activity.SignActivity;
import com.example.avanto.ui.activity.UserEditActivity;
import com.example.avanto.ui.stateholder.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;


public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private FirebaseAuth mAuth;
    private static final String TAG = "PROFILE_TAG";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), UserEditActivity.class));
            }
        });
        /*binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/

        /*userTextName = binding.userTextUserName;
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
        });*/
    }

    private void loadUserInfo() {
        //Log.d(TAG, "loadUserInfo: Loading user info" + mAuth.getUid());

        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(mAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = "" + snapshot.child("userName").getValue();
                        String profileImage = "" + snapshot.child("userImage").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String userType = "" + snapshot.child("userPhoneNumber").getValue();

                        String formattedDate = formatTimestamp(Long.parseLong(timestamp));

                        binding.emailTv.setText(currentUser.getEmail());
                        binding.nameTv.setText(name);
                        binding.memberDateTv.setText(formattedDate);
                        binding.accountTypeTv.setText(userType);

                        if (!requireActivity().isFinishing()) {
                            Glide.with(UserFragment.this)
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_user)
                                    .into(binding.profileIv);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static final String formatTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();
        return date;
    }

    /*private void updateUserDetails(User user) {
        if (user != null) {
            userTextName.setText(user.getUserName());
            userTextPhone.setText(user.getUserPhoneNumber());
        }
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}