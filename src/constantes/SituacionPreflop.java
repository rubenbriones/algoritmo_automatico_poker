package constantes;

/**
 * Son todas las cituaciones Preflop en las que nos podemos encontrar en cuanto nos llegue nuestro primer turno.
 * NO estan las situaciones que se pueden dar preflop si nos toca actuar una segunda vez preflop. Una vez nos ha
 * llegado el primer turno prelfop, decidimos ya cuales serian nuestras decisiones posteriores si nos enfrentamos
 * a raises de otros jugadores. Esta info se guarda en MovPreflop.
 */
public enum SituacionPreflop {
	UNONPENED(""), //esta se usa para cuando nos llega la mano limpia, las manos de este tipo siempre estaran en el directorio raiz
	_3H_SB_vs_BU_LIMP("vs 0 LIMP"),
	_3H_SB_vs_BU_MR("vs 0 MR"),
	_3H_SB_vs_BU_ALLIN("vs 0 ALLIN"),
	_3H_BB_vs_BU_LIMP("vs 0 LIMP"),
	_3H_BB_vs_BU_MR("vs 0 MR"),
	_3H_BB_vs_BU_ALLIN("vs 0 ALLIN"),
	BB_vs_SB_LIMP("vs 9 LIMP"),
	BB_vs_SB_MR("vs 9 MR"),
	BB_vs_SB_ALLIN("vs 9 ALLIN");
	
	//vs_EP_MR
	//vs_MP_MR
	//vs_4bet
	//vs_EP_MR_y_call
	
	private final String ruta;
	
	SituacionPreflop(String ruta){
		this.ruta = ruta;
	}
	
	public String getRuta(){
		return this.ruta;
	}
}
