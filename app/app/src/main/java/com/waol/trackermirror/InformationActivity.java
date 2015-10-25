package com.waol.trackermirror;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.waol.trackermirror.utils.Settings;

public class InformationActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    private EditText nameInput;
    private EditText surnameInput;
    private EditText emailInput;
    private EditText heightInput;
    private EditText shoeSizeInput;
    private EditText ipInput;
    private EditText portInput;
    private Button saveButton;

    private boolean hasRunnedAnimations = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        this.nameInput = (EditText)findViewById(R.id.information_name_input);
        this.surnameInput = (EditText)findViewById(R.id.information_lastname_input);
        this.emailInput = (EditText)findViewById(R.id.information_email_input);
        this.heightInput = (EditText)findViewById(R.id.information_height_input);
        this.shoeSizeInput = (EditText)findViewById(R.id.information_shoesize_input);
        this.ipInput = (EditText)findViewById(R.id.information_ip_input);
        this.portInput = (EditText)findViewById(R.id.information_port_input);
        this.saveButton = (Button)findViewById(R.id.information_submit);

        this.nameInput.setText(Settings.get(this, getString(R.string.saved_name)));
        this.surnameInput.setText(Settings.get(this, (getString(R.string.saved_surname))));
        this.emailInput.setText(Settings.get(this, (getString(R.string.saved_email))));
        this.heightInput.setText(Settings.get(this, (getString(R.string.saved_height))));
        this.shoeSizeInput.setText(Settings.get(this, (getString(R.string.saved_shoesize))));
        this.ipInput.setText(Settings.get(this, getString(R.string.saved_ip)));
        this.portInput.setText(Settings.get(this, getString(R.string.saved_port)));

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.store(InformationActivity.this, getString(R.string.saved_name), nameInput.getText().toString());
                Settings.store(InformationActivity.this, getString(R.string.saved_surname), surnameInput.getText().toString());
                Settings.store(InformationActivity.this, getString(R.string.saved_email), emailInput.getText().toString());
                Settings.store(InformationActivity.this, getString(R.string.saved_height), heightInput.getText().toString());
                Settings.store(InformationActivity.this, getString(R.string.saved_shoesize), shoeSizeInput.getText().toString());
                Settings.store(InformationActivity.this, getString(R.string.saved_ip), ipInput.getText().toString());
                Settings.store(InformationActivity.this, getString(R.string.saved_port), portInput.getText().toString());

                startActivity(new Intent(InformationActivity.this, ConnectedActivity.class));
            }
        });

        findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if(!hasRunnedAnimations){
            hasRunnedAnimations = true;
            ViewGroup root = (ViewGroup)findViewById(R.id.information_wrapper);

            int startOffset = 0;
            int offset = 50;
            for (int i = 0; i < root.getChildCount(); i++) {
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.swipe_in_animation);
                animation.setStartOffset(startOffset);
                startOffset += offset;

                root.getChildAt(i).startAnimation(animation);
            }
        }
    }
}