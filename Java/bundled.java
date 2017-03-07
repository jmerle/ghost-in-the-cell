import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Player {
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

class BasicStrategy implements Strategy {
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

class Board {
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

class Bomb {
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

class BronzeStrategy implements Strategy {
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

class Factory {
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

class Link {
    public Factory factory1;
    public Factory factory2;
    public int distance;
    
    public Link(Factory factory1, Factory factory2, int distance) {
        this.factory1 = factory1;
        this.factory2 = factory2;
        this.distance = distance;
    }
}

interface Strategy {
    public abstract void execute(Board board);
}

class Troop {
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