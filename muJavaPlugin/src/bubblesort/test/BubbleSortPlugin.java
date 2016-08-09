package bubblesort.test;

import mujava.plugin.DataProvider;
import mujava.plugin.Plugin;
import mujava.plugin.SpecificationProvider;

/**
 * @author Shreyash
 *
 */
public class BubbleSortPlugin {

	public static void main(String[] args) {
		SpecificationProvider specs = new BubbleSortSpecificationProvider();
		DataProvider dataSet = new BubbleSortDataProvider();

		Plugin bubbleSortPlugin = new Plugin(specs, dataSet);
		bubbleSortPlugin.execute(args);
	}
}
