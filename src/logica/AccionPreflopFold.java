package logica;

import constantes.AccionEnum;

public class AccionPreflopFold implements AccionPreflop {

	@Override
	public Accion getAccionFinal(double ciegaGrande,double tamanyoUltimoRaiseFichas, double ciegasEfectivas) {
		return new Accion(AccionEnum.FOLD);
	}

	public String toString(){return "F";}
}
