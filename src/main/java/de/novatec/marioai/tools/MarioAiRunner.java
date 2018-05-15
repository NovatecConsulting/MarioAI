package de.novatec.marioai.tools;

import java.awt.Point;
import java.io.IOException;
import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.ChallengeTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.RunnerOptions;
import ch.idsia.tools.MainFrame;
import io.prometheus.client.exporter.HTTPServer;

/**
 * Simple helper class to start the evaluation of an agent. 
 * @author rgu 
 *
 */
public class MarioAiRunner {
	
	private static Logger log=LogManager.getLogger(MarioAiRunner.class);
	/**
	 * No instances needed.
	 */
	private MarioAiRunner() {
		
	}

	/**
	 * Starts the evaluation of an agent with the specified parameters.
	 * @param agent Agent to be evaluated
	 * @param levelConfig Configuration for the {@link}LevelGenerator. Either use preset levelConfigs or create your own.
	 * @param fps sets the amount of frames per seconds, bigger values make the game faster (Standard: 24 fps)
	 * @param randomize determines if the level should be randomized based on the levelConfig
	 * @param viewable determines if the evaluation should be viewable (if true fps will be overridden!)
	 * @param debugView determines if additional debug-views should be shown
	 * @param zoomFactor  multiplier for window height/width (Standard: 320x240)
	 */
	public static void run(Agent agent,LevelConfig levelConfig,int fps,int zoomFactor, boolean randomize, boolean viewable, boolean debugView) {
		List<Agent> tmp=new ArrayList<>();
		tmp.add(agent);
		multiAgentRun(tmp, levelConfig, new ChallengeTask(), fps, zoomFactor, randomize, viewable, debugView);
	}
	
	/**
	 *  Equals the call of @link #run(Agent, LevelConfig, boolean, int)} with attributes run(agent, levelConfig, randomize, 3).
	 * @param agent Agent to be evaluated
	 * @param levelConfig Configuration for the {@link}LevelGenerator. Either use preset levelConfigs or create your own
	 * @param randomize determines if the level should be randomized based on the levelConfig
	 */
	public static void run(Agent agent,LevelConfig levelConfig,boolean randomize) {		
		run(agent, levelConfig, 24,3,randomize, true,false);
	}
	
	/**
	 *  Hides some attributes of @link #run(Agent, LevelConfig, int,int, boolean, boolean, boolean). Standard values will be used for missing attributes.
	 * @param agent Agent to be evaluated
	 * @param levelConfig Configuration for the {@link}LevelGenerator. Either use preset levelConfigs or create your own
	 * @param randomize determines if the level should be randomized based on the levelConfig
	 * @param zoomFactor multiplier for window height/width (Standard: 320x240)
	 */
	public static void run(Agent agent,LevelConfig levelConfig,boolean randomize, int zoomFactor) {		
		run(agent, levelConfig, 24,zoomFactor,randomize, true,false);
	}
	
	public static List<EvaluationInfo> multiAgentRun(List<Agent> agents, LevelConfig levelConfig,Task task,int fps,int zoomFactor, boolean randomize, boolean viewable, boolean debugView) {
		
		//log.info("MarioAi - trying to start evaluation...");
		if(agents==null){
			log.error("Agents List can't be null!\nPlease use a proper List with agents! - no evaluation started");
			return new ArrayList<>();
		}
		if(agents.isEmpty()) {
			log.error("Agents List is empty - no evaluation started");
			return new ArrayList<>();
		}
		if(levelConfig==null&&!randomize) log.warn("LevelConfig is null, Level will be randomized!");
		if(randomize||levelConfig==null) levelConfig=LevelConfig.randomize(levelConfig);
		if(task==null) {
			log.error("Task can't be null");
			log.error("Exiting...");
			return new ArrayList<>();
		}
		if(zoomFactor<1) zoomFactor=1;	
		
		RunnerOptions baseOptions=new RunnerOptions(agents.get(0),levelConfig,task);

		baseOptions.setViewable(viewable);
		log.trace("viewable="+viewable);
		baseOptions.setFPS(fps);
		log.trace("initialFps="+fps);
		baseOptions.setWindowHeigth(240*zoomFactor);
		baseOptions.setWindowWidth(320*zoomFactor);
		log.trace("zoomFactor="+zoomFactor);
		log.trace("initialWindowWidth="+320*zoomFactor+" initialWindowHeight="+240*zoomFactor);
		baseOptions.setDebugView(debugView);
		log.trace("debugView="+debugView);
		
		int port=1234; //TODO changeable? just a test
		try {
			HTTPServer server=null;
			try {
				server=new HTTPServer(port, true);
			}
			catch (BindException e) {
				log.error("Port "+port+" is already in use!");
				log.error("No server will be started!");
			}

			ExecutorService runner = Executors.newCachedThreadPool();
			MainFrame configurator=new MainFrame(agents.size(), false, viewable, new Point());
			
			List<Future<EvaluationInfo>> results= new ArrayList<>();

			//log.info("Evaluating the following agents: ");
			for(Agent next: agents) {
				//log.info(next.getName());
				Evaluator ev=new Evaluator(baseOptions.getCopyWithNewAgent(next),configurator);
				results.add(runner.submit(ev));
			}
			
			
			//log.info("waiting for results...");
			
			List<EvaluationInfo> res=new ArrayList<>();
			for(Future<EvaluationInfo> next: results) {
				EvaluationInfo info=next.get();
				res.add(info);
				//log.info("Agent: "+info.agentName+" finished");
				//log.info(info);
			}
			
			
			runner.shutdown();
			if(server!=null)server.stop();
			
			return res;
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return new ArrayList<>();
	}
	
	public static void challengeRun(String packageName, int agentsPerRound, List<LevelConfig> levels, boolean autoKill) {
		try {
			
			ClassLoader cl=ClassLoader.getSystemClassLoader();
			URI uri = cl.getResource(packageName.replace('.', '/')).toURI();
			
			 Path myPath;
			 FileSystem fileSystem=null;
		        if (uri.getScheme().equals("jar")) {
		            fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
		            myPath = fileSystem.getPath("/"+packageName.replace('.', '/'));		            
		        } else {
		            myPath = Paths.get(uri);
		        }
		        Stream<Path> walk = Files.walk(myPath, Integer.MAX_VALUE);
		        List<Agent> agents=new ArrayList<>();
		        
		        String tmpSeparator = "\\\\";
		        String filter=".+"+packageName.replace(".", tmpSeparator)+tmpSeparator+"[^"+tmpSeparator+"]+\\.class$";
		        
		        for (Iterator<Path> it = walk.iterator(); it.hasNext();){
		        	Path tmp=it.next();
		        	if(tmp.toString().replace("/", "\\").matches(filter)) {
			        	try {
							agents.add((Agent)cl.loadClass(packageName+"."+tmp.getFileName().toString().substring(0, tmp.getFileName().toString().length()-6)).newInstance());
						} catch (ClassNotFoundException e) {
							log.catching(Level.FATAL,e);
						}
		        	}
		        }
		        walk.close();
		        if(fileSystem!=null) fileSystem.close();
		        //--- finished fetching agents from package
		        
		        log.info("Challenge started!");
	        	log.info("Agents, which will compete against each other:");
	        	for(Agent nextAgent:agents)log.info(nextAgent.getName()+" of Class: "+(nextAgent.getClass().getSimpleName()));
	        	
		        Map<Agent,Double> scores=new HashMap<>();
		        Scanner sc=new Scanner(System.in);
		        for(LevelConfig nextLevel:levels) {
		        	int oldAgentSize=agents.size();

		        	int agentsToKill=0;
		        	if(autoKill) {
		        		if(oldAgentSize>agentsPerRound) {
		        			agentsToKill=oldAgentSize/2;
		        			if(agentsPerRound>oldAgentSize-agentsToKill) agentsToKill=oldAgentSize-agentsPerRound;
		        		}
		        	}
		        	else {
		        	log.info("How many agents should be killed this round?");
		        	agentsToKill=sc.nextInt();
		        	if(agents.size()-agentsToKill<agentsPerRound) agentsToKill=agents.size()-agentsPerRound;
		        	if(agentsToKill<0) agentsToKill=0;
		        	}
		        	log.info("Starting next round...");
		        	log.info("Next round started - "+agentsToKill+" will be killed!");
		        	
		        	int agentsPlayed=0;
		        	int totalAgentsPlayed=0;
		        	while(totalAgentsPlayed<agents.size()) {

		        		List<Agent> tmpAgents=new ArrayList<>();
		        		for(int agentNumber=0;agentNumber<agentsPerRound;agentNumber++) {
		        			if(agents.size()>totalAgentsPlayed+agentNumber)tmpAgents.add(agents.get(totalAgentsPlayed+agentNumber));
		        			agentsPlayed++;
		        		}
		        		totalAgentsPlayed+=agentsPlayed;
		        		agentsPlayed=0;
		        		
		        		for(EvaluationInfo nextInfo:multiAgentRun(tmpAgents, nextLevel, new ChallengeTask(), 24, 4, false, true, false)) {
		        			Double oldScore=scores.get(nextInfo.getUsedAgent());
		        			if(oldScore==null) oldScore=0.0;
		        			scores.put(nextInfo.getUsedAgent(), oldScore+nextInfo.computeBasicFitness());
		        		}
		        		try {
							Thread.sleep(4*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		        	}
		        			
		        		while(agents.size()>oldAgentSize-agentsToKill) {
		        			Agent toKill=agents.get(0);
		        			
		        			for(Agent nextAgent: agents) {
		        				if(scores.get(toKill)>scores.get(nextAgent)) toKill=nextAgent;
		        			}
		        			agents.remove(toKill);
		        			log.info("Agent "+toKill.getName()+" was removed.");
		        		}
		        		
		        		log.info("Scores:");
		        		for(Map.Entry<Agent, Double> nextEntry: scores.entrySet()) log.info("Agent: "+nextEntry.getKey().getName()+" Score: "+nextEntry.getValue());
		        }
		        
		        log.info("The winner is...");
		        
		        Agent winner=null;
		        for(Map.Entry<Agent, Double> nextEntry: scores.entrySet()) {
		        	if(winner==null) winner=nextEntry.getKey();
		        	else if(scores.get(winner)<scores.get(nextEntry.getKey())) winner=nextEntry.getKey();
		        }
		        log.info("..."+winner.getName()+" of "+winner.getClass());
		        sc.close();
		        
		        
		} catch (URISyntaxException e) {
			log.catching(e);
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			log.catching(e);
		}
	}
	
	public static void main (String[] args) {
		
		List<LevelConfig> levels=new ArrayList<>();

		levels.add(LevelConfig.HARD_ENEMY_TRAINING);
		levels.add(LevelConfig.BOWSERS_CASTLE);
		levels.add(LevelConfig.DEALBREAKER);
		
		challengeRun("de.novatec.marioai.agents.included", 4, levels, true);
		System.exit(0);
	}
}


