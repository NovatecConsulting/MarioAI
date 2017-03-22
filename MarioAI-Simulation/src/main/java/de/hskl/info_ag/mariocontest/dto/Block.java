package de.hskl.info_ag.mariocontest.dto;

import de.hskl.info_ag.mariocontest.enums.BlockType;

public class Block extends AbstractGameObject {

	private final BlockType blockType;
	
	public Block(int relPosX, int relPosY, BlockType blockType) {
		super(relPosX, relPosY);
		this.blockType = blockType;
	}
	
	public BlockType getBlockType() {
		return blockType;
	}
	
}
