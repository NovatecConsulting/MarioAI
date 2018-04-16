package ch.idsia.mario.engine;

import java.awt.Graphics;
import java.util.List;

import ch.idsia.mario.engine.sprites.Sprite;

public class SpriteRenderer {

	private List<Sprite> sprites;
	
	public SpriteRenderer(List<Sprite> sprites) {
		this.sprites=sprites;
	}
	
	public void render(Graphics g, float alpha) {
		
	}
}
