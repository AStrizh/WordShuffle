package com.erudos.WordShuffle;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

public class WordShuffle {

    private HashMap<Integer, String> myMap;
    private int fileResource;
    private BufferedReader reader;
    private Context context;


    public WordShuffle (int fileResource, Context context) throws IOException {

        myMap = new HashMap<>();
        this.fileResource = fileResource;
        this.context = context;

        mapMaker();

    }


    String[] taskCreator(){

        int linenumber = RandLine();

        return getWordArr(myMap.get(linenumber));
    }

    private static String[] getWordArr (String sentence) {

        StringBuilder sb = new StringBuilder();
        StringBuilder kb = new StringBuilder();

        String[] words = sentence.split(" ");

        String orgWord = words[0];
        //String shuffled = shuffle(words[0]);
        //String rank = words[1];

        words[0] = "";
        //words[1] = "";

        for(String s : words) {
            sb.append(" " + s);
        }

        for( char c : ( sb.toString().toCharArray() ) ) {

            //TODO: Find out what this commented block is for
            /*
            if( Character.isDigit(c) && Character.getNumericValue (c) > 3  ) {
                break;

            } else if (Character.isDigit(c) && Character.getNumericValue(c) > 1) {
                kb.append("\n" + c);

            } else {
                kb.append(c);
            }
            */
            if ( c == '~') {
                kb.append("\n");

            } else {
                kb.append(c);
            }

        }

        String[] trimmed = new String[3];

        trimmed[0] = orgWord;
        //trimmed[1] = rank;
        trimmed[2] = kb.toString().trim();

        return trimmed;

    }


    private int RandLine() {

        Random r = new Random();
        return r.nextInt(myMap.size()) + 1;
    }


    private void mapMaker()  throws IOException {

        int lineNumber = 1;

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

}
