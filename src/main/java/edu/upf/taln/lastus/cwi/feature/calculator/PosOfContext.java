package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.Utilities;
import org.apache.log4j.Logger;

import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyString;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * POS of words in relative position with respect to the current word.
 *
 * @author Francesco Ronzano
 */
public class PosOfContext implements FeatCalculator<String, TrainingExample, TrainingCtx> {

    private static Logger logger = Logger.getLogger(PosOfContext.class);

    private Integer relativePosition = -1;

    public PosOfContext(Integer relPosition) {
        relPosition = (relPosition != null) ? relPosition : 0;
        this.relativePosition = relPosition;
    }

    @Override
    public MyString calculateFeature(TrainingExample obj, TrainingCtx doc) {
        MyString retValue = new MyString("__NO_POS_");

        if (obj != null && obj.getSentence() != null && obj.getWordOffsets() != null) {
            if (Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).size() > 0) {
                int relIndex = Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).get(0) + this.relativePosition;
                String[] splitSentence = obj.getSentence().split(" ");
                if (relIndex >= 0 && relIndex < splitSentence.length) {
                    String word_depPars = doc.getParsedSent().getToken().get(relIndex);
                    String pos_depPars = doc.getParsedSent().getPos().get(relIndex);
                    if (splitSentence[relIndex].equals(word_depPars) && pos_depPars != null && !pos_depPars.equals("")) {
                        retValue.setValue(pos_depPars);
                    } else {
                        logger.error("The word and the dep parse word are not equal > word:" + obj.getWord() + ", dep parse word: " + word_depPars);
                    }
                }
            }
        }

        return retValue;
    }

}
