/**
 * 
 */
package com.fidel.dl4j.pdfutil;

import java.text.BreakIterator;
import java.util.Locale;

/**
 * @author Swapnil
 *
 */
public class BreakSentence {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String paragraph = "Line boundary analysis determines where a text "
				+ "string can be broken when line-wrapping. The " + "mechanism correctly handles punctuation and "
				+ "hyphenated words. Actual line breaking needs to " + "also consider the available line width and is "
				+ "handled by higher-level software. ";

		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);

		int sentences = count(iterator, paragraph);
		System.out.println("Number of sentences: " + sentences);
	}

	private static int count(BreakIterator bi, String source) {
		int counter = 0;
		bi.setText(source);

		int lastIndex = bi.first();
		while (lastIndex != BreakIterator.DONE) {
			int firstIndex = lastIndex;
			lastIndex = bi.next();

			if (lastIndex != BreakIterator.DONE) {
				String sentence = source.substring(firstIndex, lastIndex);
				System.out.println("sentence = " + sentence);
				counter++;
			}
		}
		return counter;
	}
}
