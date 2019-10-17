package decididores;

import logica.Accion;
import logica.AccionPostflop;
import logica.ResCalculadoraEquity;
import logica.ResJugadas;
import logica.ResJugadasRango;

public interface IDecididorTurn {

	/* COMUNES AL FLOP */
	public AccionPostflop heroAgresorFlopOOP(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival);
	//Este casi siempre sera igual que el heroAgresorOOP, pero por si alguna vez queremos actuar ligeramente diferente los pongo separados.
	public AccionPostflop heroAgresorFlopIP(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival);
	
	public AccionPostflop heroVsDonkBet(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival, Accion accionRival);
	//Este metodo casi siempre sera igual que el heroVsDonkBet, pero lo pongo por si en un futuro queremos diferenciar entre
	//si nos donkea allin o no, con el fin de igualar los NAI con NADA solo con el proposito de farolearle despues. Porque en el
	//caso de que nos donkee allin eso no seria posible claro.
	public AccionPostflop heroVsDonkAllin(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival, Accion accionRival);

	public AccionPostflop heroDonkear(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival); //podemos donkear o checkear OOP

	public AccionPostflop heroFlotar(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival); //podemos flotar o checkear behind IP

	public AccionPostflop heroDefensorFlopIPvsContBet(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival, Accion accion);
	//Este casi siempre sera igual que el heroDefensorIIPvsBet
	public AccionPostflop heroDefensorFlopIPvsContAllin(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival, Accion accion);

	public AccionPostflop heroVsContBet(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival, Accion accion);
	//Este metodo puede que casi siempre sera igual que el de heroVsContBet
	public AccionPostflop heroVsContAllin(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival, Accion accion);
	
	//este metodo puede que sea parecido a heroVsContBet en el sentido que los doss van de igualar una apuesta fuera de posicion.
	public AccionPostflop heroVsFloat(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival, Accion accion);
	
	
	/* SOLO DEL TURN */
	public AccionPostflop heroAgresorDeleayearOOP(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival);
	public AccionPostflop heroAgresorDeleayearIP(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival);
	
	public AccionPostflop heroVsProbeBet(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival, Accion accion);
	public AccionPostflop heroVsProbeAllin(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival, Accion accion);
	
	public AccionPostflop heroProbetear(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival);
	
	public AccionPostflop heroFlotarDelayed(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival);
	
	public AccionPostflop heroDefensorIPvsDelayedBet(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival, Accion accion);
	public AccionPostflop heroDefensorIPvsDelayedAllin(ResCalculadoraEquity resCalc, ResJugadas jugadasHero, ResJugadasRango jugadasRival, Accion accion);
}
