package com.alexiopeiris.mobiledevcw;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameFourActivity extends AppCompatActivity {
    private ArrayList<Drawable> carImages = new ArrayList<>();
    private ArrayList<String> carNames = new ArrayList<>();
    private ImageView carImage1;
    private ImageView carImage2;
    private ImageView carImage3;
    private TextView timerTextView;
    private EditText carOneGuess;
    private EditText carTwoGuess;
    private EditText carThreeGuess;
    private int round;
    private int totalRounds;
    private int correctAnswers;
    private String timerString;
    private String difficulty;
    private Button submitBtn;
    private int score;
    private int tries;
    private String correctCarNames;
    private CountDownTimer cDT;
    //boolean variables to check if images have been guessed or not
    private boolean boolGuessOne;
    private boolean boolGuessTwo;
    private boolean boolGuessThree;
    //booolean to check if the round is over(used for the timer)
    private boolean roundOver;
    @Override
    public void onSaveInstanceState(Bundle outState) {
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
        outState.putInt("imageView_imgIndex1",carImages.indexOf(carImage1.getDrawable()));
        outState.putInt("imageView_imgIndex2",carImages.indexOf(carImage2.getDrawable()));
        outState.putInt("imageView_imgIndex3",carImages.indexOf(carImage3.getDrawable()));
        outState.putString("carOneGuessTextView", carOneGuess.getText().toString());
        outState.putString("carTwoGuessTextView", carTwoGuess.getText().toString());
        outState.putString("carThreeGuessTextView", carThreeGuess.getText().toString());
        outState.putInt("score",score);
        outState.putInt("tries",tries);
        outState.putString("correctCarNames", correctCarNames);
        outState.putBoolean("boolGuessOne",boolGuessOne);
        outState.putBoolean("boolGuessTwo",boolGuessTwo);
        outState.putBoolean("boolGuessThree",boolGuessThree);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_four);
        //hiding the action bar
        getSupportActionBar().hide();
        Log.i("onCreate", "onCreate executed");
        submitBtn = findViewById(R.id.submitBtn);
        carImage1 = findViewById(R.id.carImg1);
        carImage2 = findViewById(R.id.carImg2);
        carImage3 = findViewById(R.id.carImg3);
        timerTextView = findViewById(R.id.timerTextView);
        carOneGuess = findViewById(R.id.car1NameEditText);
        carTwoGuess = findViewById(R.id.car2NameEditText);
        carThreeGuess = findViewById(R.id.car3NameEditText);
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
            int imgViewIndex1 = savedInstanceState.getInt("imageView_imgIndex1");
            Drawable  drawable  = carImages.get(imgViewIndex1);
            carImage1.setImageDrawable(drawable);
            int imgViewIndex2 = savedInstanceState.getInt("imageView_imgIndex2");
            drawable  = carImages.get(imgViewIndex2);
            carImage2.setImageDrawable(drawable);
            int imgViewIndex3 = savedInstanceState.getInt("imageView_imgIndex3");
            drawable  = carImages.get(imgViewIndex3);
            carImage3.setImageDrawable(drawable);
            carOneGuess.setText(savedInstanceState.getString("carOneGuessTextView"));
            carTwoGuess.setText(savedInstanceState.getString("carTwoGuessTextView"));
            carThreeGuess.setText(savedInstanceState.getString("carThreeGuessTextView"));
            score = savedInstanceState.getInt("score");
            tries = savedInstanceState.getInt("tries");
            correctCarNames=savedInstanceState.getString("correctCarNames");
            boolGuessOne=savedInstanceState.getBoolean("boolGuessOne");
            boolGuessTwo=savedInstanceState.getBoolean("boolGuessTwo");
            boolGuessThree=savedInstanceState.getBoolean("boolGuessThree");
            carOneGuess.setBackgroundColor(Color.parseColor("#C0C0C0"));
            carTwoGuess.setBackgroundColor(Color.parseColor("#C0C0C0"));
            carThreeGuess.setBackgroundColor(Color.parseColor("#C0C0C0"));
            if(timerString.equals("TimerOn")){
                try{
                    cDT.cancel();
                }catch (Exception e){
                    Log.e("TIMER", "Timer wasnt running so cant switch off");
                    e.printStackTrace();
                }
                if(timerString.equals("TimerOn")&&submitBtn.getText().toString().equals("Submit")){
                    gameTimer();
                    Log.d("TIMER", "Timer switched on");
                }

            }
        }else {
            Bundle bundle = getIntent().getExtras();
            //getting messages from the main activity(difficulty and timer)
            timerString = bundle.getString("timerState");
            difficulty = bundle.getString("difficultyState");
            totalRounds = Integer.parseInt(bundle.getString("roundState"));
            addImagesToList();
            roundOver = false;
            round = 0;
            correctAnswers = 0;
            newRound();
            Log.i("GAME_STARTED", "Game started");
        }
    }
    private void newRound(){
        boolGuessOne=false;
        boolGuessTwo=false;
        boolGuessThree=false;
        roundOver=false;
        tries=0;
        correctAnswers=0;
        round++;
        setImages();
        carOneGuess.setEnabled(true);
        carTwoGuess.setEnabled(true);
        carThreeGuess.setEnabled(true);
        carOneGuess.setText("");
        carTwoGuess.setText("");
        carThreeGuess.setText("");
        carOneGuess.setBackgroundColor(Color.parseColor("#C0C0C0"));
        carTwoGuess.setBackgroundColor(Color.parseColor("#C0C0C0"));
        carThreeGuess.setBackgroundColor(Color.parseColor("#C0C0C0"));
        if(timerString.equals("TimerOn")){
            gameTimer();
            Log.d("TIMER", "Timer switched on");
        }

    }
    //setting images to imageviews and name to textView, this is also the method that gets called when a new round is initiated
    private void setImages(){
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
                    Intent intent = new Intent(GameFourActivity.this,GameFourActivity.class);
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
            alertDialogBuilder.setMessage("Score: " + score + " out of "+(round*3)+". \n" +
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
                            Intent intent = new Intent(GameFourActivity.this,GameFourActivity.class);
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

    private void checkGuess(){
        if(timerString.equals("TimerOn")) {
            //stopping timer
            cDT.cancel();
            Log.d("TIMER", "Timer switched off");
        }
        tries++;
        //getting correct names by checking index of drawable and getting corresponding index frm name list
        int car1index = carImages.indexOf(carImage1.getDrawable());
        String car1Name = carNames.get(car1index);
        int car2index = carImages.indexOf(carImage2.getDrawable());
        String car2Name = carNames.get(car2index);
        int car3index = carImages.indexOf(carImage3.getDrawable());
        String car3Name = carNames.get(car3index);

        //getting guesses from edit text
        String car1GuessString=carOneGuess.getText().toString().toLowerCase();;
        String car2GuessString=carTwoGuess.getText().toString().toLowerCase();
        String car3GuessString=carThreeGuess.getText().toString().toLowerCase();

        if(difficulty.equals("Easy")){
            //split string and only get manufacturer name
            String[] splittedString = car1Name.split(" ");
            car1Name=splittedString[1];
            splittedString = car2Name.split(" ");
            car2Name=splittedString[1];
            splittedString = car3Name.split(" ");
            car3Name=splittedString[1];
        }else if(difficulty.equals("Hard")){
            //" "+ because when replacing "_" in  the method where images are added to list, it becomes " "
            // so as every image starts with "_" this is needed
            car1GuessString = " "+carOneGuess.getText().toString().toLowerCase();
            car2GuessString=" "+carTwoGuess.getText().toString().toLowerCase();
            car3GuessString=" "+carThreeGuess.getText().toString().toLowerCase();
        }

        //checking if guess was previously guessed or not
        if(!boolGuessOne){

            if (car1Name.toLowerCase().equals(car1GuessString)) {
                correctAnswers++;
                boolGuessOne = true;
                carOneGuess.setEnabled(false);
                carOneGuess.setBackgroundColor(Color.GREEN);
                score++;
            } else {
                carOneGuess.setBackgroundColor(Color.RED);
                carOneGuess.setText("");
                boolGuessOne = false;
            }
        }
        //checking if guess was previously guessed or not
        if(!boolGuessTwo) {
            if (car2Name.toLowerCase().equals(car2GuessString)) {
                correctAnswers++;
                boolGuessTwo = true;
                carTwoGuess.setEnabled(false);
                carTwoGuess.setBackgroundColor(Color.GREEN);
                score++;
            } else {
                carTwoGuess.setBackgroundColor(Color.RED);
                carTwoGuess.setText("");
                boolGuessTwo = false;
            }
        }
        //checking if guess was previously guessed or not
        if(!boolGuessThree) {
            if (car3Name.toLowerCase().equals(car3GuessString)) {
                correctAnswers++;
                boolGuessThree = true;
                carThreeGuess.setEnabled(false);
                carThreeGuess.setBackgroundColor(Color.GREEN);
                score++;
            } else {
                carThreeGuess.setBackgroundColor(Color.RED);
                carThreeGuess.setText("");
                boolGuessThree = false;
            }
        }

        //checking if images names have been guessed
        imageNamesGuessed();

        //checking if tries are over AFTER CHECKING IF IMAGE NAMES HAVE BEEN GUESSED
        triesOver(car1Name,car2Name,car3Name);
        TextView gameScore = findViewById(R.id.game4Score);
        gameScore.setText(""+score);
        //only if tries are not over yet run the timer again
        if(tries<3){
            if(timerString.equals("TimerOn")) {
                gameTimer();
                Log.d("TIMER", "Timer switched on");
            }
        }
    }
    //method used to check if all the chances are over
    public void triesOver(String car1Name,String car2Name,String car3Name){
        if(tries>=3){
            //setting correct answer in editText to wrong guesses
            correctCarNames="";
            if(!boolGuessOne){
                carOneGuess.setText(car1Name);
                carOneGuess.setBackgroundColor(Color.YELLOW);
                carOneGuess.setEnabled(false);
                correctCarNames=correctCarNames+"\n Image 1 : " +  car1Name;
            }
            if(!boolGuessTwo){
                carTwoGuess.setText(car2Name);
                carTwoGuess.setBackgroundColor(Color.YELLOW);
                carTwoGuess.setEnabled(false);
                correctCarNames=correctCarNames+"\n Image 2 : " +  car2Name;
            }
            if(!boolGuessThree){
                carThreeGuess.setText(car3Name);
                carThreeGuess.setBackgroundColor(Color.YELLOW);
                carThreeGuess.setEnabled(false);
                correctCarNames=correctCarNames+"\n Image 3 : " +  car3Name;
            }
            submitBtn.setText("Next");
            showWrongAlertDialog();
            roundOver=true;
        }else {
            //roundOver used for the timer, to check if round is over or user has clicked the submit button
            roundOver=false;
        }
    }
    //method used to check if all images have been guessed
    public void imageNamesGuessed(){
        if(boolGuessOne && boolGuessTwo && boolGuessThree){
            if(timerString.equals("TimerOn")){
                cDT.cancel();
            }
            tries=3;
            roundOver=true;
            submitBtn.setText("Next");
            showCorrectAlertDialog();
        }
    }

    public void submitBtn(View view) {
        if(submitBtn.getText().toString().equals("Next")){
            Log.d("NEXT", "Next button clicked");
            newRound();
            submitBtn.setText("Submit");
        }else if (submitBtn.getText().toString().equals("Submit")){
            Log.d("SUBMIT", "Submit button clicked");
            checkGuess();
        }
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

    public void showWrongAlertDialog(){
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
        alertDialogBuilder.setMessage(correctCarNames+
                "\nCorrect guesses: "+correctAnswers+" out of "+tries);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void showCorrectAlertDialog(){
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
        alertDialogBuilder.setMessage("All answers were correct!");
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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