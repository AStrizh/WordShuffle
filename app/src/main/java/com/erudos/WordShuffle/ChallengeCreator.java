package com.erudos.WordShuffle;


import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

import static com.erudos.WordShuffle.Calculations.breakString;
import static com.erudos.WordShuffle.Calculations.shuffle;

class ChallengeCreator {

    private HashMap<Integer, String> myMap;
    private Context context;

    ChallengeCreator( int fileResource,  Context context ) throws IOException {

        myMap = new HashMap<>();
        this.context = context;
        mapMaker(fileResource);
    }

    WordChallenge getNextChallenge() {

        String line = myMap.get(RandLine());

        String[] working = line.split("~",2);
        String challengeWord = working[0].trim();
        String definitions = working[1].replace("~","\n");
        String[] scrambled = breakString(challengeWord);
        shuffle(scrambled);

        return new WordChallenge(challengeWord, scrambled, definitions);
    }

    private void mapMaker( int fileResource)  throws IOException {

        int lineNumber = 1;
        BufferedReader reader = null;

        try {

            InputStream inputStream = context.getResources().openRawResource(fileResource);
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null){

                myMap.put(lineNumber, line);
                lineNumber++;

            }

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private int RandLine() {

        Random r = new Random();
        return r.nextInt(myMap.size()) + 1;
    }
}