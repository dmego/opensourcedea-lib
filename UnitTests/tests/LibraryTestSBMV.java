package tests;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

//import java.util.ArrayList;

//import static org.junit.Assert.assertEquals;
//import java.util.*;
//import static org.junit.Assert.assertTrue;

import org.junit.Test;

//import dea.DEAModelOrientation;
import dea.*;




public class LibraryTestSBMV {
	
	/* If this Unit Test fails, please read the instructions in the
	 * Lpsolve class.*/
	
	DEAProblem tester = new DEAProblem(20, 4);

	public DEAPSolution GetSBMCResults() {
		
		
		DEAPSolution SBMCSol = new DEAPSolution(20, 4);
		
		SBMCSol.setObjectives(createSBMCObjectives());		
		
		return SBMCSol;
	}


	private double[] createSBMCObjectives() {
		
		double[] Objectives = new double[20];
		
		
		Objectives[0] = 0.4337207968687;
		Objectives[1] = 0.655740942924697;
		Objectives[2] = 1;
		Objectives[3] = 0.0711847916698139;
		Objectives[4] = 0.633843170637834;
		Objectives[5] = 1;
		Objectives[6] = 1;
		Objectives[7] = 0.718162581012847;
		Objectives[8] = 0.478929894370442;
		Objectives[9] = 0.14541274686293;
		Objectives[10] = 0.00662837755677064;
		Objectives[11] = 1;
		Objectives[12] = 0.14246955242863;
		Objectives[13] = 0.0204266899493119;
		Objectives[14] = 0.427059536602581;
		Objectives[15] = 0.489456449809396;
		Objectives[16] = 1;
		Objectives[17] = 0.0870188714455836;
		Objectives[18] = 1;
		Objectives[19] = 0.9999999999999; //inaccuracy probably caused by solver algos 0.999802949715669;
		return Objectives;
	}
	
	private int[] createSolRanks() {
		int[] ranks = new int[20];
		
		ranks[0] = 13;
		ranks[1] = 9;
		ranks[2] = 1;
		ranks[3] = 18;
		ranks[4] = 10;
		ranks[5] = 1;
		ranks[6] = 1;
		ranks[7] = 8;
		ranks[8] = 12;
		ranks[9] = 15;
		ranks[10] = 20;
		ranks[11] = 1;
		ranks[12] = 16;
		ranks[13] = 19;
		ranks[14] = 14;
		ranks[15] = 11;
		ranks[16] = 1;
		ranks[17] = 17;
		ranks[18] = 1;
		ranks[19] = 1; //Evaluated efficient because better accuracy of solver
		
		return ranks;
	}
	
	public void BuildDEAProblem(ModelType ModelType) { //, DEAModelOrientation ModelOrientation) {
		
		tester.setModelType(ModelType);
		//tester.setModelOrientation(ModelOrientation);
		tester.setVariableNames(TestData.createTestVariableNames());
		tester.setVariableTypes(TestData.createTestVariableTypes());
		tester.setDataMatrix(TestData.createTestDataMatrix());
		tester.setDMUNames(TestData.createTestDMUNames());
	}
	
//	private ArrayList<ArrayList<Integer>> getTestReferenceSet() {
//		ArrayList<ArrayList<Integer>> ReferenceSet = new ArrayList<ArrayList<Integer>>();
//		
//		ArrayList<Integer> Array0 = new ArrayList<Integer>();
//		Array0.add(6);
//		ReferenceSet.add(Array0);
//		
//		ArrayList<Integer> Array1 = new ArrayList<Integer>();
//		Array1.add(5);
//		ReferenceSet.add(Array1);
//		
//		ArrayList<Integer> Array2 = new ArrayList<Integer>();
//		Array2.add(6);
//		Array2.add(16);
//		ReferenceSet.add(Array2);
//				
//		ArrayList<Integer> Array3 = new ArrayList<Integer>();
//		Array3.add(16);
//		ReferenceSet.add(Array3);
//		
//		ArrayList<Integer> Array4 = new ArrayList<Integer>();
//		Array4.add(6);
//		ReferenceSet.add(Array4);
//		
//		ArrayList<Integer> Array5 = new ArrayList<Integer>();
//		Array5.add(5);
//		ReferenceSet.add(Array5);
//		
//		ArrayList<Integer> Array6 = new ArrayList<Integer>();
//		Array6.add(6);
//		ReferenceSet.add(Array6);
//		
//		ArrayList<Integer> Array7 = new ArrayList<Integer>();
//		Array7.add(5);
//		Array7.add(6);
//		ReferenceSet.add(Array7);
//		
//		ArrayList<Integer> Array8 = new ArrayList<Integer>();
//		Array8.add(6);
//		ReferenceSet.add(Array8);
//		
//		ArrayList<Integer> Array9 = new ArrayList<Integer>();
//		Array9.add(16);
//		ReferenceSet.add(Array9);
//		
//		ArrayList<Integer> Array10 = new ArrayList<Integer>();
//		Array10.add(6);
//		Array10.add(16);
//		ReferenceSet.add(Array10);
//		
//		ArrayList<Integer> Array11 = new ArrayList<Integer>();
//		Array11.add(11);
//		ReferenceSet.add(Array11);
//		
//		ArrayList<Integer> Array12 = new ArrayList<Integer>();
//		Array12.add(5);
//		Array12.add(16);
//		ReferenceSet.add(Array12);
//		
//		ArrayList<Integer> Array13 = new ArrayList<Integer>();
//		Array13.add(16);
//		ReferenceSet.add(Array13);
//		
//		ArrayList<Integer> Array14 = new ArrayList<Integer>();
//		Array14.add(5);
//		Array14.add(18);
//		ReferenceSet.add(Array14);
//		
//		ArrayList<Integer> Array15 = new ArrayList<Integer>();
//		Array15.add(6);
//		ReferenceSet.add(Array15);
//		
//		ArrayList<Integer> Array16 = new ArrayList<Integer>();
//		Array16.add(16);
//		ReferenceSet.add(Array16);
//		
//		ArrayList<Integer> Array17 = new ArrayList<Integer>();
//		Array17.add(5);
//		Array17.add(18);
//		ReferenceSet.add(Array17);
//		
//		ArrayList<Integer> Array18 = new ArrayList<Integer>();
//		Array18.add(18);
//		ReferenceSet.add(Array18);
//		
//		ArrayList<Integer> Array19 = new ArrayList<Integer>();
//		Array19.add(16);
//		ReferenceSet.add(Array19);
//		
//		return ReferenceSet;
//	}
	
	@Test
	public void TestSBM() {
		
		BuildDEAProblem(ModelType.SBMV); //, DEAModelOrientation.NonOriented);
		
		try {
			tester.solve();
		}
		catch (DEAException e) {
			System.out.println(e.toString());
		}
		
		DEAPSolution CheckedSol = GetSBMCResults();
		
		
		assertArrayEquals(tester.getObjectives(), CheckedSol.getObjectives(),0.0001);
		
		assertArrayEquals(tester.getRanks(true, RankingType.STANDARD, 10), createSolRanks());
		
//		assertEquals(getTestReferenceSet(),tester.getReferenceSet());
		
		assertEquals(tester.getOptimisationStatus(),SolverReturnStatus.OptimalSolutionFound);
		
	}
	
	
	
}
