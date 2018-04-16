package ch.idsia.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.mario.simulation.BasicSimulator;
import ch.idsia.mario.simulation.Simulation;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 6, 2009
 * Time: 8:12:18 PM
 * Package: .Tools
 */

public class Evaluator implements Runnable
{
    Thread thisThread = null;
    RunnerOptions rOptions;
    MarioComponent component;

    private List<EvaluationInfo> evaluationSummary = new ArrayList<EvaluationInfo>();

    

    public List<EvaluationInfo> evaluate()
    {
      
        Simulation simulator = new BasicSimulator(component,new RunnerOptions(rOptions));
        // Simulate One Level

        EvaluationInfo evaluationInfo;

        long startTime = System.currentTimeMillis();

            evaluationInfo = simulator.simulateOneLevel();
                                                        
            evaluationInfo.levelType = rOptions.getLevelType();
            evaluationInfo.levelDifficulty = rOptions.getDifficulty();
            evaluationInfo.levelRandSeed = rOptions.getLevelSeed();
            evaluationSummary.add(evaluationInfo);

        long currentTime = System.currentTimeMillis();
        
        @SuppressWarnings("unused") //TODO USE
		long elapsed = currentTime - startTime;
      
        return evaluationSummary;
    }

    public void verbose(String message, LOGGER.VERBOSE_MODE verbose_mode)
    {
        LOGGER.println(message, verbose_mode);
    }

    public void reset()
    {
        evaluationSummary = new ArrayList<EvaluationInfo>();
    }

    public Evaluator(RunnerOptions rOptions)
    {                      
        init(rOptions);
    }

    public void run()
    {
        evaluate();
    }

    public void start()
    {
        if (thisThread.getState() == Thread.State.NEW)
            thisThread.start();
    }

    public void init(RunnerOptions rOptions)
    {
         component=ToolsConfigurator.CreateMarioComponentFrame(
                rOptions);
        
        this.rOptions = rOptions;
        if (thisThread == null)
            thisThread = new Thread(this);
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