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
import openjava.tools.parser.*;
import openjava.ptree.util.*;

import java.io.*;
import java.util.*;
import java.lang.reflect.Constructor;

import mujava.cli.Util;
import mujava.op.util.*;
import mujava.util.*;

import com.sun.tools.javac.Main;

/**
 * <p>Generate mutants according to selected mutation 
 *            operator(s) from gui.GenMutantsMain. 
 *            The original version is loaded, mutated, and compiled. 
 *            Outputs (mutated source and class files) are in the 
 *            -mutants folder. </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public abstract class MutantsGenerator 
{
   //private boolean debug = false;
//	static int  counter;
   /** Java source file where mutation operators are applied to */
   File original_file;         // mutation�� ������ file

   /** mutation operators to apply */
   String[] operators = null;

   FileEnvironment file_env = null;
   CompilationUnit comp_unit = null;

   public MutantsGenerator(File f) 
   {
      this.original_file = f;
      initPrimitiveTypes();
   }

   public MutantsGenerator(File f, boolean debug_flag) 
   {
      this(f);
      //debug = debug_flag;
   }

   public MutantsGenerator(File f, String[] operator_list) 
   {
      this(f);
      operators = operator_list;
   }

   public MutantsGenerator(File f, String[] operator_list, boolean debug_flag) 
   {
      this(f, operator_list);
      //debug = debug_flag;
   }

   /**
    * Generate and initialize parse tree from the original Java source file.
    * Generate mutants. Arrange and compile the original Java source file.        
    * @return
    * @throws OpenJavaException
    */
   public boolean makeMutants() throws OpenJavaException
   {
      Debug.print("-------------------------------------------------------\n");
      Debug.print("* Generating parse tree. \n" );

      generateParseTree();
      Debug.print("..done. \n" );
      //System.out.println("0");
      Debug.print("* Initializing parse tree. \n" );
      initParseTree();
      Debug.print("..done. \n" );
      //System.out.println("1");
      Debug.print("* Generating Mutants \n" );
      genMutants();
      Debug.print("..done.\n" );
      //System.out.println("2");
      Debug.print("* Arranging original soure code. \n" );
      arrangeOriginal();
      //System.out.println("3");
      compileOriginal();
      Debug.print("..done. \n" );
      Debug.flush();
      return true;
   }

   abstract void genMutants();

  /*void generateMutant(OJClass mutant_op){
	    try {
	      mutant_op.translateDefinition(comp_unit);
	    }catch (Exception ex){
	      System.err.println("fail to translate " +mutant_op.getName()+" : " + ex);
	      ex.printStackTrace();
	    }
	  }*/

   /**
    * Generate mutants from Java bytecode
    */
   void generateMutant(DeclAnalyzer mutant_op)
   {
      try 
      {
         mutant_op.translateDefinition(comp_unit);
      } 
      catch (Exception ex)
      {
         System.err.println("fail to translate " + mutant_op.getName() + " : " + ex);
         ex.printStackTrace();
      }
   }

   /**
    * Arrange the original source file into an appropriate directory
    */
   private void arrangeOriginal()
   {
      if (comp_unit == null)
      {
         System.err.println(original_file + " is skipped.");
      }
      
      ClassDeclarationList cdecls = comp_unit.getClassDeclarations();
      for (int j=0; j<cdecls.size(); ++j)
      {
         ClassDeclaration cdecl = cdecls.get(j);
         File outfile = null;
         try 
         {
            outfile = new File(MutationSystem.ORIGINAL_PATH, MutationSystem.CLASS_NAME + ".java");
            FileWriter fout = new FileWriter( outfile ); 
            PrintWriter out = new PrintWriter( fout );
            MutantCodeWriter writer = new MutantCodeWriter( out );
            writer.setClassName(cdecl.getName());
            comp_unit.accept( writer );
            out.flush();  
            out.close();
         } 
         catch ( IOException e ) 
         {
            System.err.println( "fails to create " + outfile );
         } 
         catch ( ParseTreeException e ) 
         {
            System.err.println( "errors during printing " + outfile );
            e.printStackTrace();
         }
      }
   }

   /**
    * Initialize parse tree 
    * @throws OpenJavaException
    */
   private void initParseTree() throws OpenJavaException
   {
      try
      {
    	  //System.out.println("OJSystem.env0 :" + OJSystem.env );
         comp_unit.accept(new TypeNameQualifier (file_env));
         //System.out.println("OJSystem.env1 :" + OJSystem.env );
         MemberAccessCorrector corrector = new MemberAccessCorrector(file_env);
        // System.out.println("OJSystem.env2 :" + OJSystem.env );
         comp_unit.accept(corrector);
         //System.out.println("OJSystem.env3 :" + OJSystem.env );
      } 
      catch (ParseTreeException e)
      {
         throw new OpenJavaException("can't initialize parse tree");
      }
   }

   /**
    * Initialize parse tree
    * @param parent_comp_unit
    * @param parent_file_env
    */
   void initParseTree(CompilationUnit[] parent_comp_unit,FileEnvironment[] parent_file_env)
   {
      try
      {
         parent_comp_unit[0].accept(new TypeNameQualifier(parent_file_env[0]));
         MemberAccessCorrector corrector = new MemberAccessCorrector(parent_file_env[0]);
         parent_comp_unit[0].accept(corrector);
      } 
      catch (ParseTreeException e)
      {
         System.err.println("Encountered errors during analysis.");
         e.printStackTrace();
      }
   }

   /**
    * Generate parse tree
    * @throws OpenJavaException
    */
   private void generateParseTree() throws OpenJavaException
   {
      try
      {
         comp_unit = parse(original_file);

         String pubcls_name = getMainClassName(file_env, comp_unit);

         if (pubcls_name == null)
         {
            int len = original_file.getName().length();
            pubcls_name = original_file.getName().substring(0, len-6);
         }
         
         file_env = new FileEnvironment(OJSystem.env, comp_unit, pubcls_name);
         ClassDeclarationList typedecls = comp_unit.getClassDeclarations();
         
         for (int j = 0; j < typedecls.size(); ++j)
         {
            ClassDeclaration class_decl = typedecls.get(j);
            OJClass c = makeOJClass(file_env, class_decl);
            OJSystem.env.record(c.getName(), c);
            recordInnerClasses(c);
         }

      } 
      catch (OpenJavaException e1)
      {
         throw e1;
      }
      catch (Exception e)
      {
         System.err.println("errors during parsing. " + e);
         System.out.println(e);
         e.printStackTrace();
      }
   }

   /**
    * Evaluate whether a parse tree is successfully generated
    * @param f
    * @param comp_unit
    * @param file_env
    * @return
    */
   boolean generateParseTree(File f, CompilationUnit[] comp_unit, FileEnvironment[] file_env)
   {
      try
      {
         comp_unit[0] = parse(f);
         String pubcls_name = getMainClassName(file_env[0], comp_unit[0]);
         if (pubcls_name == null)
         {
            int len = f.getName().length();
            pubcls_name = f.getName().substring(0, len-6);
         }

         file_env[0] = new FileEnvironment(OJSystem.env, comp_unit[0], pubcls_name);

         ClassDeclarationList typedecls = comp_unit[0].getClassDeclarations();
         for (int j=0; j<typedecls.size(); ++j)
         {
            ClassDeclaration class_decl = typedecls.get(j);

            if ( class_decl.getName().equals(MutationSystem.CLASS_NAME) )
            {
               if ( class_decl.isInterface() ||
            		class_decl.getModifiers().contains(ModifierList.ABSTRACT) )
               {
                  return false;
               }
            }

            OJClass c = makeOJClass(file_env[0], class_decl);
            OJSystem.env.record(c.getName(), c);
            recordInnerClasses(c);
         }
      } 
      catch (Exception e)
      {
         System.err.println("errors during parsing. " + e);
         e.printStackTrace();
         return false; 
      }
      return true;
   }

   /**
    * Record inner-classes
    * @param c
    */
   private static void recordInnerClasses( OJClass c ) 
   {
      OJClass[] inners = c.getDeclaredClasses();
      for (int i = 0; i < inners.length; ++i) 
      {
         OJSystem.env.record( inners[i].getName(), inners[i] );
         recordInnerClasses( inners[i] );
      }
   }

   /** -> to move to OJClass.forParseTree() **/
   private OJClass makeOJClass( Environment env, ClassDeclaration cdecl ) 
   {
      OJClass result;
      String qname = env.toQualifiedName( cdecl.getName() );
      Class meta = OJSystem.getMetabind( qname );
      try 
      {
         Constructor constr = meta.getConstructor( new Class[]{
             Environment . class,OJClass . class, ClassDeclaration . class } );
         Object[] args = new Object[]{env, null, cdecl};
         result = (OJClass) constr.newInstance(args);
      } 
      catch (Exception ex) 
      {
         System.err.println("errors during gererating a metaobject for " + qname);
         ex.printStackTrace();
         result = new OJClass( env, null, cdecl );
      }
      return result;
   }

   /**
    * Prepare a compilation unit 
    * @param file
    * @return
    * @throws OpenJavaException
    */
   private static CompilationUnit parse( File file ) throws OpenJavaException
   {
      Parser parser;
      try
      {
         parser = new Parser(new java.io.FileInputStream( file ) );        
      } 
      catch ( java.io.FileNotFoundException e ) 
      {
         System.err.println( "File " + file + " not found." );
         return null;
      }
      catch (UnsupportedClassVersionError e)
      {
	  System.err.println("[ERROR] Unable to use the OpenJava Parser because the class version is unsupported. It may be that the openjava.jar file was compiled with a version of Java later than the one you're using.");
	  System.err.println();
	  e.printStackTrace();
	  return null;
      }
      
      CompilationUnit result;
      try
      {
    	 System.out.println( "File " + file );
         result = parser.CompilationUnit( OJSystem.env );         
      } 
      catch (ParseException e) 
      {
         throw new OpenJavaException(" can't generate parse tree");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         result = null;
      }
      return result;
   }

   /**
    * 
    * @param env
    * @param comp_unit
    * @return
    * @throws ParseTreeException
    */
   private static String getMainClassName(FileEnvironment env,
          CompilationUnit comp_unit) throws ParseTreeException
   {

      ClassDeclaration cd = comp_unit.getPublicClass();
      if (cd != null) 
      {
         return cd.getName();
      }
      else 
    	 return null;
   }

   /**
    * Compile mutants 
    */
   public void compileMutants()
   {
       // Lin add a counter 12/12/13
       int counter = 0;
       String fileName = new String();
	   
      File f = new File(MutationSystem.MUTANT_PATH);
      
      String[] s = f.list(new MutantDirFilter());

      for (int i=0; i<s.length; i++)
      {
         File target_dir = new File(MutationSystem.MUTANT_PATH + "/" + s[i]);
         String[] target_file = target_dir.list(new ExtensionFilter("java"));
         fileName = target_file[0];

         
         
         Vector v = new Vector();
         for (int j=0; j<target_file.length; j++)
         {
            v.add(MutationSystem.MUTANT_PATH + "/" + s[i] + "/" + target_file[j]);
         }

         String[] pars = new String[v.size()+2];

         pars[0] = "-classpath";
         pars[1] = MutationSystem.CLASS_PATH;
         for (int j=0; j<v.size(); j++)
         {
            pars[2+j] = v.get(j).toString();
         }
         try
         {
        // result = 0 : SUCCESS,   result = 1 : FALSE
        //int result = Main.compile(pars,new PrintWriter(new FileOutputStream("temp")));
        	 
        	 /*
        	  * 12/19/13 Lin modified:
        	  * if not in debug mode, for not showing the compile result when some 
        	  * mutants can't pass compiler
        	  * if in debug mode, display
        	  */
        	 
				int result;
				if (Util.debug)
					result = Main.compile(pars);
				else {
					File tempCompileResultFile = new File(
							MutationSystem.SYSTEM_HOME + "/compile_output");
					PrintWriter out = new PrintWriter(tempCompileResultFile);

					result = Main.compile(pars, out);
					tempCompileResultFile.delete();
					
				}
            
            
            if (result == 0)
            {
               Debug.print("+" + s[i] + "   ");
               counter++;
            }
            else
            {
               Debug.print("-" + s[i] + "   ");
             // delete directory
               File dir_name = new File(MutationSystem.MUTANT_PATH + "/" + s[i]);
               File[] mutants = dir_name.listFiles();
               boolean tr = false;
               
               for (int j=0; j<mutants.length; j++)
               {
            // [tricky solution] It can produce loop -_-;;
                  while (!tr)
                  {
                     tr = mutants[j].delete();
                  }
                  tr = false;
               }
               
               while (!tr)
               {
                  tr = dir_name.delete();
               }
            }
         } 
         catch (Exception e)
         {
            System.err.println(e);
         }
      }
      Debug.println();
      
      
      // Lin add printer total mutants
      Util.Total = Util.Total+counter;
//      System.out
//		.println("------------------------------------------------------------------");
//      System.out.println("Total mutants gnerated for " + fileName +": " + Integer.toString(counter));
   }

   /**
    * Compile original java source file
    */
   private void compileOriginal()
   {
      String[] pars= { "-classpath",
                      MutationSystem.CLASS_PATH,
                      MutationSystem.ORIGINAL_PATH + "/" + MutationSystem.CLASS_NAME + ".java"};
      try
      {
      // result = 0 : SUCCESS,   result = 1 : FALSE
      //int result = Main.compile(pars,new PrintWriter(new FileOutputStream("temp")));
         Main.compile(pars);
      }
      catch (NoClassDefFoundError e) {
	  System.err.println("[ERROR] Could not compile the generated mutants. Make sure that tools.jar is in your classpath.");
	  System.err.println("You may also need to delete the mutants that were generated (but not compiled) in the result/ directory of the muJava installation.");
	  System.err.println();
	  e.printStackTrace();
	  System.exit(1);
      }
      catch (Exception e)
      {
         System.err.println(e);
      }
   }


   private static void initPrimitiveTypes() 
   {
      OJSystem.initConstants();
   }

   /**
    * Determine whether a string contain a certain operator
    * @param list
    * @param item
    * @return true if a string contain the operator, false otherwise
    */
   protected boolean hasOperator (String[] list, String item)
   {
      for (int i=0; i<list.length; i++)
      {
         if (list[i].equals(item)) 
            return true;
      }
      return false;
   }
}