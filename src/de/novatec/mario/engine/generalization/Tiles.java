package de.novatec.mario.engine.generalization;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.idsia.mario.environments.Environment;

public class Tiles {
	
	Environment env;
	
	public Tiles(Environment env) {
		this.env=env;
	}
	
	public TileType getTile(int x,int y) {
		//no check needed, Map already managed that
		
		Tile res=env.getTiles().get(new Coordinates(x,y));
		if(res==null) return TileType.NOTHING;
		
		return res.getType();
	}
	
	public boolean isEmpty(int x, int y) {
		return getTile(x, y)==TileType.NOTHING;
	}
	
	public boolean isNotEmpty(int x,int y) {
		return !isEmpty(x, y);
	}
	
	public boolean isBrick(int x,int y) {
		TileType res=getTile(x, y);
		switch(res) {
		case BREAKABLE_BRICK:
		case QUESTION_BRICK:
		case UNBREAKABLE_BRICK:
		case FLOWER_POT:
		case CANON:
			return true;
		}
		return false;
	}
	
	public List<Tile> getTiles(){
	List<Tile> res=new LinkedList<>();
		
		Map<Coordinates,Tile> map=env.getTiles();
		
		for(Entry<Coordinates,Tile> next:map.entrySet()) {
				res.add(next.getValue());
			}
		
		return res;
	}
	
	public List<Tile> getInteractiveBlocksOnScreen(){
		List<Tile> tiles=getTiles(),res=new LinkedList<>();
		
		for(Tile next:tiles) {
			TileType type=next.getType();
			if(type==TileType.BREAKABLE_BRICK||type==TileType.QUESTION_BRICK||type==TileType.COIN) 	res.add(next);
			
		}
		return res;
	}
	
	public void setEnvironment(Environment env) {
		this.env=env;
	}
	
	public enum TileType{
		BREAKABLE_BRICK,
		QUESTION_BRICK,
		NOTHING,
		COIN,
		UNBREAKABLE_BRICK,
		LADDER,
		FLOWER_POT,
		CANON,
		UNKNOWN,
		SOMETHING
	}
}
