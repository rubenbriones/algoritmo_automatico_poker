package logica;

import java.util.ArrayList;
import java.util.List;

public class Mano {

	private Carta carta1;
	private Carta carta2;
	
	public Mano(Carta carta1, Carta carta2) {
		this.carta1 = carta1;
		this.carta2 = carta2;
	}
	
	//Texto en el formato: Ad9d
	public Mano(String text){
		this.carta1 = new Carta(text.substring(0,2));
		this.carta2 = new Carta(text.substring(2,4));
	}
	
	public Carta getCarta1() {
		return carta1;
	}

	public Carta getCarta2() {
		return carta2;
	}
	
	public List<Carta> getListaCartas(){
		List<Carta> lista = new ArrayList<Carta>(2);
		lista.add(carta1);
		lista.add(carta2);
		return lista;
	}

	public Carta getCartaAlta(){
		return carta1.getNumero() > carta2.getNumero() ? carta1 : carta2;
	}
	
	public boolean isPocket(){
		return carta1.getNumero() == carta2.getNumero();
	}
	
	public String toString(){
		return carta1.toString()+carta2.toString();
	}
	
	public Combo getCombo(){
		char c1 = carta1.getText().charAt(0);
		char c2 = carta2.getText().charAt(0);
		if(c1 == c2) return new Combo(c1+c2+"");
		if(carta1.getPalo() == carta2.getPalo())
			return new Combo(c1+c2+"s");
		else
			return new Combo(c1+c2+"o");
	}
}
