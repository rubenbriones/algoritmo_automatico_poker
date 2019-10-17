package logica;

import constantes.Calle;

public interface IAlgoritmo {
	//Analiza la mano que le pasamos como parametro en la hand.getCalleActual()
	//Siempre vamos a llamar a este metodo cuando sea nuestro turno, por tanto siempre vamos
	//a querer analizar la decision a tomar en la calleActual.
	public  Accion analyze(Hand hand);
	
	//Metemos este metodo para avisar al algoritmo cuando ha finalizado una mano y que resetee las variables orpotunas para la siguiente mano.
	public void resetFinDeMano();
}
