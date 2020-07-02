package edu.upf.taln.lastus.cwi.feature.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import edu.upf.taln.lastus.cwi.Utilities;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;
import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import org.apache.commons.lang3.tuple.Pair;


/**
 * Created by Ahmed on 11/13/15.
 */
public class BNCFrequency implements FeatCalculator<Double, TrainingExample, TrainingCtx> {
    private static HashMap<String, Integer> frequencies = null;
    private Integer relativePosition = -1;

    public BNCFrequency(Integer relPosition) throws IOException {
        relPosition = (relPosition != null) ? relPosition : 0;
        this.relativePosition = relPosition;
        init();
    }

    public static void init() throws IOException {

        if (frequencies == null || frequencies.size() == 0) {
            frequencies = new HashMap<String, Integer>();
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/corpora-frequencies/BNC_lemmas_frequencies/1_1_all_fullalpha.txt"));
            try {
                String line = br.readLine();

                while (line != null) {
                    String[] atts = line.split("\t");
                    String lemma = atts[1];
                    int freq = Integer.parseInt(atts[4]);

                    if (!frequencies.containsKey(lemma.trim())) {
                        frequencies.put(lemma.trim(), freq);
                    } else {
                        Integer particalFreqCount = frequencies.get(lemma.trim());
                        frequencies.put(lemma.trim(), particalFreqCount + freq);
                    }

                    line = br.readLine();
                }
            } finally {
                br.close();
            }
        }
    }

    public static Integer getLemmaFrequency(String word) {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (word != null && !word.trim().equals("") && frequencies.containsKey(word.trim())) {
            return frequencies.get(word.trim());
        } else {
            return 0;
        }

    }

    @Override
    public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
        MyDouble value = new MyDouble(0d);

        if (obj != null && obj.getSentence() != null && obj.getWordOffsets() != null) {
            String sentence = obj.getSentence().trim();
            String[] splitSentence = sentence.split(" ");
            Double freq = 0d;

            if (splitSentence.length > 0) {
                for (Integer index : Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
                    int relIndex = index + this.relativePosition;
                    String lemma = doc.getParsedSent().getLemmaAtIndex(relIndex);
                    if (lemma != null && relIndex >= 0 && relIndex < splitSentence.length) {
                        freq += Double.valueOf(getLemmaFrequency(lemma));
                    }
                }
                value.setValue(freq / Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).size());
            }
        }
        return value;
    }
}
