package ch.idsia.tools;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import ch.idsia.mario.environments.Environment;

/**
 * KeyListener for executing actions on the given environment based on user input
 * @author rgu
 *
 */
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
		case KeyEvent.VK_T:
			env.performTick();
			break;
		case KeyEvent.VK_H:
			env.setPaused(true);
			env.swapAgent();
			break;
		case KeyEvent.VK_MINUS:
		case 109:
			env.resizeView(env.getActualDimension().width-32, env.getActualDimension().height-24);
			break;
		case KeyEvent.VK_PLUS:
		case 107:
			env.resizeView(env.getActualDimension().width+32, env.getActualDimension().height+24);
		break;
		case 520:
		case 106:	
			env.resizeView(env.getInitialDimension().height, env.getInitialDimension().width);
		break;
		
		}
		
	}

}
