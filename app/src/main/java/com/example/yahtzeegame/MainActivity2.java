package com.example.yahtzeegame;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import android.content.Context;
import android.media.MediaPlayer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity2 extends AppCompatActivity implements SensorEventListener {
    private boolean udahBelumSih;
    private ImageButton die1, die2, die3, die4, die5;
    private Button throwButton/*, cleanButton*/;
    private Button acesScoreButton, twosScoreButton, threesScoreButton, foursScoreButton, fivesScoreButton,
            sixesScoreButton, bonusButton;
    private TextView acesLabel, twosLabel, threesLabel, foursLabel, fivesLabel, sixesLabel, fullHouseLabel,
            threeOfAKindLabel, fourOfAKindLabel, smallStraightLabel, largeStraightLabel, yahtzeeLabel, chanceLabel,
            upperBonusScore;
    private TextView scoreLabel, upperTotalScore, lowerTotalScore;
    private int counter;
    private int totalScore;
    private int upperTotalScoreInt, lowerTotalScoreInt;
    private Button chanceButton, yahtzeeButton, largeStraightButton, smallStraightButton, fullHouseButton,
            fourOfAKindButton, threeOfAKindButton;
    private int[] valuesInEachDice = new int[5];
    private TextView counterText;

    private SensorManager sensorIt;
    private Sensor accelerateIt;
    private Vibrator vibeIt;
    private float shakingDifference = 8f;
    private float nowItsStill = 400f;
    private float previousX = 0, previousY = 0, previousZ = 0;
    private long recordedTimeBeforeShaken = 0;
    private boolean shaken = false;
    private MediaPlayer sound;
    private String username;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = getIntent().getStringExtra("username");
        uid = getIntent().getStringExtra("uid");
        udahBelumSih = true;
        initializeViews();
        setupClickListeners();

        TextView welcomingText = findViewById(R.id.titleText);
        welcomingText.setText("Hi, " + username + "!");
        vibeIt = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorIt = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerateIt = sensorIt.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorIt.registerListener((SensorEventListener) MainActivity2.this, accelerateIt,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @SuppressLint("DiscouragedApi")
    @Override
    public void onSensorChanged(SensorEvent e) {
        float x = e.values[0];
        long timeNow = System.currentTimeMillis();
        float changedX = Math.abs(x - previousX);
        // if the x coordinate is changing bigger than 8f, then it is shaken as long as
        // it is more than 8
        if (changedX > shakingDifference) {
            recordedTimeBeforeShaken = timeNow;
            if (!shaken) {
                shaken = true;

                vibeItt();
            }
        }
        // it stops when X coordinate lays below 2 in or more than 400milliseconds
        if (shaken && (timeNow - recordedTimeBeforeShaken >= nowItsStill) && changedX < 2f) { // the second if compares
                                                                                              // the time
            shaken = false;
            vibeItt();
            shakeIt();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void vibeItt() {
        if (vibeIt != null && vibeIt.hasVibrator()) {
            vibeIt.vibrate(50);
        }
    }

    private void initializeViews() {

        die1 = findViewById(R.id.dice1);
        die2 = findViewById(R.id.dice2);
        die3 = findViewById(R.id.dice3);
        die4 = findViewById(R.id.dice4);
        die5 = findViewById(R.id.dice5);
        throwButton = findViewById(R.id.shakeButton);
        //cleanButton = findViewById(R.id.cleanButton);
        acesScoreButton = findViewById(R.id.acesButton);
        twosScoreButton = findViewById(R.id.twosButton);
        threesScoreButton = findViewById(R.id.threesButton);
        foursScoreButton = findViewById(R.id.foursButton);
        fivesScoreButton = findViewById(R.id.fivesButton);
        sixesScoreButton = findViewById(R.id.sixesButton);
        bonusButton = findViewById(R.id.bonusButton);
        acesLabel = findViewById(R.id.acesScore);
        twosLabel = findViewById(R.id.twosScore);
        threesLabel = findViewById(R.id.threesScore);
        foursLabel = findViewById(R.id.foursScore);
        fivesLabel = findViewById(R.id.fivesScore);
        sixesLabel = findViewById(R.id.sixesScore);
        scoreLabel = findViewById(R.id.totalScore);
        threeOfAKindLabel = findViewById(R.id.threeOfAKindScore);
        fourOfAKindLabel = findViewById(R.id.fourOfAKindScore);
        fullHouseLabel = findViewById(R.id.fullHouseScore);
        smallStraightLabel = findViewById(R.id.smallStraightScore);
        largeStraightLabel = findViewById(R.id.largeStraightScore);
        yahtzeeLabel = findViewById(R.id.yahtzeeScore);
        chanceLabel = findViewById(R.id.chanceScore);
        upperTotalScore = findViewById(R.id.upperTotalScore);
        lowerTotalScore = findViewById(R.id.lowerTotalScore);
        upperBonusScore = findViewById(R.id.upperBonusScore);

        threeOfAKindButton = findViewById(R.id.threeOfAKindButton);
        fourOfAKindButton = findViewById(R.id.fourOfAKindButton);
        fullHouseButton = findViewById(R.id.fullHouseButton);
        smallStraightButton = findViewById(R.id.smallStraightButton);
        largeStraightButton = findViewById(R.id.largeStraightButton);
        yahtzeeButton = findViewById(R.id.yahtzeeButton);
        chanceButton = findViewById(R.id.chanceButton);

        counter = 0;
        totalScore = 0;
        upperTotalScoreInt = 0;
        lowerTotalScoreInt = 0;
        counterText = findViewById(R.id.counterText);
    }

    private void showPics(ImageButton whichDice, int randomNumber) {
        int dice;
        switch (randomNumber) {
            case 1:
                dice = R.drawable.dice1;
                break;
            case 2:
                dice = R.drawable.dice2;
                break;
            case 3:
                dice = R.drawable.dice3;
                break;
            case 4:
                dice = R.drawable.dice4;
                break;
            case 5:
                dice = R.drawable.dice5;
                break;
            case 6:
                dice = R.drawable.dice6;
                break;
            default:
                dice = R.drawable.dice1;
                break;
        }

        whichDice.setImageResource(dice);

        // put in the number so that the game can track
        // it was Text with button but since its imageButton then its tag
        // and tag isnt immidiately attached to the button like text does
        // thats why we're putting the number text to the valuesInEachDice
        ImageButton[] buttons = { die1, die2, die3, die4, die5 };
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == whichDice) {
                valuesInEachDice[i] = randomNumber;
                break;
            }
        }
    }

    private void setupClickListeners() {
        die1.setOnClickListener(v -> {
            if (die1.isEnabled()) {
                die1.setEnabled(false);
                die1.setAlpha(0.5f);
            }
        });
        die2.setOnClickListener(v -> {
            if (die2.isEnabled()) {
                die2.setEnabled(false);
                die2.setAlpha(0.5f);
            }
        });
        die3.setOnClickListener(v -> {
            if (die3.isEnabled()) {
                die3.setEnabled(false);
                die3.setAlpha(0.5f);
            }
        });
        die4.setOnClickListener(v -> {
            if (die4.isEnabled()) {
                die4.setEnabled(false);
                die4.setAlpha(0.5f);
            }
        });
        die5.setOnClickListener(v -> {
            if (die5.isEnabled()) {
                die5.setEnabled(false);
                die5.setAlpha(0.5f);
            }
        });

        throwButton.setOnClickListener(v -> {
            shakeIt();
        });

        /*cleanButton.setOnClickListener(v -> {
            if(udahBelumSih == false){
                Toast.makeText(this, "You have to score first before rolling the dice", Toast.LENGTH_LONG).show();
            } else {
                checkTheClean();
            }
        });*/

        acesScoreButton.setOnClickListener(v -> {
            udahBelumSih = true;

            int counter1 = 0;
            ImageButton[] buttons = { die1, die2, die3, die4, die5 };
            for (int i = 0; i < buttons.length; i++) {
                if (valuesInEachDice[i] == 1) {
                    counter1 = counter1 + 1;
                }
            }

            upperScoreScoring(counter1, acesLabel, acesScoreButton);
            checkTheClean();

        });

        twosScoreButton.setOnClickListener(v -> {
            udahBelumSih = true;

            int counter2 = 0;
            for (int i = 0; i < 5; i++) {
                if (valuesInEachDice[i] == 2) {
                    counter2 = counter2 + 2;
                }
            }

            upperScoreScoring(counter2, twosLabel, twosScoreButton);
            checkTheClean();
        });

        threesScoreButton.setOnClickListener(v -> {
            udahBelumSih = true;
            int counter3 = 0;
            for (int i = 0; i < 5; i++) {
                if (valuesInEachDice[i] == 3) {
                    counter3 = counter3 + 3;
                }
            }

            upperScoreScoring(counter3, threesLabel, threesScoreButton);
            checkTheClean();
        });

        foursScoreButton.setOnClickListener(v -> {
            udahBelumSih = true;
            int counter4 = 0;
            for (int i = 0; i < 5; i++) {
                if (valuesInEachDice[i] == 4) {
                    counter4 = counter4 + 4;
                }
            }
            upperScoreScoring(counter4, foursLabel, foursScoreButton);
            checkTheClean();
        });

        fivesScoreButton.setOnClickListener(v -> {
            udahBelumSih = true;
            int counter5 = 0;
            for (int i = 0; i < 5; i++) {
                if (valuesInEachDice[i] == 5) {
                    counter5 = counter5 + 5;
                }
            }

            upperScoreScoring(counter5, fivesLabel, fivesScoreButton);
            checkTheClean();
        });

        sixesScoreButton.setOnClickListener(v -> {
            udahBelumSih = true;
            int counter6 = 0;
            for (int i = 0; i < 5; i++) {
                if (valuesInEachDice[i] == 6) {
                    counter6 = counter6 + 6;
                }
            }

            upperScoreScoring(counter6, sixesLabel, sixesScoreButton);
            checkTheClean();
        });

        bonusButton.setOnClickListener((v -> {
            udahBelumSih = true;
            if (upperTotalScoreInt >= 63) {
                giveEmBonus();
            }
            checkTheClean();
        }));

        threeOfAKindButton.setOnClickListener(v -> {
            udahBelumSih = true;
            int[] array = arrangeTheDice();
            if (array == null) {
                return;
            }
            int sumItUp = array[0] + array[1] + array[2] + array[3] + array[4];
            boolean isIt = false;
            for (int j = 0; j < 3; j++) {
                if (array[j] == array[j + 1] && array[j] == array[j + 2]) {
                    isIt = true;
                    break;
                }
            }

            either0orResult(sumItUp, isIt, threeOfAKindLabel, threeOfAKindButton);
            checkTheClean();
        });

        fourOfAKindButton.setOnClickListener(v -> {
            udahBelumSih = true;
            int[] array = arrangeTheDice();
            if (array == null) {
                return;
            }
            int sumItUp = array[0] + array[1] + array[2] + array[3] + array[4];
            boolean isIt = false;
            for (int j = 0; j < 2; j++) {
                if (array[j] == array[j + 1] && array[j] == array[j + 2] && array[j] == array[j + 3]) {
                    isIt = true;
                    break;
                }
            }

            either0orResult(sumItUp, isIt, fourOfAKindLabel,fourOfAKindButton);
            checkTheClean();
        });

        fullHouseButton.setOnClickListener(v -> {
            udahBelumSih = true;
            int[] array = arrangeTheDice();
            if (array == null) {
                return;
            }
            boolean isIt = (array[0] == array[1] && array[1] == array[2] && array[3] == array[4] && array[2] != array[3]) ||
                    (array[0] == array[1] && array[2] == array[3] && array[3] == array[4] && array[1] != array[2]);

            either0orResult(25, isIt, fullHouseLabel, fullHouseButton);
            checkTheClean();
        });

        smallStraightButton.setOnClickListener(v -> {
            udahBelumSih = true;
            int[] array = arrangeTheDice();
            if (array == null) {
                return;
            }
            
            boolean isIt = false;

            if ((array[0] == 1 && array[1] == 2 && array[2] == 3 && array[3] == 4) ||
                (array[0] == 2 && array[1] == 3 && array[2] == 4 && array[3] == 5) ||
                (array[1] == 2 && array[2] == 3 && array[3] == 4 && array[4] == 5) ||
                (array[1] == 3 && array[2] == 4 && array[3] == 5 && array[4] == 6)) {
                isIt = true;
            }

            either0orResult(30, isIt, smallStraightLabel, smallStraightButton);
            checkTheClean();
        });

        largeStraightButton.setOnClickListener(v -> {
            udahBelumSih = true;
            int[] array = arrangeTheDice();
            if (array == null) {
                return;
            }
            boolean isIt = (array[0] == 1 && array[1] == 2 && array[2] == 3 && array[3] == 4 && array[4] == 5) ||
                    (array[0] == 2 && array[1] == 3 && array[2] == 4 && array[3] == 5 && array[4] == 6);

            either0orResult(40, isIt, largeStraightLabel,largeStraightButton);
            checkTheClean();
        });

        yahtzeeButton.setOnClickListener(v -> {
            udahBelumSih = true;
            int[] array = arrangeTheDice();
            if (array == null) {
                return;
            }
            boolean isIt = array[0] == array[4];


            either0orResult(50, isIt, yahtzeeLabel,  yahtzeeButton);
            checkTheClean();
        });

        chanceButton.setOnClickListener(v -> {
            udahBelumSih = true;
            for (int i = 0; i < 5; i++) {
                if (valuesInEachDice[i] == 0) {
                    Toast.makeText(this, "Roll the dice first!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += valuesInEachDice[i];
            }

            either0orResult(sum, true, chanceLabel, chanceButton);
            checkTheClean();
        });
    }

    private void upperScoreScoring(int counter, TextView label1, Button button1){
        label1.setText("" + counter);
        button1.setEnabled(false);
        upperTotalScoreInt = upperTotalScoreInt + counter;
        upperTotalScore.setText("" + upperTotalScoreInt);
        totalScore = totalScore + counter;
        scoreLabel.setText("Total Score: " + totalScore);
    }
    private void either0orResult(int result, boolean isIt, TextView label1, Button button1){
        int toGive;
        if(isIt == true){
            toGive = result;
        } else {
            toGive = 0;
        }
        label1.setText("" + toGive);
        lowerTotalScoreInt = lowerTotalScoreInt + toGive;
        lowerTotalScore.setText("" + lowerTotalScoreInt);
        totalScore = totalScore + toGive;
        scoreLabel.setText("Total Score: " + totalScore);
        button1.setEnabled(false);
    }

    private int[] arrangeTheDice() {
        int[] array = new int[5];
        for (int i = 0; i < 5; i++) {
            if (valuesInEachDice[i] == 0) {
                Toast.makeText(this, "Roll the dice first!", Toast.LENGTH_SHORT).show();
                return null;
            }
            array[i] = valuesInEachDice[i];
        }
        Arrays.sort(array);
        return array;
    }

    private void giveEmBonus() {

        upperBonusScore.setText("35");
        upperTotalScoreInt = upperTotalScoreInt + 35;
        upperTotalScore.setText("" + upperTotalScoreInt);
        totalScore = totalScore + 35;
        scoreLabel.setText("Total Score: " + totalScore);
        bonusButton.setEnabled(false);
    }

    public void shakeIt() {
        if(udahBelumSih == false){
            Toast.makeText(this, "You have to score first before rolling the dice", Toast.LENGTH_LONG).show();
            return;
        }

        counter = counter + 1;
        counterText.setText("Shakes: " + counter +"/3");

        Random random = new Random();
        ImageButton[] buttons = { die1, die2, die3, die4, die5 };
        int[] dieNumber = new int[5];

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].isEnabled()) {
                dieNumber[i] = random.nextInt(6) + 1;
                showPics(buttons[i], dieNumber[i]);
            }
        }

        if (counter == 3) {
            counter = 0;
            throwButton.setEnabled(false);
        }
        /*cleanButton.setEnabled(true);*/

    }

    private void checkTheClean() {
        ImageButton[] buttons = { die1, die2, die3, die4, die5 };
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setImageResource(0);
            buttons[i].setEnabled(true);
            buttons[i].setAlpha(1.0f);
            valuesInEachDice[i] = 0;
        }
        throwButton.setEnabled(true);

        Button[] scoreButtons = { acesScoreButton, twosScoreButton, threesScoreButton, foursScoreButton,
                fivesScoreButton, sixesScoreButton, threeOfAKindButton, fourOfAKindButton, fullHouseButton,
                smallStraightButton, largeStraightButton, yahtzeeButton, chanceButton, bonusButton };
        TextView[] scoreLabels = { acesLabel, twosLabel, threesLabel, foursLabel, fivesLabel, sixesLabel,
                threeOfAKindLabel, fourOfAKindLabel, fullHouseLabel, smallStraightLabel, largeStraightLabel,
                yahtzeeLabel, chanceLabel, upperBonusScore };

        if (!acesScoreButton.isEnabled() && !twosScoreButton.isEnabled() && !threesScoreButton.isEnabled()
                && !foursScoreButton.isEnabled() && !fivesScoreButton.isEnabled()
                && !sixesScoreButton.isEnabled()) {
            if (upperTotalScoreInt >= 63) {
                giveEmBonus();
            } else {
                bonusButton.setEnabled(false);
            }
        }

        if (!acesScoreButton.isEnabled() && !twosScoreButton.isEnabled() && !threesScoreButton.isEnabled() && !foursScoreButton.isEnabled() &&
        !fivesScoreButton.isEnabled() && !sixesScoreButton.isEnabled() && !threeOfAKindButton.isEnabled() && !fourOfAKindButton.isEnabled() && !fullHouseButton.isEnabled() &&
        !smallStraightButton.isEnabled() && !largeStraightButton.isEnabled() && !yahtzeeButton.isEnabled() && !chanceButton.isEnabled() && !bonusButton.isEnabled()){
            storeTheScore();
            Toast.makeText(this, "Your final score is " + totalScore, Toast.LENGTH_LONG).show();
            for (int j = 0; j < scoreButtons.length; j++) {
                scoreButtons[j].setEnabled(true);
                scoreLabels[j].setText("");
            }
            upperTotalScore.setText("");
            lowerTotalScore.setText("");
            lowerTotalScoreInt=0;
            upperTotalScoreInt=0;
            totalScore = 0;
            scoreLabel.setText("Total Score: " + totalScore);
            udahBelumSih = true;
        }

        counter = 0;
        counterText.setText("Shakes:" + counter + "/3");


    }

    private void storeTheScore() {

        /*FirebaseUser currentPlayerId = FirebaseAuth.getInstance().getCurrentUser();
        String checkThePlayer = currentPlayerId.getUid();
        if (checkThePlayer.equals(uid)) {
            DatabaseReference scoreRef = FirebaseDatabase
                    .getInstance("https://yahtzeegame-2b1d7-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("scores");
            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("userId", uid);
            scoreData.put("username", username);
            scoreData.put("scored", totalScore);
            scoreRef.push().setValue(scoreData);
        } else {
            Toast.makeText(this, "You are not logged in, this score could not be saved", Toast.LENGTH_SHORT).show();
        }*/
        try {
            FirebaseUser currentPlayerId = FirebaseAuth.getInstance().getCurrentUser();
            if (currentPlayerId == null) {
                Toast.makeText(this, "You are not logged in, this score could not be saved", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String checkThePlayer = currentPlayerId.getUid();
            if (checkThePlayer.equals(uid)) {
                DatabaseReference scoreRef = FirebaseDatabase
                        .getInstance("https://yahtzeegame-2b1d7-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference("scores");
                Map<String, Object> scoreData = new HashMap<>();
                scoreData.put("userId", uid);
                scoreData.put("username", username);
                scoreData.put("scored", totalScore);
                scoreRef.push().setValue(scoreData)
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save score: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            } else {
                Toast.makeText(this, "You are not logged in, this score could not be saved", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error saving score", Toast.LENGTH_SHORT).show();
        }
    }

}