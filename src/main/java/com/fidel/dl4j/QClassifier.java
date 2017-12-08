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

/**
 * @author Swapnil Patil
 *
 */
public class QClassifier {
	/**
	 * @param args
	 */
	ParagraphVectors paragraphVectors;
	LabelAwareIterator iterator;
	TokenizerFactory tokenizerFactory;
	Scanner in;
	BufferedReader bufferedReader;

	public static void main(String[] args) throws Exception {

		QClassifier app = new QClassifier();
		app.makeParagraphVectors();
		app.checkUnlabeledData();
	}

	void makeParagraphVectors() {
		ClassPathResource resource = new ClassPathResource("Training-Data/Intents");

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
		paragraphVectors = new ParagraphVectors.Builder().learningRate(0.025).minLearningRate(0.001).batchSize(1000)
				.epochs(20).iterate(iterator)/* .trainElementsRepresentation(true) */.trainWordVectors(true)
				.tokenizerFactory(tokenizerFactory).build();

		// Start model training
		paragraphVectors.fit();
	}

	void checkUnlabeledData() throws Exception {
		String questionNo = "";
		in = new Scanner(System.in);

		MeansBuilder meansBuilder = new MeansBuilder((InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable(),
				tokenizerFactory);
		LabelSeeker seeker = new LabelSeeker(iterator.getLabelsSource().getLabels(),
				(InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

		LabelledDocument document1 = new LabelledDocument();

		int ch = 0;
		while (ch != 2) {
			System.out.print("Ask: ");
			String que = in.nextLine();
			document1.setContent(que);
			INDArray documentAsCentroid1 = meansBuilder.documentAsVector(document1);
			List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid1);
			Double tScore1 = new Double(0.0);

			for (Pair<String, Double> score : scores) {
				if (score.getSecond() > tScore1) {
					questionNo = score.getFirst();
					tScore1 = score.getSecond();
				}
			}
			//System.out.println("Intent: " + questionNo);
			getParagraph(getQNumber(questionNo));

			/*
			 * System.out.println("Intent: "); for (Pair<String, Double> score : scores) {
			 * System.out.println("      " + score.getFirst() + ": " + score.getSecond()); }
			 */
		}
	}

	void getParagraph(String intent) throws Exception {
		// TODO Auto-generated method stub
		List<String> list = getFileList();
		String txtToPrint = "";
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			if (getQNumber(string).equals(intent)) {
				txtToPrint = FileUtil.readFile(new File("src/main/resources/Training-Data/" + string));
			}
		}
		System.out.println(txtToPrint);
	}

	private String getQNumber(String intentNm) {
		// TODO Auto-generated method stub
		String no = intentNm.trim().replaceAll("[^0-9]", "");
		return no;
	}

	

	private List<String> getFileList() {
		// TODO Auto-generated method stub
		List<String> fileList = new ArrayList<String>();
		File folder = new File("src/main/resources/Training-Data");

		if (folder.exists()) {
			File[] listOfFiles = folder.listFiles();
			//System.out.println("List:" + listOfFiles.length);
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					fileList.add(listOfFiles[i].getName());
					//System.out.println("File " + listOfFiles[i].getName());
				}
			}
		} else {
			System.out.println("Training-Data Folder Not Found...");
		}
		return fileList;
	}

}
