package edu.upf.taln.lastus.cwi.feature;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.extjwnl.data.POS;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.backingdata.nlp.utils.Manage;

import edu.upf.taln.ml.feat.FeatUtil;
import edu.upf.taln.ml.feat.FeatureSet;
import edu.upf.taln.ml.feat.NominalW;
import edu.upf.taln.ml.feat.NumericW;
import edu.upf.taln.ml.feat.StringW;
import edu.upf.taln.ml.feat.exception.FeatSetConsistencyException;
import edu.upf.taln.ml.feat.exception.FeatureException;
import edu.upf.taln.lastus.cwi.feature.calculator.BNCFrequency;
import edu.upf.taln.lastus.cwi.feature.calculator.CharNumber;
import edu.upf.taln.lastus.cwi.feature.calculator.ClassGetter;
import edu.upf.taln.lastus.cwi.feature.calculator.Dale_Chall;
import edu.upf.taln.lastus.cwi.feature.calculator.DepthOfContext;
import edu.upf.taln.lastus.cwi.feature.calculator.ENwikiFreqOfContext;
import edu.upf.taln.lastus.cwi.feature.calculator.ParentTokenOfContext;
import edu.upf.taln.lastus.cwi.feature.calculator.PosOfContext;
import edu.upf.taln.lastus.cwi.feature.calculator.SenHighFreqGN;
import edu.upf.taln.lastus.cwi.feature.calculator.SenHighFreqWSD;
import edu.upf.taln.lastus.cwi.feature.calculator.SenLowFreqGN;
import edu.upf.taln.lastus.cwi.feature.calculator.SenLowFreqWSD;
import edu.upf.taln.lastus.cwi.feature.calculator.SenSumHighFreqGN;
import edu.upf.taln.lastus.cwi.feature.calculator.SenSumHighFreqWSD;
import edu.upf.taln.lastus.cwi.feature.calculator.SenSumLowFreqGN;
import edu.upf.taln.lastus.cwi.feature.calculator.SenSumLowFreqWSD;
import edu.upf.taln.lastus.cwi.feature.calculator.SentenceId;
import edu.upf.taln.lastus.cwi.feature.calculator.SentenceText;
import edu.upf.taln.lastus.cwi.feature.calculator.WNDepth;
import edu.upf.taln.lastus.cwi.feature.calculator.WNGlossM;
import edu.upf.taln.lastus.cwi.feature.calculator.WNLemmaPOSN;
import edu.upf.taln.lastus.cwi.feature.calculator.WNPOS;
import edu.upf.taln.lastus.cwi.feature.calculator.WNRelationN;
import edu.upf.taln.lastus.cwi.feature.calculator.WNSenseN;
import edu.upf.taln.lastus.cwi.feature.calculator.WNSynsetN;
import edu.upf.taln.lastus.cwi.feature.calculator.WordCounter;
import edu.upf.taln.lastus.cwi.feature.calculator.WordOfContext;
import edu.upf.taln.lastus.cwi.feature.calculator.WordPosition;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.TestingDataReader;
import edu.upf.taln.lastus.cwi.reader.TrainingDataReader;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;
import weka.core.converters.ArffSaver;

/**
 * Read the training or testing file of CWI 2018 Task and generate the ARFF by extracting
 * the features of each instance
 * 
 * @author Ahmed
 *
 */
public class FeatureGenerator {

	private static Logger logger = Logger.getLogger(FeatureGenerator.class);
	
	// Folder where to store the output ARFF
	private static String outputFolder = "/home/aaburaed/CWISharedTask2018";
	
	// Version name to add to the output file name
	private static String version = "100";
	
	// Set to true to generate training set ARFF, otherwise to generate test set ARFF set to false 
	private static boolean generateTraining = true;

	// NLPutils resource folder
	// Path to resource folder downloadable at: http://backingdata.org/nlputils/ - http://backingdata.org/nlputils/NLPutils-resources-0.1.tar.gz
	private static String NLPutilResFolder = "/home/aaburaed/CWISharedTask2018/NLPutils-resources-0.1.tar/NLPutils-resources-0.1";

	private static Set<String> classValues = new HashSet<String>();
	static {
		classValues.add("COMPLEX");
		classValues.add("NOT_COMPLEX");
	}

	public static void main(String args[]) {

		Logger.getRootLogger().setLevel(Level.DEBUG);
		
		Manage.setResourceFolder(NLPutilResFolder);
		
		// Load training / testing instances
		// Symmary: cwi_training.txt
		// Detailed: cwi_training_allannotations.txt
		// MVN: List<TrainingExample> trainingExamples = TrainingDataReader.readTrainingSet("/data/cwi_training/cwi_training_allannotations.txt", true);
		// LOCAL:
		List<TrainingExample> trainingExamples = TrainingDataReader.readTrainingSet("/home/aaburaed/CWISharedTask2018/src/main/resources/data/CWI 2018 Training Set/english/Wikipedia_Dev.tsv", false);
		// TESTING UNANNOTATED: List<TrainingExample> testingExamples = TestingDataReader.readTestingSet("/data/cwi_testing/cwi_testing.txt", true);
		// TESTING ANNOTATED:
		// MVN: List<TrainingExample> testingExamples = TestingDataReader.readTestingSet("/data/cwi_testing/cwi_testing_annotated.txt", true);
		// LOCAL:
		List<TrainingExample> testingExamples = null/*TestingDataReader.readTestingSet("/home/francesco/Desktop/SEMEVAL_2016/TASK_11/DATA_AND_SCRIPTS/cwi_testing_annotated/cwi_testing_annotated.txt", false)*/;
		
		// Generate feature set
		FeatureSet<TrainingExample, TrainingCtx> featSet = new FeatureSet<TrainingExample, TrainingCtx>();
		
		try {
			// Adding document identifier
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("SENTENCE_ID", new SentenceId()));
			
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("SENTENCE_TOKENS", new SentenceText()));
			
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("CHARACTERS_NUMBER", new CharNumber()));
			
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("CURRENT_WORD", new WordOfContext(0)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("PREVIOUS_WORD_3", new WordOfContext(-3)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("PREVIOUS_WORD_2", new WordOfContext(-2)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("PREVIOUS_WORD_1", new WordOfContext(-1)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("NEXT_WORD_1", new WordOfContext(1)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("NEXT_WORD_2", new WordOfContext(2)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("NEXT_WORD_3", new WordOfContext(3)));
			
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("POS_CURRENT_WORD", new PosOfContext(0)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("POS_PREVIOUS_WORD_3", new PosOfContext(-3)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("POS_PREVIOUS_WORD_2", new PosOfContext(-2)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("POS_PREVIOUS_WORD_1", new PosOfContext(-1)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("POS_NEXT_WORD_1", new PosOfContext(1)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("POS_NEXT_WORD_2", new PosOfContext(2)));
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("POS_NEXT_WORD_3", new PosOfContext(3)));
			
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("DEPTREE_DEPTH_CURRENT_WORD", new DepthOfContext(0)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("DEPTREE_DEPTH_PREVIOUS_WORD_3", new DepthOfContext(-3)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("DEPTREE_DEPTH_PREVIOUS_WORD_2", new DepthOfContext(-2)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("DEPTREE_DEPTH_PREVIOUS_WORD_1", new DepthOfContext(-1)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("DEPTREE_DEPTH_NEXT_WORD_1", new DepthOfContext(1)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("DEPTREE_DEPTH_NEXT_WORD_2", new DepthOfContext(2)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("DEPTREE_DEPTH_NEXT_WORD_3", new DepthOfContext(3)));
			
			featSet.addFeature(new StringW<TrainingExample, TrainingCtx>("DEPTREE_PARENT_TOKEN_CURRENT_WORD", new ParentTokenOfContext(0)));
			
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("POSITION_OF_WORD", new WordPosition()));
			
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("NUM_WORDS_IN_SENTENCE", new WordCounter()));
			
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WIKIFREQ_CURRENT_WORD", new ENwikiFreqOfContext(0)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WIKIFREQ_PREVIOUS_WORD_3", new ENwikiFreqOfContext(-3)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WIKIFREQ_PREVIOUS_WORD_2", new ENwikiFreqOfContext(-2)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WIKIFREQ_PREVIOUS_WORD_1", new ENwikiFreqOfContext(-1)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WIKIFREQ_NEXT_WORD_1", new ENwikiFreqOfContext(1)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WIKIFREQ_NEXT_WORD_2", new ENwikiFreqOfContext(2)));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WIKIFREQ_NEXT_WORD_3", new ENwikiFreqOfContext(3)));
			
			//WordNet based features
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WNdepth", new WNDepth()));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WNSenseN", new WNSenseN()));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WNSynsetN", new WNSynsetN()));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WNLemmaPOSN", new WNLemmaPOSN()));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WNGlossM", new WNGlossM()));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WNRelationN", new WNRelationN()));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WNPOSNOUN", new WNPOS(POS.NOUN)));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WNPOSVERB", new WNPOS(POS.VERB)));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WNPOSADJECTIVE", new WNPOS(POS.ADJECTIVE)));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("WNPOSADVERB", new WNPOS(POS.ADVERB)));

            //BNCFrequency based features
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("BNCFrequency_CURRENT_WORD", new BNCFrequency(0)));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("BNCFrequency_PREVIOUS_WORD_3", new BNCFrequency(-3)));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("BNCFrequency_PREVIOUS_WORD_2", new BNCFrequency(-2)));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("BNCFrequency_PREVIOUS_WORD_1", new BNCFrequency(-1)));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("BNCFrequency_NEXT_WORD_1", new BNCFrequency(1)));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("BNCFrequency_NEXT_WORD_2", new BNCFrequency(2)));
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("BNCFrequency_NEXT_WORD_3", new BNCFrequency(3)));

            //Synonyms Frequencies comparison
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("SenHighFreqGN", new SenHighFreqGN()));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("SenHighFreqWSD", new SenHighFreqWSD()));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("SenLowFreqGN", new SenLowFreqGN()));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("SenLowFreqWSD", new SenLowFreqWSD()));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("SenSumHighFreqGN", new SenSumHighFreqGN()));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("SenSumHighFreqWSD", new SenSumHighFreqWSD()));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("SenSumLowFreqGN", new SenSumLowFreqGN()));
			featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("SenSumLowFreqWSD", new SenSumLowFreqWSD()));
			
            //Dale-Chall easy words
            featSet.addFeature(new NumericW<TrainingExample, TrainingCtx>("Dale_Chall", new Dale_Chall()));
            
			// Class feature (lasts)
			featSet.addFeature(new NominalW<TrainingExample, TrainingCtx>("class", classValues, new ClassGetter()));

			// Instance Weight Getter
			// featSet.addWeightFeature(new NumericW<TrainingExample, TrainingCtx>("Dale_Chall", new InstanceWeightGetter()));
			
			
		} catch (FeatureException e) {
			logger.debug("Error instantiating feature generation template.");
			e.printStackTrace();
			return;
		} catch (IOException e) {
            e.printStackTrace();
        }


		String outputInstancesType = (generateTraining) ? "training" : "testingAnnotated";
		logger.debug(" - " + outputInstancesType + " instances generation...");
		int parsedInstances = 0;
		int generatedFeatVectors = 0;
		List<TrainingExample> extamplesToParse = (generateTraining) ? trainingExamples : testingExamples;
		
		// Loop training / testing instances to populate ARFF
		int fromElem = 1;
		int toElem = 80000;
		
		int complexCount = 0;
		int notComplexCount = 0;
		for(TrainingExample te : extamplesToParse) {
			try {
				parsedInstances++;
				
				if(!(parsedInstances >= fromElem && parsedInstances < toElem)) {
					continue;
				}
				
				logger.debug("Parsing " + outputInstancesType + " instance " + parsedInstances + ": (sentence: " + te.getSentence() + "):");
				
				if(te.getIsComplex() != null) {
					if(te.getIsComplex() > 0) {
						complexCount++;
					}
					else {
						notComplexCount++;
					}
				}
				
				// Set training context
				TrainingCtx trCtx = new TrainingCtx(te);
				
				featSet.addElement(te, trCtx);
				
				if(generatedFeatVectors % 1000 == 0) {
					System.gc();
				}
				
				generatedFeatVectors++;
				
				if(generatedFeatVectors % 10 == 0) {
					logger.debug("STAT: complex " + complexCount + " / not complex: " + notComplexCount);
				}
				
			} catch (Exception e) {
				logger.debug("Error generating " + outputInstancesType + " instance features of example " + parsedInstances + ": (sentence: " + te.getSentence() + "):");
				e.printStackTrace();
			}
		}
		logger.debug(" - " + outputInstancesType + " instances generated (parsed instances: " + parsedInstances + ", generated vectors: " + generatedFeatVectors + ").");
		logger.debug("STAT: complex " + complexCount + " / not complex: " + notComplexCount);
		
		// Check for the consistency of the features
		logger.debug("Feature consistency check...");
		boolean featConsistency = true;
		try {
			featConsistency = FeatUtil.featureListConsistencyCheck(featSet);
		} catch (FeatSetConsistencyException e) {
			e.printStackTrace();
			featConsistency = false;
		}
		finally {
			if(!featConsistency) {
				logger.error("Feature consistency problem:");
				logger.error(FeatUtil.printFeatureStats(featSet));
				return;
			}
		}
		logger.debug("Feature consistency check passed.");

		// --- STORE ARFF:
		logger.info("STORING ARFF...");
		try {
			ArffSaver saver = new ArffSaver();
			saver.setInstances(FeatUtil.wekaInstanceGeneration(featSet, "cwi_v_" + version + "_from_" + fromElem + "_to_" + toElem) );
			saver.setFile(new File(outputFolder + "/Wikipedia_Dev_cwi_" + outputInstancesType + "_v_" + version + "_from_" + fromElem + "_to_" + toElem + ".arff"));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FeatSetConsistencyException e) {
			e.printStackTrace();
		}

		logger.info("END PROCESSING");

	}

}
