package com.example.avanto.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
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
    private FragmentSignInBinding binding;
    TextView forgotPassword;
    private EditText signInUserEmail, signInUserPassword;
    private CheckBox checkBox;
    private FirebaseAuth mAuth;

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
        forgotPassword = binding.forgotPasswordText;
        Button signInButton = binding.signinButton;
        TextView toSignUpText = binding.signinToSignupText;

        // Сохранение email и password
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(FILE_EMAIL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String saveEmail = sharedPreferences.getString("saveEmail", "");
        String savePassword = sharedPreferences.getString("savePassword", "");
        checkBox.setChecked(sharedPreferences.contains("checked") && sharedPreferences.getBoolean("checked", false));
        binding.signinEmailInputEditField.setText(saveEmail);
        binding.signinPasswordInputEditField.setText(savePassword);

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
                    // Если кнопка checkBox активирована, то сохраняются данные, которые были написаны в TextInputEditText
                    editor.putBoolean("checked", true);
                    editor.apply();
                    StoreDataUsingSharedPref(userEmail, userPassword);

                    if (TextUtils.isEmpty(userEmail)) {
                        signInUserEmail.setError("Email field cannot be empty!");
                    }
                    else if (TextUtils.isEmpty(userPassword)) {
                        signInUserPassword.setError("Password field cannot be empty!");
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
                                            Toast.makeText(getContext(), "SignIn failed.", Toast.LENGTH_SHORT).show();
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
                        requireContext().getSharedPreferences(FILE_EMAIL, Context.MODE_PRIVATE).edit().clear().apply();
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
                                            Toast.makeText(getContext(), "SignIn failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }

            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
                EditText emailBox = dialogView.findViewById(R.id.dialog_emailBox);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.dialog_buttonReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userEmail = emailBox.getText().toString();

                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                            //Toast.makeText(getActivity(), "Enter your registered Email", Toast.LENGTH_SHORT).show();
                            emailBox.setError("Enter your registered Email");
                            return;
                        }
                        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Check your Email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                                else {
                                    Toast.makeText(getActivity(), "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.dialog_buttonCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
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