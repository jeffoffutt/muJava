package analyzer;

import static util.Constants.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mujava.plugin.DataProvider;
import mujava.plugin.MethodInvoker;
import mujava.plugin.SpecificationProvider;
import util.MutantMethodNameComparator;
import util.StringUtil;

public class CorrectnessAnalyzer {

	private Class<? extends Object> combinedMutantsClazz;
	
	private Class<? extends Object> baseProgramClazz;

	private Object combinedMutantClassObject = null;

	private Object baseProgramClassObject = null;

	private Method baseProgramMethod = null;
	
	private static int maxExecutionTime = 2000;

	private static StringUtil stringUtil = new StringUtil();

	private static Comparator<String> comparator = new MutantMethodNameComparator();

	private MethodInvoker baseProgramMethodInvoker;
	private MethodInvoker mutantMethodInvoker;
	
	public CorrectnessAnalyzer(Class<? extends Object> combinedMutantsClazz, Class<? extends Object> baseProgramClazz) {
		this.combinedMutantsClazz = combinedMutantsClazz;
		this.baseProgramClazz = baseProgramClazz;

		try {
			combinedMutantClassObject = combinedMutantsClazz.newInstance();
			baseProgramClassObject = baseProgramClazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {

			e1.printStackTrace();
		}
	}

	public int doCorrectnessAnalysisForMutant(Method method, List<Object> dataSet, SpecificationProvider specs) {

		int presentCorrectnessStatus = NOT_TESTED_MUTANT;

		int iteration = 0;
		boolean isFinalTest = false;
		int correctnessScore = 0;
		for (Object data : dataSet) {
			iteration++;

			if (iteration == dataSet.size()) {
				isFinalTest = true;
			}

			int[] intArray;
			int[] intArrayCloneForMutant;
			int[] intArrayCloneForBaseProgram;

			if (data instanceof Integer[]) {
				Object mutantTestResultObject = null;
				Object baseProgramTestResultObject = null;
				boolean mutantTestResult = false;
				boolean baseProgramTestResult = false;

				intArray = new int[((Integer[]) data).length];
				for (int i = 0; i < intArray.length; i++) {
					intArray[i] = ((Integer[]) data)[i];
				}

				intArrayCloneForMutant = intArray.clone();
				intArrayCloneForBaseProgram = intArray.clone();

				System.out.println("Test Data: " + stringUtil.printIntegerArray(intArray));

				// Threaded execution for mutant method invocation
				mutantMethodInvoker = new MethodInvoker(method, combinedMutantClassObject, true,
						intArrayCloneForMutant);
				Thread mutantThread = new Thread(mutantMethodInvoker);

				try {
					synchronized (mutantMethodInvoker) {
						mutantThread.setDaemon(true);
						mutantThread.start();
						// System.out.println("waiting on " +
						// mutantMethodInvoker);
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
					System.out
							.println("Mutant result:: " + stringUtil.printIntegerArray((int[]) mutantTestResultObject));
					if (mutantTestResult) {
						correctnessScore++;
					}
				} else {
					System.out.println("Mutant result:: null");
				}

				// Threaded execution for base program method invocation
				baseProgramMethodInvoker = new MethodInvoker(baseProgramMethod, baseProgramClassObject, false,
						intArrayCloneForBaseProgram);
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
				if (baseProgramTestResultObject != null) {
					baseProgramTestResult = specs.testAgainstSpecification(baseProgramTestResultObject,
							specificationArray);
					System.out.println("Base Program result:: "
							+ stringUtil.printIntegerArray((int[]) baseProgramTestResultObject));
				} else {
					System.out.println("Base Program result:: null");
				}
				// Threaded execution ends

				presentCorrectnessStatus = calculateNewCorrectnessStatus(presentCorrectnessStatus,
						baseProgramTestResult, mutantTestResult, isFinalTest);
				if (presentCorrectnessStatus == INCORRECT_MUTANT
						|| presentCorrectnessStatus == STRICTLY_RELATIVELY_CORRECT_MUTANT) {

					if (correctnessScore == dataSet.size()) {
						presentCorrectnessStatus = ABSOLUTELY_CORRECT_MUTANT;
					}
					return presentCorrectnessStatus;
				}

			}
			/**
			 * Caution: New Code ahead
			 */
			else {

				Object mutantTestResultObject = null;
				Object baseProgramTestResultObject = null;
				boolean mutantTestResult = false;
				boolean baseProgramTestResult = false;

				System.out.println("Test Data: " + data.toString());

				// Threaded execution for mutant method invocation
				MethodInvoker mutantMethodInvoker = new MethodInvoker(method, combinedMutantClassObject, true, data);
				Thread mutantThread = new Thread(mutantMethodInvoker);

				try {
					synchronized (mutantMethodInvoker) {
						mutantThread.setDaemon(true);
						mutantThread.start();
						// System.out.println("waiting on " +
						// mutantMethodInvoker);
						mutantMethodInvoker.wait(maxExecutionTime);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// Threaded execution ends

				Object specificationObject = specs.provideSpecification(data);

				mutantTestResultObject = mutantMethodInvoker.objectReturned;
				if (mutantTestResultObject != null) {

					mutantTestResult = specs.testAgainstSpecification(mutantTestResultObject, specificationObject);
					System.out.println("Mutant result:: " + mutantTestResultObject.toString());
					if (mutantTestResult) {
						correctnessScore++;
					}
				} else {
					System.out.println("Mutant result:: null");
				}

				// Threaded execution for base program method invocation
				MethodInvoker baseProgramMethodInvoker = new MethodInvoker(baseProgramMethod, baseProgramClassObject, false, data);
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
				if (baseProgramTestResultObject != null) {
					baseProgramTestResult = specs.testAgainstSpecification(baseProgramTestResultObject, specificationObject);
					System.out.println("Base Program result:: " + baseProgramTestResultObject.toString());
				} else {
					System.out.println("Base Program result:: null");
				}
				// Threaded execution ends

				presentCorrectnessStatus = calculateNewCorrectnessStatus(presentCorrectnessStatus,
						baseProgramTestResult, mutantTestResult, isFinalTest);
				if (presentCorrectnessStatus == INCORRECT_MUTANT
						|| presentCorrectnessStatus == STRICTLY_RELATIVELY_CORRECT_MUTANT) {

					if (correctnessScore == dataSet.size()) {
						presentCorrectnessStatus = ABSOLUTELY_CORRECT_MUTANT;
					}
					return presentCorrectnessStatus;
				}

			}
		}
		return presentCorrectnessStatus;
	}

	private int calculateNewCorrectnessStatus(int presentCorrectnessStatus, boolean baseProgramResult,
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
				} else {
					return RELATIVELY_CORRECT_MUTANT;
				}
			} else if (!baseProgramResult && !mutantTestResult) {
				if (isFinalTest) {
					return STRICTLY_RELATIVELY_CORRECT_MUTANT;
				} else {
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

	public void analyzeAllMutants(SpecificationProvider specs, DataProvider dataSet) {

		List<String> absolutelyCorrectMutants = new ArrayList<String>();
		List<String> strictlyRelativelyCorrectMutants = new ArrayList<String>();
		List<String> relativelyCorrectMutants = new ArrayList<String>();
		
		for(Method baseProgramMethod:baseProgramClazz.getMethods()){
			if(baseProgramMethod.getDeclaringClass().equals(baseProgramClazz)){
				this.baseProgramMethod = baseProgramMethod;
			}
		}
		
		for (Method method : combinedMutantsClazz.getMethods()) {

			if (method.getDeclaringClass().equals(combinedMutantsClazz)) {
				System.out.println("\n\nMutant method : " + method.getName()
						+ " found in CombinedMutants.class! Checking for strict relative correctness...");

				switch (doCorrectnessAnalysisForMutant(method, dataSet.provideData(), specs)) {

				case STRICTLY_RELATIVELY_CORRECT_MUTANT:
					System.out.println(method.getName() + " selected by Strict relative correctness test");
					strictlyRelativelyCorrectMutants.add(method.getName());
					break;
				case ABSOLUTELY_CORRECT_MUTANT:
					System.out.println(method.getName() + " selected by absolute correctness test");
					absolutelyCorrectMutants.add(method.getName());
					break;
				case RELATIVELY_CORRECT_MUTANT:
				case EQUALLY_CORRECT_MUTANT:
					System.out.println(method.getName() + " selected by relative correctness test");
					relativelyCorrectMutants.add(method.getName());
					break;
				default:
					System.out.println(method.getName() + " rejected by all correctness tests");
				}
			} else {
				System.out.println("Method name: " + method.getName() + " found in "
						+ method.getDeclaringClass().getName() + ". Do Nothing!");
			}
		}

		Collections.sort(absolutelyCorrectMutants, comparator);
		Collections.sort(strictlyRelativelyCorrectMutants, comparator);
		Collections.sort(relativelyCorrectMutants, comparator);
		

		System.out.println("\n\n\n########################################################################");
		System.out.println("Mutants that were selected by relative correctness test are -");
		int count = 1;

		for (String mutant : relativelyCorrectMutants) {
			System.out.println(count + ". " + mutant);
			count++;
		}
		System.out.println("\n");
		
		System.out.println("Mutants that were selected by strict relative correctness test are -");
		count = 1;

		for (String mutant : strictlyRelativelyCorrectMutants) {
			System.out.println(count + ". " + mutant);
			count++;
		}
		System.out.println("\n");
		System.out.println("Mutants that were selected by absolute correctness test are -");
		count = 1;
		for (String mutant : absolutelyCorrectMutants) {
			System.out.println(count + ". " + mutant);
			count++;
		}
		System.out.println("\n\n\n########################################################################");
	}
}
