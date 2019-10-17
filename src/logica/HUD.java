package logica;

import java.util.HashMap;
import java.util.Map;

import stats.BBvsBU_FoldFCB;
import stats.BU3H_VPIP;
import stats.Stat;

public class HUD {

	private Map<String, Stat> stats;
	
	//En el constructor del hud tengo que crear una instancia de todas las stats.
	//Cambiar tamaño del HashMap cada vez que añada una nueva.
	public HUD(){
		this.stats = new HashMap<String, Stat>(2);
		
		stats.put("BBvsBU_FoldFCB", new BBvsBU_FoldFCB());
		stats.put("BU3H_VPIP", new BU3H_VPIP());		
		
		/** nombres que he ido dando a ls stats en los algoritmos pero que todavia no estan creadas:
		 * (para luego darles el mismo nombre al crearlas)
		 * 
		 * BB2H_ROL -> el rol general tanto AI como NAI (no voy a diferenciarlo), y si que tiene tramos
		 * BB2H_VPIP -> tiene tramos
		 * BB2H_3bet -> tiene tramos, no diferenciamos entre AI y NAI
		 * 
		 * small blind
		 * SB2H_4bet -> tiene tramos
		 * SB2H_PFR -> tiene tramos, no diferenciamos por tamaños, salvo q sea allin claro
		 * SB2H_limp -> tiene tramos
		 * SB2H_OS -> tiene tramos
		 * SB2H_VPIP -> tiene tramos, es la suma de PFR+limp+OS
		 * SB2H_fold3betAI -> tiene tramos
		 * SB2H_fold3betNAI -> tiene tramos
		 * SB2H_limpFoldAI -> tiene tramos
		 * SB2H_limpFoldNAI -> tiene tramos
		 */
	}
	
	public double getReadlessValue(String stat){
		return stats.get(stat).getReadlessValue();
	}
	public double getReadlessValue(String stat, double ciegas){
		return stats.get(stat).getReadlessValue(ciegas);
	}
}
