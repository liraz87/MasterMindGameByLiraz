package com.example.owner.mymastermindgame;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

public class InstructionsMenu extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_menu);
    }



    public void btn_instructions(View view) {
        Intent intent = new Intent(this, Instructions.class);
        startActivity(intent);
    }



    public void btn_how_to_play(View view) {
        Intent intent = new Intent(this, HowToPlay.class);
        startActivity(intent);
    }
}
