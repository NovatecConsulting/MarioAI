package de.hskl.info_ag.mariocontest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.idsia.benchmark.mario.engine.SimulatorOptions;
import ch.idsia.benchmark.mario.engine.generalization.Tile;
import de.hskl.info_ag.mariocontest.dto.AbstractGameObject;
import de.hskl.info_ag.mariocontest.dto.Block;
import de.hskl.info_ag.mariocontest.dto.Collectible;
import de.hskl.info_ag.mariocontest.dto.Enemy;
import de.hskl.info_ag.mariocontest.enums.BlockType;
import de.hskl.info_ag.mariocontest.enums.CollectibleType;
import de.hskl.info_ag.mariocontest.enums.EnemyType;
import de.novatec.marioai.MarioAgenNtBase;

public abstract class MarioDemoAgent extends MarioAgenNtBase {

	private final int WIDTH_HALF_FIELD_GRID = (SimulatorOptions.receptiveFieldWidth-1)/2;
	private final int HEIGHT_HALF_FIELD_GRID = (SimulatorOptions.receptiveFieldHeight-1)/2;
	
	final static int ENEMY_CHECK_DISTANCE = 4;

	public MarioDemoAgent() {
		super();
	}

	@Override
	public String getName() {
		return "HS-KL Demo Agent";
	}

	// Mario interactions
	public void runRight() {
		this.getMarioControl().runRight();
	}

	public void runLeft() {
		this.getMarioControl().runLeft();
	}

	public void sprint() {
		this.getMarioControl().sprint();
	}

	public void jump() {
		this.getMarioControl().jump();
	}

	public void jumpHigh() {
		this.getMarioControl().jump();
		if (!this.getMarioEntity().onGround)
			this.getMarioControl().jump();
	}

	public void shoot() {
		this.getMarioControl().shoot();
	}

	// Environment detection
	/**
	 * Returns if an unpassable brick is ahead in a distance of <b>3</b>.
	 * @return {@link Boolean}
	 */
	public boolean isBrickAhead() {
		return this.getTiles().brick(1, 0) || this.getTiles().brick(1, -1) || this.getTiles().brick(2, 0)
				|| this.getTiles().brick(2, -1) || this.getTiles().brick(3, 0) || this.getTiles().brick(3, -1);
	}

	/**
	 * Returns if a slope is ahead in a distance of <b>2</b>.
	 * @return {@link Boolean}
	 */
	public boolean isSlopeAhead() {
		return this.getTiles().brick(0, 1) && (this.getTiles().emptyTile(1, 1) || this.getTiles().emptyTile(2, 1));
	}

	// Enemy detection
	/**
	 * Returns if a enemy is in range of {@value #ENEMY_CHECK_DISTANCE} ahead of Mario.
	 * 
	 * @return {@link Boolean}
	 */
	public boolean isEnemyAhead() {
		
		boolean enemyAhead = false;
		
		for (int curPos = 0; curPos <= ENEMY_CHECK_DISTANCE; curPos++) {
			if (getEntities().danger(curPos, 0) || getEntities().danger(curPos, -1)) {
				enemyAhead = true;
			}
		}
		
		return enemyAhead;
	}

	/**
	 * Returns an ordered list of enemies in range of
	 * {@value #ENEMY_CHECK_DISTANCE} ahead of Mario (ordered by closest enemy first)
	 * 
	 * @return {@link List}
	 */
	public List<Enemy> getEnemiesAhead() {

		List<Enemy> enemyList = new ArrayList<Enemy>();

		for (int curPos = 0; curPos <= ENEMY_CHECK_DISTANCE; curPos++) {
			if (getEntities().danger(curPos, 0)) {
				enemyList.add(new Enemy(curPos, 0, EnemyType.parseEntityType(getEntities().entityType(curPos, 0))));
			}

			if (getEntities().danger(curPos, -1)) {
				enemyList.add(new Enemy(curPos, -1, EnemyType.parseEntityType(getEntities().entityType(curPos, -1))));
			}
		}

		return enemyList;
	}

	/**
	 * Returns an ordered List of all {@link Collectible}s that are currently
	 * rendered within the grid (see debug).
	 * 
	 * @return
	 */
	public List<Collectible> getCollectiblesOnScreen() {

		List<Collectible> collectibleList = new ArrayList<Collectible>();

		/*
		 * Y-Axis goes from -9 (top) to +9 (bottom)
		 * X-Axis goes from -9 (left) to +9 (right)
		 */
		// Vertical iteration (Top to bottom)
		for (int curPosY = -(HEIGHT_HALF_FIELD_GRID); curPosY <= HEIGHT_HALF_FIELD_GRID; curPosY++) {
			// Horizontal iteration (Left to right)
			for (int curPosX = -(WIDTH_HALF_FIELD_GRID); curPosX <= WIDTH_HALF_FIELD_GRID; curPosX++) {
				if (getEntities().collectible(curPosX, curPosY)) {
					try {
						collectibleList.add(new Collectible(curPosX, curPosY,
								CollectibleType.parseEntityType(getEntities().entityType(curPosX, curPosY))));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		Collections.sort(collectibleList);
		
		return collectibleList;
	}
	
	/**
	 * Returns an ordered List of all {@link Enemy}s that are currently
	 * rendered within the grid (see debug).
	 * 
	 * @return
	 */
	public List<Enemy> getEnemiesOnScreen() {

		List<Enemy> enemyList = new ArrayList<Enemy>();

		/*
		 * Y-Axis goes from -9 (top) to +9 (bottom)
		 * X-Axis goes from -9 (left) to +9 (right)
		 */
		// Vertical iteration (Top to bottom)
		for (int curPosY = -(HEIGHT_HALF_FIELD_GRID); curPosY <= HEIGHT_HALF_FIELD_GRID; curPosY++) {
			// Horizontal iteration (Left to right)
			for (int curPosX = -(WIDTH_HALF_FIELD_GRID); curPosX <= WIDTH_HALF_FIELD_GRID; curPosX++) {
				if (getEntities().danger(curPosX, curPosY)) {
					try {
						enemyList.add(new Enemy(curPosX, curPosY,
								EnemyType.parseEntityType(getEntities().entityType(curPosX, curPosY))));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		Collections.sort(enemyList);
		
		return enemyList;
	}
	
	/**
	 * Returns the direct distance. Visually a straight line from Mario to target.
	 * 
	 * @return {@link double}
	 */
	public double getDistance(AbstractGameObject target) {
		return AbstractGameObject.getDistance(target);
	}
	
	/**
	 * Returns an ordered List of all {@link Block}s that are currently
	 * rendered within the grid (see debug).
	 * 
	 * <i>NOTE:</i> receptiveFieldWidth is the width of the whole field
	 * including 0 tile in the middle and both sides with the length of
	 * (receptiveFieldWidth-1)/2.
	 * 
	 * @return
	 */
	public List<Block> getInteractiveBlocksOnScreen() {
		
		List<Block> interactiveBlocks = new ArrayList<>();
		
		Tile[][] tileField = getTiles().tileField;
		for(int i = 0; i < tileField.length; i++) {
			for (int j = 0; j < tileField[i].length; j++) {
				int posX = j - WIDTH_HALF_FIELD_GRID,
					posY = i - HEIGHT_HALF_FIELD_GRID;
				
				switch(tileField[i][j]) {
					case BREAKABLE_BRICK:
						interactiveBlocks.add(new Block(posX, posY, BlockType.BREAKABLE));
						break;
					case QUESTION_BRICK:
						interactiveBlocks.add(new Block(posX, posY, BlockType.QUESTIONMARK));
						break;
					case BRICK:
						interactiveBlocks.add(new Block(posX, posY, BlockType.SIMPLE));
						break;
					default:
						break;
				}
			}
		}
		
		Collections.sort(interactiveBlocks);
		
		return interactiveBlocks;
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Debug
	public void waiting(int milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

}
