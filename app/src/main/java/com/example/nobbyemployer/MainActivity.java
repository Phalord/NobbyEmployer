package com.example.nobbyemployer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText etEmail, etPassword;
    private String ssUsername, ssPassword;
    private URL dbLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
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
                try {
                    fillLogInUrl();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                activateLogIn();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(0);
            }
        });
    }

    private class DataBaseConnection extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL...urls) {
            try {
                HttpsURLConnection nbConnection = (HttpsURLConnection) urls[0].openConnection();
                BufferedReader bfReader = new BufferedReader(new InputStreamReader(nbConnection.getInputStream()));
                String answer = bfReader.readLine();
                bfReader.close();
                nbConnection.disconnect();
                return answer;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String answer) {
            super.onPostExecute(answer);
            if(answer != null) {
                switch (answer) {
                    case "1":
                        Toast.makeText(MainActivity.this, "El formato de correo es erróneo"
                                , Toast.LENGTH_SHORT).show();
                        break;
                    case "2":
                        Toast.makeText(MainActivity.this, "Inicio de Sesión correcto"
                                , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "3":
                        Toast.makeText(MainActivity.this, "Cuenta no activada"
                                , Toast.LENGTH_SHORT).show();
                        break;
                    case "4":
                        Toast.makeText(MainActivity.this, "Correo o contraseña no coinciden"
                                , Toast.LENGTH_SHORT).show();
                        break;
                }
            } else {
                Toast.makeText(MainActivity.this, "Fallo al inicio de sesión"
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void activateLogIn() {
        new DataBaseConnection().execute(dbLogIn);
    }

    private void fillLogInUrl() throws MalformedURLException{
        String liFormat = "C/login/"+ssUsername+","+ssPassword;
        dbLogIn = new URL("https://nobbyapi.000webhostapp.com/"+liFormat);
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
