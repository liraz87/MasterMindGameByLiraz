package com.example.owner.mymastermindgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

// This is the main activity.
// It only contains 3 methods that open 3 different activities:
// 1. play - which open the game activity
// 2. btn_settings - which open the settings activity
// 3. btn_instructions_menu - which open the instructions menu activity
// I deleted the onOptionSelected and etc because they are not necessary

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void play(View view) {
        Intent intent = new Intent(this, BoardActivity.class);
        startActivity(intent);
    }

    public void btn_settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void btn_instructions_menu(View view) {
        Intent intent = new Intent(this, InstructionsMenu.class);
        startActivity(intent);

    }
}
