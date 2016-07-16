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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import mujava.cli.testnew;
import mujava.cli.genmutes;
import mujava.cli.runmutes;

/**
 * @author Shreyash
 */
public class Plugin {

	private static String operatorString;
	private static String baseProgram;
	private static String testOracle;
	private static File combinedMutantFile;
	private static final String COMBINED_MUTANTS_FILE_NAME = "CombinedMutants";
	
	private static List<String> methodList = new ArrayList<String>();

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

		parseArgs(args);

		createTestSession();
		
		generateMutants();
		
		//runTests();

		combineMutants();
	
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
