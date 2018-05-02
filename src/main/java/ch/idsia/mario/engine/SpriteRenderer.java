package ch.idsia.mario.engine;

import java.awt.Graphics;
import java.util.List;

import ch.idsia.mario.engine.sprites.Sprite;

/**
 * Renderer for all sprites that are in the given list
 * @author rgu
 *
 */
public class SpriteRenderer {

	private List<Sprite> sprites;
	
	public SpriteRenderer(List<Sprite> sprites) {
		this.sprites=sprites;
	}
	
	public void render(Graphics g,int xCam,int yCam, int layer) {
		g.translate(-xCam, -yCam);

		for (Sprite sprite : sprites) { 
			if (sprite.getLayer() == layer) {
				sprite.render(g);
			}
		}
	}
}
