package logica;

import constantes.Palo;

public class Carta implements Comparable<Carta>{	
	private String text; //aqui guardamos en string la carta en notacion shortEnglish Ad/5s/3h
	//J=11, Q=12, K=13, As=14
	private int numero;
	private Palo palo;
	
	/** Se le pasa en notacion: numero+h/s/d/c (ej: 2c, Ah, ...) **/
	public Carta(String text){
		char num = text.charAt(0);
		int numero = 0;
		if(num == 'T') numero = 10;
		else if(num == 'J') numero = 11;
		else if(num == 'Q') numero = 12;
		else if(num == 'K') numero = 13;
		else if(num == 'A') numero = 14;
		else numero = Integer.parseInt(num+"");
		
		Character paloChar = text.charAt(1);
		
		this.text = text;
		this.numero = numero;
		this.palo = Palo.englishAbrevToPalo(paloChar);
	}
	
	/*// Es mandatorio pasarle bien el *text*, aquneu le pasemos tmb el numero y el palo	
	public Carta(String text, int numero, Palo palo) {
		this.text = text;
		this.numero = numero;
		this.palo = palo;
	}*/

	public String getText(){
		return text;
	}
	
	public int getNumero() {
		return numero;
	}

	public Palo getPalo() {
		return palo;
	}
	
	public String toString(){
		return text;
	}

	@Override
	public boolean equals(Object obj) {
		if(((Carta)obj).getText().equals(text)) return true;
		return false;
	}
	
	@Override
    public int compareTo(Carta o) {
        if (numero < o.getNumero()) {
            return -1;
        }
        if (numero > o.getNumero()) {
            return 1;
        }
        return 0;
    }

}
