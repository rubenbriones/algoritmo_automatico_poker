package logica;

import constantes.AccionEnum;

/**
 * Esta clase representa un RAISE (o rol) preflop que haremos a un tamanyo fijo de BBs.
 */
public class AccionPreflopRaiseBBs implements AccionPreflop {

	private double numBBs;
	
	public AccionPreflopRaiseBBs(double numBBs) {
		this.numBBs = numBBs;
	}

	public double getNumBBs() {
		return numBBs;
	}

	@Override
	public Accion getAccionFinal(double ciegaGrande, double tamanyoUltimoRaiseFichas, double ciegasEfectivas) {
		return new Accion(AccionEnum.RAISE, ciegaGrande*numBBs);
	}

	public String toString(){return "R"+numBBs;}
}
