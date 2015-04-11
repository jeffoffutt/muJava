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
import java.util.List;

import com.beust.jcommander.Parameter;
 /**
 * <p>
 * Description: Pre-defined arguments options for genmutes command
 * </p>
 * 
 * @author Lin Deng
 * @version 1.0  
  */
class genmutesCom {
	  @Parameter
	  private List<String> parameters = new ArrayList<String>();
	 	  
	  
	  @Parameter(names = "-AORB",  description = "Generate mutants of AORB")
	  private boolean AORB;
	  @Parameter(names = "-AORS", description = "Generate mutants of AORS")
	  private boolean AORS;
	  @Parameter(names = "-AOIU", description = "Generate mutants of AOIU")
	  private boolean AOIU;
	  @Parameter(names = "-AOIS", description = "Generate mutants of AOIS")
	  private boolean AOIS;
	  @Parameter(names = "-AODU", description = "Generate mutants of AODU")
	  private boolean AODU;
	  @Parameter(names = "-AODS", description = "Generate mutants of AODS")
	  private boolean AODS;
	  @Parameter(names = "-ROR",  description = "Generate mutants of ROR")
	  private boolean ROR;
	  @Parameter(names = "-COR",  description = "Generate mutants of COR")
	  private boolean COR;
	  @Parameter(names = "-COD",  description = "Generate mutants of COD")
	  private boolean COD;
	  @Parameter(names = "-COI",  description = "Generate mutants of COI")
	  private boolean COI;
	  @Parameter(names = "-SOR",  description = "Generate mutants of SOR")
	  private boolean SOR;
	  @Parameter(names = "-LOR",  description = "Generate mutants of LOR")
	  private boolean LOR;
	  @Parameter(names = "-LOI",  description = "Generate mutants of LOI")
	  private boolean LOI;
	  @Parameter(names = "-LOD",  description = "Generate mutants of LOD")
	  private boolean LOD;
	  @Parameter(names = "-ASRS", description = "Generate mutants of ASRS")
	  private boolean ASRS;
	  @Parameter(names = "-SDL",  description = "Generate mutants of SDL")
	  private boolean SDL;
	  @Parameter(names = "-VDL",  description = "Generate mutants of VDL")
	  private boolean VDL;
	  @Parameter(names = "-CDL",  description = "Generate mutants of CDL")
	  private boolean CDL;
	  @Parameter(names = "-ODL",  description = "Generate mutants of ODL")
	  private boolean ODL;
	  @Parameter(names = "-all",  description = "Generate mutants of ALL MUTATION OPERATORS")
	  private boolean all;
	  
	  @Parameter(names = "-D",  description = "Generate mutants of ALL classes in the directory")
	  private boolean D;
	  
	  
	  @Parameter(names = "--help", help = true)
	  private boolean help;
	  
	  
	  @Parameter(names = "-debug", description = "Debug mode")
	  private boolean debug = false;
	  
	  
	  
public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}


	public List<String> getParameters()
	{
		return parameters;
	}
	public void setParameters(List<String> parameters)
	{
		this.parameters = parameters;
	}

	public boolean getAORB()
	{
		return AORB;
	}
	public void setAORB(boolean aORB)
	{
		AORB = aORB;
	}
	public boolean getAORS()
	{
		return AORS;
	}
	public void setAORS(boolean aORS)
	{
		AORS = aORS;
	}
	public boolean getAOIU()
	{
		return AOIU;
	}
	public void setAOIU(boolean aOIU)
	{
		AOIU = aOIU;
	}
	public boolean getAOIS()
	{
		return AOIS;
	}
	public void setAOIS(boolean aOIS)
	{
		AOIS = aOIS;
	}
	public boolean getAODU()
	{
		return AODU;
	}
	public void setAODU(boolean aODU)
	{
		AODU = aODU;
	}
	public boolean getAODS()
	{
		return AODS;
	}
	public void setAODS(boolean aODS)
	{
		AODS = aODS;
	}
	public boolean getROR()
	{
		return ROR;
	}
	public void setROR(boolean rOR)
	{
		ROR = rOR;
	}
	public boolean getCOR()
	{
		return COR;
	}
	public void setCOR(boolean cOR)
	{
		COR = cOR;
	}
	public boolean getCOD()
	{
		return COD;
	}
	public void setCOD(boolean cOD)
	{
		COD = cOD;
	}
	public boolean getCOI()
	{
		return COI;
	}
	public void setCOI(boolean cOI)
	{
		COI = cOI;
	}
	public boolean getSOR()
	{
		return SOR;
	}
	public void setSOR(boolean sOR)
	{
		SOR = sOR;
	}
	public boolean getLOR()
	{
		return LOR;
	}
	public void setLOR(boolean lOR)
	{
		LOR = lOR;
	}
	public boolean getLOI()
	{
		return LOI;
	}
	public void setLOI(boolean lOI)
	{
		LOI = lOI;
	}
	public boolean getLOD()
	{
		return LOD;
	}
	public void setLOD(boolean lOD)
	{
		LOD = lOD;
	}
	public boolean getASRS()
	{
		return ASRS;
	}
	public void setASRS(boolean aSRS)
	{
		ASRS = aSRS;
	}
	public boolean getSDL()
	{
		return SDL;
	}
	public void setSDL(boolean sDL)
	{
		SDL = sDL;
	}
	public boolean getAll()
	{
		return all;
	}
	public void setAll(boolean all)
	{
		this.all = all;
	}
	  
	public boolean getD()
	{
		return D;
	}
	
	public void setD(boolean D)
	{
		this.D = D;
	}
	public boolean getVDL() {
		return VDL;
	}
	public void setVDL(boolean vDL) {
		VDL = vDL;
	}
	public boolean getCDL() {
		return CDL;
	}
	public void setCDL(boolean cDL) {
		CDL = cDL;
	}
	public boolean getODL() {
		return ODL;
	}
	public void setODL(boolean oDL) {
		ODL = oDL;
	}
	 
	
	  
	}