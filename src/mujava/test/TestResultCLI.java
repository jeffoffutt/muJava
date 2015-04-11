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


package mujava.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import mujava.MutationSystem;
import mujava.TestExecuterCLI;
import mujava.cli.Util;
import mujava.cli.runmutes;

/**
 * <p>
 * Description: New test result class build exclusively for command line version
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0
 */

public class TestResultCLI extends TestResult {

	public Vector eq_mutants = new Vector();

	public String path = new String();

	public void outputToFile(String method) throws IOException {
		//displayResultConsole();

		File f = new File(path);

		if (runmutes.mode.equals("fresh")) // fresh mode, need to save with time
											// stamp
		{
			if(!TestExecuterCLI.methodList.contains(method))
				{System.out.println("ERROR");return;}
			
			TestExecuterCLI.methodList.remove(method);
			
			// merge results
			Util.mutants.addAll(mutants);
			Util.killed_mutants.addAll(killed_mutants);
			Util.live_mutants.addAll(live_mutants);
			Util.eq_mutants.addAll(eq_mutants);
			
			// if no methods left, save file
			// else continue
			if(TestExecuterCLI.methodList.size()==0)
			{	
				// get time
				Calendar nowtime = new GregorianCalendar();

				path = path + "_" + nowtime.get(Calendar.YEAR) + "_" + (nowtime.get(Calendar.MONTH) + 1) + "_"
						+ nowtime.get(Calendar.DATE) + "_" + nowtime.get(Calendar.HOUR) + "_"
						+ nowtime.get(Calendar.MINUTE) + "_" + nowtime.get(Calendar.SECOND);

				f = new File(path);

				FileOutputStream fout = new FileOutputStream(f);
				StringBuffer fileContent = new StringBuffer();

				fileContent.append("killed mutants (" + Util.killed_mutants.size() + "): ");
				for (Object object : Util.killed_mutants) {
					fileContent.append(object.toString() + ", ");
				}
				fileContent.append("\r\n");

				fileContent.append("live mutants (" + Util.live_mutants.size() + "): ");
				for (Object object : Util.live_mutants) {
					fileContent.append(object.toString() + ", ");
				}
				fileContent.append("\r\n");

				fileContent.append("equivalent mutants (" + Util.eq_mutants.size() + "): ");
				for (Object object : Util.eq_mutants) {
					fileContent.append(object.toString() + ", ");
				}

				fout.write(fileContent.toString().getBytes("utf-8"));
				fout.close();
				displayResultConsole();
				Util.setUpVectors(); // reset after finish
				
			}

			return;

		}

		if (!f.exists()) // no file exist, save to file without reading history
							// results
		{
			FileOutputStream fout = new FileOutputStream(f);
			StringBuffer fileContent = new StringBuffer();

			fileContent.append("killed mutants (" + killed_mutants.size() + "): ");
			for (Object object : killed_mutants) {
				fileContent.append(object.toString() + ", ");
			}
			fileContent.append("\r\n");

			fileContent.append("live mutants (" + live_mutants.size() + "): ");
			for (Object object : live_mutants) {
				fileContent.append(object.toString() + ", ");
			}
			fileContent.append("\r\n");

			fileContent.append("equivalent mutants (" + eq_mutants.size() + "): ");
			for (Object object : eq_mutants) {
				fileContent.append(object.toString() + ", ");
			}

			fout.write(fileContent.toString().getBytes("utf-8"));
			fout.close();
			displayResultConsole();
			return;
		}

		if (runmutes.mode.equals("default")) {
			if (!f.exists()) // no file exist, new run
			{
				FileOutputStream fout = new FileOutputStream(f);
				StringBuffer fileContent = new StringBuffer();

				fileContent.append("killed mutants (" + killed_mutants.size() + "): ");
				for (Object object : killed_mutants) {
					fileContent.append(object.toString() + ", ");
				}
				fileContent.append("\r\n");

				fileContent.append("live mutants (" + live_mutants.size() + "): ");
				for (Object object : live_mutants) {
					fileContent.append(object.toString() + ", ");
				}
				fileContent.append("\r\n");

				fileContent.append("equivalent mutants (" + eq_mutants.size() + "): ");
				for (Object object : eq_mutants) {
					fileContent.append(object.toString() + ", ");
				}

				fout.write(fileContent.toString().getBytes("utf-8"));
				fout.close();
				displayResultConsole();
				return;

			} else { // second run, need read file first
				TestResultCLI oldTestResult = new TestResultCLI();
				oldTestResult.setPath(path);
				oldTestResult.getResults();
				for (Object liveMutant : live_mutants)
				{
					if(!oldTestResult.live_mutants.contains((String) liveMutant))
						oldTestResult.live_mutants.add(liveMutant);
				}
				for (Object killedMutant : killed_mutants) // for each new
															// killed mutant,
															// move it from live
															// to killed
				{
					if (oldTestResult.live_mutants.contains((String) killedMutant))
						oldTestResult.live_mutants.remove(killedMutant);
						
					if (!oldTestResult.killed_mutants.contains((String) killedMutant))
						oldTestResult.killed_mutants.add(killedMutant);
				}

				// if in eq mode, an eq mutant is killed, also need to move to
				// killed mutant set
				if (runmutes.runEq) {
					for (Object killedMutant : killed_mutants) // for each new
																// killed
																// mutant, move
																// it from live
																// to killed
					{
						if (oldTestResult.eq_mutants.contains((String) killedMutant)) {
							oldTestResult.eq_mutants.remove(killedMutant);
							oldTestResult.killed_mutants.add(killedMutant);
						}
					}
				}

				FileOutputStream fout = new FileOutputStream(f);
				StringBuffer fileContent = new StringBuffer();

				fileContent.append("killed mutants (" + oldTestResult.killed_mutants.size() + "): ");
				for (Object object : oldTestResult.killed_mutants) {
					fileContent.append(object.toString() + ", ");
				}
				fileContent.append("\r\n");

				fileContent.append("live mutants (" + oldTestResult.live_mutants.size() + "): ");
				for (Object object : oldTestResult.live_mutants) {
					fileContent.append(object.toString() + ", ");
				}
				fileContent.append("\r\n");

				fileContent.append("equivalent mutants (" + oldTestResult.eq_mutants.size() + "): ");
				for (Object object : oldTestResult.eq_mutants) {
					fileContent.append(object.toString() + ", ");
				}

				fout.write(fileContent.toString().getBytes("utf-8"));
				fout.close();
				displayResultConsole();
				return;

			}
		}

		if (runmutes.mode.equals("dead")) {
			if (!f.exists()) // no file exist, new run
			{
				FileOutputStream fout = new FileOutputStream(f);
				StringBuffer fileContent = new StringBuffer();

				fileContent.append("killed mutants (" + killed_mutants.size() + "): ");
				for (Object object : killed_mutants) {
					fileContent.append(object.toString() + ", ");
				}
				fileContent.append("\r\n");

				fileContent.append("live mutants (" + live_mutants.size() + "): ");
				for (Object object : live_mutants) {
					fileContent.append(object.toString() + ", ");
				}
				fileContent.append("\r\n");

				fileContent.append("equivalent mutants (" + eq_mutants.size() + "): ");
				for (Object object : eq_mutants) {
					fileContent.append(object.toString() + ", ");
				}

				fout.write(fileContent.toString().getBytes("utf-8"));
				fout.close();
				displayResultConsole();
				return;

			} else { // second run, need read file first
				TestResultCLI oldTestResult = new TestResultCLI();
				oldTestResult.setPath(path);
				oldTestResult.getResults();
				//
				for (Object liveMutant : live_mutants)
				{
					if(!oldTestResult.live_mutants.contains((String) liveMutant))
						oldTestResult.live_mutants.add(liveMutant);
				}
				
				for (Object killedMutant : killed_mutants) // for each new
															// killed mutant,
															// move it from live
															// to killed
				{
					if (oldTestResult.live_mutants.contains((String) killedMutant))
						oldTestResult.live_mutants.remove(killedMutant);
						
					if (!oldTestResult.killed_mutants.contains((String) killedMutant))
						oldTestResult.killed_mutants.add(killedMutant);
				}
				

				// if in eq mode, an eq mutant is killed, also need to move to
				// killed mutant set
				if (runmutes.runEq) {
					for (Object killedMutant : killed_mutants) // for each new
																// killed
																// mutant, move
																// it from live
																// to killed
					{
						if (oldTestResult.eq_mutants.contains((String) killedMutant))
							oldTestResult.eq_mutants.remove(killedMutant);
						if (!oldTestResult.killed_mutants.contains((String) killedMutant))
							oldTestResult.killed_mutants.add(killedMutant);

					}
				}

				FileOutputStream fout = new FileOutputStream(f);
				StringBuffer fileContent = new StringBuffer();

				fileContent.append("killed mutants (" + oldTestResult.killed_mutants.size() + "): ");
				for (Object object : oldTestResult.killed_mutants) {
					fileContent.append(object.toString() + ", ");
				}
				fileContent.append("\r\n");

				fileContent.append("live mutants (" + oldTestResult.live_mutants.size() + "): ");
				for (Object object : oldTestResult.live_mutants) {
					fileContent.append(object.toString() + ", ");
				}
				fileContent.append("\r\n");

				fileContent.append("equivalent mutants (" + oldTestResult.eq_mutants.size() + "): ");
				for (Object object : oldTestResult.eq_mutants) {
					fileContent.append(object.toString() + ", ");
				}

				fout.write(fileContent.toString().getBytes("utf-8"));
				fout.close();
				displayResultConsole();
				return;

			}
		}

	}

	private void displayResultConsole() throws IOException {
		File f = new File(path);

		// if file exist, need to read file
		if (f.exists()) {
			TestResultCLI oldTestResult = new TestResultCLI();
			oldTestResult.setPath(path);
			oldTestResult.getResults();
			for (Object killedMutant : killed_mutants) // for each new killed
														// mutant, move it from
														// live to killed
			{
				if (oldTestResult.live_mutants.contains((String) killedMutant)) {
					oldTestResult.live_mutants.remove(killedMutant);
				}
				if (!oldTestResult.killed_mutants.contains((String) killedMutant))
					oldTestResult.killed_mutants.add(killedMutant);
			}
			int total = oldTestResult.killed_mutants.size() + oldTestResult.live_mutants.size()
					+ oldTestResult.eq_mutants.size();
			Util.Print("\nTotal mutants killed: " + oldTestResult.killed_mutants.size());
			Util.Print("Total mutants: " + total);
			double ms = (double)oldTestResult.killed_mutants.size() / (double)total;
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(4);
			Util.Print("Mutation Score: " + df.format(ms));
		} else {// file not exist, directly display
			int total = killed_mutants.size() + live_mutants.size() + eq_mutants.size();
			Util.Print("\nTotal mutants killed: " + killed_mutants.size());
			Util.Print("Total mutants: " + total);
			double ms = (double)killed_mutants.size() / (double)total;
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(4);
			Util.Print("Mutation Score: " + df.format(ms));
		}
		Util.Print("Please look at the result files (mutant_list and result_list.csv) for details."
				+ " \nUse \"markequiv\" command to mark equivalent mutants.");

	}

	public void getResults() throws IOException {
		String s = null;
		StringBuffer sb = new StringBuffer();
		File f = new File(path);
		if (!f.exists()) {
			System.out.println("can't find the mutant result file");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		while ((s = br.readLine()) != null) {
			sb.append(s);

			// : divide; , divide
			String[] temp = s.split(":\\s+");
			Util.DebugPrint(temp[0]);
			if (temp[0].contains("killed") && temp.length > 1) {
				List<String> killed_mutantsList = Arrays.asList(temp[1].split(",\\s+"));
				killed_mutants = new Vector(killed_mutantsList);
			} else if (temp[0].contains("live") && temp.length > 1) {
				List<String> live_mutantsList = Arrays.asList(temp[1].split(",\\s+"));
				live_mutants = new Vector(live_mutantsList);
			} else if (temp[0].contains("equivalent") && temp.length > 1) {
				List<String> eq_mutantsList = Arrays.asList(temp[1].split(",\\s+"));
				eq_mutants = new Vector(eq_mutantsList);
			}

		}

	}

	public void markEqvl(String eqMutant) throws IOException {

		if (!live_mutants.contains(eqMutant))
			return;
		// erase and update
		live_mutants.remove(eqMutant);
		eq_mutants.add(eqMutant);

		// write file
		outputToFile(" ");

	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
