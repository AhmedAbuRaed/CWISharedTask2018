package edu.upf.taln.lastus.cwi.feature.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.upf.taln.lastus.cwi.Utilities;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;

import org.apache.log4j.Logger;

import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Created by Ahmed on 12/20/15.
 */
public class SenHighFreqWSD implements FeatCalculator<Double, TrainingExample, TrainingCtx> {

	private static Logger logger = Logger.getLogger(DepthOfContext.class);
	public static Map<String, String> mapZeroToUnoWNverb = new HashMap<String, String>();
	public static Map<String, String> mapZeroToUnoWNadverb = new HashMap<String, String>();
	public static Map<String, String> mapZeroToUnoWNnoun = new HashMap<String, String>();
	public static Map<String, String> mapZeroToUnoWNadjective = new HashMap<String, String>();


	static Dictionary dic = null;

	public SenHighFreqWSD() {
		try {
			dic = Dictionary.getDefaultResourceInstance();
			try {
				init();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (JWNLException e) {
			e.printStackTrace();
		}
	}

	public static void init() throws IOException {
		if(mapZeroToUnoWNverb == null || mapZeroToUnoWNverb.size() == 0) {
			mapZeroToUnoWNverb = new HashMap<String, String>();
			mapZeroToUnoWNadjective = new HashMap<String, String>();
			mapZeroToUnoWNnoun = new HashMap<String, String>();
			mapZeroToUnoWNadverb = new HashMap<String, String>();

			BufferedReader br = new BufferedReader(new FileReader("src/main/resources/wn30map31.txt"));
			try {
				String line = br.readLine();

				while (line != null) {
					
					if(line.trim().startsWith("#")) {
						line = br.readLine();
						continue;
					}

					String [] atts = line.split("\t");

					if(atts == null || atts.length != 3 || atts[0] == null || atts[1] == null || atts[2] == null) {
						line = br.readLine();
						continue;
					}

					if(atts[0].trim().toLowerCase().equals("v")) {
						mapZeroToUnoWNverb.put(atts[1].trim(), atts[2].trim());
					}
					else if(atts[0].trim().toLowerCase().equals("a")) {
						mapZeroToUnoWNadjective.put(atts[1].trim(), atts[2].trim());
					}
					else if(atts[0].trim().toLowerCase().equals("n")) {
						mapZeroToUnoWNnoun.put(atts[1].trim(), atts[2].trim());
					}
					else if(atts[0].trim().toLowerCase().equals("r")) {
						mapZeroToUnoWNadverb.put(atts[1].trim(), atts[2].trim());
					}

					line = br.readLine();
				}
			} finally {
				br.close();
			}
		}
	}

	@Override
	public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
		HashMap<String, Integer> synsFreq = new HashMap<String, Integer>();
		Integer maxPer =0;
		Integer minPer =0;

		List<Word> words = null;
		MyDouble value = new MyDouble(-1d);
		try {
			for(Integer index: Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
				IndexWordSet lemmas = dic.lookupAllIndexWords(doc.getParsedSent().getLemmaAtIndex(index));
				if (null != lemmas && 0 < lemmas.size()) {
					for (POS pos : POS.values()) {
						IndexWord indexWord = lemmas.getIndexWord(pos);
						if (null != indexWord) {
							for (Synset synset : indexWord.getSenses()) {
								String synsetID = synset.getKey().toString().replaceFirst("^0+(?!$)", "").split("-")[0];
								if (doc.getFreelingAnalysis().get(index) != null) {
									try {
										SenHighFreqWSD.init();
										String free30synsetID = doc.getFreelingAnalysis().get(index);
										String free30synsetIDonlyNum = free30synsetID.substring(0, free30synsetID.length() - 2);
										String converted31FreelingSynID = "";
										if (free30synsetID.contains("n") && SenHighFreqWSD.mapZeroToUnoWNnoun.containsKey(free30synsetIDonlyNum)) {
											converted31FreelingSynID = SenHighFreqWSD.mapZeroToUnoWNnoun.get(free30synsetIDonlyNum);
										} else if (free30synsetID.contains("a") && SenHighFreqWSD.mapZeroToUnoWNadjective.containsKey(free30synsetIDonlyNum)) {
											converted31FreelingSynID = SenHighFreqWSD.mapZeroToUnoWNadjective.get(free30synsetIDonlyNum);
										} else if (free30synsetID.contains("v") && SenHighFreqWSD.mapZeroToUnoWNverb.containsKey(free30synsetIDonlyNum)) {
											converted31FreelingSynID = SenHighFreqWSD.mapZeroToUnoWNverb.get(free30synsetIDonlyNum);
										} else if (free30synsetID.contains("r") && SenHighFreqWSD.mapZeroToUnoWNadverb.containsKey(free30synsetIDonlyNum)) {
											converted31FreelingSynID = SenHighFreqWSD.mapZeroToUnoWNadverb.get(free30synsetIDonlyNum);
										}

										while (converted31FreelingSynID.startsWith("0")) {
											converted31FreelingSynID = converted31FreelingSynID.substring(1);
										}

										if (synsetID.equals(converted31FreelingSynID)) {
											words = synset.getWords();
											for (Word word : words) {
												synsFreq.put(word.getLemma(), BNCFrequency.getLemmaFrequency(word.getLemma()));
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
										logger.error("Exception during Freeling synset ID conversion.");
									}
								}
							}
						}
					}
				}
			}

			TreeMap<String, Integer> sortedMap = SortByValue(synsFreq);
			int high = 0;
			int low=0;
			Boolean flag =false;
			for(Integer index: Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
				for (String key : sortedMap.descendingMap().descendingKeySet()) {
					if (key.equals(doc.getParsedSent().getLemmaAtIndex(index))) {
						flag = true;
					}
					if (flag == false) {
						high++;
						//System.out.println("High " + key);
					} else if (flag && !key.equals(doc.getParsedSent().getLemmaAtIndex(index))) {
						low++;
						//System.out.println("low " + key);
					}
				}
			}

			value.setValue(getPercentage(high,sortedMap.size()));
		} catch (JWNLException e) {
			e.printStackTrace();
		}

		return value;
	}

	public static double getPercentage(int n, int total) {
		double proportion = ((double) n) / ((double) total);
		return proportion * 100;
	}

	public static TreeMap<String, Integer> SortByValue
	(HashMap<String, Integer> map) {
		ValueComparatorSenHighFreqWSD vc =  new ValueComparatorSenHighFreqWSD(map);
		TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(vc);
		sortedMap.putAll(map);
		return sortedMap;
	}
}
class ValueComparatorSenHighFreqWSD implements Comparator<String> {

	Map<String, Integer> map;

	public ValueComparatorSenHighFreqWSD(Map<String, Integer> base) {
		this.map = base;
	}

	public int compare(String a, String b) {
		if (map.get(a) >= map.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}



