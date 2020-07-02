package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyString;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Text of the sentence
 * 
 * @author Francesco Ronzano
 *
 */
public class SentenceText implements FeatCalculator<String, TrainingExample, TrainingCtx> {
	
	@Override
	public MyString calculateFeature(TrainingExample obj, TrainingCtx doc) {
		MyString retValue = new MyString("");
		
		if(obj != null && obj.getSentence() != null) {
			retValue.setValue(obj.getSentence());
		}
		
		return retValue;
	}
	
}
