package de.novatec.marioai.astar;

import de.novatec.marioai.tools.MarioInput;

public class Edge {

	private Node to;
	private MarioInput usedInput;
	
	private double cost;
	
	public Edge(Node to, MarioInput usedInput, double cost) {
		this.to = to;
		this.usedInput = usedInput;
		this.cost= cost;
	}

	public Node getTo() {
		return to;
	}

	public MarioInput getUsedInput() {
		return usedInput;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result + ((usedInput == null) ? 0 : usedInput.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass()) return false;
		
		Edge other = (Edge) obj;

		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		if (usedInput == null) {
			if (other.usedInput != null)
				return false;
		} else if (!usedInput.equals(other.usedInput))
			return false;
		return true;
	}
	
	
}
