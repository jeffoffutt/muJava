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

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import mujava.cli.Util;
import mujava.cli.runmutes;
import mujava.test.*;
import mujava.util.*;

import org.junit.*;
import org.junit.internal.RealSystem;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.*;
/**
 * 
 * <p>
 * Description: New test executer class build exclusively for command line version
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0
 * 
 */

public class TestExecuterCLI extends TestExecuter {
	
//	int TIMEOUT = 3000;
	public static ArrayList<String> methodList = new ArrayList<>();
	public static ArrayList<String> methodList2 = new ArrayList<>();

	public TestExecuterCLI(String targetClassName) {
		super(targetClassName);

	}


	public boolean readTestSet(String testSetName) {
		try {
			testSet = testSetName;
			// Class loader for the original class
			OriginalLoader myLoader = new OriginalLoader();
			Util.DebugPrint(testSet);
			original_executer = myLoader.loadTestClass(testSet);
			original_obj = original_executer.newInstance(); // initialization of
															// the test set
															// class
			if (original_obj == null) {
				System.out.println("Can't instantiace original object");
				return false;
			}

			// read testcases from the test set class
			testCases = original_executer.getDeclaredMethods();
			if (testCases == null) {
				System.out.println(" No test case exist ");
				return false;
			}
		} catch (Exception e) {
			System.err.println(e);
			return false;
		}
		return true;

	}



	/**
	 * get the mutants for one method based on the method signature
	 * 
	 * @param methodSignature
	 * @return
	 * @throws NoMutantDirException
	 * @throws NoMutantException
	 */
	protected String[] getMutants(String methodSignature) throws NoMutantDirException, NoMutantException {

		// Read mutants
		// System.out.println("mutant_path: " + MutationSystem.MUTANT_PATH);
		File f = new File(MutationSystem.MUTANT_PATH);

		if (!f.exists()) {
			System.err.println(" There is no directory for the mutants of " + MutationSystem.CLASS_NAME);
			System.err.println(" Please generate mutants for " + MutationSystem.CLASS_NAME);
			throw new NoMutantDirException();
		}

		// mutantDirectories match the names of mutants
		String[] mutantDirectories = f.list(new MutantDirFilter());

		if (mutantDirectories == null || mutantDirectories.length == 0) {
			if (!methodSignature.equals(""))
				System.err.println(" No mutants have been generated for the method " + methodSignature
						+ " of the class" + MutationSystem.CLASS_NAME);
			else
				System.err.println(" No mutants have been generated for the class " + MutationSystem.CLASS_NAME);
			// System.err.println(" Please check if zero mutant is correct.");
			// throw new NoMutantException();
		}

		return mutantDirectories;
	}

	/**
	 * compute the result of a test under the original program
	 */
	public void computeOriginalTestResults() {
		Debug.println("\n\n======================================== Generating Original Test Results ========================================");
		try {
			// initialize the original results to "pass"
			// later the results of the failed test cases will be updated
			for (int k = 0; k < testCases.length; k++) {
				Annotation[] annotations = testCases[k].getDeclaredAnnotations();
				for (Annotation annotation : annotations) {
					// System.out.println("name: " + testCases[k].getName() +
					// annotation.toString() +
					// annotation.toString().indexOf("@org.junit.Test"));
					if (annotation.toString().indexOf("@org.junit.Test") != -1) {
						// killed_mutants[k]= ""; // At first, no mutants are
						// killed by each test case
						originalResults.put(testCases[k].getName(), "pass");
						junitTests.add(testCases[k].getName());
						finalTestResults.put(testCases[k].getName(), "");
						continue;
					}
				}
			}

			JUnitCore jCore = new JUnitCore();
			// result = jCore.runMain(new RealSystem(), "VMTEST1");
			result = jCore.run(original_executer);

			// get the failure report and update the original result of the test
			// with the failures
			List<Failure> listOfFailure = result.getFailures();
			for (Failure failure : listOfFailure) {
				String nameOfTest = failure.getTestHeader().substring(0, failure.getTestHeader().indexOf("("));
				String testSourceName = testSet + "." + nameOfTest;

				// System.out.println("failure message: " + failure.getMessage()
				// + failure.getMessage().equals(""));
				String[] sb = failure.getTrace().split("\\n");
				String lineNumber = "";
				for (int i = 0; i < sb.length; i++) {
					if (sb[i].indexOf(testSourceName) != -1) {
						lineNumber = sb[i].substring(sb[i].indexOf(":") + 1, sb[i].indexOf(")"));
					}
				}

				// put the failure messages into the test results
				if (failure.getMessage() == null)
					originalResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + "fail");
				else {
					if (failure.getMessage().equals(""))
						originalResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + "fail");
					else
						originalResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + failure.getMessage());
				}

			}
			Util.DebugPrint(originalResults.toString());

			// System.out.println(System.getProperty("user.dir"));
			// System.out.println(System.getProperty("java.class.path"));
			// System.out.println(System.getProperty("java.library.path"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

			// original_results[k] = e.getCause().getClass().getName()+" : "
			// +e.getCause().getMessage();
			// Debug.println("Result for " + testName + "  :  "
			// +original_results[k] );
			// Debug.println(" [warining] " + testName +
			// " generate exception as a result " );

			// ----------------------------------

		} finally {
			// originalResultFileRead();
		}
	}

	private TestResultCLI runMutants(TestResultCLI tr, String methodSignature) throws NoMutantException, NoMutantDirException {

		try {

			String[] mutantDirectories = getMutants(methodSignature);

			int mutant_num = mutantDirectories.length;
			tr.setMutants();
			for (int i = 0; i < mutant_num; i++) {
				// set live mutnats
				tr.mutants.add(mutantDirectories[i]);
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
					Runnable r = new Runnable() {
						public void run() {
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

								JUnitCore jCore = new JUnitCore();
								result = jCore.run(mutant_executer);

								List<Failure> listOfFailure = result.getFailures();
								for (Failure failure : listOfFailure) {
									String nameOfTest = failure.getTestHeader().substring(0,
											failure.getTestHeader().indexOf("("));
									String testSourceName = testSet + "." + nameOfTest;

									// System.out.println(testSourceName);
									String[] sb = failure.getTrace().split("\\n");
									String lineNumber = "";
									for (int i = 0; i < sb.length; i++) {
										// System.out.println("sb-trace: " +
										// sb[i]);
										if (sb[i].indexOf(testSourceName) != -1) {
											lineNumber = sb[i].substring(sb[i].indexOf(":") + 1, sb[i].indexOf(")"));

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
						}
					};

					Thread t = new Thread(r);
					t.setDaemon(true);
					t.start();

					synchronized (lockObject) {
						lockObject.wait(TIMEOUT); // Check out if a mutant is in
													// infinite loop
					}
					if (mutantRunning) {
						// System.out.println("check point4");
						t.interrupt();
						// mutant_result = "time_out: more than " + TIMEOUT +
						// " seconds";
						Util.DebugPrint(" time_out: more than " + TIMEOUT + " milliseconds");
						// mutantResults.put(nameOfTest, nameOfTest + ": " +
						// lineNumber + "; " + failure.getMessage());

						for (int k = 0; k < testCases.length; k++) {
							Annotation[] annotations = testCases[k].getDeclaredAnnotations();
							for (Annotation annotation : annotations) {
								// System.out.println("name: " +
								// testCases[k].getName() +
								// annotation.toString() +
								// annotation.toString().indexOf("@org.junit.Test"));
								if (annotation.toString().indexOf("@org.junit.Test") != -1) {
									// killed_mutants[k]= ""; // At first, no
									// mutants are killed by each test case
									mutantResults.put(testCases[k].getName(), "time_out: more than " + TIMEOUT
											+ " milliseconds");
									continue;
								}
							}
						}

					}
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
		Calendar nowtime = new GregorianCalendar();

		try {
			runmutes.saveTestResults(whole_class_name, finalTestResults, finalMutantResults, methodSignature);
			// tr.setPath(MutationSystem.TRADITIONAL_MUTANT_PATH
			// + "\\mutant_list_"
			// + nowtime.get(Calendar.YEAR) + "_"
			// + nowtime.get(Calendar.MONTH) + "_"
			// + nowtime.get(Calendar.DATE) + "_"
			// + nowtime.get(Calendar.HOUR) + "_"
			// + nowtime.get(Calendar.MINUTE) + "_"
			// + nowtime.get(Calendar.SECOND));

			tr.setPath(MutationSystem.TRADITIONAL_MUTANT_PATH + "/mutant_list");

			tr.outputToFile(methodSignature);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tr;

	}



	public TestResultCLI runTraditionalMutants(String methodSignature, String[] mutantTypes, double percentage)
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

	private TestResultCLI runMutants(TestResultCLI tr, String methodSignature, String[] mutantTypes, double percentage)
			throws NoMutantException, NoMutantDirException {
		try {

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
					Runnable r = new Runnable() {
						public void run() {
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
									for (int i = 0; i < sb.length; i++) {
										// System.out.println("sb-trace: " +
										// sb[i]);
										if (sb[i].indexOf(testSourceName) != -1) {
											lineNumber = sb[i].substring(sb[i].indexOf(":") + 1, sb[i].indexOf(")"));

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
						}
					};

					Thread t = new Thread(r);
					t.setDaemon(true);
					t.start();

					synchronized (lockObject) {
						lockObject.wait(TIMEOUT); // Check out if a mutant is in
													// infinite loop
					}
					if (mutantRunning) {
						 // System.out.println("check point4");
						//System.out.println(t.getName());
						
						 t.interrupt();
						 
						//mutantRunning=false;
						// mutant_result = "time_out: more than " + TIMEOUT +
						// " seconds";
						Util.DebugPrint(" time_out: more than " + TIMEOUT + " milliseconds");
						// mutantResults.put(nameOfTest, nameOfTest + ": " +
						// lineNumber + "; " + failure.getMessage());

						for (int k = 0; k < testCases.length; k++) {
							Annotation[] annotations = testCases[k].getDeclaredAnnotations();
							for (Annotation annotation : annotations) {
								// System.out.println("name: " +
								// testCases[k].getName() +
								// annotation.toString() +
								// annotation.toString().indexOf("@org.junit.Test"));
								if (annotation.toString().indexOf("@org.junit.Test") != -1) {
									// killed_mutants[k]= ""; // At first, no
									// mutants are killed by each test case
									mutantResults.put(testCases[k].getName(), "time_out: more than " + TIMEOUT
											+ " milliseconds");
									continue;
								}
							}
						}

					}

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

	public TestResultCLI runTraditionalMutants(String methodSignature, String[] mutantTypes, double percentage,
			Vector live_mutants) throws NoMutantException, NoMutantDirException {
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
				while (readSignature != null) {

					MutationSystem.MUTANT_PATH = original_mutant_path + "/" + readSignature;
					try {
						runMutants(test_result, readSignature, mutantTypes, percentage, live_mutants);
					} catch (NoMutantException e) {
					}
					readSignature = reader.readLine();
				}
				reader.close();
			} catch (Exception e) {
				System.err.println("Error in update() in TraditioanlMutantsViewerPanel.java");
			}
		} else {
			MutationSystem.MUTANT_PATH = original_mutant_path + "/" + methodSignature;
			runMutants(test_result, methodSignature, mutantTypes, 1);
		}
		return test_result;
	}

	private TestResultCLI runMutants(TestResultCLI tr, String methodSignature, String[] mutantTypes, double percentage,
			Vector live_mutants) throws NoMutantException, NoMutantDirException {
		try {

			String[] mutantDirectories = getMutants(methodSignature);
			// Lin adds: only run certain type
			// int mutant_num = mutantDirectories.length;
			int mutant_num = live_mutants.size();
			tr.setMutants();

			tr.mutants = live_mutants;

			Util.Print("\nCurrent running mode: " + runmutes.mode);

			// Lin adds: eliminate extra mutants based on random percentage
			if (percentage != 1) {
				int rondomSize = (int) (mutant_num * percentage);
				Random rand = new Random(System.currentTimeMillis());
				Vector tempMutantVector = new Vector(rondomSize);
				for (int i = 0; i < rondomSize; i++) {
					// be sure to use Vector.remove() or you may get the same
					// item
					// twice
					//System.out.println("candidates: " + tr.mutants);
					int randomNumber = rand.nextInt(tr.mutants.size());
					//System.out.println("random number: " + randomNumber);
					Object randomMutant = tr.mutants.remove(randomNumber);
					//System.out.println("random mutant selected: " + randomMutant);
					tempMutantVector.add(randomMutant);
					//System.out.println("result: " + tempMutantVector);
					// tempMutantVector.add(tr.mutants.remove(rand.nextInt(tr.mutants.size())));
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
				//System.out.println("!!!!!!!!!!!!!!!!" + mutant_executer.toString());
				try {
					// Mutants are runned using Thread to detect infinite loop
					// caused by mutation
					Runnable r = new Runnable() {
						public void run() {
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

								JUnitCore jCore = new JUnitCore();
								result = jCore.run(mutant_executer);

								List<Failure> listOfFailure = result.getFailures();
								for (Failure failure : listOfFailure) {
									String nameOfTest = failure.getTestHeader().substring(0,
											failure.getTestHeader().indexOf("("));
									String testSourceName = testSet + "." + nameOfTest;

									// System.out.println(testSourceName);
									String[] sb = failure.getTrace().split("\\n");
									String lineNumber = "";
									for (int i = 0; i < sb.length; i++) {
										// System.out.println("sb-trace: " +
										// sb[i]);
										if (sb[i].indexOf(testSourceName) != -1) {
											lineNumber = sb[i].substring(sb[i].indexOf(":") + 1, sb[i].indexOf(")"));

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
						}
					};

					Thread t = new Thread(r);
					t.setDaemon(true);
					t.start();

					synchronized (lockObject) {
						lockObject.wait(TIMEOUT); // Check out if a mutant is in
													// infinite loop
					}
					if (mutantRunning) {
						// System.out.println("check point4");

						//System.out.println("debug: " + mutantResults);
						t.interrupt();
						// mutant_result = "time_out: more than " + TIMEOUT +
						// " seconds";
						Util.DebugPrint(" time_out: more than " + TIMEOUT + " milliseconds");
						// mutantResults.put(nameOfTest, nameOfTest + ": " +
						// lineNumber + "; " + failure.getMessage());

						for (int k = 0; k < testCases.length; k++) {
							Annotation[] annotations = testCases[k].getDeclaredAnnotations();
							for (Annotation annotation : annotations) {
								// System.out.println("name: " +
								// testCases[k].getName() +
								// annotation.toString() +
								// annotation.toString().indexOf("@org.junit.Test"));
								if (annotation.toString().indexOf("@org.junit.Test") != -1) {
									// killed_mutants[k]= ""; // At first, no
									// mutants are killed by each test case
									mutantResults.put(testCases[k].getName(), "time_out: more than " + TIMEOUT
											+ " milliseconds");
									continue;
								}
							}
						}

						//System.out.println("debug: " + mutantResults);

					}
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
		Calendar nowtime = new GregorianCalendar();

		try {
			runmutes.saveTestResults(whole_class_name, finalTestResults, finalMutantResults, methodSignature); // write
																								// csv
																								// file


			tr.setPath(MutationSystem.TRADITIONAL_MUTANT_PATH + "/mutant_list");

			tr.outputToFile(methodSignature);

		} catch (IOException e) {
			e.printStackTrace();
		}


		return tr;
	}

}
