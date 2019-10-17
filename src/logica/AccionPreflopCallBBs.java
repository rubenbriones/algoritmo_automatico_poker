package logica;

import constantes.AccionEnum;

/**
 * Esta clase representa un CALL preflop que haremos si el tamanyo del último raise es menor a X BBs.
 * 
 * NOTA: Si numMaxBBs = 0, signifca que hacemos call a cualquier size.
 */
public class AccionPreflopCallBBs implements AccionPreflop {
	
	//Este será un porcentaje de seguridad que usaremos para que si tenemos que de maximo igualar un raise de 5BB
	//y nos meten 5.01BB pues no foldeemos, pues estaría mal.
	public static final double MARGEN_SEGURIDAD = 1.05;

	private double numMaxBBs; //Si esto es =0 significa que calleamos a cualquier size, incluido allin claro.
	
	public AccionPreflopCallBBs(double numBBs) {
		this.numMaxBBs = numBBs;
	}

	public double getNumMaxBBs() {
		return numMaxBBs;
	}

	@Override
	//NO SE NECEISTAN LAS ciegasEfectivas. Se le pasa un -1 en este parametro.
	public Accion getAccionFinal(double ciegaGrande, double tamanyoUltimoRaiseFichas, double ciegasEfectivas) {
		if(numMaxBBs!=0 && tamanyoUltimoRaiseFichas/ciegaGrande > numMaxBBs*MARGEN_SEGURIDAD)
			return new Accion(AccionEnum.FOLD);
		return new Accion(AccionEnum.CALL);
	}

	public String toString(){return "C"+numMaxBBs;}
}
