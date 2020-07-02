package edu.upf.taln.lastus.cwi.feature.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.backingdata.nlp.utils.parser.mate.MateParserEN;
import org.backingdata.nlp.utils.parser.mate.ParsedSentence;

import edu.upf.taln.lastus.cwi.feature.FreelingTextAnalyzer_English;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Context class of each training instance
 * 
 * @author Francesco Ronzano
 *
 */
public class TrainingCtx {
	private ParsedSentence parsedSent;
	private Map<Integer,String> freelingSynsets = new HashMap<Integer,String>();
	
	// Constructor
	public TrainingCtx(TrainingExample te) {
		super();
		
		//WSD of the sentense
		String sentence = te.getSentence();
		this.freelingSynsets = FreelingTextAnalyzer_English.parseSentence(sentence,freelingSynsets);
		
		String[] tokens = te.getSentence().split(" ");
		List<String> tokenList = new ArrayList<String>();
		for(int i = 0; i < tokens.length; i++) {
			tokenList.add(tokens[i]);
		}
		this.parsedSent = MateParserEN.parseSentenceTokens(tokenList);
	}

	// Getters and setters
	public ParsedSentence getParsedSent() {
		return parsedSent;
	}

	public void setParsedSent(ParsedSentence parsedSent) {
		this.parsedSent = parsedSent;
	}
	
	public Map<Integer, String> getFreelingAnalysis(){
		return freelingSynsets;
	}
}
