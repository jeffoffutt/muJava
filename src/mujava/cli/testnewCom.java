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

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;


/**
 * <p>
 * Description: Pre-defined arguments options for testnew command
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0
 * 
 */


class testnewCom {
	  @Parameter
	  public List<String> parameters = new ArrayList<String>();
	 
	  @Parameter(names = "-test", description = "Debug mode")
	  private boolean test = false;
	  
	  @Parameter(names = "-research", description = "Debug mode")
	  private boolean research = false;
	  
	  @Parameter(names = "-debug", description = "Debug mode")
	  private boolean debug = false;
	  
	  @Parameter(names = "-d", description = "Debug mode")
	  private boolean debugMode = false;

	public List<String> getParameters()
	{
		return parameters;
	}

	public void setParameters(List<String> parameters)
	{
		this.parameters = parameters;
	}

	public boolean isTest()
	{
		return test;
	}

	public void setTest(boolean test)
	{
		this.test = test;
	}

	public boolean isResearch()
	{
		return research;
	}

	public void setResearch(boolean research)
	{
		this.research = research;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	  









}