package de.novatec.marioai;

import java.awt.event.KeyEvent;

import ch.idsia.agents.controllers.MarioHijackAIBase;
import ch.idsia.agents.controllers.modules.Entities;
import ch.idsia.agents.controllers.modules.Tiles;
import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.SimulatorOptions;
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity;
import ch.idsia.benchmark.mario.engine.input.MarioControl;
import ch.idsia.benchmark.mario.engine.input.MarioInput;

/**
 * This class is wrapping the given {@link MarioHijackAIBase} API in order to
 * hide irrelevant members. It will be used by {@link MarioAiAgent}
 *
 * @author tj NovaTec GmbH
 */
public abstract class MarioAgentNtWrapper extends MarioHijackAIBase {

	/**
	 * @see {@link MarioAiAgent#getMarioControl}
	 */
	public MarioControl getMarioControl() {
		return control;
	}

	/**
	 * @see {@link MarioAiAgent#getMarioEntity}
	 */
	public MarioEntity getMarioEntity() {
		return mario;
	}

	/**
	 * @see {@link MarioAiAgent#getMarioInput}
	 */
	public MarioInput getMarioInput() {
		return action;
	}

	/**
	 * @see {@link MarioAiAgent#getEntities}
	 */
	public Entities getEntities() {
		return e;
	}

	/**
	 * @see {@link MarioAiAgent#getTiles}
	 */
	public Tiles getTiles() {
		return t;
	}

	/**
	 * @see {@link MarioAiAgent#getLevelScene()}
	 */
	public LevelScene getLevelScene() {
		return levelScene;
	}

	@Override
	protected void toggleKey(KeyEvent e, boolean isPressed) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		// Pauses/Resumes the simulation.
		case KeyEvent.VK_P:
			if (isPressed) {
				SimulatorOptions.isGameplayStopped = !SimulatorOptions.isGameplayStopped;
			}
			return;

		// Pauses the simulation if not paused already and pokes the simulation
		// to compute the next frame.
		case KeyEvent.VK_N:
			if (isPressed) {
				if (!SimulatorOptions.isGameplayStopped) {
					SimulatorOptions.isGameplayStopped = true;
				}
				SimulatorOptions.nextFrameIfPaused = true;
			}
			return;
		}
	}

}
