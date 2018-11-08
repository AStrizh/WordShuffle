package com.erudos.WordShuffle;


class WordChallenge {

    private String word;
    private String definitions;
    private String[] scrambledWord;

    WordChallenge(String word, String[] scrambledWord ,String definitions){
        this.word = word;
        this.definitions = definitions;
        this.scrambledWord = scrambledWord;
    }

    String getWord(){
        return word;
    }

    String getDefinitions(){
        return definitions;
    }

    String[] getScrambled(){
        return scrambledWord;
    }

}
