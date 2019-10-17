package stats;

import java.lang.ProcessBuilder.Redirect;
import java.util.List;

public abstract class Stat {
	
	private String name;
	private double readlessValue; //el valor medio de la poblacion total (o de fishes, ya que va a ser parecido)
	private double readlessRegValue; //el valor medio de la poblacion de regs
	
	private double occurences; //veces que se ha dado este caso (el fold, el call, el raise...)
	private double opportunities; //veces que el jugador ha tenido la posibilidad de hacer ese movimeinto, lo haya hecho o no.
	
	//En las Stats que no necesitemos usar tramos esto sera NULL
	//Si la Stat SI que tiene tramos, las variables sencillas tendran la media total (el valor q tendria la stat sin tener en cuenta los tramos)
	//El primer indice del array significa el numero de ciegas, y en esa fila estarán los datos de la stat en el tramo desde esa ciega (incluida)
	//a las ciegas del nivel anterior (excluidas) o hasta el infintio si es la primera fila del array (tramos[0]).
	//La ultima fila (tramos[tramos.length-1]) contendrá los casos que van desde que tenemos menos de las ciegas del ultimo tramo hasta 0.
	//Las columnas son: 0-numero de ciegas del tramo, 1-readlessValue, 2-readlessRegValue, 3-occurences, 4-opportuinities.
	private double[][] tramos;
	
	public Stat(String name, double readlessValue){
		this.name = name;
		this.readlessValue = readlessValue;		
	}
	public Stat(String name, double readlessValue, double readlessRegValue){
		this.name = name;
		this.readlessValue = readlessValue;
		this.readlessRegValue = readlessRegValue;
	}
	
	public Stat(String name, double readlessValue, double[][] tramos){
		this(name, readlessValue);
		this.tramos = tramos;
	}
	public Stat(String name, double readlessValue, double readlessRegValue, double[][] tramos){
		this(name, readlessValue, readlessRegValue);
		this.tramos = tramos;
	}
	
	public String getName(){
		return name;
	}
	public double getReadlessValue(){
		return readlessValue;
	}
	public double getReadlessRegValue(){
		return readlessRegValue;
	}
	
	public double getValue(){
		return (occurences/opportunities)*100;
	}
	
	
	public double getReadlessValue(double ciegas){
		for(int i=0; i<tramos.length; i++){
			if(ciegas >= tramos[i][0]) return tramos[i][1];
		}
		return -1;
	}
	public double getReadlessRegValue(double ciegas){
		for(int i=0; i<tramos.length; i++){
			if(ciegas >= tramos[i][0]) return tramos[i][2];
		}
		return -1;
	}	
	public double getValue(double ciegas){
		for(int i=0; i<tramos.length; i++){
			if(ciegas >= tramos[i][0]) return (tramos[i][3]/tramos[i][4])*100;
		}
		return -1;
	}
	
}
