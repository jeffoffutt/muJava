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
import mujava.op.util.DeclAnalyzer;

/**
 * <p>Generate IHD (Hiding variable deletion) mutants -- delete each 
 *    declaration of an overriding, or hiding variable to causes 
 *    references to that variable to access the variable defined in
 *    the parent.</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class IHD extends DeclAnalyzer
{
   Environment file_env = null;

   /** number of IHD mutant for a target class*/
   private int total = 0;

   /** return number of IHD mutant for a target class */
   public int getTotal()
   {
      return total;
   }

   /** Generate IHD mutant <br>
    *  <i> When to generate</i>: if declared fields have the same name and 
    *         type with the inherited fields
    */
   public void translateDefinition(CompilationUnit comp_unit) throws openjava.mop.MOPException
   {
      OJField[] d_fields = getDeclaredFields();
      OJField[] i_fields = getInheritedFields();
      if ( (d_fields.length == 0) || (i_fields.length == 0) ) 
         return;

      for (int i=0; i<d_fields.length; i++)
      {
         // private fields do not have have no effect although they are hidden.
         if (d_fields[i].getModifiers().isPrivate()) 
        	continue;
         
         for (int j=0; j<i_fields.length; j++)
         {
            if (equalNameAndType(d_fields[i], i_fields[j]))
            {
               // examine equivalency
               if ((d_fields[i].getModifiers().isPublic()) || (!isEquivalent(comp_unit,d_fields[i])) )
               {
                  FieldDeclaration original = d_fields[i].getSourceCode();
                  FieldDeclaration mutant;
                  mutant = (FieldDeclaration)original.makeRecursiveCopy();
                  outputToFile(comp_unit, original, mutant);
                  total++;
               }
               break;
            }
         }
      }
   }

   private boolean isEquivalent(CompilationUnit comp_unit, OJField f)
   {
      IHD_IHI_EqAnalyzer engine = new IHD_IHI_EqAnalyzer(file_env, comp_unit, f.getName());
      try
      {
         comp_unit.accept(engine);
      } catch (ParseTreeException e)
      {
    	  // do nothing
      }
      
      if (engine.isEquivalent()) 
         return true;
      else 
         return false;
   }

   /**
    * Output IHD mutants to files
    * @param comp_unit
    * @param original
    * @param mutant
    */
   public void outputToFile(CompilationUnit comp_unit, FieldDeclaration original, FieldDeclaration mutant)
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
         IHD_Writer writer = new IHD_Writer( mutant_dir, out );
         writer.setMutant(original,mutant);
         comp_unit.accept( writer );
         out.flush();  out.close();
      } catch ( IOException e ) 
      {
         System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) 
      {
         System.err.println( "errors during printing " + f_name );
         e.printStackTrace();
      }
   }

   public IHD( openjava.mop.Environment oj_param0, openjava.mop.OJClass oj_param1, openjava.ptree.ClassDeclaration oj_param2 )
   {
      super( oj_param0, oj_param1, oj_param2 );
      file_env = oj_param0;
   }

   public IHD( java.lang.Class oj_param0, openjava.mop.MetaInfo oj_param1 )
   {
      super( oj_param0, oj_param1 );
   }

}
