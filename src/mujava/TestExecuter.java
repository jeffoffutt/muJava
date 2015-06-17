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
 
 
package mujava;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import mujava.test.*;
import mujava.util.*;

import org.junit.*;
import org.junit.internal.RealSystem;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.*;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @update by Nan Li May 2012 integration with JUnit
 * @version 1.0
  */

public class TestExecuter {
  Object lockObject = new Object();

  //int TIMEOUT = 3000;
  int TIMEOUT;
  final int MAX_TRY = 100;

  Class original_executer;
  Object original_obj;        // instancitation of the test set class
  volatile Object mutant_result;


  Class mutant_executer;      // test set class for a mutant
  volatile Object mutant_obj;          // test set object for a mutant

  Method[] testCases;
  volatile Method testcase;

  String whole_class_name;
  String testSet;
  boolean mutantRunning = true;
  
  //original test results
  Map<String, String> originalResults = new HashMap<String, String>();
  //results for mutants
  Map<String, String> mutantResults = null;
  //JUnit test cases
  List<String> junitTests = new ArrayList<String>();
  //result of a test case
  Result result = null;
  //results as to how many mutants are killed by each test  
  Map<String, String> finalTestResults = new HashMap<String, String>();
  //results as to how many tests can kill each single mutant  
  Map<String, String> finalMutantResults = new HashMap<String, String>();
  

  public TestExecuter(String targetClassName) {

    int index = targetClassName.lastIndexOf(".");
    if(index<0){
      MutationSystem.CLASS_NAME = targetClassName;
    }else{
      MutationSystem.CLASS_NAME = targetClassName.substring(index+1,targetClassName.length());
    }

    MutationSystem.DIR_NAME = targetClassName;
    MutationSystem.CLASS_MUTANT_PATH = MutationSystem.MUTANT_HOME+"/"+targetClassName
                                      +"/"+MutationSystem.CM_DIR_NAME;
    MutationSystem.TRADITIONAL_MUTANT_PATH = MutationSystem.MUTANT_HOME+"/"+targetClassName
                                      +"/"+MutationSystem.TM_DIR_NAME;
    MutationSystem.EXCEPTION_MUTANT_PATH = MutationSystem.MUTANT_HOME+"/"+targetClassName
                                      +"/"+MutationSystem.EM_DIR_NAME;

    whole_class_name = targetClassName;

  }

	public void setTimeOut(int msecs){
		TIMEOUT = msecs;
	}

  public boolean readTestSet(String testSetName){
    try{
        testSet = testSetName;
        // Class loader for the original class
        OriginalLoader myLoader = new OriginalLoader();
        System.out.println(testSet);
        original_executer = myLoader.loadTestClass(testSet);
        original_obj = original_executer.newInstance();       // initialization of the test set class
        if(original_obj == null){
          System.out.println("Can't instantiace original object");
          return false;
        }

        // read testcases from the test set class
        testCases = original_executer.getDeclaredMethods();
        if(testCases==null){
          System.out.println(" No test case exist ");
          return false;
        }
    }catch(Exception e){
      System.err.println(e);
      return false;
    }
    return true;

  }

  boolean sameResult(Object result1,Object result2){
    if( !(result1.toString().equals(result2.toString())) ) return false;
    return true;
  }

  public TestResult runClassMutants()  throws NoMutantException,NoMutantDirException{
    MutationSystem.MUTANT_PATH = MutationSystem.CLASS_MUTANT_PATH;
    TestResult test_result = new TestResult();
    runMutants(test_result, "");
    return test_result;
  }

  public TestResult runExceptionMutants()  throws NoMutantException,NoMutantDirException{
    MutationSystem.MUTANT_PATH = MutationSystem.EXCEPTION_MUTANT_PATH;
    TestResult test_result = new TestResult();
    runMutants(test_result, "");
    return test_result;
  }

  public TestResult runTraditionalMutants(String methodSignature)  throws NoMutantException,NoMutantDirException{

    MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
    String original_mutant_path = MutationSystem.MUTANT_PATH;

    TestResult test_result = new TestResult();

    if(methodSignature.equals("All method")){
        try{
          //setMutantPath();
          //computeOriginalTestResults();
          File f = new File(MutationSystem.TRADITIONAL_MUTANT_PATH, "method_list");
          FileReader r = new FileReader(f);
          BufferedReader reader = new BufferedReader(r);
          String readSignature = reader.readLine();
          while(readSignature != null){

            MutationSystem.MUTANT_PATH = original_mutant_path + "/" + readSignature;
            try{
              runMutants(test_result, readSignature);
            }catch(NoMutantException e){
            }
            readSignature = reader.readLine();
          }
          reader.close();
        }catch(Exception e){
          System.err.println("[WARNING] A problem occurred when running the traditional mutants:");
	  System.err.println();
	  e.printStackTrace();
        }
    }else{
      MutationSystem.MUTANT_PATH = original_mutant_path + "/" + methodSignature;
      runMutants(test_result, methodSignature);
    }
    return test_result;
  }
 /**
  * get the result of the test under the mutanted program
  * @deprecated
  * @param mutant
  * @param testcase
  * @throws InterruptedException
  */
  
  void runMutants(Object mutant,Method testcase) throws InterruptedException{
    mutantRunning = true;
    try{
      // testcase execution
      mutant_result = testcase.invoke(mutant_obj,null);
    }catch(Exception e){
      // execption occurred -> abnormal execution
      mutant_result = e.getCause().getClass().getName()+" : "  +e.getCause().getMessage();
    }
    mutantRunning = false;
    synchronized(lockObject){
    lockObject.notify();
    }
    //throw new InterruptedException();
  }

  synchronized void waitUntilAtLeast(long timeOut) throws InterruptedException{
    wait(timeOut);
  }
  
  /**
   * get the mutants for one method based on the method signature
   * @param methodSignature
   * @return
   * @throws NoMutantDirException
   * @throws NoMutantException
   */
  private String[] getMutants (String methodSignature) throws NoMutantDirException, NoMutantException{

	      // Read mutants
	     //System.out.println("mutant_path: " + MutationSystem.MUTANT_PATH);
	     File f = new File(MutationSystem.MUTANT_PATH);
	    
	     if(!f.exists()){
	        System.err.println(" There is no directory for the mutants of " + MutationSystem.CLASS_NAME);
	        System.err.println(" Please generate mutants for " + MutationSystem.CLASS_NAME);
	        throw new NoMutantDirException();
	     }

	      // mutantDirectories match the names of mutants
	     String[] mutantDirectories = f.list(new MutantDirFilter());

	     if(mutantDirectories == null || mutantDirectories.length == 0){
	    	  if(!methodSignature.equals(""))
	    		  System.err.println(" No mutants have been generated for the method " + methodSignature + " of the class" + MutationSystem.CLASS_NAME);
	    	  else
	    		  System.err.println(" No mutants have been generated for the class " + MutationSystem.CLASS_NAME);
	       // System.err.println(" Please check if zero mutant is correct.");
	       // throw new NoMutantException();
	     }  
	  
	return mutantDirectories;	  
  }
  

  /**
   * compute the result of a test under the original program
   */
   public void computeOriginalTestResults(){
	  Debug.println("\n\n======================================== Generating Original Test Results ========================================");	  
	  try{   	  
    	  //initialize the original results to "pass"
    	  //later the results of the failed test cases will be updated
          for(int k = 0;k < testCases.length;k++){
          	Annotation[] annotations = testCases[k].getDeclaredAnnotations();
            	for(Annotation annotation : annotations)
            	{
            		//System.out.println("name: " + testCases[k].getName() + annotation.toString() + annotation.toString().indexOf("@org.junit.Test"));
            		if(annotation.toString().indexOf("@org.junit.Test") != -1){
                		//killed_mutants[k]= "";   // At first, no mutants are killed by each test case
                		originalResults.put(testCases[k].getName(), "pass");
                		junitTests.add(testCases[k].getName());
                		finalTestResults.put(testCases[k].getName(), "");
                		continue;
                	}
            	}
          }
           	 
      	JUnitCore jCore = new JUnitCore();	
      	//result = jCore.runMain(new RealSystem(), "VMTEST1");
    	result = jCore.run(original_executer);

    	//get the failure report and update the original result of the test with the failures
      	List<Failure> listOfFailure = result.getFailures();
  		for(Failure failure: listOfFailure){
  			String nameOfTest = failure.getTestHeader().substring(0, failure.getTestHeader().indexOf("("));
   			String testSourceName = testSet + "." + nameOfTest;
   			
   			//System.out.println("failure message: " + failure.getMessage() + failure.getMessage().equals(""));
   			String[] sb = failure.getTrace().split("\\n");
   			String lineNumber = "";
   			for(int i = 0; i < sb.length;i++){
   				if(sb[i].indexOf(testSourceName) != -1){
   					lineNumber = sb[i].substring(sb[i].indexOf(":") + 1, sb[i].indexOf(")"));   					
   				}
   			}
   			
   			//put the failure messages into the test results
   			if(failure.getMessage() == null)
   				originalResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + "fail");
   			else{
	   			if(failure.getMessage().equals(""))
	   				originalResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + "fail");
	   			else
	   				originalResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + failure.getMessage());
   			}
  			
  		}
  		System.out.println(originalResults.toString());
   
      //  System.out.println(System.getProperty("user.dir"));
      //  System.out.println(System.getProperty("java.class.path"));
      //  System.out.println(System.getProperty("java.library.path"));

      
      } 
	  catch (NoClassDefFoundError e) {
	      System.err.println("Could not find one of the necessary classes for running tests. Make sure that .jar files for hamcrest and junit are in your classpath.");
	      e.printStackTrace();
	      return;
	  }
       catch(Exception e){
    	   System.out.println(e.getMessage());
    	   e.printStackTrace();
      	 
    //    original_results[k] = e.getCause().getClass().getName()+" : "  +e.getCause().getMessage();
   //     Debug.println("Result for " + testName + "  :  " +original_results[k] );
    //    Debug.println(" [warining] " + testName + " generate exception as a result " );
        
         // ----------------------------------
        
     }finally{
          //originalResultFileRead();
     }
      }

   private TestResult runMutants(TestResult tr, String methodSignature) throws NoMutantException,NoMutantDirException{
    try{

      String[] mutantDirectories = getMutants(methodSignature);
      
      int mutant_num = mutantDirectories.length;
      tr.setMutants();
      for(int i = 0;i < mutant_num;i++){
          // set live mutnats
          tr.mutants.add(mutantDirectories[i]);
      }

      // result againg original class for each test case
     // Object[] original_results = new Object[testCases.length];
      // list of the names of killed mutants with each test case
      //String[] killed_mutants = new String[testCases.length];

      Debug.println("\n\n======================================== Executing Mutants ========================================");
      for(int i = 0; i < tr.mutants.size(); i++){
        // read the information for the "i"th live mutant
        String mutant_name = tr.mutants.get(i).toString();
        finalMutantResults.put(mutant_name, "");
        JMutationLoader mutantLoader = new JMutationLoader(mutant_name);
        //mutantLoader.loadMutant();
        mutant_executer = mutantLoader.loadTestClass(testSet);
        mutant_obj = mutant_executer.newInstance();
        Debug.print("  " + mutant_name);
        

        try{
        // Mutants are runned using Thread to detect infinite loop caused by mutation
        Runnable r = new Runnable(){
          public void run(){
            try{
           	 mutantRunning = true;
            	 
            	  //original test results
            	  mutantResults = new HashMap<String, String>();
            	  for(int k = 0;k < testCases.length;k++){
                    	Annotation[] annotations = testCases[k].getDeclaredAnnotations();
                      	for(Annotation annotation : annotations)
                      	{
                      		//System.out.println("name: " + testCases[k].getName() + annotation.toString() + annotation.toString().indexOf("@org.junit.Test"));
                      		if(annotation.toString().indexOf("@org.junit.Test") != -1){
                          		//killed_mutants[k]= "";   // At first, no mutants are killed by each test case
                      			mutantResults.put(testCases[k].getName(), "pass");
                          		continue;
                          	}
                      	}
                    }

            	JUnitCore jCore = new JUnitCore();	
             	result = jCore.run(mutant_executer);
             	
               	List<Failure> listOfFailure = result.getFailures();
           		for(Failure failure: listOfFailure){
           			String nameOfTest = failure.getTestHeader().substring(0, failure.getTestHeader().indexOf("("));
           			String testSourceName = testSet + "." + nameOfTest;
           			
           			//System.out.println(testSourceName);
           			String[] sb = failure.getTrace().split("\\n");
           			String lineNumber = "";
           			for(int i = 0; i < sb.length;i++){
           				//System.out.println("sb-trace: " + sb[i]);
           				if(sb[i].indexOf(testSourceName) != -1){
           					lineNumber = sb[i].substring(sb[i].indexOf(":") + 1, sb[i].indexOf(")"));
           					
           				}
           			}
           			//get the line where the error happens       
           			/*
           			String tempLineNumber = "";
           			if(failure.getTrace().indexOf(testSourceName) != -1){
           				tempLineNumber = failure.getTrace().substring(failure.getTrace().indexOf(testSourceName) + testSourceName.length() + 1, failure.getTrace().indexOf(testSourceName) + testSourceName.length() + 5);
           				System.out.println("tempLineNumber: " + tempLineNumber);
           				lineNumber = tempLineNumber.substring(0, tempLineNumber.indexOf(")"));
           				//System.out.print("LineNumber: " + lineNumber);
           			}	*/
           			//get the test name that has the error and save the failure info to the results for mutants
           			if(failure.getMessage() == null)
           				mutantResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + "fail");
           			else if(failure.getMessage().equals(""))
           				mutantResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + "fail");
           			else
           				mutantResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + failure.getMessage());
           		}
           	
           		 System.out.println(mutantResults.toString());
            	 mutantRunning = false;
            	    synchronized(lockObject){
            	      lockObject.notify();
            	    }
            	
            }catch(Exception e){
              e.printStackTrace();
              //System.out.println("e.getMessage()");
              //System.out.println(e.getMessage());
            }
          }
        };

        Thread t = new Thread(r);
        t.start();

        synchronized(lockObject){
          lockObject.wait(TIMEOUT); // Check out if a mutant is in infinite loop
        }
        if(mutantRunning){
        	//System.out.println("check point4");
          t.interrupt();
          //mutant_result = "time_out: more than " + TIMEOUT + " seconds";
          System.out.println(" time_out: more than " + TIMEOUT + " milliseconds");
         // mutantResults.put(nameOfTest, nameOfTest + ": " + lineNumber + "; " + failure.getMessage());
          
    	  for(int k = 0;k < testCases.length;k++){
          	Annotation[] annotations = testCases[k].getDeclaredAnnotations();
            	for(Annotation annotation : annotations)
            	{
            		//System.out.println("name: " + testCases[k].getName() + annotation.toString() + annotation.toString().indexOf("@org.junit.Test"));
            		if(annotation.toString().indexOf("@org.junit.Test") != -1){
                		//killed_mutants[k]= "";   // At first, no mutants are killed by each test case
            			mutantResults.put(testCases[k].getName(), "time_out: more than " + TIMEOUT + " milliseconds");
                		continue;
                	}
            	}
          }
    	  
        }
      }catch(Exception e){
        mutant_result = e.getCause().getClass().getName()+" : "  +e.getCause().getMessage();
      }
        
      //determine whether a mutant is killed or not
      //update the test report
      boolean sign = false;
      for(int k = 0;k < junitTests.size();k++){
    	  String name = junitTests.get(k);	  
    	  if(!mutantResults.get(name).equals(originalResults.get(name))){
    		  sign = true;		  
    		  //update the final results by tests
    		  if(finalTestResults.get(name).equals(""))
    			  finalTestResults.put(name, mutant_name);
    		  else
    			  finalTestResults.put(name, finalTestResults.get(name) + ", " + mutant_name);
    		  //update the final results by mutants
    		  if(finalMutantResults.get(mutant_name).equals(""))
    			  finalMutantResults.put(mutant_name, name);
    		  else
    			  finalMutantResults.put(mutant_name, finalMutantResults.get(mutant_name) + ", " + name);
    	  }
      }
      if(sign == true)
		  tr.killed_mutants.add(mutant_name);
	  else
		  tr.live_mutants.add(mutant_name);
      
      
        mutantLoader = null;
        mutant_executer = null;
        System.gc();
      }
 
      for(int i = 0;i < tr.killed_mutants.size();i++){
        tr.live_mutants.remove(tr.killed_mutants.get(i));
      }
/*
      System.out.println(" Analysis of testcases ");
      for(int i = 0;i < killed_mutants.length;i++){
        System.out.println("  test " + (i+1) + "  kill  ==> " + killed_mutants[i]);
      }
*/
    }catch(NoMutantException e1){
      throw e1;
    }catch(NoMutantDirException e2){
      throw e2;
    }
    /*catch(ClassNotFoundException e3){
      System.err.println("[Execution 1] " + e3);
      return null;
    }
    */catch(Exception e){
      System.err.println("[Exception 2]" + e);
      return null;
    }
    System.out.println("test report: " + finalTestResults);
    System.out.println("mutant report: " + finalMutantResults);
    return tr;
  }

  void erase_killed_mutants(Vector v){
    System.out.println("Deleting directories of killed mutants");
    for(int i=0;i<v.size();i++){
      System.out.print(v.get(i).toString()+" ");
      erase_directory(v.get(i).toString());
    }
  }

  void erase_directory(String mutant_name){
    File mutant_dir = new File(MutationSystem.MUTANT_PATH+"/"+mutant_name);
    File[] f = mutant_dir.listFiles();
    boolean flag = false;
    for(int i=0;i<f.length;i++){
      while(!flag){
        flag = f[i].delete();
      }
      flag = false;
    }

    while(!flag){
      flag = mutant_dir.delete();
    }
  }
}
