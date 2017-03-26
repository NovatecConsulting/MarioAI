package de.novatec.marioai;

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
