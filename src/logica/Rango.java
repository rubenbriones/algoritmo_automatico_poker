package logica;

import java.util.ArrayList;
import java.util.List;

public class Rango {
	
	//Tiene que estar siempre sin combos repetidos, y ordenados de mayor a menor fuerza.
	private List<Combo> combos;

	public Rango(){
		this.combos = new ArrayList<Combo>();
	}
	public Rango(List<Combo> combos) {
		this.combos = combos;
	}

	public List<Combo> getCombos() {
		return combos;
	}
	
	public int getNumCombos(){
		return combos.size();
	}
	
	//NO INSERTAR COMBOS REPETIDOS, AQUI NO SE COMPRUEBA NADA.
	//E INSERTARLOS EN ORDEN DE MAYOR FUERZA A MENOR FUERZA, PARA QUE ESTE SIEMPRE ORDENADO EL RANGO
	//COMENTO ESTE METODO YA QUE NUNCA SE UTILIZA, NI DEBERIA UTILIZARSE.
	/*public void addCombo(Combo c){
		combos.add(c);
	}*/
	
	/*public Rango incluirPockets(){
		List<Combo> pocketsIncluidas = new ArrayList<Combo>(13);
		for(Combo c : combos){
			if(c.isPocket()) pocketsIncluidas.add(c);
		}
		List<Combo> pockets = Rango.generarRangoPockets().getCombos();
		for(Combo c : pockets){
			if(!pocketsIncluidas.contains(c)) this.combos.add(c);
		}	
		return this;
	}*/
	
	public Rango excluirPockets(){
		for(int i=0; i<combos.size(); i++){
			if(combos.get(i).isPocket()) combos.remove(i);
		}
		return this;
	}

	public int getCombinacionesTotales(){
		int res = 0;
		for(Combo c : combos) res += c.getCombinaciones();
		return res;
	}
	
	public Rango eliminarTop(double porcentaje){
		int combinacionesTotales = getCombinacionesTotales();
		int combinacionesAeliminar = (int)(combinacionesTotales*(porcentaje/100.0));
		for(int i=0; i<combos.size() && combinacionesAeliminar>0; i++){
			int aux = combos.get(i).getCombinaciones();
			if(combinacionesAeliminar-aux>=0 || Math.abs(combinacionesAeliminar-aux)<combinacionesAeliminar) combos.remove(i);
		}
		return this;
	}
	
	public Rango eliminarBottom(double porcentaje){
		int combinacionesTotales = getCombinacionesTotales();
		int combinacionesAeliminar = (int)(combinacionesTotales*(porcentaje/100.0));
		for(int i=combos.size()-1; i>=0 && combinacionesAeliminar>0; i++){
			int aux = combos.get(i).getCombinaciones();
			if(combinacionesAeliminar-aux>=0 || Math.abs(combinacionesAeliminar-aux)<combinacionesAeliminar) combos.remove(i);
		}
		return this;
	}
	
	public String toString(){
		String res = "";
		for(Combo c : combos) res+=c.getCombo()+",";
		return res;
	}
	
	
	/** RANGOS PREDEFINIDOS ESPECIALES **/
	public static Rango generarRangoPockets(){
		String[] combos  = {"AA","KK","QQ","JJ","TT","99","88","77","66","55","44","33","22"};
		List<Combo> pockets = new ArrayList<Combo>(13);
		for (int i = 0; i < combos.length; i++) {
			pockets.add(new Combo(combos[i]));
		}
		return new Rango(pockets);
	}
}
