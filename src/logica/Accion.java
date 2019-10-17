package logica;

import constantes.AccionEnum;
import constantes.Posicion;

public class Accion {

	private String player;
	private int posicion;
	private AccionEnum accion; //un allin no se considera un bet normal, hay que estar diferenciandolo todo el rato
	private double cantidad; //fichas exactas (si es un CHECK o FOLD esto estara a null)

	
	public Accion(String player, int pos, AccionEnum accion) {
		this.player = player;
		this.posicion = pos;
		this.accion = accion;
	}
	public Accion(String player, int pos, AccionEnum accion, double cantidad) {
		this.player = player;
		this.posicion = pos;
		this.accion = accion;
		this.cantidad = cantidad;
	}
	
	//Estos dos constructores se utilizan para crear las acciones que tiene que efectuar el robot del algoritmo
	//pero que son acciones que no vamos a guardar en la IHand (se guardaran despues al leerlas del chat/historial).
	public Accion(AccionEnum accion) {
		this.accion = accion;
	}
	public Accion(AccionEnum accion, double cantidad) {
		this.accion = accion;
		this.cantidad = cantidad;
	}
	

	public AccionEnum getAccion() {
		return accion;
	}
	
	public double getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public int getPosicion() {
		return posicion;
	}
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
		
}
