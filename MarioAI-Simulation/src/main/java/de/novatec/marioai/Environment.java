package de.novatec.marioai;

import ch.idsia.agents.controllers.modules.Entities;
import ch.idsia.agents.controllers.modules.Tiles;
import ch.idsia.benchmark.mario.engine.generalization.Entity;
import ch.idsia.benchmark.mario.engine.generalization.EntityType;
import ch.idsia.benchmark.mario.engine.generalization.Tile;
import ch.idsia.benchmark.mario.engine.generalization.TileType;
import ch.idsia.benchmark.mario.engine.sprites.Mario;

public class Environment {

	private Tiles tiles;
	
	private Entities entities;
	
	private Mario mario;
	
	public Environment(Tiles tiles, Entities entities, Mario mario) {
		this.tiles = tiles;
		this.entities = entities;
		this.mario = mario;
	}
	
	public TileType getTileRelativeToMario(int x, int y) {
		if (mario == null) {
			return null;
		}
		
		int relativeTileX =  mario.mapX + x;
		int relativeTileY = mario.mapY + y; 

		System.out.println("TILE_POS: " + relativeTileX + ", " + relativeTileY);
		
		Tile tile = tiles.getTile(relativeTileX, relativeTileY);
		
		EntityType entity = null;
		
		if (entities.anything(x,y)) {
			entity = entities.entityType(relativeTileX, relativeTileY);
		}
		
		if (entity != null) {
			System.out.println("ENTITY TYPE");
			return entity.getTileType();
		}
		
			System.out.println("TILE TYPE");
		return tile.getTileType();
	}

}
