package util;

public class StringUtil {

	public String printIntegerArray(int[] array) {
		StringBuilder sb = new StringBuilder("[ ");
		if (array == null) {
			return "null";
		}

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i] + " ");
		}
		sb.append("]");
		return sb.toString();
	}

}
