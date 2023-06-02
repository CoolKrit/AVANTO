package com.example.avanto.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avanto.R;
import com.example.avanto.data.ReadWriteUserDetails;
import com.example.avanto.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private EditText signUpUserName, signUpUserPhone, signUpUserEmail, signUpUserPassword;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signUpUserName = binding.signupUserInputEditField;
        signUpUserPhone = binding.signupPhoneInputEditField;
        signUpUserEmail = binding.signupUserEmailInputEditField;
        signUpUserPassword = binding.signupUserPasswordInputEditField;
        Button signUpButton = binding.signupButton;
        TextView toSignInText = binding.signupToSignInText;

        // Изменение цвета в тексте только определённого слова
        SpannableString spannableString = new SpannableString(toSignInText.getText().toString());
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.app_selected));
        int start = spannableString.toString().indexOf("Sign In");
        int end = start + "Sign In".length();
        spannableString.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toSignInText.setText(spannableString);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Users");

                String userName = signUpUserName.getText().toString();
                String userPhone = signUpUserPhone.getText().toString();
                String userEmail = signUpUserEmail.getText().toString();
                String userPassword = signUpUserPassword.getText().toString();

                if (TextUtils.isEmpty(userName)) {
                    signUpUserName.setError("Name field cannot be empty!");
                }
                else if (TextUtils.isEmpty(userEmail)) {
                    signUpUserEmail.setError("Email field cannot be empty!");
                }
                else if (TextUtils.isEmpty(userPassword)) {
                    signUpUserPassword.setError("Password field cannot be empty!");
                }
                else {
                    signUp(userName, userPhone, userEmail, userPassword);
                }
            }
        });

        toSignInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragment);
            }
        });
    }

    private void signUp(String userName, String userPhone, String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();

                            // Ввод пользовательских данных в Firebase Realtime Database
                            ReadWriteUserDetails readWriteUserDetails = new ReadWriteUserDetails(userName, userPhone);
                            reference.child(currentUser.getUid()).setValue(readWriteUserDetails);
                            Toast.makeText(getContext(), "You have signup successfully!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(getView()).navigate(R.id.action_signUpFragment_to_signInFragment);
                        } else {
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}