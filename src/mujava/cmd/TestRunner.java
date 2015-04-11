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


package mujava.cmd;

import mujava.TestExecuter;
import mujava.test.*;
/**
 * <p>
 * Description: 
 * </p>
 * 
 * @author Jeff Offutt and Yu-Seung Ma 
 * @version 1.0
 * 
 */

public class TestRunner {

  static final boolean CLASS_MODE = true;
	static final boolean TRADITIONAL_MODE = false;

	static TestResult runTest(String targetClassName, String testSetName, int timeout_secs, boolean mode){

    if((targetClassName!=null)&&(testSetName!=null)){
			try{
					TestExecuter test_engine = new TestExecuter(targetClassName);
					test_engine.setTimeOut(timeout_secs);

					// First, read (load) test suite class.
					test_engine.readTestSet(testSetName);

					TestResult test_result = new TestResult();
					if(mode==CLASS_MODE){
						test_result = test_engine.runClassMutants();
					}else{
						test_result = test_engine.runTraditionalMutants("");
					}
					return test_result;
			}catch(Exception e){
					System.err.println(e);
					return null;
			}
		}else{
				System.out.println(" [Error] Please check test target or test suite ");
				return null;
		}

	}

	static TestResult runClassTest(String targetClassName, String testSetName, int timeout_secs){
		return runTest(targetClassName,testSetName,timeout_secs,CLASS_MODE);
	}


	static TestResult runTraditionalTest(String targetClassName, String testSetName, int timeout_secs){
			return runTest(targetClassName,testSetName,timeout_secs,TRADITIONAL_MODE);
	}

}