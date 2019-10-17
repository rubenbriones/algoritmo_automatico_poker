package logica;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * En el flopzilla el porcentaje importnte de manos seleccionadas es el de la derecha del todo, no el de abajo de la barra.
 */
public class SelectorManosPredefinido implements ISelectorManos{

	private static final int NUM_MANOS_POSIBLES = 1326; //13*6 + 78*12 + 78*4
	
	private String nombreFichero;
	
	//Aqui guardaremos los 169 combos preflop posibles ordenados de mayor a menor fuerza preflop.
	//Esta lista no hay que modificarla nunca, solo copiarla
	private List<Combo> combosOrdenados; 
	
	private int[] acumuladoManosPosibles;

	public SelectorManosPredefinido(String nombreFichero) {
		this.nombreFichero = nombreFichero;
		this.combosOrdenados = Collections.unmodifiableList(leerFicheroCombos(nombreFichero));
		
		acumuladoManosPosibles = new int[combosOrdenados.size()];
		acumuladoManosPosibles[0] = combosOrdenados.get(0).getCombinaciones();
		for(int i=1; i<acumuladoManosPosibles.length; i++) {
			acumuladoManosPosibles[i] = acumuladoManosPosibles[i-1]+combosOrdenados.get(i).getCombinaciones();
		}
	}
	
	private List<Combo> leerFicheroCombos(String nombreFichero){
		List<Combo> lista = new ArrayList<Combo>(169);
		File f = new File("FuerzaManosPreflop\\"+nombreFichero+".txt");
		Scanner s;
		try {
			s = new Scanner(f).useDelimiter(",");
			while(s.hasNext()){
				lista.add(new Combo(s.next()));
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return lista;
	}
		
	@Override
	//En los limites se queda con el combo que esta mas cerca en valor absoluto del % deseado
	//A veces eso hara que se queden algunas manos fuera, y otras que entren algunas manos mas.
	//En caso de empate total (el % este justo en medio medio de dos combos), si que tira por la opcion que mete de mas.
	public Rango getMejoresManos(double porcentaje, double porcentajeExcluidoPorArriba) {
		int manosSeleccionadasPreExclusion = (int) Math.round(NUM_MANOS_POSIBLES*porcentaje/100.0);
		int manosExcluidasPorArriba = (int) Math.round(NUM_MANOS_POSIBLES*porcentajeExcluidoPorArriba/100.0);
		
		int indicePrimerCombo = 0;
		while(indicePrimerCombo<acumuladoManosPosibles.length &&
				acumuladoManosPosibles[indicePrimerCombo] <= manosExcluidasPorArriba){
			indicePrimerCombo++;
		}
		//vemos a que combo realmente se aporxima mas el numero exacto
		if(indicePrimerCombo>0 && indicePrimerCombo<acumuladoManosPosibles.length-1 &&
				acumuladoManosPosibles[indicePrimerCombo]-manosExcluidasPorArriba < manosExcluidasPorArriba-acumuladoManosPosibles[indicePrimerCombo-1]) indicePrimerCombo++;
		
		int indiceUltimoCombo = indicePrimerCombo;
		while(indiceUltimoCombo<acumuladoManosPosibles.length &&
				acumuladoManosPosibles[indiceUltimoCombo] <= manosSeleccionadasPreExclusion){
			indiceUltimoCombo++;
		}
		//vemos a que combo realmente se aporxima mas el numero exacto
		if(indiceUltimoCombo>0 && indiceUltimoCombo<acumuladoManosPosibles.length-1 &&
				acumuladoManosPosibles[indiceUltimoCombo]-manosSeleccionadasPreExclusion <= manosSeleccionadasPreExclusion-acumuladoManosPosibles[indiceUltimoCombo-1]) indiceUltimoCombo++;
		
		/*System.out.println(indicePrimerCombo+" "+indiceUltimoCombo);
		System.out.println(manosSeleccionadasPreExclusion+" "+manosExcluidasPorArriba+" "+manosSeleccionadas);
		System.out.println("ini: "+acumuladoManosPosibles[indicePrimerCombo-1]+" "+acumuladoManosPosibles[indicePrimerCombo]+" "+acumuladoManosPosibles[indicePrimerCombo+1]);
		System.out.println("fin: "+acumuladoManosPosibles[indiceUltimoCombo-1]+" "+acumuladoManosPosibles[indiceUltimoCombo]+" "+acumuladoManosPosibles[indiceUltimoCombo+1]);
		System.out.println("ini: "+combosOrdenados.get(indicePrimerCombo-1)+" "+combosOrdenados.get(indicePrimerCombo)+" "+combosOrdenados.get(indicePrimerCombo+1));
		System.out.println("fin: "+combosOrdenados.get(indiceUltimoCombo-1)+" "+combosOrdenados.get(indiceUltimoCombo)+" "+combosOrdenados.get(indiceUltimoCombo+1));*/
		if(indicePrimerCombo<combosOrdenados.size()){
			List<Combo> lista = combosOrdenados.subList(indicePrimerCombo, indiceUltimoCombo);
			return new Rango(lista);
		}
		return new Rango();
	}
	
	/*@Override
	public Rango getMejoresManosConPockets(double porcentaje, double porcentajeExcluidoPorArriba) {
		return getMejoresManos(porcentaje, porcentajeExcluidoPorArriba).incluirPockets();
	}*/

	@Override
	public Rango getMejoresManosSinPockets(double porcentaje, double porcentajeExcluidoPorArriba) {
		return getMejoresManos(porcentaje, porcentajeExcluidoPorArriba).excluirPockets();
	}

	@Override
	public Rango getRangoPolarizado(double porcentajeTop, double porcentajeExluidoTop, double porcentajeBottom, double porcentajeExcuidoBottom) {
		Rango rangoTop = getMejoresManos(porcentajeTop, porcentajeExluidoTop);
		List<Combo> listaTop = new ArrayList<Combo>(rangoTop.getCombos());

		int manosExcluidasBottom = (int) Math.round(NUM_MANOS_POSIBLES*porcentajeExcuidoBottom/100.0);
		int manosSeleccionadasBottom = (int) Math.round(NUM_MANOS_POSIBLES*porcentajeBottom/100.0);
		int manosSeleccionadasIncuidasBottom = manosExcluidasBottom + manosSeleccionadasBottom;
		int acmuladoTotal = acumuladoManosPosibles[acumuladoManosPosibles.length-1]; //esto es igual a  NUM_MANOS_POSIBLES
		
		int indiceUltimoComboBottom = acumuladoManosPosibles.length-1;		
		while(indiceUltimoComboBottom>=0 &&
				acmuladoTotal-acumuladoManosPosibles[indiceUltimoComboBottom] < manosExcluidasBottom){
			indiceUltimoComboBottom--;
		}
		indiceUltimoComboBottom++; //al ir marcha atras tengo que aumentar uno, ya que la condicion de parada tendria q haber sido ahi.
		//vemos a que combo realmente se aporxima mas el numero exacto
		if(indiceUltimoComboBottom>0 && indiceUltimoComboBottom<=acumuladoManosPosibles.length-1 &&
				manosExcluidasBottom-(acmuladoTotal-acumuladoManosPosibles[indiceUltimoComboBottom]) <= acmuladoTotal-acumuladoManosPosibles[indiceUltimoComboBottom-1]-manosExcluidasBottom) indiceUltimoComboBottom++;
		
		int indicePrimerComboBottom = indiceUltimoComboBottom-1;
		while(indicePrimerComboBottom>=0 &&
				acmuladoTotal-acumuladoManosPosibles[indicePrimerComboBottom] < manosSeleccionadasIncuidasBottom){
			indicePrimerComboBottom--;
		}
		indicePrimerComboBottom++; //al ir marcha atras tengo que aumentar uno, ya que la condicion de parada tendria q haber sido ahi.
		//vemos a que combo realmente se aporxima mas el numero exacto
		if(indicePrimerComboBottom>0 && indicePrimerComboBottom<=acumuladoManosPosibles.length-1 &&
				manosSeleccionadasIncuidasBottom-(acmuladoTotal-acumuladoManosPosibles[indicePrimerComboBottom]) < acmuladoTotal-acumuladoManosPosibles[indicePrimerComboBottom-1]-manosSeleccionadasIncuidasBottom) indicePrimerComboBottom++;
		
		/*System.out.println(manosExcluidasBottom+" "+manosSeleccionadasBottom+" "+manosSeleccionadasIncuidasBottom+"       "+indiceUltimoComboBottom+" "+acmuladoTotal);
		System.out.println("ini: "+acumuladoManosPosibles[indicePrimerComboBottom-1]+" "+acumuladoManosPosibles[indicePrimerComboBottom]+" "+acumuladoManosPosibles[indicePrimerComboBottom+1]);
		System.out.println("fin: "+acumuladoManosPosibles[indiceUltimoComboBottom-1]+" "+acumuladoManosPosibles[indiceUltimoComboBottom]+" "+acumuladoManosPosibles[indiceUltimoComboBottom+1]);
		System.out.println("ini: "+combosOrdenados.get(indicePrimerComboBottom-1)+" "+combosOrdenados.get(indicePrimerComboBottom)+" "+combosOrdenados.get(indicePrimerComboBottom+1));
		System.out.println("fin: "+combosOrdenados.get(indiceUltimoComboBottom-1)+" "+combosOrdenados.get(indiceUltimoComboBottom)+" "+combosOrdenados.get(indiceUltimoComboBottom+1));
		*/
		List<Combo> listaBottom = combosOrdenados.subList(indicePrimerComboBottom, indiceUltimoComboBottom);
		
		listaTop.addAll(listaBottom);
		return new Rango(listaTop);
	}
	
	
	public static void main(String[] args){
		ISelectorManos selector = new SelectorManosPredefinido("HUai");
		//Rango r = selector.getMejoresManos(36.95,0.38);
		//Rango r = selector.getRangoPolarizado(20, 0, 35, 0); //mr SB 25bb
		Rango r = selector.getRangoPolarizado(15, 0, 25, 0);
		Util.imprimirCuadradoRango(r);
	}

}
