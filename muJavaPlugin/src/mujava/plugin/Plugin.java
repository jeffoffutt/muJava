package mujava.plugin;

import static mujava.cli.testnew.sessionName;
import static util.Constants.*;

import java.io.File;

import analyzer.CorrectnessAnalyzer;

import util.CustomCompiler;
import util.FileUtil;
import util.MuJavaWrapper;

/**
 * @author Shreyash
 */
public class Plugin {

	private  DataProvider dataSet;
	private  SpecificationProvider specs;
	
	private  String operatorString;
	private  String baseProgram;
	private  File combinedMutantFile;

	private  Class<? extends Object> combinedMutantsClazz = null;
	private  Class<? extends Object> baseProgramClazz = null;

	private  CustomCompiler compiler;
	private  MuJavaWrapper muJavaWrapper = null;
	private  FileUtil fileUtil;
	private  CorrectnessAnalyzer analyzer;


	public Plugin(SpecificationProvider specs, DataProvider dataSet){

		compiler = new CustomCompiler();
		fileUtil = new FileUtil();
		
		this.specs = specs;
		this.dataSet = dataSet;
	}
	
	private void parseArgs(String[] args) {

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

	private void generateMutantsUsingMuJava() {

		muJavaWrapper.setMujavaEnv();
		muJavaWrapper.createTestSession();
		muJavaWrapper.generateMutants();
	}

	private void combineMutantMethodsInSingleJavaFile() {
		combinedMutantFile = fileUtil.combineMutants(baseProgram, COMBINED_MUTANTS_FILE_NAME);
	}

	@SuppressWarnings("unchecked")
	private void compileAll() {
		combinedMutantsClazz = compiler.compile(combinedMutantFile.getAbsolutePath());
		baseProgramClazz = compiler.compile(baseProgram);
	}

	private void analyze() {

		//specs = new BubbleSortSpecificationProvider();
		//dataSet = new BubbleSortDataProvider();

		//SpecificationProvider specs = new UglyNumberSpecificationProvider();
		//DataProvider dataSet = new UglyNumberDataProvider();

		analyzer = new CorrectnessAnalyzer(combinedMutantsClazz, baseProgramClazz);

		analyzer.analyzeAllMutants(specs, dataSet);

	}

	
	public void execute(String[] args) {

		//Plugin plugin = new Plugin();
		
		Thread.currentThread().setName("Main-Thread");

		parseArgs(args);

		generateMutantsUsingMuJava();

		combineMutantMethodsInSingleJavaFile();

		compileAll();

		analyze();

	}

}
