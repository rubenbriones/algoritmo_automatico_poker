package logica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tests.GeneradorBoardsRandom;
import constantes.Jugada;

public class ResJugadasRango {
	
	private RangoConcreto rango;
	private List<ResJugadas> resJugadas;
	
	private Map<Jugada, List<ResJugadas>> mapJugadas;
	
	private double percentageNoMadeHand;
	private double percentageNoMadeHandWithoutProjectsAndBackdoors; //Aqui trigueamos el rango de no_made_hand, es decir este seria el porcentaje de manos que segun flopzilla
																	//pasarian a la siguiente calle si el rival nos paga con todo salvo con NO_MADE_HAND
	
	public ResJugadasRango(RangoConcreto rango){
		this.rango = rango;
		this.resJugadas = new ArrayList<ResJugadas>(rango.getNumManos());
		this.percentageNoMadeHand = -1;
		this.percentageNoMadeHandWithoutProjectsAndBackdoors = -1;
	}
	
	public void addResJugadas(ResJugadas res){
		resJugadas.add(res);
	}
	
	public Map<Jugada, List<ResJugadas>> getMapJugadas(){
		if(mapJugadas == null) mapJugadas = destriparJugadas();
		return mapJugadas;
	}

	public double getPercentageNoMadeHand() {
		if(percentageNoMadeHand == -1) percentageNoMadeHand = calculatePercentageNoMadeHand();
		return percentageNoMadeHand;
	}

	public double getPercentageNoMadeHandWithoutProjectsAndBackdoors() {
		if(percentageNoMadeHandWithoutProjectsAndBackdoors == -1)
			percentageNoMadeHandWithoutProjectsAndBackdoors = calculatePercentaheNoMadeHandWithoutProjectsAndBackdoors();
		return percentageNoMadeHandWithoutProjectsAndBackdoors;
	}

	//Devuelve el porcentaje de veces que el rango es mejor que la mano que le pasamos con su resJugada.
	//Suponiendo que no van a salir mas cartas. Es decir no se tienen en cuenta los proyectos.
	//LLAMAR SOLO UNA VEZ A ESTE METODO, PUES HACE MUCHOS CALCULOS.
	public double calculatePercentageRangeIsBest(ResJugadas mano){
		int manosGanadoras = 0;
		for(int i=0; i<resJugadas.size(); i++){
			if(resJugadas.get(i).getJugadaMaxima().getValorAlShowdown() > mano.getJugadaMaxima().getValorAlShowdown()) manosGanadoras++;
			else if(resJugadas.get(i).getJugadaMaxima().getValorAlShowdown() == mano.getJugadaMaxima().getValorAlShowdown()){
				//En el caso de que los dos tengan escalera el ganador sera el que la tenga mas alta, hay que tener cuidado cuando tiene escalera al as pero por abajo
				if(resJugadas.get(i).getJugadaMaxima() == Jugada.STRAIGHT){
					if(resJugadas.get(i).getBoardMasMano().get(4).getNumero() > mano.getBoardMasMano().get(4).getNumero()) manosGanadoras++;
					else if(resJugadas.get(i).getBoardMasMano().get(4).getNumero() == mano.getBoardMasMano().get(4).getNumero()) manosGanadoras += 0.5;
				}
				//Si NO es escalera, entonces el ganador siempre se ve viendo quien tiene la carta mas alta de las que tiene en mano.
				else{
					List<Carta> cartasRango = resJugadas.get(i).getCartasDeLaManoUtilizadas();
					List<Carta> cartasMano = mano.getCartasDeLaManoUtilizadas();
					Collections.sort(cartasRango, Collections.reverseOrder());
					Collections.sort(cartasMano, Collections.reverseOrder());
					double res = 0.5;
					int maxCartas = cartasRango.size() | cartasMano.size();
					if(maxCartas!=0){
						for(int j=0; j<maxCartas; j++){
							if(cartasRango.get(j).compareTo(cartasMano.get(j)) != 0){
								res = cartasRango.get(j).compareTo(cartasMano.get(j)) > 0 ? 1 : 0;
								break;
							}
						}
					}
					else{
						if(cartasRango.size() > cartasMano.size()) res = 1;
						else if(cartasRango.size() < cartasMano.size()) res = 0;
					}
					manosGanadoras += res;
				}
			}
		}
		return (manosGanadoras/(double)resJugadas.size())*100.0;
	}
	
	private double calculatePercentageNoMadeHand(){
		int manosNoMadeHand = 0;
		for(int i=0; i<resJugadas.size(); i++){
			if(resJugadas.get(i).getJugadaMaxima() == Jugada.NO_MADE_HAND) manosNoMadeHand++;
		}
		return (manosNoMadeHand/(double)resJugadas.size())*100.0;
	}
	
	private double calculatePercentaheNoMadeHandWithoutProjectsAndBackdoors(){
		int manosNoMadeHand = 0;
		for(int i=0; i<resJugadas.size(); i++){
			if(resJugadas.get(i).getJugadaMaxima() == Jugada.NO_MADE_HAND &&
				resJugadas.get(i).getProyectos().size() == 0 &&
				resJugadas.get(i).getBackdoors().size() == 0) manosNoMadeHand++;
		}
		return (manosNoMadeHand/(double)resJugadas.size())*100.0;
	}

	private Map<Jugada, List<ResJugadas>> destriparJugadas(){
		Map<Jugada, List<ResJugadas>> m = new HashMap<Jugada, List<ResJugadas>>();
		Jugada[] jugadas = Jugada.values();
		for (int i = 0; i < jugadas.length; i++){
			m.put(jugadas[i], new ArrayList<ResJugadas>());
		}
		
		for(ResJugadas res : resJugadas){
			m.get(res.getJugadaMaxima()).add(res);
			List<Jugada> proyectos = res.getProyectos();
			for(Jugada j : proyectos) m.get(j).add(res);
			List<Jugada> backdoors = res.getBackdoors();
			for(Jugada j : proyectos) m.get(j).add(res);
		}
		
		return m;
	}
	
	
	public static void main(String[] args){
		//ISelectorManos selector = new SelectorManosPredefinido("nolimit");
		ICalculadoraEquity calc = new CalculadoraEquitySteveBrecher();
		List<Combo> combos  = new ArrayList<Combo>();
		combos.add(new Combo("KQs"));
		combos.add(new Combo("Q9s"));
		combos.add(new Combo("T7s"));
		combos.add(new Combo("88"));
		combos.add(new Combo("32s"));
		Rango rango = new Rango(combos);
		Util.imprimirCuadradoRango(rango);

		List<Carta> flop = new ArrayList<Carta>();
		flop.add(new Carta("6h"));
		flop.add(new Carta("7c"));
		flop.add(new Carta("9c"));
		Mano mano = new Mano("KcTh");		

		RangoConcreto rangoConcreto = new RangoConcreto(rango.getCombos(), mano, flop);
		ResCalculadoraEquity equity = calc.calc(mano, rangoConcreto.getManos(), flop);
		ResJugadas resMano = Jugada.getJugadasEnFlop(mano, flop);
		ResJugadasRango resRango = Jugada.getJugadasRango(rangoConcreto, flop);

		double best = resRango.calculatePercentageRangeIsBest(resMano);
		double nomadehand = resRango.getPercentageNoMadeHand();
		double nomadehandonly = resRango.getPercentageNoMadeHandWithoutProjectsAndBackdoors();
		System.out.println(mano.getListaCartas().toString()+"\t"+flop.toString()+"\t"+equity.getEquityMano()+"\t"+equity.getEquityRival()+"\t\t"+best+"\t"+nomadehand+"\t"+nomadehandonly);
		
	}
}
