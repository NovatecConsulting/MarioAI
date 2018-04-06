package ch.idsia.ai.tasks;

import java.util.List;

import ch.idsia.ai.agents.Agent;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.RunnerOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:20:41 AM
 * Package: ch.idsia.ai.tasks
 */
public interface Task {
    public double[] evaluate ();
    
    public List<EvaluationInfo> evaluteWithExtendedInfo();

    public void setOptions (RunnerOptions options);

    public RunnerOptions getOptions ();

}
