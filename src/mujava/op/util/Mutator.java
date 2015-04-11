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
package mujava.op.util;

import mujava.*;
import java.io.*;
import openjava.mop.*;
import openjava.ptree.*;

/**
 * <p>File Analyzer for generating mutants </p>
 * @author Yu-Seung Ma
 * @update by Nan Li May 2012 one exception is commented out
 * @version 1.0
  */

public class Mutator extends mujava.openjava.extension.VariableBinder
{
   public int num = 0;
   public CompilationUnit comp_unit = null;
   //-------------------------------------
   public FileEnvironment file_env = null;

   public Mutator( Environment env, CompilationUnit comp_unit ) 
   {
      super( env );
      this.comp_unit = comp_unit;
   }

   //--------------
   protected OJClass getType( Expression p ) throws ParseTreeException 
   {
      OJClass result = null;
      try 
      {
    	 //System.out.println("p: " + p);
    	// System.out.println("getEnvironment() : " + getEnvironment().toString());
	     result = p.getType( getEnvironment() );
	     
      } catch ( Exception e ) {	  
  	       throw new ParseTreeException( e );
    	  // do not throw the exception; otherwise, the program stops generating mutants
  	      //System.out.println("Exception: " + e.getStackTrace().toString());
      }
     
      if (result == null) 
      {
	     System.err.println("cannot resolve the type of expression");
	     System.err.println(p.getClass() + " : " + p);
	     System.err.println(getEnvironment());
	     /*****DebugOut.println(getEnvironment().toString());*/
	     if (p instanceof ArrayAccess) 
	     {
		    ArrayAccess aaexpr = (ArrayAccess) p;
		    Expression refexpr = aaexpr.getReferenceExpr();
		    OJClass refexprtype = null;
		    OJClass comptype = null;
		    try 
		    {
		       refexprtype = refexpr.getType(getEnvironment());
		       comptype = refexprtype.getComponentType();
		    } catch (Exception ex) 
		    {
		       // do nothing
		    }
	   	    System.err.println(refexpr + " : " + refexprtype + " : " + comptype);
	     }
	  }
	  return result;
   }

   protected OJClass getSelfType() throws ParseTreeException 
   {
	  OJClass result;
	  try 
	  {
	     Environment env = getEnvironment();
	     String selfname = env.currentClassName();
	     result = env.lookupClass(selfname);
	  } catch (Exception ex) 
	  {
	     throw new ParseTreeException(ex);
	  }
	  return result;
   }

   protected OJClass getType( TypeName typename ) throws ParseTreeException 
   {
	  OJClass result = null;
	  try 
	  {
	     Environment env = getEnvironment();
	     String qname = env.toQualifiedName(typename.toString());
	     result = env.lookupClass(qname);
	  } catch (Exception ex) {
         throw new ParseTreeException(ex);
	  }
  
	  if (result == null) 
	  {
         System.err.println("unknown type for a type name : " + typename);
      }
	  return result;
   }

   protected OJClass computeRefType( TypeName typename, Expression expr )
	                          throws ParseTreeException
   {
 	  if (typename != null)  
 		 return getType( typename );
	
 	  if (expr != null)  
 		 return getType( expr );
	
 	  return getSelfType();
   }
   
   //-----------------
   /**
    * Return a class name
    */
   public String getClassName()
   {
      Class cc = this.getClass();
      return exclude(cc.getName(),cc.getPackage().getName());
   }

   /**
    * Remove a portion of string from a specific position
    * @param a 
    * @param b  
    * @return
    */
   public String exclude(String a, String b)
   {
      return a.substring(b.length()+1,a.length());
   }

   /**
    * Return an ID of a mutant
    * @return
    */
   public String getMuantID()
   {
	  String str = getClassName()+"_"+this.num;
	  return str;
   }

   /**
    * Return an ID of a given operator name
    * @param op_name
    * @return
    */
   public String getMuantID(String op_name)
   {
	  String str = op_name + "_" + this.num;
	  return str;
   }

   /**
    * Return the source's file name
    * @param op_name
    * @return
    */
   public String getSourceName(String op_name)
   {
	  // make directory for the mutant
	  String dir_name = MutationSystem.MUTANT_PATH + "/" + op_name + "_" + this.num;
	  File f = new File(dir_name);
	  f.mkdir();

  	  // return file name
	  String name;
	  name = dir_name + "/" +  MutationSystem.CLASS_NAME + ".java";
      return name;
   }

   /**
    * Return file name 
    * @param clazz
    * @return
    */
   public String getSourceName(Mutator clazz)
   {
      // make directory for the mutant
	  String dir_name = MutationSystem.MUTANT_PATH + "/" + getClassName() + "_" + this.num;
	  File f = new File(dir_name);
  	  f.mkdir();

  	  // return file name
	  String name;
	  name = dir_name + "/" +  MutationSystem.CLASS_NAME + ".java";
      return name;
   }

   public PrintWriter getPrintWriter(String f_name) throws IOException
   {
      File outfile = new File(f_name);
      FileWriter fout = new FileWriter( outfile );
      PrintWriter out = new PrintWriter( fout );
      return out;
   }

   /**
    * Determine whether two methods return the same type
    * @param m1
    * @param m2
    * @return true - same type
    */
   public boolean sameReturnType(OJMethod m1, OJMethod m2)
   {
      OJClass c1 = m1.getReturnType();
      OJClass c2 = m2.getReturnType();
      return (c1.equals(c2));
   }

   public void visit(MethodDeclaration p) throws ParseTreeException 
   {
	  if (!(p.getName().equals("main"))) 
		 super.visit(p);
   }

   /**
    * Determine whether two method have the same parameter type
    * @param m1
    * @param m2
    * @return true - same type
    */
   public boolean sameParameterType(OJMethod m1, OJMethod m2)
   {
      OJClass[] c1 = m1.getParameterTypes();
      OJClass[] c2 = m2.getParameterTypes();

      if (c1.length == c2.length)
      {
	     for (int i=0; i<c1.length; i++)
	     {
	        if (!(c1[i].equals(c2[i])) ) 
	           return false;
	     }
      }
      else 
      {
	     return false;
      }
      return true;
   }
}
