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
import java.util.Vector;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.table.*;
import javax.swing.border.*;

import mujava.MutationSystem;
import mujava.util.DirFileFilter;
import mujava.util.MutantDirFilter;

/**
 * <p>Template for viewing mutants: show original source code,
 *                 source code of the selected mutants, summary of mutants </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */


public abstract class MutantsViewerPanel  extends JPanel 
{
   JComboBox classCB = new JComboBox();
   String target_dir = "";
   JTable summaryTable = new JTable();
   JLabel totalLabel = new JLabel(" Total : " );
   JScrollPane summaryPanel = new JScrollPane();
   JTextField changeTF = new JTextField("  ");
   JScrollPane originalSP = new JScrollPane();
   JScrollPane mutantSP = new JScrollPane();
   JTextPane originalTP = new JTextPane();
   JTextPane mutantTP = new JTextPane();
   JList mList = new JList();

   SimpleAttributeSet red_attr = new SimpleAttributeSet();
   SimpleAttributeSet blue_attr = new SimpleAttributeSet();
   SimpleAttributeSet black_attr = new SimpleAttributeSet();

   /**
    * Initialization
    * @throws Exception
    */
   void jbInit() throws Exception 
   {
      this.setLayout(new FlowLayout());

      StyleConstants.setForeground(red_attr, Color.red);
      StyleConstants.setForeground(blue_attr, Color.blue);
      StyleConstants.setForeground(black_attr, Color.black);
 
      // summary part: 
      // for each mutation operators, show the number of mutants generated
      // also show the total number of mutants generated
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
      // Note: For traditional mutants, this section allows the users to 
      //           select a java class file and a method         
      //       For web application, this section allows the users to
      //           select a java servlet, jsp, htm, or html file
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

      
      JPanel contentPanel = new JPanel();

      // show a list of mutants to be selected for viewing
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
      rightContentPanel.setLayout(new BoxLayout(rightContentPanel, BoxLayout.PAGE_AXIS));

      // show the line mutated 
      changeTF.setPreferredSize(new Dimension(550, 40));
      rightContentPanel.add(changeTF);
      
      // show the source code of the original file and the mutant
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
      rightPanel.add(Box.createRigidArea(new Dimension(10, 10)));
      rightPanel.add(contentPanel);

      contentPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

      leftPanel.setPreferredSize(new Dimension(100, 500));
      this.add(leftPanel);
      this.add(rightPanel);

      refreshEnv();
   }

   abstract void setSummaryTableSize();
   abstract int getMutantType();

   /**
    * Check if the source programs are available  
    * @param str
    * @return true -- available, otherwise return false
    */
   boolean isProperClass(String str)
   {
      File f = new File(MutationSystem.MUTANT_HOME, str);
      File[] dirs = f.listFiles(new DirFileFilter());
      if ((dirs == null) || (dirs.length == 0))    
         return false;
    
      String[] names = {"", "original"};
      switch(getMutantType())
      {
         case MutationSystem.CM: names[0] = "traditional_mutants";
                                 break;
         case MutationSystem.TM: names[0] = "class_mutants";
                                 break;
         case MutationSystem.EM: names[0] = "exceptional_mutants";
                                 break;
//         case MutationSystem.WM: names[0] = "web_mutants";
//                                 break;                              
      }
    
      for (int i=0; i<names.length; i++)
      {
         boolean found = false;
         for (int j=0; j<dirs.length; j++)
         {
            if (dirs[j].getName().equals(names[i]))
            {
               found = true;
               break;
            }
         }
         if (!found)    
            return false;
      }
      return true;
   }

   abstract void printGeneratedMutantNum(String[] operators, int[] num);

   // name: ������ ����Ʈ �̸���
   // ����Ʈ �̸����� �����̼� ������ ���� ����Ѵ�.
   void showGeneratedMutantsNum(String[] name)
   {
      String[] operators = null;

      switch(getMutantType())
      {
         case MutationSystem.CM: operators = MutationSystem.cm_operators; // Ŭ���� ���� �����̼� ������
                                 break;
         case MutationSystem.TM: operators = MutationSystem.tm_operators; // �޼ҵ� ���� �����̼� ������
                                 break;
         case MutationSystem.EM: operators = MutationSystem.em_operators; // ���ܻ�Ȳ �����̼� ������
                                 break;
//         case MutationSystem.WM: operators = MutationSystem.wm_operators; 
//                                 break;
      }

      int[] num = new int[operators.length]; // �����̼� ������ �� ������ ����
      for (int i=0; i<operators.length; i++)
      { // num �迭 �ʱ�ȭ
         num[i] = 0;
      }
      
      if (name != null)
      {
         for (int i=0; i<name.length; i++)
         {
            for (int j=0; j<operators.length; j++)
            { // ==>
               if (name[i].indexOf(operators[j]+"_") == 0)
               { 
                  num[j]++;
               }  // ==>
            }
         }
      } 

      printGeneratedMutantNum(operators, num);
   }
   
   /** Change contents for the newly selected class */
   void updateClassComboBox()
   {
      target_dir = classCB.getSelectedItem().toString();
      
      if (target_dir == null)    return;
      
      if (isProperClass(target_dir)) 
      {
         clearSourceContents();
         MutationSystem.setJMutationPaths(target_dir);
         updateContents();
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

   abstract void setMutantPath();
   abstract String getMutantPath();

   void updateContents()
   {
      setMutantPath();
      File mutant_dir = new File(getMutantPath());
      String[] mutants = mutant_dir.list(new MutantDirFilter());
      showGeneratedMutantsNum(mutants);
      mList.setListData(mutants);
      mList.repaint();
      clearSourceContents();
      showOriginal();
   }

   /** Adjust column size of summary table for generated mutants */
   protected void adjustSummaryTableSize(JTable table, AbstractTableModel model) 
   {
      TableColumn column = null;
      for (int i = 0; i < table.getColumnCount(); i++) 
      {
         column = table.getColumnModel().getColumn(i);
         switch (i)
         {
            case 0 :  column.setMaxWidth(40);
                      break;
            case 1 :  column.setMaxWidth(60);
                      break;
         }
      }
   }

   abstract void initSummaryTable();

   public void refreshEnv()
   {
      File f = new File(MutationSystem.MUTANT_HOME);
      String[] c_list = f.list(new DirFileFilter());
      try
      {
         classCB.removeAllItems();
      } 
      catch(NullPointerException e)
      {  // do nothing???
      }
      
      if ( (c_list == null) || (c_list.length == 0) )
      { 
         return;
      }
      for (int i=0; i<c_list.length; i++)
      {
         classCB.addItem(c_list[i]);
      }
      //updateClassComboBox();
      this.repaint();
   }

   abstract void setMutationType();

   public void clearSourceContents()
   {
      try
      {
         Document ddoc;
         ddoc = originalTP.getDocument();
         ddoc.remove(0, ddoc.getLength());

         ddoc = mutantTP.getDocument();
         ddoc.remove(0, ddoc.getLength());
      } 
      catch(BadLocationException e)
      {
         System.err.println("error " +e);
      }
      changeTF.setText("");
   }

   void mList_mouseClicked(MouseEvent e)
   {
      setMutationType();
      Object selected_obj = mList.getSelectedValue();
      if (selected_obj != null)
      {
         String mutant_name = selected_obj.toString();
         String mutant_log = getMutantLog(mutant_name);
         if (mutant_log != null) 
         {
        	System.out.println("MutantsViewerPanel.mList_mouseClicked - mutant_log != null");
            showMutant(mutant_name,mutant_log);
         }
      }
   }

   /** Show the source code of original Java program */
   public void showOriginal()
   {
      try
      {
         String strLine;
         File myFile = new File(MutationSystem.ORIGINAL_PATH,MutationSystem.CLASS_NAME+".java");
         String blank_str;
         LineNumberReader lReader = new LineNumberReader(new FileReader(myFile));

         Document ddoc = originalTP.getDocument();

         while ((strLine=lReader.readLine()) != null)
         {
      	    blank_str = "";
      	    int del = (new Integer(lReader.getLineNumber())).toString().length();
      	    for (int k=0; k<5-del; k++)
      	    {
      	       blank_str=blank_str+" ";
            }
      	    ddoc.insertString (ddoc.getLength(), lReader.getLineNumber()+blank_str, blue_attr);
      	    ddoc.insertString (ddoc.getLength(), strLine+"\n", black_attr);
         }
         lReader.close();

      } catch (Exception  e)
      {
         System.err.println(" [error] " + e);
      }
   }

  /** Show source code of the selected mutant. Changed part is colored in red
   *  @param dir_name the name of class (including package name)
   *  @param changed_line line number of mutated code against original program*/
   public void showMutant(String dir_name,int changed_line)
   {
      System.out.println("MutantsViewerPanel.showMutant - with changed_line");
      try
      {
         Document ddoc = mutantTP.getDocument();
         ddoc.remove(0, ddoc.getLength());

         int line_num = 0;
         int caret_pos = 0;
         String strLine;
         File myFile = new File(MutationSystem.MUTANT_PATH + 
        		  "/" + dir_name, MutationSystem.CLASS_NAME + ".java");

         String blank_str;

      } catch(Exception  e)
      {
         System.err.println(" [error] " + e);
      }
   }

  /** Show source code of the selected mutant. Changed part is colored in red
   *  @param dir_name the name of class (including package name)
   *  @param changed_line line number of mutated code against original program*/
   public void showMutant(String dir_name,String mutant_log)
   {
	  System.out.println("MutantsViewerPanel.showMutant (with mutant_log)");
      try
      {
         int changed_line = getMutatedLineNum(mutant_log);
         String changed_content = getMutatedContent(mutant_log);
         changeTF.setText(" (line " + changed_line + ") " + changed_content);
         changeTF.repaint();

         Document ddoc = mutantTP.getDocument();
         ddoc.remove(0, ddoc.getLength());

         int line_num=0;
         int caret_pos=0;
         String strLine;
         File myFile = new File(MutationSystem.MUTANT_PATH + 
        		  "/" + dir_name, MutationSystem.CLASS_NAME + ".java");
         System.out.println("showMutant: myFile =" + myFile.getAbsolutePath());

         String blank_str;
         LineNumberReader lReader = new LineNumberReader(new FileReader(myFile));

         while ((strLine=lReader.readLine()) != null)
         {
            blank_str="";
            line_num = lReader.getLineNumber();
            int del = (new Integer(line_num)).toString().length();
            for (int k=0; k<5-del; k++)
            {
               blank_str = blank_str + " ";
            }
            ddoc.insertString(ddoc.getLength(), line_num+blank_str, blue_attr);
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
  
  /** Return log for the mutant <i> mutant_name </i> from the log file "mutation_log" <br>
   *  @return log for the mutant (if no log found, NULL is returned.)*/
   String getMutantLog(String mutant_name)
   {
      try
      {
         File myFile = new File(MutationSystem.MUTANT_PATH, "mutation_log");
         String strLine;
         LineNumberReader lReader = new LineNumberReader(new FileReader(myFile));
         while ((strLine=lReader.readLine()) != null)
         {
            if (strLine.indexOf(mutant_name) == 0)
            {
               return strLine;
            }
         }
      } catch (FileNotFoundException e1)
      {
         System.err.println(e1);
      } catch (IOException e2)
      {
         System.err.println(e2);
      }
      return null;
   }

  /**
   * @param name name of the mutant
   * @return mutated line for the mutant <i>name</i> */
   public int getMutatedLine(String name)
   {
      try 
      {
         File myFile = new File(MutationSystem.MUTANT_PATH, "mutation_log");
         String strLine;
         LineNumberReader lReader = new LineNumberReader(new FileReader(myFile));

         while ((strLine = lReader.readLine()) != null)
         {
            if (strLine.indexOf(name) == 0)
            {
               int changed_line = getMutatedLineNum(strLine);
               String changed_content = getMutatedContent(strLine);
               changeTF.setText(" (line " + changed_line + ") " + changed_content);
               changeTF.repaint();
               mutantTP.repaint();
               return changed_line;
            }
         }
         changeTF.setText(" ");
      } catch (FileNotFoundException e1)
      {
         System.err.println(e1);
      } catch (IOException e2)
      {
         System.err.println(e2);
      }
      return -1;
   }

   int getMutatedLineNum(String str)
   {
      // MutationSystem.LOG_IDENTIFIER = ":"	   
      int start_index = str.indexOf(MutationSystem.LOG_IDENTIFIER);
//      int end_index = str.lastIndexOf(MutationSystem.LOG_IDENTIFIER);
      
// (Upsorn, 07/14/2009) Modify end_index. Originally, a line of log is formatted as
//    mutant_name:line_no:original_string => mutated_string
// In html, it is possible to have <A Href="http://www.ise.gmu.edu/~ofut/classes/632/">       
// Thus, for example, log will be recorded as       
//    WLR_2:28:<A Href="http://www.ise.gmu.edu/~ofut/classes/632/"> ...... 
// Here, to make the format flexible, consider start_index with the first ":"
//    end_index with the second ":"
      String log_str = str;
      log_str = log_str.substring(start_index+1);
      int end_index = start_index + 1 + log_str.indexOf(MutationSystem.LOG_IDENTIFIER);      
      System.out.println("MutantsViewerPanel.getMutatedLineNum --- " +
    		   "start_index =" + start_index + " : end_index =" + end_index ); 
      
      String temp = str.substring(start_index + 1, end_index);
      return ((new Integer(temp)).intValue());
   }

   String getMutatedContent(String str)
   {
//      int end_index = str.lastIndexOf(MutationSystem.LOG_IDENTIFIER);

// (Upsorn, 07/14/2009) Modify end_index. Originally, a line of log is formatted as
//	    mutant_name:line_no:original_string => mutated_string
// In html, it is possible to have <A Href="http://www.ise.gmu.edu/~ofut/classes/632/">       
// Thus, for example, log will be recorded as       
//	    WLR_2:28:<A Href="http://www.ise.gmu.edu/~ofut/classes/632/"> ...... 
// Here, to make the format flexible, consider start_index with the first ":"
//	    end_index with the second ":"

      // MutationSystem.LOG_IDENTIFIER = ":"	   
      int start_index = str.indexOf(MutationSystem.LOG_IDENTIFIER);
      String log_str = str;
      log_str = log_str.substring(start_index + 1);
      int end_index = start_index + 1 + log_str.indexOf(MutationSystem.LOG_IDENTIFIER);      
      System.out.println("MutantsViewerPanel.getMutatedContent --- " +
	    		   "start_index =" + start_index + " : end_index =" + end_index ); 
	   
      return str.substring(end_index + 1);
   }
}
 