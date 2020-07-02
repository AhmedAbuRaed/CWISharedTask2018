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
 * Created by Ahmed on 11/12/15.
 */
public class WNPOS implements FeatCalculator<Double, TrainingExample, TrainingCtx> {

    private static Logger logger = Logger.getLogger(DepthOfContext.class);

    static Dictionary dic = null;
    private POS pos;

    public WNPOS(POS pos) {
        try {
            dic = Dictionary.getDefaultResourceInstance();
            this.pos = pos;
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
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
                                if (synset.getPOS() == this.pos) {
                                    value.setValue(1.0);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }

        if (value.getValue() == -1d)
            value.setValue(0.0);

        return value;
    }
}
