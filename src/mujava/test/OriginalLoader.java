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


package mujava.test;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import mujava.MutationSystem;
import mujava.cli.Util;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @author Nan Li modified on 06/30/2013 for adding getResource(String)
 * @version 1.0
  */

public class OriginalLoader extends ClassLoader{

  public OriginalLoader()
  {
    super(null);
  }


  public synchronized Class loadTestClass(String name) throws ClassNotFoundException{
    Class result;
    try{
      byte[] data = getClassData(name,MutationSystem.TESTSET_PATH);
      result = defineClass(name, data,0,data.length);
      if(result==null){
        throw new ClassNotFoundException(name);
      }
    }catch(IOException e){
      throw new ClassNotFoundException();
    }
    return result;
  }

  public synchronized Class loadClass(String name) throws ClassNotFoundException
  {
    // See if type has already been loaded by
    // this class loader
    Class result = findLoadedClass(name);
    if (result != null){
      // Return an already-loaded class
      return result;
    }

    try{
      result = findSystemClass(name);
      return result;
    } catch (ClassNotFoundException e){
      // keep looking
    }

    try{
      byte[] data;
      try{
      // Try to load it
        data = getClassData(name,MutationSystem.CLASS_PATH);
      }catch(FileNotFoundException e){
        data = getClassData(name,MutationSystem.TESTSET_PATH);
      }
      result = defineClass(name, data,0,data.length);
      if(result==null) throw new ClassNotFoundException(name);
      return result;
    }catch(IOException e){
      throw new ClassNotFoundException();
    }
  }



  private byte[] getClassData(String name,String directory) throws FileNotFoundException,IOException
  {
    String filename = name.replace ('.', File.separatorChar) + ".class";
    Util.DebugPrint("file name: " + filename);

    // Create a file object relative to directory provided
    File f = new File (directory, filename);
     // Get stream to read from
    FileInputStream fis = new FileInputStream(f);

    BufferedInputStream bis = new BufferedInputStream(fis);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      int c = bis.read();
      while (c != -1) {
        out.write(c);
        c = bis.read();
      }
    } catch (IOException e) {
      return null;
    }
    return out.toByteArray();
  }
  
  /**
   * Overrides getResource (String) to get non-class files including resource bundles from property files
   */
  @Override
  public URL getResource(String name){
	  URL url = null;
	  File resource = new File(MutationSystem.CLASS_PATH, name);
	  if(resource.exists()){
		  try {
			return resource.toURI().toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  return url;
  }

}


