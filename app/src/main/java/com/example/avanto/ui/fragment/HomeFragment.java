package com.example.avanto.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avanto.R;
import com.example.avanto.data.model.User;
import com.example.avanto.data.model.Video;
import com.example.avanto.databinding.FragmentHomeBinding;
import com.example.avanto.ui.stateholder.adapter.FolderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView homeTextGreetings;
    private String homeGreetings;
    private FirebaseUser currentUser;

    /*private ArrayList<String> folderList = new ArrayList<>();
    private ArrayList<Video> videoList = new ArrayList<>();
    FolderAdapter folderAdapter;
    RecyclerView recyclerView;*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeTextGreetings = binding.homeTextGreetings;

        if (currentUser != null) {
            showHomeGreetings(currentUser);
        }
    }

    private void showHomeGreetings(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        // Получение пользовательской информации из Database "Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userDetails = snapshot.getValue(User.class);
                    if (userDetails != null) {
                        homeGreetings = userDetails.getUserName();
                        homeTextGreetings.setText(String.format("Welcome, %s", homeGreetings));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}