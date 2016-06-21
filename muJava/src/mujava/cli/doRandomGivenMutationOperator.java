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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
 /**
 * <p>
 * Description: The Class doRandomGivenMutationOperator. 
 * Overview: This class is used to calculate mutation scores
 * based on randomly generated adequate test set of given mutation operators.
 * Experiment use only.
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0  $Date: 06/10/2014 $
  */ 

public class doRandomGivenMutationOperator {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		// need result file name
//		String[] argv = { "/Users/dmark/COVDLsExpCopy/Triangle/result_list_2014_7_22_11_13_35.csv", "twoPred", "CDL", "10",
//				"/Users/dmark/COVDLsExpCopy/Triangle/" };
		String path = args[0];
		String target = args[2];

		int numOfRandom = Integer.valueOf(args[3]);
		if (numOfRandom < 1) {
			System.out.println("need more random times");
			return;
		}

		String resultPath = args[4];

		ArrayList<String> targets = new ArrayList<>();
		
		// used for getting results
		// if no target is selected, randomly take one test
		
//		targets.add("SDL");
//		targets.add("ODL");
//		targets.add("VDL");
//		targets.add("CDL");
//		targets.add("SDL");targets.add("ODL");
//		targets.add("SDL");targets.add("VDL");
//		targets.add("SDL");targets.add("CDL");
//		targets.add("VDL");targets.add("CDL");
//		targets.add("SDL");targets.add("VDL");targets.add("CDL");
		
		
		
		// read in file into data structure
		HashMap<String, ArrayList<String>> result = readResultsFromFile(path);
		// get all tests names
		ArrayList<String> testNames = result.get("Mutant");

		// need a filtered map based on target
		HashMap<String, ArrayList<String>> filtered_data = getFilteredDataBasedOnTargets(result, targets);

		// get the adequate test set
//		CopyOnWriteArrayList<ArrayList<String>> adequateTestSet = getAdequateTestSets4(filtered_data, testNames);
		// randomly get N adequate test set
		ArrayList<ArrayList<String>> randomedTestSet = new ArrayList<>();
		for(int i = 0; i < numOfRandom; i++)
		{
			randomedTestSet.add(getAdequateTestSets4(filtered_data, testNames));
		}
		
		
		// calculate each one with mutation score
		ArrayList<Pair> mutationScores = getMutationScores(randomedTestSet, path);
		double totalMS = 0.0;
		for (Pair ms : mutationScores)
		{
			System.out.println(ms.testSet + ": " + ms.mutationScore);
			totalMS=totalMS+ms.mutationScore;
		}
		double avgMS = totalMS / mutationScores.size();
		System.out.println("avg: " + avgMS);
		
		// write files
		writeResultToFiles(mutationScores, resultPath, targets);

	
	}

	/**
	 * Write result to files.
	 *
	 * @param mutationScores the mutation scores
	 * @param resultPath the result path
	 * @param targets the targets
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void writeResultToFiles(ArrayList<Pair> mutationScores, String resultPath, ArrayList<String> targets)
			throws UnsupportedEncodingException, IOException {
		
		resultPath=resultPath+"ResultOfRandomAdequateTests_"+targets+".txt";
		// set the output file
		File file = new File(resultPath);

		FileOutputStream fout = new FileOutputStream(file);
		StringBuffer fileContent = new StringBuffer();

		double totalMS = 0.0;
		for (Pair pair : mutationScores) {
			totalMS = totalMS + pair.mutationScore;
			fileContent.append(pair.testSet + ": " + pair.mutationScore);
			fileContent.append("\r\n");
		}
		double avgMS = totalMS / mutationScores.size();
		fileContent.append("Avg: " + avgMS);

		fout.write(fileContent.toString().getBytes("utf-8"));
		fout.close();

	}

	/**
	 * Gets the filtered data based on targets.
	 * 
	 * @param result
	 *            the result
	 * @param targets
	 *            the targets
	 * @return the filtered data based on targets
	 */
	private static HashMap<String, ArrayList<String>> getFilteredDataBasedOnTargets(
			HashMap<String, ArrayList<String>> result, ArrayList<String> targets) {
		HashMap<String, ArrayList<String>> filtered_data = new HashMap<String, ArrayList<String>>();

		for (String target : targets) {
			for (Object key : result.keySet()) {
				if (key.toString().contains(target))
					filtered_data.put(key.toString(), result.get(key));

			}
		}
		return filtered_data;
	}

	/**
	 * Read results from file. Note: This result include the fist title line.
	 * e.g. {VDL_3=[, 1, 1, 1, 2], Mutant=[test4, test1, test2, test3, Total,
	 * Equiv?], VDL_4=[1, 1, 1, , 3, y], VDL_5=[1, , 1, , 2]}
	 * 
	 * @param path
	 *            the path
	 * @return the hash map
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static HashMap<String, ArrayList<String>> readResultsFromFile(String path) throws IOException {
		// read the file
		File f = new File(path);
		if (!f.exists()) {
			System.out.println("Can't find file at: " + path);
			return null;
		}
		// read in file into data structure
		HashMap<String, ArrayList<String>> result = new HashMap<>();

		String s = null;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		while ((s = br.readLine()) != null) {
			// split line
			List<String> tests = Arrays.asList(s.split(",\\s*"));
			// System.out.println(tests.get(1));
			ArrayList<String> list = new ArrayList<>();
			for (int i = 1; i < tests.size(); i++) {
				list.add(tests.get(i));
			}
			result.put(tests.get(0), list);
		}

		return result;
	}

	/**
	 * Gets the mutation scores.
	 *
	 * @param randomedTestSet            the randomed test set
	 * @param path the path
	 * @return the mutation scores
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static ArrayList<Pair> getMutationScores(ArrayList<ArrayList<String>> randomedTestSet, String path)
			throws IOException {

		// read in file into data structure, this result includes title line
		HashMap<String, ArrayList<String>> result = readResultsFromFile(path);

		// get all tests names
		ArrayList<String> testNames = result.get("Mutant");

		// build result with names, i.e. VDL_3=[test1, test2, test3],
		// VDL_4=[test4, test1, test2], VDL_5=[test4, test2]
		HashMap<String, ArrayList<String>> resultWithTestNames = getResultsWithNames(result);

		// calculate
		// get total number
		int totalNumOfMutants = resultWithTestNames.size();

		// get equiv number
		int numOfEq = 0;
		for (Object key : result.keySet()) {
			if (result.get(key).contains("y") || result.get(key).contains("Y")) // has
																				// an
																				// equiv
			{
				numOfEq++;
			}
		}

		ArrayList<Pair> mutationScores = new ArrayList<>();
		// get killed number
		// for each test test set, calculate how many killed mutants
		for (ArrayList<String> tests : randomedTestSet) {
			int numOfKilledMutants = 0;
			for (Object key : resultWithTestNames.keySet()) // for each mutant
			{
				for (String test : tests) // for each test in current test set
				{
					if (resultWithTestNames.get(key).contains(test)) // current
																		// test
																		// kills
																		// current
																		// mutant
					{
						numOfKilledMutants++;
						break; // directly go to the next mutant
					}
				}
			}

			// save mutation score
			double ms = (double)numOfKilledMutants / (double)(totalNumOfMutants - numOfEq);
			mutationScores.add(new Pair(tests, ms));
		}

		return mutationScores;
	}

	/**
	 * Gets the results with names.
	 *
	 * @param result the result
	 * @return the results with names
	 */
	private static HashMap<String, ArrayList<String>> getResultsWithNames(HashMap<String, ArrayList<String>> result) {
		// get all tests names
		ArrayList<String> testNames = result.get("Mutant");

		// build result with names
		HashMap<String, ArrayList<String>> resultWithTestNames = new HashMap<>();
		HashMap<String, Integer> resultStat = new HashMap<>();

		for (Object key : result.keySet()) {
			ArrayList<String> temp = result.get(key);
			ArrayList<String> testStrings = new ArrayList<>();
			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).equals("1") && !testNames.get(i).equals("Total")) // current test kills the current
												// mutant
				{
					testStrings.add(testNames.get(i));
					if (resultStat.containsKey(testNames.get(i)))
						resultStat.put(testNames.get(i), resultStat.get(testNames.get(i)) + 1);
					else {
						resultStat.put(testNames.get(i), 1);
					}
				}
			}
			if (!key.toString().equals("Mutant"))
				resultWithTestNames.put(key.toString(), testStrings);
		}
		return resultWithTestNames;
	}


	

	
	/**
	 * Gets the adequate test sets.
	 *
	 * @param filtered_data the filtered_data
	 * @param testNamesCopy the test names copy
	 * @return the adequate test sets4
	 */
	public static ArrayList<String> getAdequateTestSets4 (HashMap<String, ArrayList<String>> filtered_data,
			ArrayList<String> testNamesCopy) {
		
		CopyOnWriteArrayList<ArrayList<String>> testSets = new CopyOnWriteArrayList<ArrayList<String>>();
		CopyOnWriteArrayList<String> mutantStrings = new CopyOnWriteArrayList<>();
		HashMap<String, ArrayList<String>> resultWithTestNames = new HashMap<>();
		HashMap<String, Integer> resultStat = new HashMap<>();
		
		ArrayList<String> testNames = new ArrayList<>(testNamesCopy);
		
		// get all mutants name in filtered data
		for (Object key : filtered_data.keySet()) {
			mutantStrings.add(key.toString());
			ArrayList<String> temp = filtered_data.get(key);
			ArrayList<String> testStrings = new ArrayList<>();
			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).equals("1") && !testNames.get(i).equals("Total")) // current test kills the current
												// mutant
				{
					testStrings.add(testNames.get(i));
 
					if (resultStat.containsKey(testNames.get(i)))
						resultStat.put(testNames.get(i), resultStat.get(testNames.get(i)) + 1);
					else {
						resultStat.put(testNames.get(i), 1);
					}
				}
			}
			resultWithTestNames.put(key.toString(), testStrings);
		}
		
		// check if there is an equivalent
		int numOfEq = 0;
		int numOfTotalMutants = filtered_data.size();

		ArrayList<String> nonEquivNonKilled = new ArrayList<>();
		
		// remove those weren't be killed
		for (Object key : filtered_data.keySet()) {
			if (filtered_data.get(key).contains("y") || 
					filtered_data.get(key).contains("Y") ||
					filtered_data.get(key).contains("n") || 
					filtered_data.get(key).contains("N")|| 
					filtered_data.get(key).contains("?")
					)  
			{
				mutantStrings.remove(key.toString());
			}
		}
		
 
		testNames.remove("Total");
		testNames.remove("Equiv?");
		
		ArrayList<String> result = new ArrayList<>();
		int[] mutants = new int[mutantStrings.size()];
		// for each mutant, randomly find a test that kills it
		for(int i =0; i<mutantStrings.size();i++)
		{
			if(mutants[i]==1)
				continue;
			// get all tests that kill it
			ArrayList<String> testsKillCurrentMutant = resultWithTestNames.get(mutantStrings.get(i));
				
			// randomly choose one
			SecureRandom random = new SecureRandom();
			String randomTest = testsKillCurrentMutant.get(random.nextInt(testsKillCurrentMutant.size()));
			result.add(randomTest);
			
			// remove this mutant by marking the array to 1
			mutants[i] = 1;       
			
			// remove other mutants that killed by the same test
			for (int j =0; j<mutantStrings.size();j++)
			{
				if(mutants[j]==1)
					continue;
				if(resultWithTestNames.get(mutantStrings.get(j)).contains(randomTest))
					// the test selected killed this m
				{
					mutants[j] = 1;    
				}
			}
			
		}
//	System.out.println(result);
	return result;
	}
}
