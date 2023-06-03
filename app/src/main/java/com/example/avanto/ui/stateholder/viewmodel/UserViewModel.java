package com.example.avanto.ui.stateholder.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.avanto.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<User> userLiveData;
    private final DatabaseReference userReference;

    public UserViewModel() {
        userLiveData = new MutableLiveData<>();
        userReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void loadUserData(String userId) {
        userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userLiveData.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок
                userLiveData.setValue(null);
            }
        });
    }
}
