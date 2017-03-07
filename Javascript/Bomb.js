class Bomb {
    constructor(owner, source, destination, remainingTurns) {
        this.owner = owner;
        this.source = source;
        this.destination = destination;
        this.remainingTurns = remainingTurns;
    }
}

module.exports = Bomb;