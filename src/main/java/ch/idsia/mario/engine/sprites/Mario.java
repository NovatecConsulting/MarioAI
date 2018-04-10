package ch.idsia.mario.engine.sprites;

import java.util.Arrays;

import ch.idsia.mario.engine.Art;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.Scene;
import ch.idsia.mario.engine.level.Level;

public class Mario extends Sprite // cloneable
{
	private boolean large = false;
	private boolean fire = false;
	private int coins = 0;
	private STATUS status = STATUS.RUNNING;
	private final int FractionalPowerUpTime = 0;
	private int gainedMushrooms;
	private int gainedFlowers;
	private int timesHurt=0;
	private boolean isMarioInvulnerable;

	public void reset(MODE marioMode) {
		large = (marioMode == MODE.MODE_LARGE || marioMode == MODE.MODE_FIRE);
		fire = marioMode == MODE.MODE_FIRE;
		resetCoins();
		gainedMushrooms = 0;
		gainedFlowers = 0;
	}

	public void setMode(MODE mode) {
		this.large = ((mode == MODE.MODE_LARGE));
		fire = (mode == MODE.MODE_FIRE);
	}

	public Mario.MODE getMode() {
		if (isLarge()) {
			if (fire)
				return Mario.MODE.MODE_FIRE;
			else
				return Mario.MODE.MODE_LARGE;
		}

		else
			return Mario.MODE.MODE_SMALL;
	}

	public enum MODE {
		MODE_SMALL, MODE_LARGE, MODE_FIRE
	}
	
	public enum STATUS {
		UNKNOWN(-1),LOSE(0),WIN(1),RUNNING(2);
		
		int status;
		
		private STATUS(int status) {
			this.status=status;
			}
		
		public int getStatus() {
			return status;
		}
	}

	public void resetCoins() {
		coins = 0;
	}

	public static final int KEY_LEFT = 0;
	public static final int KEY_RIGHT = 1;
	public static final int KEY_DOWN = 2;
	public static final int KEY_JUMP = 3;
	public static final int KEY_SPEED = 4;
	public static final int KEY_UP = 5;
	public static final int KEY_PAUSE = 6;
	public static final int KEY_DUMP_CURRENT_WORLD = 7;
	public static final int KEY_LIFE_UP = 8;
	public static final int KEY_WIN = 9;

	private static final float GROUND_INERTIA = 0.89f;
	private static final float AIR_INERTIA = 0.89f;

	private boolean[] keys;
	private boolean[] cheatKeys;
	private float runTime;
	private boolean wasOnGround = false;
	private boolean onGround = false;
	private boolean mayJump = false;
	private boolean ducking = false;
	private boolean sliding = false;
	private int jumpTime = 0;
	private float xJumpSpeed;
	private float yJumpSpeed;
	private boolean canShoot = false;

	private static final int width = 4;
	private int height = 24;

	private int facing;
	private int powerUpTime = 0; // exclude pause for rendering changes

	private int xDeathPos, yDeathPos;

	private int deathTime = 0;
	private int winTime = 0;
	private int invulnerableTime = 0;

	private Sprite carried = null;

	public Mario(LevelScene world, MODE mode) {
		kind = KIND_MARIO;
		this.spriteContext = world;
		keys = Scene.keys; // SK: in fact, this is already redundant due to using Agent
		cheatKeys = Scene.keys; // SK: in fact, this is already redundant due to using Agent
		x = 32;
		y = 0;
		
		facing = 1;

		if (mode == MODE.MODE_FIRE)
			setLarge(true, true);
		else if (mode == MODE.MODE_LARGE)
			setLarge(true, false);
		else
			setLarge(false, false);
	}

	public Mario(LevelScene alreadyCopied, Mario toCopy) {
		super(alreadyCopied, toCopy);

		this.large = toCopy.large;
		this.fire = toCopy.fire;
		this.coins = toCopy.coins;
		this.status = toCopy.status;
		this.gainedMushrooms = toCopy.gainedMushrooms;
		this.gainedFlowers = toCopy.gainedFlowers;
		this.isMarioInvulnerable = toCopy.isMarioInvulnerable;
		this.keys = Arrays.copyOf(toCopy.keys, toCopy.keys.length); //TESTING
		this.cheatKeys =  Arrays.copyOf(toCopy.cheatKeys,toCopy.cheatKeys.length);
		this.runTime = toCopy.runTime;
		this.wasOnGround = toCopy.wasOnGround;
		this.onGround = toCopy.onGround;
		this.mayJump = toCopy.mayJump;
		this.ducking = toCopy.ducking;
		this.sliding = toCopy.sliding;
		this.jumpTime = toCopy.jumpTime;
		this.xJumpSpeed = toCopy.xJumpSpeed;
		this.yJumpSpeed = toCopy.yJumpSpeed;
		this.canShoot = toCopy.canShoot;
		this.height = toCopy.height;
		this.facing = toCopy.facing;
		this.powerUpTime = toCopy.powerUpTime;
		this.xDeathPos = toCopy.xDeathPos;
		this.yDeathPos = toCopy.yDeathPos;
		this.deathTime = toCopy.deathTime;
		this.winTime = toCopy.winTime;
		this.invulnerableTime = toCopy.invulnerableTime;
		this.carried = toCopy.carried;
		this.lastLarge = toCopy.lastLarge;
		this.lastFire = toCopy.lastFire;
		this.newLarge = toCopy.newLarge;
		this.newFire = toCopy.newFire;
	}

	private boolean lastLarge;
	private boolean lastFire;
	private boolean newLarge;
	private boolean newFire;

	private void blink(boolean on) {
		this.large = (on ? newLarge : lastLarge);
		this.fire = on ? newFire : lastFire;

		if (isLarge()) {
			sheet = Art.mario;
			if (fire)
				sheet = Art.fireMario;

			xPicO = 16;
			yPicO = 31;
			wPic = hPic = 32;
		} else {
			sheet = Art.smallMario;

			xPicO = 8;
			yPicO = 15;
			wPic = hPic = 16;
		}

		calcPic();
	}

	void setLarge(boolean large, boolean fire) {
		if (fire)
			large = true;
		if (!large)
			fire = false;

		lastLarge = this.large;
		lastFire = this.fire;

		this.large = large;
		this.fire = fire;

		newLarge = this.large;
		newFire = this.fire;

		blink(true);
	}

	public void move() {
		if (winTime > 0) {
			winTime = (winTime + 1);

			xa = 0;
			ya = 0;
			return;
		}

		if (getDeathTime() > 0) {
			deathTime = (getDeathTime() + 1);
			if (getDeathTime() < 11) {
				xa = 0;
				ya = 0;
			} else if (getDeathTime() == 11) {
				ya = -15;
			} else {
				ya += 2;
			}
			x += xa;
			y += ya;
			return;
		}

		if (powerUpTime != 0) {
			if (powerUpTime > 0) {
				powerUpTime--;
				blink(((powerUpTime / 3) & 1) == 0);
			} else {
				powerUpTime++;
				blink(((-powerUpTime / 3) & 1) == 0);
			}

			if (powerUpTime == 0)
				//spriteContext.setPaused(false);

			calcPic();
			return;
		}

		if (invulnerableTime > 0)
			invulnerableTime--;
		visible = ((invulnerableTime / 2) & 1) == 0;

		wasOnGround = onGround;
		float sideWaysSpeed = keys[KEY_SPEED] ? 1.2f : 0.6f;
		// float sideWaysSpeed = onGround ? 2.5f : 1.2f;

		if (onGround) {
			if (keys[KEY_DOWN] && isLarge()) {
				ducking = true;
			} else {
				ducking = false;
			}
		}

		if (xa > 2) {
			facing = 1;
		}
		if (xa < -2) {
			facing = -1;
		}

		if (keys[KEY_JUMP] || (jumpTime < 0 && !onGround && !sliding)) {
			if (jumpTime < 0) {
				xa = xJumpSpeed;
				ya = -jumpTime * yJumpSpeed;
				jumpTime++;
			} else if (onGround && mayJump) {
				xJumpSpeed = 0;
				yJumpSpeed = -1.9f;
				jumpTime = 7;
				ya = jumpTime * yJumpSpeed;
				onGround = false;
				sliding = false;
			} else if (sliding && mayJump) {
				xJumpSpeed = -facing * 6.0f;
				yJumpSpeed = -2.0f;
				jumpTime = -6;
				xa = xJumpSpeed;
				ya = -jumpTime * yJumpSpeed;
				onGround = false;
				sliding = false;
				facing = -facing;
			} else if (jumpTime > 0) {
				xa += xJumpSpeed;
				ya = jumpTime * yJumpSpeed;
				jumpTime--;
			}
		} else {
			jumpTime = 0;
		}

		if (keys[KEY_LEFT] && !ducking) {
			if (facing == 1)
				sliding = false;
			xa -= sideWaysSpeed;
			if (jumpTime >= 0)
				facing = -1;
		}

		if (keys[KEY_RIGHT] && !ducking) {
			if (facing == -1)
				sliding = false;
			xa += sideWaysSpeed;
			if (jumpTime >= 0)
				facing = 1;
		}

		if ((!keys[KEY_LEFT] && !keys[KEY_RIGHT]) || ducking || ya < 0 || onGround) {
			sliding = false;
		}

		if (keys[KEY_SPEED] && canShoot && this.fire && spriteContext.getFireballsOnScreen() < 2) {
			spriteContext.addSprite(new Fireball(spriteContext, x + facing * 6, y - 20, facing));
		}
		//spriteContext.setPaused(false);
//		if (cheatKeys[KEY_WIN])
//			win();
		
		canShoot = !keys[KEY_SPEED];

		mayJump = (onGround || sliding) && !keys[KEY_JUMP];

		xFlipPic = facing == -1;

		runTime += (Math.abs(xa)) + 5;
		if (Math.abs(xa) < 0.5f) {
			runTime = 0;
			xa = 0;
		}

		calcPic();

		if (sliding) {
			for (int i = 0; i < 1; i++) {
				spriteContext.addSprite(new Sparkle(spriteContext, (int) (x + Math.random() * 4 - 2) + facing * 8,
						(int) (y + Math.random() * 4) - 24, (float) (Math.random() * 2 - 1), (float) Math.random() * 1,
						0, 1, 5));
			}
			ya *= 0.5f;
		}

		onGround = false;
		move(xa, 0);
		move(0, ya);

		if (y > spriteContext.getLevelHight() * 16 + 16) {
			die();
		}

		if (x < 0) {
			x = 0;
			xa = 0;
		}

		if (x > spriteContext.getLevelXExit() * 16) {
			x = spriteContext.getLevelXExit() * 16;
			win();
		}

		if (x > spriteContext.getLevelWidth() * 16) {
			x = spriteContext.getLevelWidth() * 16;
			xa = 0;
		}

		ya *= 0.85f;
		if (onGround) {
			xa *= GROUND_INERTIA;
		} else {
			xa *= AIR_INERTIA;
		}

		if (!onGround) {
			ya += 3;
		}

		if (getCarried() != null) {
			getCarried().x = x + facing * 8;
			getCarried().y = y - 2;
			if (!keys[KEY_SPEED]) {
				getCarried().release(this);
				setCarried(null);
			}
		}
	}

	private void calcPic() {
		int runFrame = 0;

		if (isLarge()) {
			runFrame = ((int) (runTime / 20)) % 4;
			if (runFrame == 3)
				runFrame = 1;
			if (getCarried() == null && Math.abs(xa) > 10)
				runFrame += 3;
			if (getCarried() != null)
				runFrame += 10;
			if (!onGround) {
				if (getCarried() != null)
					runFrame = 12;
				else if (Math.abs(xa) > 10)
					runFrame = 7;
				else
					runFrame = 6;
			}
		} else {
			runFrame = ((int) (runTime / 20)) % 2;
			if (getCarried() == null && Math.abs(xa) > 10)
				runFrame += 2;
			if (getCarried() != null)
				runFrame += 8;
			if (!onGround) {
				if (getCarried() != null)
					runFrame = 9;
				else if (Math.abs(xa) > 10)
					runFrame = 5;
				else
					runFrame = 4;
			}
		}

		if (onGround && ((facing == -1 && xa > 0) || (facing == 1 && xa < 0))) {
			if (xa > 1 || xa < -1)
				runFrame = isLarge() ? 9 : 7;

			if (xa > 3 || xa < -3) {
				for (int i = 0; i < 3; i++) {
					spriteContext.addSprite(
							new Sparkle(spriteContext, (int) (x + Math.random() * 8 - 4), (int) (y + Math.random() * 4),
									(float) (Math.random() * 2 - 1), (float) Math.random() * -1, 0, 1, 5));
				}
			}
		}

		if (isLarge()) {
			if (ducking)
				runFrame = 14;
			height = ducking ? 12 : 24;
		} else {
			height = 12;
		}

		xPic = runFrame;
	}

	private boolean move(float xa, float ya) {
		while (xa > 8) {
			if (!move(8, 0))
				return false;
			xa -= 8;
		}
		while (xa < -8) {
			if (!move(-8, 0))
				return false;
			xa += 8;
		}
		while (ya > 8) {
			if (!move(0, 8))
				return false;
			ya -= 8;
		}
		while (ya < -8) {
			if (!move(0, -8))
				return false;
			ya += 8;
		}

		boolean collide = false;
		if (ya > 0) {
			if (isBlocking(x + xa - width, y + ya, xa, 0))
				collide = true;
			else if (isBlocking(x + xa + width, y + ya, xa, 0))
				collide = true;
			else if (isBlocking(x + xa - width, y + ya + 1, xa, ya))
				collide = true;
			else if (isBlocking(x + xa + width, y + ya + 1, xa, ya))
				collide = true;
		}
		if (ya < 0) {
			if (isBlocking(x + xa, y + ya - height, xa, ya))
				collide = true;
			else if (collide || isBlocking(x + xa - width, y + ya - height, xa, ya))
				collide = true;
			else if (collide || isBlocking(x + xa + width, y + ya - height, xa, ya))
				collide = true;
		}
		if (xa > 0) {
			sliding = true;
			if (isBlocking(x + xa + width, y + ya - height, xa, ya))
				collide = true;
			else
				sliding = false;
			if (isBlocking(x + xa + width, y + ya - height / 2, xa, ya))
				collide = true;
			else
				sliding = false;
			if (isBlocking(x + xa + width, y + ya, xa, ya))
				collide = true;
			else
				sliding = false;
		}
		if (xa < 0) {
			sliding = true;
			if (isBlocking(x + xa - width, y + ya - height, xa, ya))
				collide = true;
			else
				sliding = false;
			if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya))
				collide = true;
			else
				sliding = false;
			if (isBlocking(x + xa - width, y + ya, xa, ya))
				collide = true;
			else
				sliding = false;
		}

		if (collide) {
			if (xa < 0) {
				x = (int) ((x - width) / 16) * 16 + width;
				this.xa = 0;
			}
			if (xa > 0) {
				x = (int) ((x + width) / 16 + 1) * 16 - width - 1;
				this.xa = 0;
			}
			if (ya < 0) {
				y = (int) ((y - height) / 16) * 16 + height;
				jumpTime = 0;
				this.ya = 0;
			}
			if (ya > 0) {
				y = (int) ((y - 1) / 16 + 1) * 16 - 1;
				onGround = true;
			}
			return false;
		} else {
			x += xa;
			y += ya;
			return true;
		}
	}

	private boolean isBlocking(float _x, float _y, float xa, float ya) {
		int x = (int) (_x / 16);
		int y = (int) (_y / 16);
		if (x == (int) (this.x / 16) && y == (int) (this.y / 16))
			return false;

		boolean blocking = spriteContext.levelIsBlocking(x, y, xa, ya);

		byte block = spriteContext.levelGetBlock(x, y);

		if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_PICKUPABLE) > 0) {
			getCoin();
			spriteContext.setLevelBlock(x, y, (byte) 0);
			for (int xx = 0; xx < 2; xx++)
				for (int yy = 0; yy < 2; yy++)
					spriteContext.addSprite(new Sparkle(spriteContext, x * 16 + xx * 8 + (int) (Math.random() * 8),
							y * 16 + yy * 8 + (int) (Math.random() * 8), 0, 0, 0, 2, 5));
		}

		if (blocking && ya < 0) {
			spriteContext.bump(x, y, isLarge());
		}

		return blocking;
	}

	public void stomp(Enemy enemy) {
		if (getDeathTime() > 0 || spriteContext.isPaused())
			return;

		float targetY = enemy.y - enemy.height / 2;
		move(0, targetY - y);

		xJumpSpeed = 0;
		yJumpSpeed = -1.9f;
		jumpTime = 8;
		ya = jumpTime * yJumpSpeed;
		onGround = false;
		sliding = false;
		invulnerableTime = 1;
	}

	public void stomp(Shell shell) {
		if (getDeathTime() > 0 || spriteContext.isPaused())
			return;

		if (keys[KEY_SPEED] && shell.getFacing() == 0) {
			setCarried(shell);
			shell.setCarried(true);
		} else {
			float targetY = shell.y - Shell.getHeight() / 2;
			move(0, targetY - y);

			xJumpSpeed = 0;
			yJumpSpeed = -1.9f;
			jumpTime = 8;
			ya = jumpTime * yJumpSpeed;
			onGround = false;
			sliding = false;
			invulnerableTime = 1;
		}
	}

	public void getHurt() {
		if (getDeathTime() > 0 || spriteContext.isPaused() || isMarioInvulnerable())
			return;

		if (invulnerableTime > 0)
			return;

		timesHurt++;
		if (isLarge()) {
			//spriteContext.setPaused(true);
			powerUpTime = -3 * FractionalPowerUpTime;
			if (fire) {
				setLarge(true, false);
			} else {
				setLarge(false, false);
			}
			invulnerableTime = 32;
		} else {
			die();
		}
	}

	private void win() {
		xDeathPos = ((int) x);
		yDeathPos = ((int) y);
		//spriteContext.setPaused(true);
		winTime = 1;
		status = STATUS.WIN;
	}

	public void die() {
		xDeathPos = ((int) x);
		yDeathPos = ((int) y);
		//spriteContext.setPaused(true);
		deathTime = (25);
		status = STATUS.LOSE;
	}

	public void getFlower() {
		if (getDeathTime() > 0 || spriteContext.isPaused())
			return;

		if (!fire) {
			//spriteContext.setPaused(true);
			powerUpTime = 3 * FractionalPowerUpTime;
			setLarge(true, true);
		}

		++gainedFlowers;
	}

	public void getMushroom() {
		if (getDeathTime() > 0 || spriteContext.isPaused())
			return;

		if (!isLarge()) {
			//spriteContext.setPaused(true);
			powerUpTime = 3 * FractionalPowerUpTime;
			setLarge(true, false);
		}

		++gainedMushrooms;
	}

	public void kick(Shell shell) {
		 if (deathTime > 0 || spriteContext.isPaused()) return;

		if (keys[KEY_SPEED]) {
			setCarried(shell);
			shell.setCarried(true);
		} else {
			invulnerableTime = 1;
		}
	}

	public void stomp(BulletBill bill) {
		if (getDeathTime() > 0 || spriteContext.isPaused())
			return;

		float targetY = bill.y - BulletBill.getHeight() / 2;
		move(0, targetY - y);

		xJumpSpeed = 0;
		yJumpSpeed = -1.9f;
		jumpTime = 8;
		ya = jumpTime * yJumpSpeed;
		onGround = false;
		sliding = false;
		invulnerableTime = 1;
	}

	public byte getKeyMask() {
		int mask = 0;
		for (int i = 0; i < 7; i++) {
			if (keys[i])
				mask |= (1 << i);
		}
		return (byte) mask;
	}

	public void setKeys(byte mask) {
		for (int i = 0; i < 7; i++) {
			keys[i] = (mask & (1 << i)) > 0;
		}
	}

	public void getCoin() {
		coins = (getCoins() + 1);
	}

	public STATUS getStatus() {
		return status;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public boolean mayJump() {
		return mayJump;
	}

	public int getFacing() {
		return facing;
	}

	public boolean isLarge() {
		return large;
	}

	public int getCoins() {
		return coins;
	}

	public int getGainedMushrooms() {
		return gainedMushrooms;
	}

	public int getGainedFlowers() {
		return gainedFlowers;
	}

	public boolean isMarioInvulnerable() {
		return isMarioInvulnerable;
	}

	public void setMarioInvulnerable(boolean isMarioInvulnerable) {
		this.isMarioInvulnerable = isMarioInvulnerable;
	}

	public boolean[] getKeys() {
		return keys;
	}

	public void setKeys(boolean[] keys) {
		this.keys = keys;
	}

	public boolean[] getCheatKeys() {
		return cheatKeys;
	}

	public void setCheatKeys(boolean[] cheatKeys) {
		this.cheatKeys = cheatKeys;
	}

	public boolean wasOnGround() {
		return wasOnGround;
	}

	public int getHeight() {
		return height;
	}

	public Sprite getCarried() {
		return carried;
	}

	public void setCarried(Sprite carried) {
		this.carried = carried;
	}

	public int getWinTime() {
		return winTime;
	}

	public int getDeathTime() {
		return deathTime;
	}

	public int getTimesHurt() {
		return timesHurt;
	}

	public int getyDeathPos() {
		return yDeathPos;
	}

	public int getxDeathPos() {
		return xDeathPos;
	}

	@Override
	public String toString() {
		return "Mario [ x="+x+", y="+y+", large=" + large + ", fire=" + fire + ", status=" + status + "]";
	}

	public boolean isSliding() {
		return sliding;
	}

}