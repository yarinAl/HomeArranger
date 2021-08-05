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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    TextView linkViewRegisterScreen;
    EditText inputName, inputEmail, inputPassword, inputConfirmPassword;
    Button btnRegister;
    FirebaseAuth mAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        initListener();

        mAuth = FirebaseAuth.getInstance();
    }

    private void initListener() {
        btnRegister.setOnClickListener(this);
        linkViewRegisterScreen.setOnClickListener(this);
    }

    private void initViews() {
        linkViewRegisterScreen = findViewById(R.id.btnViewRegisterScreen);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister: userRegister();break;
            case R.id.btnViewRegisterScreen: createAccountBtn(linkViewRegisterScreen);break;
        }
    }

    private void userRegister() {
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            inputName.setError("Full Name is Required");
            inputName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            inputEmail.setError("Email is Required");
            inputEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Invalid Email Address");
            inputEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            inputPassword.setError("Password is Required");
            inputPassword.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            inputConfirmPassword.setError("Confirm password is Required");
            inputConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            inputConfirmPassword.setError("Password do not Match");
            inputConfirmPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                User user = new User(name, email, password, confirmPassword);

                FirebaseDatabase.getInstance().getReference("User")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userId =mAuth.getCurrentUser().getUid();
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAccountBtn(TextView view) {
        String text = "Already have an account? Sign In";
        SpannableString SpanString = new SpannableString(text);

        ClickableSpan clickableSpanSignIn = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        };

        SpanString.setSpan(clickableSpanSignIn, 25, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(SpanString);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }
}