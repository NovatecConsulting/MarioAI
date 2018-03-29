/*package ch.idsia.mario.simulation;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.tools.EvaluationInfo;

public class AdvancedSimulator implements Simulation{ // even needed??? maybe for multiple agents at the same time :)
	
	 SimulationOptions simulationOptions = null;
	 private MarioComponent marioComponent;

	@Override
	public void setSimulationOptions(SimulationOptions simulationOptions) {
		
		
	}

	@Override
	public EvaluationInfo simulateOneLevel() {
		 Mario.resetStatic(simulationOptions.getMarioMode());        
	        
		 	Agent agent = simulationOptions.getAgent();
	        agent.reset();
	        marioComponent.setAgent(agent);
		 
	        marioComponent.setZLevelScene(simulationOptions.getZLevelMap());
	        marioComponent.setZLevelEnemies(simulationOptions.getZLevelEnemies());
	        marioComponent.startLevel(simulationOptions.getLevelRandSeed(), simulationOptions.getLevelDifficulty()
	                                 , simulationOptions.getLevelType(), simulationOptions.getLevelLength(),
	                                  simulationOptions.getTimeLimit());
	        marioComponent.setPaused(simulationOptions.isPauseWorld());
	        marioComponent.setZLevelEnemies(simulationOptions.getZLevelEnemies());
	        marioComponent.setZLevelScene(simulationOptions.getZLevelMap());
	        marioComponent.setMarioInvulnerable(simulationOptions.isMarioInvulnerable());
	        return marioComponent.run1(simulationOptions.currentTrial++,
	                simulationOptions.getNumberOfTrials());
		
	}

}
*/