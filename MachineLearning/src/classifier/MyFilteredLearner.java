package classifier;

/**
 * A Java class that implements a simple text learner, based on WEKA.
 * To be used with MyFilteredClassifier.java.
 * WEKA is available at: http://www.cs.waikato.ac.nz/ml/weka/
 * Copyright (C) 2013 Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 *
 * This program is free software: you can redistribute it and/or modify
 * it for any purpose.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Evaluation;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.converters.ArffLoader.ArffReader;
import java.io.*;

/**
 * This class implements a simple text learner in Java using WEKA.
 * It loads a text dataset written in ARFF format, evaluates a classifier on it,
 * and saves the learnt model for further use.
 * @author Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 * @see MyFilteredClassifier
 */
public class MyFilteredLearner {

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
			classifier.setClassifier(new SMO());//SMO
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
			classifier.setClassifier(new SMO());
			classifier.buildClassifier(trainData);
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
	 * @param args Command-line arguments: fileData and fileModel.
	 * @throws Exception 
	 */
	public static void main (String[] args) throws Exception {
		System.out.println("Present Project Directory : "+ System.getProperty("user.dir"));
		MyFilteredLearner learner;
			learner = new MyFilteredLearner();
			learner.loadDataset("arff/myArff.arff");
			// Evaluation must be done before training
			// More info in: http://weka.wikispaces.com/Use+WEKA+in+your+Java+code
			learner.evaluate();
			//learner.attributeRank();
			learner.learn();
			learner.saveModel("model/myModel.dat");
		
	}
	
	
	/**
	  * Provides a {@code SortedSet} of {@code Map.Entry} objects. The sorting is in ascending order if {@param order} > 0
	  * and descending order if {@param order} <= 0.
	  * @param map   The map to be sorted.
	  * @param order The sorting order (positive means ascending, non-positive means descending).
	  * @param <K>   Keys.
	  * @param <V>   Values need to be {@code Comparable}.
	  * @return      A sorted set of {@code Map.Entry} objects.
	  */
	 static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>>
	 entriesSortedByValues(Map<K,V> map, final int order) {
	     SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<>(
	         new Comparator<Map.Entry<K,V>>() {
	             public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                 return (order > 0) ? compareToRetainDuplicates(e1.getValue(), e2.getValue()) : compareToRetainDuplicates(e2.getValue(), e1.getValue());
	         }
	     }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	 
	 private static <V extends Comparable<? super V>> int compareToRetainDuplicates(V v1, V v2) {
		    return (v1.compareTo(v2) == -1) ? -1 : 1;
		}
	 
	 
}