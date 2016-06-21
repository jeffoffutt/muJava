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
 * Description: Pre-defined arguments options for markequiv command
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0  
 */
class markequivCom {
	  @Parameter
	  private List<String> parameters = new ArrayList<String>();
	 
	  @Parameter(names = "-name", arity = 1,  description = "Class name")
	  private String name;
	  
	  @Parameter(names = "-mutant", arity = 1,  description = "Mutant name")
	  private String mutant;

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMutant() {
		return mutant;
	}

	public void setMutant(String mutant) {
		this.mutant = mutant;
	}
	  
	  
	  
	  
	}