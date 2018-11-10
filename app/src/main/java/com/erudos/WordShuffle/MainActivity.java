package com.erudos.WordShuffle;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.erudos.WordShuffle.Calculations.*;

//TODO: Make sure the animated sequence word placement adjusts to any screen type

public class MainActivity extends AppCompatActivity {

    public static final String DIFFICULTY = "com.erudos.WordShuffle.DIFFICULTY";

    TextView[] myTextViews;
    PointF[] viewStartPositions;
    int wordLength;

    Dialog dialog = null; //new Dialog(this, R.style.DialogFullscreenWithTitle);
    WebView webView = null; //dialog.findViewById(R.id.web_source_licenses);
    Button btn_source_licenses_close = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewGroup viewGroup = findViewById(android.R.id.content);

        //Toolbar toolbar = findViewById(R.id.toolbar_main);
       // setSupportActionBar(toolbar);

        DisplayMetrics metrics;
        int widthScreen;
        int indentElement;
        int viewSideLength;
        int leftMarginView;


        Drawable box = ContextCompat.getDrawable(this, R.drawable.box);
        Drawable hole = ContextCompat.getDrawable(this, R.drawable.hole);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.logotransparent);

        metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthScreen = metrics.widthPixels;


        String wordWorking = "WORDSHUFFLE";
        wordLength = wordWorking.length();

        String[] letters = breakString(wordWorking);

        myTextViews = new TextView[wordLength];
        viewStartPositions = new PointF[wordLength];

        int fromTop1 = 200;
        int fromTopCenter = 400;
        int fromTop2 = 600;
        int fromTop3 = 800;
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

        boxView.setLayoutParams(viewParam);
        boxView.setX(center); boxView.setY(fromTopCenter);

        boxView.setImageDrawable(hole);
        viewGroup.addView(boxView);


        for (int i = 0; i < wordLength; i++) {
            final TextView rowTextView = new TextView(this);

            if ( i == 4)
                count = 0;

            leftMarginView =  (int)(indentElement + (viewSideLength * count));

            if( i < 4 ) {
                rowTextView.setX(leftMarginView - 60); rowTextView.setY(fromTop1);
            }
            else {
                rowTextView.setX(leftMarginView - 60); rowTextView.setY(fromTop2);
            }


            rowTextView.setBackground(box);
            rowTextView.setText(letters[i]);

            rowTextView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            rowTextView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.letter_size));

            rowTextView.setGravity(Gravity.CENTER);

            rowTextView.setLayoutParams(viewParam);
            viewGroup.addView(rowTextView);
            myTextViews[i] = rowTextView;
            count++;
        }


        ImageView logoView = new ImageView(this);
        viewParam.height = widthScreen/4;
        viewParam.width = (3*widthScreen)/4;

        logoView.setLayoutParams(viewParam);
        logoView.setX(widthScreen/8); logoView.setY(fromTop3);

        logoView.setImageDrawable(logo);
        viewGroup.addView(logoView);

        new CountDownTimer(4000, 1000) {

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
                Intent intent = new Intent(this, GameActivity.class);
                String dictType = Integer.toString(R.raw.dictionary_easy_temp);
                intent.putExtra(DIFFICULTY, dictType);
                startActivity(intent);
                break;

            //Commented until more dictionaries available
            case R.id.medium:
                intent = new Intent(this, GameActivity.class);
                dictType = Integer.toString(R.raw.dictionary_medium_temp);
                intent.putExtra(DIFFICULTY, dictType);
                startActivity(intent);
                break;

            case R.id.hard:
                intent = new Intent(this, GameActivity.class);
                dictType = Integer.toString(R.raw.dictionary_hard_temp);
                intent.putExtra(DIFFICULTY, dictType);
                startActivity(intent);
                break;
        }
    }


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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        dialog = new Dialog(this, R.style.DialogFullscreenWithTitle);

        switch (item.getItemId()) {
            case R.id.action_menu_main_1:

                dialog.setTitle(getString(R.string.nav_privacy));
                dialog.setContentView(R.layout.dialog_source_licenses);
                webView = dialog.findViewById(R.id.web_source_licenses);
                webView.loadUrl("file:///android_asset/privacy_policy.html");
                break;

            case R.id.action_menu_main_2:

                dialog.setTitle(getString(R.string.nav_terms));
                dialog.setContentView(R.layout.dialog_source_licenses);
                webView = dialog.findViewById(R.id.web_source_licenses);
                webView.loadUrl("file:///android_asset/terms_and_conditions.html");
                break;

        }

        btn_source_licenses_close = dialog.findViewById(R.id.btn_source_licenses_close);
        btn_source_licenses_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        return super.onOptionsItemSelected(item);
    }

}