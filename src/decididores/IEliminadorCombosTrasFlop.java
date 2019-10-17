package decididores;

import logica.Hand;
import logica.RangoConcreto;

public interface IEliminadorCombosTrasFlop {

	/**
	 * Eliminamos los combos (manos concretas mejor dicho) del rival que no pasan del flop,
	 * así que en la variable @rango solo dejariamos los combos con los que llega el rival al TURN, 
	 * antes de que pase nada en esta calle.
	 **/
	public void eliminarCombosRangoConcretoTrasFlop(RangoConcreto rango, Hand hand, int posHero, int posRival);
	
}
