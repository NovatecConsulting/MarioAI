package ch.idsia.ai.tasks;

import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.RunnerOptions;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:26:43 AM
 * Package: ch.idsia.ai.tasks
 */
public class ProgressTask implements Task {

    private RunnerOptions options;

    public ProgressTask(RunnerOptions options) {
        setOptions(options);
    }

    public void setOptions(RunnerOptions options) {
        this.options = options;
    }
    
	@Override
	public RunnerOptions getOptions() {
		return options;
	}
	
	@Override
	public List<EvaluationInfo> evaluteWithExtendedInfo() {
        Evaluator evaluator = new Evaluator(options);
		return evaluator.evaluate();
	}

	@Override
	public double[] evaluate() {
		return null;
	}


}
