package edu.upf.taln.lastus.cwi.feature;

import java.util.HashMap;
import java.util.Map;

import edu.upc.freeling.HmmTagger;
import edu.upc.freeling.ListSentence;
import edu.upc.freeling.ListSentenceIterator;
import edu.upc.freeling.ListWord;
import edu.upc.freeling.ListWordIterator;
import edu.upc.freeling.Maco;
import edu.upc.freeling.MacoOptions;
import edu.upc.freeling.Nec;
import edu.upc.freeling.SWIGTYPE_p_splitter_status;
import edu.upc.freeling.Senses;
import edu.upc.freeling.Sentence;
import edu.upc.freeling.Splitter;
import edu.upc.freeling.Tokenizer;
import edu.upc.freeling.Ukb;
import edu.upc.freeling.Util;
import edu.upc.freeling.Word;

public class FreelingTextAnalyzer_English {

	// Check the following paths
	private static final String FREELINGDIR = "/usr/local/";
	private static final String DATA = "/usr/local/share/freeling/";

	private static final String LANG = "en";
	// Define analyzers:
	private static Tokenizer tk = null;
	private static Splitter sp = null;
	private static Maco mf = null;
	private static HmmTagger tg = null;
	private static Nec neclass = null;
	private static Senses sen = null;
	private static Ukb dis = null;

	public FreelingTextAnalyzer_English() {
		init();
	}
	
	public static void init() {
		// Instantiate Freeling resources if not already done
		if(tk == null) {
			System.loadLibrary("freeling_javaAPI");

			Util.initLocale("default");

			// Create options set for maco analyzer.
			// Default values are Ok, except for data files.
			MacoOptions op = new MacoOptions(LANG);

			op.setDataFiles(
					"", 
					DATA + "common/punct.dat",
					DATA + LANG + "/dicc.src",
					DATA + LANG + "/afixos.dat",
					"",
					DATA + LANG + "/locucions.dat", 
					DATA + LANG + "/np.dat",
					"", // DATA + LANG + "/quantities.dat",
					DATA + LANG + "/probabilitats.dat");

			tk = new Tokenizer(DATA + LANG + "/tokenizer.dat");
			sp = new Splitter(DATA + LANG + "/splitter.dat");

			SWIGTYPE_p_splitter_status sid = sp.openSession();

			mf = new Maco(op);
			mf.setActiveOptions(false, true, true, true,  // select which among created 
					true, true, false, true,  // submodules are to be used. 
					true, true, false, true);  // default: all created submodules 
			// are used

			tg = new HmmTagger(DATA + LANG + "/tagger.dat", true, 2);

			sen = new Senses(DATA + LANG + "/senses.dat" ); // sense dictionary

			dis = new Ukb( DATA + LANG + "/ukb.dat" ); // sense disambiguator
		}
	}

	/**
	 * Extract tokens from text
	 * 
	 * @param text
	 * @return
	 */
	public ListWord toknize(String text) {

		ListWord l = tk.tokenize(text);

		return l;
	}

	/**
	 * Sentence split, morpho analysis, lemmtization, tagging and word sense disambiguation
	 * 
	 * @param text
	 * @return
	 */
	public ListSentence analyzeText(String text) {

		ListWord l = tk.tokenize(text);

		SWIGTYPE_p_splitter_status sid = sp.openSession();

		ListSentence ls = sp.split(sid, l, true);

		mf.analyze(ls);

		tg.analyze(ls);

		sen.analyze(ls);

		dis.analyze(ls);

		return ls;
	}
	
	public static Map<Integer,String> parseSentence(String sentense, Map<Integer,String> wsd) {
		// Set the text to analyze
		String sentText = sentense;

		// Instantiate the Analyzer
		FreelingTextAnalyzer_English tagger = new FreelingTextAnalyzer_English();
		
		// Analyze the text and get the result in the ListSentence variable
		ListSentence paarsedSentList = tagger.analyzeText(sentText);

		// Navigate the data structure of type ListSentence to access the text analysis results
		//System.out.println("\n Text analysis results: " + sentText);
		ListSentenceIterator lsi = new ListSentenceIterator(paarsedSentList);
		while(lsi.hasNext()) {
			Sentence se1 = lsi.next();
			ListWordIterator wIt = new ListWordIterator(se1);
			while (wIt.hasNext()) {
				Word wd = wIt.next();
				/*System.out.println("\n");
				System.out.println("**********************************************");
				System.out.println("**********************************************");
				System.out.println("   ********** ANALYSIS RESULT OF WORD: " + wd.getForm() + " **********");
				System.out.println("   > WORD FORM: " + wd.getForm());
				System.out.println("   > WORD LEMMA: " + wd.getForm());
				System.out.println("   > WORD TAG: " + wd.getForm());*/

				// Word senses
				try {
					String ss = wd.getSensesString();

					// The senses for a FreeLing word are a list of pair<string,double> (sense and page rank). From java, we
					// have to get them as a string with format sense:rank/sense:rank/sense:rank
					// which will have to be properly split to obtain the info.
					Map<String, Double> senseRanking = new HashMap<String, Double>();
					if(ss != null && !ss.equals("")) {
						//System.out.println("     SENSES (format: senseID:rank/senseID:rank/senseID:rank): " + ss);

						String[] splitSense = ss.split("/");
						if(splitSense.length > 0) {
							for(int k = 0; k < splitSense.length; k++) {
								String localSense = splitSense[k];
								String[] localSenseParts = localSense.split(":");
								if(localSenseParts != null && localSenseParts.length == 2) {
									String senseID = localSenseParts[0];
									String senseRank = localSenseParts[1];

									if(senseID != null && !senseID.equals("") && senseRank != null && !senseRank.equals("")) {
										senseRanking.put(senseID, Double.valueOf(senseRank));
									}
								}

							}
						}
					}

					String maxSenseID = "";
					Double maxSenseRank = 0d;
					for(Map.Entry<String, Double> entry : senseRanking.entrySet()) {
						//System.out.println("      |--> WORD: '" + wd.getForm() + "' -> candidate sense: " + entry.getKey() + " with ranking: " + entry.getValue());

						if(maxSenseID.equals("")) {
							maxSenseID = entry.getKey();
							maxSenseRank = entry.getValue();
						}
						else {
							if(entry.getValue() > maxSenseRank) {
								maxSenseID = entry.getKey();
								maxSenseRank = entry.getValue();
							}
						}
					}
					if(!maxSenseID.equals("")) {
						//System.out.println("     --> WSD output result for word '" + wd.getForm() + "' (best sense) is " + maxSenseID + " with ranking " + maxSenseRank.toString());
						wsd.put(Integer.parseInt(String.valueOf(wd.getPosition())),maxSenseID);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}


			}
		}
		return wsd;
	}
	
	
	public static void main(String[] args) {
		
		parseSentence("This is a test sentence.", new HashMap<Integer,String>());
	}
	
}
