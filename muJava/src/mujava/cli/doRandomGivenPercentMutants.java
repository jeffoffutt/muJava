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

import com.sun.tools.classfile.Opcode.Set;
 /**
 * <p>
 * Description: The Class doRandomGivenPercentMutants. 
 * Overview: This class is used to get random mutants, given a percentage
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0  $Date: 06/10/2014 $
  */

public class doRandomGivenPercentMutants {

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
//		String[] argv = { "/Users/dmark/COVDLsExpCopy/Triangle/result_list_2014_7_22_11_13_35.csv", "100", "10",
//				"/Users/dmark/COVDLsExpCopy/Triangle/result/" };
		String path = args[0];
		double percent = Double.valueOf(args[1])/100.0;
		int numOfRandom = Integer.valueOf(args[2]);
		if (numOfRandom < 1) {
			System.out.println("need more random times");
			return;
		}

		String resultPath = args[3];

		ArrayList<String> targets = new ArrayList<>();
		//targets.add("VDL");

		// read in file into data structure
		HashMap<String, ArrayList<String>> result = readResultsFromFile(path);
		// get all tests names
		ArrayList<String> testNames = result.get("Mutant");


		// randomly get N adequate test set
		ArrayList<ArrayList<String>> randomedTestSet = new ArrayList<>();
		for(int i = 0; i < numOfRandom; i++)
		{
			// need a fltered map based on target
			HashMap<String, ArrayList<String>> filtered_data = getRandomMutants(result, percent);
			
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
		writeResultToFiles(mutationScores, resultPath, Double.toString(percent));

	
	}

	private static void writeResultToFiles(ArrayList<Pair> mutationScores, String resultPath, String percent)
			throws UnsupportedEncodingException, IOException {
		
		resultPath=resultPath+"ResultOfRandomPercent_"+percent+".txt";
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
	 * Gets the filtered data based on random percentage.
	 * 
	 * @param result
	 *            the result
	 * @param percent
	 *            the random percentage
	 * @return the filtered data based on percentage
	 */
	private static HashMap<String, ArrayList<String>> getRandomMutants(
			HashMap<String, ArrayList<String>> result, double percent) {
		HashMap<String, ArrayList<String>> filtered_data = new HashMap<String, ArrayList<String>>();

		// calculate how many mutants needed
		int numOfTotalMutants = result.size() - 1;
		int numOfRandomMutants = (int) (numOfTotalMutants * percent+0.5);
		// put all keys into a list
		List<String> keysList = new ArrayList<String>(result.keySet());
		
		while (filtered_data.size()<numOfRandomMutants) // randomly fill in the data
		{
			SecureRandom secureRandom = new SecureRandom();
			String randomKey = keysList.get(secureRandom.nextInt(keysList.size()));
			if (!randomKey.equals("Mutant") && !filtered_data.keySet().contains(randomKey))
			{// if not the top title line, not already added into result, then add it
				filtered_data.put(randomKey, result.get(randomKey));
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
	 * @param randomedTestSet
	 *            the randomed test set
	 * @param path
	 * @return the mutation scores
	 * @throws IOException
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
	 * Gets the randomed test set.
	 * 
	 * @param adequateTestSet
	 *            the adequate test set
	 * @param numOfRandom
	 *            the num of random
	 * @return the randomed test set
	 */
	private static ArrayList<ArrayList<String>> getRandomedTestSet(CopyOnWriteArrayList<ArrayList<String>> adequateTestSet,
			int numOfRandom) {
		ArrayList<ArrayList<String>> result = new ArrayList<>();

		for (int i = 0; i < numOfRandom; i++) // random N times
		{
			SecureRandom random = new SecureRandom();
			ArrayList<String> randomTest = adequateTestSet.get(random.nextInt(adequateTestSet.size()));
			result.add(randomTest);
		}
		return result;
	}

	/**
	 * Gets the adequate test sets.
	 * 
	 * @param filtered_data
	 *            the filtered_data
	 * @param testNames
	 *            the test names
	 * @return the adequate test sets
	 */

	public static CopyOnWriteArrayList<ArrayList<String>> getAdequateTestSets(HashMap<String, ArrayList<String>> filtered_data,
			ArrayList<String> testNames) {
		CopyOnWriteArrayList<ArrayList<String>> testSets = new CopyOnWriteArrayList<ArrayList<String>>();
		// ArrayList<String> tt = new ArrayList<>();
		// tt.add("a");
		// testSets.add(tt);

		HashMap<String, ArrayList<String>> resultWithTestNames = new HashMap<>();
		HashMap<String, Integer> resultStat = new HashMap<>();

		for (Object key : filtered_data.keySet()) {
			ArrayList<String> temp = filtered_data.get(key);
			ArrayList<String> testStrings = new ArrayList<>();
			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).equals("1")&& !testNames.get(i).equals("Total")) // current test kills the current
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

		// filtered_data has y, need to count y for equiv
		for (Object key : filtered_data.keySet()) {
			if (filtered_data.get(key).contains("y") || filtered_data.get(key).contains("Y")) // has
																								// an
																								// equiv
			{
				numOfEq++;
			}
		}
		// for (Object key : resultWithTestNames.keySet()) {
		// if(resultWithTestNames.get(key).size()==0)
		// numOfEq ++;
		// }

		int non_equiv = numOfTotalMutants - numOfEq;
		// for (Object key : resultStat.keySet()) {
		// if (resultStat.get(key)==non_equiv) // find a single test can kill
		// all
		// {
		// ArrayList<String> t = new ArrayList<>();
		// t.add(key.toString());
		// testSets.add(t);
		// }
		// }
		// testSets= new ArrayList<ArrayList<String>>();
		for (Object key : resultWithTestNames.keySet()) {
			ArrayList<String> line = resultWithTestNames.get(key);
			if (line.size() == 0) // equiv mutant, go next one
				continue;
			if (testSets.size() == 0) { // first time, initiate new sets

				for (String str : line) {
					ArrayList<String> t = new ArrayList<>();
					t.add(str);
					testSets.add(t);
					System.out.println("add test "+t);
				}

			} else { // not the first time, need to extend everyone, and add
						// back
				// load last line's tests
				ArrayList<ArrayList<String>> temp = new ArrayList<>();
				for (ArrayList<String> tests : testSets)
					temp.add(tests);
				// empty the result set
				// ArrayList<ArrayList<String>> tempTestSets = new
				// ArrayList<>();
				testSets = new CopyOnWriteArrayList<>();
				// add and save back
				for (String str : line) {
					for (ArrayList<String> tests : temp) {
						ArrayList<String> t = new ArrayList<>();
						t.addAll(tests);
						t.add(str);
						testSets.add(t);
						System.out.println("add test "+t);
					}
				}

				// testSets = new ArrayList<>();
				// testSets.addAll(tempTestSets);
			}

		}

		testSets = removeDuplicate(testSets);
		testSets = getMinimalTests(testSets);
		// System.out.println(non_equiv);
		return testSets;
	}

	/**
	 * Gets the minimal tests.
	 * 
	 * @param testSets
	 *            the test sets
	 * @return the minimal tests
	 */
	private static CopyOnWriteArrayList<ArrayList<String>> getMinimalTests(CopyOnWriteArrayList<ArrayList<String>> testSets) {
		CopyOnWriteArrayList<ArrayList<String>> result = new CopyOnWriteArrayList<>();
		List<ArrayList<String>> testSets2 = new CopyOnWriteArrayList<ArrayList<String>>();
		List<ArrayList<String>> testSets3 = new CopyOnWriteArrayList<ArrayList<String>>();
		testSets2.addAll(testSets);
		testSets3.addAll(testSets);
		for (ArrayList<String> test : testSets3) {
			for (ArrayList<String> test2 : testSets2) {
				if (test.containsAll(test2) && !test2.containsAll(test)) {
					testSets.remove(test);
					testSets2.remove(test);
					testSets3.remove(test);
					System.out.println("remove test "+test);
					break;
				}
			}
		}
		result.addAll(testSets);
		return result;
	}

	/**
	 * Removes the duplicate.
	 * 
	 * @param testSets
	 *            the test sets
	 * @return the array list
	 */
	private static CopyOnWriteArrayList<ArrayList<String>> removeDuplicate(CopyOnWriteArrayList<ArrayList<String>> testSets) {
		ArrayList<ArrayList<String>> testSetNoDupInEachTest = new ArrayList<>();
		CopyOnWriteArrayList<ArrayList<String>> result = new CopyOnWriteArrayList<>();
		// remove dup in each test
		for (ArrayList<String> test : testSets) {
			HashSet<String> hs = new HashSet<>();
			hs.addAll(test);
			test.clear();
			test.addAll(hs);
			testSetNoDupInEachTest.add(test);
		}
		// remove dup tests
		for (ArrayList<String> test : testSetNoDupInEachTest)
		{
			if (!result.contains(test))
				result.add(test);
		}

		return result;
	}
	
	
	
	public static CopyOnWriteArrayList<ArrayList<String>> getAdequateTestSets2(HashMap<String, ArrayList<String>> filtered_data,
			ArrayList<String> testNames) {
		CopyOnWriteArrayList<ArrayList<String>> testSets = new CopyOnWriteArrayList<ArrayList<String>>();
		CopyOnWriteArrayList<String> mutantStrings = new CopyOnWriteArrayList<>();
		// ArrayList<String> tt = new ArrayList<>();
		// tt.add("a");
		// testSets.add(tt);

		HashMap<String, ArrayList<String>> resultWithTestNames = new HashMap<>();
		HashMap<String, Integer> resultStat = new HashMap<>();

		for (Object key : filtered_data.keySet()) {
			mutantStrings.add(key.toString());
			ArrayList<String> temp = filtered_data.get(key);
			ArrayList<String> testStrings = new ArrayList<>();
			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).equals("1")&& !testNames.get(i).equals("Total")) // current test kills the current
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
		
		// filtered_data has y, need to count y for equiv
		for (Object key : filtered_data.keySet()) {
			if (filtered_data.get(key).contains("y") || filtered_data.get(key).contains("Y")) // has
																								// an
																								// equiv
			{
				numOfEq++;
			}
			if (filtered_data.get(key).contains("n") || filtered_data.get(key).contains("N") || filtered_data.get(key).contains("?")) // there is a mutant that can't be killed, but not equiv
			{
				nonEquivNonKilled.add(key.toString());
			}
		}
		// remove those can't be killed
		mutantStrings.removeAll(nonEquivNonKilled);
		
		int non_equiv = numOfTotalMutants - numOfEq;

		testNames.remove("Total");
		testNames.remove("Equiv?");
		
		// convert testNames into an array
		String [] stockArr = testNames.toArray(new String[testNames.size()]);
		
		// get all possible test combinations
        int all = stockArr.length;
        int nbit = 1 << all;
        for (int i = 0; i < nbit; i++) {
            ArrayList<String> test = new ArrayList<>();
            for (int j = 0; j < all; j++) {
                if ((i & (1 << j)) != 0) {
                    test.add(stockArr[j]);
                }
            }
            if (test.size()!=0)
            	{testSets.add(test);
            	System.out.println("add test: "+test);}
        }
		
        // remove non-adequate tests
        for (ArrayList<String> tests : testSets) // for each test set
        {
        	ArrayList<String> liveMutants = new ArrayList<>(mutantStrings);
        	for (String test : tests) // for each single test
        	{
        		for (String mutant : mutantStrings) // for each single mutant
            	{
            		if (resultWithTestNames.get(mutant).contains(test)) // if current test kills current mutant
            		{
            			liveMutants.remove(mutant); // the current is killed, remove from live
            		}
            	}
        	}
        	if (liveMutants.size() > numOfEq)  // have more live mutants left than equiv mutants
        	{
        		testSets.remove(tests); // not adequate tests, remove it from test Sets
        		System.out.println("remove test: "+tests);
        	}
        	
        }

		testSets = removeDuplicate(testSets);
		testSets = getMinimalTests(testSets);
		// System.out.println(non_equiv);
		return testSets;
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
