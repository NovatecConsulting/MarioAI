package de.novatec.mario.engine.generalization;

public class Coordinates {
	
 @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

private int x,y;
 
 public Coordinates () {
	 this.x=0;
	 this.y=0;
 }
 
 public Coordinates (int x,int y) {
	 this.x=x;
	 this.y=y;
 }

public int getX() {
	return x;
}

public int getY() {
	return y;
}
 
public String toString() {
	return "["+this.x+","+this.y+"]";
}
}
