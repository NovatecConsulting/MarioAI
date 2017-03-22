package de.hskl.info_ag.mariocontest;

import java.util.List;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import de.hskl.info_ag.mariocontest.dto.Block;

public class MyAgent extends MarioDemoAgent {
	@Override
	public MarioInput doAiLogic() {
		runRight();

		if (isBrickAhead())
			jump();

		if (isSlopeAhead())
			jump();

		if (isEnemyAhead()) {
			shoot();
			jump();
		}
		
		List<Block> blocks = getInteractiveBlocksOnScreen();
		if(!blocks.isEmpty())
			System.err.println("BLOCKS DETECTED:");
		for(Block b : blocks) {
			System.err.println("Block[X: " + b.getRelPosX() + ", Y: " + b.getRelPosY() + ", Type: " + b.getBlockType() + "]");
		}
		if(!blocks.isEmpty()) {
			System.err.println();
			System.err.println();
			waiting(2000);
		}
		
		

		return getMarioInput();
	}
}