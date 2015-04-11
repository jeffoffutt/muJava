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

import openjava.ptree.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;

import mujava.op.basic.*;
import mujava.op.util.*;
import mujava.util.Debug;
/**
 * <p>
 * Description: New tranditional mutants generator class build exclusively for command line version
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0
 * 
 */

public class TraditionalMutantsGeneratorCLI extends TraditionalMutantsGenerator {
	
	HashMap<String, List<String>> traditionalOpMap;
	

	public TraditionalMutantsGeneratorCLI(File f) {
		super(f);
	}

	public TraditionalMutantsGeneratorCLI(File f, boolean debug) {
		super(f, debug);
	}

	public TraditionalMutantsGeneratorCLI(File f, String[] tOP) {
		super(f, tOP);
	}

	public TraditionalMutantsGeneratorCLI(File f, HashMap<String, List<String>> traditionalOps)
	{
		super(f);
		traditionalOpMap = traditionalOps;
	}
	

/** 
    * Verify if the target Java source and class files exist, 
    * generate traditional mutants
    */
   void genMutants()
   {
      if (comp_unit == null)
      {
         System.err.println (original_file + " is skipped.");
      }
      
      ClassDeclarationList cdecls = comp_unit.getClassDeclarations();
      
      if (cdecls == null || cdecls.size() == 0) 
         return;

     // if (traditionalOp != null && traditionalOp.length > 0)
      {
	     Debug.println("* Generating traditional mutants");
         MutationSystem.clearPreviousTraditionalMutants();
         MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
         CodeChangeLog.openLogFile();
         genTraditionalMutants(cdecls);
         CodeChangeLog.closeLogFile();
      }
   }

   /**
    * Compile traditional mutants into bytecode 
    */
   public void compileMutants()
   {
      if (traditionalOp != null && traditionalOp.length > 0)
      {
         try
         {
            Debug.println("* Compiling traditional mutants into bytecode");
            String original_tm_path = MutationSystem.TRADITIONAL_MUTANT_PATH;
            File f = new File(original_tm_path, "method_list");
            FileReader r = new FileReader(f);
            BufferedReader reader = new BufferedReader(r);
            String str = reader.readLine();
            
            while (str != null)
            {
               MutationSystem.MUTANT_PATH = original_tm_path + "/" + str;
               super.compileMutants();
               str = reader.readLine();
            }
            reader.close();
            MutationSystem.MUTANT_PATH = original_tm_path;
         } catch (Exception e)
         {
            System.err.println("Error at compileMutants() in TraditionalMutantsGenerator.java");
         }
      }
   }

   /**
    * Apply selected traditional mutation operators: 
    *      AORB, AORS, AODU, AODS, AOIU, AOIS, ROR, COR, COD, COI,
    *      SOR, LOR, LOI, LOD, ASRS, SID, SWD, SFD, SSD 
    * @param cdecls
    */
   void genTraditionalMutants(ClassDeclarationList cdecls)
   {
      for (int j=0; j<cdecls.size(); ++j)
      {
         ClassDeclaration cdecl = cdecls.get(j);

         if (cdecl.getName().equals(MutationSystem.CLASS_NAME))
         {
            try
            {
               mujava.op.util.Mutator mutant_op;
               boolean AOR_FLAG = false;
     
               try
               {
                  // ∞¢ ∏ﬁº“µÂ signatureø° «ÿ¥Á«œ¥¬ µ∑∫≈‰∏Æ∏¶ ∏∏µÁ¥Ÿ.
                  File f = new File(MutationSystem.MUTANT_PATH, "method_list");
                  FileOutputStream fout = new FileOutputStream(f);
                  PrintWriter out = new PrintWriter(fout);
                  mutant_op = new CreateDirForEachMethod(file_env, cdecl, comp_unit, out);
                  comp_unit.accept(mutant_op);
                  out.flush();  
                  out.close();
               } catch (Exception e)
               {
                  System.err.println("Error in writing method list");
                  return;
               }

//               if (hasOperator (traditionalOp, "AORB") )
//               {
//                  Debug.println("  Applying AOR-Binary ... ... ");
//                  AOR_FLAG = true;
//                  mutant_op = new AORB(file_env, cdecl, comp_unit);
//                  comp_unit.accept(mutant_op);
//               }
               if (hasOperator (traditionalOp, "AORB")  )
               {
                  Debug.println("  Applying AOR-Binary ... ... ");
                  AOR_FLAG = true;
                  mutant_op = new AORB(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
               
               if (hasOperator (traditionalOp, "AORS")  )
               {
                  Debug.println("  Applying AOR-Short-Cut ... ... ");
                  AOR_FLAG = true;
                  mutant_op = new AORS(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator (traditionalOp, "AODU")  ) 
               {
                  Debug.println("  Applying AOD-Normal-Unary ... ... ");
                  mutant_op = new AODU(file_env, cdecl, comp_unit);
                  ((AODU)mutant_op).setAORflag(AOR_FLAG);
                  comp_unit.accept(mutant_op);
               }
          
               if (hasOperator (traditionalOp, "AODS") )
               {
                  Debug.println("  Applying AOD-Short-Cut ... ... ");
                  mutant_op = new AODS(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
               
               if (hasOperator (traditionalOp, "AOIU") )
               {
                  Debug.println("  Applying AOI-Normal-Unary ... ... ");
                  mutant_op = new AOIU(file_env,cdecl,comp_unit);
                  ((AOIU)mutant_op).setAORflag(AOR_FLAG);
                  comp_unit.accept(mutant_op);
               }
               
               if (hasOperator (traditionalOp, "AOIS") )
               {
                  Debug.println("  Applying AOI-Short-Cut ... ... ");
                  mutant_op = new AOIS(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
               
               if (hasOperator (traditionalOp, "ROR") )
               {
                  Debug.println("  Applying ROR ... ... ");
                  mutant_op = new ROR(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator (traditionalOp, "COR") )
               {
                  Debug.println("  Applying COR ... ... ");
                  mutant_op = new COR(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator (traditionalOp, "COD") ) 
               {
                  Debug.println("  Applying COD ... ... ");
                  mutant_op = new COD(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator (traditionalOp, "COI"))
               {
                  Debug.println("  Applying COI ... ... ");
                  mutant_op = new COI(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator (traditionalOp, "SOR") )
               {
                  Debug.println("  Applying SOR ... ... ");
                  mutant_op = new SOR(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator (traditionalOp, "LOR") )
               {
                  Debug.println("  Applying LOR ... ... ");
                  mutant_op = new LOR(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator (traditionalOp, "LOI") )
               {
                  Debug.println("  Applying LOI ... ... ");
                  mutant_op = new LOI(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator (traditionalOp, "LOD") )
               { 
                  Debug.println("  Applying LOD ... ... ");
                  mutant_op = new LOD(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator (traditionalOp, "ASRS") )
               {
                  Debug.println("  Applying ASR-Short-Cut ... ... ");
                  mutant_op = new ASRS(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
               
               if (hasOperator (traditionalOp, "SDL")  )
               {
                  Debug.println("  Applying SDL ... ... ");
                  mutant_op = new SDL(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
               if (hasOperator (traditionalOp, "VDL")  )
               {
                  Debug.println("  Applying VDL ... ... ");
                  mutant_op = new VDL(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
               if (hasOperator (traditionalOp, "ODL")  )
               {
                  Debug.println("  Applying ODL ... ... ");
                  mutant_op = new ODL(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
               if (hasOperator (traditionalOp, "CDL")  )
               {
                  Debug.println("  Applying CDL ... ... ");
                  mutant_op = new CDL(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

// upsorn: First attempt: statement deletion operator               
//               if (hasOperator (traditionalOp, "SDL") )
//               {
//                  Debug.println("  Applying SDL ... ... ");
//                  mutant_op = new SDL(file_env, cdecl, comp_unit);
//                  comp_unit.accept(mutant_op);
//               }
/*
               if (hasOperator (traditionalOp, "SID") )
               {
                  Debug.println("  Applying SID ... ... ");
                  mutant_op = new SID(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }

               if (hasOperator (traditionalOp, "SWD") )
               {
                  Debug.println("  Applying SWD ... ... ");
                  mutant_op = new SWD(file_env, cdecl, comp_unit);
                  comp_unit.accept(mutant_op);
               }
*/
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
