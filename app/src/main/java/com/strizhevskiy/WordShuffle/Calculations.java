package com.strizhevskiy.WordShuffle;

import android.graphics.PointF;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by abstr on 12/23/2016.
 */

class Calculations {

    private static final int MINDISTANCE = 100;



    //Kunth shuffle to mix the letters of the word around
    static String[] shuffle (String word) {

        Random gen = new Random();

        //TODO: Change to char to String cast rather than array copy
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

    //Kunth shuffle to mix and array of points
    static PointF[] shuffle (PointF[] positions) {

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
    static boolean distanceClose(PointF curCent , PointF tarCent) {

        // Euclidean distance between two points
        double distance = Math.sqrt(Math.pow(curCent.x - tarCent.x, 2)
                + Math.pow(curCent.y - tarCent.y, 2));

        return (distance <= MINDISTANCE);
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


}
