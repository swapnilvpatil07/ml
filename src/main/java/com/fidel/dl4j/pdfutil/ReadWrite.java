package com.fidel.dl4j.pdfutil;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class ReadWrite {

	public static void main(String[] args) {
		pdfBox();
		// iTextReadWrite();
	}

	private static void iTextReadWrite() {
		// TODO Auto-generated method stub
		PdfReader reader;
		try {
			reader = new PdfReader(fileChose());
			// pageNumber = 1
			String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);
			System.out.println(textFromPage);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void pdfBox() {
		// TODO Auto-generated method stub
		PDDocument pd;
		BufferedWriter wr;
		int pages;
		try {
			File input = new File(fileChose()); // The PDF file from where you would like to extract
			File output = new File("D:\\SampleText.txt"); // The text file where you are going to store the extracted
															// data

			pd = PDDocument.load(input);
			pages = pd.getNumberOfPages();

			System.out.println("Pages: " + pd.getNumberOfPages());
			System.out.println("Encrypted: " + pd.isEncrypted());
			System.out.println("Extract: " + pd.getCurrentAccessPermission().canExtractContent());
			System.out.println("OwnerPermission: " + pd.getCurrentAccessPermission().isOwnerPermission());
			if (!pd.isEncrypted()) {
				pd.save("CopyOfInvoice.pdf"); // Creates a copy called "CopyOfInvoice.pdf"

				PDFTextStripper stripper = new PDFTextStripper();
				
				PDFRenderer pdfRenderer = new PDFRenderer(pd);
				BufferedImage image = pdfRenderer.renderImage(0);

			      //Writing the image to a file
			      ImageIO.write(image, "JPEG", new File("D:\\myimage.jpg"));
			      
				System.out.println(stripper.getText(pd));
				wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
				stripper.writeText(pd, wr);

				// createPdf(pages);
				// writeToPdf();

				if (pd != null) {
					pd.close();
				}
				wr.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createPdf(int pages) throws IOException {
		// TODO Auto-generated method stub
		PDDocument document = new PDDocument();
		for (int i = 1; i <= pages; i++) {
			// Creating a blank page
			PDPage blankPage = new PDPage();

			// Adding the blank page to the document
			document.addPage(blankPage);
		}
		/*
		 * for (int i = 0; i < pages; i++) { PDPage page = document.getPage(i);
		 * contentStream = new PDPageContentStream(document, page); }
		 */

		// Saving the document
		document.save("D:\\Swapnil\\AI Insurance\\Docs\\my_doc.pdf");
		System.out.println("PDF created");
		// Closing the document
		document.close();
	}

	private static void writeToPdf() {
		// TODO Auto-generated method stub

	}

	public static String fileChose() {
		JFileChooser fc = new JFileChooser();
		int ret = fc.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String filename = file.getAbsolutePath();
			return filename;
		} else {
			return null;
		}
	}
}