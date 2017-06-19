package com.strizhevskiy.WordShuffle;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private static int dictType;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

    }


    public void ButtonOnClick(View v) {
        switch (v.getId()) {
            case R.id.easy:
                dictType = R.raw.dictionary_easy;
                setContentView(R.layout.fragloader);
                break;

            case R.id.medium:
                dictType = R.raw.dictionary_easy;
                setContentView(R.layout.fragloader);
                break;
            case R.id.hard:
                dictType = R.raw.dictionary_easy;
                setContentView(R.layout.fragloader);
                break;
        }
    }

    public int getDict() {
        return dictType;
    }

    public Context getCtxt() {
        return context;
    }


}