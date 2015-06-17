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



package mujava.gui;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import mujava.*;
import mujava.util.Debug;


/**
 * <p>Template for generating mutants</p>
 * <p>  supporting function: 
 *      (1) choose Java program(s) to be tested,
 *      (2) choose mutation operator(s) to applied </p>  
 * @author Yu-Seung Ma
 * @version 1.0
  */


public class MutantsGenPanel extends JPanel 
{
   private static final long serialVersionUID = 103L;

   GenMutantsMain parent_frame;

   JButton runB = new JButton("Generate");

   JComboBox logCB = new JComboBox(new String[]{"1","2","3"});

   JTable fileTable = new JTable();
   JButton fileNoneB = new JButton("None");
   JButton fileAllB = new JButton("All");

   JTable traditionalOpTable = new JTable();
   JButton traditionalNoneB = new JButton("None");
   JButton traditionalAllB = new JButton("All");

   JTable classOpTable = new JTable();
   JButton classNoneB = new JButton("None");
   JButton classAllB = new JButton("All");
   
   
   // Upsorn (05/18/2009): add button for mutation operators' description
//   JButton descriptionB = new JButton("Mutation operators\' description");
   
   
   public MutantsGenPanel(GenMutantsMain parent_frame)
   {
      try 
      {
         this.parent_frame = parent_frame;
         jbInit();
      } catch (Exception ex) 
      {
         ex.printStackTrace();
      }
   }

   /**
    * Initialization
    */
   void jbInit()
   {
      this.setLayout (new BoxLayout (this, BoxLayout.LINE_AXIS));
      JPanel leftPanel = new JPanel();
      JPanel rightPanel = new JPanel();

      // LEFT part
      leftPanel.setLayout (new BoxLayout (leftPanel, BoxLayout.PAGE_AXIS));

      JPanel usagePanel = new JPanel();
      usagePanel.setLayout(new FlowLayout(FlowLayout.LEADING));

      // instruction
      JPanel tempP = new JPanel();
      JLabel temp  = new JLabel("   Usage : " );
      temp.setForeground(Color.gray);
      tempP.add(temp);
      tempP.setPreferredSize(new Dimension(70, 70));
      tempP.setBorder(new EtchedBorder());

      JPanel usgeContentP = new JPanel();
      usgeContentP.setLayout(new BoxLayout(usgeContentP, BoxLayout.PAGE_AXIS));
      usgeContentP.add(new JLabel(" [1] Select files to test"));
      usgeContentP.add(new JLabel(" [2] Select mutation operators to apply"));
      usgeContentP.add(new JLabel(" [3] Push \"RUN\" button"));
      usgeContentP.add(new JLabel(" [4] Wait with endurance. ^^;"));
      usagePanel.add(tempP);
      usagePanel.add(usgeContentP);
      usagePanel.setBorder(new EtchedBorder());
      leftPanel.add(usagePanel);

      leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

      
      // list of (target) files to be tested
      JPanel filePanel = new JPanel();
      filePanel.setLayout(new BorderLayout());
      JScrollPane fileSP = new JScrollPane();
      FileTableModel fTableModel = new FileTableModel(MutationSystem.getNewTragetFiles());
      fileTable = new JTable(fTableModel);
      initFileColumnSizes(fileTable,fTableModel);
      fileSP.getViewport().add(fileTable, null);
      fileSP.setPreferredSize(new Dimension(500, 500));
      leftPanel.add(fileSP);

      leftPanel.add(Box.createRigidArea(new Dimension(10, 10)));

      JPanel fileBP = new JPanel();
      fileBP.setLayout(new BoxLayout(fileBP, BoxLayout.LINE_AXIS));
      fileBP.add(Box.createRigidArea(new Dimension(10, 10)));
      fileBP.add(fileNoneB);
      fileNoneB.addMouseListener(new java.awt.event.MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            fileNoneB_mouseClicked(e);
         }
      });
      fileBP.add(Box.createRigidArea(new Dimension(10, 10)));
      fileBP.add(fileAllB);
      fileAllB.addMouseListener(new java.awt.event.MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            fileAllB_mouseClicked(e);
         }
      });

      // Debug level Add
      JPanel logP = new JPanel();
      JLabel logL= new JLabel("Log level ");
      logP.add(logL);
      //logCB.setEditable(false);
      logP.add(logCB);
      logCB.setPreferredSize(new Dimension(50,25));
      logCB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            changeLogLevel();
         }
      });
      leftPanel.add(logP);

      fileBP.add(Box.createHorizontalGlue());
      
      // Generate button
      runB.setBackground(Color.YELLOW);
      fileBP.add(runB);
      runB.addMouseListener(new java.awt.event.MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            runB_mouseClicked(e);
         }
      });
      leftPanel.add(fileBP);
      fileBP.add(Box.createRigidArea(new Dimension(10, 10)));
      leftPanel.add(Box.createRigidArea(new Dimension(20, 20)));


      // RIGHT part
      rightPanel.setPreferredSize(new Dimension(300, 660));
      rightPanel.setMaximumSize(new Dimension(300, 660));
      rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
      rightPanel.setBorder(new EtchedBorder());


      // Upsorn (05/18/2009): add button for mutation operators' description
//      JPanel descriptionBPanel = new JPanel();
//      descriptionBPanel.setLayout(new FlowLayout());
//      descriptionBPanel.add(descriptionB);
//      descriptionB.addMouseListener(new java.awt.event.MouseAdapter()
//      {
//         public void mouseClicked(MouseEvent e)
//         {
//            descriptionB_mouseClicked(e);
//         }
//      });
//      rightPanel.add(descriptionBPanel);
       
      
      JTextField titleOP= new JTextField(" Java Mutation Operator");
      titleOP.setBackground(Color.black);
      titleOP.setPreferredSize(new Dimension(300, 30));
      titleOP.setMaximumSize(new Dimension(300, 30));
      titleOP.setEnabled(false);
      rightPanel.add(titleOP);

      // method-level operator selection
      JPanel traditional_operator_panel = new JPanel();
      traditional_operator_panel.setBorder(new TitledBorder("Method-level"));
      traditional_operator_panel.setPreferredSize(new Dimension(140, 550));
      traditional_operator_panel.setMaximumSize(new Dimension(140, 550));

      traditional_operator_panel.setLayout(new BoxLayout(traditional_operator_panel, BoxLayout.PAGE_AXIS));
      JScrollPane traditional_operator_scrollP = new JScrollPane();
      traditional_operator_panel.add(traditional_operator_scrollP);
      TMOTableModel tmTableModel = new TMOTableModel();
      traditionalOpTable = new JTable(tmTableModel);
      initColumnSizes(traditionalOpTable,tmTableModel);
      traditional_operator_scrollP.getViewport().add(traditionalOpTable, null);
      traditional_operator_scrollP.setPreferredSize(new Dimension(115, 400));
      traditional_operator_scrollP.setMaximumSize(new Dimension(115, 400));

      traditional_operator_panel.add(traditional_operator_scrollP);
      JPanel traditionalBPanel = new JPanel();
      traditionalBPanel.setLayout(new FlowLayout());
      traditionalBPanel.add(traditionalNoneB);
      traditionalNoneB.addMouseListener(new java.awt.event.MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            traditionalNoneB_mouseClicked(e);
         }
      });
      traditionalBPanel.add(traditionalAllB);
      traditionalAllB.addMouseListener(new java.awt.event.MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            traditionalAllB_mouseClicked(e);
         }
      });
      traditional_operator_panel.add(traditionalBPanel);

      
      // class-level operator selection 
      JPanel class_operator_panel = new JPanel();
      class_operator_panel.setBorder(new TitledBorder("Class-level"));
      class_operator_panel.setPreferredSize(new Dimension(140, 550));
      class_operator_panel.setMaximumSize(new Dimension(140, 550));
      class_operator_panel.setLayout(new BoxLayout(class_operator_panel,BoxLayout.PAGE_AXIS));
      JScrollPane class_operator_scrollP = new JScrollPane();
      class_operator_panel.add(class_operator_scrollP);
      CMOTableModel cmTableModel = new CMOTableModel();
      classOpTable = new JTable(cmTableModel);

      initColumnSizes(classOpTable,cmTableModel);
      class_operator_scrollP.getViewport().add(classOpTable, null);
      class_operator_scrollP.setPreferredSize(new Dimension(115, 550));
      class_operator_scrollP.setMaximumSize(new Dimension(115, 550));

      class_operator_panel.add(class_operator_scrollP);
      JPanel classBPanel = new JPanel();
      classBPanel.setLayout(new FlowLayout());
      classBPanel.add(classNoneB);
      classNoneB.addMouseListener(new java.awt.event.MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            classNoneB_mouseClicked(e);
         }
      });
      classBPanel.add(classAllB);
      classAllB.addMouseListener(new java.awt.event.MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            classAllB_mouseClicked(e);
         }
      });
      class_operator_panel.add(classBPanel);

      // This is the original part.
      //rightPanel.add(Box.createRigidArea(new Dimension(0,5)));
      // Traditional operators ar not supported they are newly designed.
      //rightPanel.add(traditional_operator_panel);
      //rightPanel.add(temp_traditional_panel);
      // rightPanel.add(class_operator_panel);


      //----
      JPanel operator_panel = new JPanel();
      operator_panel.setLayout(new FlowLayout());
      operator_panel.add(traditional_operator_panel);
      operator_panel.add(class_operator_panel);
      //rightPanel.add(Box.createRigidArea(new Dimension(0,4)));
      rightPanel.add(operator_panel);

      this.add(leftPanel);
      this.add(Box.createRigidArea(new Dimension(10, 10)));
      this.add(rightPanel);
   }

 
   void changeLogLevel()
   {
      String selectedLevel = logCB.getSelectedItem().toString();
      if (selectedLevel == null)        return;
      if (selectedLevel.equals("1"))
      {
         Debug.setDebugLevel(1);
      }
      else if (selectedLevel.equals("2"))
      {
         Debug.setDebugLevel(2);
      }
      else
      {
         Debug.setDebugLevel(3);
      }
   }

   protected void initTripleColumnWidth(JTable table, AbstractTableModel model, 
                                               int w1, int w2, int w3) 
   {
      TableColumn column = null;

      for (int i = 0; i < table.getColumnCount(); i++) 
      {
         column = table.getColumnModel().getColumn(i);
         switch(i) 
         {
            case 0 :  column.setMaxWidth(w1);
                      break;
            case 1 :  column.setMaxWidth(w2);
                      break;
            case 2 :  column.setMaxWidth(w3);
                      break;
         }
      }
   }


   protected void initColumnSizes(JTable table, AbstractTableModel model)
   {
      initTripleColumnWidth(table, model, 30, 90, 80);
/*
      TableColumn column = null;

      for (int i = 0; i < table.getColumnCount(); i++) 
      {
         column = table.getColumnModel().getColumn(i);
         switch(i)
         {
            case 0 :  column.setMaxWidth(30);
                      break;
            case 1 :  column.setMaxWidth(90);
                      break;
            case 2 :  column.setMaxWidth(80);
                      break;
         }
      }*/
   }

   
   void fileAllB_mouseClicked(MouseEvent e)
   {
      FileTableModel table = (FileTableModel)fileTable.getModel();
      table.setAllSelectValue(true);
      fileTable.setModel(table);
      fileTable.repaint();
   }

   void fileNoneB_mouseClicked(MouseEvent e)
   {
      FileTableModel table = (FileTableModel)fileTable.getModel();
      table.setAllSelectValue(false);
      fileTable.setModel(table);
      fileTable.repaint();
   }

   void runB_mouseClicked(MouseEvent e)
   {
   	//check if any files are selected, return an error message if no files have been selected
      FileTableModel fTableModel = (FileTableModel)fileTable.getModel();
      String[] file_list = fTableModel.getSelectedFiles();
      if (file_list == null || file_list.length == 0)
      {
         System.err.println("[ERROR] No class is selected. Please select one or more .java files for which you'd like to generate mutants.");
         return;
      }
      
      //check if any operators are selected, return an error message if no files have been selected
      CMOTableModel cmoTableModel = (CMOTableModel)classOpTable.getModel();
      String[] class_ops = cmoTableModel.getSelectedOprators();
 
      TMOTableModel tmoTableModel = (TMOTableModel)traditionalOpTable.getModel();
      String[] traditional_ops = tmoTableModel.getSelectedOprators();

      if ( (class_ops == null || class_ops.length == 0) && 
           (traditional_ops == null || traditional_ops.length == 0) )
      {
         System.out.println("[ERROR] No operators are selected. Please select one or more mutation operators."); 
         return;
      }
      
      //disable the button
      runB.setEnabled(false);
      
      for (int i=0; i<file_list.length; i++)
      {
      // file_name = ABSTRACT_PATH - MutationSystem.SRC_PATH
      // For example: org/apache/bcel/Class.java
         String file_name = file_list[i];
         try
         {
            //System.out.println(i + " : " + file_name);
            // [1] Examine if the target class is interface or abstract class
            //     In that case, we can't apply mutation testing.

            // Generate class name from file_name
            String temp = file_name.substring(0,file_name.length()-".java".length());
            String class_name="";
            
            for (int j=0; j<temp.length(); j++)
            {
               if ( (temp.charAt(j) == '\\') || (temp.charAt(j) == '/') )
               {
                  class_name = class_name + ".";
               } 
               else
               {
                  class_name = class_name + temp.charAt(j);
               }
            }
           
            int class_type = MutationSystem.getClassType(class_name);
            
			if (class_type == MutationSystem.NORMAL)
			{   // do nothing?
			} 
			else if (class_type == MutationSystem.MAIN)
			{
               System.out.println(" -- "  + file_name + " class contains 'static void main()' method.");
               System.out.println("    Pleas note that mutants are not generated for the 'static void main()' method");
            }
			//Added on 1/19/2013, no mutants will be generated for a class having only one main method
			else if(class_type == MutationSystem.MAIN_ONLY){
				System.out.println("Class " + file_name + " has only the 'static void main()' method and no mutants will be generated.");
				break;
			}
//			else
//			{
//               switch (class_type)
//               {
//                  case MutationSystem.INTERFACE :
//                            System.out.println(" -- Can't apply because " + file_name+ " is 'interface' ");
//                            break;
//                  case MutationSystem.ABSTRACT :
//                            System.out.println(" -- Can't apply because " + file_name+ " is 'abstract' class ");
//                            break;
//                  case MutationSystem.APPLET :
//                            System.out.println(" -- Can't apply because " + file_name+ " is 'applet' class ");
//                            break;
//                  case MutationSystem.GUI :
//                            System.out.println(" -- Can't apply because " + file_name+ " is 'GUI' class ");
//                            break;
//               }
//               deleteDirectory();
//               continue;
//            }
			
            // [2] Apply mutation testing
            setMutationSystemPathFor(file_name);
            
            //File[] original_files = new File[1];
            //original_files[0] = new File(MutationSystem.SRC_PATH,file_name);
            
            File original_file = new File(MutationSystem.SRC_PATH, file_name);
           
            /*AllMutantsGenerator genEngine;
            genEngine = new AllMutantsGenerator(original_file,class_ops,traditional_ops);
            genEngine.makeMutants();
            genEngine.compileMutants();*/
            
            ClassMutantsGenerator cmGenEngine;
            
            //do not generate class mutants if no class mutation operator is selected
            if(class_ops != null){
	            cmGenEngine = new ClassMutantsGenerator(original_file,class_ops);   
	            cmGenEngine.makeMutants();      
	            cmGenEngine.compileMutants();
            }
            
            //do not generate traditional mutants if no class traditional operator is selected
            if(traditional_ops != null){
	            TraditionalMutantsGenerator tmGenEngine;
	            //System.out.println("original_file: " + original_file);
	            //System.out.println("traditional_ops: " + traditional_ops);
	            tmGenEngine = new TraditionalMutantsGenerator(original_file,traditional_ops);
	            tmGenEngine.makeMutants();
	            tmGenEngine.compileMutants();
            }

         } catch (OpenJavaException oje)
         {
            System.out.println("[OJException] " + file_name + " " + oje.toString());
            //System.out.println("Can't generate mutants for " +file_name + " because OpenJava " + oje.getMessage());
            deleteDirectory();
         } catch(Exception exp)
         {
            System.out.println("[Exception] " + file_name + " " + exp.toString());
            exp.printStackTrace();
            //System.out.println("Can't generate mutants for " +file_name + " due to exception" + exp.getClass().getName());
            //exp.printStackTrace();
            deleteDirectory();
         } catch(Error er)
         {
            System.out.println("[Error] " + file_name + " " + er.toString());
            System.out.println("MutantsGenPanel: ");
            er.printStackTrace();

            //System.out.println("Can't generate mutants for " +file_name + " due to error" + er.getClass().getName());
            deleteDirectory();
         }
      }
      runB.setEnabled(true);
      parent_frame.cvPanel.refreshEnv();
      parent_frame.tvPanel.refreshEnv();
      System.out.println("------------------------------------------------------------------");
      System.out.println("All files are handled");
   }

   
   void deleteDirectory()
   {
      File originalDir = new File(MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME
                                + "/" + MutationSystem.ORIGINAL_DIR_NAME);
      while (originalDir.delete())  
      {    // do nothing?
      }

      File cmDir = new File(MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME
                                + "/" + MutationSystem.CM_DIR_NAME);
      while (cmDir.delete()) 
      {    // do nothing?
      }

      File tmDir = new File (MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME
                                + "/" + MutationSystem.TM_DIR_NAME);
      while (tmDir.delete())
      {    // do nothing?
      }

      File myHomeDir = new File(MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME);
      while (myHomeDir.delete())
      {    // do nothing?
      }
   }


   void setMutationSystemPathFor(String file_name)
   {
      try
      {
         String temp;
         temp = file_name.substring(0, file_name.length()-".java".length());
         temp = temp.replace('/', '.');
         temp = temp.replace('\\', '.');
         int separator_index = temp.lastIndexOf(".");
         
         if (separator_index >= 0)
         {
            MutationSystem.CLASS_NAME=temp.substring(separator_index+1, temp.length());
         }
         else
         {
            MutationSystem.CLASS_NAME = temp;
         }

         String mutant_dir_path = MutationSystem.MUTANT_HOME + "/" + temp;
         File mutant_path = new File(mutant_dir_path);
         mutant_path.mkdir();

         String class_mutant_dir_path = mutant_dir_path + "/" + MutationSystem.CM_DIR_NAME;
         File class_mutant_path = new File(class_mutant_dir_path);
         class_mutant_path.mkdir();

         String traditional_mutant_dir_path = mutant_dir_path + "/" + MutationSystem.TM_DIR_NAME;
         File traditional_mutant_path = new File(traditional_mutant_dir_path);
         traditional_mutant_path.mkdir();

         String original_dir_path = mutant_dir_path + "/" + MutationSystem.ORIGINAL_DIR_NAME;
         File original_path = new File(original_dir_path);
         original_path.mkdir();

         MutationSystem.CLASS_MUTANT_PATH = class_mutant_dir_path;
         MutationSystem.TRADITIONAL_MUTANT_PATH = traditional_mutant_dir_path;
         MutationSystem.ORIGINAL_PATH = original_dir_path;
         MutationSystem.DIR_NAME = temp;
      } catch(Exception e)
      {
         System.err.println(e);
      }
   }

   
   void descriptionB_mouseClicked(MouseEvent e)
   {
         
   }
   
   void traditionalAllB_mouseClicked(MouseEvent e)
   {
      TMOTableModel table = (TMOTableModel) traditionalOpTable.getModel();
      table.setAllSelectValue(true);
      traditionalOpTable.setModel(table);
      traditionalOpTable.repaint();
   }
 
   void traditionalNoneB_mouseClicked(MouseEvent e)
   {
      TMOTableModel table = (TMOTableModel)traditionalOpTable.getModel();
      table.setAllSelectValue(false);
      traditionalOpTable.setModel(table);
      traditionalOpTable.repaint();
   }

   void classAllB_mouseClicked(MouseEvent e)
   {
      CMOTableModel table = (CMOTableModel)classOpTable.getModel();
      table.setAllSelectValue(true);
      classOpTable.setModel(table);
      classOpTable.repaint();
   }
  
   void classNoneB_mouseClicked(MouseEvent e)
   {
      CMOTableModel table = (CMOTableModel)classOpTable.getModel();
      table.setAllSelectValue(false);
      classOpTable.setModel(table);
      classOpTable.repaint();
   }

   protected void initFileColumnSizes(JTable table, AbstractTableModel model) 
   {
      initTripleColumnWidth(table,model,30,700,80);
   } 
}

/**
 * <p>Description: Template containing method-level mutation operators
 *                 and class-level mutation operators</p>
 * <p>Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED </p>
 * @author Yu-Seung Ma
 * @version 1.0
 */
abstract class MOTableModel extends AbstractTableModel 
{
   String[] columnHeader = new String[]{"", "Operator"};
   String[] op;
   Object[][] data;

   static final int CMO = 0;
   static final int TMO = 1;

   abstract int getOperatorType();

   public MOTableModel()
   {
      if (getOperatorType() == CMO)
      {
         op = MutationSystem.cm_operators;
      } 
      else
      {
         op = MutationSystem.tm_operators;
      }

      data = new Object[op.length][2];
      for (int i=0; i<op.length; i++)
      {
         data[i][0] = new Boolean(false);
         data[i][1] = op[i];
      }
   }

   public void setAllSelectValue(boolean b)
   {
      for (int i=0; i<data.length; i++) 
      {
         data[i][0] = new Boolean(b);
      }
   }

   public String getColumnName(int col)
   {
      return columnHeader[col];
   }

   public int getColumnCount() 
   {
      return columnHeader.length; 
   }

   public Object getValueAt(int row, int col) 
   {
      return data[row][col];
   }

   public int getRowCount() 
   {
      return data.length; 
   }

   public Class getColumnClass(int c) 
   {
      return getValueAt(0, c).getClass();
   }

   public void setValueAt(Object value, int row, int col) 
   {
      if (data[0][col] instanceof Integer && 
          !(value instanceof Integer)) 
      {
         try 
         {
            data[row][col] = new Integer(value.toString());
            fireTableCellUpdated(row, col);
         } catch (NumberFormatException e) 
         {    // do nothing?
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
      //Note that the data/cell address is constant,
      //no matter where the cell appears on screen.
      if (col < 1) 
         return true;
      else 
         return false;
   }

   public String[] getSelectedOprators()
   {
      Vector set = new Vector();
      int numRows = getRowCount();
      int i;
      for (i=0; i<numRows; i++)
      {
         if (data[i][0].toString().equals("true"))
         {
            set.add(data[i][1]);
         }
      }

      String[] names = new String[set.size()];
      if (set.size() > 0) 
      {
         for (i=0; i<set.size(); i++)
         {
            names[i] = set.get(i).toString();
         }
         return names;
      } 
      else 
      {
         return null;
      }
   }
}


class CMOTableModel extends MOTableModel 
{
   private static final long serialVersionUID = 104L;
   int getOperatorType(){    return CMO;    }
}


class TMOTableModel extends MOTableModel 
{
   private static final long serialVersionUID = 105L;
   int getOperatorType(){    return TMO;    }
}


class FileTableModel extends AbstractTableModel 
{
   private static final long serialVersionUID = 106L;

   String[] columnHeader = new String[]{"","File"};
   String[] op;

   Object[][] data;

   public FileTableModel(Vector value)
   {
      data = new Object[value.size()][2];
      for (int i=0; i<value.size(); i++)
      {
         data[i][0] = new Boolean(false);
         data[i][1] = value.get(i);
      }
   }
   
   public String getColumnName(int col)
   {
      return columnHeader[col];
   }

   public int getColumnCount() 
   {
      return columnHeader.length;
   }
   
   public void setAllSelectValue(boolean b)
   {
      for (int i=0; i<data.length; i++)
      {
         data[i][0] = new Boolean(b);
      }
   }
  
   public Object getValueAt(int row, int col) 
   {
      return data[row][col];
   }
  
   public int getRowCount() 
   {
      return data.length; 
   }

   public String[] getSelectedFiles()
   {
      Vector set = new Vector();
      int numRows = getRowCount();
      int i;
      
      for (i=0; i<numRows; i++)
      {
         if (data[i][0].toString().equals("true"))
            set.add(data[i][1]); 
      }

      String[] names = new String[set.size()];
      if (set.size() > 0) 
      {
         for (i=0; i<set.size(); i++)
         {
            names[i] = set.get(i).toString();
         }
         return names;
      } 
      else 
      {
         return null;
      }
   }

   public Class getColumnClass(int c) 
   {
      return getValueAt(0, c).getClass();
   }

   public void setValue(Object[] value)
   {
      data = new Object[value.length][2];
      for (int i=0; i<value.length; i++)
      {
         data[i][0] = new Boolean(true);
         data[i][1] = value[i];
      }
   }

   public void setValueAt(Object value, int row, int col) 
   {
      if (data[0][col] instanceof Integer && 
          !(value instanceof Integer)) 
      {
         try 
         {
            data[row][col] = new Integer(value.toString());
            fireTableCellUpdated(row, col);
         } catch (NumberFormatException e)    
         {  // do nothing?
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
     //Note that the data/cell address is constant,
     //no matter where the cell appears on screen.
      if (col < 1) 
      {
         return true;
      } 
      else 
      {
         return false;
      }
   }
}


