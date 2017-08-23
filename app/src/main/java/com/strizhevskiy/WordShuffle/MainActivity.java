package com.strizhevskiy.WordShuffle;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.strizhevskiy.WordShuffle.Calculations.*;

//TODO: Make sure the animated sequence word placement adjusts to any screen type
//TODO: When starting game, call new activity instead of creating fragment

public class MainActivity extends AppCompatActivity {

    private static int dictType;
    private Context context;

    TextView[] myTextViews;
    PointF[] viewStartPositions;
    int wordLength;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        ViewGroup viewGroup = findViewById(android.R.id.content);

        DisplayMetrics metrics;
        int widthScreen;
        int indentElement;
        int viewSideLength;
        int leftMarginView;


        Drawable box = ContextCompat.getDrawable(this, R.drawable.box);
        Drawable hole = ContextCompat.getDrawable(this, R.drawable.hole);

        metrics = new DisplayMetrics();

        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthScreen = metrics.widthPixels;


        String wordWorking = "WORDSHUFFLE";
        wordLength = wordWorking.length();

        String[] letters = breakString(wordWorking);

        myTextViews = new TextView[wordLength];
        viewStartPositions = new PointF[wordLength];

        int fromTop1 = 400;
        int fromTopCenter = 600;
        int fromTop2 = 800;
        float count = 1.4f;

        RelativeLayout.LayoutParams viewParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);


        indentElement = widthScreen/7;
        viewSideLength = (widthScreen - indentElement) / 7;
        viewParam.height = viewSideLength;
        viewParam.width = viewSideLength;

        ImageView boxView = new ImageView(this);
        int center = (int)(indentElement + (viewSideLength * 2.5f));
        viewParam.setMargins(center, fromTopCenter, 0, 0);
        boxView.setLayoutParams(viewParam);
        boxView.setImageDrawable(hole);
        viewGroup.addView(boxView);


        for (int i = 0; i < wordLength; i++) {
            final TextView rowTextView = new TextView(this);

            if ( i == 4)
                count = 0;

            leftMarginView =  (int)(indentElement + (viewSideLength * count));

            if( i <4)
                viewParam.setMargins(leftMarginView - 60, fromTop1, 0, 0);
            else
                viewParam.setMargins(leftMarginView - 60, fromTop2, 0, 0);

            rowTextView.setBackground(box);
            rowTextView.setText(letters[i]);

            rowTextView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            rowTextView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.size));

            rowTextView.setGravity(Gravity.CENTER);

            rowTextView.setLayoutParams(viewParam);
            viewGroup.addView(rowTextView);
            myTextViews[i] = rowTextView;
            count++;
        }



        new CountDownTimer(7000, 1000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                viewStartPositions = generateViewPositions(myTextViews);
                shuffle(viewStartPositions);
                reset();
            }
        }.start();
    }

    public void ButtonOnClick(View v) {
        switch (v.getId()) {
            case R.id.easy:
                dictType = R.raw.dictionary_easy;
                setContentView(R.layout.fragloader);
                break;

            //Commented until more dictionaries available
//            case R.id.medium:
//                dictType = R.raw.dictionary_easy;
//                setContentView(R.layout.fragloader);
//                break;
//            case R.id.hard:
//                dictType = R.raw.dictionary_easy;
//                setContentView(R.layout.fragloader);
//                break;
        }
    }

    int getDict() { return dictType;}

    public Context getCtxt() { return context;}


    //The code below animates the movement of the tiles to their new positions
    private void reset(){

        TranslateAnimation animation;

        for(int j = 0; j<wordLength;j++) {

            animation = new TranslateAnimation(
                    (int)myTextViews[j].getX()-(int)viewStartPositions[j].x, 0,
                    (int)myTextViews[j].getY()-(int)viewStartPositions[j].y, 0);

            animation.setDuration(1000);
            myTextViews[j].startAnimation(animation);

            myTextViews[j].setX((int)( viewStartPositions[j].x ) );
            myTextViews[j].setY((int)( viewStartPositions[j].y ) );
        }

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished){}

            public void onFinish() {
                viewStartPositions = generateViewPositions(myTextViews);
                shuffle(viewStartPositions);
                reset();
            }
        }.start();

    }

}