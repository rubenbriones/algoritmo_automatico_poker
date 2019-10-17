package logica;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SelectorManosPredefinidoMAL implements ISelectorManos{

	private static final int NUM_COMBOS = 169;
	
	private String nombreFichero;
	
	//Aqui guardaremos los 169 combos preflop posibles ordenados de mayor a menor fuerza preflop.
	//Esta lista no hay que modificarla nunca, solo copiarla
	private List<Combo> combosOrdenados; 

	public SelectorManosPredefinidoMAL(String nombreFichero) {
		this.nombreFichero = nombreFichero;
		this.combosOrdenados = Collections.unmodifiableList(leerFicheroCombos(nombreFichero));
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
	public Rango getMejoresManos(double porcentaje, double porcentajeExcluidoPorArriba) {
		int combosExcluidosPorArriba = (int) Math.round(NUM_COMBOS*porcentajeExcluidoPorArriba/100.0);
		int combosSeleccionados = (int) Math.round(NUM_COMBOS*porcentaje/100.0);
		List<Combo> lista = combosOrdenados.subList(combosExcluidosPorArriba, combosSeleccionados);
		return new Rango(lista);
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
	public Rango getRangoPolarizado(double porcentajeTop, double porcentajeBottom,
			double porcentajeExluidoTop, double porcentajeExcuidoBottom) {
		int combosExcluidosTop = (int) Math.round(NUM_COMBOS*porcentajeExluidoTop/100.0);
		int combosSeleccionadosTop = (int) Math.round(NUM_COMBOS*porcentajeTop/100.0);
		List<Combo> listaTop = combosOrdenados.subList(combosExcluidosTop, combosSeleccionadosTop);

		int combosExcluidosBottom = (int) Math.round(NUM_COMBOS*porcentajeExcuidoBottom/100.0);
		int combosSeleccionadosBottom = (int) Math.round(NUM_COMBOS*porcentajeBottom/100.0);
		List<Combo> listaBottom = combosOrdenados.subList(combosOrdenados.size()-combosExcluidosBottom-combosSeleccionadosBottom, combosOrdenados.size()-combosExcluidosBottom);
		
		listaTop.addAll(listaBottom);
		return new Rango(listaTop);
	}

}
