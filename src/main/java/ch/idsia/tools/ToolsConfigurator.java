package ch.idsia.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.mario.engine.sprites.Mario.STATUS;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 29, 2009
 * Time: 6:27:25 PM
 * Package: .Tools
 */
public class ToolsConfigurator extends JFrame  {
	
	private static final long serialVersionUID = 1L;

	private CyclicBarrier barrier;
	
	private List<MarioComponent> toControl;
	
	private MarioComponent controlledComponent;
	
	private MouseInterpreter interpreter;
	
    public void init(){
    	setFocusable(true);
    	int size=toControl.size();
    	
    	switch(size) {
    	
    	case 0:
    		System.err.println("No Agents given to evaluate!");
    		System.exit(1);
    	
    	case 1:
    		MarioComponent marioComponent=toControl.get(0);
    		setContentPane(marioComponent);
    		controlledComponent=marioComponent;
            marioComponent.init();
        break;
        
    	default: 
    		int rows=0,cols=0;
    		
    		switch(size) {
    		case 2:
    			rows=1;
    			cols=2;
    			break;
    		case 3:
    		case 4:
    			rows=2;
    			cols=2;
    			break;
    		case 5:
    		case 6:
    			rows=2;
    			cols=3;
    			break;
    		default: System.exit(1);
    		}
    		
    		GridLayout layout=new GridLayout(rows, cols);
    		setLayout(layout);
    		
    		for(int i=0;i<toControl.size();i++) {
    			MarioComponent tmp=toControl.get(i);
    			JPanel panel=new JPanel();
    			add(panel);
    			if(i!=0) panel.setBackground(Color.BLACK);
    			else {
    				controlledComponent=toControl.get(i);
    				panel.setBackground(Color.RED);
    			}
    			panel.add(tmp);
    			tmp.init();
    			tmp.addMouseListener(interpreter);
    		}

    	break;
    	}
    	
    	getContentPane().setBackground(Color.BLACK);
    	pack();
        
    }
    public void resizeAll(Dimension d) {
    	for(MarioComponent next: toControl) {
    		if(next.getLevelScene().getMarioStatus()!=STATUS.RUNNING) return;
    	}
    	for(MarioComponent next: toControl) {
    		next.setPreferredSize(d);
    	}
    	pack();
    }

	public MarioComponent register(RunnerOptions rOptions) {
    	MarioComponent marioComponent;
    	marioComponent = new MarioComponent(rOptions.getWindowHeigth(),rOptions.getWindowWidth()); //CHANGES WINDOW SIZE 
        toControl.add(marioComponent);          
		return marioComponent;
    }
    
    
   public ToolsConfigurator(int agentAmount, boolean viewAlwaysOnTop, boolean isViewable, Point origin) {
	   super("MarioAi");
	   if(agentAmount<1) {
		   System.err.println("No agents were given to evaluate!");
		   System.exit(1); 
	   }
	   
	   if(agentAmount>6) {
		   System.err.println("To many agents given!");
		   System.exit(1);
	   }
	   this.barrier=new CyclicBarrier(agentAmount,new BarrierAction(this));
	   this.toControl=new ArrayList<>(agentAmount);
	   
	   setAlwaysOnTop(viewAlwaysOnTop);
	   setVisible(isViewable);
       setLocation(origin);
       setResizable(false);
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
       interpreter=new MouseInterpreter(this);
       addMouseListener(interpreter);
       new KeyboardInterpreter(this);      
   }
   
   public void awaitBarrier() {
		try {
			barrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
   }
   
   private class BarrierAction implements Runnable{

	   ToolsConfigurator configurator;
	   
	   public BarrierAction(ToolsConfigurator configurator) {
		   this.configurator=configurator;
	   }   
	   
	   @Override
	   public void run() {
		configurator.init();
	   }
	   
   }
   
   public MarioComponent getControlledComponent() {
	   return controlledComponent;
   }
   
   public void switchControlledComponent(MarioComponent newComponent) {
	   MarioComponent tmp=getControlledComponent();
	   
	   tmp.removeLastKeyboardListener();
	   tmp.getParent().setBackground(Color.BLACK);
	   
	   this.controlledComponent=newComponent;
	   controlledComponent.addLastKeyboardListener();
	   controlledComponent.getParent().setBackground(Color.RED);
   }

}