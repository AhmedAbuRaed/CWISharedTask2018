package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.Utilities;
import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyBase;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTree;
import net.sf.extjwnl.dictionary.Dictionary;
import org.apache.log4j.Logger;

/**
 * Created by Ahmed on 11/11/15.
 */
public class WNRelationN implements FeatCalculator<Double, TrainingExample, TrainingCtx> {

    private static Logger logger = Logger.getLogger(DepthOfContext.class);

    static Dictionary dic = null;

    public WNRelationN() {
        try {
            dic = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
        MyDouble value = new MyDouble(-1d);
        double count = 0;
        double sum = 0;
        try {
            for (Integer index : Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
                IndexWordSet lemmas = dic.lookupAllIndexWords(doc.getParsedSent().getLemmaAtIndex(index));
                if (null != lemmas && 0 < lemmas.size()) {
                    for (POS pos : POS.values()) {
                        IndexWord indexWord = lemmas.getIndexWord(pos);
                        if (null != indexWord) {
                            for (Synset synset : indexWord.getSenses()) {
                                try {

                                    PointerTargetNodeList relations = PointerUtils.getAlsoSees(synset);
                                    if (relations.size() > 0) {
                                        sum = sum + relations.size();
                                        count++;
                                    }
                                } catch (JWNLException e) {
                                    e.printStackTrace();
                                }
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
