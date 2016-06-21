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

package mujava.op.basic;

import openjava.mop.*;
import openjava.ptree.*;
import java.io.*;
import mujava.MutationSystem;
/**
 * <p>
 * </p>
 * @author Jeff Offutt and Yu-Seung Ma
 * @version 1.0
  */
public class MethodLevelMutator  extends mujava.op.util.Mutator
{
   String currentMethodSignature = null;

   public MethodLevelMutator(FileEnvironment file_env, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }

   String getMethodSignature(MethodDeclaration p)
   {
       //remover the generics in the return type
	   String temp = p.getReturnType().getName();
       if(temp.indexOf("<") != -1 && temp.indexOf(">")!= -1){
    	   temp = temp.substring(0, temp.indexOf("<")) + temp.substring(temp.lastIndexOf(">") + 1, temp.length());
       }
	  String str = temp + "_" + p.getName() + "(";
      ParameterList pars = p.getParameters();
      
      //the for loop goes through each parameter of a method and return them in a String, separated by comma
      for (int i = 0; i < pars.size(); i++)
      {
    	 //because generics in introduced, the original code does not work anymore
    	 //the code below applies the cheapest solution: ignore generics by removing the contents between '<' and '>'
    	 String tempParameter = pars.get(i).getTypeSpecifier().getName();
    	 if(tempParameter.indexOf("<") >=0 && tempParameter.indexOf(">") >=0){
    		 tempParameter = tempParameter.substring(0, tempParameter.indexOf("<")) + tempParameter.substring(tempParameter.lastIndexOf(">") + 1, tempParameter.length());
    		 str += tempParameter;		
    	 }    		 
    	 else{
    		 str += tempParameter;		 
    	 }

         if (i != (pars.size()-1)) 
        	str += ",";
      }
      str += ")";
      return str;
   }

   String getConstructorSignature(ConstructorDeclaration p)
   {
      String str = p.getName() +"(";
      ParameterList pars = p.getParameters();
      
      //the for loop goes through each parameter of a constructor and return them in a String, separated by comma
      for (int i=0; i<pars.size(); i++)
      {
         /** the original code: str += pars.get(i).getTypeSpecifier().getName();  **/
    	 //because generics in introduced, the original code does not work anymore
     	 //the code below applies the cheapest solution: ignore generics by removing the contents between '<' and '>'
    	  String tempParameter = pars.get(i).getTypeSpecifier().getName(); 
    	  if(tempParameter.indexOf("<") >=0 && tempParameter.indexOf(">") >=0){
     		 tempParameter = tempParameter.substring(0, tempParameter.indexOf("<")) + tempParameter.substring(tempParameter.lastIndexOf(">") + 1, tempParameter.length());
     		 str += tempParameter;		
     	  }    		 
     	  else{
     		 str += tempParameter;		 
     	  }
         
          if (i != (pars.size()-1)) 
        	 str+=",";
      }
      str += ")";
      return str;
   }

   /**
    * Retrieve the source's file name
    */
   public String getSourceName(mujava.op.util.Mutator clazz)
   {
	  // make directory for the mutant
	  String dir_name = MutationSystem.MUTANT_PATH + "/" + currentMethodSignature + "/"
                        + getClassName() + "_" + this.num;
	  File f = new File(dir_name);
	  f.mkdir();

	  // return file name
	  String name;
	  name = dir_name + "/" +  MutationSystem.CLASS_NAME + ".java";
      return name;
   }

   /**
    * Retrieve the source's file name
    */
   public String getSourceName(String op_name)
   {
 	  // make directory for the mutant
	  String dir_name = MutationSystem.MUTANT_PATH + "/" + currentMethodSignature + "/" + op_name + "_" + this.num;
	  File f = new File(dir_name);
	  f.mkdir();

	  // return file name
	  String name;
	  name = dir_name + "/" +  MutationSystem.CLASS_NAME + ".java";
      return name;
   }

   public void visit(MethodDeclaration p) throws ParseTreeException 
   {
      currentMethodSignature = getMethodSignature(p);
      super.visit(p);
   }

   public void visit(ConstructorDeclaration p) throws ParseTreeException 
   {
      currentMethodSignature = getConstructorSignature(p);
      super.visit(p);
   }
} 