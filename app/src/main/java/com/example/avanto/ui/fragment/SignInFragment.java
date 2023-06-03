package com.example.avanto.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.avanto.R;
import com.example.avanto.databinding.DialogForgotPasswordBinding;
import com.example.avanto.databinding.FragmentSignInBinding;
import com.example.avanto.ui.stateholder.viewmodel.AuthViewModel;
import com.example.avanto.ui.activity.HomeActivity;


public class SignInFragment extends Fragment {

    private static final String FILE_EMAIL = "rememberMe";
    private AuthViewModel authViewModel;
    private FragmentSignInBinding binding;
    private DialogForgotPasswordBinding bindingForgotPassword;
    TextView forgotPassword;
    private EditText signInUserEmail, signInUserPassword;
    private CheckBox checkBox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

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

        authViewModel.getSignInSuccessLiveData().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "SignIn successful.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "SignIn failed.", Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getResetPasswordSuccessLiveData().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getActivity(), "Check your email", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Unable to send, failed", Toast.LENGTH_SHORT).show();
            }
        });

        forgotPassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            bindingForgotPassword = DialogForgotPasswordBinding.inflate(getLayoutInflater());
            EditText emailBox = bindingForgotPassword.dialogEmailBox;

            builder.setView(bindingForgotPassword.getRoot());
            AlertDialog dialog = builder.create();

            bindingForgotPassword.dialogButtonReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userEmail = emailBox.getText().toString();

                    if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                        emailBox.setError("Enter a valid email");
                        return;
                    }

                    authViewModel.resetPassword(userEmail);
                    dialog.dismiss();
                }
            });

            bindingForgotPassword.dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            dialog.show();
        });

        // Обработка нажатия кнопки входа в аккаунт
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

                    if (TextUtils.isEmpty(userEmail))
                        signInUserEmail.setError("Email field cannot be empty!");
                    else if (TextUtils.isEmpty(userPassword))
                        signInUserPassword.setError("Password field cannot be empty!");
                    else {
                        authViewModel.signIn(userEmail, userPassword);
                    }
                }
                else
                {
                    // Если кнопка checkBox не активирована, то данные не сохраняются в полях TextInputEditText
                    requireContext().getSharedPreferences(FILE_EMAIL, Context.MODE_PRIVATE).edit().clear().apply();

                    if (TextUtils.isEmpty(userEmail))
                        signInUserEmail.setError("Email field cannot be empty!");
                    else if (TextUtils.isEmpty(userPassword))
                        signInUserPassword.setError("Password field cannot be empty!");
                    else {
                        authViewModel.signIn(userEmail, userPassword);
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