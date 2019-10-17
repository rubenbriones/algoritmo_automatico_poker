package logica;

import java.util.List;
import java.util.Map;

import constantes.Calle;

public interface IHand {

	public String getHistory();
	
	public long getHandNumber();
	
	public int getNumberOfPlayers();

	public String getHeroName();
	public int getHeroPosition();
	
	public int getNumTurnoCalleActual(); //Nos devuelve un int que nos dira si es la 1ª,2ª,3ª... vez que es nuestro turno en la calle actual
	
	public double getSB();
	public double getBB();

	public Map<String, Integer> getPositions();
	public Integer getPosition(String player);
	
	//Devuelve una lista con las posiciones de los jugadores vivos en el momento ACTUAL de la mano.
	//Es en tiempo real, siempre que se le llame cuando sea nuestro turno claro.
	public List<Integer> getPositionsAlivePlayers();
	
	//Devuelve los stacks que tienen los jugadores vivos justo en el momento de que empiece esa calle.
	//Estos se calculan por primera vez cuando se llama al metodo, pero luego ya solo se devuelven si se les llama una segunda vez.
	public Map<Integer, Double> getStacksPreflop(); //aqui hay que tener los stacks ANTES de poner las ciegas.
	public Map<Integer, Double> getStacksFlop();
	public Map<Integer, Double> getStacksTurn();
	public Map<Integer, Double> getStacksRiver();

	//Nos devuelve el stack efectivo en fichas al LLEGARNOS LA ACCION A NOSOTROS en la calle que le pasemos como argumento
	//No es lo mismo que el stack efectivo al principio de la calle, ya que puede que alguno haya foldeado.
	//NOTA: SI LLAMAMOS A ESTE METODO EN ALGUN MOMENTO QUE NO SEA NUESTRO TURNO FALLARA.
	//NOTAAAAAA: Este metodo en general hace varios calculos, NO ES UN SIMPLE RETURN, POR LO QUE HAY QUE LLAMARLE EL MINIMO NUMERO DE VECES POSIBLE.
	//Asi que si vamos a usar esa variable otra vez es mejor que la guardameos en una variable auxiliar en cez de vovler a llamar a este metodo.
	/** Usa el Map "actions" **/
	public double getStackEfectivo(Calle calle);
	
	public Calle getCalleActual();
	
	public Mano getHeroCards();
	
	public List<Carta> getFlop();
	public List<Carta> getTurn(); //devuele el flop incluido, el turn es la pos 3
	public List<Carta> getRiver(); //devuelve todo, size=5, el river esta en la pos 4
	
	//Estos son los botes que habia al principio de cada calle. (ej: preflop solo tiene las ciegas)
	//pero podria ser que la BB no tuviera suficiente stack y no pudiera poner la BB entera.
	public double getPreflopPot();
	public double getFlopPot();
	public double getTurnPot();
	public double getRiverPot();

	//De normal habra solo un ganador, pero cuando se divida el bote puede haber 2 o mas ganadores.
	public List<Winner> getWinners();
	
	//Devuelve una lista con todas las holecards que se han mostrado en el showdown y su mejor mano.
	public List<Showdown> getShowdowns();
	
	//Las acciones de cada jugador. Identificamos a los jugadores por su posicion, en lugar de por su nombre.
	public Map<Integer, Map<Calle, List<Accion>>> getActions();
	
	//Devuelve una lista con todas las acciones que se han producido en la mano, ordenadas en tiempo.
	//Lo mejor es que cada vez que anyados una accion al MAP de acciones, en ese mismo metodo tmb la anyadamos a esta lista.
	public List<Accion> getOrderedActions();
	public List<Accion> getOrderedActionsPreflop();
	public List<Accion> getOrderedActionsFlop();
	public List<Accion> getOrderedActionsTurn();
	public List<Accion> getOrderedActionsRiver();
}
