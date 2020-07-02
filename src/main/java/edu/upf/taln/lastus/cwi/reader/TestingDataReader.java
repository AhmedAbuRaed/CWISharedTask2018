package edu.upf.taln.lastus.cwi.reader;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

/**
 * Utility class to read testing instances
 * 
 * @author Francesco Ronzano
 *
 */
public class TestingDataReader {
	
	private static Logger logger = Logger.getLogger(TestingDataReader.class);
	
	/**
	 * Read training examples in a list from the specified file
	 * 
	 * @param resourcePath
	 * @return
	 */
	public static List<TrainingExample> readTestingSet(String resourcePath, boolean fromMvnFiles) {
		List<TrainingExample> retList = new ArrayList<TrainingExample>();
		
		URL testingFileURL = null;
		if(fromMvnFiles) {
			try {
				logger.info("Loading testing data from: " + resourcePath);
				testingFileURL = retList.getClass().getResource(resourcePath);
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error("Impossible to open the testing file!");
				return null;
			}
		}
		else {
			try {
				logger.info("Loading testing data from: " + resourcePath);
				testingFileURL = new File(resourcePath).toURI().toURL();
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error("Impossible to open the testing file!");
				return null;
			}
		}
		
		Integer lineCount = 0;
		try {
			 CSVReader reader = new CSVReader(new FileReader(Paths.get(testingFileURL.toURI()).toFile()), '\t', CSVParser.NULL_CHARACTER,false);
		     String [] nextLine;
		     while ((nextLine = reader.readNext()) != null) {
		        try  {
		        	lineCount++;
		        	// Training unannotated instance: length 3
		        	// Training annotated instance: length 4
		        	if(nextLine.length == 7) {
		        		TrainingExample newTrainingExample = new TrainingExample();
						newTrainingExample.setHITID(nextLine[0]);
						newTrainingExample.setSentence(nextLine[1]);
						newTrainingExample.setWordOffsets(Pair.of(Integer.valueOf(nextLine[2]), Integer.valueOf(nextLine[3])));
						newTrainingExample.setWord(nextLine[4]);
						newTrainingExample.setNativeNonNativeCount(Pair.of(Integer.valueOf(nextLine[5]), Integer.valueOf(nextLine[6])));
		        		
		        		// Consistency check
		        		if(StringUtils.isNoneBlank(newTrainingExample.getHITID()) && StringUtils.isNoneBlank(newTrainingExample.getSentence()) &&
								StringUtils.isNoneBlank(newTrainingExample.getWord()) &&
								newTrainingExample.getWordOffsets().getLeft() != null && newTrainingExample.getWordOffsets().getLeft() >= 0 &&
								newTrainingExample.getWordOffsets().getRight() != null && newTrainingExample.getWordOffsets().getRight() >= 0 &&
								newTrainingExample.getNativeNonNativeCount().getLeft() != null && newTrainingExample.getNativeNonNativeCount().getLeft() >= 0 &&
								newTrainingExample.getNativeNonNativeCount().getRight() != null && newTrainingExample.getNativeNonNativeCount().getRight() >= 0) {
		        			retList.add(newTrainingExample);
		        		}
		        		else {
		        			logger.error("Consistancy check failed at line: " + lineCount + " --> SKIPPED");
		        		}
		        		
		        	}
		        	else {
		        		logger.error("Impossible to read file line number (length != 4): " + lineCount);
		        	}
		        }
		        catch (Exception e) {
					e.printStackTrace();
					logger.error("Impossible to read file line number: " + lineCount);
				}
		     }
		     reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Impossible to read the training file!");
		}
		
		logger.info("Loaded " + retList.size() + " training data (from " + lineCount + " lines) from: " + resourcePath);
		
		return retList;
	}
}
