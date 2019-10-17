package logica;

import constantes.AccionEnum;

public class AccionPreflopCheck implements AccionPreflop {

	@Override
	public Accion getAccionFinal(double ciegaGrande,double tamanyoUltimoRaiseFichas, double ciegasEfectivas) {
		return new Accion(AccionEnum.CHECK);
	}

	public String toString(){return "X";}
}
