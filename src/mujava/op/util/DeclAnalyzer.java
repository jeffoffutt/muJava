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
package mujava.op.util;

import mujava.MutationSystem;
import openjava.mop.*;
import openjava.ptree.CompilationUnit;

import java.io.*;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class DeclAnalyzer extends OJClass
{
    public int num = 0;

	 public String getMuantID(){
		String str = getClassName()+ "_" + this.num;
		return str;
	 }

    public String getSourceName(OJClass clazz){
		// make directory for the mutant
		String dir_name = MutationSystem.MUTANT_PATH+"/"+getClassName()+"_"+this.num;
		File f = new File(dir_name);
		f.mkdir();

		// return file name
		String name;
		name = dir_name + "/" +  MutationSystem.CLASS_NAME+".java";
      //name = MutationSystem.getPrefix()+getClassName()+"_"+this.num+".java";
      return name;
    }

    public String getClassName(){
      Class cc = this.getClass();
      return exclude(cc.getName(),cc.getPackage().getName());
    }

    public String exclude(String a, String b)
    {
      return a.substring(b.length()+1,a.length());
    }


    public OJClass bindedType(String name){
	Environment env = getEnvironment();
	OJClass bindedtype = env.lookupBind( name );
	return bindedtype;
    }

    // Examine if OJField f1 and f2 are same.
    // It is used for hiding variable.
    public boolean equalNameAndType(OJField f1,OJField f2){
      return (f1.getName().equals( f2.getName() ))
		    && (f1.getType() == f2.getType());
    }


    public void translateDefinition(CompilationUnit comp_unit) throws MOPException {
        ;
    }

    private boolean isSameParameter(OJMethod m1, OJMethod m2){
    	OJClass[] params1 = m1.getParameterTypes();
    	OJClass[] params2 = m2.getParameterTypes();
    	if(params1.length!=params2.length) return false;
    	for(int i=0;i<params2.length;++i){
    		if(params1[i] != params2[i]) return false;
    	}
    	return false;
    }
    
    // Examine if OJMethod m1 and m2 has same method signature.
    // It is used for overriding method.
    public boolean sameSignature(OJMethod m1,OJMethod m2){
      return ( m1.getName().equals(m2.getName()) 
		    &&  m1.getReturnType().equals(m2.getReturnType()) 
		    && isSameParameter(m1,m2) );
    }

    public PrintWriter getPrintWriter(String f_name) throws IOException{
      File outfile = new File(f_name);
      FileWriter fout = new FileWriter( outfile );
      PrintWriter out = new PrintWriter( fout );
      return out;
    }


    public DeclAnalyzer( openjava.mop.Environment oj_param0, openjava.mop.OJClass oj_param1, openjava.ptree.ClassDeclaration oj_param2 )
    {
        super( oj_param0, oj_param1, oj_param2 );
    }

    public DeclAnalyzer( java.lang.Class oj_param0, openjava.mop.MetaInfo oj_param1 )
    {
        super( oj_param0, oj_param1 );
    }

}
