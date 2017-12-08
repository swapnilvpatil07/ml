/**
 * 
 */
package com.fidel.dl4j;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
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



/**
 * @author Swapnil Patil
 *
 */
public class Classifier {

	/**
	 * @param args
	 */
	ParagraphVectors paragraphVectors;
	LabelAwareIterator iterator;
	TokenizerFactory tokenizerFactory;
	Scanner in;

	public static void main(String[] args) throws Exception {

		Classifier app = new Classifier();
		app.makeParagraphVectors();
		app.checkUnlabeledData();
	}

	void makeParagraphVectors() {
		ClassPathResource resource = new ClassPathResource("intents");

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
				.epochs(5).iterate(iterator).trainElementsRepresentation(true).trainWordVectors(true).tokenizerFactory(tokenizerFactory).build();

		// Start model training
		paragraphVectors.fit();
	}

	void checkUnlabeledData() throws Exception {
		String category = "";
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
					category = score.getFirst();
					tScore1 = score.getSecond();
				}
			}
			getAns(category);
			
			/*System.out.println("Intent: ");
			for (Pair<String, Double> score : scores) {
				System.out.println("      " + score.getFirst() + ": " + score.getSecond());
			}*/
		}
	}

	void getAns(String intent) throws Exception {
		// TODO Auto-generated method stub
		InputStream in =  this.getClass().getClassLoader().getResourceAsStream("intents/response.properties");
		Properties p = new Properties();
		p.load(in);
		System.out.println("Reply: " + p.getProperty(intent) + "\n");
	}

}
