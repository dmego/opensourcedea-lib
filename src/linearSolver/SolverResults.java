
/*	<DEASolver (googleproject opensourcedea) is a java DEA solver.>
    Copyright (C) <2010>  <Hubert Paul Bernard VIRTOS>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    
    @author Hubert Paul Bernard VIRTOS
    @version 0.1 2011-02-04
*/

package linearSolver;

/**
 * This class defines a linear problem Solution. This object is returned by the {@link linearSolver.Lpsolve#solveLPProblem(java.util.ArrayList, double[], double[], dea.enums.SolverObjDirection)} method.
 * 
 * @author Hubert Virtos
 *
 */
public class SolverResults {
	public double[] VariableResult; // = new double[NbColumns];
	public double[] ConstraintResult; //= new double[NbRows];
	public double Objective; //= 0;
	public double[] DualResult; // = new double[NbColumns + NbRows + 1];
	public double[] Weights; // = new double[NbRows];
	SolverReturnStatus Status;
	
	
	
	
}
