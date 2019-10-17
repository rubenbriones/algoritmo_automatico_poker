package logica;

import constantes.AccionEnum;

public class AccionPreflopAllin implements AccionPreflop {

	@Override
	public Accion getAccionFinal(double ciegaGrande, double tamanyoUltimoRaiseFichas, double ciegasEfectivas) {
		return new Accion(AccionEnum.ALLIN);
	}

	public String toString(){return "A";}
	
}
