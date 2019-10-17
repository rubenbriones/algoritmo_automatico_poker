package constantes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logica.Carta;
import logica.Mano;
import logica.RangoConcreto;
import logica.ResJugadas;
import logica.ResJugadasRango;
import logica.Util;

public enum Jugada {
	/** CADA VEZ QUE ANYADA UNA JUGADA NUEVA AGREGARGA AL CORRESPONDIENTE ARRAY SEGUN EL TIPO DE JUGADA **/
	ROYAL_FLUSH(11),
	QUADS(10),
	FULL(9),
	FLUSH(8),
	STRAIGHT(7),
	SET(6),
	TWO_PAIR(5),
	OVERPAIR(4.5),
	TOP_PAIR(4.4),
	PP_BELOW_TP(4.3),
	MIDDLE_PAIR(4.2),
	WEAK_PAIR(4.1),
	//PAIR(4), //esta engloba a todas las pair obviamente
	ACE_HIGH(3),
	KING_HIGH(2),
	QUEEN_HIGH(1),
	NO_MADE_HAND(0),
	
	FLUSHDRAW(0.9),
	OESD(0.8),
	GUTSHOT(0.7),
	OVERCARDS(0.6), //sus dos cartas tienen que ser overcards
	ONE_OVERCARD(0.5), //solo una de las dos cartas tiene que ser overcard
	
	BACKDOOR_FLUSH(0.2);
	//BACKDOOR_OESD(0.1);
	
	
	private double valorAlShowdown;

	private Jugada(double valorAlShowdown) {
		this.valorAlShowdown = valorAlShowdown;
	}

	public double getValorAlShowdown() {
		return valorAlShowdown;
	}
	
	public static Jugada[] getJugadas(){
		return new Jugada[]{ROYAL_FLUSH,QUADS,FULL,FLUSH,STRAIGHT,SET,TWO_PAIR,OVERPAIR,TOP_PAIR,PP_BELOW_TP,MIDDLE_PAIR,WEAK_PAIR,ACE_HIGH,KING_HIGH,QUEEN_HIGH,NO_MADE_HAND};
	}
	
	public static Jugada[] getProyectos(){
		return new Jugada[]{FLUSHDRAW, OESD, GUTSHOT, OVERCARDS, ONE_OVERCARD};
	}
	
	public static Jugada[] getBackdoors(){
		return new Jugada[]{BACKDOOR_FLUSH};
	}

	//en @board le podemos pasar 3,4 o 5 cartas.
	public static ResJugadasRango getJugadasRango(RangoConcreto rango, List<Carta> board){
		ResJugadasRango res = new ResJugadasRango(rango);
		for(Mano m : rango.getManos()){
			switch(board.size()){
				case 3: res.addResJugadas(getJugadasEnFlop(m, board)); break;
				case 4: res.addResJugadas(getJugadasEnTurn(m, board)); break;
				case 5: res.addResJugadas(getJugadasEnRiver(m, board)); break;
			}
		}
		return res;
	}
	
	public static ResJugadas getJugadasEnFlop(Mano mano, List<Carta> flop){
		Collections.sort(flop, Collections.reverseOrder());
		List<Carta> boardMasMano = new ArrayList<Carta>(flop);
		boardMasMano.add(mano.getCarta1());
		boardMasMano.add(mano.getCarta2());
		
		//lo primero ordenamos las cartas de mayor a menor
		Collections.sort(boardMasMano, Collections.reverseOrder());
		
		Jugada jugadaMaxima = extraerJugadaMaxima(mano, boardMasMano, flop);
				
		List<Jugada> proyectos = new ArrayList<Jugada>();
		List<Jugada> backdoors = new ArrayList<Jugada>();		
		extraerProyectosAndBackdoors(jugadaMaxima, mano, boardMasMano, flop, proyectos, backdoors);
		
		return new ResJugadas(mano, boardMasMano, flop, jugadaMaxima, proyectos, backdoors);
	}
	
	public static ResJugadas getJugadasEnTurn(Mano mano, List<Carta> turn){
		Collections.sort(turn, Collections.reverseOrder());
		List<List<Carta>>  combinaciones = Util.generarCombinacionesDe5cartas(mano, turn);
				
		Jugada jugadaMaximaGlobal = NO_MADE_HAND;
		List<Carta> boardMasManoMax = null;
		List<Jugada> proyectosTotales = new ArrayList<Jugada>();
		List<Jugada> backdoorsTotales = new ArrayList<Jugada>();		
		
		for(List<Carta> boardMasMano : combinaciones){
			Collections.sort(boardMasMano, Collections.reverseOrder());
			Jugada jugadaMaxima = extraerJugadaMaxima(mano, boardMasMano, turn);
			List<Jugada> proyectos = new ArrayList<Jugada>();
			List<Jugada> backdoors = new ArrayList<Jugada>();		
			extraerProyectosAndBackdoors(jugadaMaxima, mano, boardMasMano, turn, proyectos, backdoors);
			proyectosTotales.addAll(proyectos);
			backdoorsTotales.addAll(backdoors);
			
			if(jugadaMaxima.getValorAlShowdown() > jugadaMaximaGlobal.getValorAlShowdown()){
				boardMasManoMax = boardMasMano;
				jugadaMaximaGlobal = jugadaMaxima;
			}
		}
				
		return new ResJugadas(mano, boardMasManoMax, turn, jugadaMaximaGlobal, proyectosTotales, backdoorsTotales);
	}
	
	public static ResJugadas getJugadasEnRiver(Mano mano, List<Carta> river){
		Collections.sort(river, Collections.reverseOrder());
		List<List<Carta>>  combinaciones = Util.generarCombinacionesDe5cartas(mano, river);
				
		Jugada jugadaMaximaGlobal = NO_MADE_HAND;
		List<Carta> boardMasManoMax = null;
		
		for(List<Carta> boardMasMano : combinaciones){
			Collections.sort(boardMasMano, Collections.reverseOrder());
			Jugada jugadaMaxima = extraerJugadaMaxima(mano, boardMasMano, river);
			if(jugadaMaxima.getValorAlShowdown() > jugadaMaximaGlobal.getValorAlShowdown()){
				boardMasManoMax = boardMasMano;
				jugadaMaximaGlobal = jugadaMaxima;
			}
		}
				
		return new ResJugadas(mano, boardMasManoMax, river, jugadaMaximaGlobal, null, null);
	}
	
	/**
	 * NOTA: HAY QUE PASARLE EL @boardMasMano y el @board YA ORDENADO, CON LAS CARTAS DE MAYOR A MENOR SEGUN SU NUMERO.
	 * 
	 * Devuelve la jugada maxima dadas:
	 * @mano es la mano que analizamos, luego en el boardMasMano puede que solo utilicemos una carta de las dos, eso es para los pokers/fulles/colores/escaleras/sets/dobles
	 * 		 pero para detectar todo los tipos de parejas utilizamos siempre las dos cartas de Mano. Ya que puede que para hacer una escalera o color solo necesite una de las
	 * 		 dos pero luego tiene una overpair con ellas 2, o una middle pair con la segunda, etc.
	 * @boardMasMano las hero cartas utilizadas mas las cartas del board utilizadas, SIEMPRE HABRA 5 CARTAS AQUI METIDAS.
	 * @board le pasamos las cartas del board, no solo las que se utilizan para conformar nuestra "mano de 5 cartas", sino todas (pueden ser 3,4, o 5)
	 */
	private static Jugada extraerJugadaMaxima(Mano mano, List<Carta> boardMasMano, List<Carta> board){		
		//Sacamos el valor de la top pair, middle pair, y weak pair del board (teniendo en cuenta que puede estar doblado el board)
		int[] tops = new int[5]; //puede que en en flop solo hay un top (si salen 3 cartas iguales), pero daria igual pues los otros tops los pondria a 0 y nunca se cumpliria mano.get(0)=top[1]
		int cont=0;
		tops[cont++] = board.get(0).getNumero();
		for(int i=1; i<board.size(); i++){
			if(board.get(i).getNumero() < tops[cont-1]){
				tops[cont++]=board.get(i).getNumero();
			}
		}
		
		/** NO METER LINEAS DE CODIGO NORMAL (aignaciones, calculos, etc) ENTRE LOS IF-ELSE PUES AL ESTAR SIN PARENTESIS SE CARAGRIA TODO **/
		//royal flush
		if((boardMasMano.get(0).getPalo() == boardMasMano.get(1).getPalo() &&
			boardMasMano.get(1).getPalo() == boardMasMano.get(2).getPalo() &&
			boardMasMano.get(2).getPalo() == boardMasMano.get(3).getPalo() &&
			boardMasMano.get(3).getPalo() == boardMasMano.get(4).getPalo())
			&&
		  ((boardMasMano.get(0).getNumero() == boardMasMano.get(1).getNumero()+1 &&
			boardMasMano.get(1).getNumero() == boardMasMano.get(2).getNumero()+1 &&
			boardMasMano.get(2).getNumero() == boardMasMano.get(3).getNumero()+1 &&
			boardMasMano.get(3).getNumero() == boardMasMano.get(4).getNumero()+1) ||
		   (boardMasMano.get(0).getNumero() == 14 && boardMasMano.get(1).getNumero() == 5 &&
			boardMasMano.get(1).getNumero() == boardMasMano.get(2).getNumero()+1 &&
			boardMasMano.get(2).getNumero() == boardMasMano.get(3).getNumero()+1 &&
			boardMasMano.get(3).getNumero() == boardMasMano.get(4).getNumero()+1))) return ROYAL_FLUSH;
		
		//quads
		else if((boardMasMano.get(0).getNumero() == boardMasMano.get(1).getNumero() &&
			boardMasMano.get(1).getNumero() == boardMasMano.get(2).getNumero() &&
			boardMasMano.get(2).getNumero() == boardMasMano.get(3).getNumero()) ||
		   (boardMasMano.get(1).getNumero() == boardMasMano.get(2).getNumero() &&
			boardMasMano.get(2).getNumero() == boardMasMano.get(3).getNumero() &&
			boardMasMano.get(3).getNumero() == boardMasMano.get(4).getNumero())) return QUADS;
		
		//full
		else if((boardMasMano.get(0).getNumero() == boardMasMano.get(1).getNumero() &&
			boardMasMano.get(1).getNumero() == boardMasMano.get(2).getNumero() &&
			boardMasMano.get(3).getNumero() == boardMasMano.get(4).getNumero()) ||
		   (boardMasMano.get(0).getNumero() == boardMasMano.get(1).getNumero() &&
			boardMasMano.get(2).getNumero() == boardMasMano.get(3).getNumero() &&
			boardMasMano.get(3).getNumero() == boardMasMano.get(4).getNumero())) return FULL;
		
		//flush
		else if(boardMasMano.get(0).getPalo() == boardMasMano.get(1).getPalo() &&
			boardMasMano.get(1).getPalo() == boardMasMano.get(2).getPalo() &&
			boardMasMano.get(2).getPalo() == boardMasMano.get(3).getPalo() &&
			boardMasMano.get(3).getPalo() == boardMasMano.get(4).getPalo()) return FLUSH;
		
		//straight
		else if((boardMasMano.get(0).getNumero() == boardMasMano.get(1).getNumero()+1 &&
			boardMasMano.get(1).getNumero() == boardMasMano.get(2).getNumero()+1 &&
			boardMasMano.get(2).getNumero() == boardMasMano.get(3).getNumero()+1 &&
			boardMasMano.get(3).getNumero() == boardMasMano.get(4).getNumero()+1) ||
			(boardMasMano.get(0).getNumero() == 14 && boardMasMano.get(1).getNumero() == 5 &&
			boardMasMano.get(1).getNumero() == boardMasMano.get(2).getNumero()+1 &&
			boardMasMano.get(2).getNumero() == boardMasMano.get(3).getNumero()+1 &&
			boardMasMano.get(3).getNumero() == boardMasMano.get(4).getNumero()+1)) return STRAIGHT;
		
		//set
		else if((boardMasMano.get(0).getNumero() == boardMasMano.get(1).getNumero() &&
			boardMasMano.get(1).getNumero() == boardMasMano.get(2).getNumero()) ||
		   (boardMasMano.get(1).getNumero() == boardMasMano.get(2).getNumero() &&
			boardMasMano.get(2).getNumero() == boardMasMano.get(3).getNumero()) ||
		   (boardMasMano.get(2).getNumero() == boardMasMano.get(3).getNumero() &&
			boardMasMano.get(3).getNumero() == boardMasMano.get(4).getNumero())) return SET;
		
		//two pair
		else if((boardMasMano.get(0).getNumero() == boardMasMano.get(1).getNumero() &&
			boardMasMano.get(2).getNumero() == boardMasMano.get(3).getNumero()) ||
		   (boardMasMano.get(0).getNumero() == boardMasMano.get(1).getNumero() &&
			boardMasMano.get(3).getNumero() == boardMasMano.get(4).getNumero()) ||
		   (boardMasMano.get(1).getNumero() == boardMasMano.get(2).getNumero() &&
			boardMasMano.get(3).getNumero() == boardMasMano.get(4).getNumero())) return TWO_PAIR;
		
		//overpair
		else if(mano.isPocket() && 
		   mano.getCarta1().getNumero() > tops[0]) return OVERPAIR;
		
		//top pair
		else if(mano.getCarta1().getNumero() == tops[0] || 
			mano.getCarta2().getNumero() == tops[0]) return TOP_PAIR;
		
		//pp below tp
		else if(mano.isPocket() && 
		   mano.getCarta1().getNumero() < tops[0] && mano.getCarta1().getNumero() > tops[1]) return PP_BELOW_TP;
				
		//middle pair
		else if(mano.getCarta1().getNumero() == tops[1] || 
		   mano.getCarta2().getNumero() == tops[1]) return MIDDLE_PAIR;

		//weak pair
		else if(mano.getCarta1().getNumero() == tops[2] ||
				mano.getCarta1().getNumero() == tops[3] ||
				mano.getCarta1().getNumero() == tops[4] ||
				mano.getCarta2().getNumero() == tops[2] ||
				mano.getCarta2().getNumero() == tops[3] ||
				mano.getCarta2().getNumero() == tops[4]) return WEAK_PAIR;

		//ace high
		else if(mano.getCartaAlta().getNumero() == 14 && 
				mano.getCartaAlta().getNumero() > board.get(0).getNumero() &&
				!hayJugadaDe5CartasEnMesa(board)) return ACE_HIGH;

		//king high
		/*else if(mano.getCartaAlta().getNumero() == 13 && 
				mano.getCartaAlta().getNumero() > board.get(0).getNumero() &&
				!hayJugadaDe5CartasEnMesa(board)) return KING_HIGH;

		//queen high
		else if(mano.getCartaAlta().getNumero() == 12 && 
				mano.getCartaAlta().getNumero() > board.get(0).getNumero() &&
				!hayJugadaDe5CartasEnMesa(board)) return QUEEN_HIGH;*/
		
		//no made hand
		return NO_MADE_HAND;
	}
	
	/**
	 * NOTA: HAY QUE PASARLE EL @boardMasMano y el @board YA ORDENADO, CON LAS CARTAS DE MAYOR A MENOR SEGUN SU NUMERO.
	 * 
	 * Inserta los proyectos y los backdoors que haya en las dos listas que le pasamos coo parametro. Y necesita los siguenets argumentos:
	 * @jugadaMaxima es la Jugada maxima que tiene @mano en ese @board, la jugada maxima que hay en @boardMasMano vamos. Es para que si ya tiene full, no vamos a decir
	 * 				 que tiene tambien proyecto de color o escalera, pues no le serviria de nada, no tendria sentido.
	 * @mano es la mano que analizamos, luego en el boardMasMano puede que solo utilicemos una carta de las dos, eso es para los pokers/fulles/colores/escaleras/sets/dobles
	 * 		 pero para detectar todo los tipos de parejas utilizamos siempre las dos cartas de Mano. Ya que puede que para hacer una escalera o color solo necesite una de las
	 * 		 dos pero luego tiene una overpair con ellas 2, o una middle pair con la segunda, etc.
	 * @boardMasMano las hero cartas utilizadas mas las cartas del board utilizadas, SIEMPRE HABRA 5 CARTAS AQUI METIDAS.
	 * @board le pasamos las cartas del board, no solo las que se utilizan para conformar nuestra "mano de 5 cartas", sino todas (pueden ser 3,4, o 5)
	 */
	private static void extraerProyectosAndBackdoors(Jugada jugadaMaxima, Mano mano, List<Carta> boardMasMano, List<Carta> board,
			List<Jugada> proyectos, List<Jugada> backdoors){
		//PROYECTOS Y BACKDOORS
		//flushdraw
		int maxCartasDelPalo = maxCartasDeUnPalo(boardMasMano);
		if(jugadaMaxima.getValorAlShowdown() < Jugada.FLUSH.getValorAlShowdown())
			if(maxCartasDelPalo == 4) proyectos.add(FLUSHDRAW);
			else if(board.size()==3 && maxCartasDelPalo == 3) backdoors.add(BACKDOOR_FLUSH);
		
		//oesd
		if(jugadaMaxima.getValorAlShowdown() < Jugada.STRAIGHT.getValorAlShowdown())
			//si hay 3 cartas iguales en la mesa ya no puede haber ni OESD, ni GUTSHOT, pero SI que puede haber backdoor OESD
			if(jugadaMaxima.getValorAlShowdown() < Jugada.SET.getValorAlShowdown() &&
			   hay4cartasSeguidas(boardMasMano)) proyectos.add(OESD);
			else 
				//gutshot
				if(jugadaMaxima.getValorAlShowdown() < Jugada.SET.getValorAlShowdown() &&
					(boardMasMano.get(0).getNumero() == 14 && boardMasMano.get(1).getNumero() == 13 &&
					boardMasMano.get(0).getNumero() == 12 && boardMasMano.get(1).getNumero() == 11) ||
					(boardMasMano.get(4).getNumero() == 14 && boardMasMano.get(3).getNumero() == 2 &&
					boardMasMano.get(2).getNumero() == 3 && boardMasMano.get(1).getNumero() == 4) ||
					hay3cartasSeguidasY1alHueco(boardMasMano)) proyectos.add(GUTSHOT);
				/*else
				   //aqui tengo que poner lo de backdoor oesd, 
				   //tendria que poner como condicion board.size()==3 ya que solo hay backdoors en el flop */
			
		//overcards
		if(mano.getCarta1().getNumero() > board.get(0).getNumero() &&
		   mano.getCarta2().getNumero() > board.get(0).getNumero()) proyectos.add(OVERCARDS);
		/*else if(mano.getCarta1().getNumero() > board.get(0).getNumero() ||
				mano.getCarta2().getNumero() > board.get(0).getNumero()) proyectos.add(ONE_OVERCARD);*/
	}
	
	
	//hay que pasarle una lista de 5 cartas
	private static int maxCartasDeUnPalo(List<Carta> cartas){
		int[] palos = new int[4];
		int max = 0;
		for(Carta c : cartas){
			int posPalo = -1;
			switch(c.getPalo()){
				case CORAZONES: posPalo = 0; break;
				case DIAMANTES: posPalo = 1; break;
				case TREBOLES: posPalo = 2; break;
				case PICAS: posPalo = 3; break;
			}
			palos[posPalo]++; 
			if(palos[posPalo] > max) max = palos[posPalo];
		}
		return max;
	}
	
	private static boolean hayJugadaDe5CartasEnMesa(List<Carta> board){
		if(board.size() < 5) return false;
		if((board.get(0).getNumero() == board.get(1).getNumero() &&
			board.get(1).getNumero() == board.get(2).getNumero() &&
			board.get(3).getNumero() == board.get(4).getNumero()) ||
		   (board.get(0).getNumero() == board.get(1).getNumero() &&
			board.get(2).getNumero() == board.get(3).getNumero() &&
			board.get(3).getNumero() == board.get(4).getNumero())) return true;
		
		//flush
		if(board.get(0).getPalo() == board.get(1).getPalo() &&
			board.get(1).getPalo() == board.get(2).getPalo() &&
			board.get(2).getPalo() == board.get(3).getPalo() &&
			board.get(3).getPalo() == board.get(4).getPalo()) return true;
		
		//straight
		if((board.get(0).getNumero() == board.get(1).getNumero()+1 &&
			board.get(1).getNumero() == board.get(2).getNumero()+1 &&
			board.get(2).getNumero() == board.get(3).getNumero()+1 &&
			board.get(3).getNumero() == board.get(4).getNumero()+1) ||
		   (board.get(0).getNumero() == 14 && board.get(1).getNumero() == 5 &&
			board.get(1).getNumero() == board.get(2).getNumero()+1 &&
			board.get(2).getNumero() == board.get(3).getNumero()+1 &&
			board.get(3).getNumero() == board.get(4).getNumero()+1)) return true;
		
		return false;			
	}
	
	//hay que pasarle una lista de 5 cartas
	//Es para comprobar OESD, ya que los boards en los que hay cartas dobladas complican todo
	//No pueden haber 3 cartas iguals, pues los boards en los que hay SET ya los hemos descartado.
	//pero si que puede haber iguales a pares, dobles parejas.
	private static boolean hay4cartasSeguidas(List<Carta> cartas){
		List<Carta> cartasSinRep = new ArrayList<Carta>(cartas.size());
		cartasSinRep.add(cartas.get(0));
		int cont = 0;
		for(int i=1; i<cartas.size(); i++){
			if(cartasSinRep.get(cont).getNumero() != cartas.get(i).getNumero()){
				cartasSinRep.add(cartas.get(i));
				cont++;
			}
		}
		
		//si hay dobels parejas no puede haber OESD
		if(cartasSinRep.size() <= 3) return false;
		
		//comprobaciones cuando 4 o 5 cartas sin repetir
		if(cartasSinRep.get(0).getNumero() == cartasSinRep.get(1).getNumero()+1 &&
			cartasSinRep.get(1).getNumero() == cartasSinRep.get(2).getNumero()+1 &&
			cartasSinRep.get(2).getNumero() == cartasSinRep.get(3).getNumero()+1) return true;
		
		if(cartasSinRep.size() < 5) return false;
		
		//comprobaciones que hay que hacer solo cuando son 5 cartas, no hay ninguna repetida
		if(cartasSinRep.get(1).getNumero() == cartasSinRep.get(2).getNumero()+1 &&
			cartasSinRep.get(2).getNumero() == cartasSinRep.get(3).getNumero()+1 &&
			cartasSinRep.get(3).getNumero() == cartasSinRep.get(4).getNumero()+1) return true;

		return false;
	}
		
	//hay que pasarle una lista de 5 cartas
	//Es para comprobar las gutshots, ya que los boards en los que hay cartas dobladas complican todo
	//No pueden haber 3 cartas iguals, pues los boards en los que hay SET ya los hemos descartado.
	//pero si que puede haber iguales a pares, dobles parejas.
	private static boolean hay3cartasSeguidasY1alHueco(List<Carta> cartas){
		List<Carta> cartasSinRep = new ArrayList<Carta>(cartas.size());
		cartasSinRep.add(cartas.get(0));
		int cont = 0;
		for(int i=1; i<cartas.size(); i++){
			if(cartasSinRep.get(cont).getNumero() != cartas.get(i).getNumero()){
				cartasSinRep.add(cartas.get(i));
				cont++;
			}
		}
		
		//si hay dobels parejas no puede haber Gutshot
		if(cartasSinRep.size() <= 3) return false;
		
		//comprobaciones comunes tanto a una lista de 4 como de 5 cartas
		if(cartasSinRep.get(0).getNumero() == cartasSinRep.get(1).getNumero()+2 &&
			cartasSinRep.get(1).getNumero() == cartasSinRep.get(2).getNumero()+1 &&
			cartasSinRep.get(2).getNumero() == cartasSinRep.get(3).getNumero()+1) return true;
		if(cartasSinRep.get(0).getNumero() == cartasSinRep.get(1).getNumero()+1 &&
			cartasSinRep.get(1).getNumero() == cartasSinRep.get(2).getNumero()+2 &&
			cartasSinRep.get(2).getNumero() == cartasSinRep.get(3).getNumero()+1) return true;
		if(cartasSinRep.get(0).getNumero() == cartasSinRep.get(1).getNumero()+1 &&
			cartasSinRep.get(1).getNumero() == cartasSinRep.get(2).getNumero()+1 &&
			cartasSinRep.get(2).getNumero() == cartasSinRep.get(3).getNumero()+2) return true;
		
		if(cartasSinRep.size() < 5) return false;
		
		//comprobaciones que hay que hacer solo cuando son 5 cartas
		if(cartasSinRep.get(1).getNumero() == cartasSinRep.get(2).getNumero()+2 &&
			cartasSinRep.get(2).getNumero() == cartasSinRep.get(3).getNumero()+1 &&
			cartasSinRep.get(3).getNumero() == cartasSinRep.get(4).getNumero()+1) return true;
		if(cartasSinRep.get(1).getNumero() == cartasSinRep.get(2).getNumero()+1 &&
			cartasSinRep.get(2).getNumero() == cartasSinRep.get(3).getNumero()+2 &&
			cartasSinRep.get(3).getNumero() == cartasSinRep.get(4).getNumero()+1) return true;
		if(cartasSinRep.get(0).getNumero() == cartasSinRep.get(1).getNumero()+1 &&
			cartasSinRep.get(1).getNumero() == cartasSinRep.get(2).getNumero()+1 &&
			cartasSinRep.get(2).getNumero() == cartasSinRep.get(3).getNumero()+2) return true;

		return false;
	}
}
