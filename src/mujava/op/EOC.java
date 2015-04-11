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


package mujava.op;

import java.io.*;
import openjava.mop.*;
import openjava.ptree.*;

/**
 * <p>Generate EOC (Java-specific reference comparison and content assignment replacement) mutants --
 *    check whether the two references point to the same data object in memory, using
 *    the Java convention of an <i>equals()</i> method 
 * </p>
 * <p><i>Example</i>:
 *    boolean b = (f1 == f2); is mutated to boolean b = (f1.equals(f2));  
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class EOC extends mujava.op.util.Mutator
{
   public EOC(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env,comp_unit );
   }

   public void visit( MethodCall p ) throws ParseTreeException
   {
      if (p.getName().equals("equals"))
      {
    	 // do nothing
      }
      else
      {
         super.visit(p);
      }
   }

   public void visit( BinaryExpression p ) throws ParseTreeException 
   {
      if (p.getOperator() == BinaryExpression.EQUAL)
      {
         Expression left = p.getLeft();
         Expression right = p.getRight();

         if (right instanceof Variable)
         {
			Environment env = getEnvironment();
			OJClass bindedtype = env.lookupBind(right.toString());

			if ( (bindedtype!=null) && !(bindedtype.isPrimitive()) )
			{
			   try
			   {
				  OJMethod[] m = bindedtype.getAllMethods();
				  boolean find = true;
				  for (int i=0; i<m.length ; i++)
				  {
					 if (m[i].getName().equals("equals") &&
						 m[i].getDeclaringClass().getName().equals("java.lang.Object"))
					 {
				        find = false;
				  	 }
				  }
				  if (find)
				  {
					 String mutant = left.toString()+ ".equals(" + right.toString()+ ")";
	  				 outputToFile(p, mutant);
					 return;
				  }
			   } catch(Exception e)
			   {
				  System.err.println(" [error] " + e);
                  e.printStackTrace();
			   }
		    }
         }
      }

      // Normal Action
      Expression newp = this.evaluateDown( p );
      if (newp != p) 
      {
         p.replace( newp );
         return;
      }
      p.childrenAccept( this );
      newp = this.evaluateUp( p );
      if (newp != p)  
    	 p.replace( newp );
   }


   /**
    * Output EOC mutants to files
    * @param original
    */
   public void outputToFile(MethodCall original)
   {
      if (comp_unit == null) 
    	 return;
      
      String f_name;
      num++;
      f_name = getSourceName(this);
      String mutant_dir = getMuantID();

      try 
      {
	     PrintWriter out = getPrintWriter(f_name);
   	     EOC_Writer writer = new EOC_Writer( mutant_dir, out );
	     writer.setMutant(original);
	     comp_unit.accept( writer );
	     out.flush();  
	     out.close();
      } catch ( IOException e ) {
   	     System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
	     System.err.println( "errors during printing " + f_name );
	     e.printStackTrace();
      }
   }

   /**
    * Output EOC mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(BinaryExpression original, String mutant)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName(this);
      String mutant_dir = getMuantID();

      try 
      {
	     PrintWriter out = getPrintWriter(f_name);
	     EOC_Writer writer = new EOC_Writer( mutant_dir, out );
	     writer.setMutant(original, mutant);
	     comp_unit.accept( writer );
	     out.flush();   out.close();
      } catch ( IOException e ) {
	     System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
	     System.err.println( "errors during printing " + f_name );
	     e.printStackTrace();
      }
   }
}
