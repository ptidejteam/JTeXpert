package csbst.analysis;

public class BranchesProperties {
	/*hidden node' properties
	 * - numberBranch
	 * - expression
	 * - numberParentBranch (Parent numberBranch)
	 * - isMultiBranchesRoot (conditional statements)
	 * - methodContainer (branches)
	 * - influencers (branches)
	 * - 
	 * - callers (Methods) contains list of branches
	 * - transformers (DataMember)
	 * - 
	 */
	public static final String NUM_BRANCH="numberBranch";
	public static final String EXPRESSION="expression";
	public static final String NUM_PARENT_BRANCH="numberParentBranch";
	public static final String IS_MULTIBRANCHES_ROOT="isMultiBranchesRoot";
	public static final String METHOD_CONTAINER="methodContainer";
	public static final String INFLUENCERS="influencers";
	public static final String DIFF_COEF="difficultyCoefficient";

}
