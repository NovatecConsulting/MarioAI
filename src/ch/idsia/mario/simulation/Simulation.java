package ch.idsia.mario.simulation;

import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.RunnerOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 7, 2009
 * Time: 2:13:59 PM
 * Package: .Simulation
 */
public interface Simulation
{
    public void setRunnerOptions(RunnerOptions rOptions);

    public EvaluationInfo simulateOneLevel();
}
