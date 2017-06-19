package com.strizhevskiy.WordShuffle;

/**
 * Created by abstr on 12/23/2016.
 */

public class Calculations {



/*

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

                myTextViews[j].setX((int)( viewStartPositions[j].x ) );
                myTextViews[j].setY((int)( viewStartPositions[j].y ) );
            }
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










*/


}
