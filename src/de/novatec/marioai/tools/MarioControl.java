package de.novatec.marioai.tools;

import ch.idsia.mario.environments.Environment;

/**
 * {@link MarioInput} wrapper that abstracts Mario movement into callable
 * methods. It hides or articulates some intricacies of Mario controls (e.g.
 * sprint is bound to the same key as shooting etc.).
 * 
 * modified by rgu @Novatec
 * 
 * @author Jakub 'Jimmy' Gemrot, gemrot@gamedev.cuni.cz
 * 
 */
public class MarioControl {

	private MarioInput input;
	
	private Environment env;

	private int frame;

	private boolean shooting;

	private boolean sprinting;

	private int lastFrameShootCalled;

	private int lastFrameSprintCalled;

	public MarioControl(MarioInput input) {
		this.input = input;
	}
	
	public void setEnvironment(Environment env) {
		this.env=env;
	}

	public void tick() {
		++frame;

		// DO WE STILL WANT TO BE SPRINTING?
		sprinting = (lastFrameSprintCalled + 1) == frame;

		// DO WE STILL WANT TO BE SHOOTING?
		shooting = (lastFrameShootCalled + 1) == frame;

		// WHAT IS THE STATE OF SPEED/SHOOT BUTTON?
		if (input.isPressed(MarioKey.SPEED)) {
			// SPEED/SHOOT WAS PRESSED LAST FRAME
			if (shooting && env.mayShoot()) {
				input.release(MarioKey.SPEED);
			} else if (!sprinting) {
				input.release(MarioKey.SPEED);
			}
		} else {
			// SPEED/SHOOT BUTTON WAS NOT PRESSED LAST FRAME
			if (shooting && env.mayShoot()) {
				input.press(MarioKey.SPEED);
			} else if (sprinting) {
				input.press(MarioKey.SPEED);
			}
		}

		input.release(MarioKey.LEFT);
		input.release(MarioKey.RIGHT);
		input.release(MarioKey.JUMP);
		input.release(MarioKey.DOWN);
	}

	public void reset() {
		frame = 0;
		lastFrameShootCalled = 0;
		shooting = false;
		sprinting = false;
		input.reset();
	}

	public void shoot() {
		// CAN WE EVEN SHOOT
		if (!env.mayShoot()) {
			// => NO!
			return;
		}

		// MARK THE FRAME shoot() WAS CALLED
		lastFrameShootCalled = frame;

		if (shooting) {
			// SHOOTING ALREADY HANDLED BY PREVIOUS shoot() CALL OR tick()
			return;
		}

		// WE WANT TO START SHOOTING NOW
		shooting = true;

		// DETERMINE STATE OF SPEED/SHOOT BUTTON
		if (input.isPressed(MarioKey.SPEED)) {
			// BUTTON PRESSED ...
			// AND WE CAN SHOOT
			// => release it
			input.release(MarioKey.SPEED);
		} else {
			// BUTTON NOT PRESSED
			// AND WE CAN SHOOT
			// => press it
			input.press(MarioKey.SPEED);
		}
	}

	public void sprint() {
		// MARK THE FRAME sprint() WAS CALLED
		lastFrameSprintCalled = frame;

		if (sprinting) {
			// SHOOTING ALREADY HANDLED BY PREVIOUS shoot() CALL OR tick()
			return;
		}

		// WE WANT TO START SPRINTING NOW
		sprinting = true;

		// DO WE WANT TO SHOOT AND CAN WE START SHOOTING?
		if (shooting && env.mayShoot()) {
			// YES
			// => shooting has a priority over sprinting
			return;
		}

		// WE EITHER DO NOT WANT TO SHOOT or WE CANNOT SHOOT
		// => sprint
		input.press(MarioKey.SPEED);
	}

	/**
	 * Mario will JUMP if able.
	 */
	public void jump() {
		if (!env.mayMarioJump() && !env.isFalling()) {
			return;
		}
		input.press(MarioKey.JUMP);
	}

	/**
	 * Mario will move LEFT, overrides previous {@link #moveRight()}.
	 */
	public void moveLeft() {
		input.press(MarioKey.LEFT);
		input.release(MarioKey.RIGHT);
	}

	/**
	 * Mario will move RIGHT, overrides previous {@link #moveLeft()}.
	 */
	public void moveRight() {
		input.press(MarioKey.RIGHT);
		input.release(MarioKey.LEFT);
	}

	/**
	 * Can Mario shoot?
	 * 
	 * @return
	 */
	public boolean canShoot() {
		return shooting;
	}

	/**
	 * Can Mario sprint?
	 * 
	 * @return
	 */
	public boolean canSprint() {
		return sprinting;
	}

	/**
	 * Presses a given {@link MarioKey}.
	 * 
	 * @param key
	 *            to press
	 */
	public void press(MarioKey key) {
		input.press(key);
	}

}
