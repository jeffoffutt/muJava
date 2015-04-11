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

import openjava.mop.*;
import openjava.ptree.*;

/**
 * <p>Generate ABS (Absolute Value Insertion) mutants --
 *    modify each arthmetic expression (and subexpression) by the function 
 *    <i>abs()</i>, <i>negAbs()</i>, and <i>faileOnZero()</i>. 
 * </p>
 * <p>-- <i>abs()</i> returns the absolute value of the expression<br/> 
 *    -- <i>negAbs()</i> returns the negative of the absolute value<br/> 
 *    -- <i>failOnZero()</i> tests whether the expression is zero 
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class ABS extends mujava.op.util.Mutator
{
   public ABS(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }
}
