package bot_Ghost_in_the_Cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BronzeStrategy implements Strategy {
	public void execute(Board board) {
		for (Factory f : board.factories) {
			System.err.println(f.id + " " + this.getCyborgsNeededForTakeover(f));
		}
		
		List<String> moves = new ArrayList<String>();
		moves.add("MSG Uhmm, hi?");
		
		Factory[] myFactories = Arrays.stream(board.factories)
				.filter(f -> f.owner == 1)
				.toArray(size -> new Factory[size]);
		
		Factory[] neutralFactories = Arrays.stream(board.factories)
				.filter(f -> f.owner == 0)
				.toArray(size -> new Factory[size]);
		
		Factory[] enemyFactories = Arrays.stream(board.factories)
				.filter(f -> f.owner == -1)
				.toArray(size -> new Factory[size]);
		
		for (Factory factory : myFactories) {
			if (factory.cyborgs > 0) {
				Factory target;
				boolean isEnemy = true;
				
				if (neutralFactories.length > 0) {
					target = Arrays.stream(neutralFactories)
							.sorted((f1, f2) -> Integer.compare(f1.distanceTo(factory), f2.distanceTo(factory)))
							.findFirst()
							.get();
				} else if (enemyFactories.length > 0) {
					target = Arrays.stream(enemyFactories)
							.sorted((f1, f2) -> Integer.compare(f1.distanceTo(factory), f2.distanceTo(factory)))
							.findFirst()
							.get();
				} else {
					target = Arrays.stream(myFactories)
							.filter(f -> f.id != factory.id)
							.sorted((f1, f2) -> Integer.compare(f1.distanceTo(factory), f2.distanceTo(factory)))
							.findFirst()
							.get();
					isEnemy = false;
				}
				
				if (isEnemy) {
					int cyborgsNeeded = Math.abs(this.getCyborgsNeededForTakeover(target)) + 3;
					int cyborgsToSend = factory.cyborgs - 3 - cyborgsNeeded > 0 ? cyborgsNeeded : factory.cyborgs;
					moves.add("MOVE " + factory.id + " " + target.id + " " + cyborgsToSend);
				} else {
					moves.add("MOVE " + factory.id + " " + target.id + " " + Math.abs(factory.cyborgs / 4 * 3));
				}
			}
		}
		
		System.out.println(String.join(";", moves));
	}
	
	private int getCyborgsNeededForTakeover(Factory factory) {
		int me = factory.incomingTroops.stream()
				.filter(t -> t.owner == 1)
				.mapToInt(t -> t.cyborgs)
				.sum();
		
		int enemy = factory.incomingTroops.stream()
				.filter(t -> t.owner == -1)
				.mapToInt(t -> t.cyborgs)
				.sum();
		
		switch (factory.owner) {
			case 1:
				return (factory.cyborgs + me) - enemy + 1;
			case -1:
				return (factory.cyborgs + enemy) - me + 1;
			default:
				return (factory.cyborgs + enemy) - me + 1;
		}
	}
}
