package de.novatec.marioai.tools;

import java.util.List;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.environments.Environment;
import de.novatec.mario.engine.generalization.Entities;
import de.novatec.mario.engine.generalization.Tiles;
import de.novatec.mario.engine.generalization.Entities.EntityType;
import de.novatec.mario.engine.generalization.Entity;
import de.novatec.mario.engine.generalization.Tile;
import de.novatec.mario.engine.generalization.Tiles.TileType;

public abstract class MarioNtAgent implements Agent{
	
	private MarioInput input=new MarioInput();
	
	private MarioControl control=new MarioControl(input);
	
	private Environment env;
	
	private Tiles tiles;
	
	private Entities entities;
	
	private static final int ENEMY_CHECK_DISTANCE=4/*,BRICK_CHECK_DISTANCE=3*/;

	@Override
	public void reset() {		
	}

	@Override
	public final boolean[] getAction(Environment observation) {
		
		env=observation;
		if(tiles==null) tiles=new Tiles(observation);
		else tiles.setEnvironment(observation);
		
		if(entities==null) entities=new Entities(observation);
		else entities.setEnvironment(observation);
		
		control.setEnvironment(observation);
		
		control.tick();
		input.tick();
		
		MarioInput input=doAiLogic();
		
		return input.toArray();
	}

	@Override
	public final AGENT_TYPE getType() {
		return AGENT_TYPE.AI;
	}

	@Override
	public String getName() {
		return "This should not be seen! Please give your agent a proper name!";
	}

	@Override
	public final void setName(String name) {
			}
	
	public abstract MarioInput doAiLogic(); // must be implemented by agents who extend this!
		
	public final MarioInput getMarioInput() {
		return input;
	}
	
	public final MarioControl getMarioControl() {
		return control;
	}
	
	////////////////////////////////
	// Mario interaction methods
	////////////////////////////////
	
	public final void moveRight() {
		this.getMarioControl().moveRight();
	}
	
	public final void moveLeft() {
		this.getMarioControl().moveLeft();
	}
	
	public final void sprint() {
		this.getMarioControl().sprint();
	}
	
	public final void jump() {
		this.getMarioControl().jump();
	}
	
	public final void shoot() {
		this.getMarioControl().shoot();
	}
	
	////////////////////////////////
	// Environment detection methods
	////////////////////////////////
	
	
	////--- MarioControl
	public final boolean mayShoot() {
		return this.env.mayShoot();
	}
	
	public final boolean mayJump() {
		return this.env.mayMarioJump();
	}
	
	public final float[] getMarioFloatPos(){
		return this.env.getMarioFloatPos();
	}
	
	public final boolean isOnGround() {
		return this.env.isMarioOnGround();
	}
	
	public final boolean isFalling() {
		return this.env.isFalling();
	}
	
	public final boolean isCarrying() {
		return this.env.isMarioCarrying();
	}
	
	///--- Simple Detection Methods
	public final boolean isBrickAhead() {
		return isBrick(1,0)||isBrick(1,-1)||isBrick(2, 0)||isBrick(2, -1)||isBrick(3, 0)||isBrick(3, -1);
	}
	
	public final boolean isSlopeAhead() {
		return isBrick(0,1)&&isEmpty(1, 1)&&isEmpty(2, 1);
	}
	
	public final boolean isDeepSlopeAhead() {
		return isSlopeAhead()&&isEmpty(1,2)&&isEmpty(2, 2);
	}
	
	public final boolean isEnemyAhead() {

		for(int i=1;i<ENEMY_CHECK_DISTANCE;i++) {
			if(isDangerousAt(i, 0)||isDangerousAt(i, 1)) return true;
		}
		return false;
	}
	
	public final boolean isHoleAhead() { //TODO IMPLEMENT
		return false;
	}
	
	public final boolean isQuestionbrickAbove() {
		return getTile(0,-1)==TileType.QUESTION_BRICK||getTile(0,-2)==TileType.QUESTION_BRICK||getTile(0,-3)==TileType.QUESTION_BRICK;
	}
	
	////--- Tiles - Abstracted Level Information
	
	public final Tiles getTiles() {
		return tiles;
	}

	public final List<Tile> getInteractiveBlocksOnScreen(){
		return tiles.getInteractiveBlocksOnScreen();
	}
	
	public final TileType getTile(int x, int y) {
		return tiles.getTile(x, y);
	}
	
	public final  boolean isBrick(int x,int y) {
		return tiles.isBrick(x, y);
	}
	
	public final boolean isEmpty(int x, int y) {
		return tiles.isEmpty(x, y);
	}
	
	public final boolean isNotEmpty(int x,int y) {
		return tiles.isNotEmpty(x, y);
	}
	
	////--- Entities - Abstracted Enemy Detection
	
	public final Entities getEntities() {
		return entities;
	}
	
	public final List<Entity> getEntities(int x,int y){
		return entities.getEntities(x, y);
	}
	
	public final List<Entity> getAllEntitiesOnScreen(){
		return entities.getEntities();
	}
	
	public final List<Entity> getAllEnemiesOnScreen(){
		return entities.getEnemies();
	}
	
	public final List<Entity> getEnemies(int x,int y){
		return entities.getEnemies(x,y);
	}
	
	public final EntityType getMostDangrousEntityTypeAt(int x,int y) {
		return entities.getMostDangrousEntityTypeAt(x, y);
	}
	
	public final boolean isNothingAt (int x,int y) {
		return entities.isNothingAt(x, y);
	}
	
	public final boolean isSomethingAt (int x, int y) {
		return entities.isSomethingAt(x, y);
	}
	
	public final boolean isDangerousAt(int x, int y) {
		return entities.isDangerousAt(x, y);
	}
	
	public final boolean isSquishableAt(int x, int y) {
		return entities.isSquishableAt(x, y);
	}
	
	public final boolean isShootableAt(int x, int y) {
		return entities.isShootableAt(x, y);
	}
	
	public final boolean isCollectableAt(int x,int y) {
		return entities.isCollectableAt(x, y);
	}
	
	//--- A* Helper Methods
	
	public final LevelScene getDeepCopyOfLevelScene(){
		return env.getLevelScene().getDeepCopy();
	}
	
	public final void setMarioInput(LevelScene alreadyCopied,MarioInput input) { //RLY NEEDED?
		alreadyCopied.setMarioKeys(input.toArray());
	}
	////////////////////////////////
	// Debug methods
	////////////////////////////////

	public void showMarioViewAsAscii() {
		byte[][] tmp=env.getCompleteObservation();
		System.out.println(" --------------------------------Marios Receptive Field:--------------------------------");
    	for(int i=0;i<tmp.length;i++) {
    		for(int j=0;j<tmp[i].length;j++) {
    			if(i==11&&j==11) System.out.print("   M");
    			
    			else
    			if(tmp[i][j]<10&&tmp[i][j]>=0)  System.out.print("   "+tmp[i][j]);
    			
    			else if(tmp[i][j]<0) System.out.print(" "+tmp[i][j]);
    			else System.out.print("  "+tmp[i][j]);
    		}
    		System.out.println();
    	}
    	System.out.println(" ----------------------------------------------------------------------------------------");
    	System.out.println();
	}
	
}
