package bubblesort.test;

import java.util.ArrayList;
import java.util.List;

import mujava.plugin.DataProvider;

public class BubbleSortDataProvider implements DataProvider {

	@Override
	public List<Object> provideData() {

		Integer[] data0 = { 1, 2, 3, 4, 5 };
		Integer[] data1 = { 5, 4, 3, 2, 1 };
		Integer[] data2 = { -2, 7, 0, 42, 8 };
		Integer[] data3 = { -2, -600, -245, -2, -2151255 };
		Integer[] data4 = { 1, 1 };

		List<Object> dataset = new ArrayList<Object>();

		dataset.add(data0);
		dataset.add(data1);
		dataset.add(data2);
		dataset.add(data3);
		dataset.add(data4);

		return dataset;
	}

}
