package uglynumber.test;

import java.util.LinkedList;
import java.util.List;

import mujava.plugin.DataProvider;

public class UglyNumberDataProvider implements DataProvider {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> provideData() {
		int n = 50;
		LinkedList list = new LinkedList();
		for (int i = 2; i < n + 1; i++) {
			list.add(i);
		}
		LinkedList result = new LinkedList();
		while (list.size() != 0) {
			Integer t = (Integer) list.getFirst();
			result.add(t);
			list.removeFirst();
			if (list.size() != 0) {

				LinkedList temp = new LinkedList();
				for (Object k : list) {

					if (((Integer) k) % t != 0) {
						temp.add(k);
					}
				}
				list = temp;
			} else {
				break;
			}
		}
		return result;
	}

	
	public static void main(String[] args) {
		UglyNumberDataProvider provider = new UglyNumberDataProvider();
		List<Object> list = provider.provideData();
		
		for (Object o:list){
			System.out.println(((Integer)o).toString());
		}
	}
}
