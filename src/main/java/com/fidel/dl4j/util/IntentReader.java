package com.fidel.dl4j.util;

import java.io.File;
import java.io.IOException;

import com.itextpdf.text.log.SysoCounter;

public class IntentReader {

	private String FILEPATH = "src/main/resources/Training-Data-Set/Intents/";
	private String RFILEPATH = "src/main/resources/Training-Data-Set/Response/";

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String name = "src/main/resources/row_data.txt";
		IntentReader intentReader = new IntentReader();

		intentReader.readIntents(name);
	}

	public void readIntents(String intentFile) throws IOException {
		// TODO Auto-generated method stub
		boolean flag = false;

		if (!(new File(FILEPATH).exists())) {
			FileUtil.dirCreation(FILEPATH);
		}
		String[] fileContent = FileUtil.readFile(new File(intentFile)).replaceAll("\\]", "").split("\\[");
		String intentDir = "";
		String intent = "";

		for (String str : fileContent) {
			String[] fields = str.trim().split("\n\n");
			String txt = "";
			for (String string : fields) {
				String section[] = string.split("\n");

				for (int i = 0; i < section.length; i++) {
					txt = section[i].trim().toLowerCase().replaceFirst("\\:", "");

					if (txt.equalsIgnoreCase("intent:") || txt.equals("intent")) {
						File file = new File(FILEPATH + section[i + 1].trim());
						intent = section[i + 1].trim();
						intentDir = file.getAbsolutePath();
						FileUtil.dirCreation(intentDir);
					}
					if (txt.equalsIgnoreCase("questions:") || txt.equals("questions")) {
						for (int j = i + 1; j < section.length; j++) {
							String path = intentDir + "/txt-";
							// FileUtil.createFile(path);
							//System.out.println(section.length + intentDir);
							if (section.length < 3) {
								for (int k = 0; k < 10; k++) {
									FileUtil.writeFile(section[j].trim(), new File(path + (k + 1)));
								}
							}else if (section.length < 7) {
								for (int k = 0; k < 4; k++) {
									FileUtil.writeFile(section[j].trim(), new File(path + (k + 1)));
								}
							}else {
								FileUtil.writeFile(section[j].trim(), new File(path + (j)));
							}
							
							FileUtil.writeFile(section[j].trim(), new File("src/main/resources/Training-Data-Set/test-questions.txt"));
						}
					}
					if (txt.equalsIgnoreCase("response:") || txt.equals("response")) {
						String path = RFILEPATH + intent;
						FileUtil.dirCreation(RFILEPATH);
						FileUtil.writeFile(section[i + 1], new File(path + ".txt"));
					}
				}
			}
		}
		System.out.println("Done");
	}
}
