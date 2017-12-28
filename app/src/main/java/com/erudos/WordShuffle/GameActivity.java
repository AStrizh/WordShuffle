package com.erudos.WordShuffle;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;

import static com.erudos.WordShuffle.Calculations.*;

public class GameActivity extends AppCompatActivity {

    private String[] words;
    private Context context;
    private ViewGroup contentView;
    private InterstitialAd interstitialAd;

    //private TextView score;
    private TextView stats;
    private WordShuffle ws;
    private TextView hint;
    private TextView message;
    private Button resetBtn;

    //private int points;
    //private int total;

    private PointF[] holeCenters;
    private PointF[] viewStartPositions;
    private ImageView[] myImageViews;
    private TextView[] myTextViews;
    private String[] letterCollection;
    private String wordWorking;

    private int widthScreen;
    private int indentElement;
    private int viewSideLength;
    private boolean firstLoad;
    private int letterCount;
    private int wordLength;

    private static final int ANIMATION_DURATION = 300;

    private int skipCount;
    private int correctCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        int fileResource = Integer.parseInt(intent.getStringExtra(MainActivity.DIFFICULTY));
        contentView = findViewById(android.R.id.content);
        context = getApplicationContext();


        //Gets the screen width so views could be built proportionally
        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthScreen = metrics.widthPixels;

        try {

            ws = new WordShuffle(fileResource, context);

        } catch (IOException e) {
            e.printStackTrace();
        }


        resetBtn= findViewById(R.id.reset);


        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId( getString(R.string.interstitial_ad_unit_id));

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                skip();
            }
        });

        requestNewInterstitial();

        AdView bannerAd = findViewById(R.id.bannerAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(adRequest);

        initializer();
    }

    //Set the parameters for the first game
    private void initializer(){

        Button skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    skip();
                }

            }
        });

        //score = findViewById(R.id.total);
        stats = findViewById(R.id.stats);
        stats.setText(String.format( getString(R.string.stats), correctCount, skipCount ));
        message = findViewById(R.id.message);
        hint = findViewById(R.id.hint);
        taskBuilder();

    }

    //Calls WordShuffle method to select a new word
    private void taskBuilder(){

        words = ws.taskCreator();

        //Resets the screen with a new word challenge
        firstLoad = true;
        wordBuilder(words[0]);
        targetBuilder();

        hint.setText(words[2]);


        resetBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                RotateAnimation rotateAnimation = new RotateAnimation(
                        0,360, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

                rotateAnimation.setDuration(500);
                resetBtn.startAnimation(rotateAnimation);

                if(!firstLoad){
                    reset();
                }

            }

        });
    }
    //TODO: Create Custom view classes rather than defining the view here
    //TODO: Make sure the view Override CustomOutline for shadows
    //Creates movable textViews based on the provided word
    private void wordBuilder(String word) {

        wordWorking = word;
        Drawable box = ContextCompat.getDrawable(context, R.drawable.box);

        //String[] letters = shuffle(wordWorking);
        String[] letters = breakString(word);
        shuffle(letters);

        wordLength = letters.length;
        int leftMarginView;


        myTextViews = new TextView[wordLength];
        viewStartPositions = new PointF[wordLength];
        letterCollection = new String[wordLength];

        indentElement = widthScreen/wordLength;
        viewSideLength = (widthScreen - 2*indentElement) / wordLength;

        RelativeLayout.LayoutParams viewParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        viewParam.height = viewSideLength;
        viewParam.width = viewSideLength;

        for (int i = 0; i < wordLength; i++) {
            letterCollection[i] = " ";
        }

        for (int i = 0; i < wordLength; i++) {
            final TextView rowTextView = new TextView(context);

            leftMarginView =  indentElement + (viewSideLength * i);

            rowTextView.setBackground(box);
            rowTextView.setText(letters[i]);
            rowTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            rowTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.letter_size));

            rowTextView.setGravity(Gravity.CENTER);

            rowTextView.setLayoutParams(viewParam);
            rowTextView.setX(leftMarginView); rowTextView.setY(100);
            contentView.addView(rowTextView);
            rowTextView.setOnTouchListener(onTouchListener());

            myTextViews[i] = rowTextView;

        }
    }
    //TODO: Create Custom view classes rather than defining the view here
    //Creates static target views  based on the length of the provided word
    private void targetBuilder() {

        Drawable hole = ContextCompat.getDrawable(context, R.drawable.hole);
        myImageViews = new ImageView[wordLength];
        holeCenters = new PointF[wordLength];
        int leftMarginImage;
        int topOffset;


        RelativeLayout.LayoutParams imageParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        int paddingElement = indentElement/(wordLength + 1);
        viewSideLength = (widthScreen - indentElement) / wordLength;
        imageParam.height = viewSideLength;
        imageParam.width = viewSideLength;
        topOffset = 100 + viewSideLength + 10;


        for (int i = 0; i < wordLength; i++) {

            final ImageView holeView = new ImageView(context);
            holeView.setImageDrawable(hole);

            leftMarginImage = viewSideLength*i + paddingElement*(i+1);

            holeView.setLayoutParams(imageParam);
            holeView.setX(leftMarginImage); holeView.setY(topOffset);
            contentView.addView(holeView);
            myImageViews[i] = holeView;

        }
    }

    //Controls all the motion for the views
    private View.OnTouchListener onTouchListener() {

        return new View.OnTouchListener() {

            //Points used to track view position during movement
            PointF DownPT = new PointF();
            PointF StartPT = new PointF();

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                /*
                Gets reference positions when the Views first become visible to the user
                These are used to create the snapping effect when a letter is put on target
                Also used to send a letter back to its start position when overlap occurs.
                 */
                if(firstLoad){

                    viewStartPositions = generateViewPositions( myTextViews );
                    holeCenters = generateTargetCenters( myImageViews );
                    firstLoad = false;
                }

                view.bringToFront();
                int eid = event.getAction();

                switch (eid) {

                    case MotionEvent.ACTION_MOVE:
                        PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                        view.setX((int) (StartPT.x + mv.x));
                        view.setY((int) (StartPT.y + mv.y));
                        StartPT = new PointF(view.getX(), view.getY());
                        break;

                    case MotionEvent.ACTION_DOWN:
                        DownPT.x = event.getX();
                        DownPT.y = event.getY();
                        StartPT = new PointF(view.getX(), view.getY());

                        //Checks if a view is being moved out of a target; clears that letter
                        PointF thisCenter = getCenter(view);

                        int k=0;
                        for (PointF targetCenter : holeCenters) {
                            if( distanceClose(thisCenter, targetCenter)) {
                                letterCollection[k] = " ";
                                letterCount--;
                            }
                            k++;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        //Checks if center of view is close to any targets. If true snaps to target
                        PointF currentCenter = getCenter(view);

                        int i=0;
                        for (PointF targetCenter : holeCenters) {
                            if ( distanceClose(currentCenter, targetCenter) ) {
                                view.setX((int)( targetCenter.x - (view.getWidth()/2) ) );
                                view.setY((int)( targetCenter.y - (view.getHeight()/2)) );

                                view.playSoundEffect(android.view.SoundEffectConstants.CLICK);

                                //Puts the letter from the view into an array for checking later
                                letterCollection[i] = ( (TextView) view).getText().toString();
                                letterCount++;

                                //Checks if the view now overlaps another
                                overlap(view);

                                break;
                            }
                            i++;
                        }
                        if (letterCount == wordLength)
                            checkText();
                        break;

                    default:
                        break;
                }
                return true;
            }
        };
    }

    //Checks if  user got the word right
    private void checkText() {

        if ( correctAnswer(letterCollection, wordWorking) ){

            for (int j = 0; j < wordLength; j++) {
                letterCollection[j] = " ";
            }
            letterCount = 0;

            //points = ( Integer.parseInt( words[1] ) );
            //total += points;
            //score.setText("Total: " + total);

            message.setText(getString(R.string.correct));
            stats.setText(String.format( getString(R.string.stats), ++correctCount, skipCount ));


            //This pair removes the all word blocks
            for(View v : myTextViews)
                contentView.removeView(v);
            for(View v : myImageViews)
                contentView.removeView(v);

            taskBuilder();

        } else{
            wrongShake();
            message.setText(getString(R.string.incorrect));
        }

    }


    //Checks overlap between tiles. If true moves background tile to start position
    private void overlap(View view){

        for(int j = 0; j<wordLength;j++) {

            if( getCenter(myTextViews[j]).equals( getCenter(view)) &&  myTextViews[j] != view){

                letterCount--;
                TranslateAnimation animation = new TranslateAnimation(
                        (int)myTextViews[j].getX()-(int)viewStartPositions[j].x, 0,
                        (int)myTextViews[j].getY()-(int)viewStartPositions[j].y, 0);

                animation.setDuration(ANIMATION_DURATION);
                myTextViews[j].startAnimation(animation);

                myTextViews[j].setX((int)( viewStartPositions[j].x ) );
                myTextViews[j].setY((int)( viewStartPositions[j].y ) );
            }
        }
    }

    //Method to create interstitial ads
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
    }

    //Animates the movement of tiles to their start position
    private void reset(){

        TranslateAnimation animation;

        for(int j = 0; j<wordLength;j++) {


            animation = new TranslateAnimation(
                    (int)myTextViews[j].getX()-(int)viewStartPositions[j].x, 0,
                    (int)myTextViews[j].getY()-(int)viewStartPositions[j].y, 0);

            animation.setDuration(ANIMATION_DURATION);
            myTextViews[j].startAnimation(animation);

            myTextViews[j].setX((int)( viewStartPositions[j].x ) );
            myTextViews[j].setY((int)( viewStartPositions[j].y ) );
        }
        letterCount = 0;
    }

    //Button to allow user to skip word. Shows an interstitial ad.
    private void skip(){

        //stats.setText("Total: " + total);

        stats.setText(String.format( getString(R.string.stats), correctCount, ++skipCount ));

        message.setText(getString(R.string.skipped) + " " + words[0],
                TextView.BufferType.SPANNABLE);

        //Defines Text style for Skipped to be Bold Red
        Spannable s = (Spannable)message.getText();
        int start = getString(R.string.skipped).length();
        int end = start + 1 + words[0].length();

        s.setSpan(new StyleSpan(Typeface.BOLD),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Red)),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        letterCount = 0;

        //This pair removes the all word blocks
        for(View v : myTextViews)
            contentView.removeView(v);
        for(View v : myImageViews)
            contentView.removeView(v);

        taskBuilder();
    }

    private void wrongShake() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE))
                    .vibrate(VibrationEffect.createOneShot(
                    150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }
}
