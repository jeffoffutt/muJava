package bubblesort.test;

import java.util.Arrays;

import mujava.plugin.SpecificationProvider;

public class BubbleSortSpecificationProvider implements SpecificationProvider {

	@Override
	public Object provideSpecification(Object args) {

		int[] intArgs = (int[]) args;
		Arrays.sort(intArgs);

		return intArgs;
	}

	/*
	 * private boolean validateArguments(Object testResult, Object
	 * specification) { int[] resultArray = (int[])testResult;
	 * 
	 * 
	 * if (!(testResult instanceof Integer[]) || !(specification instanceof
	 * Integer[])) { throw new IllegalArgumentException(
	 * "Both the parameters should be instance of Integer[]"); } return true; }
	 */

	@Override
	public boolean testAgainstSpecification(Object mutantTestResult, Object specification) {

		for (int index = 0; index < ((int[]) mutantTestResult).length; index++) {

			if (((int[]) mutantTestResult)[index] != ((int[]) specification)[index]) {
				return false;
			}

		}

		return true;
	}

	@Override
	public boolean testForStrictlyRelativeCorrectness(Object mutantTestResult, Object baseProgramResult,
			Object specification) {

		int[] specificationArray = (int[]) specification;
		int[] baseProgramResultArray = (int[]) baseProgramResult;
		int[] mutantTestResultArray = (int[]) mutantTestResult;

		boolean moreCorrectMutant = false;

		for (int i = 0; i < specificationArray.length; i++) {

			if (specificationArray[i] == baseProgramResultArray[i]) {
				if (mutantTestResultArray[i] != specificationArray[i]) {
					moreCorrectMutant = false;
					return moreCorrectMutant;
				}
			} else if (mutantTestResultArray[i] == specificationArray[i]) {
				moreCorrectMutant = true;
			}
		}

		return moreCorrectMutant;
	}

	@Override
	public boolean testForRelativeCorrectness(Object mutantTestResult, Object baseProgramResult, Object specification) {

		int[] specificationArray = (int[]) specification;
		int[] baseProgramResultArray = (int[]) baseProgramResult;
		int[] mutantTestResultArray = (int[]) mutantTestResult;

		boolean moreCorrectMutant = false;

		for (int i = 0; i < specificationArray.length; i++) {

			if (specificationArray[i] != baseProgramResultArray[i]
					&& specificationArray[i] == mutantTestResultArray[i]) {
				moreCorrectMutant = true;
				return moreCorrectMutant;
			}
		}

		return moreCorrectMutant;
	}

}
