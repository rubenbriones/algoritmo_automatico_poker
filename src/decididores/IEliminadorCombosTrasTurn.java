package decididores;

import logica.Hand;
import logica.RangoConcreto;

public interface IEliminadorCombosTrasTurn {

	/**
	 * Eliminamos los combos (manos concretas mejor dicho) del rival que no pasan del turn,
	 * así que en la variable @rango solo dejariamos los combos con los que llega el rival al RIVER, 
	 * antes de que pase nada en esta calle.
	 **/
	public void eliminarCombosRangoConcretoTrasTurn(RangoConcreto rango, Hand hand, int posHero, int posRival);
	
}
