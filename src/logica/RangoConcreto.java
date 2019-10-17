package logica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Esta clase se utilizara para ir teniendo el rango posible del rival, pero con masnos cocretas (aunque en flopzilla les llame combos)
 * ya que si sale un 7 de picas en flop, hay manos/combos de un Rango que no podria tener el rival etc.
 */
public class RangoConcreto {
	
	//No puede haber manos repetidas logicamente,
	private List<Mano> manos;
	
	//Aqui iremos poniendo cuantas manos nos quedan de cada combo en el rango.
	//Si un combo no esta en el map, significa que no hay ninguna mano de ese combo en el rango.
	//Si un combo se queda con 0 manos concretas o quitamos del map.
	private Map<String, Integer> numCombos;

	//En general llamaremos a este metodo al principio del flop y le pasaremos una lista que
	//contenga nuestras HoleCards y las 3 cartas del flop.
	public RangoConcreto(List<Combo> combos, Mano heroCards, List<Carta> flop) {
		this.manos = new ArrayList<Mano>();
		this.numCombos = new HashMap<String, Integer>(combos.size());
		List<Carta> cartasYaRepartidas = null;
		if(heroCards != null){
			cartasYaRepartidas = new ArrayList<Carta>(5);
			cartasYaRepartidas.addAll(heroCards.getListaCartas());
			cartasYaRepartidas.addAll(flop);
		}
		else{
			cartasYaRepartidas = flop;
		}
		for(Combo c : combos){
			List<Mano> list = c.generarManosConcretas(cartasYaRepartidas);
			if(list.size()>0){
				manos.addAll(list);
				numCombos.put(c.getCombo(), list.size());
			}
		}
	}	

	public List<Mano> getManos() {
		return manos;
	}
	
	public int getNumManos(){
		return manos.size();
	}
	
	public Mano getManoRandom(){
		int pos = (int)(Math.random()*manos.size());
		return manos.get(pos);
	}
	
	/*public void eliminarManosImposibles(List<Carta> cartasYaRepartidas){
		for(Carta c : cartasYaRepartidas) eliminarManosImposibles(c);
	}*/
	
	public void eliminarManosImposibles(Carta nuevaCartaRepartida){
		for(int i=0; i<manos.size(); i++){
			if(manos.get(i).getCarta1().equals(nuevaCartaRepartida) || manos.get(i).getCarta2().equals(nuevaCartaRepartida))
				manos.remove(i);
		}
	}
}
