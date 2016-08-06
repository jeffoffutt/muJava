package mujava.plugin;

import static mujava.cli.testnew.sessionName;
import static util.Constants.*;

import java.io.File;

import analyzer.CorrectnessAnalyzer;
import bubblesort.test.BubbleSortDataProvider;
import bubblesort.test.BubbleSortSpecificationProvider;

import uglynumber.test.UglyNumberDataProvider;
import uglynumber.test.UglyNumberSpecificationProvider;
import util.CustomCompiler;
import util.FileUtil;
import util.MuJavaWrapper;

/**
 * @author Shreyash
 */
public class Plugin {

	private static String operatorString;
	private static String baseProgram;
	private static File combinedMutantFile;

	private static Class<? extends Object> combinedMutantsClazz = null;
	private static Class<? extends Object> baseProgramClazz = null;

	private static CustomCompiler compiler;
	private static MuJavaWrapper muJavaWrapper = null;
	private static FileUtil fileUtil;
	private static CorrectnessAnalyzer analyzer;

	static {
		compiler = new CustomCompiler();
		fileUtil = new FileUtil();
	}

	private static void parseArgs(String[] args) {

		if (args == null || args.length != 3) {
			throw new IllegalArgumentException("Please provide session name and list of mutation operators as argument."
					+ "Example -> session=session1 operator=-AORB basep=C:\\Users\\Shreyash\\Desktop\\cal.java");
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
			default:
				throw new IllegalArgumentException(
						"Please provide session name and list of mutation operators as argument."
								+ "Example -> session=session1 operator=-AORB basep=C:\\Users\\Shreyash"
								+ "\\Desktop\\cal.java");
			}
		}

		muJavaWrapper = new MuJavaWrapper(baseProgram, operatorString);

		System.out.println("Session Name: " + sessionName);
		System.out.println("Operator Name: " + operatorString);
		System.out.println("Base Program Name: " + baseProgram);

	}

	private static void generateMutantsUsingMuJava() {

		muJavaWrapper.setMujavaEnv();
		muJavaWrapper.createTestSession();
		muJavaWrapper.generateMutants();
	}

	private static void combineMutantMethodsInSingleJavaFile() {
		combinedMutantFile = fileUtil.combineMutants(baseProgram, COMBINED_MUTANTS_FILE_NAME);
	}

	@SuppressWarnings("unchecked")
	private static void compileAll() {
		combinedMutantsClazz = compiler.compile(combinedMutantFile.getAbsolutePath());
		baseProgramClazz = compiler.compile(baseProgram);
	}

	private static void analyze() {

		SpecificationProvider specs = new BubbleSortSpecificationProvider();
		DataProvider dataSet = new BubbleSortDataProvider();

		//SpecificationProvider specs = new UglyNumberSpecificationProvider();
		//DataProvider dataSet = new UglyNumberDataProvider();

		analyzer = new CorrectnessAnalyzer(combinedMutantsClazz, baseProgramClazz);

		analyzer.analyzeAllMutants(specs, dataSet);

	}

	public static void main(String[] args) {

		Thread.currentThread().setName("Main-Thread");

		parseArgs(args);

		generateMutantsUsingMuJava();

		combineMutantMethodsInSingleJavaFile();

		compileAll();

		analyze();

	}

}
