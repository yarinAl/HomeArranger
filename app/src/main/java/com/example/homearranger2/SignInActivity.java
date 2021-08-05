package com.example.homearranger2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
//שיניתי
    TextView linkViewLoginScreen, linkForgetPassword;
    Button btnLogin;
    EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initViews();
        initListener();

        mAuth = FirebaseAuth.getInstance();

    }

    private void initListener() {
        btnLogin.setOnClickListener(this);
        linkViewLoginScreen.setOnClickListener(this);
        linkForgetPassword.setOnClickListener(this);
    }

    private void initViews() {
        linkViewLoginScreen = findViewById(R.id.btnViewLoginScreen);
        linkForgetPassword = findViewById(R.id.forgetPassword);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin: userLogin();break;
            case R.id.btnViewLoginScreen: createAccountBtn(linkViewLoginScreen);break;
            case R.id.forgetPassword: forgetPasswordBtn(linkForgetPassword);break;
        }
    }

    private void userLogin() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.isEmpty()) {
            inputEmail.setError("Email is Missing");
            inputEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Invalid Email Address");
            inputEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            inputPassword.setError("Password is Missing");
            inputEmail.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(SignInActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        }
    }

    private void forgetPasswordBtn(TextView view) {
        String text = "Forget Password?";
        SpannableString SpanString = new SpannableString(text);

        ClickableSpan clickableSpanSignIn = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(SignInActivity.this, ForgetPasswordActivity.class));
            }
        };

        SpanString.setSpan(clickableSpanSignIn, 0, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(SpanString);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void createAccountBtn(TextView view) {
        String text = "Don't have an account? Sign Up";
        SpannableString SpanString = new SpannableString(text);

        ClickableSpan clickableSpanSignIn = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        };

        SpanString.setSpan(clickableSpanSignIn, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(SpanString);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }
}