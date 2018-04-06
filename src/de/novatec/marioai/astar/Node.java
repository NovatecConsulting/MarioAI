package de.novatec.marioai.astar;

import java.util.Arrays;

import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Mario.STATUS;
import de.novatec.marioai.tools.MarioInput;

public class Node {
	
	private AStarCoordinates coords;
	private Node parent;
	
	private boolean[] usedInput;
	private LevelScene usedLevelScene;
	
	private boolean gotHurt=false;
	
	public Node() {
		
	}
	
	public Node(boolean[] usedInput, LevelScene usedLevelScene, Node parent) { //for search-nodes		
		this.usedLevelScene=usedLevelScene.getDeepCopy();
		this.usedInput=usedInput;
		int tmpHurtStatus=this.usedLevelScene.getTimesMarioHurt();
		
		
			
		
		this.usedLevelScene.setMarioKeys(usedInput);
		//System.out.println("X before Tick"+this.usedLevelScene.getMarioX());
		//System.out.println("Y before Tick"+this.usedLevelScene.getMarioY());
		if(usedLevelScene.getMarioStatus()==STATUS.RUNNING) this.usedLevelScene.tick(); //goOneTick
		//System.out.println("X after Tick"+this.usedLevelScene.getMarioX());
		//System.out.println("Y after Tick"+this.usedLevelScene.getMarioY());
		if(tmpHurtStatus!=this.usedLevelScene.getTimesMarioHurt()) gotHurt=true; 
		
		this.coords=new AStarCoordinates(this.usedLevelScene.getMarioX(), this.usedLevelScene.getMarioY());
		
	}
	
	public Node (Node toCopy) {
		this.usedLevelScene=toCopy.usedLevelScene;
		this.coords=toCopy.coords;
		this.usedInput=toCopy.usedInput;
		this.parent=toCopy.parent;
		this.gotHurt=toCopy.gotHurt;
	}
	
	public Node(LevelScene levelScene) { // for start-nodes
			this.usedLevelScene=levelScene.getDeepCopy();
			this.coords=new AStarCoordinates(this.usedLevelScene.getMarioX(),this.usedLevelScene.getMarioY());
			this.usedInput=new boolean[] {false,false,false,false,false};
	}
	
	public float getX() {
		return this.coords.getX();
	}
	
	public float getY() {
		return this.coords.getY();
	}
	
	public boolean gotHurt() {
		return this.gotHurt;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public void setParent(Node parent) { 
		this.parent=parent;
	}
	
	public LevelScene getUsedLevelScene() {
		return this.usedLevelScene;
	}
	
	public boolean[] getUsedInput() {
		return this.usedInput;
	}
	
	public double getDistanceTo(Node other) {
		return Math.sqrt(Math.pow(other.getX()-this.getX(), 2)+Math.pow(other.getY()-this.getY(), 2));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coords == null) ? 0 : coords.hashCode());
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
		Node other = (Node) obj;
		if (coords == null) {
			if (other.coords != null)
				return false;
		} else if (!coords.equals(other.coords))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Node [coords=" + coords + ", usedInput="+Arrays.toString(usedInput)+"]"; 
	}
}
