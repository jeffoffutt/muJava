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
		Integer[] data4 = { -36749,36680,4550,84178,96125,88996,64896,96000,65640,75536,37221,8314};
		Integer[] data5 = {-1933828594,-1826741579,-1967431093,-292960255,1009398317,-186707243,1464534424,-1209090526,369338638,948232730};
		List<Object> dataset = new ArrayList<Object>();

		dataset.add(data0);
		dataset.add(data1);
		dataset.add(data2);
		dataset.add(data3);
		dataset.add(data4);
		dataset.add(data5);

		return dataset;
	}

}
