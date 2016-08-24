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

import openjava.mop.*;
import openjava.ptree.*;

import java.io.*;
import mujava.op.*;
import mujava.op.basic.*;
import mujava.op.util.*;
import mujava.util.Debug;
 /**
 * <p>Description: Generate all mutants </p>
 * @author Yu-Seung Ma
 * @version 1.0
 * 
 * 
 * Taking out aor_flag for not clear about the reason of using it.
 * Lin Deng, Aug 23
 * 
  */ 

public class AllMutantsGenerator extends MutantsGenerator
{
   boolean existIHD = false;

   String[] classOp;
   String[] traditionalOp;

   public AllMutantsGenerator(File f) 
   {
      super(f);
      classOp = MutationSystem.cm_operators;
      traditionalOp = MutationSystem.tm_operators;
   }
   
   public AllMutantsGenerator(File f, boolean debug) 
   {
      super(f, debug); 
      classOp = MutationSystem.cm_operators;
      traditionalOp = MutationSystem.tm_operators;
   }

   public AllMutantsGenerator(File f, String[] cOP, String[] tOP) 
   {
      super(f);
      classOp = cOP;
      traditionalOp = tOP;
   }

   void genMutants()
   {
      if (comp_unit == null)
      {
         System.err.println(original_file + " is skipped.");
      }
      ClassDeclarationList cdecls = comp_unit.getClassDeclarations();
      if (cdecls == null || cdecls.size() == 0) 
         return;

      if (traditionalOp != null && traditionalOp.length > 0)
      {
         Debug.println("* Generating traditional mutants"); 
         MutationSystem.clearPreviousTraditionalMutants();
         MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
         CodeChangeLog.openLogFile();
         genTraditionalMutants(cdecls);
         CodeChangeLog.closeLogFile();
      }

      if (classOp != null && classOp.length > 0)
      {
	     Debug.println("* Generating class mutants");
         MutationSystem.clearPreviousClassMutants();
         MutationSystem.MUTANT_PATH = MutationSystem.CLASS_MUTANT_PATH;
         CodeChangeLog.openLogFile();
         genClassMutants(cdecls);
         CodeChangeLog.closeLogFile();
      }
   }


   void genClassMutants(ClassDeclarationList cdecls)
   {
      genClassMutants1(cdecls); 
      genClassMutants2(cdecls);
   }

   void genClassMutants2(ClassDeclarationList cdecls)
   {
      for (int j=0; j<cdecls.size(); ++j)
      {
    	 ClassDeclaration cdecl = cdecls.get(j);
         if (cdecl.getName().equals(MutationSystem.CLASS_NAME))
         {
    	    DeclAnalyzer mutant_op;

            if (hasOperator(classOp, "IHD"))
            {

               Debug.println("  Applying IHD ... ... ");
               mutant_op = new IHD(file_env, null, cdecl);
               generateMutant(mutant_op);
               
               if (((IHD)mutant_op).getTotal() > 0) 
                  existIHD = true;
            }

            if (hasOperator(classOp, "IHI"))
            {
               Debug.println("  Applying IHI ... ... ");
               mutant_op = new IHI(file_env, null, cdecl);
               generateMutant(mutant_op);
            }

            if (hasOperator(classOp, "IOD"))
            {
               Debug.println("  Applying IOD ... ... ");
               mutant_op = new IOD(file_env, null, cdecl);
               generateMutant(mutant_op);
            }

            if (hasOperator(classOp, "OMR"))
            {
               Debug.println("  Applying OMR ... ... ");
               mutant_op = new OMR(file_env, null, cdecl);
               generateMutant(mutant_op);
            }

            if (hasOperator(classOp, "OMD"))
            {
               Debug.println("  Applying OMD ... ... ");
               mutant_op = new OMD(file_env, null, cdecl);
               generateMutant(mutant_op);
            }

            if (hasOperator(classOp, "JDC"))
            {
               Debug.println("  Applying JDC ... ... ");
               mutant_op = new JDC(file_env, null, cdecl);
               generateMutant(mutant_op);
            }
         }
      }
   }


   void genClassMutants1(ClassDeclarationList cdecls)
   {
      for (int j=0; j<cdecls.size(); ++j)
      {
         ClassDeclaration cdecl = cdecls.get(j);
         if (cdecl.getName().equals(MutationSystem.CLASS_NAME))
         {
            String qname = file_env.toQualifiedName(cdecl.getName());
            try 
            {
               mujava.op.util.Mutator mutant_op;

               if (hasOperator(classOp,"AMC"))
               {
                  Debug.println("  Applying AMC ... ... ");
                  mutant_op = new AMC(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "IOR"))
               {
                  Debug.println("  Applying IOR ... ... ");
                  try
                  {
                     Class parent_class = Class.forName(qname).getSuperclass();
                     if (!(parent_class.getName().equals("java.lang.Object")))
                     {
                        String temp_str = parent_class.getName();
                        String result_str = "";
                        for (int k=0; k<temp_str.length(); k++)
                        {
                           char c = temp_str.charAt(k);
                           if (c == '.')
                           {
                              result_str = result_str + "/";
                           }
                           else
                           {
                              result_str = result_str + c;
                           }
                        }

                        File f = new File(MutationSystem.SRC_PATH, result_str + ".java");
                        if (f.exists())
                        {
                           CompilationUnit[] parent_comp_unit = new CompilationUnit[1];
                           FileEnvironment[] parent_file_env = new FileEnvironment[1];
                           this.generateParseTree(f, parent_comp_unit, parent_file_env);
                           this.initParseTree(parent_comp_unit, parent_file_env);
                           mutant_op = new IOR(file_env, cdecl, comp_unit);
                           ((IOR)mutant_op).setParentEnv(parent_file_env[0], parent_comp_unit[0]);
                           comp_unit.accept(mutant_op);
                        }
                     }
                  } catch (ClassNotFoundException e)
                  {
                     System.out.println(" Exception at generating IOR mutant. File : AllMutantsGenerator.java ");
                  } catch (NullPointerException e1)
                  {
                     System.out.print(" IOP  ^^; ");
                  }    
               }

               if (hasOperator(classOp, "ISD"))
               {
                  Debug.println("  Applying ISD ... ... ");
                  mutant_op = new ISD( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }
 
               if (hasOperator(classOp, "IOP"))
               {
                  Debug.println("  Applying IOP ... ... ");
                  mutant_op = new IOP(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "IPC"))
               {
                  Debug.println("  Applying IPC ... ... ");
                  mutant_op = new IPC( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "PNC"))
               {
                  Debug.println("  Applying PNC ... ... ");
                  mutant_op = new PNC( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "PMD"))
               {
                  Debug.println("  Applying PMD ... ... ");
                  // if(existIHD){
                  mutant_op = new PMD( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
                  //}
               }

               if (hasOperator(classOp, "PPD"))
               {
                  Debug.println("  Applying PPD ... ... ");
                  // if(existIHD){
                  mutant_op = new PPD( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
                  // }
               }

               if (hasOperator (classOp, "PRV"))
               {
                  Debug.println("  Applying PRV ... ... ");
                  mutant_op = new PRV( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "PCI"))
               {
                  Debug.println("  Applying PCI ... ... ");
                  mutant_op = new PCI( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               } 

               if (hasOperator(classOp, "PCC"))
               {
                  Debug.println("  Applying PCC ... ... ");
                  mutant_op = new PCC( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "PCD"))
               {
                  Debug.println("  Applying PCD ... ... ");
                  mutant_op = new PCD( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "JSD"))
               {
                  Debug.println("  Applying JSC ... ... ");
                  mutant_op = new JSD( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "JSI"))
               {
                  Debug.println("  Applying JSI ... ... ");
                  mutant_op = new JSI( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "JTD"))
               {
                  Debug.println("  Applying JTD ... ... ");
                  mutant_op = new JTD( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "JTI"))
               {
                  Debug.println("  Applying JTI ... ... ");
                  mutant_op = new JTI( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "JID"))
               {
                  Debug.println("  Applying JID ... ... ");
                  mutant_op = new JID( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "OAN"))
               {
                  Debug.println("  Applying OAN ... ... ");
                  mutant_op = new OAN( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "EOA"))
               {
                  Debug.println("  Applying EOA ... ... ");
                  mutant_op = new EOA( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "EOC"))
               {
                  Debug.println("  Applying EOC ... ... ");
                  mutant_op = new EOC( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "EAM"))
               {
                  Debug.println("  Applying EAM ... ... ");
                  mutant_op = new EAM( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(classOp, "EMM"))
               {
                  Debug.println("  Applying EMM ... ... ");
                  mutant_op = new EMM( file_env, cdecl, comp_unit );
                  comp_unit.accept(mutant_op);
               }

            } catch (ParseTreeException e ) 
            {
               System.err.println( "Encountered errors during generating mutants." );
               e.printStackTrace();
            }
         }
      }
   }

   /**
    * Compile mutants into bytecode
    */
   public void compileMutants()
   {
      if (traditionalOp != null && traditionalOp.length > 0)
      {
	     Debug.println("* Compiling traditional mutants into bytecode");
         MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
         super.compileMutants(); 
      }

      if (classOp != null && classOp.length > 0)
      {
	     Debug.println("* Compiling class mutants into bytecode");
         MutationSystem.MUTANT_PATH = MutationSystem.CLASS_MUTANT_PATH;
         super.compileMutants();
      }
   }

   void genTraditionalMutants(ClassDeclarationList cdecls)
   {
      for(int j=0; j<cdecls.size(); ++j)
      {
         ClassDeclaration cdecl = cdecls.get(j);

         if (cdecl.getName().equals(MutationSystem.CLASS_NAME))
         {
            try
            {
               mujava.op.util.Mutator mutant_op;
//               boolean AOR_FLAG = false;

               if (hasOperator(traditionalOp, "AORB"))
               {
                  Debug.println("  Applying AOR-Binary ... ... ");
//                  AOR_FLAG = true;
                  mutant_op = new AORB(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
               if (hasOperator(traditionalOp, "AORS"))
               {
                  Debug.println("  Applying AOR-Short-Cut ... ... ");
//                  AOR_FLAG = true;
                  mutant_op = new AORS(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(traditionalOp, "AODU"))
               {
                  Debug.println("  Applying AOD-Normal-Unary ... ... ");
                  mutant_op = new AODU(file_env, cdecl, comp_unit);
//                  ((AODU)mutant_op).setAORflag(AOR_FLAG);
                  comp_unit.accept(mutant_op);
               }
               
               if (hasOperator(traditionalOp, "AODS"))
               {
                  Debug.println("  Applying AOD-Short-Cut ... ... ");
                  mutant_op = new AODS(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
               
               if (hasOperator(traditionalOp, "AOIU"))
               {
                  Debug.println("  Applying AOI-Normal-Unary ... ... ");
                  mutant_op = new AOIU(file_env, cdecl, comp_unit);
//                  ((AOIU)mutant_op).setAORflag(AOR_FLAG);
                  comp_unit.accept(mutant_op);
               }
               
               if (hasOperator(traditionalOp, "AOIS"))
               {
                  Debug.println("  Applying AOI-Short-Cut ... ... ");
                  mutant_op = new AOIS(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
               
               if (hasOperator(traditionalOp, "ROR"))
               {
                  Debug.println("  Applying ROR ... ... ");
                  mutant_op = new ROR(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(traditionalOp, "COR"))
               {
                  Debug.println("  Applying COR ... ... ");
                  mutant_op = new COR(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(traditionalOp, "COD"))
               {
                  Debug.println("  Applying COD ... ... ");
                  mutant_op = new COD(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(traditionalOp, "COI"))
               {
                  Debug.println("  Applying COI ... ... ");
                  mutant_op = new COI(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(traditionalOp, "SOR"))
               {
                  Debug.println("  Applying SOR ... ... ");
                  mutant_op = new SOR(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(traditionalOp, "LOR"))
               {
                  Debug.println("  Applying LOR ... ... ");
                  mutant_op = new LOR(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(traditionalOp, "LOI"))
               {
                  Debug.println("  Applying LOI ... ... ");
                  mutant_op = new LOI(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(traditionalOp, "LOD"))
               {
                  Debug.println("  Applying LOD ... ... ");
                  mutant_op = new LOD(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator(traditionalOp, "ASRS"))
               {
                  Debug.println("  Applying ASR-Short-Cut ... ... ");
                  mutant_op = new ASRS(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
               if (hasOperator(traditionalOp, "SDL"))
               {
            	   Debug.println("  Applying SDL ... ... ");
                   mutant_op = new SDL(file_env, cdecl, comp_unit);
                   comp_unit.accept(mutant_op);
               }
               if (hasOperator(traditionalOp, "VDL"))
               {
            	   Debug.println("  Applying VDL ... ... ");
                   mutant_op = new VDL(file_env, cdecl, comp_unit);
                   comp_unit.accept(mutant_op);
               }
               if (hasOperator(traditionalOp, "CDL"))
               {
            	   Debug.println("  Applying CDL ... ... ");
                   mutant_op = new CDL(file_env, cdecl, comp_unit);
                   comp_unit.accept(mutant_op);
               }
               if (hasOperator(traditionalOp, "ODL"))
               {
            	   Debug.println("  Applying ODL ... ... ");
                   mutant_op = new ODL(file_env, cdecl, comp_unit);
                   comp_unit.accept(mutant_op);
               }
            } catch (ParseTreeException e)
            {
               System.err.println( "Exception, during generating traditional mutants for the class "
                              + MutationSystem.CLASS_NAME);
               e.printStackTrace();
            }
         } 
      }
   }
}