class Troop {
    constructor(owner, source, destination, cyborgs, remainingTurns) {
        this.owner = owner;
        this.source = source;
        this.destination = destination;
        this.cyborgs = cyborgs;
        this.remainingTurns = remainingTurns;
    }
}

module.exports = Troop;