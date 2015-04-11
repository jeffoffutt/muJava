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
import openjava.ptree.*;
import mujava.op.util.ScopeHandlingMutantCodeWriter;

//import java.util.Enumeration;
import openjava.mop.*;

/**
 * <p>Output and log IOR mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class IOR_Writer extends ScopeHandlingMutantCodeWriter
{
   String target_name = null;
   TypeName target_return_type = null;
   ParameterList target_parList = null;

   /**
    * Set mutated code
    * @param mutant
    */
   public void setMutant(MethodDeclaration mutant)
   {
      target_name = mutant.getName();
      target_return_type = mutant.getReturnType();
      target_parList = mutant.getParameters();
   }

   public IOR_Writer( Environment base_env,String file_name, PrintWriter out ) 
   {
	  super(base_env, file_name, out);
   }

   boolean isMutantCall(MethodCall p)
   {
      Expression arg_val;
      OJClass type = null;
      String binded_type;

      if ( !(p.getName().equals(target_name)) ) 
    	 return false;

      try
      {
         Expression lexp = p.getReferenceExpr();
         if (lexp != null)
         {
            binded_type = getType(lexp).getName();
            if (!binded_type.equals(class_name)) 
               return false;
         }

         // Compare Arguments...
         ExpressionList args = p.getArguments();
         if (args.size() != target_parList.size()) 
        	return false;
        
         for (int i=0; i<args.size() ; i++) 
         {
            arg_val = args.get(i);
            type = getType(arg_val);
            if (!type.getName().equals(target_parList.get(i).getTypeSpecifier().getName())) 
               return false;
         }
      } catch (Exception e) 
      {
         System.err.println("IOR: " + e);
         return false;
      }
      
      return true;
   }

   public void visit( MethodCall p ) throws ParseTreeException
   {
      if (!isMutantCall(p))
      {
	     super.visit(p);
	  } 
      else
      {
	     Expression expr = p.getReferenceExpr();
	     TypeName reftype = p.getReferenceType();

	     if (expr != null) 
	     {
            if (expr instanceof Leaf || expr instanceof ArrayAccess ||
		        expr instanceof FieldAccess || expr instanceof MethodCall ||
		        expr instanceof Variable) 
            {
   		       expr.accept( this );
	        } 
            else 
            {
		       writeParenthesis( expr );
	        }
	        out.print( "." );
	     } 
	     else if (reftype != null) 
	     {
  	        reftype.accept( this );
	        out.print( "." );
	     }

  	     String name = p.getName()+"_";
	     out.print( name );

	     ExpressionList args = p.getArguments();
	     writeArguments( args );
 	  }
   }

   boolean isTarget(MethodDeclaration m)
   {
      if ( !(target_name.equals(m.getName())) )  
    	 return false;
      
      if ( !(target_return_type.getName().equals(m.getReturnType().getName())) ) 
    	 return false;
      
      ParameterList my_parList = m.getParameters();
      if ( (target_parList == null) || (my_parList == null) ) 
    	 return false;
      
      int p1_num = target_parList.size();
      int p2_num = my_parList.size();
      
      if (p1_num != p2_num) 
    	 return false;
      
      for (int i=0; i<p1_num; i++)
      {
         if ( !(target_parList.get(i).getTypeSpecifier().getName().equals(my_parList.get(i).getTypeSpecifier().getName())) )
        	return false;
      }
      return true;
   }

   public void visit( MethodDeclaration p ) throws ParseTreeException
   {
      if (!(isTarget(p)))
      {
	     super.visit(p);
      } 
      else
      {              // Change name by appending "_"
         super.visit(p);   // keep original method for preventing execution errors
         this.evaluateDown();
         // -----------------------------------------------------

         mutated_line = line_num;
         String temp = p.getModifiers().toString() + " "
                         + p.getReturnType().getName()+ " "
                         + p.getName() +"("
                         + p.getParameters().toString()+")";
         writeLog(removeNewline(temp)+" is renamed to " + p.getName()+ "_ ");

         // ----------------------------------------------------

         writeTab();

         /*ModifierList*/
         ModifierList modifs = p.getModifiers();
         if (modifs != null) 
         {
            modifs.accept( this );
            if (! modifs.isEmptyAsRegular())  
               out.print( " " );
         }

         TypeName ts = p.getReturnType();
         ts.accept( this );

         out.print( " " );

         String name = p.getName()+"_";
         out.print( name );

         ParameterList params = p.getParameters();
         out.print( "(" );
         if (! params.isEmpty()) 
         {
	        out.print( " " );  
	        params.accept( this );  
	        out.print( " " );
         } 
         else 
         {
	        params.accept( this );
         }
         out.print( ")" );

         TypeName[] tnl = p.getThrows();
         if (tnl.length != 0) 
         {
	        out.println(); line_num++;
	        writeTab();  
	        writeTab();
            out.print( "throws " );
            tnl[0].accept( this );
            for (int i = 1; i < tnl.length; ++i) 
            {
               out.print ( ", " );
               tnl[i].accept( this );
            }
         }

         StatementList bl = p.getBody();
         if (bl == null) 
         {
	        out.print( ";" );
         } 
         else 
         {
	        out.println(); line_num++; writeTab();
	        out.print( "{" );
	        if (bl.isEmpty()) 
	        {
		       bl.accept( this );
	        } 
	        else 
	        {
		       out.println(); 
		       line_num++;
		       pushNest();  
		       bl.accept( this );  
		       popNest();
		       writeTab();
	        }
	        out.print( "}" );
         }
         out.println(); 
         line_num++;
         this.evaluateUp();
      }
   }
}
