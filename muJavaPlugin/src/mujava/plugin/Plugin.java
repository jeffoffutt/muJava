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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.beust.jcommander.JCommander;

import mujava.MutationSystem;
import mujava.cli.Util;
import mujava.cli.genmutesCom;
import mujava.cli.testnew;
import mujava.cli.testnewCom;
import mujava.cli.genmutes;

public class Plugin {

	public static void main(String[] args) {

		if (args == null || args.length < 2) {
			new IllegalArgumentException("Please provide session name and list of mutation operators as argument.");
		}
				
		sessionName = args[0];
		muJavaHomePath = System.getProperty("user.dir") + separator + "src" + separator + "mujava";
		mujava.cli.genmutes.muJavaHomePath = muJavaHomePath;
		
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

				File sessionSrcDir = new File(sessionDir.getAbsolutePath() + separator + "src");
				FileUtils.copyFileToDirectory(file, sessionSrcDir);

				updateJavaPackageName(new File(sessionSrcDir + separator + file.getName()), sessionName);

			} catch (IOException e) {
				e.printStackTrace();
			}


			System.out.println("argv: "+argv);
			
			System.out.println(sessionDir.getAbsolutePath() + separator + "src" + separator
					+ file.getName().substring(0, file.getName().length() - ".java".length()));
			boolean result = testnew.compileSrc(sessionDir.getAbsolutePath() + separator + "src" + separator
					+ file.getName().substring(0, file.getName().length() - ".java".length()));
			System.out.println(result);
			if (result)
				Util.Print("Session is built successfully.");
		}

		testnewCom testNewJct = new testnewCom();

		if (testNewJct.isDebug() || testNewJct.isDebugMode()) {
			Util.debug = true;
		}

		new JCommander(testNewJct, argv);

		genmutesCom genMutesJct = new genmutesCom();
		
		// get all mutation operators selected
				HashMap<String, List<String>> ops = new HashMap<String, List<String>>();

		
		JCommander jCommander = new JCommander(genMutesJct, args);

		
		String[] paras = new String[] { "1", "0" };
		if (genMutesJct.getAll()) // all is selected, add all operators
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
			// ops.put("SDL", genMutesJct.getAll());

		} else { // if not all, add selected ops to the list
			if (genMutesJct.getAORB()) {
				ops.put("AORB", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getAORS()) {
				ops.put("AORS", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getAOIU()) {
				ops.put("AOIU", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getAOIS()) {
				ops.put("AOIS", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getAODU()) {
				ops.put("AODU", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getAODS()) {
				ops.put("AODS", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getROR()) {
				ops.put("ROR", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getCOR()) {
				ops.put("COR", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getCOD()) {
				ops.put("COD", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getCOI()) {
				ops.put("COI", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getSOR()) {
				ops.put("SOR", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getLOR()) {
				ops.put("LOR", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getLOI()) {
				ops.put("LOI", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getLOD()) {
				ops.put("LOD", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getASRS()) {
				ops.put("ASRS", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getSDL()) {
				ops.put("SDL", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getVDL()) {
				ops.put("VDL", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getODL()) {
				ops.put("ODL", new ArrayList<String>(Arrays.asList(paras)));
			}
			if (genMutesJct.getCDL()) {
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
		genmutes.setJMutationStructureAndSession(sessionName);
		// MutationSystem.setJMutationStructureAndSession(sessionName);
		try {
			MutationSystem.recordInheritanceRelation();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// generate mutants
		
		String[] fileNames = new String[sourceFiles.size()];
		int i=0;
		for(File file:sourceFiles){
			fileNames[i]=file.getName();
			i++;
		}
		
		genmutes.generateMutants(fileNames, ops);
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
					//line = line.replace("mujava.source", "mujava." + sessionName + ".src");
					line="";
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
	/*
	 * String[] argv = { "-all", "-debug", "Flower" }; genmutesCom jct = new
	 * genmutesCom();
	 */

}
