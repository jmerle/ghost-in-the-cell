package bot_Ghost_in_the_Cell;

import java.util.ArrayList;
import java.util.List;

public class Board {
	public Factory[] factories;
	public List<Troop> troops = new ArrayList<Troop>();
	public List<Bomb> bombs = new ArrayList<Bomb>();
	public int turn = -1;
	public int bombsLeft = 2;
	
	public Board(int factoryCount) {
		this.factories = new Factory[factoryCount];		
		for (int i = 0; i < factoryCount; i++) {
			this.factories[i] = new Factory(i);
		}
	}
	
	public void addTroop(Troop troop) {
		this.troops.add(troop);
		troop.destination.incomingTroops.add(troop);
	}
	
	public void addBomb(Bomb bomb) {
		this.bombs.add(bomb);
		bomb.source.incomingBombs.add(bomb);
		if (bomb.destination != null) {
			bomb.destination.incomingBombs.add(bomb);
		}
	}
	
	public void initRound() {
		this.troops.clear();
		this.bombs.clear();
		turn++;
		
		for (Factory factory : this.factories) {
			factory.incomingTroops.clear();
			factory.incomingBombs.clear();
		}
	}
}
