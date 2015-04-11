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


/**
 * <p>Description: </p>
 * @author Jeff Offutt and Yu-Seung Ma
 * @version 1.0
  */ 


package mujava.openjava.extension;

import java.util.Enumeration;
import java.util.Vector;

import openjava.mop.ClosedEnvironment;
import openjava.mop.Environment;

public class ExtendedClosedEnvironment extends ClosedEnvironment{
	
   /* public ExtendedClosedEnvironment() {
       parent = null;//no op
    }*/

    public ExtendedClosedEnvironment( Environment env ) {
        super(env);
    }
	
    public String[] getAccessibleVariables(){
        Enumeration e = symbol_table.keys();
        Vector v = new Vector();
        while(e.hasMoreElements()){
          v.add((String)(e.nextElement(
  		  )));
        }
        int num = v.size();
        String[] results;
        if(num>0){
          results = new String[num];
          for(int i=0;i<num;i++){
            results[i] = (String)(v.get(i));
          }
          return results;
        }
        return null;
      }
}
