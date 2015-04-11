/**
 * Copyright (C) 2015  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 




package mujava.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.beust.jcommander.JCommander;

import mujava.MutationSystem;
import mujava.test.TestResultCLI;
 /**
 * <p>
 * Description: Mark equivalent mutants API for command line version
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0  
  */
public class markequiv {
	
	static String muJavaHomePath = new String();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		markequivCom jct = new markequivCom();
		String[] argv = { "Triangle", "AOIS_18", "Triangle" }; // dev use

		JCommander jCommander = new JCommander(jct, args);

		if (jct.getParameters().size() < 3) {
			Util.Error("At least 3 arguments required: \"class-name\" \"mutant-name\" \"session-name\"");
			return;
		}

		String targetClassName = jct.getParameters().get(0);

		// if(jct.getName()!=null)
		// targetClassName = jct.getName();
		// else {
		// return;
		// }

		List<String> parameters = jct.getParameters();
		muJavaHomePath = Util.loadConfig();
		String session = parameters.get(parameters.size() - 1);
		parameters.remove(parameters.size() - 1);
		ArrayList<String> eqMutants = new ArrayList<>();
		eqMutants.addAll(parameters);

		// get all file names
		File folder = new File(muJavaHomePath + "/" + session + "/result" + "/" + targetClassName + "/"
				+ MutationSystem.TM_DIR_NAME);
		File[] listOfFiles = folder.listFiles();

		// run one by one update
		if (listOfFiles == null) {
			Util.Error("Can't find result folder of class: " + targetClassName);
			return;
		}
		for (File file : listOfFiles) {
			if (file.getName().contains("mutant_list")) {
				TestResultCLI tr = new TestResultCLI();

				tr.path = muJavaHomePath + "/" + session + "/result" + "/" + targetClassName + "/"
						+ MutationSystem.TM_DIR_NAME + "/" + file.getName();
				tr.getResults();

				for (String eqMutant : eqMutants) {
					if (!tr.live_mutants.contains(eqMutant))
						continue;

					// erase and update
					tr.live_mutants.remove(eqMutant);
					tr.eq_mutants.add(eqMutant);
				}

				// tr.markEqvl(eqMutant);

				markMutantListFile(tr, file);

			} else if (file.getName().contains("result_list")) { // also need to
																	// mark
																	// result_list.csv
			// TestResultCLI tr = new TestResultCLI();
			// tr.path = MutationSystem.SYSTEM_HOME + "/"
			// + session +
			// "/result"+"/"+targetClassName+"/"+MutationSystem.TM_DIR_NAME+"\\"+file.getName();
			// tr.getResults();

				markResultListFile(eqMutants, file);
			}
		}

		Util.Print("All equivalent mutants are marked.");
		//System.exit(0);

	}

	private static void markResultListFile(ArrayList<String> eqMutants, File file) throws IOException {
		// read csv file
		Map<String, ArrayList<String>> oldResults = new HashMap<String, ArrayList<String>>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String s = new String();
		while ((s = br.readLine()) != null) // read lines
		{
			String[] temp = s.split(",");
			ArrayList<String> tempList = new ArrayList<>();
			for (int i = 1; i < temp.length; i++) {
				tempList.add(temp[i]);
			}
			oldResults.put(temp[0], tempList);
		}

		// mark eq
		for (String eqMutant : eqMutants) {
			ArrayList<String> newResult = oldResults.get(eqMutant);
			if (newResult == null) {
				continue;
			}
			if (!newResult.get(newResult.size() - 2).equals("0")) {
				continue;
			}
			newResult.set(newResult.size() - 1, "Y");
			oldResults.put(eqMutant, newResult);
		}

		// write file
		String path = file.getPath();

		file.delete();

		file = new File(path);
		FileOutputStream fout = new FileOutputStream(file);
		StringBuffer fileContent = new StringBuffer();

		// build title
		fileContent.append("Mutant");
		for (String test : oldResults.get("Mutant"))
			fileContent.append("," + test);

		fileContent.append("\r\n");
		// build content
		for (Entry<String, ArrayList<String>> oldEntry : oldResults.entrySet()) {
			if (oldEntry.getKey().equals("Mutant"))
				continue;
			fileContent.append(oldEntry.getKey());
			for (String str : oldEntry.getValue()) {
				fileContent.append("," + str);
			}

			fileContent.append("\r\n");
		}

		fout.write(fileContent.toString().getBytes("utf-8"));
		fout.close();

	}

	private static void markMutantListFile(TestResultCLI tr, File file) throws UnsupportedEncodingException, IOException {
		// delete original file
		file.delete();

		// build new file
		File newFile = new File(tr.getPath());

		FileOutputStream fout = new FileOutputStream(newFile);
		StringBuffer fileContent = new StringBuffer();

		fileContent.append("killed mutants (" + tr.killed_mutants.size() + "): ");
		for (Object object : tr.killed_mutants) {
			fileContent.append(object.toString() + ", ");
		}
		fileContent.append("\r\n");

		fileContent.append("live mutants (" + tr.live_mutants.size() + "): ");
		for (Object object : tr.live_mutants) {
			fileContent.append(object.toString() + ", ");
		}
		fileContent.append("\r\n");

		fileContent.append("equivalent mutants (" + tr.eq_mutants.size() + "): ");
		for (Object object : tr.eq_mutants) {
			fileContent.append(object.toString() + ", ");
		}

		fout.write(fileContent.toString().getBytes("utf-8"));
		fout.close();

	}

}
