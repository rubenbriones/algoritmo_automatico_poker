package logica;

public interface ISelectorManos {
	/*
	 * Le pasamos el porcentaje de las ejores manos que queremos seleccionar, por ejemplo el VPIP de BB en HU,
	 * y el porcentaje de manos que excluimos por arriba, es decir, las que 3betearia en BB HU.
	 * El porcentaje exluido se resta del porcentaje total.
	 * De esta manera si le pasamos que coja el top 20%, y quite el top 3%, en realidad cogera el rango que va del 3% al 20% (es decir, cogeria solo un 17% de las manos).
	 */
	public Rango getMejoresManos(double porcentaje, double porcentajeExcluidoPorArriba);
	
	/*
	 * Igual que el metodo anterior, solo que ademas incluye todas las pockets (si es que no eestaban incluidas
	 * ya en el porcentaje que le pasamos como parametro.
	 */
	//public Rango getMejoresManosConPockets(double porcentaje, double porcentajeExcluidoPorArriba);
	
	/*
	 * Justo lo contrarrio que el metodo anterior, excluye las pockets del rango final
	 */
	public Rango getMejoresManosSinPockets(double porcentaje, double porcentajeExcluidoPorArriba);
	
	/*
	 * Nos devuelve un rango polarizado cogiendo el X porcentaje de las menores manos y el Y porcentaje de las peores.
	 * Aparte podemos decirle que no tenga en cuenta cierto porcentaje de las mejores manos (por ejemplo que el top 1% (AA y KK) no las tenga en cuenta,
	 * de esta manera si le pasamos que coja el top 20%, y quite el top 3%, en realidad cogera el rango que va del 3% al 20% (es decir, cogeria solo un 17% de las manos).
	 * 
	 * ¡¡¡¡¡pero por ARRRIBA si se hace igual que el getMejoresManos()!!!!
	 * PERO POR ABAJO NO SE HACE IGUAL: si le decimos que coja el Bottom 20%, pero que excluya el Bottom 3%, nos cogera el rango que va desd el 
	 * 									3% peor, al 23% peor (es decir, en total si que nos devuelve un 20% de combos y no un 17% como en el caso del top).
	 * 
	 * Esto es porque caudno se es SB tiene sentido no querer abrir el bottom, bottom, range y foldearlo directaemnte sin polarizarlo. Pero el Top, top, range
	 * siempre lo vamos a querer abrir si o si.
	 */
	public Rango getRangoPolarizado(double porcentajeTop, double porcentajeExluidoTop, double porcentajeBottom, double porcentajeExcuidoBottom);
}
