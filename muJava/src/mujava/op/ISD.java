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
import java.util.*;
import openjava.mop.*;
import openjava.ptree.*;
import java.lang.reflect.*;
import mujava.MutationSystem;

/**
 * <p>Generate ISI (Super keyword insertion) and 
 *    ISD (Super keyword deletion) mutants -- insert the <i>super</i> keyword; 
 *    delete each occurrence of the <i>super</i> keyword
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class ISD extends mujava.op.util.Mutator
{
   MethodDeclaration current_method;
   Stack fieldStack = new Stack();
   Stack methodStack = new Stack();
   Stack s = new Stack();

   Vector overridingFields = new Vector();
   Vector overridingMethods = new Vector();
   Class parent = null;
   int nesting = 0;

   public ISD(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env, comp_unit );
   }

   public void visit( ClassDeclaration p ) throws ParseTreeException 
   {
      this.evaluateDown( p );

      if (p.getName().equals(MutationSystem.CLASS_NAME))
      {
         nesting = 1;
      } 
      else if (nesting == 0)
      {
         this.evaluateUp(p);
         return;
      } 
      else
      {
         nesting++;
         fieldStack.add(overridingFields);
         methodStack.add(overridingMethods);
         overridingFields = new Vector();
         overridingMethods = new Vector();
      }

      s.push(p.getName());

      String package_name = comp_unit.getPackage();
      String target_class = "";

      if ((package_name == null)|| (package_name.equals("null")))
      {
         for (int i=0; i<s.size(); i++)
         {
            if (i == 0)
            {
               target_class = s.get(i).toString();
            }
            else
            {
               target_class = target_class + "$" + s.get(i).toString();
            }
         }
      }
      else
      {
         target_class = package_name;
         for (int i=0; i<s.size(); i++)
         {
            if (i == 0)
            {
               target_class = target_class + "." + s.get(i).toString();
            }
            else
            {
               target_class = target_class + "$" + s.get(i).toString();
            }
         }
      }

      try
      {
         parent = Class.forName(target_class).getSuperclass();
         if ((parent == null) || (parent.getName().equals("java.lang.Object"))) 
        	return;
 
         // Examine overriding methods, hiding variables.
         Class clazz = Class.forName(target_class);
         if (clazz == null) 
        	return;
         
         Method[] child_ms = clazz.getDeclaredMethods();
         Method[] parent_ms = parent.getDeclaredMethods();
         if ((child_ms == null) || (parent_ms == null)) 
        	 return;
         
         for (int i=0; i<child_ms.length; i++)
         {
            for (int j=0; j<parent_ms.length; j++)
            {
               if ( !(child_ms[i].getName().equals(parent_ms[j].getName())) ) 
            	  continue;
            
               if (!sameReturnType(child_ms[i].getReturnType(), parent_ms[j].getReturnType())) 
            	  continue;
            
               if ( !sameParameterType(child_ms[i].getParameterTypes(), parent_ms[j].getParameterTypes())) 
            	  continue;
            
               overridingMethods.add(child_ms[i]);
            }
         }

         Field[] child_fs = clazz.getDeclaredFields();
         Field[] parent_fs = parent.getDeclaredFields();
         for (int i=0; i<child_fs.length; i++)
         {
            for (int j=0; j<parent_fs.length; j++)
            {
               if ( !(child_fs[i].getName().equals(parent_fs[j].getName())) ) 
            	  continue;
               
               if ( !(child_fs[i].getType().getName().equals(parent_fs[j].getType().getName())) ) 
            	  continue;
             
               overridingFields.add(p.getName());
            }
         }
         p.childrenAccept( this );
      } catch (ClassNotFoundException e)
      {
    	 // do nothing
      }
      
      s.pop();
      if (nesting > 1)
      {
         overridingFields = (Vector)fieldStack.pop();
         overridingMethods = (Vector) methodStack.pop();
         nesting--;
      }
      this.evaluateUp( p );
   }

   boolean sameReturnType(Class c1, Class c2)
   {
      if ((c1 == null) && (c2 == null)) 
    	 return true;
      
      if ((c1 == null) || (c2 == null)) 
    	 return false;
      
      if (c1.getName().equals(c2.getName())) 
    	 return true;
      
      return false;
   }

   boolean sameParameterType(Class[] par1, Class[] par2)
   {
      if ((par1 == null) || (par1.length == 0))
      {
         if ( (par2 == null) || (par2.length == 0) )
        	return true;
         else 
        	return false;
      }

      if (par1.length != par2.length) 
    	 return false;

      for (int i=0; i<par1.length; i++)
      {
         if (!(par1[i].getName().equals(par2[i].getName()))) 
        	return false;
      }
      return true;
   }

   boolean sameParameterType(Class[] par1, ParameterList par2)
   {
      if ((par1 == null) || (par1.length == 0))
      {
         if ( (par2 == null) || (par2.size() == 0) )
        	return true;
         else 
        	return false;
      }

      if (par1.length != par2.size()) 
    	 return false;

      for (int i=0; i<par1.length; i++)
      {
         if (!(par1[i].getName().equals(par2.get(i).getTypeSpecifier().getName()))) 
        	return false;
      }
      return true;
   }

   public void visit( MethodDeclaration p ) throws ParseTreeException 
   {
      current_method = p;
	  super.visit(p);
   }

   public void visit( FieldAccess p ) throws ParseTreeException 
   {
      Expression ref_exp = p.getReferenceExpr();
      if (ref_exp instanceof SelfAccess)
      {
	     if (((SelfAccess)ref_exp).getAccessType() == SelfAccess.SUPER)
	     {
			FieldAccess mutant = (FieldAccess)p.makeRecursiveCopy();
			mutant.setReferenceExpr(null);
			outputToFile(p, mutant);
	     }
      }
   }

   public void visit( AssignmentExpression p ) throws ParseTreeException 
   {
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

   boolean isDifinedMethod(MethodCall p)
   {
      try
      {
         OJClass clazz = getSelfType();
         OJMethod[] ms = clazz.getDeclaredMethods();
         
         for (int i=0; i<ms.length; i++)
         {
            if (isSameMethod(ms[i], p)) 
               return true;
         }
      } catch(Exception e)
      {
         e.printStackTrace();
      }
    
      return false;
   }

   boolean isSameMethod(OJMethod m, MethodCall p)
   {
      try
      {
         if (!(m.getName().equals(p.getName()))) 
        	return false;
       
         if (!(m.getReturnType().getName().equals(getType(p).getName()))) 
        	return false;
       
         ExpressionList elist = p.getArguments();
         OJClass[] plist = m.getParameterTypes();
         
         if ( (elist == null) && (plist == null)) 
        	return true;
       
         if ( ((elist != null) && (plist == null)) || 
        	  ((elist == null) && (plist != null))) 
        	return false;
       
         if (elist.size() != plist.length) 
        	return false;
       
         for (int j=0; j<elist.size(); j++)
         {
            OJClass type = getType(elist.get(j));
            if (!(type.getName().equals(plist[j].getName())))
            {
               return false;
            }
         }
         return true;
      } catch(Exception e)
      {
         e.printStackTrace();
      }
      return true;
   }

   public void visit( MethodCall p ) throws ParseTreeException 
   {
  	  Expression lexp = p.getReferenceExpr();
	  if (lexp != null)
	  {
		 if (lexp instanceof SelfAccess)
		 {
			if ( (((SelfAccess)lexp).getAccessType() == SelfAccess.SUPER) &&
				 !(occurRecursive(p)) && isDifinedMethod(p))
		    {
			   MethodCall mutant;
			   mutant = (MethodCall)p.makeRecursiveCopy();
			   mutant.setReferenceExpr(null);
			   outputToFile(p,mutant);
			}
		 }
		 else if (lexp instanceof FieldAccess)
		 {
			super.visit(p);
		 }
	  }
	  else
	  {
         if (isOverridingMethodCall(p)) 
        	isi_outputToFile(p);
	  }
   }

   boolean isOverridingMethodCall(MethodCall p)
   {
      try 
      {
         for (int i=0; i<overridingMethods.size(); i++)
         {
            Method m = (Method)overridingMethods.get(i);
            if (!(p.getName().equals(m.getName()))) 
               continue;
            
            ExpressionList elist = p.getArguments();
            Class[] plist = m.getParameterTypes();
            
            if ( (elist == null) && (plist == null)) 
               return true;
        
            if ( ((elist != null) && (plist == null)) || 
            	 ((elist == null) && (plist != null))) 
               continue;
        
            boolean found = true;
            
            if (elist.size() != plist.length) 
               continue;
            
            for (int j=0; j<elist.size(); j++)
            {
               OJClass type = getType(elist.get(j));
               if (!(type.getName().equals(plist[j].getName())))
               {
                  found = false;
                  break;
               }
            }
            
            if (found) 
               return true;
            else 
               return false;
         }
         return false;
      } catch (ParseTreeException e)
      {
         return false;
      }
   }

   public void visit( Variable p ) throws ParseTreeException 
   {
      for (int i=0; i<overridingFields.size(); i++)
      {
         if (p.toString().equals(overridingFields.get(i).toString()))
         {
		    isi_outputToFile(p);
		 }
      }
   }

   /**
    * Output ISI mutants to files
    * @param mutant
    */
   public void isi_outputToFile(MethodCall mutant)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getISISourceName(this);
      String mutant_dir = getISIMuantID();

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 ISI_Writer writer = new ISI_Writer(mutant_dir, out);
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

   /**
    * Output ISI mutants to files
    * @param mutant
    */
   public void isi_outputToFile(Variable mutant)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getISISourceName(this);
      String mutant_dir = getISIMuantID();

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 ISI_Writer writer = new ISI_Writer(mutant_dir,out);
		 writer.setMutant(mutant);
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

   /**
    * Write mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(FieldAccess original, FieldAccess mutant)
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
		 ISK_JTD_Writer writer = new ISK_JTD_Writer(mutant_dir,out);
		 writer.setMutant(original, mutant);
		 comp_unit.accept( writer );
		 out.flush();  out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }
 

   /**
    * Write mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(MethodCall original, MethodCall mutant)
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
		 ISK_JTD_Writer writer = new ISK_JTD_Writer(mutant_dir,out);
		 writer.setMutant(original,mutant);
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

   private boolean occurRecursive(MethodCall p)
   {
      // compare method name
      if ( !(p.getName()).equals(current_method.getName()) ) 
    	 return false;

      ExpressionList args = p.getArguments();
      ParameterList pars = current_method.getParameters();

      // compare parameter number
      if (pars.size() != args.size()) 
    	 return false;
      if ( (pars.size()==0) && (args.size()==0)) 
    	 return true;

      // compare paremeter type
      if (pars.size() > 0)
      {
         try
         {
            String par_type;
            String arg_type;
            for (int i=0; i<pars.size(); i++)
            {
               par_type = pars.get(i).getTypeSpecifier().getName();
               arg_type = (getType(args.get(i))).getName();
               if ( !(par_type.equals(arg_type)) ) 
            	  return false;
            }
         } catch (Exception e)
         {
            return false;
         }
      }
      return true;
   }

   /**
    * Retrieve the file name 
    * @param clazz
    * @return
    */
   public String getISISourceName(mujava.op.util.Mutator clazz)
   {
	  // make directory for the mutant
	  String dir_name = MutationSystem.MUTANT_PATH + "/ISI_" + this.num;
	  File f = new File(dir_name);
  	  f.mkdir();

	  // return file name
	  String name;
	  name = dir_name + "/" +  MutationSystem.CLASS_NAME+".java";
      return name;
   }

   /**
    * Retrieve the ID of ISI mutant
    * @return str - ID of the mutant 
    */
   public String getISIMuantID()
   {
      String str = "ISI_"+this.num;
      return str;
   }
}
