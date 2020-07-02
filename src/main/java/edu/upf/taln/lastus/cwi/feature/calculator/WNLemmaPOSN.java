package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.Utilities;
import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.dictionary.Dictionary;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed on 11/9/15.
 */
public class WNLemmaPOSN implements FeatCalculator<Double, TrainingExample, TrainingCtx> {
    private static Logger logger = Logger.getLogger(DepthOfContext.class);

    static Dictionary dic = null;

    public WNLemmaPOSN() {
        try {
            dic = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
        int count =0;
        MyDouble value = new MyDouble(-1d);
        try {
            List<IndexWordSet> indexWordlemmas = new ArrayList<IndexWordSet>();
            for(Integer index: Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()))
            {
                indexWordlemmas.add(dic.lookupAllIndexWords(doc.getParsedSent().getLemmaAtIndex(index)));
            }

            if (null != indexWordlemmas && 0 < indexWordlemmas.size()) {
                for(IndexWordSet indexWordSet: indexWordlemmas) {
                    count += indexWordSet.size();
                }
            }

            value.setValue((double) count);
        } catch (JWNLException e) {
            e.printStackTrace();
        }

        return value;
    }
}
