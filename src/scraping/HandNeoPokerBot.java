package scraping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import logica.Accion;
import logica.Carta;
import logica.Hand;
import logica.IHand;
import logica.Mano;
import logica.Showdown;
import logica.Winner;
import constantes.AccionEnum;
import constantes.Calle;

public class HandNeoPokerBot extends Hand {

	private String history;
	private long handNumber;
	private int numberOfPlayers;
	private String heroName;
	private int[] turnosPorCalles;
	private double SB, BB;
	private Map<String, Integer> positions; //la string es el nombre del jugador
	private List<Integer> alivePlayers;
	private Map<String, Double> stacksPreflopAux; //se usara solo momentanemamente mientras averiguamos las posiciones de cada jugador
	private Map<Integer, Double> stacksPreflop, stacksFlop, stacksTurn, stacksRiver;
	private Calle calleActual;
	private Mano heroCards;
	private List<Carta> flop, turn, river;
	private double preflopPot, flopPot, turnPot, riverPot;
	private List<Winner> winners;
	private List<Showdown> showdowns;
	private Map<Integer, Map<Calle, List<Accion>>> actions; //<player, <Calle(int), List<Accion>>
	private List<Accion> orderedActions, orderedActionsPreflop, orderedActionsFlop, orderedActionsTurn, orderedActionsRiver;
	
	public HandNeoPokerBot(){
		this.turnosPorCalles = new int[5]; //0-blinds, 1-preflop, 2-flop, 3-turn, 4-river
		this.positions = new HashMap<String, Integer>(10);
		this.alivePlayers = new ArrayList<Integer>(10);
		this.stacksPreflopAux = new HashMap<String, Double>(10);
		this.stacksPreflop = new HashMap<Integer, Double>(10);
		this.winners = new ArrayList<Winner>(10);
		this.showdowns = new ArrayList<Showdown>(10);
		this.actions = new HashMap<Integer, Map<Calle, List<Accion>>>(10);
		this.orderedActions = new ArrayList<Accion>();
		this.orderedActionsPreflop = new ArrayList<Accion>();
		this.orderedActionsFlop = new ArrayList<Accion>();
		this.orderedActionsTurn = new ArrayList<Accion>();
		this.orderedActionsRiver = new ArrayList<Accion>();
	}
	
	public void setHistory(String history) {
		this.history = history;
	}

	public void setHandNumber(long handNumber) {
		this.handNumber = handNumber;
	}
	
	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public void setHeroName(String heroName) {
		this.heroName = heroName;
	}
	
	public void sumarTurnoCalleActual(){
		switch(getCalleActual()){
			case BLINDS: turnosPorCalles[0]++; break;
			case PREFLOP: turnosPorCalles[1]++; break;
			case FLOP: turnosPorCalles[2]++; break;
			case TURN: turnosPorCalles[3]++; break;
			case RIVER: turnosPorCalles[4]++; break;	
		}
	}

	public void setSB(double sB) {
		SB = sB;
	}

	public void setBB(double bB) {
		BB = bB;
	}

	public void addPosition(String player, int position) {
		this.positions.put(player, new Integer(position));
		stacksPreflop.put(position, stacksPreflopAux.get(player));
		alivePlayers.add(position);
	}

	public void addStackPreflopAux(String player, double stack) {
		this.stacksPreflopAux.put(player, stack);
	}

	public void setCalleActual(Calle calleActual) {
		this.calleActual = calleActual;
	}
	
	public void setHeroCards(Mano heroCards) {
		this.heroCards = heroCards;
	}

	public void setFlop(List<Carta> flop) {
		this.flop = flop;
	}

	public void setTurn(Carta turn) {
		this.turn = new ArrayList<Carta>(4);
		this.turn.addAll(flop);
		this.turn.add(turn);
	}

	public void setRiver(Carta river) {
		this.river = new ArrayList<Carta>(5);
		this.river.addAll(turn);
		this.river.add(river);
	}

	public void setPreflopPot(double preflopPot) {
		this.preflopPot = preflopPot;
	}

	public void setFlopPot(double flopPot) {
		this.flopPot = flopPot;
	}

	public void setTurnPot(double turnPot) {
		this.turnPot = turnPot;
	}

	public void setRiverPot(double riverPot) {
		this.riverPot = riverPot;
	}

	public void addWinner(Winner winner) {
		this.winners.add(winner);
	}
	
	public void addShowdown(Showdown showdown) {
		this.showdowns.add(showdown);
	}
	
	public void addAction(Integer posPlayer, Calle calle, Accion accion){
		this.orderedActions.add(accion); //metemos la accion siempre en la lista de acciones ordenadas
		switch(calle){
			case PREFLOP: orderedActionsPreflop.add(accion); break;
			case FLOP: orderedActionsFlop.add(accion); break;
			case TURN: orderedActionsTurn.add(accion); break;
			case RIVER: orderedActionsRiver.add(accion); break;
			default: break;
		}
		
		Map<Calle, List<Accion>> accionesPlayer = this.actions.get(posPlayer);
		if(accionesPlayer == null){
			accionesPlayer = new HashMap<Calle, List<Accion>>(5);
			this.actions.put(posPlayer, accionesPlayer);
		}
		List<Accion> accionesCalleActual = accionesPlayer.get(calle);
		if(accionesCalleActual == null){
			accionesCalleActual = new ArrayList<Accion>();
			accionesPlayer.put(calle, accionesCalleActual);
		}
		accionesCalleActual.add(accion);
		
		//comprobamos si es un FOLD para quitarlo de los alivePlayers
		//Buscamos el INteger que representa su posicion, no es que le pasemos el indice a borrar.
		if(accion.getAccion() == AccionEnum.FOLD) alivePlayers.remove(getPosition(accion.getPlayer()));
	}

	
	@Override
	public String getHistory() {
		return history;
	}

	@Override
	public long getHandNumber() {
		return handNumber;
	}
	
	@Override
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	@Override
	public String getHeroName() {
		return heroName;
	}
	
	@Override
	public int getHeroPosition() {
		return positions.get(getHeroName());
	}
	
	@Override
	public List<Integer> getPositionsAlivePlayers(){
		return alivePlayers;
	}	

	@Override
	public int getNumTurnoCalleActual() {
		switch(getCalleActual()){
			case BLINDS: return turnosPorCalles[0];
			case PREFLOP: return turnosPorCalles[1];
			case FLOP: return turnosPorCalles[2];
			case TURN: return turnosPorCalles[3];
			case RIVER: return turnosPorCalles[4];		
		}
		return 0;
	}

	@Override
	public double getSB() {
		return SB;
	}

	@Override
	public double getBB() {
		return BB;
	}

	@Override
	public Map<String, Integer> getPositions() {
		return positions;
	}

	@Override
	public Integer getPosition(String player) {
		return positions.get(player);
	}

	@Override
	public Map<Integer, Double> getStacksPreflop() {
		return stacksPreflop;
	}

	@Override
	public Map<Integer, Double> getStacksFlop() {
		if(stacksFlop == null){
			this.stacksFlop = new HashMap<Integer, Double>(10);
			
			Iterator players = getStacksPreflop().keySet().iterator();
			while(players.hasNext()){
				int pos = (int) players.next();
				double stackPreflop = getStacksPreflop().get(pos); //que no es el mismo que el stack pre-blind (aunque para esto nos da igual)
				
				List<Accion> listPreflop = actions.get(pos).get(Calle.PREFLOP);
				if(listPreflop != null && listPreflop.get(listPreflop.size()-1).getAccion() != AccionEnum.FOLD){
					for(Accion a : listPreflop) stackPreflop -= a.getCantidad();
					
					this.stacksFlop.put(pos, stackPreflop);
				}
			}
		}
		return stacksFlop;
	}

	@Override
	public Map<Integer, Double> getStacksTurn() {
		if(stacksTurn == null){
			this.stacksTurn = new HashMap<Integer, Double>(10);
			
			Iterator players = getStacksFlop().keySet().iterator();
			while(players.hasNext()){
				int pos = (int) players.next();
				double stackFlop = getStacksFlop().get(pos);
				
				List<Accion> listFlop = actions.get(pos).get(Calle.FLOP);
				if(listFlop != null && listFlop.get(listFlop.size()-1).getAccion() != AccionEnum.FOLD){
					for(Accion a : listFlop) stackFlop -= a.getCantidad();
					
					this.stacksTurn.put(pos, stackFlop);
				}
			}
		}
		return stacksTurn;
	}

	@Override
	public Map<Integer, Double> getStacksRiver() {
		if(stacksRiver == null){
			this.stacksRiver = new HashMap<Integer, Double>(10);
			
			Iterator players = getStacksTurn().keySet().iterator();
			while(players.hasNext()){
				int pos = (int) players.next();
				double stackTurn = getStacksTurn().get(pos);
				
				List<Accion> listTurn = actions.get(pos).get(Calle.TURN);
				if(listTurn != null && listTurn.get(listTurn.size()-1).getAccion() != AccionEnum.FOLD){
					for(Accion a : listTurn) stackTurn -= a.getCantidad();
					
					this.stacksRiver.put(pos, stackTurn);
				}
			}
		}
		return stacksRiver;
	}

	@Override
	public double getStackEfectivo(Calle calle) {
		Map<Integer, Double> stacks = null;
		if(calle == Calle.PREFLOP) stacks = getStacksPreflop();
		else if(calle == Calle.FLOP) stacks = getStacksFlop();
		else if(calle == Calle.TURN) stacks = getStacksTurn();
		else if(calle == Calle.RIVER) stacks = getStacksRiver();
		
		//Vemos si la ultima accion de un jugador en la calle correspondiente ha sido FOLD, 
		//para entonces borrarlo de la lista de stacks ya que no habria q tenerlo en cuenta para el stack efectivo.
		Iterator<String> players = getPositions().keySet().iterator();
		while(players.hasNext()){
			String p = players.next();
			Integer position = getPosition(p);
			List<Accion> acciones = this.actions.get(position).get(calle);
			if(acciones.get(acciones.size()-1).getAccion() == AccionEnum.FOLD)
				stacks.remove(position);
		}
		
		double heroStack = stacks.get(getHeroName());
		stacks.remove(getHeroName());
		List<Double> lista = new ArrayList<Double>(stacks.values());
		Collections.sort(lista); //ordena de menor a mayor
		double stackMayorSinHero = lista.get(lista.size()-1);
		
		return heroStack < stackMayorSinHero ? heroStack : stackMayorSinHero;
	}

	@Override
	public Calle getCalleActual() {
		return calleActual;
	}
	
	@Override
	public Mano getHeroCards() {
		return heroCards;
	}

	@Override
	public List<Carta> getFlop() {
		return flop;
	}

	@Override
	public List<Carta> getTurn() {
		return turn;
	}

	@Override
	public List<Carta> getRiver() {
		return river;
	}

	@Override
	public double getPreflopPot() {
		return preflopPot;
	}

	@Override
	public double getFlopPot() {
		return flopPot;
	}

	@Override
	public double getTurnPot() {
		return turnPot;
	}

	@Override
	public double getRiverPot() {
		return riverPot;
	}

	@Override
	public List<Winner> getWinners() {
		return winners;
	}
	
	@Override
	public List<Showdown> getShowdowns() {
		return showdowns;
	}

	@Override
	public Map<Integer, Map<Calle, List<Accion>>> getActions() {
		return actions;
	}
	
	@Override
	public List<Accion> getOrderedActions() {
		return orderedActions;
	}

	@Override
	public List<Accion> getOrderedActionsPreflop() {
		return orderedActionsPreflop;
	}

	@Override
	public List<Accion> getOrderedActionsFlop() {
		return orderedActionsFlop;
	}

	@Override
	public List<Accion> getOrderedActionsTurn() {
		return orderedActionsTurn;
	}

	@Override
	public List<Accion> getOrderedActionsRiver() {
		return orderedActionsRiver;
	}
}
