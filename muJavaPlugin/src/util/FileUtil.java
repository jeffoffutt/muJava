package util;

import static java.io.File.separator;
import static mujava.cli.testnew.muJavaHomePath;
import static mujava.cli.testnew.sessionName;

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

public class FileUtil {

	public File combineMutants(String baseProgram, String combinedMutantFileName) {

		File combinedMutantFile = new File(muJavaHomePath + separator + sessionName + separator + "result" + separator
				+ combinedMutantFileName + ".java");

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
					writer.append("public class " + combinedMutantFileName + "{ \n");
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

		return combinedMutantFile;
	}

	private String extractMethod(File mutantFile, String methodSignature) {
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

		if (temp.contains("{"))
			codeBlockStart++;
		else if (temp.contains("}"))
			codeBlockEnd++;

		method.append(temp.replace(methodSignature, newMethodSignature) + "\n");

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
				if (temp.contains("}"))
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

	public void addJavaPackageName(File javaFile, String sessionName) throws IOException {

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

	public void updateJavaPackageName(File javaFile, String sessionName) throws IOException {

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

}
