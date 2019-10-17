package logica;

import java.util.List;

import constantes.Jugada;

public class ResJugadas {
	
	private Mano mano;
	private List<Carta> boardMasMano; //siempre tendra 5 cartas y estaran ordenadas de mayor a menor
	private List<Carta> board; //estaran ordenadas de mayor a menor.
	
	private Jugada jugadaMaxima;
	private List<Jugada> proyectos;
	private List<Jugada> backdoors;
	
	public ResJugadas(Mano mano, List<Carta> boardMasMano, List<Carta> board,
			Jugada jugadaMaxima, List<Jugada> proyectos, List<Jugada> backdoors) {
		this.mano = mano;
		this.boardMasMano = boardMasMano;
		this.board = board;
		this.jugadaMaxima = jugadaMaxima;
		this.proyectos = proyectos;
		this.backdoors = backdoors;
	}

	public Mano getMano() {
		return mano;
	}

	public List<Carta> getBoardMasMano() {
		return boardMasMano;
	}

	public List<Carta> getBoard() {
		return board;
	}

	public Jugada getJugadaMaxima() {
		return jugadaMaxima;
	}

	public List<Jugada> getProyectos() {
		return proyectos;
	}

	public List<Jugada> getBackdoors() {
		return backdoors;
	}
	
	//Devuelve cuales de las 2 cartas de Mano se han utilizado para formar la jugada
	public List<Carta> getCartasDeLaManoUtilizadas(){
		List<Carta> cartas = mano.getListaCartas();
		cartas.retainAll(boardMasMano);
		return cartas;
	}
}
