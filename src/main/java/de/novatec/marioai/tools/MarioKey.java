package de.novatec.marioai.tools;

public enum MarioKey {
	LEFT("Left",0),RIGHT("Right",1),DOWN("Down",2),JUMP("Jump",3),SPEED("Speed",4);
	
	private int code;
	private String name;
	
	MarioKey(String name,int code){
		this.name=name;
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
}
