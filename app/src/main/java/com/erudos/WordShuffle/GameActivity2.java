package com.erudos.WordShuffle;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;

public class GameActivity2 extends AppCompatActivity {

    private String[] words;
    private Context context;
    private ViewGroup contentView;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);

        Intent intent = getIntent();
        int fileResource = Integer.parseInt(intent.getStringExtra(MainActivity.DIFFICULTY));
        contentView = findViewById(android.R.id.content);
        context = getApplicationContext();


        //Gets the screen width so views could be built proportionally
        DisplayMetrics metrics = new DisplayMetrics();

        //getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthScreen = metrics.widthPixels;

        try {

            ws = new WordShuffle(fileResource, context);

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
