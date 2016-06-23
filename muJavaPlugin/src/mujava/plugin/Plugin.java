package mujava.plugin;

import static java.io.File.separator;
import static mujava.cli.testnew.sessionName;
import static mujava.cli.testnew.muJavaHomePath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.beust.jcommander.JCommander;

import mujava.cli.Util;
import mujava.cli.testnew;
import mujava.cli.testnewCom;

public class Plugin {

	public static void main(String[] args) {

		if (args[0] == null) {
			new IllegalArgumentException("Please provide session name as argument.");
		}

		sessionName = args[0];
		muJavaHomePath = System.getProperty("user.dir") + separator + "src" + separator + "mujava";

		// Source directory
		File sourceDir = new File(muJavaHomePath + separator + "source");

		// Create source directory if it does not exist.
		if (!sourceDir.exists()) {

			sourceDir.mkdir();

			throw new RuntimeException("ERROR: No source package found. \n"
					+ "A new source package has been created with the name mujava.source. \n"
					+ "Please put all the java source files in this package."
					+ "NOTE: There should be only one file with main() method.");

		}

		// Session directory
		File sessionDir = new File(
				System.getProperty("user.dir") + separator + "src" + separator + "mujava" + separator + sessionName);

		// Make sure session directory does not exist.
		if (sessionDir.exists()) {

			new RuntimeException("Session directory exists!!! \n" + "Please make sure session name is not reused.");

		}

		// accept java files in the mujava.source folder
		List<File> sourceFiles = Arrays.asList(sourceDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname != null && pathname.getName().toLowerCase().endsWith(".java")) {
					return true;
				}
				return false;
			}
		}));

		String[] argv = new String[sourceFiles.size() + 1];

		argv[0] = args[0];

		int argsCounter = 1;

		// create sub-directory for the session
		testnew.setupSessionDirectory(sessionName);

		// Get all java source files under package mujava.source
		for (File file : sourceFiles) {
			System.out.println("Found source file - " + file.getName());

			argv[argsCounter] = file.getAbsolutePath();
			argsCounter++;

			// copy to session directory
			try {
				
				File sessionSrcDir =new File(sessionDir.getAbsolutePath() + separator + "src");
				FileUtils.copyFileToDirectory(file, sessionSrcDir);
			
				updateJavaPackageName(new File(sessionSrcDir+separator+file.getName()), sessionName);
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println(sessionDir.getAbsolutePath() + separator + "src" + separator
					+ file.getName().substring(0, file.getName().length() - ".java".length()));
			boolean result = testnew.compileSrc(sessionDir.getAbsolutePath() + separator + "src" + separator
					+ file.getName().substring(0, file.getName().length() - ".java".length()));
			System.out.println(result);
			if (result)
				Util.Print("Session is built successfully.");
		}

		testnewCom jct = new testnewCom();

		if (jct.isDebug() || jct.isDebugMode()) {
			Util.debug = true;
		}

		new JCommander(jct, argv);

		
		
		
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
	               line = line.replace("mujava.source", "mujava."+sessionName+".src");
	            bw.write(line+"\n");
	         }
	      } finally {
	            if(br != null)
	               br.close();
	         
	            if(bw != null)
	               bw.close();	         
	      }
	      // Once everything is complete, delete old file..
	      
	      javaFile.delete();

	      // And rename tmp file's name to old file name
	      File newFile = new File(tmpFileName);
	      newFile.renameTo(javaFile);

	   }
	/*
	 * String[] argv = { "-all", "-debug", "Flower" }; genmutesCom jct = new
	 * genmutesCom();
	 */

}
