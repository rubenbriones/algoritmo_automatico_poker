package logica;

import java.util.ArrayList;
import java.util.List;

import constantes.Palo;
import stevebrecher.Card;
import stevebrecher.CardSet;
import stevebrecher.Enumerator;
import stevebrecher.HandEval;
import stevebrecher.UserInputMOD;

public class CalculadoraEquitySteveBrecher implements ICalculadoraEquity{
	
	static int threads = Runtime.getRuntime().availableProcessors();

	@Override
	public ResCalculadoraEquity calc(Mano mano, List<Mano> manos, List<Carta> boardCards) {
		double sumaPorcentajes = 0;
		for(Mano manoConcreta : manos){
			sumaPorcentajes += calc(mano, manoConcreta, boardCards, null);
		}
		
		double equity = sumaPorcentajes/manos.size();
				
		return new ResCalculadoraEquity(equity, manos.size());
	}
	
	@Override
	public ResCalculadoraEquity calc(Mano mano, Rango rango, List<Carta> boardCards, List<Carta> deadCards) {		
		List<Combo> combos = rango.getCombos();
		ResCalculadoraEquity[] resultadosParciales = new ResCalculadoraEquity[rango.getNumCombos()];
		
		for(int i=0; i<resultadosParciales.length; i++){
			resultadosParciales[i] = calc(mano, combos.get(i), boardCards, deadCards);
		}
		
		double sumaTotal = 0;
		int manosConcretasTotales = 0;
		for(int i=0; i<resultadosParciales.length; i++) {
			sumaTotal += resultadosParciales[i].getEquityMano()*resultadosParciales[i].getNumManosPosibles();
			manosConcretasTotales += resultadosParciales[i].getNumManosPosibles();
		}
		
		double equity = sumaTotal/manosConcretasTotales;
		
		return new ResCalculadoraEquity(equity, manosConcretasTotales);
	}
	
	@Override
	public ResCalculadoraEquity calc(Mano mano, Combo combo, List<Carta> boardCards, List<Carta> deadCards) {
		List<Carta> cartasYaRepartidas = new ArrayList<Carta>(13);
		cartasYaRepartidas.add(mano.getCarta1());
		cartasYaRepartidas.add(mano.getCarta2());
		if(boardCards != null) cartasYaRepartidas.addAll(boardCards);
		if(deadCards != null) cartasYaRepartidas.addAll(deadCards);
		
		List<Mano> manosConcretasPosibles = combo.generarManosConcretas(cartasYaRepartidas);
		double sumaPorcentajes = 0;
		for(Mano manoConcreta : manosConcretasPosibles){
			sumaPorcentajes += calc(mano, manoConcreta, boardCards, deadCards);
		}
		
		double equity = sumaPorcentajes/manosConcretasPosibles.size();
				
		return new ResCalculadoraEquity(equity, manosConcretasPosibles.size());
	}

	@Override
	public double calc(Mano mano1, Mano mano2, List<Carta> boardCards, List<Carta> deadCards) {	
		if(boardCards == null) boardCards = new ArrayList<Carta>();
		if(deadCards == null) deadCards = new ArrayList<Carta>();
		
		//Si ya tenemos las 5 cartas del board, solo hay que ver que mano es mejor (sin threads, ni combinaciones, ni barajas)
		if(boardCards.size() == 5){
			CardSet m1 = new CardSet(2);
			CardSet m2 = new CardSet(2);
			Card c11 = new Card(mano1.getCarta1().toString());
			Card c12 = new Card(mano1.getCarta2().toString());
			m1.add(c11); m1.add(c12);
			Card c21 = new Card(mano2.getCarta1().toString());
			Card c22 = new Card(mano2.getCarta2().toString());
			m2.add(c21); m2.add(c22);
			
			CardSet board = new CardSet(5);
			for(Carta c : boardCards) board.add(new Card(c.toString()));
			
			m1.addAll(board);
			m2.addAll(board);
			
			long h1 = HandEval.encode(m1);
	        long h2 = HandEval.encode(m2);
	        
	        int r1 = HandEval.hand7Eval(h1);        
	        int r2 = HandEval.hand7Eval(h2);
	        
	        if(r1 == r2) return 50.0;	        
	        return r1 > r2 ? 100.0 : 0.0;
		}
		
		//PREPARAMOS LA INFO PARA SER PROCESADA POR Enumerator
		Mano[] manos = {mano1, mano2};
		UserInputMOD ui = new UserInputMOD(manos, boardCards, deadCards, 0);
		
		//PROCESAMOS LA INFO CON VARIOS THEARDS DE Enumerator
		Enumerator[] enumerators = new Enumerator[threads];
		for (int i = 0; i < enumerators.length; i++) {
			enumerators[i] = new Enumerator(i, threads, ui.deck(), ui.holeCards(), ui.nUnknown(), ui.boardCards());
			enumerators[i].start();
		}
		for (Enumerator enumerator : enumerators) {
			try {
				enumerator.join();
			} catch (InterruptedException never) {}
		}

		//RECOPILAMOS RESULTADOS
		long[] wins = new long[2];
		//long[] splits =  new long[2];
		double[] partialPots = new double[2];
		
		for (Enumerator e : enumerators){
			for (int i = 0; i < wins.length; i++) {
				wins[i] += e.getWins()[i];
				//splits[i] += e.getSplits()[i];
				partialPots[i] += e.getPartialPots()[i];
			}
		}

		//f.printf("%nexpected return, %% of pot"); NOTA: DE LOS 3 PORCENTAJES QUE DEVOLVIA SteveBrecher EL QUE QUEREMOS ES ESTE.
		return ((wins[0] + partialPots[0]) * 100.0 / ui.nPots());
	}

	
	
	public static void main(String[] args){
		CalculadoraEquitySteveBrecher calc = new CalculadoraEquitySteveBrecher();
		/*Mano m1 = new Mano(new Carta("Ad",14,Palo.DIAMANTES), new Carta("9d",9,Palo.DIAMANTES));
		Mano m2 = new Mano(new Carta("5s",5,Palo.PICAS), new Carta("6s",6,Palo.PICAS));
		
		Carta cb1 = new Carta("7h",7,Palo.CORAZONES); //Carta.shortEnglishToCarta("Ad")
		Carta cb2 = new Carta("6c",8,Palo.TREBOLES);
		Carta cb3 = new Carta("3d",9,Palo.DIAMANTES);
		Carta cb4 = new Carta("2d",9,Palo.DIAMANTES);
		Carta cb5 = new Carta("8h",8,Palo.CORAZONES);
		List<Carta> board = new ArrayList<Carta>(5);
		board.add(cb1); board.add(cb2); board.add(cb3); board.add(cb4); board.add(cb5);
		List<Carta> dead = new ArrayList<Carta>();
		
		double res = calc.calc(m1, m2, board, dead);
		System.out.println(res);*/
		
		
		/*List<Carta> board = new ArrayList<Carta>(5);
		board.add(new Carta("4s"));
		board.add(new Carta("4c"));
		board.add(new Carta("8h"));
		board.add(new Carta("7h"));
		List<Carta> dead = new ArrayList<Carta>();
		ResCalculadoraEquity res = calc.calc(new Mano("6h5h"), new Combo("44"), board, dead);
		System.out.println(res.getEquityMano());*/
		
		
		/*Rango rango = new Rango();
		rango.addCombo(new Combo("AKo"));
		rango.addCombo(new Combo("AKs"));
		rango.addCombo(new Combo("AQo"));
		rango.addCombo(new Combo("AJs"));	
		rango.addCombo(new Combo("66"));
		rango.addCombo(new Combo("77"));	
		rango.addCombo(new Combo("43s"));
		rango.addCombo(new Combo("56s"));
		List<Carta> board = new ArrayList<Carta>(5);
		board.add(new Carta("4s"));
		board.add(new Carta("4c"));
		board.add(new Carta("8h"));
		board.add(new Carta("Ac"));
		board.add(new Carta("3s"));
		List<Carta> dead = new ArrayList<Carta>();
		ResCalculadoraEquity res = calc.calc(new Mano("6h5h"), rango, board, dead);
		System.out.println(res.getEquityMano());*/
		
		
		ISelectorManos selector = new SelectorManosPredefinido("HUai");
		Rango rango = selector.getMejoresManos(15, 0);
		System.out.println(rango.toString());
		System.out.println(rango.getNumCombos());
		List<Carta> board = new ArrayList<Carta>(5);
		List<Carta> dead = new ArrayList<Carta>();
		ResCalculadoraEquity res = calc.calc(new Mano("TdTh"), rango, board, dead);
		System.out.println(res.getEquityMano());	
	}
}
