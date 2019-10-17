package logica;

import java.util.ArrayList;
import java.util.List;

public class Combo {

	public final static int NUM_COMBOS_OFF = 12;
	public final static int NUM_COMBOS_SUITED = 4;
	public final static int NUM_COMBOS_POCKETS = 6;
	
	private String combo; //AKs, AKo, 66, AK=AKo+AKs

	public Combo(String combo) {
		this.combo = combo;
	}

	public String getCombo() {
		return combo;
	}

	public void setCombo(String combo) {
		this.combo = combo;
	}
	
	public char getFirstRank(){
		return combo.charAt(0);
	}
	
	public char getSecondRank(){
		return combo.charAt(1);
	}
	
	public int getFirstRankNumber(){
		if(getFirstRank() == 'T') return 10;
		else if(getFirstRank() == 'J') return 11;
		else if(getFirstRank() == 'Q') return 12;
		else if(getFirstRank() == 'K') return 13;
		else if(getFirstRank() == 'A') return 14;
		else return Integer.parseInt(getFirstRank()+"");
	}
	public int getSecondRankNumber(){
		if(getSecondRank() == 'T') return 10;
		else if(getSecondRank() == 'J') return 11;
		else if(getSecondRank() == 'Q') return 12;
		else if(getSecondRank() == 'K') return 13;
		else if(getSecondRank() == 'A') return 14;
		else return Integer.parseInt(getSecondRank()+"");
	}
	
	public int getCombinaciones(){
		if(combo.charAt(0) == combo.charAt(1)) return NUM_COMBOS_POCKETS;		
		else if(combo.length() == 2) return NUM_COMBOS_OFF+NUM_COMBOS_SUITED;
		else if(combo.charAt(2) == 'o') return NUM_COMBOS_OFF;
		else if(combo.charAt(2) == 's') return NUM_COMBOS_SUITED;	
		return 0;
	}
	
	public boolean isPocket(){
		return combo.charAt(0) == combo.charAt(1);
	}
	
	public boolean isSuited(){
		return combo.length()== 3 && combo.charAt(2) == 's';
	}
	
	public boolean isOffsuited(){
		return combo.length()== 3 && combo.charAt(2) == 'o';
	}
	
	@Override
	public String toString() {
		return combo;
	}
	
	public static final String[][] COMBOS_OFF_Y_SUITED = {{"s","s"},{"d","d"},{"c","c"},{"h","h"},{"s","h"},{"s","d"},{"s","c"},{"h","d"},{"h","c"},{"h","s"},{"d","c"},{"d","s"},{"d","h"},{"c","s"},{"c","h"},{"c","d"}};
	public static final String[][] COMBOS_OFF = {{"s","h"},{"s","d"},{"s","c"},{"h","d"},{"h","c"},{"h","s"},{"d","c"},{"d","s"},{"d","h"},{"c","s"},{"c","h"},{"c","d"}};
	public static final String[][] COMBOS_SUITED = {{"s","s"},{"d","d"},{"c","c"},{"h","h"}};
    public static final String[][] COMBOS_POCKETS = {{"s","h"},{"s","d"},{"s","c"},{"h","d"},{"h","c"},{"d","c"}};
    
    //Le pasamos una Lista de las cartas ya repartidas (es decir, cartas "imposibles") y nos devuelve una lista con
    //todas las manos que se pueden generar de este combo sin utilizar las cartas ya repartidas.
    public List<Mano> generarManosConcretas(List<Carta> cartasYaRepartidas){		
    	if(isOffsuited()) return generarManosConcretas(COMBOS_OFF, cartasYaRepartidas);
    	else if(isSuited()) return generarManosConcretas(COMBOS_SUITED, cartasYaRepartidas);
    	else if(isPocket()) return generarManosConcretas(COMBOS_POCKETS, cartasYaRepartidas);
    	else return generarManosConcretas(COMBOS_OFF_Y_SUITED, cartasYaRepartidas);  
    }
    
    //palos puede ser una de estas tres: COMBOS_OFF, COMBOSç_SUITED, COMBOS_POCKETS
    private List<Mano> generarManosConcretas(String[][] palos, List<Carta> cartasYaRepartidas){
		List<Mano> manosConcretas = new ArrayList<Mano>(palos.length);
		
    	for(int i=0; i<palos.length; i++){
			Carta c1 = new Carta(getFirstRank()+palos[i][0]);
			if(!cartasYaRepartidas.contains(c1)){
				Carta c2 = new Carta(getSecondRank()+palos[i][1]);
				if(!cartasYaRepartidas.contains(c2)){
					manosConcretas.add(new Mano(c1, c2));
				}
			}
		}
    	
    	return manosConcretas;
    }
}
