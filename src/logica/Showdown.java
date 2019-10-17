package logica;

import java.util.List;

public class Showdown {
	
	private String player;
	private Mano mano;
	private List<Carta> jugada; //esta es la mejor mano que pudo formar al showdown con 5 cartas
	private String jugadaString;
	
	public Showdown(String player, Mano mano, List<Carta> jugada, String jugadaString) {
		this.player = player;
		this.mano = mano;
		this.jugada = jugada;
		this.jugadaString = jugadaString;
	}

	public String getPlayer() {
		return player;
	}

	public Mano getMano() {
		return mano;
	}

	public List<Carta> getJugada() {
		return jugada;
	}

	public String getJugadaString() {
		return jugadaString;
	}
	
	
	
}
