package logica;

import java.util.List;

import constantes.AccionEnum;
import constantes.Calle;
import constantes.SituacionPreflop;

/**
 * Este es un algoritmo solo pensado para el 3H en los spins, no para el HU.
 * Y Solo puede llegarnos el turno 2 VECES en una misma calle.
 * Y la primera vez que nos llegue el turno tenemos que tener ya decidio que vamos a hacer si nos llega
 * el turno de nuevo (nos enfrentamos a un raise, ya sea AI o NAI).
 * @author Ruben
 *
 */
public class AlgoritmoSpins3HvsReadless extends Algoritmo{

	public AlgoritmoSpins3HvsReadless(ISelectorManos selector, ICalculadoraEquity calc) {
		super(selector, calc);
	}

	private MovPreflop movPreflop;
	
	@Override
	public Accion analyze(Hand hand) {
		/* copiar el de HU para tenerlo como base, pero revisra muy mucho que se cubran todos los sposts que puede haber en 3h*/
		
		return null;
	}

	@Override
	public void resetFinDeMano() {
		// TODO Auto-generated method stub
		
	}

}
