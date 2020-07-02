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
public class SenSumLowFreqWSD implements FeatCalculator<Double, TrainingExample, TrainingCtx> {

    private static Logger logger = Logger.getLogger(DepthOfContext.class);

    static Dictionary dic = null;

    public SenSumLowFreqWSD() {
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
                                String synsetID = synset.getKey().toString().replaceFirst("^0+(?!$)", "").split("-")[0];
                                if (doc.getFreelingAnalysis().get(index) != null) {
                                    try {
                                        SenHighFreqWSD.init();
                                        String free30synsetID = doc.getFreelingAnalysis().get(index);
                                        String free30synsetIDonlyNum = free30synsetID.substring(0, free30synsetID.length() - 2);
                                        String converted31FreelingSynID = "";
                                        if (free30synsetID.contains("n") && SenHighFreqWSD.mapZeroToUnoWNnoun.containsKey(free30synsetIDonlyNum)) {
                                            converted31FreelingSynID = SenHighFreqWSD.mapZeroToUnoWNnoun.get(free30synsetIDonlyNum);
                                        } else if (free30synsetID.contains("a") && SenHighFreqWSD.mapZeroToUnoWNadjective.containsKey(free30synsetIDonlyNum)) {
                                            converted31FreelingSynID = SenHighFreqWSD.mapZeroToUnoWNadjective.get(free30synsetIDonlyNum);
                                        } else if (free30synsetID.contains("v") && SenHighFreqWSD.mapZeroToUnoWNverb.containsKey(free30synsetIDonlyNum)) {
                                            converted31FreelingSynID = SenHighFreqWSD.mapZeroToUnoWNverb.get(free30synsetIDonlyNum);
                                        } else if (free30synsetID.contains("r") && SenHighFreqWSD.mapZeroToUnoWNadverb.containsKey(free30synsetIDonlyNum)) {
                                            converted31FreelingSynID = SenHighFreqWSD.mapZeroToUnoWNadverb.get(free30synsetIDonlyNum);
                                        }

                                        while (converted31FreelingSynID.startsWith("0")) {
                                            converted31FreelingSynID = converted31FreelingSynID.substring(1);
                                        }

                                        if (synsetID.equals(converted31FreelingSynID)) {
                                            words = synset.getWords();
                                            for (Word word : words) {
                                                synsFreq.put(word.getLemma(), BNCFrequency.getLemmaFrequency(word.getLemma()));
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        logger.error("Exception during Freeling synset ID conversion.");
                                    }
                                }
                            }
                        }
                    }
                }
            }

            TreeMap<String, Integer> sortedMap = SortByValue(synsFreq);

            int high = 0;
            int low = 0;
            int total = 0;
            Boolean flag = false;
            for (Integer index : Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
                for (String key : sortedMap.descendingMap().descendingKeySet()) {
                    if (key.equals(doc.getParsedSent().getLemmaAtIndex(index))) {
                        flag = true;
                    }
                    if (flag == false) {
                        high = high + synsFreq.get(key);
                        //System.out.println("High " + key);
                    } else if (flag && !key.equals(doc.getParsedSent().getLemmaAtIndex(index))) {
                        low = low + synsFreq.get(key);
                        //System.out.println("low " + key);
                    }
                    total = total + synsFreq.get(key);
                }
            }
            value.setValue(getPercentage(low, total));


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
        ValueComparatorSenSumLowFreqWSD vc = new ValueComparatorSenSumLowFreqWSD(map);
        TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(vc);
        sortedMap.putAll(map);
        return sortedMap;
    }
}

class ValueComparatorSenSumLowFreqWSD implements Comparator<String> {

    Map<String, Integer> map;

    public ValueComparatorSenSumLowFreqWSD(Map<String, Integer> base) {
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




