package logica;

import java.util.List;
import java.util.Map;

import constantes.AccionEnum;
import constantes.Calle;
import constantes.Posicion;
import constantes.SituacionPreflop;

public class GeneradorSituacionesPreflop {
	
	public final static double UMBRAL_ALLIN = 0.33;
	
	public static SituacionPreflop getSituacionPreflop(int numberOfPlayers, int heroPosition, double stackEfectivo,
			Map<Integer, Map<Calle, List<Accion>>> acciones){
		switch(numberOfPlayers){
			case 2:
				if(heroPosition == Posicion.SB) return SituacionPreflop.UNONPENED;
				
				//position == Posicion.BB
				Accion accSB2H = acciones.get(Posicion.SB).get(Calle.PREFLOP).get(0);
				if(accSB2H.getAccion() == AccionEnum.CALL) return SituacionPreflop.BB_vs_SB_LIMP;
				if(accSB2H.getAccion() == AccionEnum.RAISE || accSB2H.getAccion() == AccionEnum.ALLIN){
					if(accSB2H.getCantidad() >= stackEfectivo*UMBRAL_ALLIN)
						return SituacionPreflop.BB_vs_SB_ALLIN;
					else
						return SituacionPreflop.BB_vs_SB_MR;
				}
				
				break; //es inutil porque siempre va a haber un return antes del break, pero por aseguranos.
				
			case 3:
				if(heroPosition == Posicion.BU) return SituacionPreflop.UNONPENED;
				
				Accion accBU3H = acciones.get(Posicion.BU).get(Calle.PREFLOP).get(0);				
				if(heroPosition == Posicion.SB){
					if(accBU3H.getAccion() == AccionEnum.FOLD) return SituacionPreflop.UNONPENED;
					if(accBU3H.getAccion() == AccionEnum.CALL) return SituacionPreflop._3H_SB_vs_BU_LIMP;
					if(accBU3H.getAccion() == AccionEnum.RAISE || accBU3H.getAccion() == AccionEnum.ALLIN){
						if(accBU3H.getCantidad() >= stackEfectivo*UMBRAL_ALLIN)
							return SituacionPreflop._3H_SB_vs_BU_ALLIN;
						else
							return SituacionPreflop._3H_SB_vs_BU_MR;
					}
				}
				
				Accion accSB3H = acciones.get(Posicion.SB).get(Calle.PREFLOP).get(0);
				if(heroPosition == Posicion.BB){
					if(accBU3H.getAccion() == AccionEnum.FOLD){
						if(accSB3H.getAccion() == AccionEnum.CALL) return SituacionPreflop.BB_vs_SB_LIMP;
						if(accSB3H.getAccion() == AccionEnum.RAISE || accSB3H.getAccion() == AccionEnum.ALLIN){
							if(accSB3H.getCantidad() >= stackEfectivo*UMBRAL_ALLIN)
								return SituacionPreflop.BB_vs_SB_ALLIN;
							else
								return SituacionPreflop.BB_vs_SB_MR;
						}
					}
					if(accBU3H.getAccion() == AccionEnum.CALL){
						if(accSB3H.getAccion() == AccionEnum.CALL || accSB3H.getAccion() == AccionEnum.FOLD)
							return SituacionPreflop._3H_BB_vs_BU_LIMP;
						if(accSB3H.getAccion() == AccionEnum.RAISE || accSB3H.getAccion() == AccionEnum.ALLIN){
							if(accSB3H.getCantidad() >= stackEfectivo*UMBRAL_ALLIN)
								//zzz
								return SituacionPreflop._3H_BB_vs_BU_ALLIN; /** ESTO HABRIA QUE CHECHEARLO, VER SI IGUALARIA CON EL MISMO RANGO **/
							else
								return SituacionPreflop._3H_BB_vs_BU_ALLIN; /** ESTO HABRIA QUE CHECHEARLO, VER SI IGUALARIA CON EL MISMO RANGO **/
						}
					}
					//zzz - revisar que en este metodo estan todas las opciones posibles Y SOBRETODO SI DEBERIA CAMBIAR EL RANGO DE ALGUNA SITUACION QUE HE AGRUPADO CON OTRA SITUACION DE MALA MANERA
					if(accBU3H.getAccion() == AccionEnum.RAISE || accBU3H.getAccion() == AccionEnum.ALLIN){
						if(accSB3H.getAccion() == AccionEnum.FOLD || accSB3H.getAccion() == AccionEnum.CALL){
							if(accBU3H.getCantidad() >= stackEfectivo*UMBRAL_ALLIN)
								return SituacionPreflop._3H_BB_vs_BU_ALLIN;
							else
								return SituacionPreflop._3H_BB_vs_BU_MR;
						}
						if(accSB3H.getAccion() == AccionEnum.RAISE || accSB3H.getAccion() == AccionEnum.ALLIN){
							//zzz
							return SituacionPreflop._3H_BB_vs_BU_ALLIN; /** ESTO HABRIA QUE CHECHEARLO, VER SI IGUALARIA CON EL MISMO RANGO **/
						}
					}					
				}
				
				break; //es inutil porque siempre va a haber un return antes del break, pero por aseguranos.
				
		}
		return null;
	}
}
