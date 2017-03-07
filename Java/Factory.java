package bot_Ghost_in_the_Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factory {
	public int id;
	public int owner;
	public int cyborgs;
	public int production;
	public int turnsTillNormalProduction;
	
	public List<Troop> incomingTroops = new ArrayList<Troop>();
	public List<Bomb> incomingBombs = new ArrayList<Bomb>();
	public Map<Integer, Integer> distances = new HashMap<Integer, Integer>();
	
	public Factory(int id) {
		this.id = id;
	}
	
	public void update(int owner, int cyborgs, int production, int turnsTillNormalProduction) {
		this.owner = owner;
		this.cyborgs = cyborgs;
		this.production = production;
		this.turnsTillNormalProduction = turnsTillNormalProduction;
	}
	
	public void setDistance(int factoryID, Integer distance) {
		this.distances.put(factoryID, distance);
	}
	
	public int distanceTo(Factory factory) {
		return this.distanceTo(factory.id);
	}
	
	public int distanceTo(int factoryID) {
		return this.distances.get(factoryID);
	}
	
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof Factory)) return false;
		Factory otherFactory = (Factory) other;
		return otherFactory.id == this.id;
	}
}
