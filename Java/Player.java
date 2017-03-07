package bot_Ghost_in_the_Cell;

import java.util.Scanner;

public class Player {
	private Scanner sc = new Scanner(System.in);
	private Board board;
	private Strategy strategy = new BronzeStrategy();
	
	public void run() {
		this.board = new Board(this.sc.nextInt());
		for (int i = 0, iMax = this.sc.nextInt(); i < iMax; i++) {
			int factory1 = sc.nextInt();
			int factory2 = sc.nextInt();
			int distance = sc.nextInt();
			this.board.factories[factory1].setDistance(factory2, distance);
			this.board.factories[factory2].setDistance(factory1, distance);
		}
		
		while (true) {
			this.board.initRound();
			
			for (int i = 0, iMax = sc.nextInt(); i < iMax; i++) {
				int id = sc.nextInt();
				String type = sc.next();
				int arg1 = sc.nextInt();
				int arg2 = sc.nextInt();
				int arg3 = sc.nextInt();
				int arg4 = sc.nextInt();
				int arg5 = sc.nextInt();
				
				switch (type) {
					case "FACTORY":
						this.board.factories[id].update(arg1, arg2, arg3, arg4);
						break;
					case "TROOP":
						this.board.addTroop(new Troop(arg1, this.board.factories[arg2], this.board.factories[arg3], arg4, arg5));
						break;
					case "BOMB":
						this.board.addBomb(new Bomb(arg1, this.board.factories[arg2], arg3 != -1 ? this.board.factories[arg3] : null, arg4));
						break;
					default:
						System.err.println("Unknown entity: " + type);
				}
			}
			
			this.strategy.execute(this.board);
		}
	}
	
	public static void main(String[] args) {
		Player player = new Player();
		player.run();
	}
}
