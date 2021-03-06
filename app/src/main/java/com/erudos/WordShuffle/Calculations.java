package com.erudos.WordShuffle;

import android.graphics.PointF;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;


class Calculations {

    private static final int MIN_DISTANCE = 100;

    static  <T> void shuffle ( T[] anArray ) {

        Random gen = new Random();

        int n = anArray.length;

        while (n > 1) {
            int k = gen.nextInt(n--);
            T temp = anArray[n];
            anArray[n] = anArray[k];
            anArray[k] = temp;
        }
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

    private String stringJoiner( String[] words, String delimiter){

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<words.length; i++){

            if( i==words.length-1 )
                builder.append(words[i]);
            else
                builder.append(words[i] + delimiter);

        }
        return builder.toString();
    }

}
