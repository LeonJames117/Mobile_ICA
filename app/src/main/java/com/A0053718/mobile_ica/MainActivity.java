package com.A0053718.mobile_ica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button Start_Button;
    Button Options_Button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Start_Button = findViewById(R.id.Start_Game_Button);
        Options_Button = findViewById(R.id.Options_Button);

    }

    public void Launch_Game(View v)
    {
        Intent intent = new Intent(this,Game.class);
        startActivity(intent);
    }

    public void Launch_Options_Menu(View v)
    {
        Intent intent = new Intent(this,Options_Menu.class);
        startActivity(intent);
    }

}