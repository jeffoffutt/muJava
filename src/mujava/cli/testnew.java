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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.beust.jcommander.JCommander;

import mujava.MutationSystem;
/**
 * <p>
 * Description: Create new test session API for command line version
 * Creates a new test session. It means, it creates all the files necessary
 * to run a test on a java program. 
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0
 */
public class testnew {
	static String sessionName = new String();
	static String muJavaHomePath = new String();

	public static void main(String[] args) throws IOException {
		testnewCom jct = new testnewCom();
		String[] argv = { "Flower", "/Users/dmark/mujava/src/Flower" };
		new JCommander(jct, args);

		muJavaHomePath = Util.loadConfig();
		// muJavaHomePath= "/Users/dmark/mujava";

		// check if debug mode
		if (jct.isDebug() || jct.isDebugMode()) {
			Util.debug = true;
		}
		System.out.println(jct.getParameters().size());
		sessionName = jct.getParameters().get(0); // set first parameter as the
													// session name

		ArrayList<String> srcFiles = new ArrayList<>();

		for (int i = 1; i < jct.getParameters().size(); i++) {
			srcFiles.add(jct.getParameters().get(i)); // retrieve all src file
														// names from parameters
		}

		// get all existing session name
		File folder = new File(muJavaHomePath);
		if (!folder.isDirectory()) {
			Util.Error("ERROR: cannot locate the folder specified in mujava.config");
			return;
		}
		File[] listOfFiles = folder.listFiles();
		// null checking
		// check the specified folder has files or not
		if (listOfFiles==null)
		{
			Util.Error("ERROR: no files in the muJava home folder "+muJavaHomePath);
			return;
		}
		List<String> fileNameList = new ArrayList<>();
		for (File file : listOfFiles) {
			fileNameList.add(file.getName());
		}

		// check if the session is new or not
		if (fileNameList.contains(sessionName)) {
			Util.Error("Session already exists.");
		} else {
			// create sub-directory for the session
			setupSessionDirectory(sessionName);

			// move src files into session folder
			for (String srcFile : srcFiles) {
				// new (dir, name)
				// check abs path or not
				
				// need to check if srcFile has .java at the end or not
				if (srcFile.length() > 5) {
					if (srcFile.substring(srcFile.length() - 5).equals(".java")) // name has .java at the end, e.g. cal.java
					{
						// delete .java, e.g. make it cal
						srcFile = srcFile.substring(0, srcFile.length() - 5);
					}
				}

				File source = new File(srcFile + ".java");
				
				if (!source.isAbsolute()) // relative path, attach path, e.g. cal.java, make it c:\mujava\cal.java
				{
					source = new File(muJavaHomePath + "/src" + java.io.File.separator + srcFile + ".java");

				} 


				File desc = new File(muJavaHomePath + "/" + sessionName + "/src");
				FileUtils.copyFileToDirectory(source, desc);

				// compile src files
				// String srcName = "t";
				boolean result = compileSrc(srcFile);
				if (result)
					Util.Print("Session is built successfully.");
			}

		}

		// System.exit(0);
	}

	private static void setupSessionDirectory(String sessionName) {
		String session_dir_path = muJavaHomePath + "/" + sessionName;
		// Util.Print(mutant_dir_path);
		File mutant_path = new File(session_dir_path);

		// build the session folders

		makeDir(new File(session_dir_path));
		makeDir(new File(session_dir_path + "/src"));
		makeDir(new File(session_dir_path + "/classes"));
		makeDir(new File(session_dir_path + "/result"));
		makeDir(new File(session_dir_path + "/testset"));

	}

	/*
	 * compile the src and put it into session's classes folder
	 */
	public static boolean compileSrc(String srcName) {
		String session_dir_path = muJavaHomePath + "/" + sessionName;

		com.sun.tools.javac.Main javac = new com.sun.tools.javac.Main();

		// check if absolute path or not
		File file = new File(srcName + ".java");
		String src_dir_path = new String();
		if (!file.isAbsolute()) {
			src_dir_path = muJavaHomePath + "/src" + java.io.File.separator + srcName + ".java";
		} else {
			src_dir_path = srcName + ".java";
		}

		String[] args = new String[] { "-d", session_dir_path + "/classes", src_dir_path };
		int status = javac.compile(args);

		if (status != 0) {
			Util.Error("Can't compile src file, please compile manually.");
			return false;
		} else {
			Util.Print("Source file is compiled successfully.");
		}
		return true;

	}

	/*
	 * build the directory
	 */

	static void makeDir(File dir) {
		Util.DebugPrint("\nMake " + dir.getAbsolutePath() + " directory...");
		boolean newly_made = dir.mkdir();
		if (!newly_made) {
			Util.Error(dir.getAbsolutePath() + " directory exists already.");
		} else {
			Util.DebugPrint("Making " + dir.getAbsolutePath() + " directory " + " ...done.");
		}
	}


}
