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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import mujava.gui.util.*;

import java.io.*;

import mujava.MutationSystem;
import mujava.TestExecuter;
import mujava.util.*;
import mujava.test.*;

import java.util.Vector;

/**
 * <p>Panel for running mutant against a given test suite</p>
 * @author Yu-Seung Ma
 * @version 1.0
 * @update Nan Li
  */

public class RunTestPanel extends JPanel implements ActionListener  
{
   private static final long serialVersionUID = 108L;

   String target_dir;
   
   //A three-second initial value is required; otherwise, 
   //The program would freeze if the mutant gets stuck in an infinite loop or something similar that makes the program cannot respond
   //The bug is fixed by Nan Li
   //Updated on Dec. 5 2011
   int timeout_secs = 3000;
   
   // add customized timeout setting
   // Lin, 05232015
   int customized_time = 3000;
 
   JTable cmTable;
   JTable tmTable;
   JTable cResultTable;
   JTable tResultTable;
   JComboBox classCB;
   JComboBox methodCB; 
   JComboBox timeCB;
   
   // add a new textfield for customized timeout
   JTextField timeoutTextField;
   boolean isCustomizedTimeout = false;
   
   JList cLiveList = new JList();
   JList tLiveList = new JList();
   JList cKilledList = new JList();
   JList tKilledList = new JList();
   JLabel cmTotalLabel = new JLabel("Total= ", JLabel.LEFT);
   JLabel tmTotalLabel = new JLabel("Total= ", JLabel.LEFT);
   JRadioButton onlyClassButton = new JRadioButton("Execute only class mutants");
   JRadioButton onlyTraditionalButton = new JRadioButton("Execute only traditional mutants");
   JRadioButton onlyExceptionButton = new JRadioButton("Execute only exception mutants");
   JRadioButton bothButton = new JRadioButton("Execute all mutants");

   JComboBox testCB;
   JButton runB = new JButton("RUN");

   JPanel tResultPanel = new JPanel();
   JPanel cResultPanel = new JPanel();
   final int CLASS = 1;
   final int TRADITIONAL = 2;
   final int BOTH = 3;

   public RunTestPanel()
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

   void jbInit()
   {
      this.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();

      onlyClassButton.setActionCommand("CLASS");
      onlyClassButton.addActionListener(this);
      onlyClassButton.setSelected(true);

      onlyTraditionalButton.setActionCommand("TRADITIONAL");
      onlyTraditionalButton.addActionListener(this);

      onlyExceptionButton.setActionCommand("TRADITIONAL");
      onlyExceptionButton.addActionListener(this);

      bothButton.setActionCommand("BOTH");
      bothButton.addActionListener(this);
 
      ButtonGroup group = new ButtonGroup();
      group.add(onlyClassButton);
      group.add(onlyTraditionalButton);
      group.add(bothButton);

      JPanel optionP = new JPanel(new GridLayout(0, 1));
      optionP.add(onlyClassButton);
      optionP.add(onlyTraditionalButton);
      optionP.add(bothButton);

      c.gridx = 0;
      c.gridy = 0;
      this.add(optionP, c);

      // Summary Tables for traditioanl mutants and class mutants (x,y) = (0,1)
      JPanel summaryPanel = new JPanel();
      summaryPanel.setLayout(new FlowLayout());

      JPanel traditional_summaryPanel = new JPanel();
      traditional_summaryPanel.setLayout(new BoxLayout(traditional_summaryPanel,BoxLayout.PAGE_AXIS));
 
      JScrollPane tmTablePanel = new JScrollPane();
      TMSummaryTableModel tmodel = new TMSummaryTableModel();
      tmTable = new JTable(tmodel);
      adjustSummaryTableSize(tmTable, tmodel);
      tmTablePanel.getViewport().add(tmTable);
      tmTablePanel.setPreferredSize(new Dimension(120, 500));
      tmTablePanel.setMaximumSize(new Dimension(120, 500));

      traditional_summaryPanel.add(tmTablePanel);
      traditional_summaryPanel.add(tmTotalLabel);

      JPanel class_summaryPanel = new JPanel();
      class_summaryPanel.setLayout(new BoxLayout(class_summaryPanel, BoxLayout.PAGE_AXIS));

      JScrollPane cmTablePanel = new JScrollPane();
      CMSummaryTableModel cmodel = new CMSummaryTableModel();
      cmTable = new JTable(cmodel);
      adjustSummaryTableSize(cmTable, cmodel);
      cmTablePanel.getViewport().add(cmTable);
      cmTablePanel.setPreferredSize(new Dimension(120, 500));
      cmTablePanel.setMaximumSize(new Dimension(120, 500));

      traditional_summaryPanel.setPreferredSize(new Dimension(100, 520));
      traditional_summaryPanel.setMaximumSize(new Dimension(100, 520));
      class_summaryPanel.setPreferredSize(new Dimension(100, 520));
      class_summaryPanel.setMaximumSize(new Dimension(100, 520));

      class_summaryPanel.add(cmTablePanel);
      class_summaryPanel.add(cmTotalLabel);

      summaryPanel.add(traditional_summaryPanel);
      summaryPanel.add(class_summaryPanel);

      c.gridx = 0;
      c.gridy = 1;
      this.add(summaryPanel, c);

      // Selection part for clas, test cases names ==>  (x,y) = (1,0)
      JPanel selectPanel = new JPanel();
      selectPanel.setLayout(new GridBagLayout());
      GridBagConstraints selectConstraints = new GridBagConstraints();
      selectConstraints.gridx = 0;
      selectConstraints.gridy = 0;
      JLabel label1 = new JLabel("Class       : ", JLabel.RIGHT);
      label1.setPreferredSize(new Dimension(100,28));  
      label1.setMaximumSize(new Dimension(100,28));
      selectPanel.add(label1 , selectConstraints);

      File classF = new File(MutationSystem.MUTANT_HOME);
      String[] c_list = classF.list(new DirFileFilter());
      classCB = new JComboBox(c_list);
      classCB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            changeContents();
         }
      });

      selectConstraints.gridx = 1;
      selectConstraints.gridy = 0;
      selectConstraints.gridwidth = 2;
      classCB.setPreferredSize(new Dimension(400, 28));
      classCB.setMaximumSize(new Dimension(400, 28));
      selectPanel.add(classCB, selectConstraints); 

      selectConstraints.gridx = 0;
      selectConstraints.gridy = 1;
      selectConstraints.gridwidth = 1;
      JLabel label_method = new JLabel("Method    : ", JLabel.RIGHT);
      label_method.setPreferredSize(new Dimension(100, 28));
      label_method.setMaximumSize(new Dimension(100, 28));
      selectPanel.add(label_method, selectConstraints);

      methodCB = new JComboBox();
      methodCB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            changeMethodContents();
         }
      });

      selectConstraints.gridx = 1;
      selectConstraints.gridy = 1;
      selectConstraints.gridwidth = 2;
      methodCB.setPreferredSize(new Dimension(400, 28));
      methodCB.setMaximumSize(new Dimension(400, 28));
      selectPanel.add(methodCB, selectConstraints);

      selectConstraints.gridx = 0;
      selectConstraints.gridy = 2;
      selectConstraints.gridwidth = 1;
      JLabel label2 = new JLabel("TestCase  : ", JLabel.RIGHT);
      label2.setPreferredSize(new Dimension(100, 28));
      label2.setMaximumSize(new Dimension(100, 28));
      selectPanel.add(label2, selectConstraints);

      String[] t_list = getTestSetNames();
      testCB = new JComboBox(eraseExtension(t_list, "class"));
      testCB.setPreferredSize(new Dimension(320, 28));
      testCB.setMaximumSize(new Dimension(320, 28));
      selectConstraints.gridx = 1;
      selectConstraints.gridy = 2;
      selectPanel.add(testCB, selectConstraints);

      selectConstraints.gridx = 2;
      selectConstraints.gridy = 2;
      selectPanel.add(runB, selectConstraints);
      runB.setPreferredSize(new Dimension(80, 28));
      runB.setMaximumSize(new Dimension(80, 28));
      runB.setBackground(Color.yellow);
      runB.addMouseListener(new java.awt.event.MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            testRunB_mouseClicked(e);
         }
      });

      selectConstraints.gridx = 0;
      selectConstraints.gridy = 3;
      JLabel label_time = new JLabel("Time-Out : ", JLabel.RIGHT);
      label_time.setPreferredSize(new Dimension(100, 28));
      label_time.setMaximumSize(new Dimension(100, 28));
      selectPanel.add(label_time, selectConstraints);
  
      String[] time_list = {"3 seconds", "5 seconds", "10 seconds", "Other"};
	  timeCB = new JComboBox(time_list);
      timeCB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            changeTimeOut();
         }
      });

      timeoutTextField = new JTextField();
      timeoutTextField.setHorizontalAlignment(JTextField.CENTER);
      
      // adjust the gui to have a textfield for customized timeout
      // Lin 05232015
      selectConstraints.gridx = 1;
      selectConstraints.gridy = 3;
//      selectConstraints.gridwidth = 2;
      timeCB.setPreferredSize(new Dimension(320, 28));
      timeCB.setMaximumSize(new Dimension(320, 28));
      selectPanel.add(timeCB, selectConstraints);
      

      timeoutTextField.setPreferredSize(new Dimension(74, 28));
      timeoutTextField.setMaximumSize(new Dimension(74, 28));
      timeoutTextField.setEnabled(false);
      timeoutTextField.setText("3");
      selectConstraints.gridx = 2;
      selectConstraints.gridy = 3;
      selectPanel.add(timeoutTextField, selectConstraints);
//      
      selectConstraints.gridx = 3;
      selectConstraints.gridy = 3;
      selectPanel.add(new JLabel("s"), selectConstraints);
      
      c.gridx = 1;
      c.gridy = 0;
      this.add(selectPanel, c);

      // Mutants ==> (x,y) = (1,1)
      JPanel resultPanel = new JPanel();
      resultPanel.setLayout(new FlowLayout());

      tResultPanel.setBorder(new TitledBorder("Traditional Mutants Result"));
      tResultPanel.setLayout(new GridBagLayout());
      GridBagConstraints tResultConstraints = new GridBagConstraints();
      ResultTableModel tResultTableModel = new ResultTableModel();
      tResultTable = new JTable(tResultTableModel);
      setResultTableSize(tResultTable);
      tResultConstraints.gridx = 0;
      tResultConstraints.gridy = 0;
      tResultConstraints.gridwidth = 2;
      tResultPanel.add(tResultTable, tResultConstraints);
      JScrollPane t_livePanel = new JScrollPane();
      setSPSize(t_livePanel);
      t_livePanel.setBorder(new TitledBorder("Live"));
      t_livePanel.getViewport().add(tLiveList);
      tResultConstraints.gridx = 0;
      tResultConstraints.gridy = 1;
      tResultConstraints.gridwidth = 1;
      tResultPanel.add(t_livePanel, tResultConstraints);
      JScrollPane t_killedPanel = new JScrollPane();
      setSPSize(t_killedPanel);
      t_killedPanel.setBorder(new TitledBorder("Killed"));
      t_killedPanel.getViewport().add(tKilledList);
      tResultConstraints.gridx = 1;
      tResultConstraints.gridy = 1;
      tResultPanel.add(t_killedPanel, tResultConstraints);
      resultPanel.add(tResultPanel);

      cResultPanel.setBorder(new TitledBorder("Class Mutants Result"));
      cResultPanel.setLayout(new GridBagLayout());
      GridBagConstraints cResultConstraints = new GridBagConstraints();
      ResultTableModel cResultTableModel = new ResultTableModel();
      cResultTable = new JTable(cResultTableModel);
      setResultTableSize(cResultTable);
      cResultConstraints.gridx = 0;
      cResultConstraints.gridy = 0;
      cResultConstraints.gridwidth = 2;
      cResultPanel.add(cResultTable, cResultConstraints);
      JScrollPane c_livePanel = new JScrollPane();
      setSPSize(c_livePanel);
      c_livePanel.setBorder(new TitledBorder("Live"));
      c_livePanel.getViewport().add(cLiveList);
      cResultConstraints.gridx = 0;
      cResultConstraints.gridy = 1;
      cResultConstraints.gridwidth = 1;
      cResultPanel.add(c_livePanel, cResultConstraints);
      JScrollPane c_killedPanel = new JScrollPane();
      setSPSize(c_killedPanel);
      c_killedPanel.setBorder(new TitledBorder("Killed"));
      c_killedPanel.getViewport().add(cKilledList);
      cResultConstraints.gridx = 1;
      cResultConstraints.gridy = 1;
      cResultPanel.add(c_killedPanel, cResultConstraints);
      resultPanel.add(cResultPanel);

      resultPanel.setPreferredSize(new Dimension(500, 520));
      resultPanel.setMaximumSize(new Dimension(500, 520));
      resultPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

      c.gridx = 1;
      c.gridy = 1;
      this.add(resultPanel, c);

      this.addFocusListener(new java.awt.event.FocusAdapter()
      {
         public void focusGained(FocusEvent e)
         {
            changeContents();
         }
      });
   }
 
   String[] eraseExtension(String[] list,String extension)
   {
      String[] result = new String[list.length];
      for (int i=0; i<list.length; i++)
      {
         result[i] = list[i].substring(0, list[i].length()-extension.length()-1);
      }
      return result;
   }

   String[] getTestSetNames()
   {
      Vector v = new Vector();
      getTestSetNames(new File(MutationSystem.TESTSET_PATH), v);
      String[] result = new String[v.size()];  
      for (int i=0; i<v.size(); i++)
      {
         result[i] = v.get(i).toString();
      }
      return result;
   }

   //    File testF = new File(MutationSystem.TESTSET_PATH);
   void getTestSetNames(File testDir, Vector v)
   {
      String[] t_list = testDir.list(new ExtensionFilter("class"));
      int start_index = MutationSystem.TESTSET_PATH.length();
      int end_index = testDir.getAbsolutePath().length();
      if (start_index < end_index) 
    	 start_index ++;
      String suffix = testDir.getAbsolutePath().substring(start_index, end_index);
      if (suffix == null || suffix.equals(""))
      {
         suffix = "";
      } 
      else
      {
         String temp = "";
         for (int k=0; k<suffix.length(); k++)
         {
            char ch = suffix.charAt(k);
            if( (ch=='/') || (ch=='\\') )
            {
               temp = temp + ".";
            }
            else
            { 
               temp = temp + ch;
            }
         }
         suffix = temp + ".";
      }

      if (t_list == null)
      {
         System.out.println(" [Error] No test suite is detected. ");
      } 
      else
      {
         for (int i=0; i<t_list.length; i++)
         {
            v.add(suffix+t_list[i]);
         }

         File[] subDir = testDir.listFiles(new DirFileFilter());
         for (int i=0; i<subDir.length; i++)
         {
            getTestSetNames(subDir[i], v);
         }
      }
   }

   void testRunB_mouseClicked(MouseEvent e)
   {
	   // check if the customized timeout is used
	   // added by Lin, 05/23/2015
		if (isCustomizedTimeout) {
			try {
				timeout_secs = 1000*Integer.parseInt(timeoutTextField.getText());
				// what if a negative or zero, set it to 3000
				if (timeout_secs <= 0) {
					timeout_secs = 3000;
				}

			} catch (NumberFormatException ex) {
				// if not a number, set to be 3000
				timeout_secs = 3000;
			}
		}
	   	   
      // class name whose mutants are executed
      Object targetClassObj = classCB.getSelectedItem();
      // class name whose mutants are executed
      Object methodSignature = methodCB.getSelectedItem();
      
      if (methodSignature == null) 
    	 methodSignature = "All method";
  
      // name of test suite to apply
      Object testSetObject  = testCB.getSelectedItem();

      if((targetClassObj != null) && (testSetObject != null))
      {
         String targetClassName = classCB.getSelectedItem().toString();
         String testSetName = testCB.getSelectedItem().toString();

         TestExecuter test_engine = new TestExecuter(targetClassName);
         test_engine.setTimeOut(timeout_secs);

         // First, read (load) test suite class.
         test_engine.readTestSet(testSetName);

         TestResult test_result = new TestResult();
         try
         {
            if (onlyClassButton.isSelected())
            {
               cResultPanel.setVisible(true);
               tResultPanel.setVisible(false);
               test_engine.computeOriginalTestResults();
               test_result = test_engine.runClassMutants();
               showResult(test_result, cResultTable, cKilledList, cLiveList);
            } 
            else if (onlyTraditionalButton.isSelected())
            {
               cResultPanel.setVisible(false);
               tResultPanel.setVisible(true);
               test_engine.computeOriginalTestResults();
               test_result = test_engine.runTraditionalMutants(methodSignature.toString());
               showResult(test_result, tResultTable, tKilledList, tLiveList);
            }
            else if (bothButton.isSelected())
            {
               cResultPanel.setVisible(true);
               tResultPanel.setVisible(true);
               test_engine.computeOriginalTestResults();
               test_result = test_engine.runClassMutants();
               showResult(test_result, cResultTable, cKilledList, cLiveList);
               test_result = test_engine.runTraditionalMutants(methodSignature.toString());
               showResult(test_result, tResultTable, tKilledList, tLiveList);
            }
         } 
         catch (NoMutantException e1)
         {
         } 
         catch (NoMutantDirException e2)
         {
         }
      } 
      else
      {
         System.out.println(" [Error] Please check test target or test suite ");
      }
   }

   private void showEmptyResult(JTable table, JList killed_list, JList live_list)
   {
      // Show the result on resultTable
      ResultTableModel resultModel = (ResultTableModel)(table.getModel());
      resultModel.setValueAt( "  " + (new Integer(0)).toString() , 0 , 1 );   // live mutant
      resultModel.setValueAt( "  " + (new Integer(0)).toString() , 1 , 1 );   // killed mutant
      resultModel.setValueAt( "  " + (new Integer(0)).toString() , 2 , 1 );   // total
      resultModel.setValueAt( "  " + " - %" , 3 , 1 );   // mutant score

      killed_list.setListData(new String[0]);
      live_list.setListData(new String[0]);
      killed_list.repaint();
      live_list.repaint();
   }

   private void showResult(TestResult tr, JTable table,JList killed_list, JList live_list)
   { 
      int i;
      // Mutation Score
      if (tr == null)
    	 System.out.println("-----------");
      int killed_num = tr.killed_mutants.size();
      int live_num = tr.live_mutants.size();

      if ((killed_num + live_num) == 0)
      {
         showEmptyResult(table, killed_list, live_list);
         System.out.println("[Notice] There are no mutants to apply");
         return;
      }

      Float mutant_score = new Float((killed_num * 100) / (killed_num + live_num));

      // Show the result on resultTable
      ResultTableModel resultModel = (ResultTableModel)(table.getModel());
      resultModel.setValueAt( "  " + (new Integer(live_num)).toString() , 0 , 1 );   // live mutant
      resultModel.setValueAt( "  " + (new Integer(killed_num)).toString() , 1 , 1 ); // killed mutant
      resultModel.setValueAt( "  " + (new Integer(live_num+killed_num)).toString() , 2 , 1 );   //total
      resultModel.setValueAt( "  " + mutant_score.toString() + "%" , 3 , 1 );   // mutant score
 
      // List of Killed, Live Mutants

      String[] killed_mutants = new String[killed_num];
      String[] live_mutants = new String[live_num];
      for (i=0; i<killed_num; i++)
      {
         killed_mutants[i] = tr.killed_mutants.get(i).toString();
      } 
      for (i=0; i<live_num; i++)
      {
         live_mutants[i] = tr.live_mutants.get(i).toString();
      }

      killed_list.setListData(killed_mutants);
      live_list.setListData(live_mutants);
      killed_list.repaint();
      live_list.repaint();
   }

   void changeTimeOut()
   {
      String tstr = timeCB.getSelectedItem().toString();
      if (tstr.equals("3 seconds"))
      {
    	  timeoutTextField.setEnabled(false);
    	  timeoutTextField.setText("3");
    	  isCustomizedTimeout = false;
         timeout_secs = 3000;
      } 
      else if (tstr.equals("5 seconds"))
      {
    	  timeoutTextField.setEnabled(false);
    	  timeoutTextField.setText("5");
    	  isCustomizedTimeout = false;
         timeout_secs = 5000;
      }
      else if (tstr.equals("10 seconds"))
      {
    	  timeoutTextField.setEnabled(false);
    	  timeoutTextField.setText("10");
    	  isCustomizedTimeout = false;
         timeout_secs = 10000;
	  }
      else if (tstr.equals("Other"))
      {
    	  timeoutTextField.setEnabled(true);
    	  isCustomizedTimeout = true;
    	  //timeout_secs = customized_time;
      }
   } 

   void showTraditionalMutants()
   {
      try
      {
         Vector v = new Vector();
         //setMutantPath();
         File f = new File(MutationSystem.TRADITIONAL_MUTANT_PATH, "method_list");
         FileReader r = new FileReader(f);
         BufferedReader reader = new BufferedReader(r);
         String methodSignature = reader.readLine();
         
         while (methodSignature != null)
         {
            File mutant_dir = new File(MutationSystem.TRADITIONAL_MUTANT_PATH + "/" + methodSignature);
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
            mutants[i]=v.get(i).toString();
         }
         showGeneratedTraditionalMutantsNum(mutants);
      } catch (Exception e)
      {
         System.err.println("Error in update() in TraditioanlMutantsViewerPanel.java");
      }
   }

   void showTraditionalMutants(String methodSignature)
   {
      File mutant_dir = new File(MutationSystem.TRADITIONAL_MUTANT_PATH + "/" + methodSignature);
      String[] mutants = mutant_dir.list(new MutantDirFilter());
      showGeneratedTraditionalMutantsNum(mutants);
   }

   void changeMethodContents()
   {
      Object item = methodCB.getSelectedItem();
      if (item == null) 
         return;
      
      String methodSignature = item.toString();
      if (methodSignature == null) 
         return;
  
      if (methodSignature.equals("All method"))
      {
         showTraditionalMutants();
      } 
      else
      {
         showTraditionalMutants(methodSignature);
      }
   }

   void changeContents()
   {
      target_dir = classCB.getSelectedItem().toString();
      MutationSystem.setJMutationPaths(target_dir);

      MutationSystem.MUTANT_PATH = MutationSystem.CLASS_MUTANT_PATH;

      File mutant_dir = new File(MutationSystem.CLASS_MUTANT_PATH);
      String[] mutants = mutant_dir.list(new MutantDirFilter());
      showGeneratedClassMutantsNum(mutants);

      MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;

      showTraditionalMutants();

      methodCB.removeAllItems();
      methodCB.addItem("All method");
      try
      {
         File f = new File(MutationSystem.TRADITIONAL_MUTANT_PATH, "method_list");
         FileReader r = new FileReader(f);
         BufferedReader reader = new BufferedReader(r);
         String str = reader.readLine();
         while(str != null)
         {
            methodCB.addItem(str);
            str = reader.readLine();
         }
         reader.close();
      } catch (java.io.FileNotFoundException fnfe)
      {
      } catch (Exception e)
      {
         System.err.println("error at updateClassComboBox() in RunTestPanel");
      }
      this.repaint();
   }

   void showGeneratedClassMutantsNum(String[] name)
   {
      if (name != null)
      {
         int[] num = new int[MutationSystem.cm_operators.length];
         for (int i=0; i<MutationSystem.cm_operators.length; i++)
         {
            num[i] = 0;
         }
         
         for (int i=0; i<name.length; i++)
         {
            for (int j=0; j<MutationSystem.cm_operators.length; j++)
            {
               if (name[i].indexOf(MutationSystem.cm_operators[j] + "_") == 0)
               { 
                  num[j]++;
               }
            }
         }

         int total = 0;
         CMSummaryTableModel myModel = (CMSummaryTableModel)(cmTable.getModel());
         
         for (int i=0; i<MutationSystem.cm_operators.length; i++)
         {
            myModel.setValueAt(new Integer(num[i]), i, 1);
            total = total + num[i];
         }
         cmTotalLabel.setText("Total : " + total);
      }
   }

/*  void updateContents(String methodSignature){
    setMutantPath();
    File mutant_dir = new File(getMutantPath()+"/"+methodSignature);
    String[] mutants = mutant_dir.list(new MutantDirFilter());
    showGeneratedMutantsNum(mutants);
    mList.setListData(mutants);
    mList.repaint();
    clearSourceContents();
    showOriginal();
  }
 */
   void showGeneratedTraditionalMutantsNum(String[] name)
   {
      if (name != null)
      {
         int[] num = new int[MutationSystem.tm_operators.length];
         for (int i=0; i<MutationSystem.tm_operators.length; i++)
         {
            num[i] = 0;
         }
         
         for (int i=0; i<name.length; i++)
         {
            for (int j=0; j<MutationSystem.tm_operators.length; j++)
            {
               if (name[i].indexOf(MutationSystem.tm_operators[j] + "_") == 0)
               { 
            	  num[j]++;
               }
            }
         }
 
         int total = 0;
         TMSummaryTableModel myModel = (TMSummaryTableModel)(tmTable.getModel());
         for (int i=0; i<MutationSystem.tm_operators.length; i++)
         {
            myModel.setValueAt(new Integer(num[i]), i, 1);
            total = total + num[i];
         }
         tmTotalLabel.setText("Total : " + total);
      }
   }
  
   private void setResultTableSize(JTable table)
   {
      TableColumn column = null;

      for (int i = 0; i < table.getColumnCount(); i++) 
      {
         column = table.getColumnModel().getColumn(i);
         switch(i)
         {
            case 0 :  column.setMaxWidth(110);
                      column.setPreferredWidth(110);
                      break;
            case 1 :  column.setMaxWidth(50);
                      break;
         }
      };
   }

   private void setSPSize(JScrollPane p)
   {
      p.setPreferredSize(new Dimension(100, 410));
      p.setMaximumSize(new Dimension(100, 410));
      p.setMinimumSize(new Dimension(100, 410));
   }


   /** Listens to the radio buttons. */
   public void actionPerformed(ActionEvent e) 
   {
      String cmd = e.getActionCommand();
      if (cmd.equals("CLASS"))
      {  
    	 // do nothing
      }
      else if (cmd.equals("TRADITIONAL"))
      {
    	 // do nothing
      }else if (cmd.equals("BOTH"))
      {
    	 // do nothing
      }
   }

   protected void adjustSummaryTableSize(JTable table, AbstractTableModel model) 
   {
      TableColumn column = null;

      for (int i = 0; i < table.getColumnCount(); i++) 
      {
         column = table.getColumnModel().getColumn(i);
         switch(i)
         {
            case 0 :
                  column.setPreferredWidth(60);
                  column.setMaxWidth(60);
                  break;
            case 1 :  
            	  column.setPreferredWidth(60);
                  column.setMaxWidth(60);
                  break;
         }
      }
   }
}


class ResultTableModel extends AbstractTableModel 
{
   private static final long serialVersionUID = 109L;

   String[] columnHeader = new String[]{" Operator "," value "};

   // This part is used during implementation and testing
   Object[][] data = {   {"  Live Mutants # ", ""} ,
   	                     {"  Killed Mutants # ",""},
                         {"  Total Mutants # ", ""},
                         {"  Mutant Score ", ""}};

  /**
   * AbstractTable Implementation �Լ�
   */
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

  /*
   * JTable uses this method to determine the default renderer/
   * editor for each cell.  If we didn't implement this method,
   * then the last column would contain text ("true"/"false"),
   * rather than a check box.
   */

   public Class getColumnClass(int c) 
   {
      return getValueAt(0, c).getClass();
   }

  /*
   * Don't need to implement this method unless your table's
   * data can change.
   */
   public void setValueAt(Object value, int row, int col) 
   {
      data[row][col] = value;
      fireTableCellUpdated(row, col);
   }

   public boolean isCellEditable(int row, int col) 
   {
     //Note that the data/cell address is constant,
     //no matter where the cell appears onscreen.
      return false;
   }

}
