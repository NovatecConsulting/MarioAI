package de.novatec.marioai;

public class Enemy extends AbstractGameObject {
	final EnemyType enemyType;

	public Enemy(int relPosX, int relPosY, EnemyType enemyType) {
		super(relPosX, relPosY);
		this.enemyType = enemyType;
	}

	public EnemyType getEnemyType() {
		return enemyType;
	}

}
