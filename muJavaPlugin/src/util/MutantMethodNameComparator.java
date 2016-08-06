package util;

import java.util.Comparator;

public class MutantMethodNameComparator implements Comparator<String> {
	@Override
	public int compare(String method1, String method2) {

		if (method1 == null || method2 == null) {
			return -1;
		}

		String[] arr1 = method1.split("_");
		String[] arr2 = method2.split("_");

		Integer m1 = Integer.parseInt(arr1[arr1.length - 1]);
		Integer m2 = Integer.parseInt(arr2[arr2.length - 1]);

		return m1.compareTo(m2);

	}
}
