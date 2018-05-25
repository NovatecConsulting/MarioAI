package ch.idsia.tools;

import java.util.concurrent.Callable;
import ch.idsia.mario.engine.MarioComponent;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 6, 2009
 * Time: 8:12:18 PM
 * Package: .Tools
 */

public class Evaluator implements Callable<EvaluationInfo> {
    private RunnerOptions rOptions;
    private MarioComponent component;
    private MainFrame configurator;

    public EvaluationInfo evaluate() {
        EvaluationInfo evaluationInfo=null;
        long startTime = System.currentTimeMillis();
 
        	 configurator.awaitBarrier();
        	 component.setRunnerOptions();
        	 
			 evaluationInfo = component.run();    
			 evaluationInfo.setUsedAgent(rOptions.getAgent());
			 evaluationInfo.levelType = rOptions.getLevelType();
			 evaluationInfo.levelDifficulty = rOptions.getDifficulty();
			 evaluationInfo.levelRandSeed = rOptions.getLevelSeed();

        long currentTime = System.currentTimeMillis();

        @SuppressWarnings("unused") //TODO USE
		long elapsed = currentTime - startTime;
        configurator.finished();
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