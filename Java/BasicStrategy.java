package bot_Ghost_in_the_Cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class BasicStrategy implements Strategy {
	public void execute(Board board) {		
		List<String> moves = new ArrayList<String>();
		
		while (moves.size() < 10) {
			String move = this.getMove(board);
			if (move.equals("WAIT")) {
				if (moves.size() == 0) {
					moves.add(move);
				}
				
				break;
			} else {
				moves.add(move);
			}
		}
		
		String finalMove = "";
		for (String move : moves) {
			finalMove += move + ";";
		}
		
		System.out.println(finalMove.substring(0, finalMove.length() - 1));
	}
	
	private String getMove(Board board) {
		try {
			Factory factorySource = Arrays.stream(board.factories)
					.filter(f -> f.owner == 1)
					.sorted((f1, f2) -> Integer.compare(f2.cyborgs, f1.cyborgs))
					.findFirst()
					.get();
			
			Factory factoryDestination = Arrays.stream(board.factories)
					.filter(f -> this.getDefenseScore(f) >= 0 && f.id != factorySource.id)
					.sorted((f1, f2) -> Integer.compare(this.getDefenseScore(f1) + factorySource.distanceTo(f1), this.getDefenseScore(f2) + factorySource.distanceTo(f2)))
					.findFirst()
					.get();
			
			int cyborgsAvailable = factorySource.cyborgs / 4 * 3;
			int cyborgsNeeded = (this.getDefenseScore(factoryDestination) + factorySource.distanceTo(factoryDestination) * factoryDestination.production) + 1;
			int cyborgsToSend = cyborgsNeeded <= cyborgsAvailable ? cyborgsNeeded : cyborgsAvailable;
			
			factorySource.cyborgs -= cyborgsToSend;
			
			return "MOVE " + factorySource.id + " " + factoryDestination.id + " " + cyborgsToSend;
		} catch (NoSuchElementException e) {}
		
		return "WAIT";
	}
	
	private int getDefenseScore(Factory factory) {
		int enemyTroops = factory.incomingTroops.stream().filter(t -> t.owner == -1).mapToInt(t -> t.cyborgs - t.remainingTurns * factory.production).map(i -> i > 0 ? i : 0).sum();
		int myTroops = factory.incomingTroops.stream().filter(t -> t.owner == 1).mapToInt(t -> t.cyborgs - t.remainingTurns * factory.production).map(i -> i > 0 ? i : 0).sum();
		
		if (factory.owner == 1) {
			return enemyTroops - (factory.cyborgs + myTroops);
		} else {
			return (factory.cyborgs + enemyTroops) - myTroops;
		}
	}
}
