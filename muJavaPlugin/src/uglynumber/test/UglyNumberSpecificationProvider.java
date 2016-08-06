package uglynumber.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mujava.plugin.SpecificationProvider;

public class UglyNumberSpecificationProvider implements SpecificationProvider {

	public static Set<Integer> primeFactors(int number) {
		Set<Integer> primefactors = new HashSet<>();
		long copyOfInput = number;

		for (int i = 2; i <= copyOfInput; i++) {
			if (copyOfInput % i == 0) {
				primefactors.add(i); // prime factor
				copyOfInput /= i;
				i--;
			}
		}
		return primefactors;
	}

	@Override
	public Object provideSpecification(Object args) {
		boolean isUgly = true;
		int num = (Integer) args;

		Set<Integer> factors = primeFactors(num);

		for (Integer i : factors) {
			if (i != 1 && i != 2 && i != 3 && i != 5) {
				isUgly = false;
				break;
			}
		}
		return isUgly;
	}

	public static void main(String[] args) {
		UglyNumberDataProvider dataProvider = new UglyNumberDataProvider();

		List<Object> list = dataProvider.provideData();
		list.add(20);
		list.add(40);
		list.add(14);

		for (Object data : list) {
			System.out.println("Finding factors of:" + ((Integer) data).intValue());
			Set<Integer> factors = primeFactors((Integer) data);
			StringBuilder sb = new StringBuilder("Factors: ");
			for (Integer factor : factors) {
				sb.append(factor + " ");
			}
			System.out.println(sb.toString());
			System.out.println("Is Ugly? :" + new UglyNumberSpecificationProvider().provideSpecification(data));
		}
	}

	@Override
	public boolean testAgainstSpecification(Object mutantTestResult, Object specification) {
		Boolean mutantTestResultObject = (Boolean) mutantTestResult;
		Boolean specificationObject = (Boolean) specification;

		if (mutantTestResultObject == null || specificationObject == null) {			
			return false;
		} else {
			return mutantTestResultObject.equals(specificationObject);
		}
	}

	@Override
	public boolean testForStrictlyRelativeCorrectness(Object mutantTestResult, Object baseProgramResult,
			Object specification) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean testForRelativeCorrectness(Object mutantTestResult, Object baseProgramResult, Object specification) {
		// TODO Auto-generated method stub
		return false;
	}

}
