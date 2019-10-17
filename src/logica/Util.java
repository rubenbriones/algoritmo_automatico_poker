package logica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {
	
	public static String[][] cuadradoCombos =   {{"AA","	AKs","	AQs","	AJs","	ATs","	A9s","	A8s","	A7s","	A6s","	A5s","	A4s","	A3s","	A2s"},
												{"AKo","	KK","	KQs","	KJs","	KTs","	K9s","	K8s","	K7s","	K6s","	K5s","	K4s","	K3s","	K2s"},
												{"AQo","	KQo","	QQ","	QJs","	QTs","	Q9s","	Q8s","	Q7s","	Q6s","	Q5s","	Q4s","	Q3s","	Q2s"},
												{"AJo","	KJo","	QJo","	JJ","	JTs","	J9s","	J8s","	J7s","	J6s","	J5s","	J4s","	J3s","	J2s"},
												{"ATo","	KTo","	QTo","	JTo","	TT","	T9s","	T8s","	T7s","	T6s","	T5s","	T4s","	T3s","	T2s"},
												{"A9o","	K9o","	Q9o","	J9o","	T9o","	99","	98s","	97s","	96s","	95s","	94s","	93s","	92s"},
												{"A8o","	K8o","	Q8o","	J8o","	T8o","	98o","	88","	87s","	86s","	85s","	84s","	83s","	82s"},
												{"A7o","	K7o","	Q7o","	J7o","	T7o","	97o","	87o","	77","	76s","	75s","	74s","	73s","	72s"},
												{"A6o","	K6o","	Q6o","	J6o","	T6o","	96o","	86o","	76o","	66","	65s","	64s","	63s","	62s"},
												{"A5o","	K5o","	Q5o","	J5o","	T5o","	95o","	85o","	75o","	65o","	55","	54s","	53s","	52s"},
												{"A4o","	K4o","	Q4o","	J4o","	T4o","	94o","	84o","	74o","	64o","	54o","	44","	43s","	42s"},
												{"A3o","	K3o","	Q3o","	J3o","	T3o","	93o","	83o","	73o","	63o","	53o","	43o","	33","	32s"},
												{"A2o","	K2o","	Q2o","	J2o","	T2o","	92o","	82o","	72o","	62o","	52o","	42o","	32o","	22"}};

	
	public static void imprimirCuadradoRango(Rango r){
		String[][] cuadrado = new String[13][13];
		for(Combo c : r.getCombos()){
			if(c.isSuited())
				cuadrado[14-c.getFirstRankNumber()][14-c.getSecondRankNumber()] = c.toString();
			else 
				cuadrado[14-c.getSecondRankNumber()][14-c.getFirstRankNumber()] = c.toString();
		}
		printGrid(cuadrado);
	}

	private static void printGrid(String[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				if(a[i][j] != null) System.out.printf("%3s ", a[i][j]);
				else				System.out.printf("%3s ", "");
			}
			System.out.println();
		}
	}
	
	//zzz - en river (cuando le pasamos un board con size=5) nos devolvera una combinacion que seran las 5 propias cartas del board solo
	//no se hasta que punto eso esta bien o deberiamos hacer que no la devuelva, o tratar ese caso de manera especial luego en el algoritmo o donde corresponda.
	public static List<List<Carta>> generarCombinacionesDe5cartas(Mano mano, List<Carta> board){
		List<Carta> listaCartas = new ArrayList<Carta>(board.size()+2);
		listaCartas.add(mano.getCarta1());
		listaCartas.add(mano.getCarta2());
		listaCartas.addAll(board);
		
		List<List<Carta>> combinaciones = new ArrayList<List<Carta>>();
        combinations2(listaCartas, 5, 0, new Carta[5], combinaciones);
        return combinaciones;
	}
	
	//genera combinaciones de las cartas de @arr cogidas de @len en @len
	//las combinaciones resultantes las mete en la lista @combinaciones
    private static void combinations2(List<Carta> arr, int len, int startPosition, Carta[] result, List<List<Carta>> combinaciones){
        if (len == 0){
            //System.out.println(Arrays.toString(result));
        	List<Carta> list = new ArrayList<Carta>();
        	for (int i = 0; i < result.length; i++) {
				list.add(result[i]);
			}
            combinaciones.add(list);
            return;
        }       
        for (int i = startPosition; i <= arr.size()-len; i++){
            result[result.length - len] = arr.get(i);
            combinations2(arr, len-1, i+1, result, combinaciones);
        }
    }    

	
	/*public static void main(String[] args){
		List<Carta> arr = new ArrayList<Carta>();
		arr.add(new Carta("Ad"));
		arr.add(new Carta("Qs"));
		arr.add(new Carta("Ts"));
		arr.add(new Carta("7c"));
		arr.add(new Carta("6h"));
		arr.add(new Carta("Qh"));
		arr.add(new Carta("9s"));
		List<List<Carta>> combinaciones = generarCombinacionesDe5cartas(arr);
        
        for(List<Carta> c : combinaciones){
        	System.out.println(c.toString());
        }
    }*/
}
