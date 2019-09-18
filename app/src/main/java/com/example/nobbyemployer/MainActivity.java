package com.example.nobbyemployer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText etEmail, etPassword;
    private String ssUsername, ssPassword;
    //URL para el login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etEmail.addTextChangedListener(twLogIn);
        etPassword.addTextChangedListener(twLogIn);

        btnLogin = findViewById(R.id.btnLogIn);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fillLogInUrl();
                //activateLogIn();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(0);
            }
        });
    }

    private TextWatcher twLogIn = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getStrings();
            enableButton();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void getStrings() {
        ssUsername = etEmail.getText().toString().trim();
        ssPassword = etPassword.getText().toString().trim();
    }

    private void enableButton() {
        btnLogin.setEnabled(!TextUtils.isEmpty(ssPassword) && !TextUtils.isEmpty(ssUsername));
    }

    private void openActivity(int opc) {
        Intent intent;
        if(opc == 1) {
            intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
