package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.Utilities;
import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Position of the word normalized to sentence length.
 * 
 * @author Francesco Ronzano
 *
 */
public class WordPosition implements FeatCalculator<Double, TrainingExample, TrainingCtx> {
	
	@Override
	public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
		MyDouble retValue = new MyDouble(0d);
		
		if(obj != null && obj.getSentence() != null && obj.getWordOffsets() != null) {
			String sentence = obj.getSentence().trim();
			String[] splitSentence = sentence.split(" ");
			
			if(splitSentence.length > 0 && Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).size() > 0) {
				Double sentenceLength = Integer.valueOf(splitSentence.length).doubleValue();
				Double index = Double.valueOf(Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).get(0));

				index++;
				if(sentenceLength >= index) {
					retValue.setValue(index / sentenceLength);
				}
			}
		}
		
		return retValue;
	}
	
}
