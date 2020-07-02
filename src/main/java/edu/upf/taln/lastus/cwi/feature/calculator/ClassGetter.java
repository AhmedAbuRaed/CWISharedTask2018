package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyString;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Class: COMPLEX, NOT_COMPLEX
 * 
 * @author Francesco Ronzano
 *
 */
public class ClassGetter implements FeatCalculator<String, TrainingExample, TrainingCtx> {
	
	@Override
	public MyString calculateFeature(TrainingExample obj, TrainingCtx doc) {
		MyString retValue = new MyString("NOT_COMPLEX");
		
		if(obj != null && obj.getIsComplex() != null && obj.getIsComplex() >= 1) {
			retValue.setValue("COMPLEX");
		}
		
		return retValue;
	}
	
}
