package bot_Ghost_in_the_Cell;

public class Bomb {
	public int owner;
	public Factory source;
	public Factory destination;
	public int turnsTillExplosion;
	
	public Bomb(int owner, Factory source, Factory destination, int turnsTillExplosion) {
		this.owner = owner;
		this.source = source;
		this.destination = destination;
		this.turnsTillExplosion = turnsTillExplosion;
	}
}
