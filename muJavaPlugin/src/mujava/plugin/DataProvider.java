package mujava.plugin;

import java.util.List;

/**
 * 
 * @author Shreyash
 *
 */
public interface DataProvider {
	/**
	 * Implement this method to provide parameters for executing the mutant
	 * method under test. For example - if a method --> public int method(int x,
	 * int y) is to be tested, for dataset {(1,2); (2,5)} this method should
	 * construct a list of size 2 with each element being an array of Integers
	 * 
	 * public List<Object[]> provideData(); { List<Object[]> data = new
	 * ArrayList<Object[]> (); Object[] data0 = {1,2}; Object[] data1 = {2,5};
	 * data.add(data0); data.add(data1); return data; }
	 * 
	 * @return {@link List}
	 */
	public List<Object> provideData();
}
