package util;

import static java.io.File.separator;
import static mujava.cli.testnew.muJavaHomePath;
import static mujava.cli.testnew.sessionName;

import java.io.IOException;

import mujava.cli.genmutes;
import mujava.cli.testnew;

public class MuJavaWrapper {

	private String operatorString;
	private String baseProgram;

	public MuJavaWrapper(String baseProgram, String operatorString) {
		this.baseProgram = baseProgram;
		this.operatorString = operatorString;
	}

	public void createTestSession() {

		String[] argTestNew = { sessionName, baseProgram };
		try {
			testnew.main(argTestNew);
		} catch (IOException e1) {

			System.err.println("Error while creating test session!");
			e1.printStackTrace();
			System.exit(1);
		}

	}

	public void generateMutants() {

		String[] argGenMutes = { operatorString, sessionName };

		try {
			genmutes.main(argGenMutes);
		} catch (Exception e1) {
			System.err.println("Error while generating mutants!");
			e1.printStackTrace();
			System.exit(1);
		}

	}

	public void setMujavaEnv() {
		muJavaHomePath = System.getProperty("user.dir") + separator + "src" + separator + "mujava";
		mujava.cli.genmutes.muJavaHomePath = muJavaHomePath;
		mujava.cli.runmutes.muJavaHomePath = muJavaHomePath;
	}
}
