package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.Utilities;
import org.apache.log4j.Logger;

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
public class DepChildrenNumber implements FeatCalculator<Double, TrainingExample, TrainingCtx> {
	
	private static Logger logger = Logger.getLogger(DepChildrenNumber.class);
	
	@Override
	public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
		MyDouble retValue = new MyDouble(0d);
		
		if(obj != null && obj.getWord() != null) {
			double val = 0d;
			for (Integer index : Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
				String word_depPars = doc.getParsedSent().getToken().get(index);
				String pos_depPars = doc.getParsedSent().getPos().get(index);
				if (obj.getWord().equals(word_depPars) && pos_depPars != null && !pos_depPars.equals("")) {
					Integer childrenNumber = doc.getParsedSent().getChildrenIds_depTree(index).size();
					val += childrenNumber.doubleValue();
				} else {
					logger.error("The word and the dep parse word are not equal > word:" + obj.getWord() + ", dep parse word: " + word_depPars);
				}
			}
			retValue.setValue(val/ Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).size());
		}
		
		return retValue;
	}
	
}
