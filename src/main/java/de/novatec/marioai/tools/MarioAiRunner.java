package de.novatec.marioai.tools;

import java.awt.Point;
import java.io.IOException;
import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.RunnerOptions;
import de.novatec.marioai.agents.included.ExampleAgent;
import ch.idsia.tools.MainFrame;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.exporter.PushGateway;

/**
 * Simple helper class to start the evaluation of an agent. 
 * @author rgu 
 * @author nmn
 */
public class MarioAiRunner {
	
	private static final Logger log=LogManager.getLogger(MarioAiRunner.class);
	private static final String jobName="challengeRun";
	private static List<Agent> agents;
	private static LevelConfig levelConfig;
	private static Task task; 
	private static int fps; 
	private static int zoomFactor; 
	private static boolean randomize; 
	private static boolean viewable;
	private static boolean debugView;
	private static boolean exitOnFinish; 
	private static boolean pushMetrics;
	/**
	 * No instances needed.
	 */
	/**
	 * 
	 * @param marioAiRunnerBuilder uses all the variables from the builder and copies them 
	 */
	public MarioAiRunner(MarioAiRunnerBuilder marioAiRunnerBuilder) {
		List<Agent> tmpList = marioAiRunnerBuilder.getAgents();
		if(tmpList.isEmpty()) tmpList.add(new ExampleAgent());
		agents = tmpList;
		levelConfig  = marioAiRunnerBuilder.getLevelConfig();
		task = marioAiRunnerBuilder.getTask();
		fps = marioAiRunnerBuilder.getFps();
		zoomFactor = marioAiRunnerBuilder.getZoomFactor();
		randomize = marioAiRunnerBuilder.isRandomize();
		viewable = marioAiRunnerBuilder.isViewable();
		debugView = marioAiRunnerBuilder.isDebugView();
		exitOnFinish = marioAiRunnerBuilder.isExitOnFinish();
		pushMetrics = marioAiRunnerBuilder.isPushMetrics();
		
	}
	
	/**
	 * 
	 * @return List<EvaluationInfo> runs all agents using the current settings(variables) and returns the results
	 */
	public static List<EvaluationInfo> run() {
		
		RunnerOptions baseOptions=new RunnerOptions(agents.get(0),levelConfig,task);
		if(randomize) levelConfig = LevelConfig.randomize(levelConfig);
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
		baseOptions.setPushMetrics(pushMetrics);
		
		try {
			ExecutorService runner = Executors.newWorkStealingPool();
			MainFrame configurator=new MainFrame(agents.size(), false,viewable,exitOnFinish, new Point());
			
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
				log.info("Agent: "+info.agentName+" finished");
				log.info(info);
			}
			
			runner.shutdown();
			
			return res;
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return new ArrayList<>();
	}
	
/*	public static void challengeRun(String packageName,List<LevelConfig> levels, int agentsPerRound, int zoomFactor, boolean autoKill,boolean pushUpdates) {
		if(packageName==null) {
			log.info("packageName can't be null");
			log.info("Returning...");
			return;
		}
		
		if(levels==null||levels.isEmpty()) {
				log.info("No levels given for the challenge");
				log.info("Returning...");
				return;
			}
			
		
		if(agentsPerRound<1) {
				log.info("agentsPerRound must be bigger than 0");
				log.info("Returning...");
				return;
			}
		
		try {
			ClassLoader cl=ClassLoader.getSystemClassLoader();
			URI uri=null;
			Enumeration<URL>test=cl.getResources(packageName.replace('.', '/'));
			List<Agent> agents=new ArrayList<>();
			while(test.hasMoreElements()) {
				uri=test.nextElement().toURI();
				
				Path myPath;
				FileSystem fileSystem=null;
				
		        if (uri.getScheme().equals("jar")) { //package is in jar
		            fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
		            myPath = fileSystem.getPath("/"+packageName.replace('.', '/'));		            
		        } else { 
		            myPath = Paths.get(uri);
		        }
		        Stream<Path> walk = Files.walk(myPath, Integer.MAX_VALUE);
		        
		        String tmpSeparator = "\\\\";
		        String filter=".+"+packageName.replace(".", tmpSeparator)+tmpSeparator+"[^"+tmpSeparator+"]+\\.class$";
		        
		        for (Iterator<Path> it = walk.iterator(); it.hasNext();){
		        	Path tmp=it.next();
		        	if(tmp.toString().replace("/", "\\").matches(filter)) {
			        	try {
			        		Class<?> tmpClass=cl.loadClass(packageName+"."+tmp.getFileName().toString().substring(0, tmp.getFileName().toString().length()-6));
							if(Agent.class.isAssignableFrom(tmpClass))agents.add((Agent)tmpClass.newInstance());
						} catch (ClassNotFoundException e) {
							log.catching(Level.FATAL,e);
						}
		        	}
		        }
		        walk.close();
		        if(fileSystem!=null) fileSystem.close();
			}
		    //--- finished fetching agents from package
		        
			CollectorRegistry registry = new CollectorRegistry();
			PushGateway pg = new PushGateway(MarioComponent.address);
			Gauge data=Gauge.build().name("challenge").help("All Challenge Information").labelNames("name","agent","type").register(registry);
//			pg.delete(jobName);
			if(agents.isEmpty()) {
				log.warn("No agents found to evaluate!");
				log.warn("Returning...");
				return;
			}
		        log.info("Challenge started!");
	        	log.info("Agents, which will compete against each other:");
	        	for(Agent nextAgent:agents)log.info(nextAgent.getName()+"/"+(nextAgent.getClass().getSimpleName()));
	        	
		        Map<Agent,Double> scores=new HashMap<>();
		        Scanner sc=new Scanner(System.in);
		        
		        Iterator<LevelConfig> tmpLevels=levels.iterator();
		        List<Entry<Agent,Double>> tmpScores=null;
		        while(tmpLevels.hasNext()) {
		        	LevelConfig nextLevel=tmpLevels.next();
		        	
		        	int oldAgentSize=agents.size();

		        	int agentsToKill=-1;
		        	if(tmpLevels.hasNext()) {
			        	if(autoKill) {
			        		if(oldAgentSize>2) agentsToKill=(2>oldAgentSize-agentsToKill) ? oldAgentSize-2:oldAgentSize/2;
			        		else agentsToKill=0;
			        		
			        	}
			        	else {
			        		log.info("How many agents(of "+agents.size()+") should be killed this round?");
			        		int maxKillable=(agents.size()-2<0) ?  0 : (agents.size()-2);
			        		log.info(maxKillable+" Agents could be killed.");
			        		while(agentsToKill==-1) {
					        	try {
					        		agentsToKill=sc.nextInt();
	
						        	if(agents.size()-agentsToKill<2) {
						        		agentsToKill=agents.size()-2;
						        	}
						        	if(agentsToKill<0) agentsToKill=0;
					        	}
					        	catch(InputMismatchException e) {
					        		sc.next();
					        		log.info("Input was not an integer, please try again");
					        	} 
			        		}
			        	}
			        	log.info("Starting next round...");
			        	log.info("Next round started - "+agentsToKill+" will be killed!");
		        	}
		        	else {
		        		agentsToKill=0;
		        		log.info("Enter anything to start the next round - FINAL ROUND");
		        		sc.next();
		        		log.info("Starting final round...");
		        	}
		        	
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
		        		
		        		for(EvaluationInfo nextInfo:multiAgentRun(tmpAgents, nextLevel, new ChallengeTask(), 24, zoomFactor, false, true, false, true, true)) {
		        			Double oldScore=scores.get(nextInfo.getUsedAgent());
		        			if(oldScore==null) oldScore=0.0;
		        			scores.put(nextInfo.getUsedAgent(), oldScore+nextInfo.computeBasicFitness());
		        		}
		        		
		        		if(totalAgentsPlayed<agents.size()) {
		        		log.info("Enter anything to continue");
		        		sc.next();
		        		}
		        	}

		        	tmpScores=new ArrayList<>(scores.entrySet());
		        	tmpScores.sort(new EntryComperator());
		        	
		        		while(agents.size()>oldAgentSize-agentsToKill) {
		        			Agent toKill=agents.get(0);
		        			
		        			for(Agent nextAgent: agents) {
		        				if(scores.get(toKill)>scores.get(nextAgent)) toKill=nextAgent;
		        			}
		        			
		        			List<Agent> duellist=new ArrayList<>();
		        			for(Agent nextAgent: agents) {
		        				if(nextAgent==toKill) {
		        					duellist.add(nextAgent);
		        					continue;
		        				}
		        				if(Math.abs(scores.get(toKill)-scores.get(nextAgent))<0.0001) duellist.add(nextAgent);
		        			}
		        			if(duellist.size()>1) {
		        				log.info("It's duel time!");
		        				log.info("The following agents are about to duel:");
		        				for(Agent nextDuellist:duellist) log.info(nextDuellist.getName()+"/"+nextDuellist.getClass().getSimpleName());
		        				
		        				log.info("Enter anything to start the duel");
		        				sc.next();
		        				
			        			double worstScore=Double.MAX_VALUE;
			        			
			        			for(EvaluationInfo nextInfo:multiAgentRun(duellist, nextLevel, new ChallengeTask(), 24, zoomFactor, true, true, false, true, true)) {
			        				if(nextInfo.computeBasicFitness()<worstScore) toKill=nextInfo.getUsedAgent();
			        			}
		        			log.info("Agent "+toKill.getName()+" lost the duell.");
		        			}
		        			
		        			agents.remove(toKill);
		        			log.info("Agent "+toKill.getName()+" was removed.");
		        		}
		        		
		        		log.info("Scores:");
		        		for(Entry<Agent,Double> nextEntry:tmpScores) {
		        			if(pushUpdates)data.labels(nextEntry.getKey().getName(),nextEntry.getKey().getClass().getName().trim().replace('.', '_'),"totalScore").set(nextEntry.getValue());
		        			if(agents.contains(nextEntry.getKey())) log.info(nextEntry.getKey().getName()+"/"+nextEntry.getKey().getClass().getSimpleName()+" : "+nextEntry.getValue());		
		        		}
		        		if(pushUpdates)pg.push(registry, "challengeRun");
		        }
		        log.info("The winner is...");
		        
		        Agent winner=null;
		        for(Map.Entry<Agent, Double> nextEntry: scores.entrySet()) {
		        	if(pushUpdates)data.labels(nextEntry.getKey().getName(),nextEntry.getKey().getClass().getName().trim().replace('.', '_'),"totalScore").set(nextEntry.getValue());
		        	if(winner==null) winner=nextEntry.getKey();
		        	else if(scores.get(winner)<scores.get(nextEntry.getKey())) winner=nextEntry.getKey();
		        }
		        if(pushUpdates) pg.push(registry, jobName);
		        log.info("..."+winner.getName()+"/"+winner.getClass().getSimpleName());
		        
		        log.info("Final Scores:");
          		for(Entry<Agent,Double> nextEntry:tmpScores) log.info(nextEntry.getKey().getName()+"/"+nextEntry.getKey().getClass().getSimpleName()+" : "+nextEntry.getValue());
          		
//          		log.info("Enter anything to exit and remove all statistics");
//        		sc.next();
//          		if(pushUpdates) pg.delete(jobName);
          		sc.close();
          		log.info("Exiting...");
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
	}*/
	
	public static class EntryComperator implements Comparator<Entry<Agent,Double>>{
		@Override
		public int compare(Entry<Agent, Double> o1, Entry<Agent, Double> o2) {
			return -(o1.getValue().compareTo(o2.getValue()));
		}
	}
	
	//public static void main (String[] args) {
		
		/*List<LevelConfig> levels=new ArrayList<>();

		levels.add(LevelConfig.HARD_ENEMY_TRAINING);
		levels.add(LevelConfig.BOWSERS_CASTLE);
		levels.add(LevelConfig.DEALBREAKER);
		
		challengeRun("de.novatec.marioai.agents.included",levels, 4,  4, true,true);
		System.exit(0);
		
//		List<Agent> agents=new ArrayList<>();
//		
//		agents.add(new ExampleAgent());
//		
//		System.out.println(multiAgentRun(agents, LevelConfig.LEVEL_1, new ChallengeTask(), 24, 3, true, true, false, false));
	*/
	// }
}


