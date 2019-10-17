package stats;

public class BU3H_VPIP extends Stat{

	static double[][] tramos = {{17,75,75,0,0},
								{13,70,70,0,0},
								{9,68,68,0,0},
								{0,55,55,0,0}};
	
	public BU3H_VPIP(){
		super("BU3H_VPIP", 78, tramos);
	}

}
