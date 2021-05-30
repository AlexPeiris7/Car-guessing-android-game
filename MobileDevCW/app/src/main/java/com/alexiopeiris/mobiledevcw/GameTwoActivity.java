  package com.alexiopeiris.mobiledevcw;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

  public class GameTwoActivity extends AppCompatActivity {

      private ArrayList<Drawable> carImages = new ArrayList<>();
      private ArrayList<String> carNames = new ArrayList<>();
      private ImageView imageView;
      private TextView dashedCarName;
      private int round;
      private int totalRounds;
      private int correctAnswers;
      private String difficulty;
      private String timerString;
      private TextView timerTextView;
      private CountDownTimer cDT;
      private String dashes;
      private int wrongGuesses;
      private Button submitBtn;
      private EditText userGuess;

      @Override
      public void onSaveInstanceState(Bundle outState) {
          outState.putString("difficulty", difficulty);
          outState.putString("timerString", timerString);
          outState.putString("submitBtnState", submitBtn.getText().toString());
          outState.putString("dashes", dashedCarName.getText().toString());
          outState.putString("editTextUserGuess", userGuess.getText().toString());
          outState.putInt("round",round);
          outState.putInt("totalRounds",totalRounds);
          outState.putInt("correctAnswers",correctAnswers);
          outState.putInt("wrongGuesses",wrongGuesses);
          outState.putInt("imageView_imgIndex",carImages.indexOf(imageView.getDrawable()));
          super.onSaveInstanceState(outState);
      }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_game_two);
            //hiding the action bar
            getSupportActionBar().hide();
            Log.i("onCreate", "onCreate executed");
            submitBtn = findViewById(R.id.submitBtn);
            userGuess = findViewById(R.id.userGuess);
            timerTextView = findViewById(R.id.timerTextView);
            dashedCarName = findViewById(R.id.dashedLines);
            imageView = findViewById(R.id.imageView);
            if (savedInstanceState != null) {
                difficulty = savedInstanceState.getString("difficulty");
                timerString = savedInstanceState.getString("timerString");
                String submitBtnState = savedInstanceState.getString("submitBtnState");
                submitBtn.setText(submitBtnState);
                round = savedInstanceState.getInt("round");
                totalRounds = savedInstanceState.getInt("totalRounds");
                correctAnswers = savedInstanceState.getInt("correctAnswers");
                wrongGuesses = savedInstanceState.getInt("wrongGuesses");
                int imgViewIndex = savedInstanceState.getInt("imageView_imgIndex");
                String editTextUserGuess = savedInstanceState.getString("editTextUserGuess");
                userGuess.setText(editTextUserGuess);
                addImagesToList();
                Drawable  drawable  = carImages.get(imgViewIndex);
                imageView.setImageDrawable(drawable);
                dashes = savedInstanceState.getString("dashes");
                dashedCarName.setText(dashes);
                // if timerOn start timer
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
      //setting image to imageview, this is also the method that gets called when a new round is initiated
      private void setImg(){
          Random rand = new Random();
          int randomNum = rand.nextInt(carImages.size()-1);
          Drawable  drawable  = carImages.get(randomNum);
          imageView.setImageDrawable(drawable);
      }
      private void newRound(){
            wrongGuesses=0;
            round++;
            setImg();
            setDashes();
            if(timerString.equals("TimerOn")){
                gameTimer();
                Log.d("TIMER", "Timer switched on");
            }
      }

      private void setDashes(){
          int imgIndex = carImages.indexOf(imageView.getDrawable());
          String carName = carNames.get(imgIndex);
          dashes ="";
          if(difficulty.equals("Easy")){
              //setting only manufacturer name as carName
              String[] splittedString = carName.split(" ");
              carName=splittedString[1];
          }else if(difficulty.equals("Hard")){
              //setting manufacturer name and type and car name;
              carName = carNames.get(imgIndex);
          }
          String[] splittedString = carName.split("");
          for(String s :splittedString){
              if(splittedString[0].equals(s)){
                  continue;
              }
              if(s.equals(" ")){
                  dashes=dashes+" ";
              }else if(s.matches("[a-zA-Z0-9]*")){
                  dashes=dashes+"_";
              }
          }
          dashes.toUpperCase();
          dashedCarName.setText(dashes);
      }

      private void checkGuess(){
          if(timerString.equals("TimerOn")) {
              //stopping timer
              cDT.cancel();
              Log.d("TIMER", "Timer switched off");
          }
            if(!userInputValidation(userGuess)){
                return;
            }
            int imgIndex = carImages.indexOf(imageView.getDrawable());
          String carName = carNames.get(imgIndex);
            if(difficulty.equals("Easy")){
                //setting only manufacturer name as carName
                String[] splittedString = carName.split(" ");
                carName=splittedString[1];
            }else if(difficulty.equals("Hard")){
                //setting manufacturer name and type and car name;
                carName = carNames.get(imgIndex);
            }
            if(carName.toLowerCase().contains(userGuess.getText().toString())){
                String[] splittedName = carName.split("");
                String[] splittedDashes = dashes.split("");
                String temp="";
                int count=0;
                for(String s : splittedName){
                    if(splittedName[0].equals(s)){
                        continue;
                    }
                    if(s.toLowerCase().equals(userGuess.getText().toString())){
                        temp=temp+s;
                    }else{
                        //count + 1 because first empty char in splittedDashes is already omitted(in String dashes)
                        //whereas its still there in the splittedName array
                        temp=temp+splittedDashes[count+1];
                    }
                    count++;
                }
                System.out.println(temp);
                dashes=temp;
                dashes.toUpperCase();
                dashedCarName.setText(dashes);
            }else{
                wrongGuesses++;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Number of wrong attempts left: "+(3-wrongGuesses),
                        Toast.LENGTH_LONG);
                toast.show();
                if(wrongGuesses==3){
                    showWrongAlertDialog();
                    //carNames.remove(imgIndex);
                    //carImages.remove(imgIndex);
                    submitBtn.setText("Next");
                    userGuess.setEnabled(false);
                }
            }
            userGuess.setText("");
          if(nameGuessed()){
              //carNames.remove(imgIndex);
              //carImages.remove(imgIndex);
              submitBtn.setText("Next");
              userGuess.setEnabled(false);
          }
      }

      private boolean nameGuessed(){
          if(!dashes.contains("_")){
              correctAnswers++;
              showCorrectAlertDialog();
              return true;
          }
          return false;
      }

      private boolean userInputValidation(EditText userGuess){
          if(userGuess.getText().length()>1||userGuess.getText().length()<1){
              //using the custom made alertDialog situated in the styles xml(resource dir)
              AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
              alertDialogBuilder.setTitle("Wrong input!");
              if(userGuess.getText().length()>1){
                  alertDialogBuilder.setMessage("You can only enter one letter/number");
              }
              else if(userGuess.getText().length()<1){
                  alertDialogBuilder.setMessage("You have to enter one letter/number");
              }
              alertDialogBuilder.setPositiveButton(
                      "OK",
                      new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int id) {
                              dialog.dismiss();
                          }
                      });
              AlertDialog alertDialog = alertDialogBuilder.create();
              alertDialog.show();
              return false;
          }else {
              return true;
          }
      }

      //dinamically adding images from drawables to arraylist
      @SuppressLint("UseCompatLoadingForDrawables")
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
            if(submitBtn.getText().equals("Submit")){
                Log.d("SUBMIT", "Submit button clicked");
                checkGuess();
                if(timerString.equals("TimerOn")&&submitBtn.getText().toString().equals("Submit")) {
                    gameTimer();
                    Log.d("TIMER", "Timer switched on");
                }
            }else if(submitBtn.getText().equals("Next")){
                Log.d("NEXT", "Next button clicked");
                if(timerString.equals("TimerOn")) {
                    cDT.cancel();
                    Log.d("TIMER", "Timer switched off");
                }
                newRound();
                submitBtn.setText("Submit");
                userGuess.setEnabled(true);
            }
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
                      Intent intent = new Intent(GameTwoActivity.this,GameTwoActivity.class);
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
      private void backToMain(){
          Intent intent = new Intent(this, MainActivity.class);
          startActivity(intent);
      }
      private boolean gameOver(){
          if(round==totalRounds){
              Log.i("GAME_OVER", "Game ended");
              return true;
          }else{
              return false;
          }
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
                              Intent intent = new Intent(GameTwoActivity.this,GameTwoActivity.class);
                              intent.putExtra("timerState", timerString);
                              intent.putExtra("difficultyState", difficulty);
                              intent.putExtra("roundState", String.valueOf(totalRounds));
                              startActivity(intent);
                          }
                      });
              AlertDialog alertDialog = alertDialogBuilder.create();
              alertDialog.show();
              submitBtn.setEnabled(false);
              userGuess.setEnabled(false);
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
          int imgIndex = carImages.indexOf(imageView.getDrawable());
          alertDialogBuilder.setMessage("Correct answer: "+carNames.get(imgIndex));
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
                          System.out.println(round);
                          System.out.println(correctAnswers);
                          gameOverPopUp();
                          dialog.dismiss();
                      }
                  });
          alertDialogBuilder.setTitle("WRONG!");
          int imgIndex = carImages.indexOf(imageView.getDrawable());
          alertDialogBuilder.setMessage("Correct answer: "+carNames.get(imgIndex));
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