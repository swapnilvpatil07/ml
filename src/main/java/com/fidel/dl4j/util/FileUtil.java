package com.fidel.dl4j.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

	private static BufferedReader bufferedReader;
	private static FileWriter writer;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static String createFile(String path) throws IOException {
		// TODO Auto-generated method stub
		File f = new File(path);
		if (!f.exists()) {
			f.createNewFile();
			System.out.println("File Created...");
		} else {
			f.delete();
			f.createNewFile();
			System.out.println("File deleted & Created...");
		}
		return f.getAbsolutePath().toString();
	}
	
	public static String readFile(File fileToRead) throws IOException {
		// TODO Auto-generated method stub
		String currLine;
		StringBuffer sb = new StringBuffer();

		bufferedReader = new BufferedReader(new FileReader(fileToRead));
		while ((currLine = bufferedReader.readLine()) != null) {
			sb.append(currLine + "\n");
			// System.out.println(currLine);
		}
		return sb.toString();
	}
	
	public static void writeFile(String textToWrite, File fileName) throws IOException {
		// TODO Auto-generated method stub
		writer = new FileWriter(fileName, true);
		writer.write(textToWrite + "\n");
		writer.close();
	}
	
	public static String dirCreation(String Name) {
		// TODO Auto-generated method stub
		File f = new File(Name);
		if (!f.exists()) {
			if (f.mkdirs())
				System.out.println("Directory Created...");
			else
				System.out.println("Directory creation failed...");
		}
		return Name;
	}
}
