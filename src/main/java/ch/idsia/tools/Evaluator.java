package ch.idsia.tools;

import java.util.Comparator;
import java.util.concurrent.Callable;
import ch.idsia.mario.engine.MarioComponent;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 6, 2009
 * Time: 8:12:18 PM
 * Package: .Tools
 */

public class Evaluator implements Callable<EvaluationInfo>
{
    RunnerOptions rOptions;
    MarioComponent component;
    MainFrame configurator;


    public EvaluationInfo evaluate() {
        EvaluationInfo evaluationInfo=null;
        long startTime = System.currentTimeMillis();
 
        	 configurator.awaitBarrier();
        	 component.setRunnerOptions();
        	 
        	 component.getLevelScene().resetMario(rOptions.getMarioStartMode());     
			 evaluationInfo = component.run();                                            
			 evaluationInfo.levelType = rOptions.getLevelType();
			 evaluationInfo.levelDifficulty = rOptions.getDifficulty();
			 evaluationInfo.levelRandSeed = rOptions.getLevelSeed();

        long currentTime = System.currentTimeMillis();

        @SuppressWarnings("unused") //TODO USE
		long elapsed = currentTime - startTime;
      
        return evaluationInfo;
    }

    public Evaluator(RunnerOptions rOptions,MainFrame configurator) {           
    	this.rOptions=rOptions;
    	this.configurator=configurator;
    	this.component=configurator.register(rOptions);
    }
 
	@Override
	public EvaluationInfo call() throws Exception {
		return evaluate();
	}
}

class evBasicFitnessComparator implements Comparator<Object>
{
    public int compare(Object o, Object o1)
    {
        double ei1Fitness = ((EvaluationInfo)(o)).computeBasicFitness();
        double ei2Fitness = ((EvaluationInfo)(o1)).computeBasicFitness();
        if (ei1Fitness < ei2Fitness)
            return 1;
        else if (ei1Fitness > ei2Fitness)
            return -1;
        else
            return 0;
    }
}

class evCoinsFitnessComparator implements Comparator<Object>
{
    public int compare(Object o, Object o1)
    {
        int ei1Fitness = ((EvaluationInfo)(o)).numberOfGainedCoins;

        int ei2Fitness = ((EvaluationInfo)(o1)).numberOfGainedCoins;
        if (ei1Fitness < ei2Fitness)
            return 1;
        else if (ei1Fitness > ei2Fitness)
            return -1;
        else
            return 0;
    }
}

class evDistanceFitnessComparator implements Comparator<Object>
{
    public int compare(Object o, Object o1)
    {
        double ei1Fitness = ((EvaluationInfo)(o)).computeDistancePassed();
        double ei2Fitness = ((EvaluationInfo)(o1)).computeDistancePassed();
        if (ei1Fitness < ei2Fitness)
            return 1;
        else if (ei1Fitness > ei2Fitness)
            return -1;
        else
            return 0;
    }
}