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



package mujava.cli;

import java.util.ArrayList;
 /**
 * <p>
 * Description: Pair class used for storing results of mutation scores
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0  
 */
public class Pair
{
   public ArrayList<String> testSet;
   public double mutationScore;

   public Pair(ArrayList<String> testSet, double mutationScore)
   {
      this.testSet = testSet;
      this.mutationScore = mutationScore;
   }
   

}
