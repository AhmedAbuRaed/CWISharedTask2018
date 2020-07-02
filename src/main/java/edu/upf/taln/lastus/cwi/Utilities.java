package edu.upf.taln.lastus.cwi;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class Utilities {
    public static ArrayList<Integer> getIndex(String sentence, Pair<Integer, Integer> targetWordOffsets) {
        ArrayList<Integer> indeces = new ArrayList<Integer>();
        HashMap<Integer, Pair<Integer, Integer>> words = new HashMap<Integer, Pair<Integer, Integer>>();
        String temp = sentence;
        int i = 0;
        int shift = 0;
        while (temp.contains(" ")) {
            words.put(i, Pair.of(shift, shift + temp.indexOf(" ")));
            shift += temp.indexOf(" ") + 1;
            temp = sentence.substring(shift, sentence.length());
            i++;
        }

        boolean select = false;
        for (Integer key : words.keySet()) {
            if (words.get(key).getLeft().equals(targetWordOffsets.getLeft())) {
                select = true;
            }
            if (select) {
                indeces.add(key);
            }
            if (words.get(key).getRight().equals(targetWordOffsets.getRight())) {
                select = false;
            }
        }

        return indeces;
    }

    public static void main(String args[]) {
        System.out.println(Utilities.getIndex("Normally , the land will be passed down to future generations in a way that recognizes the community 's traditional connection to that country .",
                Pair.of(28, 34)));

    }
}
