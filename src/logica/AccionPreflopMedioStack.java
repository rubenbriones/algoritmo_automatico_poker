package logica;

import constantes.AccionEnum;

public class AccionPreflopMedioStack implements AccionPreflop {

	public static final double MEDIO_STACK = 0.45;
	@Override
	public Accion getAccionFinal(double ciegaGrande, double tamanyoUltimoRaiseFichas, double ciegasEfectivas) {
		return new Accion(AccionEnum.RAISE, round(ciegasEfectivas*MEDIO_STACK, 5));
	}

	private double round(double num, int multipleOf) {
		return Math.floor((num + multipleOf / 2) / multipleOf) * multipleOf;
	}
	
	public String toString(){return "AM";}
}
