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
import openjava.ptree.*;
import mujava.op.util.MutantCodeWriter;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class EHD_Writer extends MutantCodeWriter
{
  CatchBlock mutant = null;

  public EHD_Writer( String file_name,PrintWriter out ) {
	super(file_name,out);
  }

  public void setMutant(CatchBlock p){
    mutant = p;
  }


  public void visit( CatchBlock p ) throws ParseTreeException
  {
    if(isSameObject(p,mutant)){
        // -------------------------
        mutated_line = line_num;
        writeLog(" catch block for " +  p.getParameter().getTypeSpecifier().getName()+ " is deleted.");
        // -------------------------          }else{
    }else{
      super.visit(p);
    }
  }
}
