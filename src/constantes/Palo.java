package constantes;

public enum Palo {
	CORAZONES,
	DIAMANTES,
	TREBOLES,
	PICAS;
	
	public static Palo englishAbrevToPalo(Character c){
		if(c.equals('h')) return CORAZONES;
		if(c.equals('d')) return DIAMANTES;
		if(c.equals('c')) return TREBOLES;
		if(c.equals('s')) return PICAS;
		return null;
	}
}
