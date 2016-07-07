package classifier;

/**
 * A Java class that implements a simple text classifier, based on WEKA.
 * Modified from the original examples for the purpose of UCREL Summer School in Corpus Based NLP
 * at Lancaster University
 * http://ucrel.lancs.ac.uk/summerschool/nlp.php
 * Modified by Mahmoud El-Haj
 */
 
import weka.core.*;
import weka.classifiers.meta.FilteredClassifier;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

/**
 * This class implements a simple text classifier in Java using WEKA.
 * It loads a file with the text to classify, and the model that has been
 * learnt with MyFilteredLearner.java.
 */
 public class MyFilteredClassifier {

	/**
	 * String for classifier type.
	 */
	static String classifierName; 
		
	/**
	 * String that stores the text to classify
	 */
	String text;
	/**
	 * Object that stores the instance.
	 */
	Instances instances;
	/**
	 * Object that stores the classifier.
	 */
	FilteredClassifier classifier;
		
	/**
	 * This method loads the text to be classified.
	 * @param fileName The name of the file that stores the text.
	 */
	public void load(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			text = "";
			while ((line = reader.readLine()) != null) {
                text = text + " " + line;
            }
			System.out.println("===== Loaded text data: " + fileName + " =====");
			reader.close();
			//System.out.println(text);
		}
		catch (IOException e) {
			System.out.println("Problem found when reading: " + fileName);
		}
	}
			
	/**
	 * This method loads the model to be used as classifier.
	 * @param fileName The name of the file that stores the text.
	 */
	public void loadModel(String fileName) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
            Object tmp = in.readObject();
			classifier = (FilteredClassifier) tmp;
			//System.out.println(tmp);
            in.close();
 			//System.out.println("===== Loaded model: " + fileName + " =====");
       } 
		catch (Exception e) {
			// Given the cast, a ClassNotFoundException must be caught along with the IOException
			System.out.println("Problem found when reading: " + fileName);
		}
	}
	
	/**
	 * This method creates the instance to be classified, from the text that has been read.
	 */
	public void makeInstance() {
		// Create the attributes, class and text
		ArrayList<String> atts = new ArrayList<String>(2);
		atts.add("Chairman");
		atts.add("Governance");
		//atts.add("Remuneration");

		Attribute attribute1 = new Attribute("class", atts);
		Attribute attribute2 = new Attribute("text",(List<String>) null);
		// Create list of instances with one element
		ArrayList<Attribute> fvWekaAttributes = new ArrayList<Attribute>(2);
		fvWekaAttributes.add(attribute1);
		fvWekaAttributes.add(attribute2);
		instances = new Instances("Test relation", fvWekaAttributes, 1);           
		// Set class index
		instances.setClassIndex(0);
		// Create and add the instance
		DenseInstance instance = new DenseInstance(2);
		instance.setValue(attribute2, text);
		// Another way to do it:
		// instance.setValue((Attribute)fvWekaAttributes.elementAt(1), text);
		instances.add(instance);
 		//System.out.println("===== Instance created with reference dataset =====");
		//System.out.println(instances);
	}
	
	/**
	 * This method performs the classification of the instance.
	 * Output is done at the command-line.
	 */
	public void classify() {
		try {
			double pred = classifier.classifyInstance(instances.instance(0));
			//System.out.println("===== Classified instance =====");
			System.out.println("Class predicted: " + instances.classAttribute().value((int) pred));
		}
		catch (Exception e) {
			System.out.println("Problem found when classifying the text" + e);
		}		
	}
	
	/**
	 * Main method. It is an example of the usage of this class.
	 * @param args Command-line arguments: fileData and fileModel.
	 */
	public static void main (String[] args) {
	
		classifierName = "NaiveBayes";//SMO
		System.out.println("Classifier: >>> "+classifierName+" <<<\n");
		MyFilteredClassifier classifier;
		String testFile = "Gov";//Chair, Gov
		String model = "TwoClasses"+classifierName;
		for(int i=1 ; i<7; i++){
		classifier = new MyFilteredClassifier();
			classifier.load("MachineLearning/test/"+testFile+"_"+i+".txt");
			classifier.loadModel("MachineLearning/model/"+model+".dat");
			classifier.makeInstance();
			classifier.classify();
		}
	}
}