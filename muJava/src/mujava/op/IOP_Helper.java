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

/**
 * <p>Interface for generating IOP mutants</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public interface IOP_Helper
{
   // 4 mod exists.
   // -------------------------------------------------------------------
   // 1: first_line            // 2: last_line
   // 3: just one line up      // 4: just one line down
   // -------------------------------------------------------------------
   public static int FIRST = 1;
   public static int LAST = 2;
   public static int UP = 3;
   public static int DOWN = 4;
}
