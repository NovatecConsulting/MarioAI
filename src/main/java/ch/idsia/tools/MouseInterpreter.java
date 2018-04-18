package ch.idsia.tools;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.mario.engine.sprites.Mario.STATUS;

public class MouseInterpreter implements MouseListener {
	
	private ToolsConfigurator configurator;
	
	public MouseInterpreter(ToolsConfigurator configurator) {
		this.configurator=configurator;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//System.out.println(e.getSource());
		Object tmp=e.getSource();
		if( tmp instanceof MarioComponent) {
		if(((MarioComponent)tmp).getLevelScene().getMarioStatus()==STATUS.RUNNING)configurator.switchControlledComponent((MarioComponent)tmp);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

}
