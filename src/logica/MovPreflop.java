package logica;

import constantes.AccionEnum;

public class MovPreflop {
	//Todas tienen definida una primeraAccion, y las acciones vs raises solo las que se pueden enfretar a ellos
	//pero si la primera accion se puede enfrentar a un raise, siempre va a tener definidas las 2 variables, nunca solo una.
	private AccionPreflop primeraAccion;
	private AccionPreflop accionVSraiseNAI;
	private AccionPreflop accionVSraiseAI;
	
	
	public MovPreflop(AccionPreflop primeraAccion) {
		this.primeraAccion = primeraAccion;
	}
	public MovPreflop(AccionPreflop primeraAccion, AccionPreflop accionVSraiseNAI, AccionPreflop accionVSraiseAI) {
		this.primeraAccion = primeraAccion;
		this.accionVSraiseNAI = accionVSraiseNAI;
		this.accionVSraiseAI = accionVSraiseAI;
	}
	
	public AccionPreflop getPrimeraAccion() {
		return primeraAccion;
	}
	public void setPrimeraAccion(AccionPreflop primeraAccion) {
		this.primeraAccion = primeraAccion;
	}
	public AccionPreflop getAccionVSraiseNAI() {
		return accionVSraiseNAI;
	}
	public void setAccionVSraiseNAI(AccionPreflop accionVSraiseNAI) {
		this.accionVSraiseNAI = accionVSraiseNAI;
	}
	public AccionPreflop getAccionVSraiseAI() {
		return accionVSraiseAI;
	}
	public void setAccionVSraiseAI(AccionPreflop accionVSraiseAI) {
		this.accionVSraiseAI = accionVSraiseAI;
	}
	
	public String toString(){
		String res = primeraAccion.toString();
		if(accionVSraiseNAI!=null) res += "|"+accionVSraiseNAI.toString()+"|"+accionVSraiseAI.toString();
		return res;
	}
	
}
