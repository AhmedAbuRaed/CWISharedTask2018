package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.Utilities;
import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;
import org.apache.log4j.Logger;

/**
 * Created by Ahmed on 11/11/15.
 */
public class WNGlossM implements FeatCalculator<Double, TrainingExample, TrainingCtx> {
    private static Logger logger = Logger.getLogger(DepthOfContext.class);

    static Dictionary dic = null;

    public WNGlossM() {
        try {
            dic = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
        double sum = 0;
        double count = 0;

        MyDouble value = new MyDouble(-1d);
        try {
            for (Integer index : Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
                IndexWordSet lemmas = dic.lookupAllIndexWords(doc.getParsedSent().getLemmaAtIndex(index));
                if (null != lemmas && 0 < lemmas.size()) {
                    for (POS pos : POS.values()) {
                        IndexWord indexWord = lemmas.getIndexWord(pos);
                        if (null != indexWord) {
                            for (Synset synset : indexWord.getSenses()) {
                                sum = sum + synset.getGloss().split(" ").length;
                                count++;
                            }
                        }
                    }
                }
            }
            value.setValue(sum / count);
        } catch (JWNLException e) {
            e.printStackTrace();
        }


        return value;
    }
}
