package de.novatec.mario.engine.generalization;

public class Coordinates {
	

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
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

private float x,y;
 
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
}
