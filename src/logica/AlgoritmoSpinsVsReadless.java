package logica;

import java.util.List;

import constantes.AccionEnum;
import constantes.Calle;
import constantes.SituacionPreflop;

/**
 * Este es un algoritmo solo pensado para los SPINS EN GENERAL, sirve tanto si estamos en la etapa de 3H como en HU.
 * Y Solo puede llegarnos el turno 2 VECES en una misma calle.
 * Y la primera vez que nos llegue el turno tenemos que tener ya decidio que vamos a hacer si nos llega
 * el turno de nuevo (nos enfrentamos a un raise, ya sea AI o NAI).
 * @author Ruben
 *
 */
public class AlgoritmoSpinsVsReadless extends Algoritmo{

	private Algoritmo alg3H;
	private Algoritmo algHU;
	
	public AlgoritmoSpinsVsReadless(ISelectorManos selector, ICalculadoraEquity calc) {
		super(selector, calc);
		
		this.alg3H = new AlgoritmoSpins3HvsReadless(selector, calc);
		//this.algHU = new AlgoritmoSpinsHUvsReadless(selector, calc);
	}

	private MovPreflop movPreflop;
	
	@Override
	public Accion analyze(Hand hand) {
		if(hand.getNumberOfPlayers() == 3) return alg3H.analyze(hand);
		else 							   return algHU.analyze(hand); //hand.getNumberOfPlayers() == 2
	}

	@Override
	public void resetFinDeMano() {
		alg3H.resetFinDeMano();
		algHU.resetFinDeMano();
	}

}
