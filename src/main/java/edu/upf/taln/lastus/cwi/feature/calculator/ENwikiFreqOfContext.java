package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.Utilities;
import org.backingdata.nlp.utils.langres.wikifreq.WikipediaENwordFreq;

import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Frequency in English Wikipedia of words in relative position with respect to the current word.
 *
 * @author Francesco Ronzano
 */
public class ENwikiFreqOfContext implements FeatCalculator<Double, TrainingExample, TrainingCtx> {

    private Integer relativePosition = -1;

    public ENwikiFreqOfContext(Integer relPosition) {
        relPosition = (relPosition != null) ? relPosition : 0;
        this.relativePosition = relPosition;
    }

    @Override
    public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
        MyDouble retValue = new MyDouble(0d);

        if (obj != null && obj.getSentence() != null && obj.getWordOffsets() != null) {
            String sentence = obj.getSentence().trim();
            String[] splitSentence = sentence.split(" ");

            if (splitSentence.length > 0) {
                double val = 0d;
                for (Integer index : Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
                    int relIndex = index + this.relativePosition;
                    if (relIndex >= 0 && relIndex < splitSentence.length) {
                        val += WikipediaENwordFreq.getWordFrequency(splitSentence[relIndex]).doubleValue();
                    }
                }
                retValue.setValue(val / Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).size());
            }
        }

        return retValue;
    }

}
