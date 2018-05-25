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

public class MainFrame extends JFrame  { //TODO don't overwrite JFrame
	
	private static final long serialVersionUID = 1L;
	private static final int closeTime=5000;

	private CyclicBarrier barrier;
	
	private List<MarioComponent> toControl;
	
	private MarioComponent controlledComponent;
	private int finishedCounter=0;
	private boolean exitOnFinish;
	
	private MouseInterpreter interpreter;
	
	private int rows,cols;
	
	private void setRowsAndCols(int size) {		
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
	}
	
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
            controlledComponent.registerActualAgent();
        break;
        
    	default: 
    		setRowsAndCols(size);
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
    		controlledComponent.registerActualAgent();

    	break;
    	}
    	
    	getContentPane().setBackground(Color.BLACK);
    	Dimension d=getAllowedDimension(controlledComponent.getInitialDimension());
    	for(MarioComponent next: toControl) {
    		next.setPreferredSize(d);
    		next.revalidate();
    		next.repaint();	
    	}  
    	pack();
    }
    
    private Dimension getAllowedDimension(Dimension toCheck) {
    	Dimension d=toCheck;
    	if(d.width<320) d.width=320;
		if(d.height<240) d.height=240;
    	while(getWidth()-getContentPane().getWidth()+d.width*cols>getToolkit().getScreenSize().width||getHeight()-getContentPane().getHeight()+d.height*rows>getToolkit().getScreenSize().height) {
    		d=new Dimension(d.width-32, d.height-24);
    	}
    	return d;
    }
    public void resizeAll(Dimension oldD) {
    	Dimension d=getAllowedDimension(oldD);
    	
    	for(MarioComponent next: toControl) {
    		if(next.getLevelScene().getMarioStatus()!=STATUS.RUNNING) break;
    		next.storePaused();
    		next.setPaused(true);
    		while(!next.isPaused()) {
    			try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		}
    	
    	for(MarioComponent next: toControl) {
    		next.resizeView(d);
    	}

		this.getContentPane().revalidate();
    	this.getContentPane().repaint();
    	pack();
    	
    	for(MarioComponent next: toControl) {
    		if(next.getLevelScene().getMarioStatus()!=STATUS.RUNNING) break;
    		next.revalidate();
    		next.restorePaused();
    	}
    }

	public MarioComponent register(RunnerOptions rOptions) {
    	MarioComponent marioComponent;
    	marioComponent = new MarioComponent(rOptions.getWindowWidth(),rOptions.getWindowHeigth(),rOptions); 
        toControl.add(marioComponent);          
		return marioComponent;
    }
    
    
   public MainFrame(int agentAmount, boolean viewAlwaysOnTop, boolean isViewable, boolean exitOnFinish, Point origin) {
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
       this.exitOnFinish=exitOnFinish;
       
       interpreter=new MouseInterpreter(this);
       addMouseListener(interpreter);
       new KeyboardInterpreter(this);      
   }
   
   public void finished() {
	   if(!exitOnFinish||!isVisible()) return;
	   if(finishedCounter++<toControl.size()-1) return;
	   try {
		Thread.sleep(closeTime);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	   toControl.clear();
	   this.dispose();
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

	   MainFrame configurator;
	   
	   public BarrierAction(MainFrame configurator) {
		   this.configurator=configurator;
	   }   
	   
	   @Override
	   public void run() {
		if(isVisible())configurator.init();
	   }
   }
   
   public MarioComponent getControlledComponent() {
	   return controlledComponent;
   }
   
   public void switchControlledComponent(MarioComponent newComponent) {
	   MarioComponent tmp=getControlledComponent();
	   
	   tmp.removeLastKeyboardListener();
	   tmp.removeActualAgent();
	   if(tmp.getLevelScene().getMarioStatus()==STATUS.RUNNING)tmp.getParent().setBackground(Color.BLACK);
	   
	   this.controlledComponent=newComponent;
	   controlledComponent.addLastKeyboardListener();
	   controlledComponent.registerActualAgent();
	   controlledComponent.getParent().setBackground(Color.RED);
   }

}