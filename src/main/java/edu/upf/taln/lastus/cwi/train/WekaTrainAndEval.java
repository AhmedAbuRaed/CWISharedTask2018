package edu.upf.taln.lastus.cwi.train;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import weka.classifiers.functions.SMO;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Class to perform, once generated the training and testing ARFF, the following sequence of tasks:
 * - train a classifier (RandomForest, SMO, Logistic, NaiveBayes)
 * - evaluate the trained classifier on the testing ARFF instances so as to compute: F-score and G-score
 * @author Francesco Ronzano
 *
 */
public class WekaTrainAndEval {

	public static String baseFolder = "D:\\Research\\UPF\\Projects\\CWISharedTask2018\\";
	
	// UNWEIGHTED AND WEIGHTED TRAINING INSTANCES:
	// public static String trainingARFFname = "task11_training_v_3_NUM";
	public static String trainingARFFname = "task11_training_v_3_WEIGHTED_NUM";
	
	public static String testingARFFname = "task11_testingAnnotated_v_3_NUM";
	
	public static String newline = System.getProperty("line.separator");
	
	public static void main(String[] args) throws Exception {
		
		// 1) Load instances
		String trainingArffFilePath = baseFolder + trainingARFFname + ".arff";
		BufferedReader readerTraining = new BufferedReader(new FileReader(trainingArffFilePath));
		Instances trainingInstances = new Instances(readerTraining);
		trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
		readerTraining.close();
		
		System.out.println("1 > Loaded " + trainingInstances.numInstances() + " training instances.");
		System.out.println("1 > train attributes " + trainingInstances.numAttributes());
		
		
		// 2) Instantiate and train classifier
		// RandomForest classifierInstance = new RandomForest();
		SMO classifierInstance = new SMO();
		classifierInstance.setToleranceParameter(0.9d);
		// Logistic classifierInstance = new Logistic();
		// NaiveBayes classifierInstance = new NaiveBayes();
		classifierInstance.setDebug(true);
		classifierInstance.buildClassifier(trainingInstances);
		
		System.out.println("2 > Classifirer trained.");
		
		
		/* EVAL: 10-fold cross validation over training set 
		System.out.println("2.eval > Eval model:");
		Evaluation eval = new Evaluation(trainingInstances);
		eval.crossValidateModel(classifierInstance, trainingInstances, 10, new Random(1));
		
		System.out.println("================================================");
		System.out.println("================================================");
		System.out.println("================================================");
		System.out.println("\nEVALUATION DATA:\n");
		System.out.println(eval.toSummaryString("\nSummary\n======\n", false));

		System.out.println("\n================================================\n");
		System.out.println(eval.toMatrixString("\nConfusion matrix\n======\n"));
		
		System.out.println("\n================= CLASS DETAILS =================\n");
		System.out.println(eval.toClassDetailsString());
		
		// System.out.println("\n============= CUMULATIVE MARGIN DISTRIBUTION ====\n");
		// System.out.println(eval.toCumulativeMarginDistributionString());

		// System.out.println("\n================= TOTAL COST ====================\n");
		// System.out.println(eval.totalCost() + "");
		
		System.out.println("2.eval > Classifier evaluated.");
		*/
		
		/* EVAL: over annotated test set */
		String testingArffFilePath = baseFolder + testingARFFname + ".arff";
		BufferedReader readerTesting = new BufferedReader(new FileReader(testingArffFilePath));
		Instances testingInstances = new Instances(readerTesting);
		testingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
		readerTesting.close();
		
		System.out.println("5 > Loaded " + testingInstances.numInstances() + " testing instances.");
		System.out.println("5 > train attributes " + testingInstances.numAttributes());
		
		int correctClass = 0;
		int incorrectClass = 0;
		Writer writer = null;
		
		double truePositive = 0d;
		double falsePositive = 0d;
		double trueNegative = 0d;
		double falseNegative = 0d;
		
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(baseFolder + "EVAL_" + trainingARFFname + "_MODEL_OVER_" + testingARFFname + ".output"), "utf-8"));
			
			for(int i = 0; i < testingInstances.numInstances(); i++) {
				Instance instanceToClassify = testingInstances.get(i);
				String realClass = instanceToClassify.attribute(instanceToClassify.numAttributes() -1).value((int) instanceToClassify.value(instanceToClassify.numAttributes() -1));
				
				instanceToClassify.setClassValue(0d);
				double classInst = classifierInstance.classifyInstance(instanceToClassify);
				String forecastedClass = instanceToClassify.attribute(instanceToClassify.numAttributes() -1).value((int) classInst);
				
				System.out.println("5 > Instance number: " + i + " > Assigned class: " + forecastedClass + " (real class: " + realClass + ") --> " + ((forecastedClass.endsWith(realClass)) ? "CORRECT" : "INCORRECT"));
				
				String newLineStr = "";
				if( i < (testingInstances.numInstances() - 1) ) {
					newLineStr = newline;
				}
				
				if(forecastedClass.equals("COMPLEX")) {
					writer.write("1" + newLineStr);
				}
				else {
					writer.write("0" + newLineStr);
				}
				
				if(forecastedClass.equals(realClass)) {
					correctClass++;
				}
				else {
					incorrectClass++;
				}
				
				// true/false positive/negative
				if(forecastedClass.equals(realClass)) {
					if(forecastedClass.equals("COMPLEX")) {
						truePositive += 1d; // BOTH COMPLEX
					}
					else {
						trueNegative += 1d; // BOTH NOT_COMPLEX
					}
				}
				else {
					if(forecastedClass.equals("COMPLEX")) {
						falsePositive += 1d;
					}
					else {
						falseNegative += 1d;
					}
				} 
			}
		} catch (IOException ex) {
		  ex.printStackTrace();
		} finally {
		   try {writer.close();} catch (Exception ex) {ex.printStackTrace();}
		}
		
		System.out.println("5 > Correct class " + correctClass + " over " + (correctClass + incorrectClass));
		System.out.println("5 > Incorrect class " + incorrectClass + " over " + (correctClass + incorrectClass));
		
		System.out.println("5 > ---");
		System.out.println("5 > True positive: " + truePositive + " over " + (truePositive + trueNegative + falsePositive + falseNegative) + " - PERC: " + (100d * truePositive / (truePositive + trueNegative + falsePositive + falseNegative)));
		System.out.println("5 > True negative: " + trueNegative + " over " + (truePositive + trueNegative + falsePositive + falseNegative) + " - PERC: " + (100d * trueNegative / (truePositive + trueNegative + falsePositive + falseNegative)));
		System.out.println("5 > False positive: " + falsePositive + " over " + (truePositive + trueNegative + falsePositive + falseNegative) + " - PERC: " + (100d * falsePositive / (truePositive + trueNegative + falsePositive + falseNegative)));
		System.out.println("5 > False negative: " + falseNegative + " over " + (truePositive + trueNegative + falsePositive + falseNegative) + " - PERC: " + (100d * falseNegative / (truePositive + trueNegative + falsePositive + falseNegative)));
		
		// Recall is TP/(TP + FN) whereas precision is TP/(TP+FP).
		// True negative rate TN/(TN + FP)
		// Accuracy (ACC) = Σ True positive + Σ True negative / Σ Total population
		double precision = (truePositive == 0d) ? 0d : (truePositive / (truePositive + falsePositive));
		double racall = truePositive / (truePositive + falseNegative);
		double trueNegativeRate = trueNegative / (trueNegative + falsePositive);
		double accuracy = (truePositive + trueNegative) / (truePositive + trueNegative + falsePositive + falseNegative);
		System.out.println("5 > ---");
		System.out.println("5 > Precision: " + precision);
		System.out.println("5 > Recall: " + racall);
		System.out.println("5 > True negative rate: " + trueNegativeRate);
		System.out.println("5 > Accuracy: " + accuracy);
		
		// F-score and G-score
		// F-score: 2 * p * r / (p + r)
		// G-score: 2 * a * r / (a + r)
		double fscore = (2 * precision * racall) / (precision + racall);
		double gscore = (2 * accuracy * racall) / (accuracy + racall);
		System.out.println("5 > ---");
		System.out.println("5 > F-Score: " + fscore);
		System.out.println("5 > G-Score: " + gscore);
		System.out.println("5 > END.");
		
		
		/*
		// 3) Store trained model to file
		Debug.saveToFile("/home/francesco/Downloads/myClassifier.model", classifierInstance);
		
		System.out.println("3 > Stored model to file: '/home/francesco/Downloads/myClassifier.model'");
		
		
		// 4) Load stored model from file
		System.out.println("4 > Loading model from file: '/home/francesco/Downloads/myClassifier.model'");
		
		SerializedClassifier serializedClassifier = new SerializedClassifier();
		serializedClassifier.setModelFile(new File("/home/francesco/Downloads/myClassifier.model"));
		serializedClassifier.setDebug(false);
		System.out.println("4 > Classifiers loaded");
		
		
		// 5) Classify new instances
		String testingArffFilePath = "/home/francesco/Downloads/diabetes_test.arff";
		BufferedReader readerTesting = new BufferedReader(new FileReader(testingArffFilePath));
		Instances testingInstances = new Instances(readerTesting);
		testingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
		readerTesting.close();
		
		System.out.println("5 > Loaded " + testingInstances.numInstances() + " testing instances.");
		System.out.println("5 > train attributes " + testingInstances.numAttributes());
		
		
		for(int i = 0; i < testingInstances.numInstances(); i++) {
			Instance instanceToClassify = testingInstances.get(i);
			String realClass = instanceToClassify.attribute(instanceToClassify.numAttributes() -1).value((int) instanceToClassify.value(instanceToClassify.numAttributes() -1));
			
			double classInst = serializedClassifier.classifyInstance(instanceToClassify);
			String forecastedClass = instanceToClassify.attribute(instanceToClassify.numAttributes() -1).value((int) classInst);
			
			System.out.println("5 > Instance number: " + i + " > Assigned class: " + forecastedClass + " (real class: " + realClass + ") --> " + ((forecastedClass.endsWith(realClass)) ? "CORRECT" : "INCORRECT"));
		}
		*/

	}

}
