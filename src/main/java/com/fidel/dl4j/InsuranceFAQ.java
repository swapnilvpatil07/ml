/**
 * 
 */
package com.fidel.dl4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.examples.nlp.paragraphvectors.tools.LabelSeeker;
import org.deeplearning4j.examples.nlp.paragraphvectors.tools.MeansBuilder;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Pair;

import com.fidel.dl4j.util.FileUtil;
import com.itextpdf.text.log.SysoCounter;

import scala.annotation.elidable;

/**
 * @author Swapnil Patil
 *
 */
public class InsuranceFAQ {
	/**
	 * @param args
	 */
	ParagraphVectors paragraphVectors;
	LabelAwareIterator iterator;
	TokenizerFactory tokenizerFactory;
	Scanner in;
	BufferedReader bufferedReader;

	public static void main(String[] args) throws Exception {

		InsuranceFAQ app = new InsuranceFAQ();
		app.trainModel();
		app.checkData();
	}

	void trainModel() {
		ClassPathResource resource = new ClassPathResource("Training-Data-Set/Intents");

		try {
			iterator = new FileLabelAwareIterator.Builder().addSourceFolder(resource.getFile()).build();
			System.out.println("Processing...");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

		// ParagraphVectors training configuration
		paragraphVectors = new ParagraphVectors.Builder()
				.windowSize(5)
				.iterations(10)
				.learningRate(0.05)
				.minLearningRate(0.001)
				.epochs(12)
				.batchSize(10)
				.iterate(iterator)
				.trainElementsRepresentation(true)
				.trainWordVectors(true)
				.tokenizerFactory(tokenizerFactory)
				.build();

		// Start model training
		paragraphVectors.fit();

	}

	void checkData() throws Exception {
		String intent = "";
		in = new Scanner(System.in);

		MeansBuilder meansBuilder = new MeansBuilder((InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable(),
				tokenizerFactory);
		LabelSeeker seeker = new LabelSeeker(iterator.getLabelsSource().getLabels(),
				(InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

		LabelledDocument document1 = new LabelledDocument();

		int ch = 0;
		String[] questions = FileUtil.readFile(new File("src/main/resources/Training-Data-Set/test-questions.txt"))
				.split("\n");

		for (String string : questions) {
			// while (ch != 2) {
			System.out.println("Ask: " + string);
			String que = /* in.nextLine() */ string;
			document1.setContent(que);
			INDArray documentAsCentroid1 = meansBuilder.documentAsVector(document1);
			List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid1);
			Double tScore1 = new Double(0.0);

			for (Pair<String, Double> score : scores) {
				if (score.getSecond() > tScore1) {
					intent = score.getFirst();
					tScore1 = score.getSecond();
				}
			}
			System.out.println("Intent: " + intent);
			if (tScore1 >= 0.55)
				getParagraph(intent);
			else
				System.out.println("Sorry, i didnt get that!!!\n");

			// System.out.println("Intent: ");
			
			 for (Pair<String, Double> score : scores) 
			 { 
				 System.out.println("      " + score.getFirst() + ": " + score.getSecond()); 
			 }
			 

			// }
		}
		while (ch != 2) {
			System.out.print("Ask: ");
			String que = in.nextLine();
			document1.setContent(que);
			INDArray documentAsCentroid1 = meansBuilder.documentAsVector(document1);
			List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid1);
			Double tScore1 = new Double(0.0);

			for (Pair<String, Double> score : scores) {
				if (score.getSecond() > tScore1) {
					intent = score.getFirst();
					tScore1 = score.getSecond();
				}
			}
			System.out.println("Intent: " + intent);
			if (tScore1 >= 0.50)
				getParagraph(intent);
			else
				System.out.println("Sorry, i didnt get that!!!");

			System.out.println("Intent: ");
			for (Pair<String, Double> score : scores) {
				System.out.println("      " + score.getFirst() + ": " + score.getSecond());
			}

		}
	}

	void getParagraph(String intent) throws Exception {
		// TODO Auto-generated method stub
		List<String> list = getFileList();
		String txtToPrint = "";
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			// intent = intent+".txt";0
			if (string.equals(intent.concat(".txt"))) {
				txtToPrint = FileUtil.readFile(new File("src/main/resources/Training-Data-Set/Response/" + string));
			}
			txtToPrint = txtToPrint.replaceAll("\\.", "\n");
		}
		System.out.println("Reply: " + txtToPrint);
	}

	private List<String> getFileList() {
		// TODO Auto-generated method stub
		List<String> fileList = new ArrayList<String>();
		File folder = new File("src/main/resources/Training-Data-Set/Response");

		if (folder.exists()) {
			File[] listOfFiles = folder.listFiles();
			// System.out.println("List:" + listOfFiles.length);
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					fileList.add(listOfFiles[i].getName());
					// System.out.println("File " + listOfFiles[i].getName());
				}
			}
		} else {
			System.out.println("Training-Data Folder Not Found...");
		}
		return fileList;
	}

}
