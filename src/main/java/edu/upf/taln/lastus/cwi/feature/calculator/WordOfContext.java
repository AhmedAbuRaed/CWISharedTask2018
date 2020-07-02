package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.Utilities;
import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyString;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Word in sentence in relative position with respect to the current word.
 * 
 * @author Francesco Ronzano
 *
 */
public class WordOfContext implements FeatCalculator<String, TrainingExample, TrainingCtx> {
	
private Integer relativePosition = -1;
	
	public WordOfContext(Integer relPosition) {
		relPosition = (relPosition != null) ? relPosition : 0;
		this.relativePosition = relPosition;
	}
	
	@Override
	public MyString calculateFeature(TrainingExample obj, TrainingCtx doc) {
		MyString retValue = new MyString("__NONE__");
		
		if(obj != null && obj.getSentence() != null && obj.getWordOffsets() != null) {
			String sentence = obj.getSentence().trim();
			String[] splitSentence = sentence.split(" ");
			
			if(splitSentence.length > 0) {
				if(Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).size() > 0) {
					int relIndex = Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).get(0) + this.relativePosition;
					if (relIndex >= 0 && relIndex < splitSentence.length) {
						retValue.setValue(splitSentence[relIndex]);
					}
				}
			}
		}
		
		return retValue;
	}
	
}
