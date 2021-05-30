package com.alexiopeiris.mobiledevcw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    Switch timerSwitch;
    Switch difficultySwitch;
    String difficultyMessage;
    String timerMessage;
    String roundsMessage;
    Spinner roundsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hiding the action bar
        getSupportActionBar().hide();
        timerSwitch = findViewById(R.id.switchTimer);
        difficultySwitch = findViewById(R.id.difficultySwitch);
        //setting defaults
        difficultyMessage = "Easy";
        timerMessage = "TimerOff";
        roundsMessage = "3";
        roundsSpinner = findViewById(R.id.rounds);
        setSpinner();

    }
    public void setSpinner(){
        //setting rounds in spinner
        ArrayList<String> dropDown = new ArrayList<>();
        dropDown.add("1");
        dropDown.add("2");
        dropDown.add("3");
        dropDown.add("4");
        dropDown.add("5");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, dropDown);
        roundsSpinner.setAdapter(dataAdapter);
    }

    public void setMessages(){
        //getting timer
        if(timerSwitch.isChecked()){
            timerMessage = "TimerOn";
        }else{
            timerMessage = "TimerOff";
        }
        //getting difficulty
        if(difficultySwitch.isChecked()){
            difficultyMessage = "Hard";
        }else{
            difficultyMessage = "Easy";
        }
        //getting rounds
        roundsMessage = (String) roundsSpinner.getSelectedItem();
    }

    public void openGameOneActivity(View view) {
        setMessages();
        Intent intent = new Intent(this, GameOneActivity.class);
        intent.putExtra("timerState", timerMessage);
        intent.putExtra("difficultyState", difficultyMessage);
        intent.putExtra("roundState", roundsMessage);
        startActivity(intent);
    }

    public void openGameTwoActivity(View view) {
        setMessages();
        Intent intent = new Intent(this, GameTwoActivity.class);
        intent.putExtra("timerState", timerMessage);
        intent.putExtra("difficultyState", difficultyMessage);
        intent.putExtra("roundState", roundsMessage);
        startActivity(intent);
    }

    public void openGameThreeActivity(View view) {
        setMessages();
        Intent intent = new Intent(this, GameThreeActivity.class);
        intent.putExtra("timerState", timerMessage);
        intent.putExtra("difficultyState", difficultyMessage);
        intent.putExtra("roundState", roundsMessage);
        startActivity(intent);
    }

    public void openGameFourActivity(View view) {
        setMessages();
        Intent intent = new Intent(this, GameFourActivity.class);
        intent.putExtra("timerState", timerMessage);
        intent.putExtra("difficultyState", difficultyMessage);
        intent.putExtra("roundState", roundsMessage);
        startActivity(intent);
    }
}