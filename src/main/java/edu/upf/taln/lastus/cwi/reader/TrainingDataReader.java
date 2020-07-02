package edu.upf.taln.lastus.cwi.reader;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVParser;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Utility class to read training instances
 *
 * @author Ahmed
 */
public class TrainingDataReader {

    private static Logger logger = Logger.getLogger(TrainingDataReader.class);

    /**
     * Read training examples in a list from the specified file
     *
     * @param resourcePath
     * @return
     */
    public static List<TrainingExample> readTrainingSet(String resourcePath, boolean fromMvnFiles) {
        List<TrainingExample> retList = new ArrayList<TrainingExample>();

        URL trainingFileURL = null;
        if (fromMvnFiles) {
            try {
                logger.info("Loading training data from: " + resourcePath);
                trainingFileURL = retList.getClass().getResource(resourcePath);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Impossible to open the training file!");
                return null;
            }
        } else {
            try {
                logger.info("Loading training data from: " + resourcePath);
                trainingFileURL = new File(resourcePath).toURI().toURL();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Impossible to open the training file!");
                return null;
            }
        }

        Integer lineCount = 0;
        try {
            CSVReader reader = new CSVReader(new FileReader(Paths.get(trainingFileURL.toURI()).toFile()), '\t', CSVParser.NULL_CHARACTER,false);

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                try {
                    lineCount++;
                    if (nextLine.length == 11) {
                        TrainingExample newTrainingExample = new TrainingExample();
                        newTrainingExample.setHITID(nextLine[0]);
                        newTrainingExample.setSentence(nextLine[1]);
                        newTrainingExample.setWordOffsets(Pair.of(Integer.valueOf(nextLine[2]), Integer.valueOf(nextLine[3])));
                        newTrainingExample.setWord(nextLine[4]);
                        newTrainingExample.setNativeNonNativeCount(Pair.of(Integer.valueOf(nextLine[5]), Integer.valueOf(nextLine[6])));
                        newTrainingExample.setNativeNonNativeComplexCount(Pair.of(Integer.valueOf(nextLine[7]), Integer.valueOf(nextLine[8])));
                        newTrainingExample.setIsComplex(Integer.valueOf(nextLine[9]));
                        newTrainingExample.setIsComplexScore(Double.valueOf(nextLine[10]));

                        // Consistency check
                        if (StringUtils.isNoneBlank(newTrainingExample.getHITID()) && StringUtils.isNoneBlank(newTrainingExample.getSentence()) &&
                                StringUtils.isNoneBlank(newTrainingExample.getWord()) &&
                                newTrainingExample.getWordOffsets().getLeft() != null && newTrainingExample.getWordOffsets().getLeft() >= 0 &&
                                newTrainingExample.getWordOffsets().getRight() != null && newTrainingExample.getWordOffsets().getRight() >= 0 &&
                                newTrainingExample.getNativeNonNativeCount().getLeft() != null && newTrainingExample.getNativeNonNativeCount().getLeft() >= 0 &&
                                newTrainingExample.getNativeNonNativeCount().getRight() != null && newTrainingExample.getNativeNonNativeCount().getRight() >= 0 &&
                                newTrainingExample.getNativeNonNativeComplexCount().getLeft() != null && newTrainingExample.getNativeNonNativeComplexCount().getLeft() >= 0 &&
                                newTrainingExample.getNativeNonNativeComplexCount().getRight() != null && newTrainingExample.getNativeNonNativeComplexCount().getRight() >= 0 &&
                                newTrainingExample.getClass() != null && newTrainingExample.getIsComplex() >= 0 && newTrainingExample.getIsComplex() <= 1 &&
                                newTrainingExample.getClass() != null && newTrainingExample.getIsComplexScore() >= 0 && newTrainingExample.getIsComplexScore() <= 1) {
                            retList.add(newTrainingExample);
                        } else {
                            logger.error("Consistancy check failed at line: " + lineCount + " --> SKIPPED");
                        }

                    } else {
                        logger.error("Impossible to read file line number (length != 4): " + lineCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("Impossible to read file line number: " + lineCount);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Impossible to read the training file!");
        }

        logger.info("Loaded " + retList.size() + " training data (from " + lineCount + " lines) from: " + resourcePath);

        return retList;
    }
}
