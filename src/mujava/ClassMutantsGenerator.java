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
import mujava.op.util.*;
import mujava.util.Debug;

/**
 * <p>Generate class mutants according to selected 
 *            class mutation operator(s) from gui.GenMutantsMain. 
 *            The original version is loaded, mutated, and compiled. 
 *            Outputs (mutated source and class files) are in the 
 *            class-mutants folder. </p> 
 *            
 * <p> Currently available class mutation operators:    
 *          (1) AMC: Access modifier change, 
 *          (2) IHD: Hiding variable deletion,  
 *          (3) IHI: Hiding variable insertion, 
 *          (4) IOD: Overriding method deletion, 
 *          (5) IOP: Overriding method calling position change,
 *          (6) IOR: Overriding method rename, 
 *          (7) ISI: Super keyword insertion,
 *          (8) ISD: Super keyword deletion,          
 *          (9) IPC: Explicit call to parent's constructor deletion, 
 *         (10) PNC: New method call with child class type,  
 *         (11) PMD: Member variable declaration with parent class type, 
 *         (12) PPD: Parameter variable declaration with child class type, 
 *         (13) PCI: Type cast operator insertion, 
 *         (14) PCC: Cast type change,  
 *         (15) PCD: Type cast operator deletion, 
 *         (16) PRV: Reference assignment with other compatible variable, 
 *         (17) OMR: Overloading method contents replace,   
 *         (18) OMD: Overloading method deletion, 
 *         (19) OAN: Arguments of overloading method call change, 
 *         (20) JTI: Java-specific this keyword insertion,  
 *         (21) JTD: Java-specific this keyword deletion,  
 *         (22) JSI: Java-specific static modifier insertion,  
 *         (23) JSD: Java-specific static modifier deletion, 
 *         (24) JID: Java-specific member variable initialization deletion, 
 *         (25) JDC: Java-supported default constructor creation,  
 *         (26) EOA: Java-specific reference assignment and content assignment replacement,  
 *         (27) EOC: Java-specific reference comparison and content assignment replacement,  
 *         (28) EAM: Java-specific accessor method change,  
 *         (29) EMM: Java-specific modifier method change
 * </p>        
 * @author Yu-Seung Ma
 * @version 1.0
*/

public class ClassMutantsGenerator  extends MutantsGenerator
{
   boolean existIHD = false;

   String[] classOp;

   public ClassMutantsGenerator (File f) 
   {
      super(f);
      classOp = MutationSystem.cm_operators;
   }
   
   public ClassMutantsGenerator (File f, boolean debug) 
   {
      super(f, debug);
      classOp = MutationSystem.cm_operators;
   }

   public ClassMutantsGenerator (File f, String[] cOP) 
   {
      super(f);
      classOp = cOP;
   }
   
   /** 
    * Verify if the target Java source and class files exist, 
    * generate class mutants
    */
   void genMutants()
   {
      if (comp_unit == null)
      {
         System.err.println(original_file + " is skipped.");
      }
      ClassDeclarationList cdecls = comp_unit.getClassDeclarations();
      
      if (cdecls == null || cdecls.size() == 0)    
         return;

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
 
   /**
    * Apply selected class mutation operators 
    * @param cdecls
    */
   void genClassMutants (ClassDeclarationList cdecls)
   {
      genClassMutants1(cdecls);
      genClassMutants2(cdecls);
   }

   /** 
    * Apply selected class mutation operators: IHD, IHI, IOD, OMR, OMD, JDC
    * @param cdecls
    */
   void genClassMutants2 (ClassDeclarationList cdecls)
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
               
               if ( ( (IHD)mutant_op).getTotal() > 0 ) 
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

   /** 
    * Apply selected class mutation operators: 
    *            AMC, IOR, ISD, IOP, IPC, PNC, PMD, PPD,
    *            PRV, PCI, PCC, PCD, JSD, JSI, JTD, JTI, 
    *            JID, OAN, EOA, EOC, EAM, EMM
    * @param cdecls
    */
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

               if (hasOperator(classOp, "AMC"))
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
                     if ( !(parent_class.getName().equals("java.lang.Object")) )
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
                  } 
                  catch (ClassNotFoundException e) 
                  {
                     System.out.println(" Exception at generating IOR mutant.  file : AllMutantsGenerator.java ");
                  }
                  catch (NullPointerException e1)
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
                  //if(existIHD){
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
                 //}
               }

               if (hasOperator(classOp, "PRV"))
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

            }
            catch (ParseTreeException e ) 
            {
               System.err.println( "Encountered errors during generating mutants." );
               e.printStackTrace();
            }
         }
      }
   }

   /**
    * Compile class mutants into bytecode 
    */
   public void compileMutants()
   {
      if (classOp != null && classOp.length > 0)
      {
	     Debug.println("* Compiling class mutants into bytecode");
         MutationSystem.MUTANT_PATH = MutationSystem.CLASS_MUTANT_PATH;
         super.compileMutants();
      }
   }
}