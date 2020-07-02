package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.Utilities;
import org.apache.log4j.Logger;

import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyString;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Token of the parent in the dependency tree of the current word.
 *
 * @author Francesco Ronzano
 *
 */
public class ParentTokenOfContext implements FeatCalculator<String, TrainingExample, TrainingCtx> {

	private static Logger logger = Logger.getLogger(ParentTokenOfContext.class);

	private Integer relativePosition = -1;

	public ParentTokenOfContext(Integer relPosition) {
		relPosition = (relPosition != null) ? relPosition : 0;
		this.relativePosition = relPosition;
	}

	@Override
	public MyString calculateFeature(TrainingExample obj, TrainingCtx doc) {
		MyString retValue = new MyString("__NO_TOKEN__");

		if(obj != null && obj.getSentence() != null && obj.getWordOffsets() != null && Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).size() >0) {
			int relIndex = Utilities.getIndex(obj.getSentence(), obj.getWordOffsets()).get(0) + this.relativePosition;
			String[] splitSentence = obj.getSentence().split(" ");
			if(relIndex >= 0 && relIndex < splitSentence.length) {
				String word_depPars = doc.getParsedSent().getToken().get(relIndex);
				String pos_depPars = doc.getParsedSent().getPos().get(relIndex);
				if(splitSentence[relIndex].equals(word_depPars) && pos_depPars != null && !pos_depPars.equals("")) {
					Integer parentId = doc.getParsedSent().getParentId_depTree(relIndex);
					if(parentId >= 0 && doc.getParsedSent().getToken().get(parentId) != null) {
						retValue.setValue(doc.getParsedSent().getToken().get(parentId));
					}
				}
				else {
					logger.error("The word and the dep parse word are not equal > word:" + obj.getWord() + ", dep parse word: " + word_depPars);
				}
			}
		}
		// System.out.println("Token: " + obj.getWord() + ", parent: " + retValue.getValue());
		return retValue;
	}

}
