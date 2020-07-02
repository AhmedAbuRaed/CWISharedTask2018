package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.Utilities;
import org.apache.log4j.Logger;

import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Depth in the dependency tree of the words in relative position with respect to the current word.
 *
 * @author Francesco Ronzano
 */
public class DepthOfContext implements FeatCalculator<Double, TrainingExample, TrainingCtx> {

    private static Logger logger = Logger.getLogger(DepthOfContext.class);

    private Integer relativePosition = -1;

    public DepthOfContext(Integer relPosition) {
        relPosition = (relPosition != null) ? relPosition : 0;
        this.relativePosition = relPosition;
    }

    @Override
    public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
        MyDouble retValue = new MyDouble(-1d);

        if (obj != null && obj.getSentence() != null && obj.getWordOffsets() != null) {
            double val = 0d;

            for (Integer index : Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
                int relIndex = index + this.relativePosition;
                String[] splitSentence = obj.getSentence().split(" ");
                if (relIndex >= 0 && relIndex < splitSentence.length) {
                    String word_depPars = doc.getParsedSent().getToken().get(relIndex);
                    String pos_depPars = doc.getParsedSent().getPos().get(relIndex);
                    if (splitSentence[relIndex].equals(word_depPars) && pos_depPars != null && !pos_depPars.equals("")) {
                        val += doc.getParsedSent().getDepthOfToken_depTree(relIndex).doubleValue();
                    } else {
                        logger.error("The word and the dep parse word are not equal > word:" + obj.getWord() + ", dep parse word: " + word_depPars);
                    }
                }
            }
            retValue.setValue(val / Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).size());
        }

        return retValue;
    }

}
