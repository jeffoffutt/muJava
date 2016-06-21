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
 * <p>Generate IHI (Hiding variable insertion) mutants -- 
 *    insert a declaration to hide the declaration of 
 *    each variable declared in an ancestor</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class IHI extends DeclAnalyzer
{
   Environment file_env = null;

   public void translateDefinition(CompilationUnit comp_unit) throws openjava.mop.MOPException
   {
      OJField[] d_fields = getDeclaredFields();
      OJField[] i_fields = getInheritedFields();
      if ( i_fields.length == 0 ) 
    	 return;

      for (int i=0; i<i_fields.length; ++i)
      {
         // private or final field do not have any hiding side-effect
         if (i_fields[i].getModifiers().isPrivate()) 
        	continue;

         if (i_fields[i].getModifiers().isFinal()) 
        	continue;

         boolean isHidden = false;
         for (int j=0; j<d_fields.length; ++j)
         {
            if(equalNameAndType(i_fields[i],d_fields[j]))
            {
               isHidden = true;
               break;
            }
         }
         
         if (!isHidden)
         {
            if(i_fields[i].getModifiers().isPublic() || !isEquivalent(comp_unit,i_fields[i]))
            {
               try
               {
                  ModifierList modlist = new ModifierList();
                  OJModifier modif = i_fields[i].getModifiers();
                  TypeName tname = TypeName.forOJClass( i_fields[i].getType() );
                  modlist.add( modif.toModifier() );
                  String name = i_fields[i].getName();
                  FieldDeclaration mutant = new FieldDeclaration( modlist, tname, name, null );
                  outputToFile(comp_unit, mutant);
               } catch(Exception ex)
               {
                  System.err.println("[Exception]  " + ex);
               }
            }
         }
      }
   }

   private boolean isEquivalent(CompilationUnit comp_unit,OJField f)
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
    * Output IHI mutants to files
    * @param comp_unit
    * @param mutant
    */
   public void outputToFile(CompilationUnit comp_unit, FieldDeclaration mutant)
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
         IHI_Writer writer = new IHI_Writer( mutant_dir, out );
         writer.setMutant(mutant);
         comp_unit.accept( writer );
         out.flush();  
         out.close();
      } catch ( IOException e ) 
      {
         System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) 
      {
         System.err.println( "errors during printing " + f_name );
         e.printStackTrace();
      }
   }


   public IHI( openjava.mop.Environment oj_param0, openjava.mop.OJClass oj_param1, openjava.ptree.ClassDeclaration oj_param2 )
   {
      super( oj_param0, oj_param1, oj_param2 );
      file_env = oj_param0;
   }

   public IHI( java.lang.Class oj_param0, openjava.mop.MetaInfo oj_param1 )
   {
      super( oj_param0, oj_param1 );
   }
}

