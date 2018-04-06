package de.novatec.marioai.astar;

public class AStarCoordinates {

private float x,y;

private static final float EPSILON=0.001f;
 
 public AStarCoordinates () {
	 this.x=0;
	 this.y=0;
 }
 
 public AStarCoordinates (float x,float y) {
	 this.x=x;
	 this.y=y;
 }

public float getX() {
	return x;
}

public float getY() {
	return y; 
}
 
public String toString() {
	return "["+this.x+","+this.y+"]";
}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Float.floatToIntBits(x);
	result = prime * result + Float.floatToIntBits(y);
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	AStarCoordinates other = (AStarCoordinates) obj;
	if (Math.abs(x-other.x)>EPSILON) //CHANGE THAT!!
		return false;
	if (Math.abs(y-other.y)>EPSILON)
		return false;
	return true;
}

}
