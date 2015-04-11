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
package mujava.op.exception;

import java.io.*;
import openjava.mop.*;
import openjava.ptree.*;
import mujava.util.InheritanceINFO;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class EHC extends mujava.op.util.Mutator
{
  public EHC(FileEnvironment file_env,ClassDeclaration cdecl,
    CompilationUnit comp_unit)
  {
	super( file_env, comp_unit );
  }

  public void visit( TryStatement p ) throws ParseTreeException
  {
    CatchList catchlist = p.getCatchList();
    if (! catchlist.isEmpty()) {
      int num = catchlist.size();
      if(num==1){
        StatementList finstmts = p.getFinallyBody();
        if(!finstmts.isEmpty()){
          generateEHC(catchlist.get(0),catchlist.get(0).getParameter().getTypeSpecifier().getName());
        }
      }else{
        for(int i=0;i<num;i++){
          generateEHC(catchlist.get(i),catchlist.get(i).getParameter().getTypeSpecifier().getName());
        }

      }
    }
  }

  public void generateEHC(CatchBlock p,String e_name){
    InheritanceINFO inf = mujava.MutationSystem.getInheritanceInfo(e_name);
    if(inf==null) return;
    InheritanceINFO parent = inf.getParent();
    if(parent!=null){
      String parent_name = parent.getClassName();
      outputToFile(p,parent_name);
      generateEHC(p,parent_name);
    }
  }

  public void outputToFile(CatchBlock p,String exception_type){
      if (comp_unit==null) return;
      String f_name;
      num++;
      f_name = getSourceName(this);
      String mutant_dir = getMuantID();

      try {
		 PrintWriter out = getPrintWriter(f_name);
		 EHC_Writer writer = new EHC_Writer( mutant_dir,out );
		 writer.setMutant(p,exception_type);
		 comp_unit.accept( writer );
		 out.flush();  out.close();
      } catch ( IOException e ) {
	    System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
	    System.err.println( "errors during printing " + f_name );
	    e.printStackTrace();
      }
   }

}
