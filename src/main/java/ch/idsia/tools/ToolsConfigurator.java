package ch.idsia.tools;

import ch.idsia.mario.engine.MarioComponent;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 29, 2009
 * Time: 6:27:25 PM
 * Package: .Tools
 */
public class ToolsConfigurator extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static JFrame marioComponentFrame = null;
	
    public static MarioComponent CreateMarioComponentFrame(RunnerOptions rOptions) //creates the MarioWindow
    {
    	MarioComponent marioComponent;
    
        marioComponentFrame = new JFrame("MarioAI: "+rOptions.getAgent().getName() );
        marioComponent = new MarioComponent(rOptions.getWindowHeigth(),rOptions.getWindowWidth()); //CHANGES WINDOW SIZE
        marioComponentFrame.setContentPane(marioComponent);
        marioComponent.init();
        
        marioComponentFrame.pack();
        marioComponentFrame.setResizable(false);
        marioComponentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     
        marioComponentFrame.setAlwaysOnTop(rOptions.isViewAlwaysOnTop());
        marioComponentFrame.setLocation(rOptions.getViewLocation());
        marioComponentFrame.setVisible(rOptions.isViewable());
        
        return marioComponent;
    }

}