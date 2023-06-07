package com.example.avanto.ui.stateholder.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.avanto.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthViewModel extends ViewModel {
    private final MutableLiveData<Boolean> signInSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUpSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resetPasswordSuccessLiveData = new MutableLiveData<>();

    private final FirebaseAuth mAuth;
    private final FirebaseDatabase mDatabase;

    public AuthViewModel() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    public LiveData<Boolean> getSignInSuccessLiveData() {
        return signInSuccessLiveData;
    }

    public LiveData<Boolean> getSignUpSuccessLiveData() {
        return signUpSuccessLiveData;
    }

    public LiveData<Boolean> getResetPasswordSuccessLiveData() {
        return resetPasswordSuccessLiveData;
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        signInSuccessLiveData.setValue(true);
                    } else {
                        signInSuccessLiveData.setValue(false);
                    }
                });
    }

    public void signUp(String email, String password, String name, String phone, String userImage) {
        long timestamp = System.currentTimeMillis();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        DatabaseReference reference = mDatabase.getReference("Users");
                        reference.child(currentUser.getUid()).setValue(new User(name, phone, timestamp, userImage));
                        signUpSuccessLiveData.setValue(true);
                    } else {
                        signUpSuccessLiveData.setValue(false);
                    }
                });
    }

    public void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        resetPasswordSuccessLiveData.setValue(true);
                    } else {
                        resetPasswordSuccessLiveData.setValue(false);
                    }
                });
    }
}
