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
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.core.tokenizers.NGramTokenizer;
import java.io.*;

/**
 * This class implements a simple text learner in Java using WEKA.
 * It loads a text dataset written in ARFF format, evaluates a classifier on it,
 * and saves the learnt model for further use.
 */
public class ReducedFilteredLearner {

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
			//filter.setOptions(weka.core.Utils.splitOptions("weka.filters.unsupervised.attribute.StringToWordVector -R first-last -W 500 -prune-rate -1.0 -C -T -I -N 1 -L -stemmer weka.core.stemmers.SnowballStemmer -M 1 -tokenizer \"weka.core.tokenizers.NGramTokenizer -delimiters \" \\r\\n\\t.,;:\\\'\\\"()?!\" -max 2 -min 1\"\""));
			NGramTokenizer tokenizer=new NGramTokenizer();
			tokenizer.setNGramMinSize(1);
			tokenizer.setNGramMaxSize(1);
			tokenizer.setDelimiters("\\W");//(" \r\n\t.,;:'\"()?!'");
			filter.setTokenizer(tokenizer);
			filter.setUseStoplist(false);
			filter.setLowerCaseTokens(false);
			filter.setOutputWordCounts(true);
			filter.setWordsToKeep(100);
			classifier = new FilteredClassifier();
			classifier.setFilter(filter);			
			if(classifierName.equals("NaiveBayse"))
			classifier.setClassifier(new NaiveBayes());//
			if(classifierName.equals("SMO"))
			classifier.setClassifier(new SMO()); 
			//weka.classifiers.functions.SMO scheme = new weka.classifiers.functions.SMO();
			//scheme.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
			//classifier.setClassifier(scheme);
			Evaluation eval = new Evaluation(trainData);
			eval.crossValidateModel(classifier, trainData, 4, new Random(1));
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println("===== Evaluating on filtered (training) dataset done =====");
						
		}
		catch (Exception e) {
			System.out.println("Problem found when evaluating" + e);
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
			NGramTokenizer tokenizer=new NGramTokenizer();
			tokenizer.setNGramMinSize(1);
			tokenizer.setNGramMaxSize(1);
			tokenizer.setDelimiters("\\W");//(" \r\n\t.,;:'\"()?!'");
			filter.setTokenizer(tokenizer);
			filter.setUseStoplist(false);
			filter.setLowerCaseTokens(false);
			filter.setOutputWordCounts(true);
			filter.setWordsToKeep(100);		
			classifier.setFilter(filter);	
			if(classifierName.equals("NaiveBayse"))
			classifier.setClassifier(new NaiveBayes());//
			if(classifierName.equals("SMO"))
			classifier.setClassifier(new SMO());
			//weka.classifiers.functions.SMO scheme = new weka.classifiers.functions.SMO();
			//scheme.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
			//classifier.setClassifier(scheme);
			classifier.buildClassifier(trainData);
			// Uncomment to see the classifier
			System.out.println("--------->  "+classifier);
			System.out.println("===== Training on filtered (training) dataset done =====");
		
			
			filter.setInputFormat(trainData); //set input format to filter using training data
			Instances trainingDataFiltered = Filter.useFilter(trainData, filter); // filter training data
			ArffSaver saver = new ArffSaver();
			saver.setInstances(trainingDataFiltered);
			saver.setFile(new File("arff/ReducedFilteredLearner"+classifierName+".arff"));
			saver.writeBatch();
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
	 * @param args Command-line arguments: fileData and fileModel.
	 * @throws Exception 
	 */
	public static void main (String[] args) throws Exception {
		
		classifierName = "NaiveBayes";//SMO

		
		System.out.println("Present Project Directory : "+ System.getProperty("user.dir"));
		ReducedFilteredLearner learner;
			learner = new ReducedFilteredLearner();
			learner.loadDataset("MachineLearning/arff/TwoClasses.arff");
			// Evaluation must be done before training
			learner.evaluate();
			learner.learn();
			learner.saveModel("MachineLearning/model/TwoClasses"+classifierName+"_Reduced.dat");
		
	}
	 
	 
}