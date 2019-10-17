package persistencia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import logica.AccionPreflop;
import logica.AccionPreflopAllin;
import logica.AccionPreflopCallBBs;
import logica.AccionPreflopCheck;
import logica.AccionPreflopFold;
import logica.AccionPreflopMedioStack;
import logica.AccionPreflopRaiseBBs;
import logica.AccionPreflopRaiseX;
import logica.MovPreflop;

public class LectorTablaPreflopListaTXT implements ILectorTablaPreflop {

	Pattern pattern = Pattern.compile("(.+)\t([^\\|]+)\\|?([^\\|]*)\\|?([^\\|]*)");
	Pattern pRaise = Pattern.compile("R(.+)");
	Pattern pCall = Pattern.compile("C(.*)"); //Si el call no tiene numero es que es un call a cualquier size, lo mismo que si tiene un C0
	
	@Override
	public Map<String, MovPreflop> getTabla(File fichero) {
		Map<String, MovPreflop> tabla = new HashMap<String, MovPreflop>();
		Scanner s = null;
		try {
			s = new Scanner(fichero);
			while(s.hasNextLine()){
				String linea = s.nextLine();
				Matcher m = pattern.matcher(linea);
				m.find();
				
				String combo = m.group(1);
				String[] acciones = new String[m.groupCount()-1];
				for(int i=0; i<acciones.length; i++){
					acciones[i] = m.group(i+2);
				}
				
				AccionPreflop[] accionesPF = new AccionPreflop[acciones.length];
				for(int i=0; i<accionesPF.length; i++) {
					String a = acciones[i];
					if(a.equals("A")) accionesPF[i] = new AccionPreflopAllin();
					else if(a.equals("AM")) accionesPF[i] = new AccionPreflopMedioStack();
					else if(a.equals("X")) accionesPF[i] = new AccionPreflopCheck();
					else if(a.equals("F")) accionesPF[i] = new AccionPreflopFold();
					else{
						Matcher mRaise = pRaise.matcher(a);
						if(mRaise.find()){
							String raise = mRaise.group(1);
							if(raise.startsWith("x"))
								accionesPF[i] = new AccionPreflopRaiseX(Double.parseDouble(raise.substring(1)));
							else
								accionesPF[i] = new AccionPreflopRaiseBBs(Double.parseDouble(raise));
						}
						else{
							Matcher mCall = pCall.matcher(a);
							if(mCall.find()){
								String call = mCall.group(1);
								if(call.length()==0) accionesPF[i] = new AccionPreflopCallBBs(0);
								else accionesPF[i] = new AccionPreflopCallBBs(Double.parseDouble(call));
							}
						}
					}
				}
				
				MovPreflop mov = null;
				if(m.groupCount()>2) mov =new MovPreflop(accionesPF[0], accionesPF[1], accionesPF[2]);
				else mov = new MovPreflop(accionesPF[0]);
				
				tabla.put(combo, mov);
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return tabla;
	}
	
	/*public static void main(String[] args){
		File f = new File("Rangos/3H9 vs 0 MR 22.5BB.txt");
		LectorTablaPreflopListaTXT lector = new LectorTablaPreflopListaTXT();
		Map<String, MovPreflop> tabla = lector.getTabla(f);
		System.out.println(tabla.toString());
		int a=0;
		int b=a*5;
	}*/

}
