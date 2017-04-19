package de.novatec.marioai;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.idsia.agents.AgentOptions;
import ch.idsia.agents.IAgent;
import ch.idsia.agents.controllers.modules.Entities;
import ch.idsia.agents.controllers.modules.Tiles;
import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.SimulatorOptions;
import ch.idsia.benchmark.mario.engine.VisualizationComponent;
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity;
import ch.idsia.benchmark.mario.engine.generalization.Tile;
import ch.idsia.benchmark.mario.engine.input.MarioControl;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.engine.input.MarioKey;
import ch.idsia.benchmark.mario.environments.IEnvironment;

/**
 * Base class for MarioAgents. It wraps methods and instances which will be
 * needed to implement an MarioAgent. An Agent has to extend this class and also
 * to implement the method {@link #doAiLogic()}.
 *
 * @author tj NovaTec GmbH
 */
public abstract class MarioAiAgent implements IAgent, KeyListener {

	private final int WIDTH_HALF_FIELD_GRID = (SimulatorOptions.receptiveFieldWidth - 1) / 2;

	private final int HEIGHT_HALF_FIELD_GRID = (SimulatorOptions.receptiveFieldHeight - 1) / 2;

	private final static int ENEMY_CHECK_DISTANCE = 4;

	private List<Coords> coordinates = new ArrayList<Coords>();

	/**
	 * The execution of {@link #doAiLogic()} has to be delegated to the
	 * anonymous instance of {@link #baseApi}. This instance also overwrites
	 * {@link MarioAgentNtWrapper#actionSelectionAI()}
	 */
	private MarioAgentNtWrapper baseApi = new MarioAgentNtWrapper() {

		@Override
		public MarioInput actionSelectionAI() {
			return doAiLogic();
		}

		@Override
		public void debugDraw(VisualizationComponent vis, LevelScene level, IEnvironment env, Graphics g) {
			if (hijacked) {
				MarioInput ai = actionSelectionAI();
				if (ai != null) {
					String msg = "AGENT KEYS:   ";
					boolean first = true;
					for (MarioKey pressedKey : ai.getPressed()) {
						if (first)
							first = false;
						else
							msg += " ";
						msg += pressedKey.getDebug();
					}
					VisualizationComponent.drawStringDropShadow(g, msg, 0, 9, 6);
				}
			}
			if (mario == null)
				return;

			if (!renderExtraDebugInfo)
				return;

			Coords previous = null;
			for (Coords coord : coordinates) {
				if (previous != null) {
					g.setColor(Color.RED);
					if (previous.x > 1.0f || previous.y > 1.0f || coord.x > 1.0f || coord.y > 1.0f)
						g.drawLine((int) previous.x, (int) previous.y, (int) coord.x, (int) coord.y);
					previous = coord;
				} else {
					previous = coord;
				}
			}
		}
	};

	/**
	 * The logic (i.e. the Agent's algorithm) has to be implemented in this
	 * method.
	 * 
	 * @return the {@link MarioInput} instance which will be delegated through
	 *         the simulator execution.
	 */
	public abstract MarioInput doAiLogic();

	////////////////////////////
	// Mario interaction methods
	////////////////////////////

	/**
	 * Make Mario run right.
	 */
	public void runRight() {
		this.getMarioControl().runRight();
	}

	/**
	 * Make Mario run left.
	 */
	public void runLeft() {
		this.getMarioControl().runLeft();
	}

	/**
	 * Make Mario sprint. Can be combined with other interaction methods.
	 */
	public void sprint() {
		this.getMarioControl().sprint();
	}

	/**
	 * Make Mario jump.
	 */
	public void jump() {
		this.getMarioControl().jump();
	}

	/**
	 * Make Mario shoot (only possible if fire flower consumed).
	 */
	public void shoot() {
		this.getMarioControl().shoot();
	}

	////////////////////////////////
	// Environment detection methods
	////////////////////////////////

	/**
	 * Returns if an unpassable brick is ahead in a distance of <b>3</b>.
	 * 
	 * @return {@link Boolean}
	 */
	public boolean isBrickAhead() {
		return this.getTiles().brick(1, 0) || this.getTiles().brick(1, -1) || this.getTiles().brick(2, 0)
				|| this.getTiles().brick(2, -1) || this.getTiles().brick(3, 0) || this.getTiles().brick(3, -1);
	}

	/**
	 * Returns if a slope is ahead in a distance of <b>2</b>.
	 * 
	 * @return {@link Boolean}
	 */
	public boolean isSlopeAhead() {
		return this.getTiles().brick(0, 1) && (this.getTiles().emptyTile(1, 1) || this.getTiles().emptyTile(2, 1));
	}

	/**
	 * Returns if a enemy is in range of {@value #ENEMY_CHECK_DISTANCE} ahead of
	 * Mario.
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
	 * Checks if Mario is currently on the ground.
	 * 
	 * @return is Mario currently on the ground.
	 */
	public boolean isMarioOnGround() {
		return this.getMarioEntity().onGround;
	}

	/**
	 * Checks if Mario is currently falling.
	 * 
	 * @return is Mario currently falling.
	 */
	public boolean isMarioFalling() {
		return this.getMarioEntity().isFalling();
	}

	/**
	 * Returns an ordered list of enemies in range of
	 * {@value #ENEMY_CHECK_DISTANCE} ahead of Mario (ordered by closest enemy
	 * first)
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
		 * Y-Axis goes from -9 (top) to +9 (bottom) X-Axis goes from -9 (left)
		 * to +9 (right)
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
	 * Returns an ordered List of all {@link Enemy}s that are currently rendered
	 * within the grid (see debug).
	 * 
	 * @return
	 */
	public List<Enemy> getEnemiesOnScreen() {

		List<Enemy> enemyList = new ArrayList<Enemy>();

		/*
		 * Y-Axis goes from -9 (top) to +9 (bottom) X-Axis goes from -9 (left)
		 * to +9 (right)
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
	 * Returns the direct distance. Visually a straight line from Mario to
	 * target.
	 * 
	 * @return {@link double}
	 */
	public double getDistance(AbstractGameObject target) {
		return AbstractGameObject.getDistance(target);
	}

	/**
	 * Returns an ordered List of all {@link Block}s that are currently rendered
	 * within the grid (see debug).
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
		for (int i = 0; i < tileField.length; i++) {
			for (int j = 0; j < tileField[i].length; j++) {
				int posX = j - WIDTH_HALF_FIELD_GRID, posY = i - HEIGHT_HALF_FIELD_GRID;

				switch (tileField[i][j]) {
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

	///////////////////////
	// Advanced API methods
	///////////////////////

	/**
	 * Send control commands to Mario (like {@link MarioControl#runRight()})
	 * 
	 * @return the {@link MarioControl}
	 */
	public MarioControl getMarioControl() {
		return baseApi.getMarioControl();
	}

	/**
	 * Get some information about the Mario it self like
	 * {@link MarioEntity#onGround}
	 * 
	 * @return a {@link MarioEntity} instance
	 */
	public MarioEntity getMarioEntity() {
		return baseApi.getMarioEntity();
	}

	/**
	 * Send direct inputs to Mario is possible through a {@link MarioInput}
	 * instance (like {@link MarioInput#press})
	 * 
	 * @return a {@link MarioInput} instance
	 */
	public MarioInput getMarioInput() {
		return baseApi.getMarioInput();
	}

	/**
	 * Looking for {@link Entities} in Mario's near is possible by the
	 * {@link Entities} object.
	 * 
	 * @return a {@link Entities} instance
	 */
	public Entities getEntities() {
		return baseApi.getEntities();
	}

	/**
	 * Returns all {@link Tiles} within Mario's receptive field, which
	 * representing the terrain and collectible items such as coins, but not
	 * enemies.
	 * 
	 * @return All {@link Tiles} within Mario's receptive field.
	 */
	public Tiles getTiles() {
		return baseApi.getTiles();
	}

	/**
	 * Returns the {@link LevelScene} representation of the level.
	 * 
	 * @return the {@link LevelScene} representation of the level.
	 */
	public LevelScene getLevelScene() {
		return baseApi.getLevelScene();
	}

	////////////////
	// Debug methods
	////////////////

	/**
	 * Draws a path on the screen by connecting all given coordinates in
	 * sequential order.
	 * 
	 * @param coordinates
	 *            to draw
	 * @param vis
	 *            visualization component
	 * @param level
	 *            level scene
	 * @param env
	 *            environment
	 * @param g
	 *            graphics
	 */
	protected void drawPath(List<Coords> coordinates, VisualizationComponent vis, LevelScene level, IEnvironment env,
			Graphics g) {
		this.coordinates = coordinates;
		baseApi.debugDraw(vis, level, env, g);
	}

	/**
	 * Resets all dynamic data, such as coins collected, points, input, etc.
	 * 
	 * @param AgentOptions
	 *            options agent options after reset
	 * 
	 */
	@Override
	public final void reset(AgentOptions options) {
		baseApi.reset(options);
	}

	/**
	 * Receives the environment around Mario.
	 */
	@Override
	public final void observe(IEnvironment environment) {
		baseApi.observe(environment);
	}

	/**
	 * Gets the currently selected actions via {@link MarioInput}.
	 */
	@Override
	public final MarioInput actionSelection() {
		return baseApi.actionSelectionAI();
	}

	/**
	 * Observer method which receives the current intermediate reward (i.e. the
	 * points accumulated so far).
	 *
	 * The following table shows what is rewarded and the value of the reward:
	 * <table>
	 * <tr>
	 * <th>value</th>
	 * <th>reward</th>
	 * </tr>
	 * <tr>
	 * <td>distance</td>
	 * <td>1</td>
	 * </tr>
	 * <tr>
	 * <td>win</td>
	 * <td>1024</td>
	 * </tr>
	 * <tr>
	 * <td>coin</td>
	 * <td>16</td>
	 * </tr>
	 * <tr>
	 * <td>kill</td>
	 * <td>42</td>
	 * </tr>
	 * <tr>
	 * <td>killByFire</td>
	 * <td>4</td>
	 * </tr>
	 * <tr>
	 * <td>killByShell</td>
	 * <td>17</td>
	 * </tr>
	 * <tr>
	 * <td>killByStomp</td>
	 * <td>12</td>
	 * </tr>
	 * <tr>
	 * <td>mushroom</td>
	 * <td>58</td>
	 * </tr>
	 * <tr>
	 * <td>time left</td>
	 * <td>8</td>
	 * </tr>
	 * <tr>
	 * <td>hidden block</td>
	 * <td>24</td>
	 * </tr>
	 * <tr>
	 * <td>green mushroom</td>
	 * <td>58</td>
	 * </tr>
	 * <tr>
	 * <td>stomp</td>
	 * <td>10</td>
	 * </tr>
	 * </table>
	 * 
	 * @param float
	 *            intermediateReward the current intermediate reward
	 */
	@Override
	public final void receiveReward(float intermediateReward) {
		baseApi.receiveReward(intermediateReward);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		baseApi.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		baseApi.keyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		baseApi.keyTyped(e);

	}

}
