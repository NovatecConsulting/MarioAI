package de.novatec.marioai.agents;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import ch.idsia.mario.environments.Environment;
import de.novatec.marioai.tools.MarioInput;
import de.novatec.marioai.tools.MarioNtAgent;

public class HumanKeyboardAgent extends MarioNtAgent implements KeyListener {

	private boolean[] action=new boolean[Environment.numberOfButtons];
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		toggleKey(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		toggleKey(e.getKeyCode(), false);
	}

	@Override
	public MarioInput doAiLogic() {
		return MarioInput.arrayToMarioInput(action);
	}

	@Override
	public String getName() {
		return "Human Keyboard Agent";
	}

	private void toggleKey(int keyCode, boolean isPressed) {
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			action[0]=isPressed;
			break;
		case KeyEvent.VK_RIGHT:
			action[1]=isPressed;
			break;
		case KeyEvent.VK_DOWN:
			action[2]=isPressed;
			break;
		case KeyEvent.VK_S:
			action[3]=isPressed;
			break;
		case KeyEvent.VK_A:
			action[4]=isPressed;
			break;
		}
	}

}
