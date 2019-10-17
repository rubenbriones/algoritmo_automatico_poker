package scraping;

import logica.Accion;
import logica.Hand;
import logica.IHand;

public interface IScraper {
	
	public boolean esNuestroTurnoConNuevaInfo(); //Devuelve TRUE si es nuestro turno y aparte hay info nueva que analizar
	public long getTiempoDeEspera(); //devuelve el tiempo a esperar hasta comprobar d enuevo si nos ha llegado el turno
	
	//Cuando llamemos a este metodo ya tiene que haber info nueva recopilada
	public Hand parseInfoAndGetHandActual();
	
	public void ejecutarAccion(Accion accion); //Tiene que hacer los clicks de raton o hotkeys necesarias para ejecutar la Accion.
}
