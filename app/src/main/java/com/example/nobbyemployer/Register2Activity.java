package com.example.nobbyemployer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class Register2Activity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {
    private EditText etCurp, etAddress, etNumber;
    private String ssCurp, ssAddress, ssNumber, ssDate, ssName, ssEmail
            , ssPassword, ssState, ssDegree;
    private TextView tvDate;
    private Button btnRegister;
    private Spinner spStates, spDegrees;
    private CheckBox cbTerms;
    private ArrayAdapter<CharSequence> adptStates, adptDegree;
    private DatePickerDialog.OnDateSetListener myDateSetListener;
    private URL dbRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineLayout();

        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("Register");
        toolbar.setTitleTextColor(Color.parseColor("#EFF2F1"));
        toolbar.setBackgroundColor(Color.parseColor("#151B1A"));
        setSupportActionBar(toolbar);

        cbTerms.setMovementMethod(LinkMovementMethod.getInstance());

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpDialog = new DatePickerDialog(
                        Register2Activity.this,
                        android.R.style.Theme_Material_Dialog,
                        myDateSetListener,
                        year, month, day);
                dpDialog.show();
            }
        });

        cbTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableButton();
            }
        });

        myDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                ssDate = dayOfMonth+"-"+month+"-"+year;
                tvDate.setText(ssDate);
                enableButton();
            }
        };

        etNumber.addTextChangedListener(twRegister);
        etCurp.addTextChangedListener(twRegister);
        etAddress.addTextChangedListener(twRegister);

        spStates = findViewById(R.id.sStates);
        adptStates = ArrayAdapter.createFromResource(this, R.array.States, android.R.layout
                .simple_spinner_dropdown_item);
        adptStates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStates.setAdapter(adptStates);
        spStates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enableButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spDegrees = findViewById(R.id.sDegree);
        adptDegree = ArrayAdapter.createFromResource(this, R.array.Degrees,
                android.R.layout.simple_spinner_dropdown_item);
        adptDegree.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDegrees.setAdapter(adptDegree);
        spDegrees.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enableButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStrings();
                String formatRegister = "C/signup/"+ssEmail+","
                        +ssCurp+","+ssPassword+","+ssName+","+ssAddress+","
                        +ssDate+","+ssState+","+ssDegree;

                try {
                    dbRegister = new URL("https://nobbyapi.000webhostapp.com/"+formatRegister);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(Register2Activity.this, "New URL failed: "
                            +e, Toast.LENGTH_SHORT).show();
                }

                try {
                    new DataBaseConnection().execute(dbRegister);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Register2Activity.this, "dbConnection failed: "
                            +e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class DataBaseConnection extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
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
            if(answer.equals("1")) {
                Toast.makeText(Register2Activity.this, "El formato de correo es erróneo"
                        , Toast.LENGTH_SHORT).show();
            } else if(answer.equals("2")) {
                Intent intent = new Intent(Register2Activity.this, ConfirmationActivity.class);
                startActivity(intent);
            } else if(answer.equals("4")){
                Toast.makeText(Register2Activity.this,"El usuario ya está registrado"
                        ,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private TextWatcher twRegister = new TextWatcher() {
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

    private void enableButton() {
        btnRegister.setEnabled(!TextUtils.isEmpty(ssAddress)&& !TextUtils.isEmpty(ssCurp)
                && !TextUtils.isEmpty(ssNumber) && cbTerms.isChecked()
                && spStates.getSelectedItemPosition()!=0 && spDegrees.getSelectedItemPosition()!=0
                && !TextUtils.isEmpty(tvDate.getText().toString().trim()));
    }

    private void getStrings() {
        ssCurp = etCurp.getText().toString().trim();
        ssAddress = etAddress.getText().toString().trim();
        ssNumber = etNumber.getText().toString().trim();
        ssState = spStates.getSelectedItem().toString().trim();
        ssDegree = spDegrees.getSelectedItem().toString().trim();
        ssName = getIntent().getStringExtra("Username");
        ssEmail = getIntent().getStringExtra("EMail");
        ssPassword = getIntent().getStringExtra("Password");
    }

    private void defineLayout() {
        setContentView(R.layout.activity_register2);
        etCurp = findViewById(R.id.etCurp);
        etAddress = findViewById(R.id.etAddress);
        tvDate = findViewById(R.id.tvDate);
        etNumber = findViewById(R.id.etNumber);
        btnRegister = findViewById(R.id.btnRegister);
        cbTerms = findViewById(R.id.cbTerms);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
