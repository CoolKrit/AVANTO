package com.example.avanto.ui.fragment;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.avanto.R;
import com.example.avanto.databinding.FragmentSignUpBinding;
import com.example.avanto.ui.stateholder.viewmodel.AuthViewModel;


public class SignUpFragment extends Fragment {

    private AuthViewModel authViewModel;
    private FragmentSignUpBinding binding;
    private EditText signUpUserName, signUpUserPhone, signUpUserEmail, signUpUserPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

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

        authViewModel.getSignUpSuccessLiveData().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_signInFragment);
            } else {
                Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = signUpUserName.getText().toString();
                String userPhone = signUpUserPhone.getText().toString();
                String userEmail = signUpUserEmail.getText().toString();
                String userPassword = signUpUserPassword.getText().toString();

                if (TextUtils.isEmpty(userName))
                    signUpUserName.setError("Name field cannot be empty!");
                else if (TextUtils.isEmpty(userEmail))
                    signUpUserEmail.setError("Email field cannot be empty!");
                else if (TextUtils.isEmpty(userPassword))
                    signUpUserPassword.setError("Password field cannot be empty!");
                else
                    authViewModel.signUp(userEmail, userPassword, userName, userPhone);
            }
        });

        toSignInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragment);
            }
        });
    }
}