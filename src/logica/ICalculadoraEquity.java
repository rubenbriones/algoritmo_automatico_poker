package logica;

import java.util.List;

public interface ICalculadoraEquity {
	
	/*
	 * Utiliza solo manos concretas que ya son posibles, asi que este metodo no tiene en cuenta las boardcards
	 * a la hora de eliminar manos imposibles, pues ya estan eliminadas.
	 */
	public ResCalculadoraEquity calc(Mano mano, List<Mano> manos, List<Carta> boardCards);
	
	/*
	 * AQUI LE PASO UN RANGO QUE PUEDE TENER COMBOS IMPOSIBES, Y QUE EN ESTE PROPIO METODO DEBE ENCARGARSE DE QUITAR.
	 * EN @deadcards NO HAY QUE PASARLE LAS HOLE CARDS DE @mano EL ALGORITMO YA SE ENCARGA DE TENER ESO EN CUENTA.
	 * Calcula el porcentaje de victoria de una mano concreta (con palos) vs un rango.
	 * Obviamente para saber el porcentaje de victoria del rango es el complementario a 100 de lo devuelto.
	 */
	public ResCalculadoraEquity calc(Mano mano, Rango rango, List<Carta> boardCards, List<Carta> deadCards);
	
	//Estas 2 funciones no deberian ser obligatorias, pero van a ser necesarias para hacer la de vs Rango,
	//asi que las pongo tambien, y las tenemos separadas y vamos por partes.
	//AQUI LE PASO UN RANGO QUE PUEDE TENER COMBOS IMPOSIBES, Y QUE EN ESTE PROPIO METODO DEBE ENCARGARSE DE QUITAR.
	public ResCalculadoraEquity calc(Mano mano, Combo combo, List<Carta> boardCards, List<Carta> deadCards);
	//OBVIAMENTE EN ESTE METODO LA MANO QUE LE PASAMOS YA NO ES UNA MANO IMPOSINLE.
	public double calc(Mano mano1, Mano mano2, List<Carta> boardCards, List<Carta> deadCards);
}
