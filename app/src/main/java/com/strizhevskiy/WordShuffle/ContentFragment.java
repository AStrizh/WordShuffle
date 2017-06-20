package com.strizhevskiy.WordShuffle;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
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
import java.util.Random;




public class ContentFragment extends Fragment  {

    int fileResource;
    String[] words;
    private Context context;
    private View fragView;
    InterstitialAd mInterstitialAd;

    WordShuffle ws;
    TextView hint;
    TextView score;
    TextView message;
    Button skip;
    Button resetBtn;

    int points;
    int total;


    //Below Values Added from Tester Migration
    private ViewGroup mainLayout;
    private PointF[] holeCenters;
    private PointF[] viewStartPositions;
    private ImageView[] myImageViews;
    private TextView[] myTextViews;
    private String[] letterCollection;
    private int wordLength;
    private String wordWorking;

    DisplayMetrics metrics;
    int widthScreen;
    int indentElement;
    int viewSideLength;
    int paddingElement;
    boolean firstLoad;
    int minDistance = 100;

    String bannerAdID = "ca-app-pub-3940256099942544/6300978111";
    String interstitialAdID = "ca-app-pub-3940256099942544/1033173712";


    //The primary method or "main" method of the application starts here
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragView = inflater.inflate(R.layout.mainfrag, container, false);
        mainLayout = (RelativeLayout) fragView;


        fileResource =((MainActivity)getActivity()).getDict();
        context = (getActivity()).getApplicationContext();

        //Gets the screen width so views could be built proportionally
        metrics = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthScreen = metrics.widthPixels;

        try {

            ws = new WordShuffle(fileResource, context);

        } catch (IOException e) {
            e.printStackTrace();
        }


        resetBtn=(Button)fragView.findViewById(R.id.reset);


        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(interstitialAdID);

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
        bannerAd.setAdUnitId(bannerAdID);
        bannerAd.loadAd(adRequest);


        return(fragView);
    }
    // This is the end of the primary method






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

            RelativeLayout.LayoutParams viewParam =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
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
            rowTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.size));

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

            // create a new textview
            final ImageView holeView = new ImageView(context);
            holeView.setImageDrawable(hole);


            RelativeLayout.LayoutParams imageParam =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);



            paddingElement = indentElement/(wordLength + 1);
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

        skip = (Button)fragView.findViewById(R.id.skip);
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

        hint.setText(words[3]);


        resetBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

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

        //This pair removes the all word blocks
        for(View v : myTextViews)
            mainLayout.removeView(v);
        for(View v : myImageViews)
            mainLayout.removeView(v);

        taskBuilder();

    }





    //Checks if  user got the word right
    private void checkText() {


        //Assembles a string from the array of positioned letters
        //Checks if that string is the same as the word
        StringBuilder mySBuilder = new StringBuilder();
        for (String letter : letterCollection) {
            mySBuilder.append(letter);
        }

        if ( mySBuilder.toString().equals(wordWorking) ){

            for (int j = 0; j < wordLength; j++) {
                letterCollection[j] = " ";
            }


            points = ( Integer.parseInt(
                    words[2]) * words[0].length() ) - (points % 100);

            total += points;

            score.setText("Total: " + total);
            message.setText(String.format(getString(R.string.correct), points));

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
                    generateViewPositions();
                    generateTargetCenters();
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
                        break;

                    case MotionEvent.ACTION_UP:

                        /*
                        When finger lifted the System checks if the center of the view is close to
                        any of the targets. If it is the view snaps to the target
                         */
                        PointF currentCenter = getCenter(view);

                        int i=0;
                        for (PointF targetCenter : holeCenters) {

                            if ( distanceClose(currentCenter, targetCenter) ) {
                                view.setX((int)( targetCenter.x - (view.getWidth()/2) ) );
                                view.setY((int)( targetCenter.y - (view.getHeight()/2)) );

                                //Puts the letter from the view into an array for checking later
                                letterCollection[i] = ( (TextView) view).getText().toString();

                                //Checks if the view now overlaps another
                                overlap(view);

                                break;
                            }

                            i++;
                        }

                        checkText();

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



// TODO: Check if the calculation methods can be moved without issues

//------------------------------------------------------------------------------------------------//
//----------------------Methods Below should be moved to a new Class------------------------------//

    //TODO: Find out why both the WordShuffle class and Fragment have a shuffle method
    //A Kunth shuffle to mix the letters of the word around
    private static String[] shuffle (String word) {

        Random gen = new Random();

        //Gets rid of an extra empty element resulting from the split method
        //NOTE: May need to be changed in Android N! (When Android upgrades to Java 8)
        String[] mockLetters = word.split("");
        int n = word.length();
        String[] letters = new String[n];
        System.arraycopy(mockLetters,1,letters,0,n);

        while (n > 1) {
            int k = gen.nextInt(n--);
            String temp = letters[n];
            letters[n] = letters[k];
            letters[k] = temp;
        }

        return letters;
    }





    //This method checks for overlap if two views are in the same target.
    //This is if you put one letter on top of another letter which is already in a target spot
    private void overlap(View view){


        for(int j = 0; j<wordLength;j++) {

            if(  getCenter(myTextViews[j]).equals(getCenter(view)) &&
                    myTextViews[j] != view){


                //The code below animates the movement of the tiles to their start position
                TranslateAnimation animation = new TranslateAnimation((int)myTextViews[j].getX()-(int)viewStartPositions[j].x,
                        0, (int)myTextViews[j].getY()-(int)viewStartPositions[j].y, 0);

                animation.setDuration(300);
                myTextViews[j].startAnimation(animation);

                myTextViews[j].setX((int)( viewStartPositions[j].x ) );
                myTextViews[j].setY((int)( viewStartPositions[j].y ) );
            }
        }
    }


    private void reset(){

        TranslateAnimation animation;

        for(int j = 0; j<wordLength;j++) {

            //The code below animates the movement of the tiles to their start position
            animation = new TranslateAnimation((int)myTextViews[j].getX()-(int)viewStartPositions[j].x,
                    0, (int)myTextViews[j].getY()-(int)viewStartPositions[j].y, 0);

            animation.setDuration(300);
            myTextViews[j].startAnimation(animation);

            myTextViews[j].setX((int)( viewStartPositions[j].x ) );
            myTextViews[j].setY((int)( viewStartPositions[j].y ) );
        }

    }



    //Checks if a textView is close (within a minimum acceptable distance) to a target
    private boolean distanceClose(PointF curCent , PointF tarCent) {

        // Euclidean distance between two points
        double distance = Math.sqrt(Math.pow(curCent.x - tarCent.x, 2)
                + Math.pow(curCent.y - tarCent.y, 2));

        return (distance <= minDistance);
    }





    //Stores the centers of the target views to use for positioning checks
    private boolean generateTargetCenters() {

        int n = myImageViews.length;
        PointF centerTarget;

        for (int i = 0; i<n; i++) {

            centerTarget = getCenter( myImageViews[i] );
            holeCenters[i] = centerTarget;
        }

        return true;
    }





    //Stores the start positions of the textViews to use to send back a view when overlap occurs
    private boolean generateViewPositions() {

        int n = myTextViews.length;
        float startX;
        float startY;

        for (int i = 0; i<n; i++) {

            startX = myTextViews[i].getX();
            startY = myTextViews[i].getY();
            viewStartPositions[i] = new PointF(startX, startY);
        }

        return true;
    }




    //Calculates the center of a view
    private PointF getCenter( View v){

        float centreX =  v.getX() + v.getWidth() / 2;
        float centreY =  v.getY() + v.getHeight() / 2;

        return new PointF(centreX, centreY);
    }

//------------------------------------------------------------------------------------------------//




}