package org.opensourcedea.parameters;

public class OSDEAParameters {
	
	private static int nbDecimalsToEvaluateEfficiency = 5;
	
	
	
	
	public static int getNbDecimalsToEvaluateEfficiency() {
		return nbDecimalsToEvaluateEfficiency;
	}

	public static void setNbDecimalsToEvaluateEfficiency(
			int nbDecimalsToEvaluateEfficiency) {
		OSDEAParameters.nbDecimalsToEvaluateEfficiency = nbDecimalsToEvaluateEfficiency;
	}
	
	
	
	
}
