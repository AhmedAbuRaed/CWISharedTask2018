package edu.upf.taln.lastus.cwi.feature.calculator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.upf.taln.lastus.cwi.Utilities;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;

import org.apache.log4j.Logger;

import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Created by Ahmed on 12/20/15.
 */
public class SenLowFreqGN implements FeatCalculator<Double, TrainingExample, TrainingCtx> {

    private static Logger logger = Logger.getLogger(DepthOfContext.class);

    static Dictionary dic = null;

    public SenLowFreqGN() {
        try {
            dic = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
        HashMap<String, Integer> synsFreq = new HashMap<String, Integer>();
        //System.out.println("WSD " + doc.getFreelingAnalysis().get(obj.getIndex()));
        Integer maxPer = 0;
        Integer minPer = 0;

        List<Word> words = null;
        MyDouble value = new MyDouble(-1d);
        try {
            for (Integer index : Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
                IndexWordSet lemmas = dic.lookupAllIndexWords(doc.getParsedSent().getLemmaAtIndex(index));
                if (null != lemmas && 0 < lemmas.size()) {
                    for (POS pos : POS.values()) {
                        IndexWord indexWord = lemmas.getIndexWord(pos);
                        if (null != indexWord) {
                            for (Synset synset : indexWord.getSenses()) {
                                words = synset.getWords();
                                for (Word word : words) {
                                    synsFreq.put(word.getLemma(), BNCFrequency.getLemmaFrequency(word.getLemma()));
                                }
                            }
                        }
                    }
                }
            }

            TreeMap<String, Integer> sortedMap = SortByValue(synsFreq);
            int high = 0;
            int low = 0;
            Boolean flag = false;
            for (Integer index : Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
                for (String key : sortedMap.descendingMap().descendingKeySet()) {
                    if (key.equals(doc.getParsedSent().getLemmaAtIndex(index))) {
                        flag = true;
                    }
                    if (flag == false) {
                        high++;
                    } else if (flag && !key.equals(doc.getParsedSent().getLemmaAtIndex(index))) {
                        low++;
                    }
                }
            }

            value.setValue(getPercentage(low, sortedMap.size()));

        } catch (JWNLException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static double getPercentage(int n, int total) {
        double proportion = ((double) n) / ((double) total);
        return proportion * 100;
    }

    public static TreeMap<String, Integer> SortByValue
            (HashMap<String, Integer> map) {
        ValueComparatorSenLowFreqGN vc = new ValueComparatorSenLowFreqGN(map);
        TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(vc);
        sortedMap.putAll(map);
        return sortedMap;
    }
}

class ValueComparatorSenLowFreqGN implements Comparator<String> {

    Map<String, Integer> map;

    public ValueComparatorSenLowFreqGN(Map<String, Integer> base) {
        this.map = base;
    }

    public int compare(String a, String b) {
        if (map.get(a) >= map.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}




