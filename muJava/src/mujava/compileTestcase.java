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

import mujava.MutationSystem;
import mujava.util.Debug;
import mujava.util.ExtensionFilter;
import com.sun.tools.javac.Main;
import java.io.*;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class compileTestcase 
{
   public static void main(String[] args)
   {
      Debug.setDebugLevel(3);
      File f = new File(MutationSystem.TESTSET_PATH);
      String[] s = f.list(new ExtensionFilter("java"));
      String[] pars = new String[2+s.length];
      pars[0] = "-classpath";
      pars[1] = MutationSystem.CLASS_PATH;

      for (int i=0; i<s.length; i++)
      {
         pars[i+2] = MutationSystem.TESTSET_PATH + "/" + s[i];
      }
      try
      {
         // result = 0 : SUCCESS,   result = 1 : FALSE
         int result = Main.compile(pars,new PrintWriter(System.out));
         if (result == 0)
         {
            Debug.println("Compile Finished");
         }
      } catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
