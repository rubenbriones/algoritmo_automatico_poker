package logica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jugador {
	
	private String nick;
	private HUD hud;
	private List<Long> hands; //Lista con todos los IDs de las hands que tenemos recopiladas suyas.
	
	public Jugador(String nick) {
		this.nick = nick;
		this.hud = new HUD();
		this.hands = new ArrayList<Long>();
	}
	
	
}
