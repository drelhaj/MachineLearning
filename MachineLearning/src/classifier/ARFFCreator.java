/**
 * ARFF File Creator
 */
package classifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;

/**
 * Class to create ARFF files by reading textual data from a directory where
 * each sub directory is a class name (e.g. Positive) containing .txt files. It
 * is flexible to accept as many classes as needed.
 * 
 * @author Mahmoud El-Haj @ Lancaster University
 *
 */
public class ARFFCreator {

	static PrintWriter writer;

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		String arffDirectory = "MachineLearning/arff";//where you want to save the ARFF File
		String datasetDirectory = "MachineLearning/dataset";//where your dataset is (should contain sub-directories for each class (label or attribute).
		String[] classes = getClassNames(datasetDirectory);//get the classes (@attribute) names by simply reading the sub-directory names.
		System.out.println(Arrays.toString(classes));
		createArffHeader(arffDirectory+"/TwoClasses.arff", Arrays.toString(classes).trim(),"text String");//method to create the ARFF file, the third argument is the datatype you may need to change this manually at some point (e.g. "String, String, Int, Int")

		for (int x = 0; x < classes.length; x++) {
			System.out.println(classes[x]);
			String[] classData = readFiles(datasetDirectory, classes[x]);
			printToARFF(classData);
		}

		writer.close();

	}

			   
			
	
	/**
	 * print files contents and assign class (attribute) names to each line
	 * assuming directory names are the labels (e.g. put positive reviews in a
	 * directory called Positive and another for Negatives)
	 * 
	 * @param lines
	 */
	public static void printToARFF(String[] lines) {

		for (int x = 0; x < lines.length; x++) {
			writer.println(lines[x]);
			writer.flush();
		}

	}

	/**
	 * create ARFF File and its headers (usually called once)
	 * 
	 * @param arffFile
	 * @throws FileNotFoundException
	 */
	public static void createArffHeader(String arffFile, String classes, String classesTypes)
			throws FileNotFoundException {

		writer = new PrintWriter(arffFile);
		
		writer.println("@relation textClassifier");
		writer.print("\n");
		writer.println("@attribute textClassifier {"+classes.replace("[", "").replace("]", "").trim()+"}");
		writer.println("@attribute "+classesTypes.replace("[", "").replace("]", "").replace(",", ""));//this is just the easy way you may need to replace those to text String
		writer.print("\n");
		writer.println("@data");
		writer.flush();
	}
	
	
	
	/**
	 * get names of sub-directories (attributes/classes names). I treat each
	 * sub-directory as a class name (make sure no other sub directories
	 * presented) this makes the code flexible to accept more than 2 classes
	 * todo: isDirectory
	 * 
	 * @param datasetDir
	 * @return
	 */
	public static String[] getClassNames(String datasetDir) {
		String[] subdirectories = new File(datasetDir).list();
		return subdirectories;

	}
	
	
	/**
	 * get text files from classes directories and write contents to arff file
	 * 
	 * @param classDirectory
	 * @param className
	 * @return
	 * @throws IOException
	 */
	public static String[] readFiles(String classDirectory, String className) throws IOException {

		File folder = new File(classDirectory + File.separator + className);
		File[] listOfFiles = folder.listFiles();
		String[] lines = new String[listOfFiles.length];

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];

			if (file.isFile() && file.getName().endsWith(".txt")) {
				String content = FileUtils.readFileToString(file);
				lines[i] = className + "," + "'" + content.replaceAll("\n", "").replaceAll("\r", "").replaceAll(",", " ").replaceAll("'", " ").replace("_", " ").replace("-", " ").replace("&", " ").replace("%", " ").replaceAll(" +", " ").trim() + "'";
				// System.out.println(lines[i]);
			}
		}
		return lines;
	}
}
