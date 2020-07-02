package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;
import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;

/**
 * Number of words.
 * 
 * @author Francesco Ronzano
 *
 */
public class CharNumber implements FeatCalculator<Double, TrainingExample, TrainingCtx> {
	
	@Override
	public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
		MyDouble retValue = new MyDouble(0d);
		
		if(obj != null && obj.getWord() != null) {
			String word = obj.getWord().trim();
			retValue.setValue(Integer.valueOf(word.length()).doubleValue());
		}
		
		return retValue;
	}
	
}
