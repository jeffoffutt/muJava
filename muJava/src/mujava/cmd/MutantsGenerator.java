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

import java.io.*;
import mujava.*;
import mujava.util.Debug;
/**
 * <p>
 * Description: 
 * </p>
 * 
 * @author Jeff Offutt and Yu-Seung Ma 
 * @version 1.0
 * 
 */
public class MutantsGenerator {

	static public void generateMutants(String[] file_names){
		generateMutants(file_names,MutationSystem.cm_operators, MutationSystem.tm_operators);
	}

	static public void generateClassMutants(String[] file_names){
		generateMutants(file_names,MutationSystem.cm_operators, null);
	}

	static public void generateClassMutants(String[] file_names, String[] class_ops){
		generateMutants(file_names,class_ops, null);
	}

	static public void generateTraditionalMutants(String[] file_names){
		generateMutants(file_names, null, MutationSystem.tm_operators);
	}

	static public void generateTraditionalMutants(String[] file_names,String[] traditional_ops){
		generateMutants(file_names, null,traditional_ops );
	}

	static public void generateMutants(String[] file_names, String[] class_ops, String[] traditional_ops){

		// file_names = Relative path from MutationSystem.SRC_PATH
		String file_name;
		for(int i=0;i<file_names.length;i++){
			file_name = file_names[i];
			System.out.println(file_name);
			try{
				System.out.println(i+" : " +file_name);
				String temp = file_name.substring(0,file_name.length()-".java".length());
        String class_name="";
        for(int ii=0;ii<temp.length();ii++){
          if( (temp.charAt(ii)=='\\') || (temp.charAt(ii)=='/') ){
            class_name = class_name + ".";
          }else{
            class_name = class_name + temp.charAt(ii);
          }
        }
       // Class c = Class.forName(class_name);

				int class_type = MutationSystem.getClassType(class_name);
				if(class_type==MutationSystem.NORMAL){
				}else if(class_type==MutationSystem.MAIN){
						System.out.println(" -- "  + file_name+ " class contains 'static void main()' method.");
						System.out.println("    Pleas note that mutants are not generated for the 'static void main()' method");
				}else{
					 switch(class_type){
					    case MutationSystem.MAIN :
							        System.out.println(" -- Can't apply because " + file_name+ " contains only 'static void main()' method.");
                      break;
					    case MutationSystem.INTERFACE :
							        System.out.println(" -- Can't apply because " + file_name+ " is 'interface' ");
                      break;
					    case MutationSystem.ABSTRACT :
							        System.out.println(" -- Can't apply because " + file_name+ " is 'abstract' class ");
										  break;
					    case MutationSystem.APPLET :
											System.out.println(" -- Can't apply because " + file_name+ " is 'applet' class ");
											break;
							case MutationSystem.GUI :
											System.out.println(" -- Can't apply because " + file_name+ " is 'GUI' class ");
											break;
					 }
					 deleteDirectory();
					 continue;
				}

        // [2] Apply mutation testing
        setMutationSystemPathFor(file_name);

        //File[] original_files = new File[1];
        //original_files[0] = new File(MutationSystem.SRC_PATH,file_name);
        File original_file = new File(MutationSystem.SRC_PATH,file_name);

        AllMutantsGenerator genEngine;
        Debug.setDebugLevel(3);
        genEngine = new AllMutantsGenerator(original_file,class_ops,traditional_ops);
        genEngine.makeMutants();
        genEngine.compileMutants();
      }catch(OpenJavaException oje){
        System.out.println("[OJException] " + file_name + " " + oje.toString());
        //System.out.println("Can't generate mutants for " +file_name + " because OpenJava " + oje.getMessage());
        deleteDirectory();
      }catch(Exception exp){
        System.out.println("[Exception] " + file_name + " " + exp.toString());
				exp.printStackTrace();
        //System.out.println("Can't generate mutants for " +file_name + " due to exception" + exp.getClass().getName());
        //exp.printStackTrace();
        deleteDirectory();
      }catch(Error er){
        System.out.println("[Error] " + file_names + " " + er.toString());
        //System.out.println("Can't generate mutants for " +file_name + " due to error" + er.getClass().getName());
        deleteDirectory();
      }
		}
	}

  static void setMutationSystemPathFor(String file_name){
    try{
      String temp;
      temp = file_name.substring(0,file_name.length()-".java".length());
      temp = temp.replace('/','.');
      temp = temp.replace('\\','.');
      int separator_index = temp.lastIndexOf(".");
      if(separator_index>=0){
        MutationSystem.CLASS_NAME=temp.substring(separator_index+1,temp.length());
      }else{
        MutationSystem.CLASS_NAME = temp;
      }

      String mutant_dir_path = MutationSystem.MUTANT_HOME+"/"+temp;
      File mutant_path = new File(mutant_dir_path);
      mutant_path.mkdir();

      String class_mutant_dir_path = mutant_dir_path + "/" + MutationSystem.CM_DIR_NAME;
      File class_mutant_path = new File(class_mutant_dir_path);
      class_mutant_path.mkdir();

      String traditional_mutant_dir_path = mutant_dir_path + "/" + MutationSystem.TM_DIR_NAME;
      File traditional_mutant_path = new File(traditional_mutant_dir_path);
      traditional_mutant_path.mkdir();

      String original_dir_path = mutant_dir_path + "/" + MutationSystem.ORIGINAL_DIR_NAME;
      File original_path = new File(original_dir_path);
      original_path.mkdir();

      MutationSystem.CLASS_MUTANT_PATH = class_mutant_dir_path;
      MutationSystem.TRADITIONAL_MUTANT_PATH = traditional_mutant_dir_path;
      MutationSystem.ORIGINAL_PATH = original_dir_path;
      MutationSystem.DIR_NAME = temp;

    }catch(Exception e){
        System.err.println(e);
    }
  }

  static void deleteDirectory(){
    File originalDir = new File(MutationSystem.MUTANT_HOME+"/"+MutationSystem.DIR_NAME
                                + "/" + MutationSystem.ORIGINAL_DIR_NAME);
    while(originalDir.delete()){
    }

    File cmDir = new File(MutationSystem.MUTANT_HOME+"/"+MutationSystem.DIR_NAME
                                + "/" + MutationSystem.CM_DIR_NAME);
    while(cmDir.delete()){}

    File tmDir = new File(MutationSystem.MUTANT_HOME+"/"+MutationSystem.DIR_NAME
                                + "/" + MutationSystem.TM_DIR_NAME);
    while(tmDir.delete()){}

    File myHomeDir = new File(MutationSystem.MUTANT_HOME+"/"+MutationSystem.DIR_NAME);
    while(myHomeDir.delete()){}
  }

}