package uglynumber.test;

import mujava.plugin.DataProvider;
import mujava.plugin.Plugin;
import mujava.plugin.SpecificationProvider;

/**
 * @author Shreyash
 *
 */
public class UglyNumberPlugin {

	public static void main(String[] args) {
		SpecificationProvider specs = new UglyNumberSpecificationProvider();
		DataProvider dataSet = new UglyNumberDataProvider();		
		
		Plugin uglyNumberPlugin = new Plugin(specs, dataSet);
		uglyNumberPlugin.execute(args);
	}
}
