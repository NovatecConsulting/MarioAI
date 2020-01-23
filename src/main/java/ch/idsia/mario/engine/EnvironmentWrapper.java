package ch.idsia.mario.engine;

import ch.idsia.mario.engine.sprites.Mario.STATUS;
import ch.idsia.mario.environments.Environment;
import de.novatec.mario.engine.generalization.Coordinates;

/**
 * wrapper for the environment class, this is supposed to become the only way to get info from the environment (so the player can't manipulate it)
 */
public class EnvironmentWrapper {
	private Environment env;
	
	public EnvironmentWrapper(Environment copy) {
		env = copy;
	}

	///////////////////////////////////
	// Environment detection methods //
	///////////////////////////////////
	
	/**
	* Can Mario shoot?
	* @return a boolean value indicating whether Mario can shoot
	*/
	public final boolean mayShoot() {
		return this.env.getLevelScene().mayMarioShoot();
	}
	
	/**
	* Can Mario jump?
	* @return a boolean value indicating whether Mario can jump
	*/
	public final boolean mayJump() {
		return this.env.getLevelScene().mayMarioJump();
	}
	
	/**
	* Mario's Map Position as Coordinates, contains X and Y position.
	* @return an instance of Coordinates with Mario's (map) position.
	*/
	public final Coordinates getMarioPos(){
		return this.env.getLevelScene().getMarioPos();
	}
	
	/**
	* Mario's Physical Position as Coordinates
	* @return an instance of Coordinates with Mario's (physical) position.
	*/
	public final Coordinates getMarioFloatPos() {
		return this.env.getLevelScene().getMarioFloatPos();
	}
	
	/**
	* Returns Mario's Map X Position
	* @return an integer value with Mario's X position
	*/
	public final int getMarioMapX() {
		return this.env.getLevelScene().getMarioMapX();
	}
	
	/**
	* Returns Mario's Map Y Position
	* @return an integer value with Mario's Y position
	*/
	public final int getMarioMapY() {
		return this.env.getLevelScene().getMarioMapY();
	}
	
	/**
	* Returns Mario's physical X Position
	* @return a float value with Mario's physical X Position 
	*/
	public final float getMarioX() {
		return env.getLevelScene().getMarioX();
	}
	
	/**
	* Returns Mario's physical Y Position
	* @return a float value with Mario's physical Y Position
	*/
	public final float getMarioY() {
		return env.getLevelScene().getMarioY();
	}
	
	/**
	* Is Mario on the ground?
	* @return a boolean value indicating whether Mario is on the ground
	*/
	public final boolean isOnGround() {
		return this.env.getLevelScene().isMarioOnGround();
	}
	
	/**
	* Is Mario falling?
	* @return a boolean value indicating whether Mario is falling
	*/
	public final boolean isFalling() {
		return this.env.getLevelScene().isMarioFalling();
	}
	
	/**
	* Is Mario carrying a shell?
	* @return a boolean value indicating whether Mario is carrying a shell
	*/
	public final boolean isCarrying() {
		return this.env.getLevelScene().isMarioCarrying();
	}
	
	/**
	* Returns Marios status.
	* @return an enum-object describing Marios status
	*/
	public final STATUS getMarioStatus() {
		return this.env.getLevelScene().getMarioStatus();
	}
	
	//--- A* Helper Methods
	
	/**
	 * The score of the actual LevelScene. The value of score is determined by the given task. 
	 * @return a double value with the Score of the actual LevelScene 
	 */
	public final double getActualScore() {
		return env.getLevelScene().getScore();
	}
	
	/**
	 * Returns a byte representation of mario's complete view
	 * @param zLevelScene generalization factor of the levelscene
	 * @param zLevelEnemies generalization factor of the enemies
	 * @return a byte[] array 
	 */
	public byte[][] getMergedObservationZ(int zLevelScene, int zLevelEnemies) {
		return env.getLevelScene().mergedObservation(zLevelScene, zLevelEnemies);
	}
	
	/**
	 * Returns a byte representation of mario's levelscene view
	 * @param zLevelScene  generalization factor of the levelscene
	 * @return a byte[] array 
	 */
	public byte[][] getLevelSceneObservationZ(int zLevelScene) {
		return env.getLevelScene().levelSceneObservation(zLevelScene);
	}

	/**
	 * Returns a byte representation of mario's enemy view
	 * @param zLevelEnemies generalization factor of the enemies
	 * @return a byte[] array
	 */
	public byte[][] getEnemiesObservationZ(int zLevelEnemies) {
    	return env.getLevelScene().enemiesObservation(zLevelEnemies);
	}
	
	////////////////////////////////
	// Debug methods
	////////////////////////////////

	/**
	* Shows an Ascii representation of Mario's view
	*/
	public final void showMarioViewAsAscii() {
		env.showMarioViewAsAscii();
	}
}