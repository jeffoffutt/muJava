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

import javax.swing.*;
import java.awt.event.*;
import mujava.MutationSystem;

/**
 * <p>GUI program (main interface) for generating mutants </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class GenMutantsMain extends JFrame 
{
   private static final long serialVersionUID = 102L;

   JTabbedPane mutantTabbedPane = new JTabbedPane();

   /** Panel for generating mutants. */
   MutantsGenPanel genPanel;

   /** Panel for viewing details of class mutants. */
   ClassMutantsViewerPanel cvPanel;

   /** Panel for viewing details of traditional mutants.  */
   TraditionalMutantsViewerPanel tvPanel;

   public GenMutantsMain() 
   {
      try 
      {
         jbInit();
      }
      catch (Exception e) 
      {
         e.printStackTrace();
      }
   }

   /** <p> Main program for generating mutants (no parameter required for run).</p>
    *  <p>- supporting functions: 
    *       (1) selection of Java source files to apply,
    *       (2) selection of mutation operators to apply </p> 
 * @throws Exception 
    */
   public static void main (String[] args) throws Exception 
   { 
      System.out.println("The main method starts");
      try {
	  MutationSystem.setJMutationStructure();
      }
      catch (NoClassDefFoundError e) {
	  System.err.println("[ERROR] Could not find one of the classes necessary to run muJava. Make sure that the .jar file for openjava is in your classpath.");
	  System.err.println();
	  e.printStackTrace();
	  return;
      }
      MutationSystem.recordInheritanceRelation();
      GenMutantsMain main = new GenMutantsMain();
      try {
	  main.pack();
      }
      catch (NullPointerException e) {
	  System.err.println("[ERROR] An error occurred while initializing muJava. This may have happened because the files used by muJava are in an unexpected state. Try deleting any uncompiled mutants that were generated in the result/ directory, and then re-generate them.");
	  System.err.println();
	  e.printStackTrace();
	  return;
      }
      main.setVisible(true);
   } 

   /** <p> Initialize GenMutantsMain </p> */
   private void jbInit() throws Exception 
   {
      genPanel = new MutantsGenPanel(this);
      cvPanel = new ClassMutantsViewerPanel();
      tvPanel = new TraditionalMutantsViewerPanel();

      mutantTabbedPane.add("Mutants Generator", genPanel);
      mutantTabbedPane.add("Traditional Mutants Viewer", tvPanel);
      mutantTabbedPane.add("Class Mutants Viewer", cvPanel);
      this.getContentPane().add(mutantTabbedPane);

      this.addWindowListener( new java.awt.event.WindowAdapter()
      {
         public void windowClosing(WindowEvent e)
         {
            this_windowClosing(e);
         }
      } );
   }

   void this_windowClosing (WindowEvent e)
   {
      System.exit(0);
   }
}
