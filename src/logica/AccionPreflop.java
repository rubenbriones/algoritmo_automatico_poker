package logica;

public interface AccionPreflop {
	/**
	 * El argumento tamanyoUltimoRaiseFichas es opcional, pues si somos los primeros en actuar será 0 pues no pudo haber raise anterior.
	 */
	public Accion getAccionFinal(double ciegaGrande, double tamanyoUltimoRaiseFichas, double ciegasEfectivas);
	public String toString();
}
