package de.hskl.info_ag.mariocontest.dto;

import de.hskl.info_ag.mariocontest.enums.CollectibleType;

public class Collectible extends AbstractGameObject {

	private final CollectibleType collectibleType;
	
	public Collectible(int relPosX, int relPosY, CollectibleType collectibleType) {
		super(relPosX, relPosY);
		this.collectibleType = collectibleType;
	}
	
	public CollectibleType getCollectibleType() {
		return collectibleType;
	}
	
}
