package com.fidel.dl4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

import com.fidel.dl4j.util.FileUtil;

public class DatasetCreator {
	public static void main(String[] args) throws IOException {
		DatasetCreator create = new DatasetCreator();
		create.createDataset();
	}

	private String FILEPATH = "src/main/resources/Training-Data/";
	private int count = 0;
	FileWriter writer;
	File fileToWrite;
	BufferedReader bufferedReader;
	StringBuffer paragraph;
	BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
	private int cnt = 0;

	void createDataset() throws IOException {
		List<String> identifiers = new ArrayList<String>();
		paragraph = new StringBuffer();
		ClassPathResource resource1 = new ClassPathResource("identifiers.txt");
		ClassPathResource resource2 = new ClassPathResource("/converted-policy-wording/car_txt_1.txt");

		File file1 = resource1.getFile();
		File file2 = resource2.getFile();

		SentenceIterator iter1 = new BasicLineIterator(file1);
		SentenceIterator iter2 = new BasicLineIterator(file2);

		String sentense;

		ClassPathResource modelResource = new ClassPathResource("raw_sentences.txt");
		File file = modelResource.getFile();// .getParentFile();
		SentenceIterator iter3 = new BasicLineIterator(file);

		TokenizerFactory t = new DefaultTokenizerFactory();
		t.setTokenPreProcessor(new CommonPreprocessor());

		ParagraphVectors vec = new ParagraphVectors.Builder().windowSize(5).iterations(5).layerSize(100).iterate(iter3)
				.tokenizerFactory(t).build();

		vec.buildVocab();
		vec.fit();

		System.out.println("Training completed...");

		while (iter1.hasNext()) {
			identifiers.add(iter1.nextSentence());
		}

		identifiers.stream().forEach(System.out::println);
		
		FileUtil.dirCreation(FILEPATH);
		
		int x = 0;
		fileToWrite = new File(FILEPATH + "para-" + count + ".txt");
		fileToWrite.delete();
		double baseScore = 0.00;

		while (iter2.hasNext()) {
			sentense = iter2.nextSentence();
			System.out.println("Sentense: " + sentense);
			INDArray a1 = vec.inferVector(sentense);
			for (Iterator<String> iterator = identifiers.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				int qcnt = 0;
				INDArray a2 = vec.inferVector(string);
				double cosineDiff = Transforms.cosineSim(a1, a2);
				System.out.println("compare: " + string + "   Score: " + cosineDiff);
				if ((cosineDiff > 0.80) && (compareSent(sentense.length(), string.length()))) {
					System.out.println(x++ + ". string: " + string + " score: " + cosineDiff);
					if (baseScore == 0.00) {
						count = count + 1;
						// qcnt = qcnt + 1;
					}
					if (cosineDiff > baseScore) {
						baseScore = cosineDiff;
						
						String dir = FileUtil.dirCreation(FILEPATH + "Intents\\Question-" + ++cnt + "\\");
						writeFile(sentense, new File(FileUtil.createFile(dir + "QueTxt-" + ++qcnt + ".txt")));
						writeFile(sentense, new File(FileUtil.createFile(dir + "QueTxt-" + ++qcnt + ".txt")));
						writeFile(sentense, new File(FileUtil.createFile(dir + "QueTxt-" + ++qcnt + ".txt")));
						writeFile(sentense, new File(FileUtil.createFile(dir + "QueTxt-" + ++qcnt + ".txt")));
						sentense = "";
					}

					fileToWrite = new File(FileUtil.createFile(FILEPATH + "Para-" + count + ".txt"));
					// writeFile(paragraph, fileToWrite);
					// paragraph = new StringBuffer("");
					// mp.put(string, cosineDiff);
				}
				/*
				 * if (cosineDiff > score) { score = cosineDiff; }
				 */
			}
			// System.out.println("Score: " + score);
			baseScore = 0.00;
			paragraph.append(sentense);

			if (!sentense.isEmpty()) {
				writeFile(sentense, fileToWrite);
			}
		}

		/*
		 * for (Entry<String, Double> entry : mp.entrySet()) { String key =
		 * entry.getKey(); Object value = entry.getValue(); System.out.println(key +
		 * "   " + value); }
		 */

	}

	private boolean compareSent(int sent1, int sent2) {
		// TODO Auto-generated method stub
		double min1, max1, min2, max2, per1, per2, mean;

		mean = (sent1 + sent2) / 2;

		per1 = (sent1 * 35) / 100;
		per2 = (sent2 * 35) / 100;

		min1 = sent1 - per1;
		max1 = sent1 + per1;

		min2 = sent2 - per2;
		max2 = sent2 + per2;

		if ((mean >= min1 && mean <= max1) && (mean >= min2 && mean <= max2)) {
			return true;
		}
		return false;
	}

	private void writeFile(String textToWrite, File file) throws IOException {
		// TODO Auto-generated method stub
		writer = new FileWriter(file, true);
		writer.write(textToWrite + "\n ");
		writer.close();
	}

	
}
