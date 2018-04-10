package ch.idsia.tools;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import ch.idsia.mario.environments.Environment;

public class KeyboardInterpreter implements KeyListener{
	
	private Environment env;

	public KeyboardInterpreter(Environment env) {
		env.registerKeyboardListener(this);
		this.env=env;
	}
	
	@Override
	public void keyTyped(KeyEvent e) { //not used
	}

	@Override
	public void keyPressed(KeyEvent e) { //not used
	}

	@Override
	public void keyReleased(KeyEvent e) {
	//System.out.println(e.getKeyChar()+" "+e.getKeyCode()); //debug
		
		switch(e.getKeyCode()) {
		case KeyEvent.VK_P:
			env.togglePaused();
			break;
		case KeyEvent.VK_I:
			env.setPaused(true);
			env.showMarioViewAsAscii();
			break;
		case KeyEvent.VK_O:
			env.toggleDebugView();
			break;
		}
		
		
	}

}
