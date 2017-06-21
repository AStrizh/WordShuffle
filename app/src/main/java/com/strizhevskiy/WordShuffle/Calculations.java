package com.strizhevskiy.WordShuffle;

import android.graphics.PointF;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;


class Calculations {

    private static final int MIN_DISTANCE = 100;


    //Kunth shuffle to mix the letters of the word around
    static String[] shuffle ( String word ) {

        Random gen = new Random();

        String[] letters = breakString(word);
        int n = word.length();

        while (n > 1) {
            int k = gen.nextInt(n--);
            String temp = letters[n];
            letters[n] = letters[k];
            letters[k] = temp;
        }
        return letters;
    }

    //Kunth shuffle to mix and array of points
    static PointF[] shuffle ( PointF[] positions ) {

        Random gen = new Random();

        int n = positions.length;


        while (n > 1) {
            int k = gen.nextInt(n--);
            PointF temp = positions[n];
            positions[n] = positions[k];
            positions[k] = temp;
        }

        return positions;
    }

    //Checks if a textView is close (within a minimum acceptable distance) to a target
    static boolean distanceClose( PointF curCent, PointF tarCent ) {

        // Euclidean distance between two points
        double distance = Math.sqrt(Math.pow(curCent.x - tarCent.x, 2)
                + Math.pow(curCent.y - tarCent.y, 2));

        return (distance <= MIN_DISTANCE);
    }


    //Stores the centers of the target views to use for positioning checks
    static PointF[] generateTargetCenters( ImageView[] views ) {

        int n = views.length;
        PointF centerTarget;
        PointF[] tempCenters = new PointF[n];

        for (int i = 0; i<n; i++) {

            centerTarget = getCenter( views[i] );
            tempCenters[i] = centerTarget;
        }
        return tempCenters;
    }


    //Stores the start positions of the textViews to use to send back a view when overlap occurs
    static PointF[] generateViewPositions( TextView[] views ) {

        int n = views.length;
        PointF[] tempPositions = new PointF[n];

        float startX;
        float startY;

        for (int i = 0; i<n; i++) {

            startX = views[i].getX();
            startY = views[i].getY();
            tempPositions[i] = new PointF(startX, startY);
        }

        return tempPositions;
    }


    //Calculates the center of a view
    static PointF getCenter(View v){

        float centreX =  v.getX() + v.getWidth() / 2;
        float centreY =  v.getY() + v.getHeight() / 2;

        return new PointF(centreX, centreY);
    }

    static String[] breakString (String word) {

        String[] letters = new String[word.length()];
        for (int i = 0; i<word.length(); i++)
            letters[i] = String.valueOf(word.charAt(i));

        return letters;
    }

    //Checks if letters are arranged correctly
    static boolean correctAnswer( String[] letters, String word ){

        for (int i = 0; i<word.length(); i++)
            if ( letters[i].charAt(0) != word.charAt(i) )
                return false;

        return true;
    }

}
