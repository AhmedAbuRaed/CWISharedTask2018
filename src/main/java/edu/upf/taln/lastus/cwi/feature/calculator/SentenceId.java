package edu.upf.taln.lastus.cwi.feature.calculator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.lang3.StringUtils;

import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Unambiguous id of the sentence (across training set).
 * 
 * @author Francesco Ronzano
 *
 */
public class SentenceId implements FeatCalculator<Double, TrainingExample, TrainingCtx> {
	
	private static MessageDigest md = null;
	private static Double idCount = 0d;
	private static Map<String, Double> sentenceMap = new HashMap<String, Double>();
	
	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
		MyDouble retValue = new MyDouble(-1d);
		
		if(obj != null && StringUtils.isNoneBlank(obj.getSentence())) {
			
			String hex = (new HexBinaryAdapter()).marshal(md.digest(obj.getSentence().getBytes()));
			
			if(sentenceMap.containsKey(hex)) {
				retValue.setValue(sentenceMap.get(hex));
			}
			else {
				idCount = idCount + 1d;
				retValue.setValue(new Double(idCount));
				sentenceMap.put(hex, new Double(idCount));
			}
		}
		
		return retValue;
	}
	
}
