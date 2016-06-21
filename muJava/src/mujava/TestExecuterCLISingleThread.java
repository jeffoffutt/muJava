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
 

package mujava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import mujava.cli.Util;
import mujava.cli.runmutes;
import mujava.test.JMutationLoader;
import mujava.test.NoMutantDirException;
import mujava.test.NoMutantException;
import mujava.test.TestResultCLI;
import mujava.util.Debug;

import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
/**
 * 
 * <p>
 * Description: New test executer class build exclusively for command line version
 * try single thread execution.
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0
 * 
 */
public class TestExecuterCLISingleThread extends TestExecuterCLI  {

	public TestExecuterCLISingleThread(String targetClassName) {
		super(targetClassName);
		
	}

	
	private TestResultCLI runMutants(TestResultCLI tr, String methodSignature, String[] mutantTypes, double percentage)
			throws NoMutantException, NoMutantDirException {
		try {
			
			System.out.println("rnning in single mode!!!!!!!!!!!!");

			String[] mutantDirectories = getMutants(methodSignature);
			// Lin adds: only run certain type
			int mutant_num = mutantDirectories.length;
			tr.setMutants();
			
			for (String mutantType : mutantTypes) {

				for (int i = 0; i < mutant_num; i++) {
					// set live mutants
					if (mutantDirectories[i].contains(mutantType)) {
						tr.mutants.add(mutantDirectories[i]);
					}

				}
			}

			Util.Print("\nCurrent running mode: " + runmutes.mode);

			// Lin adds: eliminate extra mutants based on random percentage
			if (percentage != 1) {
				int rondomSize = (int) (tr.mutants.size() * percentage);
				Random rand = new Random(System.currentTimeMillis());
				Vector tempMutantVector = new Vector(rondomSize);
				for (int i = 0; i < rondomSize; i++) {
					// be sure to use Vector.remove() or you may get the same
					// item
					// twice
					tempMutantVector.add(tr.mutants.remove(rand.nextInt(tr.mutants.size())));
				}
				tr.mutants = tempMutantVector;
			}

			// result againg original class for each test case
			// Object[] original_results = new Object[testCases.length];
			// list of the names of killed mutants with each test case
			// String[] killed_mutants = new String[testCases.length];

			Debug.println("\n\n======================================== Executing Mutants ========================================");
			for (int i = 0; i < tr.mutants.size(); i++) {
				// read the information for the "i"th live mutant
				String mutant_name = tr.mutants.get(i).toString();
				finalMutantResults.put(mutant_name, "");
				JMutationLoader mutantLoader = new JMutationLoader(mutant_name);
				// mutantLoader.loadMutant();
				mutant_executer = mutantLoader.loadTestClass(testSet);
				mutant_obj = mutant_executer.newInstance();
				Debug.print("  " + mutant_name);

				try {
					// Mutants are runned using Thread to detect infinite loop
					// caused by mutation
//					Runnable r = new Runnable() {
//						public void run() {
							try {
								mutantRunning = true;

								// original test results
								mutantResults = new HashMap<String, String>();
								for (int k = 0; k < testCases.length; k++) {
									Annotation[] annotations = testCases[k].getDeclaredAnnotations();
									for (Annotation annotation : annotations) {
										// System.out.println("name: " +
										// testCases[k].getName() +
										// annotation.toString() +
										// annotation.toString().indexOf("@org.junit.Test"));
										if (annotation.toString().indexOf("@org.junit.Test") != -1) {
											// killed_mutants[k]= ""; // At
											// first, no mutants are killed by
											// each test case
											mutantResults.put(testCases[k].getName(), "pass");
											continue;
										}
									}
								}
								
							//	System.out.println("start" );
								
								JUnitCore jCore = new JUnitCore();
								result = jCore.run(mutant_executer);
								
							//	System.out.println("end");
								
								List<Failure> listOfFailure = result.getFailures();
								for (Failure failure : listOfFailure) {
									String nameOfTest = failure.getTestHeader().substring(0,
											failure.getTestHeader().indexOf("("));
									String testSourceName = testSet + "." + nameOfTest;

									// System.out.println(testSourceName);
									String[] sb = failure.getTrace().split("\\n");
									String lineNumber = "";
									for (int j = 0; j < sb.length; j++) {
										// System.out.println("sb-trace: " +
										// sb[i]);
										if (sb[j].indexOf(testSourceName) != -1) {
											lineNumber = sb[j].substring(sb[j].indexOf(":") + 1, sb[j].indexOf(")"));

										}
									}
									// get the line where the error happens
									/*
									 * String tempLineNumber = "";
									 * if(failure.getTrace
									 * ().indexOf(testSourceName) != -1){
									 * tempLineNumber =
									 * failure.getTrace().substring
									 * (failure.getTrace
									 * ().indexOf(testSourceName) +
									 * testSourceName.length() + 1,
									 * failure.getTrace
									 * ().indexOf(testSourceName) +
									 * testSourceName.length() + 5);
									 * System.out.println("tempLineNumber: " +
									 * tempLineNumber); lineNumber =
									 * tempLineNumber.substring(0,
									 * tempLineNumber.indexOf(")"));
									 * //System.out.print("LineNumber: " +
									 * lineNumber); }
									 */
									// get the test name that has the error and
									// save the failure info to the results for
									// mutants
									if (failure.getMessage() == null)
										mutantResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + "fail");
									else if (failure.getMessage().equals(""))
										mutantResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + "fail");
									else
										mutantResults.put(nameOfTest,
												nameOfTest + ": " + lineNumber + "; " + failure.getMessage());
								}
								System.out.print(".");
								Util.DebugPrint(mutantResults.toString());
								mutantRunning = false;
								synchronized (lockObject) {
									lockObject.notify();
								}

							} catch (Exception e) {
								e.printStackTrace();
								// System.out.println("e.getMessage()");
								// System.out.println(e.getMessage());
							}
//						}
//					};

//					Thread t = new Thread(r);
//					t.setDaemon(true);
//					t.start();
//
//					synchronized (lockObject) {
//						lockObject.wait(TIMEOUT); // Check out if a mutant is in
//													// infinite loop
//					}
//					if (mutantRunning) {
//						 // System.out.println("check point4");
//						//System.out.println(t.getName());
//						
//						 t.interrupt();
//						 
//						//mutantRunning=false;
//						// mutant_result = "time_out: more than " + TIMEOUT +
//						// " seconds";
//						Util.DebugPrint(" time_out: more than " + TIMEOUT + " milliseconds");
//						// mutantResults.put(nameOfTest, nameOfTest + ": " +
//						// lineNumber + "; " + failure.getMessage());
//
//						for (int k = 0; k < testCases.length; k++) {
//							Annotation[] annotations = testCases[k].getDeclaredAnnotations();
//							for (Annotation annotation : annotations) {
//								// System.out.println("name: " +
//								// testCases[k].getName() +
//								// annotation.toString() +
//								// annotation.toString().indexOf("@org.junit.Test"));
//								if (annotation.toString().indexOf("@org.junit.Test") != -1) {
//									// killed_mutants[k]= ""; // At first, no
//									// mutants are killed by each test case
//									mutantResults.put(testCases[k].getName(), "time_out: more than " + TIMEOUT
//											+ " milliseconds");
//									continue;
//								}
//							}
//						}
//
//					}

//					if(mutantRunning)
//						t.stop();
					
				} catch (Exception e) {
					mutant_result = e.getCause().getClass().getName() + " : " + e.getCause().getMessage();
				}

				// determine whether a mutant is killed or not
				// update the test report
				boolean sign = false;
				for (int k = 0; k < junitTests.size(); k++) {
					String name = junitTests.get(k);
					if (!mutantResults.get(name).equals(originalResults.get(name))) {
						sign = true;
						// update the final results by tests
						if (finalTestResults.get(name).equals(""))
							finalTestResults.put(name, mutant_name);
						else
							finalTestResults.put(name, finalTestResults.get(name) + ", " + mutant_name);
						// update the final results by mutants
						if (finalMutantResults.get(mutant_name).equals(""))
							finalMutantResults.put(mutant_name, name);
						else
							finalMutantResults.put(mutant_name, finalMutantResults.get(mutant_name) + ", " + name);
					}
				}
				if (sign == true)
					tr.killed_mutants.add(mutant_name);
				else
					tr.live_mutants.add(mutant_name);

				mutantLoader = null;
				mutant_executer = null;
				System.gc();
			}

			for (int i = 0; i < tr.killed_mutants.size(); i++) {
				tr.live_mutants.remove(tr.killed_mutants.get(i));
			}
			/*
			 * System.out.println(" Analysis of testcases "); for(int i = 0;i <
			 * killed_mutants.length;i++){ System.out.println("  test " + (i+1)
			 * + "  kill  ==> " + killed_mutants[i]); }
			 */
		} catch (NoMutantException e1) {
			throw e1;
		} catch (NoMutantDirException e2) {
			throw e2;
		}
		/*
		 * catch(ClassNotFoundException e3){ System.err.println("[Execution 1] "
		 * + e3); return null; }
		 */catch (Exception e) {
			System.err.println("[Exception 2]" + e);
			return null;
		}
		Util.DebugPrint("\ntest report: " + finalTestResults);
		Util.DebugPrint("mutant report: " + finalMutantResults);

		/*
		 * Lin adds for save results
		 */
		// get time
		// Calendar nowtime = new GregorianCalendar();

		try {
			runmutes.saveTestResults(whole_class_name, finalTestResults, finalMutantResults, methodSignature); // save
																								// csv
																								// file

			tr.setPath(MutationSystem.TRADITIONAL_MUTANT_PATH + "/mutant_list");
			tr.outputToFile(methodSignature);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tr;
		
	}
	public TestResultCLI runTraditionalMutantsSingleThread(String methodSignature, String[] mutantTypes, double percentage)
			throws NoMutantException, NoMutantDirException {
		MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
		String original_mutant_path = MutationSystem.MUTANT_PATH;

		TestResultCLI test_result = new TestResultCLI();

		if (methodSignature.equals("All method")) {
			try {
				// setMutantPath();
				// computeOriginalTestResults();
				File f = new File(MutationSystem.TRADITIONAL_MUTANT_PATH, "method_list");
				FileReader r = new FileReader(f);
				BufferedReader reader = new BufferedReader(r);
				String readSignature = reader.readLine();
				while (readSignature != null) {   // for each method
					System.out.println("For method: "+readSignature);
					MutationSystem.MUTANT_PATH = original_mutant_path + "/" + readSignature;  // set the path to that method
					try {
						test_result = new TestResultCLI();
						// run each method
						runMutants(test_result, readSignature, mutantTypes, percentage);
					} catch (NoMutantException e) {
					}
					readSignature = reader.readLine();
				}
				reader.close();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		} else {
			MutationSystem.MUTANT_PATH = original_mutant_path + "/" + methodSignature;
			runMutants(test_result, methodSignature, mutantTypes, 1);
		}
		return test_result;
	}
	
	
}
