package logica;

import constantes.AccionEnum;

public class AccionPostflop {

	//Este será un porcentaje de seguridad que usaremos para que si tenemos que de maximo igualar un raise de 5BB
	//y nos meten 5.01BB pues no foldeemos, pues estaría mal.
	public static final double MARGEN_SEGURIDAD = 1.05;

	private Accion accion;
	//estas 3 variables solo se setaran si accion es un BET o RAISE
	private AccionEnum accVsRaiseAI; //esto sera o ALLIN o FOLD (pongo ALLIN y no CALL, por si hay un tercer jugador en la mano)
	private AccionEnum accVsRaiseNAI; //esto sera o ALLIN o CALL o FOLD
	private double numMaxBBs; //esta variable solo se seteara si accVsRaisNAI es CALL, y serian las BBs TOTALES de la subida del rival que pagariamos (son totles, NO adicionales)

	public AccionPostflop(Accion accion, AccionEnum accVsRaiseAI, AccionEnum accVsRaiseNAI, double numMaxBBs) {
		this.accion = accion;
		this.accVsRaiseAI = accVsRaiseAI;
		this.accVsRaiseNAI = accVsRaiseNAI;
		this.numMaxBBs = numMaxBBs;
	}
	public AccionPostflop(Accion accion, AccionEnum accVsRaiseAI, AccionEnum accVsRaiseNAI) {
		this.accion = accion;
		this.accVsRaiseAI = accVsRaiseAI;
		this.accVsRaiseNAI = accVsRaiseNAI;
	}
	public AccionPostflop(Accion accion) { //este solo se utilizara para si accion=CHECK o FOLD
		this.accion = accion;
	}
	
	public Accion getAccion() {
		return accion;
	}
	
	public Accion getAccionVsRaiseAI(){
		if(accVsRaiseAI == AccionEnum.FOLD) return new Accion(AccionEnum.FOLD);
		else return new Accion(AccionEnum.ALLIN);
	}
	
	public Accion getAccionVsRaiseNAI(double ciegaGrande, double tamanyoUltimoRaiseFichas) {
		if(accVsRaiseNAI == AccionEnum.FOLD) return new Accion(AccionEnum.FOLD);
		else if(accVsRaiseNAI == AccionEnum.ALLIN) return new Accion(AccionEnum.ALLIN);
		else 
			if(tamanyoUltimoRaiseFichas/ciegaGrande > numMaxBBs*MARGEN_SEGURIDAD)
				return new Accion(AccionEnum.FOLD);
			else
				return new Accion(AccionEnum.CALL);
	}

}
