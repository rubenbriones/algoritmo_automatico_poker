package logica;

import constantes.Calle;
import decididores.IDecididorFlop;
import decididores.IDecididorRiver;
import decididores.IDecididorTurn;
import decididores.IEliminadorCombosTrasFlop;
import decididores.IEliminadorCombosTrasTurn;
import scraping.IScraper;
import scraping.NeopokerbotScraper;
import scraping.UtilScraping;

public class Manager extends Thread {

	private IScraper scraper;
	private IAlgoritmo algPreflop, algFlop, algTurn, algRiver;
	
	public Manager(IScraper scraper, IAlgoritmo algPreflop, IAlgoritmo algFlop,
			IAlgoritmo algTurn, IAlgoritmo algRiver) {
		this.scraper = scraper;
		this.algPreflop = algPreflop;
		this.algFlop = algFlop;
		this.algTurn = algTurn;
		this.algRiver = algRiver;
	}
	public Manager(IScraper scraper, IAlgoritmo alg) { //usamos el mismo Algoritmo para todas las calles
		this(scraper, alg, alg, alg, alg);
	}
	
	public void run(){
		UtilScraping.espera(3000); //esto es para que nos de tiempo a poner la ventana del bot y el raton en su sitio
		
		while(true){
			boolean hayInfoNueva = false;
			do{
				hayInfoNueva = scraper.esNuestroTurnoConNuevaInfo();
				UtilScraping.espera(scraper.getTiempoDeEspera());
			}while(!hayInfoNueva);
			
			Hand hand = scraper.parseInfoAndGetHandActual();
			
			Accion accion = null;
			switch(hand.getCalleActual()){
				case PREFLOP: accion = algPreflop.analyze(hand); break;
				case FLOP: accion = algFlop.analyze(hand); break;
				case TURN: accion = algTurn.analyze(hand); break;
				case RIVER: accion = algRiver.analyze(hand); break;
				
				//para que si la InfoNueva es del showdown pues como no hay que hacer ninguna accion
				//volvemos al principio del while, a recopilr info de nuevo.
				//zzz - SI ESO PODIAMOS HACER QUE MOSTRAR POR PANTALLA INFO DEL RESULTADO DE LA MANO
				default: 
					algPreflop.resetFinDeMano();
					algFlop.resetFinDeMano();
					algTurn.resetFinDeMano();
					algRiver.resetFinDeMano();
					continue; 
			}
			
			//zzz - AQUI DEBEMOS MOSTRAR INFO POR PANTALLA DEL PROCEDIMIENTO PARA ELEGIR LA ACCION
			//pero en teoria creo que es mejor que cada algoritmo lo vyaa mostrando en su metodo de
			//analisis a medida que va sacando conclusiones.
			
			scraper.ejecutarAccion(accion);
		}			
	}
	
	
	public static void main(String[] args){
		IScraper sc = new NeopokerbotScraper();
		ISelectorManos selector = new SelectorManosPredefinido("HUai");
		ICalculadoraEquity calc = new CalculadoraEquitySteveBrecher();
		
		IDecididorFlop decididorFlop = null;
		IDecididorTurn decididorTurn = null;
		IDecididorRiver decididorRiver = null;		
		IEliminadorCombosTrasFlop eliminadorTrasFlop = null;
		IEliminadorCombosTrasTurn eliminadorTrasTurn = null;
		IAlgoritmo alg = new AlgoritmoSpinsHUvsReadless(selector, calc, decididorFlop, decididorTurn, decididorRiver, eliminadorTrasFlop, eliminadorTrasTurn);
		
		Manager m = new Manager(sc, alg);
		m.start();
	}
}
