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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
/**
 * <p>
 * Description: Uitility class for command line version
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0
 * 
 */
public class Util
{
	public static int Total = 0;
	public static boolean debug = false;
	
    //all mutants in a class  	
	public static Vector mutants = new Vector();
	//killed mutants in a class
	public static Vector killed_mutants = new Vector();
	//live mutants in a class
	public static Vector live_mutants = new Vector();
	//eq mutants in a class
	public static Vector eq_mutants = new Vector();
	
	public static Map<String, String> finalTestResultsMap =  new HashMap<>();
	public static Map<String, String> finalMutantResultsMap = new HashMap<>();
	
	public static void setUpVectors()
	{
	    //all mutants in a class  	
		 mutants = new Vector();
		//killed mutants in a class
		 killed_mutants = new Vector();
		//live mutants in a class
		 live_mutants = new Vector();
		//eq mutants in a class
		 eq_mutants = new Vector();
	}
	
	public static void setUpMaps()
	{
		finalMutantResultsMap = new HashMap<>();
		finalMutantResultsMap = new HashMap<>();
	}
	
	public static void Error(String errorMsg)
	{
		
		System.err.println(errorMsg);
	}
	
	public static void DebugPrint(String msg)
	{
		if(debug)
		System.out.println(msg);
	}
	
	public static void Print(String msg)
	{
		System.out.println(msg);
	}
	
	/*
	 * load config file
	 */
	static String loadConfig() throws IOException {
		FileInputStream inputStream = new FileInputStream("mujavaCLI.config");

		String input = IOUtils.toString(inputStream);
		String[] inputs = input.split("\n");
		String path = new String();
		if (inputs.length == 1) // only one line of configuration
		{
			path = inputs[0];
			path = path.replace("MuJava_HOME=", "");
			path = path.replace("\n", "");

		} else if (inputs.length == 2) // 2 lines of configuration
		{
			path = inputs[0];
			String debug = inputs[1];
			path = path.replace("MuJava_HOME=", "");
			path = path.replace("\n", "");
			debug = debug.replace("Debug_mode=", "");
			debug = debug.replace("\n", "");
			// load debug mode from config file, and set it
			if (debug.equals("true"))
				Util.debug = true;
			else {
				Util.debug = false;
			}
		}

		inputStream.close();

		return path;
	}
	
	
	
}
