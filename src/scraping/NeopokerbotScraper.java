package scraping;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import logica.Accion;
import logica.Carta;
import logica.Hand;
import logica.IHand;
import logica.Mano;
import logica.Showdown;
import logica.Winner;
import constantes.AccionEnum;
import constantes.Calle;
import constantes.Posicion;

/**
 * https://neopokerbot.com/play
 * 
 * Ponemos la web de Neopokerbot de la siguiente manera en la pantalla:
 * https://gyazo.com/0341c5545488bc9902e301761c065f0b
 * 
 * NOTE: Hay que activar el "full dealer chat"
 */
public class NeopokerbotScraper implements IScraper{
	//Para clickar encima del chat.
	private static final int clickX = 250;
	private static final int clickY = 650;
	//Para sacar captura y ver si nos toca actuar o no.
	private static final int posComprobarTurnoX = 445;
	private static final int posComprobarTurnoY = 575;
	private static final int tamanyoCaptura = 10;
	//Tiempo de espera hasta volver a comprobar si hay info nueva en el chat / es nuestro turno de nuevo
	private static final long tiempoEspera = 2000;
	
	
	private List<Hand> hands;
	
	private HandNeoPokerBot handActual;
	private String ultimaRecopilacion="";
	private String infoNueva="";
	private int posFinalDeUltimaHandEnRecopilacion=0;
	
	/** PATTERNS **/
	Pattern pIni = Pattern.compile("Dealing hand #(\\d+)\\Z");
	Pattern pBU = Pattern.compile("(.*) is the button");
	Pattern pSB = Pattern.compile("(.*) posts small blind, \\$(\\d+)\\Z");
	Pattern pBB = Pattern.compile("(.*) posts big blind, \\$(\\d+)\\Z");
	Pattern pStack = Pattern.compile("(.*) has \\$(\\d+)\\Z");
	Pattern pPreflop = Pattern.compile("Preflop:    \\(pot \\$(\\d+)\\)");
	Pattern pDealt = Pattern.compile("Dealt to (.*): \\((.*),(.*)\\)");
	Pattern pFlop = Pattern.compile("Flop: \\((.*),(.*),(.*)\\) \\(pot \\$(\\d+)\\)");
	Pattern pTurn = Pattern.compile("Turn: \\((.*),(.*),(.*)\\)\\((.*)\\) \\(pot \\$(\\d+)\\)");
	Pattern pRiver = Pattern.compile("River: \\((.*),(.*),(.*)\\)\\((.*)\\)\\((.*)\\) \\(pot \\$(\\d+)\\)");
	Pattern pShows = Pattern.compile("(.*) shows cards: \\((.*),(.*)\\)");
	Pattern pBestHand = Pattern.compile("Best hand: \\((.*),(.*),(.*),(.*),(.*)\\), (.*)\\Z");
	Pattern pWins = Pattern.compile("(.*) wins \\$(\\d+)\\Z");
	Pattern pFold = Pattern.compile("(.*) folds");
	Pattern pCheck = Pattern.compile("(.*) checks");
	Pattern pCall = Pattern.compile("(.*) calls \\$(\\d+)\\Z");
	Pattern pBet = Pattern.compile("(.*) bets \\$(\\d+)\\Z");
	Pattern pAllin = Pattern.compile("(.*) is all-in \\$(\\d+)\\Z");
	Pattern pRaise = Pattern.compile("(.*) raises \\$(\\d+)\\Z"); //la cantidad que pone es lo que mete de EXTRA
	Pattern[] patternsAcciones = {pFold, pCheck, pCall, pBet, pAllin, pRaise}; //NO CAMBIAR EL ORDEN.
	/** **/
	
	public NeopokerbotScraper() {
		this.hands = new ArrayList<Hand>();
	}
	
	@Override
	public boolean esNuestroTurnoConNuevaInfo() {
		Rectangle rect = new Rectangle(posComprobarTurnoX,posComprobarTurnoY,tamanyoCaptura,tamanyoCaptura);
		BufferedImage img = UtilScraping.captureScreen(rect);
		
		if(isCleanImage(img)) return false; //NO es nuestro turno
		
		boolean hayInfoNueva = recopilarChat();
		
		return hayInfoNueva;
	}

	@Override
	public long getTiempoDeEspera() {
		return tiempoEspera;
	}

	@Override
	//Si se llama a este metodo es que YA HAY INFO NUEVA RECOPILADA EN LA VARIABLE infoNueva
	public Hand parseInfoAndGetHandActual() {
		String[] lineas = infoNueva.split("\n");
		for(int i=0; i<lineas.length; i++){
			if(lineas[i].length()==0){ //Linea en blanco, significa el final de la mano
				handActual.setCalleActual(Calle.BLINDS);
				
				int posFinalHand = ultimaRecopilacion.indexOf("\n\n", posFinalDeUltimaHandEnRecopilacion);
				handActual.setHistory(ultimaRecopilacion.substring(posFinalDeUltimaHandEnRecopilacion, posFinalHand));
				posFinalDeUltimaHandEnRecopilacion = posFinalHand;
				
				hands.add(handActual);
				handActual = new HandNeoPokerBot(); //guardamos la mano y creamos un objeto nuevo para la nueva mano.
			}
			else{
				handActual.sumarTurnoCalleActual();
				
				if(handActual.getCalleActual() == Calle.BLINDS){
					Matcher m = null;
					m = pIni.matcher(lineas[i]); m.find();
					handActual.setHandNumber(Long.parseLong(m.group(1)));
					i++;
					m = pBU.matcher(lineas[i]); m.find();
					String bu = m.group(1);
					i++;
					m = pSB.matcher(lineas[i]); m.find();
					String sb = m.group(1);
					handActual.setSB(Double.parseDouble(m.group(2)));							
					i++;
					m = pBB.matcher(lineas[i]); m.find();
					String bb = m.group(1);
					handActual.setBB(Double.parseDouble(m.group(2)));
					i++;
					
					//ESTO REALMENTE NO SE SI ES NECESARIO, PERO ANYADO LA INFO DE LAS POST BLINDS
					//AL MAP DE ACCIONES POR SI TENGO Q CONTABILIZAR TODAS LAS FICHAS INVERTIDAS POR LOS
					//JUGADORES QUE ESTAN EN LA CIEGA EN EL TRANSCURSO DE TODA LA MANO O ALGO ASI.
					Accion accionSB = new Accion(sb, Posicion.SB, AccionEnum.POST, handActual.getSB());
					handActual.addAction(Posicion.SB, handActual.getCalleActual(), accionSB);
					Accion accionBB = new Accion(bb, Posicion.BB, AccionEnum.POST, handActual.getBB());
					handActual.addAction(Posicion.BB, handActual.getCalleActual(), accionBB);
					
					//Aqui los meteremos empezando siempre por Hero, que es el patron que sigue la web.
					List<String> players = new ArrayList<String>(10);
					
					boolean aux = true;
					while(aux){
						m = pStack.matcher(lineas[i]);
						aux = m.find();
						if(aux){
							String player = m.group(1);
							double stack = Double.parseDouble(m.group(2));
							
							//Le anayado al stack las ciegas, ya que esta web las descuenta al poner los stacks preflop.
							if(player.equals(sb)) stack += handActual.getSB();
							else if(player.equals(bb)) stack += handActual.getBB();
							
							handActual.addStackPreflopAux(player, stack);
							players.add(player);
							i++;
						}
					}
					
					handActual.setNumberOfPlayers(players.size());
					int posSB = 0;
					while(!players.get(posSB).equals(sb)) posSB++;
					for(int pos=0; pos<posSB; pos++) players.add(players.get(pos)); //anyadimos a la lista (por el final) los players q estaban delante de la SB.
					ListIterator<String> iter = players.listIterator(posSB);
					int posAct = Posicion.SB; //Empezamos asignando la pos a la SB que es el 9
					while(iter.hasNext()){
						String p = iter.next();
						//Si hay mas de 2 jugadores el BU lo asignamos manualmente pues es el 0 y no llegaremos
						//decreciendo a ese numero salvo q haya 10 jugadores. Si hay 2 players, NO hay BU solo SB y BB.
						if(p.equals(bu) && handActual.getNumberOfPlayers()>2)handActual.addPosition(p, 0);
						else handActual.addPosition(p, posAct);
						posAct--;
					}
					
					m = pPreflop.matcher(lineas[i]); m.find();
					handActual.setPreflopPot(Double.parseDouble(m.group(1)));
					i++;
					m = pDealt.matcher(lineas[i]); m.find();
					handActual.setHeroName(m.group(1));
					Carta c1 = new Carta(m.group(2));
					Carta c2 = new Carta(m.group(3));
					Mano mano = new Mano(c1,c2);
					handActual.setHeroCards(mano);
					//AQUI YA NO PONGO EL i++ PUES LO HACE EL PROPIO BUCLE.
					
					handActual.setCalleActual(Calle.PREFLOP);
				}
				else{ //calleActual != BLINDS
					boolean lineaParseada = false;
					
					//Chekeamos primero si parsean las acciones
					for(int j=0; !lineaParseada && j<patternsAcciones.length; j++){
						Matcher m = patternsAcciones[j].matcher(lineas[i]);
						if(m.find()){
							AccionEnum acc = null;
							switch(j){
								case 0: acc = AccionEnum.FOLD; break;
								case 1: acc = AccionEnum.CHECK; break;
								case 2: acc = AccionEnum.CALL; break;
								case 3: acc = AccionEnum.BET; break;
								case 4: acc = AccionEnum.ALLIN; break;
								case 5: acc = AccionEnum.RAISE; break;
							}
							String player = m.group(1);
							Integer posPlayer = handActual.getPosition(player);
							Accion accion = new Accion(player, posPlayer, acc);
							if(j>=2) accion.setCantidad(Double.parseDouble(m.group(2)));
							handActual.addAction(posPlayer, handActual.getCalleActual(), accion);
							lineaParseada = true;
						}
					}
					if(lineaParseada) continue;
					
					//Si no parsean las acciones, checkeamoss entonces si parsea Flop/Turn/River o Wins.
					Matcher m = null;
					m = pFlop.matcher(lineas[i]);
					if(m.find()){
						Carta c1 = new Carta(m.group(1));
						Carta c2 = new Carta(m.group(2));
						Carta c3 = new Carta(m.group(3));
						List<Carta> cards = new ArrayList<Carta>(3);
						cards.add(c1); cards.add(c2); cards.add(c3);
						handActual.setFlop(cards);
						handActual.setFlopPot(Double.parseDouble(m.group(4)));
						handActual.setCalleActual(Calle.FLOP);
						continue;
					}
					
					m = pTurn.matcher(lineas[i]);
					if(m.find()){
						Carta c1 = new Carta(m.group(4));
						handActual.setTurn(c1);
						handActual.setTurnPot(Double.parseDouble(m.group(5)));
						handActual.setCalleActual(Calle.TURN);
						continue;
					}
					
					m = pRiver.matcher(lineas[i]);
					if(m.find()){
						Carta c1 = new Carta(m.group(5));
						handActual.setRiver(c1);
						handActual.setRiverPot(Double.parseDouble(m.group(6)));
						handActual.setCalleActual(Calle.RIVER);
						continue; 
					}
					
					//Cuando sale el patron de "Rubén shows cards: (6h,7h)" siempre va seguido de una linea
					//con el patron: "Best hand: (6d,6h,7c,7h,Ts), Two Pair", asi que en este mismo IF parseamos
					//la siguiente linea y aumentamos el contador del FOR para que no la analice luego de nuevo
					m = pShows.matcher(lineas[i]);
					if(m.find()){				
						Carta c1 = new Carta(m.group(2));
						Carta c2 = new Carta(m.group(3));
						Mano mano = new Mano(c1,c2);
						
						Matcher m2 = pBestHand.matcher(lineas[i+1]); //parseamos el patron de BestHand que es la linea i+1
						m2.find();
						
						List<Carta> jugada = new ArrayList<Carta>(5);
						for(int j=1; j<6; j++) jugada.add(new Carta(m2.group(j)));
						
						Showdown sd = new Showdown(m.group(1), mano, jugada, m2.group(6));
						handActual.addShowdown(sd);
						i++; //aumentamos la i para que no analice la linea i+1 que ya la hemos analizado
						continue;
					}
					
					m = pWins.matcher(lineas[i]);
					if(m.find()){									
						handActual.addWinner(new Winner(m.group(1), Double.parseDouble(m.group(2))));
						
						//Comprobamos si ha habido Showdown para apuntarlo, lo hacemos aqui porque las
						//lineas con este patron siempre son las ultimas de la mano.
						if(handActual.getShowdowns().size()==0) handActual.setCalleActual(Calle.NO_SD);
						else handActual.setCalleActual(Calle.SHOWDOWN);
						
						continue;
					}
				}						
			}
		}
		
		return handActual;
	}

	@Override
	public void ejecutarAccion(Accion accion) {
		// TODO Auto-generated method stub
	}
	
	
	/** 
	 * Devuelve TRUE si ha habido info nueva y FALSE si NO la ha habido,
	 * esto es para que si se vuelve a ejecutar este metodo antes de que
	 * se haya tomado una accion no se ponga infoNueva="" que jode todo.
	 */
	public boolean recopilarChat() {
		UtilScraping.clickar(clickX, clickY);
		String aux = UtilScraping.copiarTodoElTexto();
		if(aux.length()==ultimaRecopilacion.length()) return false;
		infoNueva = aux.substring(ultimaRecopilacion.length());
		ultimaRecopilacion = aux;
		return true;
	}

	public static boolean isCleanImage(BufferedImage bmp) {
		int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
		int cont = 0;
		for(int x=0; x<bmp.getWidth(); x++)
			for(int y=0; y<bmp.getHeight(); y++)
				pixels[cont++] = bmp.getRGB(x, y);
		
		for(int i=0; i<pixels.length-1; i++) {
			if(pixels[i]!=pixels[i+1]) return false;
		}
		return true;
	}

}
