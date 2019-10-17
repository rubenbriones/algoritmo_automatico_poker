package tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import constantes.Jugada;
import constantes.Palo;
import logica.*;

public class GeneradorBoardsRandom {
	
	public static void main(String[] args){
		generarFlopsParaVerSiCbeteo();
	}
	
	//Genera manos random que se enfrentana a flops random
	public static void generarFlopsYManosParaVerSiCbeteo(){
		ISelectorManos selector = new SelectorManosPredefinido("nolimit");
		ICalculadoraEquity calc = new CalculadoraEquitySteveBrecher();
		GeneradorBoardsRandom g = new GeneradorBoardsRandom();
		List<List<Carta>> barajas = g.generarBarajasRandom(50);
		Rango rango = selector.getMejoresManos(80, 12);
		Util.imprimirCuadradoRango(rango);

		for (List<Carta> b : barajas) {
			List<Carta> flop = b.subList(0, 3);
			Mano mano = new Mano(b.get(3), b.get(4));
			RangoConcreto rangoConcreto = new RangoConcreto(rango.getCombos(), mano, flop);
			ResJugadasRango resRango = Jugada.getJugadasRango(rangoConcreto, flop);
			ResJugadas resMano = Jugada.getJugadasEnFlop(mano, flop);
			double best = resRango.calculatePercentageRangeIsBest(resMano);
			double nomadehand = resRango.getPercentageNoMadeHand();
			double nomadehandonly = resRango.getPercentageNoMadeHandWithoutProjectsAndBackdoors();
			ResCalculadoraEquity equity = calc.calc(mano, rangoConcreto.getManos(), flop);
			System.out.println(mano.getListaCartas().toString()+"\t"+flop.toString()+"\t"+equity.getEquityMano()+"\t"+equity.getEquityRival()+"\t\t"+best+"\t"+nomadehand+"\t"+nomadehandonly);
		}
	}
	
	//Genera manos de mi rango de MR en SB HU (mas o menos) vs flops random
	public static void generarFlopsParaVerSiCbeteo(){
		ISelectorManos selector = new SelectorManosPredefinido("nolimit");
		ICalculadoraEquity calc = new CalculadoraEquitySteveBrecher();
		GeneradorBoardsRandom g = new GeneradorBoardsRandom();
		
		Rango rangoHeroMR = selector.getRangoPolarizado(20, 0, 35, 0);
		//Util.imprimirCuadradoRango(rangoHeroMR);
		
		List<List<Carta>> barajas = g.generarBarajasRandom(50);
		Rango rangoRival = selector.getMejoresManos(80, 12);
		Util.imprimirCuadradoRango(rangoRival);

		for (List<Carta> b : barajas) {
			List<Carta> flop = b.subList(0, 3);
			RangoConcreto posibleRangoConcretoHero = new RangoConcreto(rangoRival.getCombos(), null, flop);
			Mano mano = posibleRangoConcretoHero.getManoRandom();
			
			RangoConcreto rangoConcreto = new RangoConcreto(rangoRival.getCombos(), mano, flop);
			ResJugadasRango resRango = Jugada.getJugadasRango(rangoConcreto, flop);
			ResJugadas resMano = Jugada.getJugadasEnFlop(mano, flop);
			double best = resRango.calculatePercentageRangeIsBest(resMano);
			double nomadehand = resRango.getPercentageNoMadeHand();
			double nomadehandonly = resRango.getPercentageNoMadeHandWithoutProjectsAndBackdoors();
			ResCalculadoraEquity equity = calc.calc(mano, rangoConcreto.getManos(), flop);
			System.out.println(mano.getListaCartas().toString()+"\t"+flop.toString()+"\t"+equity.getEquityMano()+"\t"+equity.getEquityRival()+"\t\t"+best+"\t"+nomadehand+"\t"+nomadehandonly);
		}
	}
	
	public List<List<Carta>> generarBarajasRandom(int num){
		List<List<Carta>> res = new ArrayList<List<Carta>>(num);
		List<Carta> baraja = generarBaraja();
		for(int i=0; i<num; i++){
			List<Carta> nuevaBaraja = new ArrayList<Carta>(baraja);
			Collections.shuffle(nuevaBaraja);
			res.add(nuevaBaraja);
		}
		return res;
	}
	
	public List<List<Carta>> generarBoardsRandom(int num, int longitudBoard){
		List<List<Carta>> res = new ArrayList<List<Carta>>(num);
		List<Carta> baraja = generarBaraja();
		for(int i=0; i<num; i++){
			Collections.shuffle(baraja);
			List<Carta> board = new ArrayList<Carta>(longitudBoard);
			board.addAll(baraja.subList(0, longitudBoard));
			res.add(board);
		}
		return res;
	}
	
	public List<Carta> generarBaraja(){
		List<Carta> baraja = new ArrayList<Carta>(52);
		char[] palos = {'h','s','c','d'};
		char[] numeros = {'A','K','Q','J','T','9','8','7','6','5','4','3','2'};
		
		for(int i = 0; i < palos.length; i++) {
			for(int j=0; j<numeros.length; j++){
				baraja.add(new Carta(""+numeros[j]+palos[i]));
			}
		}
		
		return baraja;
	}
}
