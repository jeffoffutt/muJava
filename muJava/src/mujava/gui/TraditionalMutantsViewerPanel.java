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

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import mujava.MutationSystem;
import mujava.util.MutantDirFilter;
import java.util.Vector;
import mujava.gui.util.TMSummaryTableModel;


/**
 * <p>Template for viewing method-level mutants: show original source code,
 *    source code of the selected mutants, summary of mutants </p>  
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class TraditionalMutantsViewerPanel  extends MutantsViewerPanel  
{
   private static final long serialVersionUID = 110L;

   JComboBox methodCB = new JComboBox(new String[]{"All Methods"});

   // Initialization
   void jbInit() throws Exception 
   {
      this.setLayout(new FlowLayout());

      StyleConstants.setForeground(red_attr, Color.red);
      StyleConstants.setForeground(blue_attr, Color.blue);  
      StyleConstants.setForeground(black_attr, Color.black);

      /** summary table: containing the numbers of mutants by each
       *  method-level mutation operators along with the total number
       *  of mutants generated 
       */
      JPanel leftPanel = new JPanel();
      leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
      JLabel summaryL= new JLabel("* Summary *");
      leftPanel.add(summaryL);
      initSummaryTable();
      summaryTable.setEnabled(false);
      summaryPanel.getViewport().add(summaryTable);
      setSummaryTableSize();
      leftPanel.add(summaryPanel);
      leftPanel.add(totalLabel);
      summaryPanel.setBorder(new EmptyBorder(1, 1, 1, 1));

      JPanel rightPanel = new JPanel();
      rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));

      /** ComboBox for class (in which mutants will be generated) selection **/
      JPanel selectClassPanel = new JPanel();
      selectClassPanel.setLayout(new FlowLayout());
      JLabel selectClassLabel = new JLabel("   Select a class : ");
      selectClassPanel.add(selectClassLabel);
      //refreshEnv();
      classCB.setEditable(false);
      selectClassPanel.add(classCB);
      classCB.setPreferredSize(new Dimension(550, 25));
      classCB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            updateClassComboBox();
         }
      });

      /** ComboBox for method (in which mutants will be generated) selection **/
      JPanel selectMethodPanel = new JPanel();
      selectMethodPanel.setLayout(new FlowLayout());
      JLabel selectMethodLabel = new JLabel("                                   Select a method : ");
      selectMethodPanel.add(selectMethodLabel);
      methodCB.setEditable(false);
      selectMethodPanel.add(methodCB);
      methodCB.setPreferredSize(new Dimension(440,25));
      methodCB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            updateMethodComboBox();
         }
      });
       
      /** show a list of mutants to be selected for viewing -- 
       *  click on the mutant to display the original source and
       *  mutated code 
       */      
      JPanel contentPanel = new JPanel();
      JScrollPane leftContentSP = new JScrollPane();
      leftContentSP.getViewport().add(mList, null);
      leftContentSP.setPreferredSize(new Dimension(100, 580));
      contentPanel.add(leftContentSP);
      mList.addMouseListener(new java.awt.event.MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            mList_mouseClicked(e);
         }
      });

      JPanel rightContentPanel = new JPanel();
      rightContentPanel.setLayout(new BoxLayout(rightContentPanel,BoxLayout.PAGE_AXIS));

      /** show the line mutated */
      changeTF.setPreferredSize(new Dimension(550, 40));
      rightContentPanel.add(changeTF);
    
      /** show the source code of the original file and the mutant */
      originalSP.setPreferredSize(new Dimension(550, 270));
      mutantSP.setPreferredSize(new Dimension(550, 270));
      originalSP.setBorder(new TitledBorder("Original"));
      mutantSP.setBorder(new TitledBorder("Mutant"));
      mutantSP.getViewport().add(mutantTP, null);
      originalSP.getViewport().add(originalTP, null);
      rightContentPanel.add(originalSP);
      rightContentPanel.add(mutantSP);
      contentPanel.add(rightContentPanel);

      rightPanel.add(selectClassPanel);
      rightPanel.add(selectMethodPanel);
      rightPanel.add(Box.createRigidArea(new Dimension(10, 10)));
      rightPanel.add(contentPanel);

      contentPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

      leftPanel.setPreferredSize(new Dimension(100,500));
      this.add(leftPanel);
      this.add(rightPanel);

      refreshEnv();
   }

   void updateContents(String methodSignature)
   {
      setMutantPath();
      File mutant_dir = new File(getMutantPath() + "/" + methodSignature);
      String[] mutants = mutant_dir.list(new MutantDirFilter());
      showGeneratedMutantsNum(mutants);
      mList.setListData(mutants);
      mList.repaint();
      clearSourceContents();
      showOriginal();
   }

   void updateContents()
   {
      try
      {
         Vector v = new Vector();
         setMutantPath();
         File f = new File(getMutantPath(), "method_list");
         FileReader r = new FileReader(f);
         BufferedReader reader = new BufferedReader(r);
         String methodSignature = reader.readLine();
         while (methodSignature != null)
         {
            File mutant_dir = new File(getMutantPath() + "/" + methodSignature);
            String[] mutants = mutant_dir.list(new MutantDirFilter());
            
            for (int i=0; i<mutants.length; i++)
            {
               v.add(mutants[i]);
            }
            mutants = null;
            methodSignature = reader.readLine();
         }
         reader.close();
         int mutant_num = v.size();
         String[] mutants = new String[mutant_num];
         
         for (int i=0; i<mutant_num; i++)
         {
            mutants[i] = v.get(i).toString();
         }
         showGeneratedMutantsNum(mutants);
         mList.setListData(mutants);
         mList.repaint();
         clearSourceContents();
         showOriginal();
      } catch (Exception e)
      {
         System.err.println("Error in update() in TraditionalMutantsViewerPanel.java");
      } 
   }

   /** Change contents for the newly selected class */
   void updateMethodComboBox()
   {
      Object item = methodCB.getSelectedItem();
      if (item == null) 
         return;
      String methodSignature = item.toString();
      
      if (methodSignature == null) 
    	  return;
      
      if (methodSignature.equals("All method"))
      {
         updateContents();
      }
      else
      {
         updateContents(methodSignature);
      }
   }

   /** Change contents for the newly selected class */
   void updateClassComboBox()
   {
      Object item = classCB.getSelectedItem();
      if (item == null) 
    	 return;
      
      target_dir = item.toString();
      if (target_dir == null) 
    	 return;
      
      if (isProperClass(target_dir))
      {
         MutationSystem.setJMutationPaths(target_dir);
         methodCB.removeAllItems();
         methodCB.addItem("All method");
         try
         {
            File f = new File(MutationSystem.TRADITIONAL_MUTANT_PATH, "method_list");
            FileReader r = new FileReader(f);
            BufferedReader reader = new BufferedReader(r);
            String str = reader.readLine();
            while (str != null)
            {
               methodCB.addItem(str);
               str = reader.readLine();
            }
            reader.close();
            clearSourceContents();
            showOriginal();
            updateMethodComboBox();
         } catch (java.io.FileNotFoundException fnfe)
         {  
         } catch (Exception e)
         {
            System.err.println("error at updateClassComboBox() in TraditionalMutantsViewerPanel");
         }
      }
      else
      {
         clearSourceContents();
         mList.setListData(new Vector());
         mList.repaint();
         showGeneratedMutantsNum(null);
      }
      this.repaint();
   }

   String getMethodSignature(String str)
   {
      int start_index = str.indexOf(MutationSystem.LOG_IDENTIFIER);
      int end_index = str.lastIndexOf(MutationSystem.LOG_IDENTIFIER);
      String temp = str.substring(start_index+1, end_index);
      end_index = temp.lastIndexOf(MutationSystem.LOG_IDENTIFIER);
      temp = temp.substring(end_index+1, temp.length());
      return temp;
   }

   int getMutatedLineNum(String str)
   {
      int start_index = str.indexOf(MutationSystem.LOG_IDENTIFIER);
      int end_index = str.lastIndexOf(MutationSystem.LOG_IDENTIFIER);
      String temp = str.substring(start_index+1, end_index);
      end_index = temp.lastIndexOf(MutationSystem.LOG_IDENTIFIER);
      temp = temp.substring(0, end_index);
      return ((new Integer(temp)).intValue());
   }

   /** Show source code of the selected mutant. Changed part is colored in red
    *  @param dir_name the name of class (including package name)
    *  @param changed_line line number of mutated code against original program
    */
   public void showMutant(String dir_name, String mutant_log)
   {
      try
      {
         int changed_line = getMutatedLineNum(mutant_log);
         String method_signature = getMethodSignature(mutant_log);
         String changed_content = getMutatedContent(mutant_log);
         changeTF.setText(" (line " + changed_line + ") " + changed_content);
         changeTF.repaint();

         Document ddoc = mutantTP.getDocument();
         ddoc.remove(0, ddoc.getLength());

         int line_num = 0;
         int caret_pos = 0;
         String strLine;
         File myFile = new File(MutationSystem.MUTANT_PATH + "/" + method_signature + "/" + dir_name,
                    MutationSystem.CLASS_NAME + ".java");

         String blank_str;
         LineNumberReader lReader = new LineNumberReader(new FileReader(myFile));

         while ((strLine = lReader.readLine()) != null)
         {
            blank_str = "";
            line_num = lReader.getLineNumber();
            int del = (new Integer(line_num)).toString().length();
            for (int k=0; k<5-del; k++)
            {
               blank_str = blank_str + " ";
            }
            ddoc.insertString(ddoc.getLength(), line_num + blank_str, blue_attr);
            if (line_num == changed_line)
            {
               caret_pos = ddoc.getLength();
               ddoc.insertString(ddoc.getLength(), strLine + "\n", red_attr);
            }
            else
            {
               ddoc.insertString(ddoc.getLength(), strLine + "\n", mutantTP.getCharacterAttributes());
            }
         }
         mutantTP.setCaretPosition(caret_pos);
         originalTP.setCaretPosition(caret_pos);
         originalTP.repaint();
         lReader.close();
      } catch(Exception  e)
      {
         System.err.println(" [error] " + e);
      }
   }
   
   public TraditionalMutantsViewerPanel() 
   {
      try 
      {
         jbInit();
      }
      catch (Exception ex) 
      {
         ex.printStackTrace();
      }
   }

   void setMutationType()
   {
      MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
   }

   void initSummaryTable()
   {
      TMSummaryTableModel tmodel = new TMSummaryTableModel();
      summaryTable = new JTable(tmodel);
      adjustSummaryTableSize(summaryTable, tmodel);
   }

   int getMutantType()
   {
      return MutationSystem.TM;
   }

   void setMutantPath()
   {
      MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
   }

   String getMutantPath()
   {
      return MutationSystem.TRADITIONAL_MUTANT_PATH;
   }

   void setSummaryTableSize()
   {
      int temp = MutationSystem.tm_operators.length * 18;
      summaryPanel.setPreferredSize(new Dimension(150, temp));
      summaryPanel.setMaximumSize(new Dimension(150, temp));
   }
   /**
    * get the number of all mutants of selected mutation operators in a class
    */
   void printGeneratedMutantNum(String[] operators, int[] num)
   {
      int total = 0;
      TMSummaryTableModel myModel = (TMSummaryTableModel)(summaryTable.getModel());
      for (int i = 0; i < MutationSystem.tm_operators.length; i++)
      {
         myModel.setValueAt(new Integer(num[i]), i, 1);
         total = total + num[i];
      }
      totalLabel.setText("Total : " + total);
   }
}