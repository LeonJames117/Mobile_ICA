package com.A0053718.mobile_ica;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Options_Menu extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_menu);
        Button Back_To_MM = findViewById(R.id.Option_Back_To_MM);
    }
    public void Back_To_MM(View v)
    {
        //
        finish();
    }
}