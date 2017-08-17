package com.strizhevskiy.WordShuffle;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import static com.strizhevskiy.WordShuffle.Calculations.*;


public class ContentFragment extends Fragment  {

    private String[] words;
    private Context context;
    private View fragView;
    private InterstitialAd mInterstitialAd;

    private WordShuffle ws;
    private TextView hint;
    private TextView score;
    private TextView message;
    private Button resetBtn;

    private int points;
    private int total;

    private ViewGroup mainLayout;
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

    //TODO: Convert this fragment into an independent activity

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        fragView = inflater.inflate(R.layout.mainfrag, container, false);
        mainLayout = (RelativeLayout) fragView;

        int fileResource = ((MainActivity)getActivity()).getDict();
        context = (getActivity()).getApplicationContext();

        //Gets the screen width so views could be built proportionally
        DisplayMetrics metrics = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthScreen = metrics.widthPixels;

        try {

            ws = new WordShuffle(fileResource, context);

        } catch (IOException e) {
            e.printStackTrace();
        }


        resetBtn=(Button)fragView.findViewById(R.id.reset);


        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId( getString(R.string.interstitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                skip();
            }
        });

        requestNewInterstitial();
        initializer();


        AdView bannerAd = (AdView) fragView.findViewById(R.id.bannerAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(adRequest);


        return(fragView);
    }




    //Creates movable textViews based on the provided word
    private void wordBuilder(String word) {

        wordWorking = word;
        Drawable box = ContextCompat.getDrawable(context, R.drawable.box);

        String[] letters = shuffle(wordWorking);
        wordLength = letters.length;
        int leftMarginView;


        myTextViews = new TextView[wordLength];
        viewStartPositions = new PointF[wordLength];
        letterCollection = new String[wordLength];

        for (int i = 0; i < wordLength; i++) {
            letterCollection[i] = " ";
        }

        for (int i = 0; i < wordLength; i++) {
            final TextView rowTextView = new TextView(context);

            RelativeLayout.LayoutParams viewParam = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);


            indentElement = widthScreen/wordLength;

            viewSideLength = (widthScreen - 2*indentElement) / wordLength;

            leftMarginView =  indentElement + (viewSideLength * i);

            viewParam.height = viewSideLength;
            viewParam.width = viewSideLength;
            viewParam.setMargins(leftMarginView, 100, 0, 0);

            rowTextView.setBackground(box);
            rowTextView.setText(letters[i]);
            rowTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            rowTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.size));

            rowTextView.setGravity(Gravity.CENTER);

            mainLayout.addView(rowTextView);
            rowTextView.setLayoutParams(viewParam);
            rowTextView.setOnTouchListener(onTouchListener());

            myTextViews[i] = rowTextView;

        }
    }


    //Creates static target views  based on the length of the provided word
    private void targetBuilder() {

        Drawable hole = ContextCompat.getDrawable(context, R.drawable.hole);
        myImageViews = new ImageView[wordLength];
        holeCenters = new PointF[wordLength];
        int leftMarginImage;
        int topOffset;

        for (int i = 0; i < wordLength; i++) {

            final ImageView holeView = new ImageView(context);
            holeView.setImageDrawable(hole);


            RelativeLayout.LayoutParams imageParam = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

            int paddingElement = indentElement/(wordLength + 1);
            viewSideLength = (widthScreen - indentElement) / wordLength;

            leftMarginImage = viewSideLength*i + paddingElement*(i+1);

            imageParam.height = viewSideLength;
            imageParam.width = viewSideLength;

            topOffset = 100 + viewSideLength + 10;

            imageParam.setMargins(leftMarginImage, topOffset, 0, 0);


            mainLayout.addView(holeView);
            holeView.setLayoutParams(imageParam);

            myImageViews[i] = holeView;

        }
    }


    //Set the parameters for the first game
    private void initializer(){

        Button skip = (Button)fragView.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    skip();
                }

            }
        });

        score = (TextView)fragView.findViewById(R.id.total);
        message = (TextView)fragView.findViewById(R.id.message);
        hint = (TextView)fragView.findViewById(R.id.hint);
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
                        0,360,Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

                rotateAnimation.setDuration(500);
                resetBtn.startAnimation(rotateAnimation);

                if(!firstLoad){
                    reset();
                }

            }

        });


    }


    //Button to allow user to skip word. Shows an interstitial ad.
    private void skip(){

        message.setText(getString(R.string.skipped) + " " + words[0]);
        letterCount = 0;

        //This pair removes the all word blocks
        for(View v : myTextViews)
            mainLayout.removeView(v);
        for(View v : myImageViews)
            mainLayout.removeView(v);

        taskBuilder();

    }


    //Checks if  user got the word right
    private void checkText() {

        if ( correctAnswer(letterCollection, wordWorking) ){

            for (int j = 0; j < wordLength; j++) {
                letterCollection[j] = " ";
            }
            letterCount = 0;
            Log.v("letterCount lowered", String.valueOf(letterCount));

            points = ( Integer.parseInt( words[1] ) );

            total += points;

            score.setText("Total: " + total);

            if( points>1)
                message.setText(String.format(getString(R.string.correct2), points));

            else
                message.setText(getString(R.string.correct1));

            //This pair removes the all word blocks
            for(View v : myTextViews)
                mainLayout.removeView(v);
            for(View v : myImageViews)
                mainLayout.removeView(v);

            taskBuilder();

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
                                Log.v("letterCount lowered", String.valueOf(letterCount));
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

                                //Puts the letter from the view into an array for checking later
                                letterCollection[i] = ( (TextView) view).getText().toString();
                                letterCount++;
                                Log.v("letterCount raised", String.valueOf(letterCount));

                                //Checks if the view now overlaps another
                                overlap(view);

                                break;
                            }
                            i++;
                        }
                        if (letterCount == wordLength) {
                            Log.v("CheckText called", String.valueOf(letterCount));
                            checkText();

                        }
                        break;

                    default:
                        break;
                }
                return true;
            }
        };
    }



    //Method to create interstitial ads
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


   //Checks overlap between tiles. If true moves background tile to start position
    private void overlap(View view){

        for(int j = 0; j<wordLength;j++) {

            if( getCenter(myTextViews[j]).equals( getCenter(view)) &&  myTextViews[j] != view){

                letterCount--;
                Log.v("letterCount lowered", String.valueOf(letterCount));
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
    }

}