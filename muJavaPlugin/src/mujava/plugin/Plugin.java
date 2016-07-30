package mujava.plugin;

import static java.io.File.separator;
import static mujava.cli.testnew.sessionName;
import static mujava.cli.testnew.muJavaHomePath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.sun.tools.javac.Main;

import bubblesort.test.BubbleSortDataProvider;
import bubblesort.test.BubbleSortSpecificationProvider;
import mujava.cli.testnew;
import mujava.test.OriginalLoader;
import mujava.MutationSystem;
import mujava.cli.Util;
import mujava.cli.genmutes;
import mujava.cli.runmutes;

/**
 * @author Shreyash
 */
public class Plugin {

/*	private static final int ABSOLUTE_CORRECTNESS_TEST_PASSED = 0;
	private static final int RELATIVE_CORRECTNESS_TEST_PASSED = 1;
	private static final int STRICT_RELATIVE_CORRECTNESS_TEST_PASSED = 2;
	private static final int NONE_OF_THE_CORRECTNESS_TEST_PASSED = -1;*/

	private static final int INCORRECT_MUTANT = -1;
	private static final int NOT_TESTED_MUTANT = 0;
	private static final int EQUALLY_CORRECT_MUTANT = 1;
	private static final int RELATIVELY_CORRECT_MUTANT = 2;
	private static final int STRICTLY_RELATIVELY_CORRECT_MUTANT = 3;
	private static final int ABSOLUTELY_CORRECT_MUTANT=100;
	
	private static String operatorString;
	private static String baseProgram;
	private static String testOracle;
	private static File combinedMutantFile;
	private static final String COMBINED_MUTANTS_FILE_NAME = "CombinedMutants";

	private static List<String> methodList = new ArrayList<String>();

	private static Class<? extends Object> combinedMutantsClazz = null;
	private static Object combinedMutantClassObject = null;

	private static Class<? extends Object> baseProgramClazz = null;
	private static Object baseProgramClassObject = null;

	private static int maxExecutionTime = 2000;

	private static void parseArgs(String[] args) {

		if (args == null || args.length != 4) {
			throw new IllegalArgumentException("Please provide session name and list of mutation operators as argument."
					+ "Example -> session=session1 operator=-AORB basep=C:\\Users\\Shreyash"
					+ "\\Desktop\\cal.java oracle=C:\\Users\\Shreyash\\Desktop\\TestCal.class");
		}

		for (String arg : args) {
			String[] tempArg = arg.split("=");

			switch (tempArg[0]) {
			case "session":
				sessionName = tempArg[1];
				break;
			case "operator":
				operatorString = tempArg[1];
				break;
			case "basep":
				baseProgram = tempArg[1];
				break;
			case "oracle":
				testOracle = tempArg[1];
				break;
			default:
				throw new IllegalArgumentException(
						"Please provide session name and list of mutation operators as argument."
								+ "Example -> session=session1 operator=-AORB basep=C:\\Users\\Shreyash"
								+ "\\Desktop\\cal.java oracle=C:\\Users\\Shreyash\\Desktop\\TestCal.class");
			}
		}

		System.out.println("Session Name: " + sessionName);
		System.out.println("Operator Name: " + operatorString);
		System.out.println("Base Program Name: " + baseProgram);

		muJavaHomePath = System.getProperty("user.dir") + separator + "src" + separator + "mujava";
		mujava.cli.genmutes.muJavaHomePath = muJavaHomePath;
		mujava.cli.runmutes.muJavaHomePath = muJavaHomePath;
	}

	private static void createTestSession() {

		String[] argTestNew = { sessionName, baseProgram };
		try {
			testnew.main(argTestNew);
		} catch (IOException e1) {

			System.err.println("Error while creating test session!");
			e1.printStackTrace();
			System.exit(1);
		}

	}

	private static void generateMutants() {

		String[] argGenMutes = { operatorString, sessionName };

		try {
			genmutes.main(argGenMutes);
		} catch (Exception e1) {
			System.err.println("Error while generating mutants!");
			e1.printStackTrace();
			System.exit(1);
		}

	}

	@SuppressWarnings("unused")
	private static void runTests() {

		String testOracleName = testOracle.substring(testOracle.lastIndexOf(separator) + 1,
				testOracle.length() - ".class".length());

		File oracleFile = new File(testOracle);
		File testsetDir = new File(muJavaHomePath + "/" + sessionName + "/testset");
		try {

			FileUtils.copyFileToDirectory(oracleFile, testsetDir);
		} catch (IOException e) {
			System.err.println("Error while copying oracle to testset directory!");
			e.printStackTrace();
		}

		String[] argRunTest = { testOracleName, sessionName };

		try {
			runmutes.main(argRunTest);
		} catch (Exception e1) {
			System.err.println("Error while running tests!");
			e1.printStackTrace();
			System.exit(1);
		}

	}

	public static void main(String[] args) {

		Thread.currentThread().setName("Main-Thread");
		parseArgs(args);

		createTestSession();

		generateMutants();

		// runTests();

		combineMutants();

		compileCombinedMutantsClass();

		compileBaseProgram();

		invokeMutantMethods();

	}

	private static void invokeMutantMethods() {
		//Parameter[] parameters = null;

		try {
			combinedMutantClassObject = combinedMutantsClazz.newInstance();
			baseProgramClassObject = baseProgramClazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {

			e1.printStackTrace();
		}

		SpecificationProvider specs = new BubbleSortSpecificationProvider();
		DataProvider dataSet = new BubbleSortDataProvider();

		List<String> strictlyRelativelyCorrectMutants= new ArrayList<String>();
		List<String> absolutelyCorrectMutants= new ArrayList<String>();
		
		for (Method method : combinedMutantsClazz.getMethods()) {

			if (method.getDeclaringClass().equals(combinedMutantsClazz)) {
				System.out.println("\n\nMutant method : " + method.getName()
						+ " found in CombinedMutants.class! Checking for strict relative correctness...");

				/*if (parameters == null) {
					parameters = method.getParameters();

					for (Parameter parameter : parameters) {
						System.out.println(parameter.getName());
					}

				}
*/

					switch (doCorrectnessAnalysis(method, dataSet.provideData(), specs)) {

					case STRICTLY_RELATIVELY_CORRECT_MUTANT:
						System.out.println(method.getName()+ " selected by Strict relative correctness test");
						strictlyRelativelyCorrectMutants.add(method.getName());
						break;
					case ABSOLUTELY_CORRECT_MUTANT:
						System.out.println(method.getName()+ " selected by absolute correctness test");
						absolutelyCorrectMutants.add(method.getName());
						break;
					default:
						System.out.println(method.getName()+ " rejected by Strict relative correctness test");
					}
					// TODO need to gracefully handle the runtime exceptions
					// or infinite loops that this method may end up into
					// due to mutation


			} else {
				System.out.println("Method name: " + method.getName() + " found in "
						+ method.getDeclaringClass().getName() + ". Do Nothing!");
			}
		}
		
		System.out.println("\n\n\n########################################################################");
		System.out.println("Mutants that were selected by strict relative correctness test are -");
		int count = 1;
		for(String mutant:strictlyRelativelyCorrectMutants){
			System.out.println(count+". "+mutant);
			count++;
		}
		System.out.println("\n");
		System.out.println("Mutants that were selected by absolute correctness test are -");
		count = 1;
		for(String mutant:absolutelyCorrectMutants){
			System.out.println(count+". "+mutant);
			count++;
		}
		System.out.println("\n\n\n########################################################################");
	}
/*
	private static int doCorrectnessAnalysis(Method method, List<Object> dataSet, SpecificationProvider specs)
			throws MutantRejectedException {

		Object mutantTestResult = null;
		boolean absolutelyCorretMutant = true;
		boolean relativelyCorrectMutant = true;
		boolean strictlyRelativelyCorrectMutant = true;

		for (Object data : dataSet) {

			int[] intArray;
			if (data instanceof Integer[]) {
				intArray = new int[((Integer[]) data).length];
				for (int i = 0; i < intArray.length; i++) {
					intArray[i] = ((Integer[]) data)[i];
				}

				// mutantTestResult = method.invoke(combinedMutantClassObject,
				// intArray);

				// Threaded execution for mutant method invocation
				MethodInvoker mutantMethodInvoker = new MethodInvoker(method, combinedMutantClassObject, true,
						intArray);
				Thread mutantThread = new Thread(mutantMethodInvoker);

				try {
					System.out.println("waiting on " + mutantMethodInvoker);
					synchronized (mutantMethodInvoker) {
						mutantThread.setDaemon(true);
						mutantThread.start();
						mutantMethodInvoker.wait(maxExecutionTime);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// Threaded execution ends

				if (mutantMethodInvoker.objectReturned != null) {
					mutantTestResult = mutantMethodInvoker.objectReturned;

					int[] specificationArray = (int[]) specs.provideSpecification(intArray);

					if (absolutelyCorretMutant) {
						absolutelyCorretMutant = specs.testForAbsoluteCorrectness(mutantTestResult, specificationArray);
					}

					if (strictlyRelativelyCorrectMutant) {

						for (Method basePMethod : baseProgramClazz.getMethods()) {

							if (method.getName().contains(basePMethod.getName())
									&& method.getReturnType().equals(basePMethod.getReturnType())) {

								int[] baseProgramResult = null;
								try {
									// baseProgramResult = (int[])
									// basePMethod.invoke(baseProgramClassObject,
									// intArray);

									// Threaded execution for base program
									// method
									// invocation
									MethodInvoker baseProgramMethodInvoker = new MethodInvoker(method,
											combinedMutantClassObject, true, intArray);
									Thread baseProgramThread = new Thread(baseProgramMethodInvoker);

									try {

										synchronized (baseProgramMethodInvoker) {
											baseProgramThread.setDaemon(true);
											baseProgramThread.start();
											baseProgramMethodInvoker.wait(maxExecutionTime);
										}
										// Thread.sleep(maxExecutionTime);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

									if (baseProgramMethodInvoker.objectReturned != null) {
										baseProgramResult = (int[]) baseProgramMethodInvoker.objectReturned;
									}
									// Threaded execution ends

								} catch (Exception e) {

									strictlyRelativelyCorrectMutant = true;
								}

								strictlyRelativelyCorrectMutant = specs.testForStrictlyRelativeCorrectness(
										mutantTestResult, baseProgramResult, specificationArray);
							}

						}

						// strictlyRelativelyCorrectMutant =
						// specs.testForStrictlyRelativeCorrectness(mutantTestResult,
						// baseProgramResult, specification)

					}

					if (relativelyCorrectMutant) {
						// relativelyCorrectMutant =
						// specs.testForStrictlyRelativeCorrectness(mutantTestResult,
						// baseProgramResult, specification);
					}

				} else {
					if (mutantThread.isAlive()) {
						System.out.println(method.getName() + " has been executing more than " + maxExecutionTime
								+ "ms. Rejecting it.");
					}
					break;
				}
				// correctness=doCorrectnessAnalysis(intArray, result, specs);
			} else {
				Object specificationArray = specs.provideSpecification(data);
				// mutantTestResult = method.invoke(combinedMutantClassObject,
				// data);

				// Threaded execution for mutant method invocation
				MethodInvoker mutantMethodInvoker = new MethodInvoker(method, combinedMutantClassObject, true, data);
				Thread mutantThread = new Thread(mutantMethodInvoker);

				try {
					synchronized (mutantMethodInvoker) {
						mutantThread.setDaemon(true);
						mutantThread.start();
						mutantMethodInvoker.wait(maxExecutionTime);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (mutantMethodInvoker.objectReturned != null) {
					mutantTestResult = mutantMethodInvoker.objectReturned;
				} else {

				}
				// Threaded execution ends

				if (absolutelyCorretMutant) {
					absolutelyCorretMutant = specs.testForAbsoluteCorrectness(mutantTestResult, specificationArray);
				}
				// correctness=doCorrectnessAnalysis(data, result, specs);
			}

			if (!absolutelyCorretMutant || !strictlyRelativelyCorrectMutant) {
				break;
			}
		}

		
		 * if (absolutelyCorretMutant) { System.out.println("Mutant " +
		 * method.getName() + " selected by Absolute correctness test"); return
		 * ABSOLUTE_CORRECTNESS_TEST_PASSED; } else
		 
		if (strictlyRelativelyCorrectMutant) {
			System.out.println("Mutant " + method.getName() + " selected by Strict relative correctness test");
			return STRICT_RELATIVE_CORRECTNESS_TEST_PASSED;
		}
		return NONE_OF_THE_CORRECTNESS_TEST_PASSED;
		
		 * else if (relativelyCorrectMutant) { System.out.println("Mutant " +
		 * method.getName() +
		 * " passed relative correctness but failed strict relative correctness. So rejecting it."
		 * ); return false; }
		 
		// boolean relativelyCorrectMutant =
		// specs.testForRelativeCorrectness(testResult, baseProgramResult,
		// specificationArray);
		
		 * Object specResult = specs.provideSpecification(testData);
		 * 
		 * if(specs.compare(specResult, testResult)!= 0){
		 * 
		 * } else{ System.out.println(
		 * "Mutant Passed Absolute correctness test!!!"); }
		 
	}

*/	
	
	
	private static int doCorrectnessAnalysis(Method method, List<Object> dataSet, SpecificationProvider specs){
		int presentCorrectnessStatus = NOT_TESTED_MUTANT;
		
		int iteration = 0;
		boolean isFinalTest = false;
		int correctnessScore =0;
		for(Object data: dataSet){			
			iteration++;
			
			if(iteration == dataSet.size()){
				isFinalTest = true;
			}
				
			int[] intArray;
			int[] intArrayCloneForMutant;
			int[] intArrayCloneForBaseProgram;
			
			if (data instanceof Integer[]) {								
					Object mutantTestResultObject=null;					
					Object baseProgramTestResultObject=null;
					boolean mutantTestResult = false;
					boolean baseProgramTestResult = false;
					
					intArray = new int[((Integer[]) data).length];
					for (int i = 0; i < intArray.length; i++) {
						intArray[i] = ((Integer[]) data)[i];
					}
				
					intArrayCloneForMutant= intArray.clone();
					intArrayCloneForBaseProgram= intArray.clone();
					
					System.out.println("Test Data: "+ printIntegerArray(intArray));
				
					// Threaded execution for mutant method invocation
					MethodInvoker mutantMethodInvoker = new MethodInvoker(method, combinedMutantClassObject, true,
							intArrayCloneForMutant);
					Thread mutantThread = new Thread(mutantMethodInvoker);
	
					try {
						synchronized (mutantMethodInvoker) {
							mutantThread.setDaemon(true);
							mutantThread.start();
							//System.out.println("waiting on " + mutantMethodInvoker);
							mutantMethodInvoker.wait(maxExecutionTime);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Threaded execution ends
			
					
					int[] specificationArray = (int[]) specs.provideSpecification(intArray);
					
					mutantTestResultObject = mutantMethodInvoker.objectReturned;
					if (mutantTestResultObject != null) {												
						
							mutantTestResult = specs.testAgainstSpecification(mutantTestResultObject, specificationArray);
							System.out.println("Mutant result:: "+ printIntegerArray((int[])mutantTestResultObject));
							if(mutantTestResult){
								correctnessScore++;
							}
					}else{
						System.out.println("Mutant result:: null");
					}			
			
					
					// Threaded execution for base program method invocation
					MethodInvoker baseProgramMethodInvoker = new MethodInvoker(method,
							baseProgramClassObject, false, intArrayCloneForBaseProgram);
					Thread baseProgramThread = new Thread(baseProgramMethodInvoker);

					try {

						synchronized (baseProgramMethodInvoker) {
							baseProgramThread.setDaemon(true);
							baseProgramThread.start();
							baseProgramMethodInvoker.wait(maxExecutionTime);
						}
						// Thread.sleep(maxExecutionTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					baseProgramTestResultObject = baseProgramMethodInvoker.objectReturned;
					if ( baseProgramTestResultObject != null) {
						baseProgramTestResult = specs.testAgainstSpecification(baseProgramTestResultObject, specificationArray);
						System.out.println("Base Program result:: "+ printIntegerArray((int[])baseProgramTestResultObject));
					}else{
						System.out.println("Base Program result:: null");
					}		
					// Threaded execution ends
					
			presentCorrectnessStatus =calculateNewCorrectnessStatus(presentCorrectnessStatus, baseProgramTestResult, mutantTestResult, isFinalTest);
			if(presentCorrectnessStatus == INCORRECT_MUTANT || presentCorrectnessStatus == STRICTLY_RELATIVELY_CORRECT_MUTANT){
				
				if(correctnessScore == dataSet.size()){
					presentCorrectnessStatus = ABSOLUTELY_CORRECT_MUTANT;
				}
				return presentCorrectnessStatus;
			}
			
			}
			}
		return presentCorrectnessStatus;
	}
	
	private static String printIntegerArray(int[] array){
		StringBuilder sb = new StringBuilder("[ ");
		if (array == null){
			return "null";
		}
		
		for (int i=0;i<array.length;i++){
			sb.append(array[i]+" ");
		}
		sb.append("]");
		return sb.toString();
	}
	
	private static int calculateNewCorrectnessStatus(int presentCorrectnessStatus, boolean baseProgramResult,
			boolean mutantTestResult, boolean isFinalTest) {
		switch (presentCorrectnessStatus) {
		case NOT_TESTED_MUTANT:
		case EQUALLY_CORRECT_MUTANT:
			if (baseProgramResult && mutantTestResult) {
				return EQUALLY_CORRECT_MUTANT;
			} else if (!baseProgramResult && !mutantTestResult) {
				return EQUALLY_CORRECT_MUTANT;
			} else if (baseProgramResult && !mutantTestResult) {
				return INCORRECT_MUTANT;
			} else if (!baseProgramResult && mutantTestResult) {
				if (isFinalTest) {
					return STRICTLY_RELATIVELY_CORRECT_MUTANT;
				} else {
					return RELATIVELY_CORRECT_MUTANT;
				}
			}
			break;

		case RELATIVELY_CORRECT_MUTANT:
			if (baseProgramResult && mutantTestResult) {
				if (isFinalTest) {
					return STRICTLY_RELATIVELY_CORRECT_MUTANT;
				}else{
					return RELATIVELY_CORRECT_MUTANT;
				}					
			} else if (!baseProgramResult && !mutantTestResult) {
				if (isFinalTest) {
					return STRICTLY_RELATIVELY_CORRECT_MUTANT;
				}else{
					return RELATIVELY_CORRECT_MUTANT;
				}
			} else if (baseProgramResult && !mutantTestResult) {
				return INCORRECT_MUTANT;
			} else if (!baseProgramResult && mutantTestResult) {
				if (isFinalTest) {
					return STRICTLY_RELATIVELY_CORRECT_MUTANT;
				} else {
					return RELATIVELY_CORRECT_MUTANT;
				}
			}

			break;

		case STRICTLY_RELATIVELY_CORRECT_MUTANT:
			throw new RuntimeException(
					"Invalid state. Strictly relatively correct mutant cannot be further tested for correctness.");

		case INCORRECT_MUTANT:
			throw new RuntimeException("Invalid state. Incorrect mutant cannot be further tested for correctness.");

		default:
			return INCORRECT_MUTANT;
		}
		return INCORRECT_MUTANT;
	}

	@SuppressWarnings("unchecked")
	public static void compileBaseProgram() {
		int status = Main.compile(new String[] { baseProgram });

		File classFile = null;
		if (status != 0) {
			Util.Error("Can't compile src file, please compile manually.");
		} else {
			Util.Print("Source file is compiled successfully.");

			classFile = new File(baseProgram.replace(".java", ".class"));
			try {
				FileUtils.copyFile(classFile, new File(MutationSystem.CLASS_PATH + separator + classFile.getName()));
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		try {
			baseProgramClazz = new OriginalLoader().loadClass(classFile.getName().replace(".class", ""));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(classFile.getName() + " loaded successfully");

	}

	@SuppressWarnings("unchecked")
	private static void compileCombinedMutantsClass() {

		int status = Main.compile(new String[] { combinedMutantFile.getAbsolutePath() });

		File classFile = null;
		if (status != 0) {
			Util.Error("Can't compile src file, please compile manually.");
		} else {
			Util.Print("Source file is compiled successfully.");

			classFile = new File(combinedMutantFile.getAbsolutePath().replace(".java", ".class"));
			try {
				FileUtils.copyFile(classFile, new File(MutationSystem.CLASS_PATH + separator + classFile.getName()));
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		try {
			combinedMutantsClazz = new OriginalLoader().loadClass("CombinedMutants");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("CombinedMutants.class loaded successfully");
	}

	public static void combineMutants() {

		combinedMutantFile = new File(muJavaHomePath + separator + sessionName + separator + "result" + separator
				+ COMBINED_MUTANTS_FILE_NAME + ".java");

		try {
			combinedMutantFile.createNewFile();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		FileWriter writer = null;
		try {
			writer = new FileWriter(combinedMutantFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		File[] resultSetBaseDirectory = new File(
				muJavaHomePath + separator + sessionName + separator + "result" + separator
						+ baseProgram.substring(baseProgram.lastIndexOf(separator) + 1, baseProgram.lastIndexOf("."))
						+ separator + "traditional_mutants").listFiles();

		String methodSignature = null;

		List<File> resultSetMethodDirectory = new ArrayList<File>();

		for (File file : resultSetBaseDirectory) {
			if (file.isDirectory()) {
				resultSetMethodDirectory.add(file);

				String filename = file.getName();
				methodSignature = filename.substring(filename.lastIndexOf("_") + 1, filename.indexOf("("));
			}
		}

		List<File> resultSetMutantsDirectory = new ArrayList<File>();

		for (File file : resultSetMethodDirectory) {
			if (file.isDirectory())
				resultSetMutantsDirectory.addAll(Arrays.asList(file.listFiles()));
		}

		for (File mutantDirectory : resultSetMutantsDirectory) {
			System.out.println(mutantDirectory);
		}

		boolean importsExtracted = false;

		for (File mutantDirectory : resultSetMutantsDirectory) {

			File mutantFile = mutantDirectory.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".java"))
						return true;
					return false;
				}
			})[0];

			System.out.println("Mutant found -" + mutantDirectory.getAbsolutePath() + separator + mutantFile.getName());

			if (!importsExtracted) {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(mutantFile));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				try {
					String temp = reader.readLine();
					while (temp != null) {
						if (temp.contains("import")) {
							writer.append(temp + "\n");
						}
						temp = reader.readLine();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					writer.append("public class " + COMBINED_MUTANTS_FILE_NAME + "{ \n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				importsExtracted = true;
			}

			try {
				writer.append(extractMethod(mutantFile, methodSignature));
			} catch (IOException e) {
				System.err.println("Error while extracting method from mutant " + mutantDirectory.getAbsolutePath()
						+ separator + mutantFile.getName());
				e.printStackTrace();
			}
		}
		try {
			writer.append("}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String extractMethod(File mutantFile, String methodSignature) {
		StringBuffer method = new StringBuffer();

		int codeBlockStart = 0;
		int codeBlockEnd = 0;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(mutantFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		boolean methodFound = false;
		String temp = null;
		while (!methodFound) {
			try {
				temp = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (temp.contains(methodSignature))
				methodFound = true;
		}

		String newMethodSignature = methodSignature + "_" + mutantFile.getParentFile().getName();

		methodList.add(newMethodSignature);

		method.append(temp.replace(methodSignature, newMethodSignature) + "\n");

		if (temp.contains("{"))
			codeBlockStart++;
		else if (temp.contains("}"))
			codeBlockEnd++;

		while ((codeBlockStart == 0 && codeBlockEnd == 0) || codeBlockStart != codeBlockEnd) {
			try {
				temp = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (temp == null) {
				break;
			} else {
				method.append(temp + "\n");
				if (temp.contains("{"))
					codeBlockStart++;
				else if (temp.contains("}"))
					codeBlockEnd++;
			}
		}

		System.out.println(method.toString());

		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return method.toString();

	}

	public static void updateJavaPackageName(File javaFile, String sessionName) throws IOException {

		String tmpFileName = "tmp_try.dat";

		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(javaFile));
			bw = new BufferedWriter(new FileWriter(tmpFileName));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("package"))
					line = line.replace("mujava.source", "mujava." + sessionName + ".src");
				// line="";
				bw.write(line + "\n");
			}
		} finally {
			if (br != null)
				br.close();

			if (bw != null)
				bw.close();
		}
		// Once everything is complete, delete old file..

		javaFile.delete();

		// And rename tmp file's name to old file name
		File newFile = new File(tmpFileName);
		newFile.renameTo(javaFile);

	}

	public static void addJavaPackageName(File javaFile, String sessionName) throws IOException {

		String tmpFileName = "tmp_try.dat";

		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(javaFile));
			bw = new BufferedWriter(new FileWriter(tmpFileName));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("package"))
					line = line.replace("mujava.source", "mujava." + sessionName + ".src");
				line = "";
				bw.write(line + "\n");
			}
		} finally {
			if (br != null)
				br.close();

			if (bw != null)
				bw.close();
		}
		// Once everything is complete, delete old file..

		javaFile.delete();

		// And rename tmp file's name to old file name
		File newFile = new File(tmpFileName);
		newFile.renameTo(javaFile);

	}

}
