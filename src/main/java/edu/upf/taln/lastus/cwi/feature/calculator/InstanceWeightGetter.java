package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Retrieve the double value that represent the instance weight
 * 
 * @author Francesco Ronzano
 *
 */
public class InstanceWeightGetter implements FeatCalculator<Double, TrainingExample, TrainingCtx> {
	
	@Override
	public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
		MyDouble retValue = new MyDouble(1d);
		
		if(obj != null && obj.getIsComplex() != null && obj.getIsComplex() > 1) {
			retValue.setValue(obj.getIsComplex().doubleValue());
		}
		
		return retValue;
	}
	
}
