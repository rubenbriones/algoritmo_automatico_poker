package logica;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import persistencia.ILectorTablaPreflop;
import persistencia.LectorTablaPreflopListaTXT;
import constantes.SituacionPreflop;

public class AlmacenTablasPreflop {
	
	private static AlmacenTablasPreflop instancia;
	
	public static AlmacenTablasPreflop getInstancia(){
		if(instancia == null) instancia = new AlmacenTablasPreflop();
		return instancia;
	}
	
	private final File dirRangos = new File("Rangos\\");
	private final ILectorTablaPreflop lector = new LectorTablaPreflopListaTXT();
	
	//El string es del tipo 2H9 (head up, pos 9), o 3H9 (3max, pos 0)
	private Map<String, Map<SituacionPreflop, TablasPreflop>> almacenTablas;
	
	private AlmacenTablasPreflop(){
		almacenTablas = new HashMap<String, Map<SituacionPreflop, TablasPreflop>>();
	}
	
	public MovPreflop getMovPreflop(String combo, int numberOfPlayers, int posicion, 
			SituacionPreflop situacion, double ciegasEfectivas){
		TablasPreflop tablas = almacenTablas.get(numberOfPlayers+"H"+posicion).get(situacion);
		if(tablas == null){
			cargarTodasLasTablas(numberOfPlayers, posicion, situacion);
			tablas = almacenTablas.get(numberOfPlayers+"H"+posicion).get(situacion);
		}
		return tablas.getMovPreflop(combo, ciegasEfectivas);
	}
	
	/*public void cargarTodasLasTablas(int numberOfPlayers){		
		int[] posiciones = new int[numberOfPlayers];
		if(numberOfPlayers == 2){
			posiciones[0] = 9;
			posiciones[0] = 8;
		}
		else{
			posiciones[0] = 0;
			int pos = 9;
			for(int i=1; i<posiciones.length; i++){
				posiciones[i] = pos;
				pos--;
			}
		}
		
		for(int posicion : posiciones){
			cargarTodasLasTablas(numberOfPlayers, posicion);
		}
	}
	
	private void cargarTodasLasTablas(int numberOfPlayers, int posicion){
		File dir = new File(dirRangos, numberOfPlayers+"H\\"+posicion);
		
		//FALTA POR HACER, HABRIA QUE IR LEYENDO TODAS LAS SITUACIONES
		
		//TablasPreflop tp = new TablasPreflop(3,0, dir, lector);		
	}*/
	
	private void cargarTodasLasTablas(int numberOfPlayers, int posicion, SituacionPreflop situacion){
		File dir = new File(dirRangos, numberOfPlayers+"H\\"+posicion+"\\"+situacion.getRuta());
		TablasPreflop tp = new TablasPreflop(numberOfPlayers, posicion, dir, lector);
		
		String key = numberOfPlayers+"H"+posicion;
		if(almacenTablas.get(key) == null)
			almacenTablas.put(key, new HashMap<SituacionPreflop, TablasPreflop>());
		almacenTablas.get(key).put(situacion, tp);
	}
	
}
