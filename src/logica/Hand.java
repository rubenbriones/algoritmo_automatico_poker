package logica;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import constantes.AccionEnum;
import constantes.Calle;
import constantes.Posicion;


public abstract class Hand implements IHand{

	public List<Accion> getAccionesPosterioresA(Accion a){
		return getOrderedActions().subList(getOrderedActions().indexOf(a), getOrderedActions().size()-1);
	}
	
	public Accion getUltimaAccion(){
		return getOrderedActions().get(getOrderedActions().size());
	}
	
	public Accion getPrimeraAccionHeroEn(Calle calle){
		return getActions().get(getHeroPosition()).get(calle).get(0);
	}

	//Nos devuelve TRUE si hero esta In-Position, es decir, que si hero hace CHECK pasamos a la siguiente calle
	//Si hay 3 o mas jugadores, tenemos que ser extrictamente los ultimos en hablar para que devuelva TRUE,
	//si estamos en medio de otros dos jugadores devolverá FALSE, pese a que estemos IP con respecto de uno de ellos 2.
	//Siempre nos referimos en la calleActual, y en el momento actual, teniendo en cuenta posibles FOLDS de jugadores en la calle actual.
	public boolean isHeroIPpostflop(){
		if(getNumberOfPlayers() == 2)
			if(getHeroPosition() == Posicion.SB) return true;
			else return false;
		else{
			List<Integer> alivePlayers = getPositionsAlivePlayers();
			for(Integer posRival : alivePlayers){
				if(getHeroPosition() > posRival) return false;
			}
			return true; //si no hay ninguna posRival mayor a posHero
		}
	}
	
	public int getPosUltimoAgresorPreflop(){
		List<Accion> acciones = getOrderedActionsPreflop();
		int posUltimoCall = -1; //si no ha habido ningun bet ni raise preflop el agresor sera el que haya hecho el ultimo call (no tiene porque ser la SB)
		for(int i=acciones.size()-1; i>=0; i++){
			Accion a = acciones.get(i);
			if(a.getAccion() == AccionEnum.RAISE || a.getAccion() == AccionEnum.BET) return a.getPosicion();
			if(a.getAccion() == AccionEnum.CALL) posUltimoCall = a.getPosicion();
		}
		return posUltimoCall;
	}
	
	//Devuelve la pos del ultimo jugador en betear (donkear, cbetear, probetear) o raisear en esa calle.
	//Si nadie beteo en esa calle, se devolverá un -1 y deveremos ver quien fue el ultimo agresor
	//en general, llamando al metodo getUltimoAgresorGlobal()
	public int getPosUltimoAgresorPostflop(Calle calle){
		List<Accion> acciones = null;
		switch(calle){
			case FLOP: acciones = getOrderedActionsFlop(); break;
			case TURN: acciones = getOrderedActionsTurn(); break;
			//el ultimo agresor en river nos da igual, pues no hay calles posteriores.
		}
		for(int i=acciones.size()-1; i>=0; i++){
			Accion a = acciones.get(i);
			if(a.getAccion() == AccionEnum.RAISE || a.getAccion() == AccionEnum.BET) return a.getPosicion();
		}
		return -1;
	}
	
	//Devuelve el ultimo agresor de la ultima calle jugada/completada, DE LA CALLE ANTERIOR A LA CALLEACTUAL
	//y si no lo hubiera miraria en las calles anteriores.
	public int getPosUltimoAgresorGlobalCallesAnteriores(){
		int pos = 999;
		if(getCalleActual() == Calle.RIVER)
			pos = getPosUltimoAgresorPostflop(Calle.TURN);
		if(getCalleActual() == Calle.TURN || pos == -1)
			pos = getPosUltimoAgresorPostflop(Calle.FLOP);
		if(getCalleActual() == Calle.FLOP || pos == -1)
			pos = getPosUltimoAgresorPreflop();
		return pos;
	}
	
	
}
