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
import java.util.*;
import java.lang.Integer;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 



public class OverloadingHelper
{
   int num = 0;

   /**
    * Determine whether the return types of two methods are of
    * the same type 
    * @param m1 - method 1
    * @param m2 - method 2
    * @return true - same type
    */
   public boolean sameReturnType(OJMethod m1, OJMethod m2)
   {
      return ( (m1.getReturnType()).equals(m2.getReturnType()));
   }

   /**
    * Examine the compatibility of two parameters
    * @param big_m
    * @param small_m
    * @return true - compatible 
    */
   public boolean compatibleParameter(OJMethod big_m, OJMethod small_m)
   {
      // m1 > m2
      OJClass[] bigP = big_m.getParameterTypes();
      OJClass[] smallP = small_m.getParameterTypes();

      int length = bigP.length;

      if (smallP.length > bigP.length) 
    	 return false;
      
      if (smallP.length == 0) 
    	 return true;

      boolean[] flag = new boolean[length];
      for (int i=0; i<length; i++)
      {
         flag[i] = false;
      }

      int find_num = 0;

      for (int i=0; i<smallP.length ; i++)
      {
         for (int j=0; j<bigP.length ; j++)
         {
	        if ( (smallP[i].getName()).equals(bigP[j].getName()) && !flag[j])
	        {
	           flag[j] = true;
	           find_num++;
	           break;
	        }
         }
      }
      
      if (find_num != smallP.length) 
    	 return false;
      else 
    	 return true;
   }

   public Vector locationForParameterType(OJMethod big_m, OJMethod small_m)
   {
      int i, j;
      Vector locInfo = new Vector();
      ParameterTypeInfo par_info = null;

      OJClass[] bigP = big_m.getParameterTypes();
      OJClass[] smallP = small_m.getParameterTypes();

      boolean[] check_flag = new boolean[smallP.length];
      for (i=0; i<smallP.length ; i++)
      {
         check_flag[i] = false;
      }

      for ( i=0; i<smallP.length ; i++ )
      {
         if (!check_flag[i])
         {
	        par_info = new ParameterTypeInfo();
	        par_info.type_name = smallP[i].getName();
	        par_info.sub_position.add(new Integer(i));
	        check_flag[i] = true;
	        for ( j=i+1; j<smallP.length; j++)
	        {
	           if (par_info.type_name.equals(smallP[j].getName()) )
	           {
	              par_info.sub_position.add(new Integer(j));
	              check_flag[j] = true;
	           }
	        }
	        
	        for ( j=0; j<bigP.length ; j++)
	        {
	           if (par_info.type_name.equals(bigP[j].getName()) )
	           {
  	              par_info.original_position.add(new Integer(j));
	           }
	        }
	        locInfo.add(par_info);
         } 
      }

      int n, r;
      for (i=0; i<locInfo.size(); i++)
      {
         par_info = (ParameterTypeInfo)locInfo.elementAt(i);
         n = par_info.original_position.size();
         r = par_info.sub_position.size();
         par_info.combination_num = getPermutationNum(n,r);
         par_info.position = new int[par_info.combination_num][r];
         int[] n_list = new int[n];
         
         for (j=0; j<n; j++)
         {
	        n_list[j] = j;
         }
         
         genEachPermutation(par_info.position, n_list, r, par_info.combination_num, 0);
      }
      return locInfo;
   }


   public int[][] genCompatibleLocations(OJMethod big_m, OJMethod small_m)
   {
      int i, num;
      int[][] parIndex = null;
      ParameterTypeInfo par_info;

      Vector parTypeLocs = locationForParameterType(big_m, small_m);

      if (parTypeLocs.size() == 0)
      {
         return null;
      }
      else
      {
         num = 1;
         for ( i=0; i<parTypeLocs.size(); i++)
         {
	        par_info = (ParameterTypeInfo)parTypeLocs.elementAt(i);
	        num = par_info.combination_num * num;
         }
         try
         {
	        parIndex = new int[num][small_m.getParameterTypes().length];
	        genIndex(parIndex, parTypeLocs, num, 0, 0);
         } catch (Exception e)
         {
	        System.out.println("error  : " + e);
         }
         return parIndex;
      }
   }


   // Calculate nPr
   public int getPermutationNum(int n, int r)
   {
      int result = 1;
      for (int i=n; i>(n-r) ; i--)
      {
         result = result*i;
      }
      return result;
   }


   // nCr = n*(n-1)*(n-2) ... *(n-r+1)
   public void genEachPermutation(int[][] list, int[] n, int r, int num, int start)
   {
      int i, j, repeat, index;
      repeat = getPermutationNum(n.length-1, r-1);
      index = start;
      try
      {
         for (i=0; i<n.length; i++)
         {
            // nPr�� ������ ������ �κ��϶� n*(n-1)* ... *1
	        if (n.length == 2 && r == 2)
	        {
	           list[index][0] = n[i%2];
	           list[index][1] = n[(i+1)%2];
	           index++;
	        }
	        else 
	        { // nCr�� ������ ��
	           for (j=0; j<repeat; j++)
	           {
	              list[index][r-1] = n[i];
	              index++;
	           }
     
	           if (repeat != 1)
	           {
	              int[] sub_n = new int[n.length-1];
	              removeElement(n, sub_n, n[i]);
	              genEachPermutation(list, sub_n, r-1, repeat, start+i*repeat);
	           }   
	        }
         }
      } catch (Exception e) {
         System.err.println("error  " + e);
      }
   }



  // ------------------------------------------------------------
  // list : permutation information for all parameter type
  // parLoc : set of information for each parameter
  // num : number of generated permutation
  // parIndex : parameter Type index which to generate permutation
  // start : start index for list
   public void genIndex(int[][] list, Vector parLoc, int num, int parIndex, int start)
   {
      if (parIndex<parLoc.size())
      {
         ParameterTypeInfo par_info;
         int i, j, k, temp;
         int repeat,index;
         int length = parLoc.size();
         Integer original_value, sub_value;

         repeat = 1;
         for (i=length-1; i>parIndex; i--)
         {
	        par_info = (ParameterTypeInfo)parLoc.elementAt(i);
	        repeat = par_info.combination_num*repeat;
         }

         index = start;
 
         try
         {
	        par_info = (ParameterTypeInfo)parLoc.elementAt(parIndex);
	        for (i=0; i<par_info.combination_num; i++)
	        {
	           for (j=0; j<repeat; j++)
	           {
	              for (k=0; k<par_info.sub_position.size(); k++)
	              {
	                 temp = par_info.position[i][k];
	                 original_value = (Integer)par_info.original_position.elementAt(temp);
	                 sub_value = (Integer)par_info.sub_position.elementAt(k);
	                 list[index][sub_value.intValue()] = original_value.intValue();
	              }
	              index++;
	           }
	           if ( repeat != 1)
	           {
	              genIndex(list, parLoc, repeat, parIndex+1, start+i*repeat);
	           }
	        }
         } catch (Exception e) {
	        System.err.println("error  " + e);
         }
      }
   }



   private void removeElement(int[] original, int[] target, int element)
   {
      int length = original.length;
      int index = 0;
      for (int i=0; i<length; i++)
      {
         if (original[i] != element)
         {
	        target[index] = original[i];
	        index++;
         }
      }
   }

}


/**
 * Template containing parameters' type information
 */
class ParameterTypeInfo
{
   String type_name = null;
   Vector original_position = new Vector();
   Vector sub_position = new Vector();
   int combination_num = 0;
   int[][] position = null;
}
