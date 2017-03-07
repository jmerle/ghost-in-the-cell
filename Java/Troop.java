package bot_Ghost_in_the_Cell;

public class Troop {
	public int owner;
	public Factory source;
	public Factory destination;
	public int cyborgs;
	public int remainingTurns;
	
	public Troop(int owner, Factory source, Factory destination, int cyborgs, int remainingTurns) {
		this.owner = owner;
		this.source = source;
		this.destination = destination;
		this.cyborgs = cyborgs;
		this.remainingTurns = remainingTurns;
	}
}
