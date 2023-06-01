package com.example.avanto.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avanto.R;
import com.example.avanto.databinding.FragmentSignInBinding;
import com.example.avanto.ui.activity.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignInFragment extends Fragment {

    private static final String FILE_EMAIL = "rememberMe";
    FragmentSignInBinding binding;
    EditText signInUserEmail, signInUserPassword;
    CheckBox checkBox;
    Button signInButton;
    TextView toSignUpText;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signInUserEmail = binding.signinEmailInputEditField;
        signInUserPassword = binding.signinPasswordInputEditField;
        checkBox = (CheckBox) binding.rememberMeCheckbox;
        signInButton = binding.signinButton;
        toSignUpText = binding.signinToSignupText;

        // Сохранение email и password
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(FILE_EMAIL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String saveEmail = sharedPreferences.getString("saveEmail", "");
        String savePassword = sharedPreferences.getString("savePassword", "");
        if (sharedPreferences.contains("checked") && sharedPreferences.getBoolean("checked", false)) {
            checkBox.setChecked(true);
        }
        else {
            checkBox.setChecked(false);
        }
        signInUserEmail.setText(saveEmail);
        signInUserPassword.setText(savePassword);

        // Изменение цвета в тексте только определённого слова
        SpannableString spannableString = new SpannableString(toSignUpText.getText().toString());
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.app_selected));
        int start = spannableString.toString().indexOf("Sign Up");
        int end = start + "Sign Up".length();
        spannableString.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toSignUpText.setText(spannableString);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = signInUserEmail.getText().toString();
                String userPassword = signInUserPassword.getText().toString();

                if (checkBox.isChecked()) {
                    editor.putBoolean("checked", true);
                    editor.apply();
                    StoreDataUsingSharedPref(userEmail, userPassword);

                    if (TextUtils.isEmpty(userEmail)) {
                        signInUserEmail.setError("User Email cannot be empty!");
                    }
                    else if (TextUtils.isEmpty(userPassword)) {
                        signInUserPassword.setError("User Password cannot be empty!");
                    }
                    else {
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "SignIn successful.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
                else {
                    if (TextUtils.isEmpty(userEmail)) {
                        signInUserEmail.setError("User Email cannot be empty!");
                    }
                    else if (TextUtils.isEmpty(userPassword)) {
                        signInUserPassword.setError("User Password cannot be empty!");
                    }
                    else {
                        requireContext().getSharedPreferences(FILE_EMAIL, Context.MODE_PRIVATE).edit().clear().commit();
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "SignIn successful.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });

        toSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });
    }

    private void StoreDataUsingSharedPref(String saveEmail, String savePassword) {
        SharedPreferences.Editor editor = requireContext().getSharedPreferences(FILE_EMAIL, Context.MODE_PRIVATE).edit();
        editor.putString("saveEmail", saveEmail);
        editor.putString("savePassword", savePassword);
        editor.apply();
    }
}