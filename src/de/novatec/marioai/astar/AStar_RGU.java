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
import de.novatec.marioai.tools.MarioInput;
import de.novatec.marioai.tools.MarioKey;
import de.novatec.marioai.tools.MarioNtAgent;

public class AStar_RGU extends MarioNtAgent{
	
	private static final double TargetOffset=Environment.HalfObsWidth*16;
	private static final long timeLimit=10000;
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
		fMap.put(start, getDistanceFromTo(scene.getMarioX(), scene.getMarioY(), scene.getLevelXExit()*16+16, 0)); //heuristic estimate
		
		Node old_actual=start;// fallback
		Node actual=null;
		
		List<Node> nextNodes=new ArrayList<>();
		while(!openSet.isEmpty()&&System.currentTimeMillis()-timeSinceStart<timeLimit) { //while open set is not empty or planing target reached
			
			//pick best node from open list with lowest cost and no damage (if all damage go back one step?)
			actual=getBestNode(openSet,closedSet,fMap);
			if((actual.getUsedLevelScene().getMarioX()>=scene.getMarioX()+TargetOffset)||actual.getUsedLevelScene().getMarioMapX()>=actual.getUsedLevelScene().getLevelWidth()*16) { //reached right field of view OR reached target
				System.out.println("reached target at: "+actual.getUsedLevelScene().getMarioX());
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
				fMap.put(neighbor, tmpGScore+getDistanceFromTo(neighbor.getUsedLevelScene().getMarioX(), neighbor.getUsedLevelScene().getMarioY(), scene.getLevelWidth()*16,scene.getLevelHight()*16/2));
			} //for each nextNodes
			
			old_actual=actual;
			
		} //while
		List<MarioInput> tmp=reconstructPath(actual);
		return tmp.get(tmp.size()-2);
	}
	
	protected List<MarioInput> reconstructPath(Node actual){
		List<MarioInput> res=new ArrayList<>();
		
		Node current=actual;
		while(current!=null) {
			System.out.print(current+" ");
			res.add(current.getUsedInput());
			current=current.getParent();
		}
		if(res.size()<=2) res.add(new MarioInput());
		return res;
	}
	
	protected List<Node> getNextNodeList(Node parent,boolean extended){ //get all possible child-nodes for the given actions
		List<Node> res=new ArrayList<>();
		for(MarioInput next:getMarioInputs(parent.getUsedLevelScene(),extended)) {
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
	
	protected static List<MarioInput> getMarioInputs(LevelScene scene,boolean extended){
		List<MarioInput> res=new LinkedList<>();
		
		MarioInput tmp;

			tmp=new MarioInput();
			tmp.press(MarioKey.RIGHT);
			res.add(tmp);

//			tmp=new MarioInput();
//			tmp.press(MarioKey.DOWN);		
//			res.add(tmp);

//		else {
//			tmp=new MarioInput();
//		tmp.press(MarioKey.RIGHT);		
//		tmp.press(MarioKey.SPEED);
//		res.add(tmp);
//		}
		
		if(scene.mayMarioJump()||!scene.isMarioFalling()) {
			
//			if(!extended) {
			tmp=new MarioInput();
			tmp.press(MarioKey.RIGHT);
			tmp.press(MarioKey.SPEED);
			tmp.press(MarioKey.JUMP);
			res.add(tmp);
			
//			tmp=new MarioInput();
//			tmp.press(MarioKey.RIGHT);
//			tmp.press(MarioKey.JUMP);
//			res.add(tmp);


//			}
//			else {
			if(extended) {
				tmp=new MarioInput();
				tmp.press(MarioKey.LEFT);
				tmp.press(MarioKey.SPEED);
				tmp.press(MarioKey.JUMP);
				res.add(tmp);	
				
				tmp=new MarioInput();
				tmp.press(MarioKey.LEFT);
				tmp.press(MarioKey.JUMP);
				res.add(tmp);
				
				tmp=new MarioInput();
				tmp.press(MarioKey.JUMP);		
				res.add(tmp);
				
				tmp=new MarioInput();
				tmp.press(MarioKey.LEFT);
				tmp.press(MarioKey.JUMP);
				res.add(tmp);
			}
		}
		
//		tmp=new MarioInput();
//		tmp.press(MarioKey.LEFT);
//		tmp.press(MarioKey.SPEED);
//		res.add(tmp);
		
//		tmp=new MarioInput();
//		tmp.press(MarioKey.LEFT);		
//		res.add(tmp);

		return res;
	}
	
//	public static void main(String [] args) {
//		System.out.println(getDistanceFromTo(0,5,0,1));
//	}

}
