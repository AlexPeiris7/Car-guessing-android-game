package com.alexiopeiris.mobiledevcw;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameOneActivity extends AppCompatActivity {

    private ArrayList<Drawable> carImages = new ArrayList<>();
    private ArrayList<String> carNames = new ArrayList<>();
    private ImageView imageView;
    private Spinner spinner;
    private int round;
    private int totalRounds;
    private int correctAnswers;
    private TextView timerTextView;
    private String difficulty;
    private String timerString;
    private Button submitBtn;
    private Button menuButton;
    private CountDownTimer cDT;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //cancel and stop timer if screen is rotated
        if(timerString.equals("TimerOn")){
            cDT.cancel();
            Log.d("TIMER", "Timer switched off");
        }
        outState.putString("difficulty", difficulty);
        outState.putString("timerString", timerString);
        outState.putString("submitBtnState", submitBtn.getText().toString());
        outState.putInt("round",round);
        outState.putInt("totalRounds",totalRounds);
        outState.putInt("correctAnswers",correctAnswers);
        outState.putInt("imageView_imgIndex",carImages.indexOf(imageView.getDrawable()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_one);
        Log.i("onCreate", "onCreate executed");
        //hiding the action bar
        getSupportActionBar().hide();
        imageView = findViewById(R.id.imageView);
        timerTextView = findViewById(R.id.timerTextView);
        spinner = findViewById(R.id.dropDownList);
        submitBtn = findViewById(R.id.identify_nextBtn);
        menuButton = findViewById(R.id.menuButton);
        //retrieving bundled information
        if (savedInstanceState != null) {
            difficulty = savedInstanceState.getString("difficulty");
            timerString = savedInstanceState.getString("timerString");
            String submitBtnState = savedInstanceState.getString("submitBtnState");
            submitBtn.setText(submitBtnState);
            round = savedInstanceState.getInt("round");
            totalRounds = savedInstanceState.getInt("totalRounds");
            correctAnswers = savedInstanceState.getInt("correctAnswers");
            int imgViewIndex = savedInstanceState.getInt("imageView_imgIndex");
            addImagesToList();
            addNamesToSpinner(difficulty);
            Drawable  drawable  = carImages.get(imgViewIndex);
            imageView.setImageDrawable(drawable);
            //
            if(timerString.equals("TimerOn")){
                //set the timer textView
            }
        }else {
            Bundle bundle = getIntent().getExtras();
            //getting messages from the main activity(difficulty and timer)
            timerString = (String) bundle.getString("timerState");
            difficulty = (String) bundle.getString("difficultyState");
            totalRounds = Integer.parseInt(bundle.getString("roundState"));
            addImagesToList();
            addNamesToSpinner(difficulty);
            setImg();
            correctAnswers = 0;
            round = 0;
            Log.i("GAME_STARTED", "Game started");
        }
    }


    //setting image to imageview, this is also the method that gets called when a new round is initiated
    private void setImg(){
        Random rand = new Random();
        int randomNum = rand.nextInt(carImages.size()-1);
        Drawable  drawable  = carImages.get(randomNum);
        imageView.setImageDrawable(drawable);
        if(timerString.equals("TimerOn")){
            gameTimer();
            Log.d("TIMER", "Timer switched on");
        }
    }

    //dinamically adding images from drawables to arraylist
    private void addImagesToList(){
        Field[] drawablesFields = com.alexiopeiris.mobiledevcw.R.drawable.class.getFields();
        for (Field field : drawablesFields) {
            try {

                //filtering through the field as the drawable class contains various files
                //so named all of the pictures starting with a " _ " so that when the images are
                //added to the drawable list only the images are put
                if(field.getName().startsWith("_")){
                    //System.out.println(field.getName());
                    carImages.add(getResources().getDrawable(field.getInt(null)));
                    String imgName = field.getName();

                    //Making letter after " _ " uppercase for displaying drop down list
                    // as you can only save files with lowercase char
                    ArrayList<String> tempList = new ArrayList<String>(Arrays.asList(imgName.split("")));
                    for(int i=0;i<tempList.size();i++){
                        if(tempList.get(i).equals("_")){
                            tempList.set(i+1,tempList.get(i+1).toUpperCase());
                        }
                    }
                    imgName="";
                    for(String s:tempList){
                        imgName=imgName+s;
                    }

                    //formatting string by replacing " _ " with " "(space)
                    imgName = imgName.replace("_"," ");
                    carNames.add(imgName);
                }
            } catch (Exception e) {
                Log.e("LOADING_IMAGES", "Loading images to list or adding names to list has failed!");
                e.printStackTrace();
            }
        }
    }

    //Dinamically adding image names to spinner drop down
    public void addNamesToSpinner(String difficulty) {
        ArrayList<String> dropDown = new ArrayList<>();
        //Change car names into manufacturer name only without type
        if(difficulty.equals("Easy")){
            for(String carName : carNames){
                String[] parts = carName.split(" ");
                //not taking parts[0] because as the car img name starts with a " _ " , the underscore
                // gets replaced with " " space.
                String manufacturerName = parts[1];
                //check if manufacturer name is already in the list to avoid duplication
                if(!dropDown.contains(manufacturerName)) {
                    dropDown.add(manufacturerName);
                }
            }
        }
        //Keep names with type as well so that its harder to guess
        else if(difficulty.equals("Hard")){
            dropDown=carNames;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, dropDown);
        spinner.setAdapter(dataAdapter);
    }

    public void submitBtn(View view) {
        if(submitBtn.getText().equals("Identify")){
            Log.d("SUBMIT", "Submit button clicked");
            if(timerString.equals("TimerOn")) {
                cDT.cancel();
                Log.d("TIMER", "Timer switched off");
            }
            checkGuess();
        }
        else if(submitBtn.getText().equals("Next")){
            Log.d("NEXT", "Next button clicked");
            setImg();
            submitBtn.setText("Identify");
            spinner.setEnabled(true);
        }
    }
    private void checkGuess(){
        round++;
        int imgIndex = carImages.indexOf(imageView.getDrawable());
        String guess = (String) spinner.getSelectedItem();
        String correctAnswer = "";
        if(difficulty.equals("Easy")){
            String[] parts = carNames.get(imgIndex).split(" ");
            correctAnswer = parts[1];
        }else{
            correctAnswer=carNames.get(imgIndex);
        }
        if(carNames.get(imgIndex).contains(guess)){
            showCorrectAlertDialog(correctAnswer);
            //carNames.remove(imgIndex);
            //carImages.remove(imgIndex);
            correctAnswers++;
        }else{
            showWrongAlertDialog(correctAnswer);
            //carNames.remove(imgIndex);
            //carImages.remove(imgIndex);
        }
        submitBtn.setText("Next");
        spinner.setEnabled(false);
    }
    private boolean gameOver(){
        if(round==totalRounds){
            Log.i("GAME_OVER", "Game ended");
            return true;
        }else{
            return false;
        }
    }
    private void backToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void menuPopUp(View view) {

        PopupMenu popup = new PopupMenu(this, menuButton);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Log.i("MENU", "Menu item was clicked");
                if(item.getTitle().equals("Exit")){
                    backToMain();
                }
                if(item.getTitle().equals("Restart")){
                    Intent intent = new Intent(GameOneActivity.this,GameOneActivity.class);
                    intent.putExtra("timerState", timerString);
                    intent.putExtra("difficultyState", difficulty);
                    intent.putExtra("roundState", String.valueOf(totalRounds));
                    startActivity(intent);
                }
                return true;
            }
        });
        popup.show();
    }
    private void gameOverPopUp(){
        if(gameOver()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            submitBtn.setText("Game Over!");
            alertDialogBuilder.setTitle("Game Over!");
            alertDialogBuilder.setMessage("Correct answers: " + correctAnswers + " out of "+round+". \n" +
                    "Go back to main screen or Play Again");
            alertDialogBuilder.setNegativeButton(
                    "Main",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            backToMain();
                        }
                    });
            alertDialogBuilder.setPositiveButton(
                    "Play Again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(GameOneActivity.this,GameOneActivity.class);
                            intent.putExtra("timerState", timerString);
                            intent.putExtra("difficultyState", difficulty);
                            intent.putExtra("roundState", String.valueOf(totalRounds));
                            startActivity(intent);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            spinner.setEnabled(false);
            submitBtn.setEnabled(false);
        }
    }
    private void showCorrectAlertDialog(String correctAnswer){
        //using the custom made alertDialog situated in the styles xml(resource dir)
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialogCorrect);
        alertDialogBuilder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        gameOverPopUp();
                        dialog.dismiss();
                    }
                });
        //if user gets answer correct remove the img and name from list
        alertDialogBuilder.setTitle("CORRECT!");
        alertDialogBuilder.setMessage("Correct answer: "+correctAnswer);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void showWrongAlertDialog(String correctAnswer){
//using the custom made alertDialog situated in the styles xml(resource dir)
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialogWrong);
        alertDialogBuilder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        gameOverPopUp();
                        dialog.dismiss();
                    }
                });
        alertDialogBuilder.setTitle("WRONG!");
        alertDialogBuilder.setMessage("Correct answer: " + correctAnswer);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void gameTimer() {
        //21000 which is 21 seconds to let
        cDT = new CountDownTimer(21000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("" + millisUntilFinished / 1000);
            }
            public void onFinish() {
                timerTextView.setText("0");
                checkGuess();
            }
        }.start();
    }
    @Override
    public void onBackPressed() {
        //cancel and stop timer if back button is clicked before going back
        if(timerString.equals("TimerOn")){
            cDT.cancel();
            Log.d("TIMER", "Timer switched off");
        }
        super.onBackPressed();
    }

}