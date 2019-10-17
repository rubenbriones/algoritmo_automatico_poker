package logica;

import java.util.List;

import constantes.AccionEnum;
import constantes.Calle;
import constantes.Jugada;
import constantes.Posicion;
import constantes.SituacionPreflop;
import decididores.IDecididorFlop;
import decididores.IDecididorRiver;
import decididores.IDecididorTurn;
import decididores.IEliminadorCombosTrasFlop;
import decididores.IEliminadorCombosTrasTurn;

/**
 * Este es un algoritmo solo pensado para el HU en los spins, no para el 3H.
 * Y Solo puede llegarnos el turno 2 VECES en una misma calle, salvo que nuestro primer movimiento en esa calle haya sido CHECK, en cuyo caso nos puede llegar 3 veces. Anque preflop solo puede 2 veces sea como sea.
 * Y la primera vez que nos llegue el turno (y NO hagamos CHECK) tenemos que tener ya decidio que vamos a hacer si nos llega
 * el turno de nuevo (nos enfrentamos a un raise, ya sea AI o NAI).
 * NO DEBEMOS LLAMAR AL ALGORITMO UNA VEZ YA SE HA HECHO ALLIN Y NO HAY NADA QUE DECIDIR.
 * 
 * NOTAS:
 * 		//preflop
 * 		-SUPONEMOS QUE NUESTROS RIVALES NO JUEGAN CON RANGOS POLARIZADOS
 * 		-SIEMPRE RAISEAN LAS MAJORES MANOS Y LIMPEAN LAS PEORES (Y FOLDEAN LAS PEORES PEORES), INCLUSO SUPONEMOS QUE RAISEAN AA,KK,etc con 6,5,4,etc BBs.
 * 		-NUNCA HACEMOS RE-RAISE PREFLOP QUE NO SEA ALLIN, CON AA-KK Y ESO O NOS QUEDAMOS AL CALL O RE-RAISEAMOS ALLIN
 * 		-ASUMIMOS 0 RANGO DE LIMP-ROL
 * 		
 * 		//postflop
 * 		-dependera de los IDecididorPostflop que le inyectemos para tomar las deciciones postflop, 
 * 		este algoritmo no hace nada postflop por si solo, todo depende de lo que le inyectemos.
 * 
 * @author Ruben
 *
 */
public class AlgoritmoSpinsHUvsReadless extends Algoritmo{
	
	private IDecididorFlop decididorFlop;
	private IDecididorTurn decididorTurn;
	private IDecididorRiver decididorRiver;
	private IEliminadorCombosTrasFlop eliminadorTrasFlop;
	private IEliminadorCombosTrasTurn eliminadorTrasTurn;
	private HUD hudReadless;

	public AlgoritmoSpinsHUvsReadless(ISelectorManos selector, ICalculadoraEquity calc,
			IDecididorFlop decididorFlop, IDecididorTurn decididorTurn, IDecididorRiver decididorRiver,
			IEliminadorCombosTrasFlop eliminadorTrasFlop, IEliminadorCombosTrasTurn eliminadorTrasTurn) {
		super(selector, calc);
		this.decididorFlop = decididorFlop;
		this.decididorTurn = decididorTurn;
		this.decididorRiver = decididorRiver;
		this.eliminadorTrasFlop = eliminadorTrasFlop;
		this.eliminadorTrasTurn = eliminadorTrasTurn;
		this.hudReadless = new HUD();
	}

	private double ciegasEfectivasPreflop;
	private MovPreflop movPreflop;
	private Rango rangoRival;
	private RangoConcreto rangoConcretoRival; //este sera el que utilicemos POSTFLOP
	
	private AccionPostflop accFlop;
	private AccionPostflop accTurn;
	private AccionPostflop accRiver;
	
	//dejo fuera estas variables para que en algunos casos no sea necesario calcularlas dos veces, sino solo una.
	//TENGO QUE ASEGURARME DE re-CALCULARLAS SIEMPRE LA PRIMERA VEZ QUE ME LLEGA EL TURNO EN CADA CALLE.
	private ResCalculadoraEquity resCalc;
	private ResJugadas jugadasHero;
	private ResJugadasRango jugadasRival;
	
	@Override
	public Accion analyze(Hand hand) {
		switch(hand.getCalleActual()){
			case PREFLOP:
			{
				if(hand.getNumTurnoCalleActual() == 1){
					double stackEfectivo = hand.getStackEfectivo(hand.getCalleActual());
					SituacionPreflop situacion = GeneradorSituacionesPreflop.getSituacionPreflop(hand.getNumberOfPlayers(), hand.getHeroPosition(), stackEfectivo, hand.getActions());
					double ciegasEfectivas = stackEfectivo/hand.getBB();
					this.ciegasEfectivasPreflop = ciegasEfectivas;
					movPreflop = AlmacenTablasPreflop.getInstancia().getMovPreflop(hand.getHeroCards().getCombo().getCombo(), hand.getNumberOfPlayers(), hand.getHeroPosition(), situacion, ciegasEfectivas);
					return movPreflop.getPrimeraAccion().getAccionFinal(hand.getBB(), -1, ciegasEfectivas);
				}
				else if(hand.getNumTurnoCalleActual() == 2){ //se podría poner un simple else tmb, pues solo nos puede llegar 2 veces el turno con este algoritmo
					//hay que ver las acciones de los otros jugadores y ver si estamos vs un raise AI o NAIN
					//Para ello recuperaremos nuestra primera accion en esta calle y veremos cuales han sido
					//las acciones de nuestros rivales posteriores a esta.
					Accion ultimaAccHero = hand.getPrimeraAccionHeroEn(Calle.PREFLOP);
					Accion accDelRival = hand.getAccionesPosterioresA(ultimaAccHero).get(0); //al ser HU solo hay 1 accion posterior
					if(accDelRival.getAccion() == AccionEnum.ALLIN)
						return movPreflop.getAccionVSraiseAI().getAccionFinal(-1,-1,-1);
					//Si el tio nos raisea pero no allin, pero si una parte muy importante de su stack (lo que nosotros ya consideramos como allin),
					//lo que hacemos es hacerle el allin preflop nosotros, en vez de callear, ya que no vamos a foldear luego postflpo.
					//AQUI NO COMPROBAMOS QUE SEA UN RAISE LA ACCION, YA QUE SI NO ES ALLIN (caso anterior) SOLO PUEDE SER RAISE, sino no nos hubierra llegado el turno de nuevo.
					if(accDelRival.getCantidad() >= hand.getStacksPreflop().get(accDelRival.getPlayer())*GeneradorSituacionesPreflop.UMBRAL_ALLIN){
						if(movPreflop.getAccionVSraiseAI().getAccionFinal(-1,-1,-1).getAccion() == AccionEnum.CALL)
							return new AccionPreflopAllin().getAccionFinal(-1,-1,-1); 
						else 
							return movPreflop.getAccionVSraiseAI().getAccionFinal(-1,-1,-1); //esto solo puede ser un call o fold
					}
					else{ //Aqui estamos ante un RAISE Non-allin del rival					
						return movPreflop.getAccionVSraiseNAI().getAccionFinal(hand.getBB(), accDelRival.getCantidad(), -1); //seimpre sera un call/fold o ALLIN, y para eso no se necesitan las ciegasEfectivas
					}
				}
				else System.err.println("Nos ha llegado el turno una 3vez en PREFLOP, y no hemos respondido nada. O en el 1o2 turno hemos ido por una rama de ejecucion que no ha hecho return.");
				break;
			}
				
			case FLOP:
			{
				int posHero = hand.getHeroPosition();
				int posRival = posHero == Posicion.SB ? Posicion.BB : Posicion.SB;
				List<Accion> accionesRival = hand.getActions().get(posRival).get(Calle.FLOP);
				
				if(hand.getNumTurnoCalleActual() == 1){					
					//Seteamos el rango del rival que llega al flop
					setRangoRivalTrasPreflop(hand, posHero, posRival);
					//Y lo traducimos en manos reales posibles
					rangoConcretoRival = new RangoConcreto(rangoRival.getCombos(), hand.getHeroCards(), hand.getFlop());
					
					//Calculos generales
					ResCalculadoraEquity resCalc = calc.calc(hand.getHeroCards(), rangoConcretoRival.getManos(), hand.getFlop());
					ResJugadas jugadasHero = Jugada.getJugadasEnFlop(hand.getHeroCards(), hand.getFlop());
					ResJugadasRango jugadasRival = Jugada.getJugadasRango(rangoConcretoRival, hand.getFlop());
					
					if(hand.getPosUltimoAgresorPreflop() == posHero){
						if(!hand.isHeroIPpostflop()){ //tenemos la iniciativa y somos los primeros en hablar, estamos OOP
							//decididor
							accFlop = decididorFlop.heroAgresorPreOOP(resCalc, jugadasHero, jugadasRival);
							return accFlop.getAccion();
						}
						else{
							if(accionesRival.get(0).getAccion() == AccionEnum.CHECK){//tenemos la inciativa y el rival o el rival ha checkeado, estamos IIP
								//decididor
								accFlop = decididorFlop.heroAgresorPreIIP(resCalc, jugadasHero, jugadasRival);
								return accFlop.getAccion();
							}
							//el rival nos ha DONKEADO, ya sea una bet normal o allin
							else if(accionesRival.get(0).getAccion() == AccionEnum.BET){
								//decididor
								accFlop = decididorFlop.heroVsDonkBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accFlop.getAccion();
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
								//decididor
								accFlop = decididorFlop.heroVsDonkAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accFlop.getAccion();
							}
						}
					}
					else{ //somos defensores
						if(!hand.isHeroIPpostflop()){ //podemos donkear o checkear
							//decididor
							accFlop = decididorFlop.heroDonkear(resCalc, jugadasHero, jugadasRival);
							return accFlop.getAccion();
						}
						else{ //el rival ha apostado, o ha checkeado siendo el agresor
							if(accionesRival.get(0).getAccion() == AccionEnum.CHECK){
								//decididor
								accFlop = decididorFlop.heroFlotar(resCalc, jugadasHero, jugadasRival);
								return accFlop.getAccion();
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.BET){
								//decididor
								accFlop = decididorFlop.heroDefensorIPvsContBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accFlop.getAccion();
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
								//decididor
								accFlop = decididorFlop.heroDefensorIPvsContAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accFlop.getAccion();
							}
						}
					}
				}
				else{ //TURNO>1
					//AQUI ESTAREMOS OOP SIEMPRE, Y CHECKEAMS EN TURNO=1.
					if(accFlop == null){ //esto es que hemos checkeado en flop y nos betea. No sabemos que hacer, hay que analizar
						if(hand.getPosUltimoAgresorPreflop() == hand.getHeroPosition()){ //esto es que checkeamos en flp siendo Agresores (roleamos pre) y nos ha beteado el rival
							//decididor
							accFlop = decididorFlop.heroVsFloat(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
							return accFlop.getAccion();
						}
						else{ //esto es que somos los defensores PRE y checkeamos en flop OOP, y nos cebetea,
							if(accionesRival.get(0).getAccion() == AccionEnum.BET){
								//decididor
								accFlop = decididorFlop.heroVsContBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accFlop.getAccion();
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
								//decididor
								accFlop = decididorFlop.heroVsContAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accFlop.getAccion();
							}
						}
					}
					//AQUI NOS ENFRENTAMOS A UN RAISE PERO YA SABEMOS QUE HACER SIEMPRE.
					else{ //esto es que nos enfrentamos a un raise. Puede ser nuestro turno 2 o 3, pero esto da igual.
						Accion ultimaAccRival = hand.getUltimaAccion();
						if(ultimaAccRival.getAccion() == AccionEnum.ALLIN)
							return accFlop.getAccionVsRaiseAI();
						else
							return accFlop.getAccionVsRaiseNAI(hand.getBB(), ultimaAccRival.getCantidad());
					}
				}
				System.err.println("Nos ha llegado el turno una 4vez en FLOP, y no hemos respondido nada. O en el 1o2o3 turno hemos ido por una rama de ejecucion que no ha hecho return.");
				break;
			}
			
			case TURN:
			{
				int posHero = hand.getHeroPosition();
				int posRival = posHero == Posicion.SB ? Posicion.BB : Posicion.SB;
				List<Accion> accionesRival = hand.getActions().get(posRival).get(Calle.TURN);
				
				if(hand.getNumTurnoCalleActual() == 1){					
					//Eliminamos las manos concretas del rival que no llegan al TURN, teniendo en cuenta todas las acciones que hubo en flop.
					eliminadorTrasFlop.eliminarCombosRangoConcretoTrasFlop(rangoConcretoRival, hand, posHero, posRival);
					
					//Calculos generales
					ResCalculadoraEquity resCalc = calc.calc(hand.getHeroCards(), rangoConcretoRival.getManos(), hand.getTurn());
					ResJugadas jugadasHero = Jugada.getJugadasEnTurn(hand.getHeroCards(), hand.getTurn());
					ResJugadasRango jugadasRival = Jugada.getJugadasRango(rangoConcretoRival, hand.getTurn());
					
					if(hand.getPosUltimoAgresorPostflop(Calle.FLOP) == posHero){ //fuimos los agresores en flop (sea como sea: cbeteando, donkeando, resubiendo, etc)
						if(!hand.isHeroIPpostflop()){ //tenemos la iniciativa y somos los primeros en hablar, estamos OOP
							//decididor
							accTurn = decididorTurn.heroAgresorFlopOOP(resCalc, jugadasHero, jugadasRival);
							return accTurn.getAccion();
						}
						else{
							if(accionesRival.get(0).getAccion() == AccionEnum.CHECK){//tenemos la inciativa y el rival o el rival ha checkeado, estamos IP
								//decididor
								accTurn = decididorTurn.heroAgresorFlopIP(resCalc, jugadasHero, jugadasRival);
								return accTurn.getAccion();
							}
							//el rival nos ha DONKEADO, ya sea una bet normal o allin
							else if(accionesRival.get(0).getAccion() == AccionEnum.BET){
								//decididor
								accTurn = decididorTurn.heroVsDonkBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accTurn.getAccion();
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
								//decididor
								accTurn = decididorTurn.heroVsDonkAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accTurn.getAccion();
							}
						}
					}
					else if(hand.getPosUltimoAgresorPostflop(Calle.FLOP) == posRival){ //somos defensores
						if(!hand.isHeroIPpostflop()){ //opdemos donkear o checkear
							//decididor
							accTurn = decididorTurn.heroDonkear(resCalc, jugadasHero, jugadasRival);
							return accTurn.getAccion();
						}
						else{ //el rival ha apostado, o ha checkeado siendo el agresor
							if(accionesRival.get(0).getAccion() == AccionEnum.CHECK){
								//decididor
								accTurn = decididorTurn.heroFlotar(resCalc, jugadasHero, jugadasRival);
								return accTurn.getAccion();
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.BET){
								//decididor
								accTurn = decididorTurn.heroDefensorFlopIPvsContBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accTurn.getAccion();
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
								//decididor
								accTurn = decididorTurn.heroDefensorFlopIPvsContAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accTurn.getAccion();
							}
						}
					}
					/** TODO ESTE ELSE LO PODRIA QUITAR SI QUISIERA ACTUAR IGUAL EN TURN INDEPENDIENTEMENTE DE SI EL FLOP HA SIDO CHECK-CHECK
					 *  YA QUE ESTAN LAS MISMAS SITUACIONES QUE ARRIBA, PERO HABIENDO SIDO X-X EN FLOP. O TAMBIEN PUEDO HACER QUE LOS DECIDIDORES SEAN IGUALES**/
					else{ // hand.getPosUltimoAgresorPostflop(Calle.FLOP) == -1 (en flop fue CHECK-CHECK)
						if(hand.getPosUltimoAgresorGlobalCallesAnteriores() == posHero){ //fuimos los agresores preflop
							if(!hand.isHeroIPpostflop()){ //podemos delayear OOP
								//decididor
								accTurn = decididorTurn.heroAgresorDeleayearOOP(resCalc, jugadasHero, jugadasRival);
								return accTurn.getAccion();
							}
							else{
								if(accionesRival.get(0).getAccion() == AccionEnum.CHECK){//podemos delayear IIP
									//decididor
									accTurn = decididorTurn.heroAgresorDeleayearIP(resCalc, jugadasHero, jugadasRival);
									return accTurn.getAccion();
								}
								//el rival nos ha PROBETEADO, ya sea una bet normal o allin
								else if(accionesRival.get(0).getAccion() == AccionEnum.BET){
									//decididor
									accTurn = decididorTurn.heroVsProbeBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accTurn.getAccion();
								}
								else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
									//decididor
									accTurn = decididorTurn.heroVsProbeAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accTurn.getAccion();
								}
							}
						} 
						//somos defensores [en HU siempre sera: getPosUltimoAgresorGlobalCallesAnteriores() == posRival
						//puede que sea otro rival el agresor que luego foldeeo en flop, asi que pongo el ELSE directamente
						//por tener ya la estructura correcta para hacer posibles algoritmos posteriores qe no sean HU como este.
						else{ 
							if(!hand.isHeroIPpostflop()){ //podemos probetear o checkear
								//decididor
								accTurn = decididorTurn.heroProbetear(resCalc, jugadasHero, jugadasRival);
								return accTurn.getAccion();
							}
							else{ //el rival ha apostado, o ha checkeado siendo el agresor
								if(accionesRival.get(0).getAccion() == AccionEnum.CHECK){
									//decididor
									accTurn = decididorTurn.heroFlotarDelayed(resCalc, jugadasHero, jugadasRival);
									return accTurn.getAccion();
								}
								else if(accionesRival.get(0).getAccion() == AccionEnum.BET){
									//decididor
									accTurn = decididorTurn.heroDefensorIPvsDelayedBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accTurn.getAccion();
								}
								else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
									//decididor
									accTurn = decididorTurn.heroDefensorIPvsDelayedAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accTurn.getAccion();
								}
							}
						}
					}
				}
				else{ //TURNO>1
					/** CUANDO NOS LLEGA EL TURNO UNA SEGUNDA VEZ, Y HA SIDO PORQUE HEMOS CHECKEADO EN TURN, YA NO DIFERENCIAMOS SI EN LAS CALLES ANTERIORES HA HABDIO ALGUN X-X,
					 *  SINO QUE TRATAMOS AMBAS SITUACIONES DE LA MISMA MANERA. **/
					//AQUI ESTAREMOS OOP SIEMPRE, Y CHECKEAMS EN TURNO=1.
					if(accTurn == null){ //esto es que hemos checkeado en turn y nos betea. No sabemos que hacer, hay que analizar
						if(hand.getPosUltimoAgresorGlobalCallesAnteriores() == hand.getHeroPosition()){ //esto es que checkeamos en turn siendo Agresores (roleamos pre) y nos ha beteado el rival
							//decididor
							accTurn = decididorTurn.heroVsFloat(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
							return accTurn.getAccion();
						}
						else{ //esto es que somos los defensores y checkeamos en turn OOP, y nos cebetea,
							if(accionesRival.get(0).getAccion() == AccionEnum.BET){
								//decididor
								accTurn = decididorTurn.heroVsContBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accTurn.getAccion();
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
								//decididor
								accTurn = decididorTurn.heroVsContAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accTurn.getAccion();
							}
						}
					}
					//AQUI NOS ENFRENTAMOS A UN RAISE PERO YA SABEMOS QUE HACER SIEMPRE.
					else{ //esto es que nos enfrentamos a un raise. Puede ser nuestro turno 2 o 3, pero esto da igual.
						Accion ultimaAccRival = hand.getUltimaAccion();
						if(ultimaAccRival.getAccion() == AccionEnum.ALLIN)
							return accTurn.getAccionVsRaiseAI();
						else
							return accTurn.getAccionVsRaiseNAI(hand.getBB(), ultimaAccRival.getCantidad());
					}
				}
				System.err.println("Nos ha llegado el turno una 4vez en TURN, y no hemos respondido nada. O en el 1o2o3 turno hemos ido por una rama de ejecucion que no ha hecho return.");
				break;
			}
				
			case RIVER:
			{
				int posHero = hand.getHeroPosition();
				int posRival = posHero == Posicion.SB ? Posicion.BB : Posicion.SB;
				List<Accion> accionesRival = hand.getActions().get(posRival).get(Calle.RIVER);
				
				if(hand.getNumTurnoCalleActual() == 1){					
					//Eliminamos las manos concretas del rival que no llegan al RIVER, teniendo en cuenta todas las acciones que hubo en turn.
					eliminadorTrasTurn.eliminarCombosRangoConcretoTrasTurn(rangoConcretoRival, hand, posHero, posRival);
					
					//Calculos generales
					ResCalculadoraEquity resCalc = calc.calc(hand.getHeroCards(), rangoConcretoRival.getManos(), hand.getRiver());
					ResJugadas jugadasHero = Jugada.getJugadasEnRiver(hand.getHeroCards(), hand.getRiver());
					ResJugadasRango jugadasRival = Jugada.getJugadasRango(rangoConcretoRival, hand.getRiver());
					
					if(hand.getPosUltimoAgresorPostflop(Calle.TURN) == posHero){ //fuimos los agresores en turn (sea como sea: cbeteando, donkeando, resubiendo, etc)
						if(!hand.isHeroIPpostflop()){ //tenemos la iniciativa y somos los primeros en hablar, estamos OOP
							if(hand.getPosUltimoAgresorPostflop(Calle.FLOP) == posHero){ //esto serian los 3 barrels
								//decididor
								accRiver = decididorRiver.heroAgresorTurnOOP(resCalc, jugadasHero, jugadasRival);
								return accRiver.getAccion();
							}
							else{ //esto seria delayed+barrel
								//decididor
								accRiver = decididorRiver.heroAgresorTurnDelayedBarrelOOP(resCalc, jugadasHero, jugadasRival);
								return accRiver.getAccion();
							}
						}
						else{
							if(accionesRival.get(0).getAccion() == AccionEnum.CHECK){//tenemos la inciativa y el rival o el rival ha checkeado, estamos IP
								if(hand.getPosUltimoAgresorPostflop(Calle.FLOP) == posHero){ //esto serian los 3 barrels
									//decididor
									accRiver = decididorRiver.heroAgresorTurnIP(resCalc, jugadasHero, jugadasRival);
									return accRiver.getAccion();
								}
								else{ //esto seria delayed+barrel
									//decididor
									accRiver = decididorRiver.heroAgresorTurnDelayedBarrelIP(resCalc, jugadasHero, jugadasRival);
									return accRiver.getAccion();
								}
							}
							//el rival nos ha DONKEADO, ya sea una bet normal o allin
							else if(accionesRival.get(0).getAccion() == AccionEnum.BET){
								//decididor
								accRiver = decididorRiver.heroVsDonkBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accRiver.getAccion();
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
								//decididor
								accRiver = decididorRiver.heroVsDonkAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accRiver.getAccion();
							}
						}
					}
					else if(hand.getPosUltimoAgresorPostflop(Calle.TURN) == posRival){ //somos defensores
						if(!hand.isHeroIPpostflop()){ //opdemos donkear o checkear
							//decididor
							accRiver = decididorRiver.heroDonkear(resCalc, jugadasHero, jugadasRival);
							return accRiver.getAccion();
						}
						else{ //el rival ha apostado, o ha checkeado siendo el agresor
							if(accionesRival.get(0).getAccion() == AccionEnum.CHECK){
								//decididor
								accRiver = decididorRiver.heroFlotar(resCalc, jugadasHero, jugadasRival);
								return accRiver.getAccion();
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.BET){
								if(hand.getPosUltimoAgresorPostflop(Calle.FLOP) == posHero){ //esto serian los 3 barrels
									//decididor
									accRiver = decididorRiver.heroDefensorTurnIPvsContBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accRiver.getAccion();
								}
								else{ //esto seria delayed+barrel
									//decididor
									accRiver = decididorRiver.heroDefensorTurnIPvsDelayedBarrelBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accRiver.getAccion();
								}
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
								if(hand.getPosUltimoAgresorPostflop(Calle.FLOP) == posHero){ //esto serian los 3 barrels
									//decididor
									accRiver = decididorRiver.heroDefensorTurnIPvsContAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accRiver.getAccion();
								}
								else{ //esto seria delayed+barrel
									//decididor
									accRiver = decididorRiver.heroDefensorTurnIPvsDelayedBarrelAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accRiver.getAccion();
								}
							}
						}
					}
					/** TODO ESTE ELSE LO PODRIA QUITAR SI QUISIERA ACTUAR IGUAL EN RIVER INDEPENDIENTEMENTE DE SI EL TURN (Y QUIEN SABE SI FLOP) HA SIDO CHECK-CHECK
					 *  YA QUE ESTAN LAS MISMAS SITUACIONES QUE ARRIBA, PERO HABIENDO SIDO X-X EN TURN (DE FLOP NO SABEMOS). O TAMBIEN PUEDO HACER QUE LOS DECIDIDORES SEAN IGUALES**/
					else{ // hand.getPosUltimoAgresorPostflop(Calle.TURN) == -1 (en turn fue CHECK-CHECK)
						if(hand.getPosUltimoAgresorGlobalCallesAnteriores() == posHero){ //fuimos los agresores preflop
							if(!hand.isHeroIPpostflop()){ //podemos delayear OOP
								//decididor
								accRiver = decididorRiver.heroAgresorDeleayearOOP(resCalc, jugadasHero, jugadasRival);
								return accRiver.getAccion();
							}
							else{
								if(accionesRival.get(0).getAccion() == AccionEnum.CHECK){//podemos delayear IIP
									//decididor
									accRiver = decididorRiver.heroAgresorDeleayearIP(resCalc, jugadasHero, jugadasRival);
									return accRiver.getAccion();
								}
								//el rival nos ha PROBETEADO, ya sea una bet normal o allin
								else if(accionesRival.get(0).getAccion() == AccionEnum.BET){
									//decididor
									accRiver = decididorRiver.heroVsProbeBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accRiver.getAccion();
								}
								else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
									//decididor
									accRiver = decididorRiver.heroVsProbeAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accRiver.getAccion();
								}
							}
						} 
						//somos defensores [en HU siempre sera: getPosUltimoAgresorGlobalCallesAnteriores() == posRival
						//puede que sea otro rival el agresor que luego foldeeo en flop, asi que pongo el ELSE directamente
						//por tener ya la estructura correcta para hacer posibles algoritmos posteriores qe no sean HU como este.
						else{ 
							if(!hand.isHeroIPpostflop()){ //podemos probetear o checkear
								//decididor
								accRiver = decididorRiver.heroProbetear(resCalc, jugadasHero, jugadasRival);
								return accRiver.getAccion();
							}
							else{ //el rival ha apostado, o ha checkeado siendo el agresor
								if(accionesRival.get(0).getAccion() == AccionEnum.CHECK){
									//decididor
									accRiver = decididorRiver.heroFlotarDelayed(resCalc, jugadasHero, jugadasRival);
									return accRiver.getAccion();
								}
								else if(accionesRival.get(0).getAccion() == AccionEnum.BET){
									//decididor
									accRiver = decididorRiver.heroDefensorIPvsDelayedBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accRiver.getAccion();
								}
								else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
									//decididor
									accRiver = decididorRiver.heroDefensorIPvsDelayedAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
									return accRiver.getAccion();
								}
							}
						}
					}
				}
				else{ //TURNO>1
					/** CUANDO NOS LLEGA EL TURNO UNA SEGUNDA VEZ, Y HA SIDO PORQUE HEMOS CHECKEADO EN RIVER, YA NO DIFERENCIAMOS SI EN LAS CALLES ANTERIORES HA HABDIO ALGUN X-X,
					 *  SINO QUE TRATAMOS AMBAS SITUACIONES DE LA MISMA MANERA. **/
					//AQUI ESTAREMOS OOP SIEMPRE, Y CHECKEAMS EN TURNO=1.
					if(accRiver == null){ //esto es que hemos checkeado en river y nos betea. No sabemos que hacer, hay que analizar
						if(hand.getPosUltimoAgresorGlobalCallesAnteriores() == hand.getHeroPosition()){ //esto es que checkeamos en river siendo Agresores (roleamos pre) y nos ha beteado el rival
							//decididor
							accRiver = decididorRiver.heroVsFloat(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
							return accRiver.getAccion();
						}
						else{ //esto es que somos los defensores y checkeamos en river OOP, y nos cebetea,
							if(accionesRival.get(0).getAccion() == AccionEnum.BET){
								//decididor
								accRiver = decididorRiver.heroVsContBet(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accRiver.getAccion();
							}
							else if(accionesRival.get(0).getAccion() == AccionEnum.ALLIN){
								//decididor
								accRiver = decididorRiver.heroVsContAllin(resCalc, jugadasHero, jugadasRival, accionesRival.get(0));
								return accRiver.getAccion();
							}
						}
					}
					//AQUI NOS ENFRENTAMOS A UN RAISE PERO YA SABEMOS QUE HACER SIEMPRE.
					else{ //esto es que nos enfrentamos a un raise. Puede ser nuestro turno 2 o 3, pero esto da igual.
						Accion ultimaAccRival = hand.getUltimaAccion();
						if(ultimaAccRival.getAccion() == AccionEnum.ALLIN)
							return accRiver.getAccionVsRaiseAI();
						else
							return accRiver.getAccionVsRaiseNAI(hand.getBB(), ultimaAccRival.getCantidad());
					}
				}
				System.err.println("Nos ha llegado el turno una 4vez en RIVER, y no hemos respondido nada. O en el 1o2o3 turno hemos ido por una rama de ejecucion que no ha hecho return.");
				break;
			}
		}
		
		return null;
	}


	/**Seteamos la variable @rangoRival con el rango que estimamos llega el rival al FLOP, antes de que pase nada en esta calle.**/
	private void setRangoRivalTrasPreflop(Hand hand, int posHero, int posRival){
		Accion primeraAccHeroPF = hand.getPrimeraAccionHeroEn(Calle.PREFLOP);
		List<Accion> accionesRival = hand.getActions().get(posRival).get(Calle.PREFLOP);
		
		//Verificamos el rango con el que ha llega el rival al FLOP
		if(posHero == Posicion.SB){
			if(primeraAccHeroPF.getAccion() == AccionEnum.CALL){
				if(accionesRival.get(0).getAccion() == AccionEnum.CHECK){
					this.rangoRival = selector.getMejoresManos(100, hudReadless.getReadlessValue("BB2H_ROL", ciegasEfectivasPreflop)); //esto es lo mismo que las peores manos excluyendo el top que ROLEARIA
				}
				else{ //esto sera si nos ha hecho ROL
					this.rangoRival = selector.getMejoresManos(hudReadless.getReadlessValue("BB2H_ROL", ciegasEfectivasPreflop), 0);
				}
			}
			else{ //esto sera que he hecho MR o allin (zzz - pero si es allin preflop no se deberia llamar mas al algoritmo)
				if(accionesRival.get(0).getAccion() == AccionEnum.CALL){
					this.rangoRival = selector.getMejoresManosSinPockets(hudReadless.getReadlessValue("BB2H_VPIP", ciegasEfectivasPreflop), hudReadless.getReadlessValue("BB2H_3bet", ciegasEfectivasPreflop));
				}
				else{ //esto sera si nos ha 3-BETEADO
					this.rangoRival = selector.getMejoresManos(hudReadless.getReadlessValue("BB2H_3bet", ciegasEfectivasPreflop), 0);
				}
			}
		}
		else{ //posHero == BB
			if(primeraAccHeroPF.getAccion() == AccionEnum.CHECK){ //esto es que el rival ha limpeado
				this.rangoRival = selector.getMejoresManosSinPockets(hudReadless.getReadlessValue("SB2H_VPIP", ciegasEfectivasPreflop), hudReadless.getReadlessValue("SB2H_PFR", ciegasEfectivasPreflop));
			}
			else if(primeraAccHeroPF.getAccion() == AccionEnum.CALL){
				this.rangoRival = selector.getMejoresManosSinPockets(hudReadless.getReadlessValue("SB2H_PFR", ciegasEfectivasPreflop), 0);
			}
			else if(primeraAccHeroPF.getAccion() == AccionEnum.RAISE){ //esto podria ser tanto un ROL como un 3bet
				if(accionesRival.get(0).getAccion() == AccionEnum.CALL){ //esto seria que hemos ROLEADO
					if(accionesRival.get(1).getAccion() == AccionEnum.CALL){ //nos callea a nuestro ROL
						double top = hudReadless.getReadlessValue("SB2H_PFR", ciegasEfectivasPreflop)+hudReadless.getReadlessValue("SB2H_OS", ciegasEfectivasPreflop);
						this.rangoRival = selector.getMejoresManosSinPockets(hudReadless.getReadlessValue("SB2H_VPIP", ciegasEfectivasPreflop), top);
						this.rangoRival.eliminarBottom(hudReadless.getReadlessValue("SB2H_limpFoldNAI", ciegasEfectivasPreflop));
						//si hubiera rango de Limp-ROL habria que contemplarlo aqui, para eliminarlo.
					}
					else{ //nos ha raiseadoNAI a nuestro ROL y nosotros hemos CALLEADO - en cualquier otra circunstancia no deberia llamarse al algoritmo o llegar a este punto
						//ESTO VA A PASAR MUY,MUY,MUY,MUY POQUITAS VECES, PUES NOS TIENE Q RESUBIR NON-ALLIN A NUESTRO ROL
						//Le pongo el rango = null pues es muy random con que nos puede hacer esto
						this.rangoRival = null;
						//como idea tmb podría coger el top 8-10% del rango que no foldea a nuestro ROL. Pero es q engeneral esto van a ser premium.
					}
				}
				else{ //esto seria que hemos 3-BETEADO
					if(accionesRival.get(1).getAccion() == AccionEnum.CALL){ //solo podemos llegar hasta aqui si el rival nos callea, ya que si nos 4betea NAI, habremos o foldeado o hecho allin nosotros.
						this.rangoRival = selector.getMejoresManosSinPockets(hudReadless.getReadlessValue("SB2H_PFR", ciegasEfectivasPreflop), 0);
						this.rangoRival.eliminarTop(hudReadless.getReadlessValue("SB2H_4bet", ciegasEfectivasPreflop));
						this.rangoRival.eliminarBottom(hudReadless.getReadlessValue("SB2H_fold3betNAI", ciegasEfectivasPreflop));
					}
				}
			}
		}
	}


	@Override
	public void resetFinDeMano() {
		this.accFlop = null;
		this.accTurn = null;
		this.accRiver =null;
	}

}
