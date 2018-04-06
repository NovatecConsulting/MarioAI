package de.novatec.marioai.astar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Mario.STATUS;
import ch.idsia.mario.environments.Environment;
import de.novatec.mario.engine.generalization.Coordinates;
import de.novatec.marioai.tools.MarioInput;
import de.novatec.marioai.tools.MarioKey;
import de.novatec.marioai.tools.MarioNtAgent;

public class AStar_RGU extends MarioNtAgent{
	
	private static final double TargetOffset=Environment.HalfObsWidth*14;
	private static final long timeLimit=8000;
	private List<Node> openSet;
	private List<Node> closedSet;
	private Map<Node,Double> gMap;
	private Map<Node,Double> fMap;

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
	}

	@Override
	public String getName() {
		return "AStar Test Agent";
	}
	
	protected MarioInput getBestPath(LevelScene scene) {
		long timeSinceStart=System.currentTimeMillis();
		Node start=new Node(scene);
		
		if(openSet==null||closedSet==null||gMap==null||fMap==null) {
			openSet=new ArrayList<>();
			closedSet=new ArrayList<>();
			gMap=new HashMap<>();
			fMap=new HashMap<>();
		}
		else {
			openSet.clear();
			closedSet.clear();
			gMap.clear();
			fMap.clear();
		}

		openSet.add(start);
		gMap.put(start, 0.0); //cost for start is 0
		fMap.put(start, getDistanceFromTo(scene.getMarioX(), scene.getMarioY(), scene.getLevelWidth()*16, scene.getLevelHight()*16/2)); //heuristic estimate
		
		
		Node old_actual=start;// fallback
		Node actual=null;
		double tmpTargetOffset=TargetOffset;
		List<Node> nextNodes=new ArrayList<>();
		while(!openSet.isEmpty()&&System.currentTimeMillis()-timeSinceStart<timeLimit) { //while open set is not empty or planing target reached
			
			//pick best node from open list with lowest cost and no damage (if all damage go back one step?)
			actual=getBestNode(openSet,closedSet,fMap);
			if(!actual.getUsedInput()[4]) tmpTargetOffset-=0.5;
			if((actual.getUsedLevelScene().getMarioX()>=scene.getMarioX()+tmpTargetOffset)) { //reached right field of view 
				//System.out.println("reached target at: "+actual.getUsedLevelScene().getMarioX());
				break;
				
			}
			
			nextNodes.clear();
			nextNodes=getNextNodeList(actual,false); //generate next node list;
			
		
			openSet.remove(actual);
			closedSet.add(actual);
			//for each neighbour
				// if not in closed list ->continue
				// if not in open list ->add to open list
			
			for(Node neighbor: nextNodes) { 
				
				if(closedSet.contains(neighbor)) continue;
				if(!openSet.contains(neighbor)) openSet.add(neighbor);
				
				double distance=actual.getDistanceTo(neighbor);

				double tmpGScore=gMap.get(actual)+distance;
				
				//check scores -> if path from actual to neighbor better than old way replace
				if(gMap.get(neighbor)!=null&&tmpGScore>gMap.get(neighbor)) continue; //check if old path is better
				
				//else set this way
				neighbor.setParent(actual);
				gMap.put(neighbor, tmpGScore);
				fMap.put(neighbor, tmpGScore+getDistanceFromTo(neighbor.getX(), neighbor.getY(), scene.getLevelWidth()*16,scene.getLevelHight()*16/2)-neighbor.getUsedLevelScene().getScore());
//				System.out.println(neighbor);
//				System.out.println("Gscore: "+tmpGScore);
//				System.out.println("Distance to Target: "+getDistanceFromTo(neighbor.getX(), neighbor.getY(), scene.getLevelWidth()*16, scene.getLevelHight()*16/2));
//				System.out.println("FScore: "+(tmpGScore+getDistanceFromTo(neighbor.getX(), neighbor.getY(), scene.getLevelWidth()*16,scene.getLevelHight()*16/2)-neighbor.getUsedLevelScene().getScore()));
			//	System.out.println(neighbor.getX());
			} //for each nextNodes
			
			old_actual=actual;
			//System.out.println("-----------------------------");
			//if(System.currentTimeMillis()-timeSinceStart>=timeLimit) System.out.println("target not reached in time");
			
		} //while
//		List<MarioInput> tmp=reconstructPath(actual);
//		return tmp.get(tmp.size()-2);
		
		return reconstructPathBetter(actual);
	}
	
	protected List<MarioInput> reconstructPath(Node actual){
		List<MarioInput> res=new ArrayList<>();
		
		Node current=actual;
		while(current!=null) {
			//System.out.print(current+" ");
			if(current.getParent()!=null)addCoordToDraw(new Coordinates(current.getX(),current.getY()));
			
			res.add(MarioInput.arrayToMarioInput(current.getUsedInput()));
			current=current.getParent();
		}
		if(res.size()<=2) res.add(new MarioInput());
		return res;
	}
	
	protected MarioInput reconstructPathBetter(Node last){
		Node current=last,lastCurrent=null,lastlastCurrent=null;
		while(current!=null) {
			//System.out.print(current+" ");
			if(current.getParent()!=null)addCoordToDraw(new Coordinates(current.getX(),current.getY()));
			lastlastCurrent=lastCurrent;
			lastCurrent=current;
			current=current.getParent();
		}
		
		if(lastlastCurrent==null) return new MarioInput();
		return MarioInput.arrayToMarioInput(lastlastCurrent.getUsedInput());
	}
	
	protected List<Node> getNextNodeList(Node parent,boolean extended){ //get all possible child-nodes for the given actions
		List<Node> res=new ArrayList<>();
		for(boolean[] next:getMarioInputs(parent.getUsedLevelScene(),extended)) {
			res.add(new Node(next,parent.getUsedLevelScene(),parent));
		}
		return res;
	}
	
 	protected Node getBestNode(List<Node> openSet,List<Node> closedSet, Map<Node,Double> fMap) { //gets the Node in openSet with the lowest fScore in fMap
		if(openSet==null||openSet.isEmpty()) {
			System.err.println("Can't get best Node, openSet is null/empty!");
			return new Node();
		}
		if(fMap==null||fMap.isEmpty()) {
			System.err.println("Can't get best Node, fMap is null/empty!");
			return new Node();
		}
		
		List<Node> toRemove=new ArrayList<>();

		Node lowestScore=null;
		
		for(Node next:openSet) {
			
			if(next.gotHurt()||next.getUsedLevelScene().getMarioStatus()==STATUS.LOOSE) {
				toRemove.add(next);
				System.out.println("GOT HURT STATUS");
				continue;
			}
				
			if(lowestScore==null) lowestScore=next;
			if(fMap.get(lowestScore)>fMap.get(next)) {
				lowestScore=next;
			}
		}
		
		if(lowestScore==null) {
			System.err.println("Mario will be hurt in every way he goes!");
		}
		openSet.removeAll(toRemove);
		closedSet.addAll(toRemove);
		return lowestScore;
	}
	
	protected static double getDistanceFromTo(float x1,float y1,float x2, float y2) {
		return Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
	}
	
	protected static List<boolean[]> getMarioInputs(LevelScene scene,boolean extended){
		List<boolean[]> res=new LinkedList<>();
		
		res.add(new boolean[] {false,true,false,false,true}); //right,speed
		res.add(new boolean[] {false,true,false,false,false}); //right
		
		if(!scene.isMarioFalling()||scene.mayMarioJump()) {
			res.add(new boolean[] {false,true,false,true,true}); // right,speed,jump
		}
		
		return res;
	}
}
