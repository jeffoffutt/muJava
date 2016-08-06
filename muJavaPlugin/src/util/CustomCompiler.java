package util;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.sun.tools.javac.Main;

import mujava.MutationSystem;
import mujava.cli.Util;
import mujava.test.OriginalLoader;

public class CustomCompiler {

	public Class compile(String javaFileName) {
		Class compiledClass = null;
		
		int status = Main.compile(new String[] { javaFileName });

		File classFile = null;
		if (status != 0) {
			Util.Error("Can't compile src file, please compile manually.");
		} else {
			Util.Print("Source file is compiled successfully.");

			classFile = new File(javaFileName.replace(".java", ".class"));
			try {
				FileUtils.copyFile(classFile, new File(MutationSystem.CLASS_PATH + separator + classFile.getName()));
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		try {
			compiledClass = new OriginalLoader().loadClass(classFile.getName().replace(".class", ""));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(classFile.getName() + " loaded successfully");

		return compiledClass;
	}
}
