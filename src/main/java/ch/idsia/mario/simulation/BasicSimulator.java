package ch.idsia.mario.simulation;

import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.RunnerOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 7, 2009
 * Time: 2:27:48 PM
 * Package: .Simulation
 */

public class BasicSimulator implements Simulation
{
    private RunnerOptions rOptions = null;
    private MarioComponent marioComponent;

    public BasicSimulator(MarioComponent component,RunnerOptions rOptions)
    {
        this.marioComponent = component;
        this.setRunnerOptions(rOptions);
    }

    private MarioComponent prepareMarioComponent()
    {
        marioComponent.setRunnerOptions(rOptions);
        return marioComponent;
    }

    public void setRunnerOptions(RunnerOptions rOptions) {
        this.rOptions = rOptions;
    }

    public EvaluationInfo simulateOneLevel()
    { 
        prepareMarioComponent();
        marioComponent.getLevelScene().resetMario(rOptions.getMarioStartMode());     
        
        return marioComponent.run1();
    }
}
