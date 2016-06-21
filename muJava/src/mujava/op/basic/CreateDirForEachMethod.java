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

package mujava.op.basic;

import openjava.mop.*;
import openjava.ptree.*;
import java.io.*;
import mujava.MutationSystem;
/**
 * <p>
 * </p>
 * @author Jeff Offutt and Yu-Seung Ma
 * @version 1.0
  */
public class CreateDirForEachMethod extends MethodLevelMutator
{
   PrintWriter out = null;
   public CreateDirForEachMethod(FileEnvironment file_env, ClassDeclaration cdecl,
                                 CompilationUnit comp_unit, PrintWriter out)
   {
      super( file_env, comp_unit );
      this.out = out;
   }

   void createDirectory(String dir_name)
   { 
      out.println(dir_name);
      String absolute_dir_path = MutationSystem.MUTANT_PATH + "/" + dir_name;
      File dirF = new File(absolute_dir_path);
      dirF.mkdir();
   }

   public void visit(ConstructorDeclaration p) throws ParseTreeException 
   {
      createDirectory(getConstructorSignature(p));
   }

   public void visit(MethodDeclaration p) throws ParseTreeException
   {
      createDirectory(getMethodSignature(p));
   }
}
