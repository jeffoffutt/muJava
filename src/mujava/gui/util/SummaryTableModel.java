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

package mujava.gui.util;

import javax.swing.table.AbstractTableModel;
import mujava.MutationSystem;

/**
 * <p>Template containing the result summary of mutants generated</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

abstract class SummaryTableModel extends AbstractTableModel 
{
   String[] columnHeader = new String[]{"Op","#"};
   String[] op;

   Object[][] data;

   static final int CMO = 0;
   static final int TMO = 1;
   static final int EMO = 2;

   abstract int getOperatorType();

   public SummaryTableModel()
   {
      switch (getOperatorType())
      {
         case CMO: op = MutationSystem.cm_operators;
                   break;
         case TMO: op = MutationSystem.tm_operators;
                   break;
         case EMO: op = MutationSystem.em_operators;
                   break;
      }
    
      data = new Object[op.length][2];
      for (int i=0; i<op.length; i++)
      {
         data[i][0] = op[i];
         data[i][1] = new Integer(0);
      }
   }

   /**
    * Retrieve a name of a given column of a result summary table
    */
   public String getColumnName(int col)
   {
      return columnHeader[col];
   }

   /**
    * Count the number of column of a result summary table
    */
   public int getColumnCount() 
   {
      return columnHeader.length;
   }

   /**
    * Retrieve value of a given row-column pair of a result summary table
    */
   public Object getValueAt(int row, int col) 
   {
      return data[row][col];
   }

   /**
    * Count the number of row of a result summary table
    */
   public int getRowCount() 
   {
      return data.length;
   }

   /**
    * Set value to a specified row-column pair of a result summary table
    */
   public void setValueAt(Object value, int row, int col) 
   {
      if (data[0][col] instanceof Integer && !(value instanceof Integer)) 
      {
         try 
         {
            data[row][col] = new Integer(value.toString());
            fireTableCellUpdated(row, col);
         } catch (NumberFormatException e) 
         {
        	// do nothing
         }
      } 
      else 
      {
         data[row][col] = value;
         fireTableCellUpdated(row, col);
      }
   }

   public boolean isCellEditable(int row, int col) 
   {
      return false;
   }
}
