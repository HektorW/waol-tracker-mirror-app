package com.waol.trackermirror;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText surnameInput;
    private EditText emailInput;
    private EditText heightInput;
    private EditText shoeSizeInput;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        this.nameInput = (EditText)findViewById(R.id.information_name_input);
        this.surnameInput = (EditText)findViewById(R.id.information_lastname_input);
        this.emailInput = (EditText)findViewById(R.id.information_email_input);
        this.heightInput = (EditText)findViewById(R.id.information_height_input);
        this.shoeSizeInput = (EditText)findViewById(R.id.information_shoesize_input);
        this.saveButton = (Button)findViewById(R.id.information_submit);

        this.nameInput.setText(getData(getString(R.string.saved_name)));
        this.surnameInput.setText(getData(getString(R.string.saved_surname)));
        this.emailInput.setText(getData(getString(R.string.saved_email)));
        this.heightInput.setText(getData(getString(R.string.saved_height)));
        this.shoeSizeInput.setText(getData(getString(R.string.saved_shoesize)));

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(getString(R.string.saved_name), nameInput.getText().toString());
                saveData(getString(R.string.saved_surname), surnameInput.getText().toString());
                saveData(getString(R.string.saved_email), emailInput.getText().toString());
                saveData(getString(R.string.saved_height), heightInput.getText().toString());
                saveData(getString(R.string.saved_shoesize), shoeSizeInput.getText().toString());

                startActivity(new Intent(InformationActivity.this, ConnectedActivity.class));
            }
        });
    }

    private void saveData(String key, String value){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String getData(String key) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }
}