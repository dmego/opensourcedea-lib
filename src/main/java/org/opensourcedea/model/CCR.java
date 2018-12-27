package org.opensourcedea.model;

import java.util.ArrayList;
import java.util.Arrays;

import lpsolve.LpSolve;

import org.opensourcedea.dea.*;
import org.opensourcedea.exception.DEASolverException;
import org.opensourcedea.exception.MissingDataException;
import org.opensourcedea.exception.ProblemNotSolvedProperlyException;
import org.opensourcedea.linearSolver.Lpsolve;
import org.opensourcedea.linearSolver.SolverResults;
import org.opensourcedea.parameters.OSDEAParameters;
import org.opensourcedea.utils.MathUtils;

public class CCR extends Model {

	
	/**
	 * This method creates and solves the CCR problems. The CCRO solution is derived from
	 * CCRI solution (the CCRO model is thus never built).
	 * @param deaP The DEAProblem to solve.
	 * @param nbDMUs The number of DMUs of the DEAProblem.
	 * This information is already in the DEAProblem but it is more efficient to use call the method
	 * with the number of DMUs rather than determining the number of DMUs from the DEAProblem.
	 * @param nbVariables The number of Variables of the DEA Problem.
	 * @param transposedMatrix The transposed of the DEAProblem matrix (model in the envelopment form)
	 * @param returnSol The Solution Object in which the solution will be stored.
	 * @param dmuIndex The index of the DMU under examination.
	 * @throws MissingDataException
	 * @throws ProblemNotSolvedProperlyException
	 * @throws DEASolverException
	 */
	@Override
	public void createAndSolve(DEAProblem deaP, int nbDMUs,
			int nbVariables, double[][] transposedMatrix,
			DEAPSolution returnSol, Integer dmuIndex)
		throws ProblemNotSolvedProperlyException, DEASolverException, MissingDataException {
		
		ArrayList<double[]> constraints = new ArrayList<double []>();
		double[] objF = new double [nbDMUs + nbVariables + 1];
		double[] rhs1;
		double[] rhs2;
		int[] solverEqualityType1;
		int[] solverEqualityType2;
		SolverResults sol = new SolverResults();
		
		  /////////////////////
		 //		Phase I		//
		/////////////////////
		
		//Instantiate the rhs1 and solverEqualityType1 arrays
		rhs1 = new double [nbVariables]; //RHS Phase I
		solverEqualityType1 = new int[nbVariables];
		
		solvePhaseI(deaP, nbDMUs, nbVariables, transposedMatrix, dmuIndex,
					constraints, objF, rhs1, solverEqualityType1, returnSol, sol);

		
		  /////////////////////////////
		 //		Solve Phase II		//
		/////////////////////////////
		/* The only things that are needed for Phase II are to:
		 * - add an extra Constraint Row to Constraints in order to ensure Theta Phase I
		 *   is not changed during Phase II Optimisation.
		 * - add the corresponding Theta to the RHS Array
		 * - change the Objective Function accordingly (all 1 on Slacks, all others coeff = 0).*/
		
		//Instantiate the rhs2 and solverEqualityType2 arrays
		rhs2 = new double[nbVariables + 1];
		solverEqualityType2 = new int[nbVariables + 1];
		
		solvePhaseII(deaP, nbDMUs, nbVariables, constraints, objF, rhs1,
				rhs2, solverEqualityType1, solverEqualityType2, sol, returnSol, dmuIndex);

	}

	
	
	
	
	private void solvePhaseI (DEAProblem deaP, int nbDMUs,
			int nbVariables, double[][] transposedMatrix, Integer dmuIndex,
			ArrayList<double[]> constraints, double[] objF, double[] rhs1,
			int[] solverEqualityType1, DEAPSolution returnSol, SolverResults sol)
	throws ProblemNotSolvedProperlyException, DEASolverException, MissingDataException  {
		
		createPhaseOneModel(deaP, nbDMUs, nbVariables, transposedMatrix, dmuIndex,
				constraints, objF, rhs1, solverEqualityType1);

		sol = Lpsolve.solveLPProblem(constraints, objF, rhs1, SolverObjDirection.MIN,
				solverEqualityType1);
		
		
		checkSolverStatus(sol, dmuIndex, deaP.getDMUName(dmuIndex));
		
		storePhaseOneInformation(deaP, returnSol, dmuIndex, sol);
		
	}
	
	
	private void solvePhaseII (DEAProblem deaP, int nbDMUs, int nbVariables, ArrayList<double[]> constraints, double[] objF,
			double[]rhs1, double[] rhs2, int[] solverEqualityType1, int[] solverEqualityType2, SolverResults sol,
			DEAPSolution returnSol, Integer dmuIndex) throws ProblemNotSolvedProperlyException, DEASolverException, MissingDataException {
		
		createPhaseTwoModel(deaP, nbDMUs, nbVariables, constraints, objF, rhs1,
				rhs2, solverEqualityType1, solverEqualityType2, returnSol, dmuIndex);
		
		
		//Solve the Phase II Problem
		sol = Lpsolve.solveLPProblem(constraints, objF, rhs2, SolverObjDirection.MAX,
				solverEqualityType2);
		
		checkSolverStatus(sol, dmuIndex, deaP.getDMUName(dmuIndex));
		
		storePhaseTwoInformation(deaP, nbDMUs, nbVariables, returnSol, dmuIndex, sol);
		
	}
	
	
	
	
	/**
	 * 
	 * @param deaP
	 * @param nbDMUs
	 * @param nbVariables
	 * @param returnSol
	 * @param dmuIndex
	 * @param sol
	 * @throws MissingDataException 
	 */
	private static void storePhaseTwoInformation(DEAProblem deaP, int nbDMUs,
			int nbVariables, DEAPSolution returnSol, int dmuIndex, SolverResults sol) throws MissingDataException {
		
	
		//Collect information from Phase II (Theta)
		ArrayList<NonZeroLambda> refSet = new ArrayList<NonZeroLambda>();
		if(deaP.getModelType() == ModelType.CCR_I) {
			for(int lambdaPos = 0; lambdaPos < nbDMUs; lambdaPos++) {
				if(sol.VariableResult[lambdaPos + 1] != 0) {
					refSet.add(new NonZeroLambda(lambdaPos, sol.VariableResult[lambdaPos + 1]));
				}
			}
			returnSol.setReferenceSet(dmuIndex, refSet);
			returnSol.setSlackArrayCopy(dmuIndex, sol.VariableResult, nbDMUs + 1, nbVariables);
		}
		else {
			for(int lambdaIndex = 0; lambdaIndex < nbDMUs; lambdaIndex++) {
				if(sol.VariableResult[lambdaIndex + 1] != 0) {
					refSet.add(new NonZeroLambda(lambdaIndex,
							sol.VariableResult[lambdaIndex + 1] / returnSol.getObjective(dmuIndex)));
				}
			}
			returnSol.setReferenceSet(dmuIndex, refSet);
			for(int varIndex = 0; varIndex < nbVariables; varIndex++) {
				//setSlack(i, s, Sol.VariableResult[NbDMUs + 1 + s] * ReturnSol.getObjective(i));
				returnSol.setSlack(dmuIndex, varIndex, sol.VariableResult[nbDMUs + 1 + varIndex] / returnSol.getObjective(dmuIndex));
			}
		}
		
		
		//efficiency - need to be done after slacks!
		boolean isEfficient = true;
		if(MathUtils.round(returnSol.getObjective(dmuIndex), OSDEAParameters.getNbDecimalsToEvaluateEfficiency()) == 1){
			for(double slack : returnSol.getSlacks(dmuIndex)){
				if(MathUtils.round(slack, OSDEAParameters.getNbDecimalsToEvaluateEfficiency()) > 0) {
					isEfficient = false;
				}
			}
		}
		else {
			isEfficient = false;
		}
		returnSol.setEfficient(dmuIndex, isEfficient);
		
		
		if(deaP.getModelType() == ModelType.CCR_I) {
			for (int varIndex = 0; varIndex < nbVariables; varIndex++) {
				if(deaP.getVariableOrientation(varIndex) == VariableOrientation.INPUT) {
					//Projections
					returnSol.setProjection(dmuIndex, varIndex,
							returnSol.getObjective(dmuIndex) * deaP.getDataMatrix(dmuIndex, varIndex)
							- returnSol.getSlack(dmuIndex, varIndex));
				}
				else {
					//Projections
					returnSol.setProjection(dmuIndex, varIndex,
							deaP.getDataMatrix(dmuIndex, varIndex) + returnSol.getSlack(dmuIndex, varIndex));
				}
			}
		}
		else {
			for (int varIndex = 0; varIndex < nbVariables; varIndex++) {
				if(deaP.getVariableOrientation(varIndex) == VariableOrientation.OUTPUT) {
					//Projections
					returnSol.setProjection(dmuIndex, varIndex,
							(1 / returnSol.getObjective(dmuIndex)) * deaP.getDataMatrix(dmuIndex, varIndex)
							+ returnSol.getSlack(dmuIndex, varIndex));
				}
				else {
					//Projections
					returnSol.setProjection(dmuIndex, varIndex,
							deaP.getDataMatrix(dmuIndex, varIndex) - returnSol.getSlack(dmuIndex, varIndex));
				}
			}
		}

		SolverStatus.checkSolverStatus(returnSol, sol);

	}

	/**
	 * Creates the model for the second phase of the BCC model.
	 * @param deaP The DEAProblem to solve.
	 * @param nbDMUs The number of DMUs of the DEAProblem.
	 * @param nbVariables The number of Variables of the DEA Problem.
	 * @param Constraints The Constraints of the DEA Problem
	 * @param ObjF The Objective function of the DEA Problem
	 * @param RHS1 The Right Hand Side element of the first phase.
	 * @param RHS2 The Right Hand Side element of the second phase.
	 * @param SolverEqualityType1 The equality type (equal, lower or equal...) from the first phase.
	 * @param SolverEqualityType2 The equality type (equal, lower or equal...) from the second phase.
	 * @param Sol The Solver result object
	 * @throws MissingDataException 
	 */
	private static void createPhaseTwoModel(DEAProblem deaP, int NbDMUs,
			int NbVariables, ArrayList<double[]> Constraints, double[] ObjF,
			double[] RHS1, double[] RHS2, int[] SolverEqualityType1,
			int[] SolverEqualityType2, DEAPSolution Sol, int dmuIndex) throws MissingDataException {
		
		double[] ConstraintRow;
		
		//Changing Constraint Matrix
		ConstraintRow = new double[NbDMUs + NbVariables + 1];
		ConstraintRow[0] = 1;
		Constraints.add(ConstraintRow);
				
		
		//Changing RHS & SolverEqTypes
		System.arraycopy(RHS1, 0, RHS2, 0, RHS1.length);

		RHS2[NbVariables] = Sol.getObjective(dmuIndex);

		System.arraycopy(SolverEqualityType1, 0, SolverEqualityType2, 0,
				SolverEqualityType1.length);
		SolverEqualityType2[NbVariables] = LpSolve.EQ;

		
	
		//Change Objective Function
		Arrays.fill(ObjF,0);
		for (int j = NbDMUs + 1; j < NbDMUs + NbVariables; j++) {
			ObjF[j] = 1;
		}

	}

	/**
	 * Stores phase one solution.
	 * @param deaP The DEAProblem being solved.
	 * @param returnSol The solution where results are stored.
	 * @param i
	 * @param sol
	 * @throws MissingDataException 
	 * @throws Exception
	 */
	private static void storePhaseOneInformation(DEAProblem deaP,
			DEAPSolution returnSol, int i, SolverResults sol) throws MissingDataException {

		//Collect information from Phase I (Theta)	
		returnSol.setObjective(i, sol.Objective);


		if(deaP.getModelOrientation() == ModelOrientation.INPUT_ORIENTED) {
			returnSol.setWeights(i, sol.Weights);
		}
		else {
			returnSol.setWeights(i, new double[sol.Weights.length]);
			for(int k = 0; k < sol.Weights.length; k++) {
				returnSol.setWeight(i, k, sol.Weights[k] / sol.Objective);
			}
		}

		SolverStatus.checkSolverStatus(returnSol, sol);
	}

	/**
	 * 
	 * @param deaP
	 * @param nbDMUs
	 * @param nbVariables
	 * @param transposedMatrix
	 * @param dmuIndex
	 * @param constraints
	 * @param objF
	 * @param rhs1
	 * @param solverEqualityType1
	 * @throws MissingDataException 
	 */
	private static void createPhaseOneModel(DEAProblem deaP, int nbDMUs,
			int nbVariables, double[][] transposedMatrix, int dmuIndex,
			ArrayList<double[]> constraints, double[] objF, double[] rhs1,
			int[] solverEqualityType1) throws MissingDataException {
		
		double[] constraintRow;
		for (int varIndex = 0; varIndex < nbVariables; varIndex++) {
						
			//Build the Constraint Matrix, row by row
			constraintRow = new double[nbDMUs + nbVariables + 1];
			//First column (input values for  DMU under observation (DMUIndex) * -1; 0 for outputs)
			if (deaP.getVariableOrientation(varIndex) == VariableOrientation.INPUT) {
				constraintRow[0] = transposedMatrix[varIndex] [dmuIndex] * -1;
			}
			else  {
				constraintRow[0] = 0;
			}
	
			//Copy rest of the data matrix
			System.arraycopy(transposedMatrix[varIndex], 0, constraintRow, 1, nbDMUs);
			
			//and slacks
			if (deaP.getVariableOrientation(varIndex) == VariableOrientation.INPUT) {
				constraintRow[nbDMUs + 1 + varIndex] = -1;
			}
			else {
				constraintRow[nbDMUs + 1 + varIndex] = 1;
			}
			
			//Add the row to the Constraints ArrayList
			constraints.add(constraintRow);
		
			//Build RHS & SolverEqualityTypes
			if (deaP.getVariableOrientation(varIndex) == VariableOrientation.INPUT) {
				rhs1[varIndex] = 0;
				solverEqualityType1[varIndex] = LpSolve.EQ;
			}
			else {
				rhs1[varIndex] = transposedMatrix[varIndex] [dmuIndex];
				solverEqualityType1[varIndex] = LpSolve.EQ;
			}


		} //finished looping through all variables

		
		//Build Objective Function (Theta column is assigned the weight 1. All the other columns are left to 0).
		objF[0] = 1;
		
	}

	
	
//	private void checkSolverStatus(SolverResults sol, Integer dmuIndex, String dmuName) {
//		if(sol.Status == SolverReturnStatus.SOLUTION_FOUND_LOW_ACCURACY) {
//			System.out.println("Low solver accuracy for DMU: " + dmuName + " (DMU Index: " + dmuIndex + ").");
//		}
//	}

	
}
