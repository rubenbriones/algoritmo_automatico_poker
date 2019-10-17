package logica;

public class ResCalculadoraEquity {
	
	private double equityMano;
	private double equityRival;
	private int numManosPosibles;
	
	public ResCalculadoraEquity(double equityMano, int numManosPosibles) {
		this.equityMano = equityMano;
		this.equityRival = 100-equityMano;
		this.numManosPosibles = numManosPosibles;
	}

	public double getEquityMano() {
		return equityMano;
	}

	public double getEquityRival() {
		return equityRival;
	}

	public int getNumManosPosibles() {
		return numManosPosibles;
	}
	
}
