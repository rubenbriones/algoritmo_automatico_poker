package logica;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import persistencia.ILectorTablaPreflop;

/**
 * Aquí están todas las tablas preflop de una Situacion concreta, es decir, hay tablas con 
 * distintos numeros de ciegas para una misma situacion.
 */
public class TablasPreflop {
	
	Pattern pNombreFichero = Pattern.compile(".* ([^ ]+)BB.txt");
	
	private File carpeta;
	private ILectorTablaPreflop lector;
	
	private String nombreSituacion;
	private int numberOfPlayers;
	private int position;
	private Map<Double, Map<String, MovPreflop>> tablas; //el double son las ciegas, y el string el combo.
	private List<Double> listaBBsTablasOrdenadas; //ordenadas de menor a mayor
	
	public TablasPreflop(int numberOfPlayers, int position, File carpeta, ILectorTablaPreflop lector) {
		this.nombreSituacion = carpeta.getPath();
		this.numberOfPlayers = numberOfPlayers;
		this.position = position;
		this.carpeta = carpeta;
		this.lector = lector;
		this.tablas = leerTablas(carpeta, lector);
		this.listaBBsTablasOrdenadas = generarListaOrdenadaBBs(tablas);
	}
	
	private Map<Double, Map<String, MovPreflop>> leerTablas(File carpeta, ILectorTablaPreflop lector){
		File[] files = carpeta.listFiles();
		Map<Double, Map<String, MovPreflop>> tablas = new HashMap<Double, Map<String, MovPreflop>>(files.length);
		for(int i = 0; i < files.length; i++) {
			Matcher m = pNombreFichero.matcher(files[i].getName());
			m.find();
			double bbs = Double.parseDouble(m.group(1));
			tablas.put(bbs, lector.getTabla(files[i]));
		}
		return tablas;
	}
	
	private List<Double> generarListaOrdenadaBBs(Map<Double, Map<String, MovPreflop>> tablas){
		TreeSet<Double> sortedSet = new TreeSet(tablas.keySet());
		return new ArrayList<Double>(sortedSet);
	}
	
	public MovPreflop getMovPreflop(String combo, double ciegasEfectivas){
		double ciegasAprox = closest(ciegasEfectivas);
		return tablas.get(ciegasAprox).get(combo);
	}
	
	public double closest(double of) {
	    double min = Double.MAX_VALUE;
	    double closest = of;

	    for (double v : listaBBsTablasOrdenadas) {
	        final double diff = Math.abs(v - of);

	        if (diff < min) {
	            min = diff;
	            closest = v;
	        }
	    }

	    return closest;
	}
	
	/*public static void main(String[] args){
		File dir = new File("Rangos/3H/0");
		ILectorTablaPreflop lector = new LectorTablaPreflopListaTXT();
		TablasPreflop tp = new TablasPreflop(3,0, dir, lector);
		int a =0;
	}*/
}
