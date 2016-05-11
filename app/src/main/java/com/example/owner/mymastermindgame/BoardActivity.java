package com.example.owner.mymastermindgame;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BoardActivity extends ActionBarActivity {

    //    Hey Elad. A few words on the variables below:
    // 1. I created the "UNCHECKED" and "checkedId" variables in order to help me to determine if some view has chosen.
    //    So if I want to mark some view as not chosen, I'm setting it to be equal to "UNCHECKED" which means -1 - which means nothing.
    // 2. the "int[] colors" array represents the 6 colors from the drawable directory.
    //    NOTICE: each of these colors has 2 visual states that i defined using a "selector" on XML: checked and unChecked
    //    so the user can easily know which color he chose.
    // 3. "secret" is an "ArrayList" that holds a sequence of 4 Integers (from 0 to 5) that chose randomly on the generateSecret() method below.
    //    This is the sequence that the user need to discover.
    // 4. int[]guess represents each one of the user's guesses. This array contains 4 ints that equal to -1 so I can
    //     easily determine if one of the indexes in the array is empty.
    // 5. "guesses" is ArrayAdapter that receive "Guess" which is an inner-class (see below), and represents the whole guesses that made by the user.
    // 6. "userGaveUp" and "userWon" are static variables i used as anchors in order to know which DilaogFragment will show up when the game is over (onBackPressed() - see below)

    private final int UNCHECKED = -1;
    private int checkedId = UNCHECKED;
    final int[] colors = {R.drawable.red_point, R.drawable.blue_point, R.drawable.yellow_point, R.drawable.purple_point, R.drawable.green_point, R.drawable.aqua_point};
    private ArrayList<Integer> secret;
    final private int[] guess = new int[]{-1, -1, -1, -1};
    private final ArrayList<Guess> guesses = new ArrayList<>();
    private GuessArrayAdapter guessArrayAdapter;
    static boolean userGaveUp = false;
    static boolean userWon = false;






    // The onCreate calls to:
    // 1. generateSecret() - which generating a secret code randomly using an ArrayList (See below).
    // 2. initLayout() - which contains several views which need to be loaded when this activity launched. (trying to keep it organized..)
    // 3. getFromSharedPreferences - which loads the user's settings from the SettingsActivity class.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        generateSecret();
        initLayout();
        SettingsActivity.getFromSharedPreferences(this);
    }






    //  initLayout() contains the views that i need to use when the actual activity first launched.
    // It contains the listView that represents the listView where the users's guess will be presented , its adapter,
    // a RadioGroup that represent the 6 colors and a listener for this group (which play a sound when each of the colors was chosen, and set the "checkedId" value to the the checked button ID
    // in order to use this information in the onColorSet() method  - see below).
    // A MediaPlayer for the sound effect.

    private void initLayout() {
        ListView listView = (ListView) findViewById(R.id.list_item);
        guessArrayAdapter = new GuessArrayAdapter(this, android.R.layout.simple_list_item_1, guesses);
        listView.setAdapter(guessArrayAdapter);
        final RadioGroup coloredButtons = (RadioGroup) findViewById(R.id.colored_buttons);
        final MediaPlayer colorSelectedSound = MediaPlayer.create(this, R.raw.new_blop_sound);
        coloredButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checked_id) {
                checkedId = checked_id;
                colorSelectedSound.start();
            }
        });

    }






    // generateSecret() method generates the secret by taking Integers from the ArrayList called "colors" and adding it to ArrayList called "secret" - randomly

    private void generateSecret() {

        ArrayList<Integer> colors = new ArrayList<>(6);
        colors.add(0);
        colors.add(1);
        colors.add(2);
        colors.add(3);
        colors.add(4);
        colors.add(5);

        secret = new ArrayList<>(4);
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int result = random.nextInt(colors.size());
            secret.add(colors.get(result));

            // If the secret contains duplicates - the system won't remove the last number that was added so it can be added more than once :
            if (!SettingsActivity.allowDuplicates)
                colors.remove(result);
        }
    }






    // onColorSet() checks if one of the 6 colors was chose - (according to the RadioGroup listener on initLayout() method) using my "checkedId" and "UNCHECKED" anchors.
    // If one of them was chose - the showChosen() method will be called (below), which manages the whole behave.

    public void onColorSet(final View view) {

        if (checkedId == UNCHECKED)
            return;
        showChosen((RadioButton) findViewById(checkedId), view);
        if (!SettingsActivity.allowDuplicates)
            checkedId = UNCHECKED;
    }






    // showChosen() manages the visual and vocal behave when a color was chose and when a color was set.
    // "colorSelected" represents the color that was selected by the user.
    // NOTICE: the red button is the first view that i created on the RadioGroup. I learned that sequential views getting a sequential Id's,
    //         so I'm using this terminology here and in another methods in order to define which view was selected. It saves me a lot of switch & case..
    //         this is why i can do the following:
    //         int colorSelected = coloredPoint.getId() - R.id.red_button;
    // "blankPoint" represents the 4 empty holes where the user need to set the colors that he chose.

    private void showChosen(RadioButton coloredPoint, View blankPoint) {
        final MediaPlayer colorSettedSound = MediaPlayer.create(this, R.raw.new_blop_sound);
        int colorSelected = coloredPoint.getId() - R.id.red_button;
        blankPoint.setBackgroundResource(colors[colorSelected]);

        // TODO: 15/03/2016  - Do i want the colored buttons to become unclickable after the user chose a color on non-duplicates game?

        if (!SettingsActivity.allowDuplicates) {
            coloredPoint.setButtonDrawable(R.drawable.blank_point);
            coloredPoint.setClickable(false);
        }

        int blankSelected = blankPoint.getId() - R.id.first_dot;
        guess[blankSelected] = colorSelected;
        colorSettedSound.start();
    }






    // On buttonChk() will be operate when the check button will be pressed.
    // Here I'm doing a couple of things:
    // 1. I verify if a the "guess" contains "-1" (REMEMBER: guess is an array with 4 indexes {-1,-1,-1,-1) ).
    //    If it does - that means the user has not chose 4 colors but less. On that case he will see a toast that telling him to choose 4 colors before continue.
    // 2. I calculate the numbers of "bulls" and "pgiot":
    //    "bulls" represents a color in the "guess" array which has the same index like on the "secret" array.
    //    "pgiot" represents a color in the "guess" array which also exist on the the "secret" array but not at the same index.
    //     The calculating is a bit long because technically "bull" is also "pgia" and i had to make sure that it doesn't counting incorrectly.
    // 3. I add the actual "guess" to the "guesses" and updating the information using "notifyDataSetChanged()"
    // 4. I call to clear() method which returns the selected colors to their original colors before they have chosen,
    //    and setting the "guess" array to be indexed again to {-1,-1,-1,-1} in order to help me at the next time.
    // 5. I'm setting the sound that will be played once the button was pressed.
    // 6. If bulls=4 which means that the user has won - I call to victory() method.

    public void buttonChk(View view) {
        ArrayList<Integer> temp = new ArrayList<>(guess.length);
        for (int i : guess)
            temp.add(i);
        if (temp.contains(-1)) {
            Toast.makeText(this, "Please choose 4 colors", Toast.LENGTH_LONG).show();
            return;
        }


        int bulls = 0;
        int pgiot = 0;
        int[] colorsHndl = {0, 0, 0, 0, 0, 0};
        boolean[] handled = {false, false, false, false};
        for (int i = 0; i < secret.size(); i++) {
            if (secret.get(i).equals(guess[i])) {
                bulls++;
                handled[i] = true;
                colorsHndl[guess[i]]++;
            }
        }
        for (int i = 0; i < secret.size(); i++) {
            if (handled[i]) continue;
            if (colorsHndl[guess[i]] >= countColor(guess[i])) continue;
            if (secret.contains(guess[i])) {
                colorsHndl[guess[i]]++;
                pgiot++;
            }
        }

        // TODO:   If I want the list View to be updated from the top - I should insert the details to the zero position like the following code:
        // guesses.add(0, new Guess(guess, bulls, pgiot));
        guesses.add(new Guess(guess, bulls, pgiot));
        guessArrayAdapter.notifyDataSetChanged();
        clear();
        final MediaPlayer checkButtonSound = MediaPlayer.create(this, R.raw.check_button_sound);
        checkButtonSound.start();

        if (bulls == 4) {
            victory();
        }
    }






    // This is one of the calculates that I do to find out the exact number of "bulls" and "pgiot" on buttonChk() method.

    private int countColor(int gues) {
        int sum = 0;
        for (int i : secret)
            if (gues == i) sum++;
        return sum;
    }






    // on clear() method I'm turning the 6 colors and the 4 blank holes back to their original state before they have chosen.
    // I'm also setting the "guess" array to be indexed again to {-1,-1,-1,-1} in order to help me at the next time.

    private void clear() {

        for (int i = 0; i < 4; i++) {
            findViewById(R.id.first_dot + i).setBackgroundResource(R.drawable.blank_point);
            guess[i] = -1;
        }

        for (int i = 0; i < 6; i++) {
            //findViewById(R.id.red_button +i).setBackgroundResource(colors[i]);
            ((RadioButton) findViewById(R.id.red_button + i)).setButtonDrawable(colors[i]);
            ((RadioButton) findViewById(R.id.red_button + i)).setChecked(false);
            ((RadioButton) findViewById(R.id.red_button + i)).setClickable(true);
        }
    }






    // The buttonGiveUp method will be operate when the user pressed on the "Give up" button.
    // It makes the 6 colors to disappear so the user can no longer to choose a color, and sets the blank holes array in index "x"
    // to be equal to the the "secret" array at the same index and by that exposing the code.
    // It also plays a loser sound.

    public void buttonGiveUp(View view) {

        final MediaPlayer giveUpSound = MediaPlayer.create(this, R.raw.give_up_sound);
        giveUpSound.start();
        userGaveUp = true;

        for (int i = 0; i < 4; i++) {
            guess[i] = secret.get(i);
            findViewById(R.id.first_dot + i).setBackgroundResource(colors[guess[i]]);
        }
        // for (int i=0; i<colors.length; i++)
        //   findViewById(R.id.red_button + i).setVisibility(View.GONE);

        findViewById(R.id.colored_buttons).setVisibility(View.INVISIBLE);
        findViewById(R.id.button_check).setVisibility(View.INVISIBLE);

    }






    // Here I created 2 classes: the class "Guess" presents the whole data that a single guess should have,
    // and the class "GuessArrayAdapter" used as adapter to the "Guess" as accepted.

    // The "GuessArrayAdapter" manages the visibility of the black and white dots according to "bulls" and "pgiot" variables,
    // by changing their visibility (GONE/VISIBLE) or changing their color.
    // (black dots represents the "bulls" and white dots represents the "pgiot")
    // It also presents the number of tries inside the list - so to user can easily know how many guesses he made.

    class Guess {
        int[] guess;
        int bulls;
        int pgiot;

        public Guess(int[] guess, int bulls, int pgiot) {
            this.guess = new int[4];
            System.arraycopy(guess, 0, this.guess, 0, 4);
            this.bulls = bulls;
            this.pgiot = pgiot;
        }
    }

    class GuessArrayAdapter extends ArrayAdapter<Guess> {
        Context context;

        public GuessArrayAdapter(Context context, int resourceId, List<Guess> objects) {
            super(context, resourceId, objects);
            this.context = context;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Button giveUpButton = (Button) findViewById(R.id.btn_giveUp);
            View rowView;
            Guess currentGuess = guesses.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_layout, parent, false);
            int WhiteDotsToHide = 4 - (currentGuess.pgiot + currentGuess.bulls);
            int turnDotsToBlack = currentGuess.bulls;

            for (int i = 0; i < WhiteDotsToHide; i++) {
                rowView.findViewById(R.id.v4 - i).setVisibility(View.GONE);
            }
            for (int i = 0; i < turnDotsToBlack; i++) {
                rowView.findViewById(R.id.v1 + i).setBackgroundResource(R.drawable.black_dot);
            }
            if (WhiteDotsToHide >= 2)
                ((LinearLayout) rowView.findViewById(R.id.group_Of_Dots)).getChildAt(1).setVisibility(View.GONE);
            int first = rowView.findViewById(R.id.first_point).getId();
            for (int i = 0; i < 4; i++) {
                ((ImageView) rowView.findViewById(first + i)).setImageResource(colors[currentGuess.guess[i]]);
                if (position > 8) {
                    giveUpButton.setVisibility(View.VISIBLE);
                }
            }

            ((TextView) rowView.findViewById(R.id.tries_counter)).setText(position + 1 + "");
            return rowView;
        }

    }






    // The victory() method will be operated when the user has won (if bulls == 4 on buttonChk() method).
    // It plays a winning sound and sets the static boolean "userWon" to be true in order to define
    // which DialogFragment to show on the onBackPressed() method below.
    private void victory() {
        new WonDlg().show(getSupportFragmentManager(), "");
        final MediaPlayer victorySound = MediaPlayer.create(this, R.raw.victory_sound);
        victorySound.start();
        userWon = true;
    }






    // The onBackPressed() method defines which DialogFragment will be show in each case:
    // If the user won, If the user gave up, or if the user decided to pause the game.
    // It also turning the anchors to be false in order to make sure that the right DialogFragment will be
    // showed next time.
    @Override
    public void onBackPressed() {
        if (userGaveUp) {
            new LoseDlg().show(getSupportFragmentManager(), "");
            userGaveUp = false;
        } else if (userWon) {
            finish();
            userWon = false;
        } else new QuitDialog().show(getSupportFragmentManager(), "");
    }






    @Override
    protected void onPause() {
        super.onPause();
    }
}