package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Number of words.
 * 
 * @author Francesco Ronzano
 *
 */
public class WordCounter implements FeatCalculator<Double, TrainingExample, TrainingCtx> {
	
	@Override
	public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
		MyDouble retValue = new MyDouble(0d);
		
		if(obj != null && obj.getSentence() != null) {
			String sentence = obj.getSentence().trim();
			String[] splitSentence = sentence.split(" ");
			
			if(splitSentence.length > 0) {
				retValue.setValue(Integer.valueOf(splitSentence.length).doubleValue());
			}
		}
		
		return retValue;
	}
	
}
