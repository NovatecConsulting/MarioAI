package de.novatec.marioai.astar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.environments.Environment;
import de.novatec.marioai.tools.MarioInput;
import de.novatec.marioai.tools.MarioKey;
import de.novatec.marioai.tools.MarioNtAgent;

public class AStar_RGU extends MarioNtAgent{
	
	private static final double TargetOffset=Environment.HalfObsWidth*16;
	private static final long timeLimit=120;
	private List<Node> openSet;
	private List<Node> closedSet;
	private Map<Node,Double> gMap;
	private Map<Node,Double> fMap;
	
	private List<Node> nodes;

	@Override
	public MarioInput doAiLogic() {
		LevelScene clonedScene=getDeepCopyOfLevelScene();
		MarioInput i=getBestPath(clonedScene);
		//System.out.println(i);
		return i;
	}

	@Override
	public void reset() {
		super.reset();
		
		if(openSet!=null) openSet.clear();
		if(closedSet!=null) closedSet.clear();
		if(nodes!=null) nodes.clear();
	}

	@Override
	public String getName() {
		return "AStar Test Agent";
	}
	
	protected MarioInput getBestPath(LevelScene scene) {
		long timeSinceStart=System.currentTimeMillis();
		Node start=new Node(scene);
		
		if(openSet==null||closedSet==null||nodes==null||gMap==null||fMap==null) {
			openSet=new ArrayList<>();
			closedSet=new ArrayList<>();
			nodes=new ArrayList<>();	
			gMap=new HashMap<>();
			fMap=new HashMap<>();
		}
		else {
			openSet.clear();
			closedSet.clear();
			nodes.clear();
			gMap.clear();
			fMap.clear();
		}
		
		openSet.add(start);
		gMap.put(start, 0.0); //cost for start is 0
		fMap.put(start, getDistanceFromTo(scene.getMarioX(), scene.getMarioY(), scene.getLevelXExit()*16, scene.getMarioY())); //heuristic estimate
		
		Node old_actual=start;// fallback
		Node actual=null;
		
		List<Node> nextNodes;
		while(!openSet.isEmpty()&&System.currentTimeMillis()-timeSinceStart<timeLimit) { //while open set is not empty or planing target reached
			//System.out.println(openSet);
			//pick best node from open list with lowest cost and no damage (if all damage go back one step?)
			
			actual=getBestNode(openSet, fMap);
			
			
			if(old_actual!=null&&actual.getUsedLevelScene().getMarioX()>old_actual.getUsedLevelScene().getMarioX()+TargetOffset) {
				break;
			}
			//generate next node list;
			nextNodes=getNextNodeList(actual);
			//System.out.println(nextNodes);
			
			openSet.remove(actual);
			closedSet.add(actual);
			//for each neighbour
				// if not in closed list ->continue
				// if not in open list ->add to open list
			
			for(Node next: nextNodes) { 
				
				if(closedSet.contains(next)) continue;
				if(!openSet.contains(next)) openSet.add(next);
				
				double tmpGScore=gMap.get(actual)+actual.getDistanceTo(next);
				
				//check scores -> if path from actual to neighbor better than old way replace
				if(gMap.get(next)!=null&&tmpGScore>=gMap.get(next)) continue; //check if old path is better
				
				next.setParent(actual);
				//gMap.remove(next);
				gMap.put(next, tmpGScore);
				//fMap.remove(next);
				fMap.put(next, tmpGScore+getDistanceFromTo(next.getUsedLevelScene().getMarioX(), next.getUsedLevelScene().getMarioY(), scene.getLevelXExit()*16, next.getUsedLevelScene().getMarioY()));

				old_actual=actual;
				
				
			} //for each nextNodes
			
		} //while
		List<MarioInput> tmp=reconstructPath(actual);
		return tmp.get(tmp.size()-2);
	}
	
	protected List<MarioInput> reconstructPath(Node actual){
		List<MarioInput> res=new ArrayList<>();
		
		Node current=actual;
		while(current!=null) {
			//System.out.println(actual);
			res.add(current.getUsedInput());
			current=current.getParent();
		}
		return res;
	}
	
	protected List<Node> getNextNodeList(Node parent){
		List<Node> res=new ArrayList<>();
		for(MarioInput next:getMarioInputs(parent.getUsedLevelScene())) {
			res.add(new Node(next,parent.getUsedLevelScene(),parent));
		}
		return res;
	}
	
 	protected Node getBestNode(List<Node> openSet, Map<Node,Double> fMap) { //gets the Node in openSet with the lowest fScore in fMap
		if(openSet==null||openSet.isEmpty()) {
			System.err.println("Can't get best Node, openSet is null/empty!");
			return new Node();
		}
		if(fMap==null||fMap.isEmpty()) {
			System.err.println("Can't get best Node, fMap is null/empty!");
			return new Node();
		}
		
		Node lowestScore=null;
		
		for(int i=0; i<openSet.size();i++) {
			if(openSet.get(i).gotHurt()) {
				System.out.println("GOT HURT STATUS");
				continue;
			}
			if(lowestScore==null) lowestScore=openSet.get(i);
			if(fMap.get(lowestScore)>=fMap.get(openSet.get(i))) {
				lowestScore=openSet.get(i);
			}
		}
		
		if(lowestScore==null) {
			System.err.println("Mario will be hurt in every way he goes!");
//			Random r=new Random();
//			lowestScore=openSet.get(r.nextInt(openSet.size()));
		}
		
		return lowestScore;
	}
	
	protected static double getDistanceFromTo(float x1,float y1,float x2, float y2) {
		return Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
	}
	
	protected static List<MarioInput> getMarioInputs(LevelScene scene){
		List<MarioInput> res=new LinkedList<>();
		
		MarioInput tmp=new MarioInput();
		
		tmp=new MarioInput();
		tmp.press(MarioKey.RIGHT);
		res.add(tmp);
		
		//if(!scene.marioIsFalling()) {
			tmp=new MarioInput();
			tmp.press(MarioKey.RIGHT);
			tmp.press(MarioKey.JUMP);
			res.add(tmp);
			
			tmp=new MarioInput();
			tmp.press(MarioKey.RIGHT);
			tmp.press(MarioKey.SPEED);
			tmp.press(MarioKey.JUMP);
			res.add(tmp);
			
			tmp=new MarioInput();
			tmp.press(MarioKey.LEFT);
			tmp.press(MarioKey.JUMP);
			res.add(tmp);
			
			tmp=new MarioInput();
			tmp.press(MarioKey.LEFT);
			tmp.press(MarioKey.SPEED);
			tmp.press(MarioKey.JUMP);
			res.add(tmp);
			
			tmp=new MarioInput();
			tmp.press(MarioKey.JUMP);		
			res.add(tmp);
		//}
		
		
		
		tmp=new MarioInput();
		tmp.press(MarioKey.RIGHT);		
		tmp.press(MarioKey.SPEED);
		res.add(tmp);
		

		

		
		
		
//		tmp=new MarioInput();
//		tmp.press(MarioKey.LEFT);		
//		res.add(tmp);
		
		tmp=new MarioInput();
		tmp.press(MarioKey.LEFT);
		tmp.press(MarioKey.SPEED);
		res.add(tmp);
		
//		
//		tmp=new MarioInput();
//		tmp.press(MarioKey.DOWN);		
//		res.add(tmp);
		
		return res;
	}
	
//	public static void main(String [] args) {
//		System.out.println(getDistanceFromTo(0,5,0,1));
//	}

}
