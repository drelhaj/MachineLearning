package classifier;

/**
 * A Java class that implements a simple text learner, based on WEKA.
 * Modified from the original examples for the purpose of UCREL Summer School in Corpus Based NLP
 * at Lancaster University
 * http://ucrel.lancs.ac.uk/summerschool/nlp.php
 * Modified by Mahmoud El-Haj
 */

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Evaluation;
import java.util.Random;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.converters.ArffSaver;
import weka.core.converters.ArffLoader.ArffReader;
import java.io.*;

/**
 * This class implements a simple text learner in Java using WEKA.
 * It loads a text dataset written in ARFF format, evaluates a classifier on it,
 * and saves the learnt model for further use.
 */
public class MyFilteredLearner {

	/**
	 * String for classifier type.
	 */
	static String classifierName; 
	
	/**
	 * Object that stores training data.
	 */
	Instances trainData;
	/**
	 * Object that stores the filter
	 */
	StringToWordVector filter;
	/**
	 * Object that stores the classifier
	 */
	FilteredClassifier classifier;
		
	/**
	 * This method loads a dataset in ARFF format. If the file does not exist, or
	 * it has a wrong format, the attribute trainData is null.
	 * @param fileName The name of the file that stores the dataset.
	 */
	public void loadDataset(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			trainData = arff.getData();
			System.out.println("===== Loaded dataset: " + fileName + " =====");
			reader.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when reading: " + fileName);
			
		}
	}
	
	/**
	 * This method evaluates the classifier. As recommended by WEKA documentation,
	 * the classifier is defined but not trained yet. Evaluation of previously
	 * trained classifiers can lead to unexpected results.
	 */
	public void evaluate() {
		try {
			trainData.setClassIndex(0);
			filter = new StringToWordVector();
			filter.setAttributeIndices("last");
			classifier = new FilteredClassifier();
			classifier.setFilter(filter);
			if(classifierName.equals("NaiveBayse"))
			classifier.setClassifier(new NaiveBayes());//
			if(classifierName.equals("SMO"))
			classifier.setClassifier(new SMO());
			Evaluation eval = new Evaluation(trainData);
			eval.crossValidateModel(classifier, trainData, 4, new Random(1));
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println("===== Evaluating on filtered (training) dataset done =====");
						
		}
		catch (Exception e) {
			System.out.println("Problem found when evaluating");
		}
	}
	

	/**
	 * This method trains the classifier on the loaded dataset.
	 */
	public void learn() {
		try {
			trainData.setClassIndex(0);
			filter = new StringToWordVector();
			filter.setAttributeIndices("last");
			classifier = new FilteredClassifier();
			classifier.setFilter(filter);
			if(classifierName.equals("NaiveBayse"))
			classifier.setClassifier(new NaiveBayes());//
			if(classifierName.equals("SMO"))
			classifier.setClassifier(new SMO());
			classifier.buildClassifier(trainData);
			Instances trainingDataFiltered = Filter.useFilter(trainData, filter); // filter training data
			ArffSaver saver = new ArffSaver();
			saver.setInstances(trainingDataFiltered);
			saver.setFile(new File("arff/MyFilteredLearner"+classifierName+".arff"));
			saver.writeBatch();
			// Uncomment to see the classifier
			//System.out.println("--------->  "+classifier);
			System.out.println("===== Training on filtered (training) dataset done =====");
		}
		catch (Exception e) {
			System.out.println("Problem found when training");
		}
	}
	
	
	
	/**
	 * This method saves the trained model into a file. This is done by
	 * simple serialization of the classifier object.
	 * @param fileName The name of the file that will store the trained model.
	 */
	public void saveModel(String fileName) {
		try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(classifier);
            out.close();
 			System.out.println("===== Saved model: " + fileName + " =====");
        } 
		catch (IOException e) {
			System.out.println("Problem found when writing: " + fileName);
		}
	}
	
	/**
	 * Main method. It is an example of the usage of this class.
	 * @throws Exception 
	 */
	public static void main (String[] args) throws Exception {
		//classifier name (either NaiveBayes or SMO for this summer school experiment)
		classifierName = "SMO";//SMO
		
		System.out.println("Present Project Directory : "+ System.getProperty("user.dir"));
		MyFilteredLearner learner;
			learner = new MyFilteredLearner();
			learner.loadDataset("arff/TwoClasses.arff");
			// Evaluation must be done before training
			learner.evaluate();
			learner.learn();
			learner.saveModel("model/TwoClasses"+classifierName+".dat");
		
	}
	 
	 
}