package de.novatec.marioai;

public abstract class AbstractGameObject implements Comparable<AbstractGameObject> {
	public AbstractGameObject(int relPosX, int relPosY) {
		super();
		this.relPosX = relPosX;
		this.relPosY = relPosY;
	}

	final int relPosX;
	final int relPosY;

	public int getRelPosX() {
		return relPosX;
	}

	public int getRelPosY() {
		return relPosY;
	}
	
	@Override
	public int compareTo(AbstractGameObject o) {
		// own distance
		double relPosX = (double) this.relPosX;
		double relPosY = (double) this.relPosY;
		double myDist = calculateDistance(relPosX, relPosY);
		
		// other distance
		relPosX = (double) o.relPosX;
		relPosY = (double) o.relPosY;
		double otherDist = calculateDistance(relPosX, relPosY);
		
		if(myDist < otherDist)
			// Current object is closer than other
			return -1;
		else if(myDist > otherDist)
			// Current object is further away than other
			return 1;
		else
			// Same distance
			return 0;
	}
	
	public static double getDistance(AbstractGameObject objectA) {
		return calculateDistance(objectA.relPosX, objectA.relPosY);
	}
	
	private static double calculateDistance(double x, double y) {
		return Math.sqrt(Math.abs(x) * Math.abs(x) + Math.abs(y) * Math.abs(y));
	}
	

}
