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

 /**
 * <p>
 * Description: Generating mutants API for command line version
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0  
  */

package mujava.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import mujava.MutationSystem;
import mujava.OpenJavaException;
import mujava.TraditionalMutantsGenerator;
import mujava.TraditionalMutantsGeneratorCLI;

import com.beust.jcommander.JCommander;

public class genmutes {
	// static String sessionName = new String();
	static String muJavaHomePath = new String();

	public static void main(String[] args) throws Exception {
		// System.out.println("test");
		genmutesCom jct = new genmutesCom();
		String[] argv = { "-sdl", "-debug", "cal" }; // development use, when release,
												// comment out this line
		JCommander jCommander = new JCommander(jct, args);

		// check session name
		if (jct.getParameters().size() > 1) {
			Util.Error("Has more parameters than needed.");
			return;
		}

		// set session name
		String sessionName = jct.getParameters().get(0);

		muJavaHomePath = Util.loadConfig();
		// check if debug mode
		if (jct.isDebug()) {
			Util.debug = true;
		}

		// get all existing session name
		File folder = new File(muJavaHomePath);
		// check if the config file has defined the correct folder
		if (!folder.isDirectory()) {
			Util.Error("ERROR: cannot locate the folder specified in mujava.config");
			return;
		}
		File[] listOfFiles = folder.listFiles();
		// null checking
		// check the specified folder has files or not
		if (listOfFiles==null)
		{
			Util.Error("ERROR: no files in the muJava home folder: "+muJavaHomePath);
			return;
		}
		List<String> fileNameList = new ArrayList<>();
		for (File file : listOfFiles) {
			fileNameList.add(file.getName());
		}

		// check if session is already created.
		if (!fileNameList.contains(sessionName)) {
			Util.Error("Session does not exist.");
			return;

		}

		
		// get all files in the session
		File[] file_list = new File[1];

		File sessionFolder = new File(muJavaHomePath + "/" + sessionName + "/src");
//		String[] extensions = new String[] { "txt", "jsp" };
		String[] extensions = new String[] { "java" };
		
		List<File> files = (List<File>) FileUtils.listFiles(sessionFolder, extensions, true);
		
//		for(File file : files)
//		{
//			System.out.println(file.getPath());
//			System.out.println(file.getName());		
//		}
//		
//		return;
	
	
		
//		File[] listOfFilesInSession = sessionFolder.listFiles();
		
		file_list = new File[files.size()];
		for (int i = 0; i < files.size(); i++) {
			file_list[i] = files.get(i);
		}


		// get all mutation operators selected
		HashMap<String, List<String>> ops = new HashMap<String, List<String>>(); // used
																					// for
																					// add
																					// random
																					// percentage
																					// and
																					// maximum


		String[] paras = new String[] { "1", "0" };
		if (jct.getAll()) // all is selected, add all operators
		{

			// if all is selected, all mutation operators are added
			ops.put("AORB", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("AORS", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("AOIU", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("AOIS", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("AODU", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("AODS", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("ROR", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("COR", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("COD", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("COI", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("SOR", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("LOR", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("LOI", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("LOD", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("ASRS", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("SDL", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("ODL", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("VDL", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("CDL", new ArrayList<String>(Arrays.asList(paras)));
			// ops.put("SDL", jct.getAll());

		} else { // if not all, add selected ops to the list
			if (jct.getAORB()) {
				ops.put("AORB", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getAORS()) {
				ops.put("AORS", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getAOIU()) {
				ops.put("AOIU", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getAOIS()) {
				ops.put("AOIS", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getAODU()) {
				ops.put("AODU", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getAODS()) {
				ops.put("AODS", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getROR()) {
				ops.put("ROR", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getCOR()) {
				ops.put("COR", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getCOD()) {
				ops.put("COD", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getCOI()) {
				ops.put("COI", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getSOR()) {
				ops.put("SOR", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getLOR()) {
				ops.put("LOR", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getLOI()) {
				ops.put("LOI", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getLOD()) {
				ops.put("LOD", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getASRS()) {
				ops.put("ASRS", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getSDL()) {
				ops.put("SDL", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getVDL()) {
				ops.put("VDL", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getODL()) {
				ops.put("ODL", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (jct.getCDL()) {
				ops.put("CDL", new ArrayList<String>(Arrays.asList(paras)));
			}
		}

		// add default option "all"
		if (ops.size() == 0) {
			ops.put("AORB", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("AORS", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("AOIU", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("AOIS", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("AODU", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("AODS", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("ROR", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("COR", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("COD", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("COI", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("SOR", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("LOR", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("LOI", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("LOD", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("ASRS", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("SDL", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("ODL", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("VDL", new ArrayList<String>(Arrays.asList(paras)));
			ops.put("CDL", new ArrayList<String>(Arrays.asList(paras)));
		}

		// String[] tradional_ops = ops.toArray(new String[0]);
		// set system
		setJMutationStructureAndSession(sessionName);
		// MutationSystem.setJMutationStructureAndSession(sessionName);
		MutationSystem.recordInheritanceRelation();
		// generate mutants
		generateMutants(file_list, ops);

		//System.exit(0);
		

	}

	private static void setJMutationStructureAndSession(String sessionName) {

		// MutationSystem.SYSTEM_HOME

		muJavaHomePath = muJavaHomePath + "/" + sessionName;
		MutationSystem.SYSTEM_HOME = muJavaHomePath;
		MutationSystem.SRC_PATH = muJavaHomePath + "/src";
		MutationSystem.CLASS_PATH = muJavaHomePath + "/classes";
		MutationSystem.MUTANT_HOME = muJavaHomePath + "/result";
		MutationSystem.TESTSET_PATH = muJavaHomePath + "/testset";

	}


	
	public static void generateMutants(File[] file_list, HashMap<String, List<String>> traditional_ops) {

		for (int i = 0; i < file_list.length; i++) {
			// file_name = ABSTRACT_PATH - MutationSystem.SRC_PATH
			// For example: org/apache/bcel/Class.java
			File file = file_list[i];
			try {
				System.out.println((i + 1) + " : " + file.getName());
				// [1] Examine if the target class is interface or abstract
				// class
				// In that case, we can't apply mutation testing.

				
				// get an absolute path
				String fileFullPath = file.getPath();
				// trim down system path
				String intermediatePath = fileFullPath.replace(MutationSystem.SRC_PATH+"/","");
				// trim down .java extension
				String temp = intermediatePath.substring(0, intermediatePath.length() - ".java".length());
				
				String class_name = "";
				// replace symbols
				for (int j = 0; j < temp.length(); j++) {
					if ((temp.charAt(j) == '\\') || (temp.charAt(j) == '/')) {
						class_name = class_name + ".";
					} else {
						class_name = class_name + temp.charAt(j);
					}
				}

				// need to get package name and file name without .java
				// e.g. computeGPA.gpa
				int class_type = MutationSystem.getClassType(class_name);

				if (class_type == MutationSystem.NORMAL) { // do nothing
				} else if (class_type == MutationSystem.MAIN) {
					System.out.println(" -- " + file.getName() + " class contains 'static void main()' method.");
					System.out
							.println("    Pleas note that mutants are not generated for the 'static void main()' method");
				} else {
					switch (class_type) {
					case MutationSystem.INTERFACE:
						System.out.println(" -- Can't apply because " + file.getName() + " is 'interface' ");
						break;
					case MutationSystem.ABSTRACT:
						System.out.println(" -- Can't apply because " + file.getName() + " is 'abstract' class ");
						break;
					case MutationSystem.APPLET:
						System.out.println(" -- Can't apply because " + file.getName() + " is 'applet' class ");
						break;
					case MutationSystem.GUI:
						System.out.println(" -- Can't apply because " + file.getName() + " is 'GUI' class ");
						break;
					case -1:
						System.out.println(" -- Can't apply because class not found ");
						break;
					}

					deleteDirectory();
					continue;
				}

				
		
				// [2] Apply mutation testing
				setMutationSystemPathFor(intermediatePath);

				File original_file = new File(MutationSystem.SRC_PATH, intermediatePath);

				String[] opArray = traditional_ops.keySet().toArray(new String[0]);

				TraditionalMutantsGeneratorCLI tmGenEngine;
				tmGenEngine = new TraditionalMutantsGeneratorCLI(original_file, opArray);
				tmGenEngine.makeMutants();
				tmGenEngine.compileMutants();
								
			      // Lin add printing total mutants
				// get all file names
				File folder = new File(MutationSystem.MUTANT_HOME + "/" + class_name + "/" + MutationSystem.TM_DIR_NAME);
				File[] listOfMethods = folder.listFiles();

				//ArrayList<String> fileNameList = new ArrayList<>();
				int total_mutants = 0;
				for (File method : listOfMethods) {
					//fileNameList.add(method.getName());
					if(method.isDirectory())
					{
						File[] listOfMutants = method.listFiles();
						total_mutants = total_mutants+listOfMutants.length;
						
					}
				}
								
				
//				File muTotalFile = new File(MutationSystem.MUTANT_PATH,"mutation_log");
//				String strLine;
//		         LineNumberReader lReader = new LineNumberReader(new FileReader(muTotalFile));
//		         int line = 0;
//		         while ((strLine=lReader.readLine()) != null)
//		         {
//		        	 line++;
//		         }
				
			      System.out
					.println("------------------------------------------------------------------");
			      System.out.println("Total mutants gnerated for " + file.getName() +": " + Integer.toString(total_mutants));

		      
			      
			} catch (OpenJavaException oje) {
				System.out.println("[OJException] " + file.getName() + " " + oje.toString());
				// System.out.println("Can't generate mutants for " +file_name +
				// " because OpenJava " + oje.getMessage());
				deleteDirectory();
			} catch (Exception exp) {
				System.out.println("[Exception] " + file.getName() + " " + exp.toString());
				exp.printStackTrace();
				// System.out.println("Can't generate mutants for " +file_name +
				// " due to exception" + exp.getClass().getName());
				// exp.printStackTrace();
				deleteDirectory();
			} catch (Error er) {
				System.out.println("[Error] " + file.getName() + " " + er.toString());
				// System.out.println("Can't generate mutants for " +file_name +
				// " due to error" + er.getClass().getName());
				deleteDirectory();
			}
		}
		// runB.setEnabled(true);
		// parent_frame.cvPanel.refreshEnv();
		// parent_frame.tvPanel.refreshEnv();
		// System.out
		// .println("------------------------------------------------------------------");
		// System.out.println(" All files are handled"); // need to say how many
		// mutants are generated

		
		
	}
	
	
	
	
	
	

	static void deleteDirectory() {
		File originalDir = new File(MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME + "/"
				+ MutationSystem.ORIGINAL_DIR_NAME);
		while (originalDir.delete()) { // do nothing?
		}

		File cmDir = new File(MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME + "/"
				+ MutationSystem.CM_DIR_NAME);
		while (cmDir.delete()) { // do nothing?
		}

		File tmDir = new File(MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME + "/"
				+ MutationSystem.TM_DIR_NAME);
		while (tmDir.delete()) { // do nothing?
		}

		File myHomeDir = new File(MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME);
		while (myHomeDir.delete()) { // do nothing?
		}
	}

	static void setMutationSystemPathFor(String file_name) {
		try {
			String temp;
			temp = file_name.substring(0, file_name.length() - ".java".length());
			temp = temp.replace('/', '.');
			temp = temp.replace('\\', '.');
			int separator_index = temp.lastIndexOf(".");

			if (separator_index >= 0) {
				MutationSystem.CLASS_NAME = temp.substring(separator_index + 1, temp.length());
			} else {
				MutationSystem.CLASS_NAME = temp;
			}

			String mutant_dir_path = MutationSystem.MUTANT_HOME + "/" + temp;
			File mutant_path = new File(mutant_dir_path);
			mutant_path.mkdir();

			String class_mutant_dir_path = mutant_dir_path + "/" + MutationSystem.CM_DIR_NAME;
			File class_mutant_path = new File(class_mutant_dir_path);
			class_mutant_path.mkdir();

			String traditional_mutant_dir_path = mutant_dir_path + "/" + MutationSystem.TM_DIR_NAME;
			File traditional_mutant_path = new File(traditional_mutant_dir_path);
			traditional_mutant_path.mkdir();

			String original_dir_path = mutant_dir_path + "/" + MutationSystem.ORIGINAL_DIR_NAME;
			File original_path = new File(original_dir_path);
			original_path.mkdir();

			MutationSystem.CLASS_MUTANT_PATH = class_mutant_dir_path;
			MutationSystem.TRADITIONAL_MUTANT_PATH = traditional_mutant_dir_path;
			MutationSystem.ORIGINAL_PATH = original_dir_path;
			MutationSystem.DIR_NAME = temp;
		} catch (Exception e) {
			System.err.println(e);
		}
		
		
		
		
		
		
	}

}
