package de.novatec.marioai.astar;

public class Coordinates {


private float x,y;

private static final float EPSILON=2f;
 
 public Coordinates () {
	 this.x=0;
	 this.y=0;
 }
 
 public Coordinates (float x,float y) {
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
	Coordinates other = (Coordinates) obj;
	if (Math.abs(x-other.x)>EPSILON) //CHANGE THAT!!
		return false;
	if (Math.abs(y-other.y)>EPSILON)
		return false;
	return true;
}


//	public static void main(String [] args) {
//		Coordinates c1=new Coordinates(1.2f,1.9f ),c2=new Coordinates(1.2f,1.7f );
//		System.out.println(c1.equals(c2));
//	}
}
