package com.alexiopeiris.mobiledevcw;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameThreeActivity extends AppCompatActivity {

    private ArrayList<Drawable> carImages = new ArrayList<>();
    private ArrayList<String> carNames = new ArrayList<>();
    private ImageButton carImage1;
    private ImageButton carImage2;
    private ImageButton carImage3;
    private TextView carNameTextView;
    private TextView timerTextView;
    private CountDownTimer cDT;
    private int round;
    private int totalRounds;
    private int correctAnswers;
    private String difficulty;
    private String timerString;
    private Button submitBtn;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("difficulty", difficulty);
        outState.putString("timerString", timerString);
        outState.putString("submitBtnState", submitBtn.getText().toString());
        outState.putInt("round",round);
        outState.putInt("totalRounds",totalRounds);
        outState.putInt("correctAnswers",correctAnswers);
        outState.putInt("imageButton_imgIndex1",carImages.indexOf(carImage1.getDrawable()));
        outState.putInt("imageButton_imgIndex2",carImages.indexOf(carImage2.getDrawable()));
        outState.putInt("imageButton_imgIndex3",carImages.indexOf(carImage3.getDrawable()));
        outState.putString("carNameTextView", carNameTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_three);
        //hiding the action bar
        getSupportActionBar().hide();
        Log.i("onCreate", "onCreate executed");
        submitBtn = findViewById(R.id.submitBtn);
        timerTextView = findViewById(R.id.timerTextView);
        carImage1 = findViewById(R.id.carImg1);
        carImage2 = findViewById(R.id.carImg2);
        carImage3 = findViewById(R.id.carImg3);
        carNameTextView = findViewById(R.id.carNameTextView);
        //retrieving bundled information
        if (savedInstanceState != null) {
            difficulty = savedInstanceState.getString("difficulty");
            timerString = savedInstanceState.getString("timerString");
            String submitBtnState = savedInstanceState.getString("submitBtnState");
            submitBtn.setText(submitBtnState);
            round = savedInstanceState.getInt("round");
            totalRounds = savedInstanceState.getInt("totalRounds");
            correctAnswers = savedInstanceState.getInt("correctAnswers");
            addImagesToList();
            int imgViewIndex1 = savedInstanceState.getInt("imageButton_imgIndex1");
            Drawable  drawable  = carImages.get(imgViewIndex1);
            carImage1.setImageDrawable(drawable);
            int imgViewIndex2 = savedInstanceState.getInt("imageButton_imgIndex2");
            drawable  = carImages.get(imgViewIndex2);
            carImage2.setImageDrawable(drawable);
            int imgViewIndex3 = savedInstanceState.getInt("imageButton_imgIndex3");
            drawable  = carImages.get(imgViewIndex3);
            carImage3.setImageDrawable(drawable);
            carNameTextView.setText(savedInstanceState.getString("carNameTextView"));
            //if timer on starts timer
            if(timerString.equals("TimerOn")){
                //set the timer textView
            }
        }else {
            Bundle bundle = getIntent().getExtras();
            //getting messages from the main activity(difficulty and timer)
            timerString = bundle.getString("timerState");
            difficulty = bundle.getString("difficultyState");
            totalRounds = Integer.parseInt(bundle.getString("roundState"));
            addImagesToList();
            round = 0;
            correctAnswers = 0;
            newRound();
            Log.i("GAME_STARTED", "Game started");
        }
    }
    private void newRound(){
        round++;
        setImages_Name();
        submitBtn.setEnabled(false);
        carImage1.setEnabled(true);
        carImage2.setEnabled(true);
        carImage3.setEnabled(true);
        if(timerString.equals("TimerOn")){
            gameTimer();
            Log.d("TIMER", "Timer switched on");
        }
    }
    public void clickedImage1(View view){
        if(timerString.equals("TimerOn")) {
            cDT.cancel();
            Log.d("TIMER", "Timer switched off");
        }
        int userGuessIndex = carImages.indexOf(carImage1.getDrawable());
        carImage2.setEnabled(false);
        carImage3.setEnabled(false);
        checkUserGuess(userGuessIndex);
    }
    public void clickedImage2(View view){
        if(timerString.equals("TimerOn")) {
            cDT.cancel();
            Log.d("TIMER", "Timer switched off");
        }
        int userGuessIndex = carImages.indexOf(carImage2.getDrawable());
        carImage1.setEnabled(false);
        carImage3.setEnabled(false);
        checkUserGuess(userGuessIndex);
    }
    public void clickedImage3(View view){
        if(timerString.equals("TimerOn")) {
            cDT.cancel();
            Log.d("TIMER", "Timer switched off");
        }
        int userGuessIndex = carImages.indexOf(carImage3.getDrawable());
        carImage2.setEnabled(false);
        carImage1.setEnabled(false);
        checkUserGuess(userGuessIndex);
    }
    private void checkUserGuess(int userGuessIndex){
        //if timer runs out
        //-1 so that when -1 is passed as a param
        //it will behave as the timer ran out
        if(userGuessIndex==-1){
            showWrongAlertDialog();
            if(timerString.equals("TimerOn")){
                cDT.cancel();
                Log.d("TIMER", "Timer switched off");
            }
            carImage1.setEnabled(false);
            carImage2.setEnabled(false);
            carImage3.setEnabled(false);
            submitBtn.setEnabled(true);
            return;
        }
        int correctIndex=0;
        //temp list with only car manufacturer names to compare with name in textView
        ArrayList<String> tempCarNames = new ArrayList<>();
        if(difficulty.equals("Easy")){
            for(String manufacturerName:carNames){
                String[] splittedString = manufacturerName.split(" ");
                manufacturerName = splittedString[1];
                tempCarNames.add(manufacturerName);
            }
            String correctAnswer = carNameTextView.getText().toString();
            correctIndex = tempCarNames.indexOf(correctAnswer);

        }else if(difficulty.equals("Hard")){
            for(String manufacturerName:carNames){
                tempCarNames.add(manufacturerName);
            }
            String correctAnswer = carNameTextView.getText().toString();
            correctIndex = carNames.indexOf(correctAnswer);
        }
        if(tempCarNames.get(userGuessIndex).equals(tempCarNames.get(correctIndex))){
            correctAnswers++;
            showCorrectAlertDialog();
        }else{
            showWrongAlertDialog();
        }
        carImage1.setEnabled(false);
        carImage2.setEnabled(false);
        carImage3.setEnabled(false);
        submitBtn.setEnabled(true);
    }

    //setting images to imageviews and name to textView, this is also the method that gets called when a new round is initiated
    private void setImages_Name(){
        Random rand = new Random();
        int randomNum;
        //Arraylist for random numbers(used as index to retrieve images and car names)
        ArrayList<Integer> indexNumbers= new ArrayList<>();
        //ArrayList for manufacturer names
        ArrayList<String> carNamesList= new ArrayList<>();
        for(int i=0;i<3;i++){
            //loop that doesnt stop until it finds suitable images to be added(not same manufacturer)
            innerloop:
            while(true) {
                randomNum = rand.nextInt(carImages.size() - 1);
                String carName = "";
                //if difficulty is easy checking if manufacturer name of the cars is the same of one or more
                //cars in selected to be in the imageView
                if (difficulty.equals("Easy")) {
                    carName = carNames.get(randomNum);
                    String[] splittedString = carName.split(" ");
                    carName = splittedString[1];
                } else if (difficulty.equals("Hard")) {
                    carName = carNames.get(randomNum);
                }
                if (!indexNumbers.contains(randomNum) && !carNamesList.contains(carName)) {
                    indexNumbers.add(randomNum);
                    carNamesList.add(carName);
                    break innerloop;
                }
            }
        }
        Drawable  drawable  = carImages.get(indexNumbers.get(0));
        carImage1.setImageDrawable(drawable);
        Drawable  drawable2  = carImages.get(indexNumbers.get(1));
        carImage2.setImageDrawable(drawable2);
        Drawable  drawable3  = carImages.get(indexNumbers.get(2));
        carImage3.setImageDrawable(drawable3);
        //setting random name from list to textView
        randomNum = rand.nextInt(3);
        carNameTextView.setText(carNamesList.get(randomNum));
    }

    public void menuPopUp(View view) {
        Button menuButton = (Button) findViewById(R.id.menuButton);
        PopupMenu popup = new PopupMenu(this, menuButton);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Log.i("MENU", "Menu item was clicked");
                if(item.getTitle().equals("Exit")){
                    backToMain();
                }
                if(item.getTitle().equals("Restart")){
                    Intent intent = new Intent(GameThreeActivity.this,GameThreeActivity.class);
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
                            Intent intent = new Intent(GameThreeActivity.this,GameThreeActivity.class);
                            intent.putExtra("timerState", timerString);
                            intent.putExtra("difficultyState", difficulty);
                            intent.putExtra("roundState", String.valueOf(totalRounds));
                            startActivity(intent);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            submitBtn.setEnabled(false);
        }
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

    public void submitBtn(View view) {
        //cancel and stop timer if screen is rotated
        if(timerString.equals("TimerOn")){
            cDT.cancel();
            Log.d("TIMER", "Timer switched off");
        }
        Log.d("SUBMIT", "Submit button clicked");
        newRound();
    }
    private void showCorrectAlertDialog(){
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
        alertDialogBuilder.setTitle("CORRECT!");
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void showWrongAlertDialog(){
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
                // -1 if timer runs outs
                checkUserGuess(-1);
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