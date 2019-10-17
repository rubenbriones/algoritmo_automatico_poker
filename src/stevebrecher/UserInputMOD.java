package stevebrecher;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import logica.Carta;
import logica.Mano;

/**
 * Las demas clases de este paquete (libreria de Steve Brecher) estan copiadas tal cual, (salvo los metodos de HandEval que eran 
 * de otras modadlidades diferentes al Texas Holdem y que hemos borrado), pero el codigo esta copiado tal cual sin ningun cambio.
 * Pero esta clase (que tambien existe en su libreria) la hemos modificado/retocado a nuestra conveniencia. Ya que el recogia
 * los datos por pantalla, y nosotros no necesaitamos eso.
 * 
 * @author Ruben
 */
public final class UserInputMOD {

	/*
	 * Cards specified by user, stored in input order so
	 * they can be output in the same order
	 */
	private CardSet[]		holeCards;
	private final CardSet	boardCards = new CardSet();
	private final CardSet	deadCards = new CardSet();

	private int				nUnknown;	// number of players with unknown hole cards

	private double			nPots;	// number of showdowns

	private CardSet			deck;

	
	//NOTA: SOLO PODEMOS PASARLE UN BOARD DE COMO MAXIMO 4 CARTAS, SI LE PASAMOS EL RIVER NO FUNCIONA.
	//De momento solo vamos a pasarle 2 Mano, pero como el codigo esta pensado para que se le puedan pasar mas
	//pues lo ponemos como un array para si en el futuro hace falta esa opcion.
	//Con nUnknowPlayers pasa lo mismo, siempre vamos a pasar 0, pero por si en el futuro nos interesa esa funcion
	//lo dejamos como parametro.
	public UserInputMOD(Mano[] manos, List<Carta> board, List<Carta> dead, int nUnknowPlayers){
		this.deck = CardSet.freshDeck();
		this.boardCards.clear();
		this.deadCards.clear();
		
		String holeCardsSeparadasPorEspacios = "";
		for(Mano m : manos){
			holeCardsSeparadasPorEspacios += m.toString();
		}		
		getHoleCards(holeCardsSeparadasPorEspacios);
		
		this.nUnknown = nUnknowPlayers;
		
		String boardString = "";
		for(Carta c : board){
			boardString += c.toString();
		}				
		getBoard(boardString);
		
		String deadString = "";
		for(Carta c : dead){
			deadString += c.toString();
		}		
		getDeadCards(deadString);
		
		this.nPots = nbrEnumerations();
		if (nPots > Long.MAX_VALUE - 1e9)
			System.err.printf("%,.0f pots required, which is higher than this program can count.%n", nPots);
	}
	
	

	public CardSet[] holeCards() {
		CardSet[] result = new CardSet[holeCards.length];
		for (int i = 0; i < holeCards.length; ++i)
			result[i] = new CardSet(holeCards[i]);
		return result;
	}

	public CardSet boardCards() {
		return new CardSet(boardCards);
	}

	public CardSet deadCards() {
		return new CardSet(deadCards);
	}

	public int nUnknown() {
		return nUnknown;
	}

	public double nPots() {
		return nPots;
	}
	
	public CardSet deck() {
		return new CardSet(deck);
	}

	

	//Hay que pasarles las manos de los jugadores SEPARADAS POR ESPACIOS EN BLANCO.
	//Las hole cards de los jugadores asi: "AdTd 5s6s Th8h"
	//Las board cards y las dead cards asi (SIN ESPACIOS, aunque tambien se le pueden meter): "6s7s8s"
	private boolean getCards(String source, CardSet cardsDest) {		
		Scanner scanner = new Scanner(source);
		Pattern cardsPattern = Pattern.compile("(?:(?:[23456789TJQKA])[cdhs])+", Pattern.CASE_INSENSITIVE);
		
		scanner.useDelimiter(" ");	//whitespace or comma NOTA: he quitado la coma (\\s+|,)
		while (scanner.hasNext()) {
			String cards = scanner.next(cardsPattern);
			int i = 0;
			while (i < cards.length()) {
				char rank = cards.charAt(i++);
				Card card = Card.getInstance(String.format("%c%c", rank, /*suit:*/cards.charAt(i++)));
				if (!deck.remove(card)) {
					System.err.println("There's only one " + card + " in the deck!");
					return false;
				}
				cardsDest.add(card);
			}
		}
		return true;
	}

	private boolean getHoleCards(String holeCardsSeparasPorEspacios) {
		CardSet	holes = new CardSet();

		getCards(holeCardsSeparasPorEspacios, holes);
		
		holeCards = new CardSet[holes.size()/2];
		Iterator<Card> iter = holes.iterator();
		for (int i = 0; i < holeCards.length; ++i) {
			holeCards[i] = new CardSet(2);
			holeCards[i].add(iter.next());
			holeCards[i].add(iter.next());
		}
		return true;
	}

	private boolean getBoard(String boardCardsJuntasString) {
		getCards(boardCardsJuntasString, boardCards);
		
		if (boardCards.size() > 4) {
			System.err.println("Number of board cards cannot exceed 4 (flop and turn).");
			return false;
		}
		return true;
	}

	private boolean getDeadCards(String deadCardsJuntasString) {
		getCards(deadCardsJuntasString, deadCards);
		
		return true;
	}

	/*private boolean getNbrUnknown() {

		String s, prompt;
		int max;

		nUnknown = 0;
		max = (deck.size() - (5 - boardCards.size())/2);
		if (max <= 0)
			return true;
		if (max > 2)
			max = 2;
		prompt = String.format("Number of players with unknown hole cards (0 to %d) [0]: ", max);
		do {
			if ((s = getResponse(prompt, unknownHelp)) == null)
				return false;
			if (s.length() == 0)
				return true;
			nUnknown = 0;
			try {
				nUnknown = Integer.parseInt(s);
			} catch (NumberFormatException e) {
				nUnknown = -1; // force loop continue
			}
		} while (nUnknown < 0 || nUnknown > max);
		return true;
	}*/


	private static double factorial(double n) {
		if (n > 1.0)
			return n * factorial(n - 1.0);
		return 1.0;
	}

	private static double combos(int chooseFrom, int choose) {
		return factorial(chooseFrom) / (factorial(choose) * factorial((chooseFrom - choose)));
	}

	private double nbrEnumerations() {
		double enums;

		enums = combos(deck.size() - 2*nUnknown, 5 - boardCards.size()); // number of boards
		if (nUnknown > 0)
			// *= the number of ORDERED sets of hole cards for the players with unknown cards
			enums *= combos(deck.size(), 2*nUnknown) * factorial(2*nUnknown) / ((1 << nUnknown)/* 2^^nUnknown */);
		return enums;
	}
}
