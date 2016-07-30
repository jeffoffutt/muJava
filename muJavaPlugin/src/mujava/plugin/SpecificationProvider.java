package mujava.plugin;

import java.util.List;

/**
 * 
 * @author Shreyash
 *
 */
public interface SpecificationProvider {

	/**
	 * Implement this method to provide specification for validating the mutant
	 * method under test for absolute/relative/strict correctness. For example -
	 * if a method --> public int method(int x, int y) is to be tested, this
	 * method should return desired output for given input against which the
	 * mutants will be tested.
	 * 
	 * @return {@link List}
	 */
	public Object provideSpecification(Object args);

	public boolean testAgainstSpecification(Object mutantTestResult, Object specification);

	public boolean testForStrictlyRelativeCorrectness(Object mutantTestResult, Object baseProgramResult, Object specification);

	public boolean testForRelativeCorrectness(Object mutantTestResult, Object baseProgramResult, Object specification);
}
