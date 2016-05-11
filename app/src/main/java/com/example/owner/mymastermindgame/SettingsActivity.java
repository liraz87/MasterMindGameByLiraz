package com.example.owner.mymastermindgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;


    // The SettingsActivity class manages the game's settings.
    // The user can control the sound level and the "duplicates" option.
    // I save the data using ShredPreferences and load the data on the onCreate() on BoardActivity class.
    // I created a static boolean called "allowDuplicates" that get the value from the sharedPreferences on this activity
    // so I can easily referring it in a few methods on "BoardActivity" class

public class SettingsActivity extends ActionBarActivity {

    private AudioManager audioManager;
    CheckBox duplicatesCheckBox;
    static boolean allowDuplicates;






    // On the onCreate() I'm calling to initDuplicatesOption() method that manages the duplicates option
    // and initSoundLevel() that manages the sound level.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initDuplicatesOption();
        initSoundLevel();
    }






    // At the initDuplicatesOption() method I'm setting the checkBox state (check or unchecked) according to the last choice
    // that the user made, using SharedPreferences.
    // If the user change the checkBox state - the new information will be saved by using SharedPreferences in order to load it
    // on the next time.
    // I also change the boolean "allowDuplicates" value in order to update it's data so i can use it on "BoardActiviy" methods.

    private void initDuplicatesOption() {
        duplicatesCheckBox = (CheckBox) findViewById(R.id.allow_duplicates_button);
        duplicatesCheckBox.setChecked(getFromSharedPreferences(getBaseContext()));
        allowDuplicates = getFromSharedPreferences(getBaseContext());

        duplicatesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveInSharedPreferences(isChecked);
            }
        });
    }






    // The initSoundLevel() method allow to the user to control the sound level.
    // On that case I don't need to use SharedPreferences in order to load the last changes because it's automatically saved thanks to AudioManager object.

    private void initSoundLevel() {
        final MediaPlayer playSound = MediaPlayer.create(this, R.raw.new_blop_sound);

        try {
            SeekBar volumeSeekbar = (SeekBar) findViewById(R.id.soundSeekBar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                    playSound.start();
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress + 1, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    // These are the SharedPreferences methods where I'm actually saving and loading the data.

    static final String SHRP_DP = "duplicatesButton";

    static boolean getFromSharedPreferences(Context context) {
        allowDuplicates = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SHRP_DP, false);
        return allowDuplicates;
    }

    public void saveInSharedPreferences(boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHRP_DP, value);
        editor.apply();
        allowDuplicates = value;
    }
}
