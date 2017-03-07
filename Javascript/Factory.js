class Factory {
    constructor(id) {
        this.id = id;
        
        this.owner = null;
        this.cyborgs = null;
        this.production = null;
        this.turnsTillNormalProduction = null;

        this.incomingTroops = [];
        this.incomingBombs = [];

        this.distances = new Map();
    }

    update(owner, cyborgs, production, turnsTillNormalProduction) {
        this.owner = owner;
        this.cyborgs = cyborgs;
        this.production = production;
        this.turnsTillNormalProduction = turnsTillNormalProduction;
    }

    getScore() {
        const myCyborgs = this.incomingTroops.filter(t => t.owner === 1).map(t => t.cyborgs).reduce((t1, t2) => t1 + t2, 0);
        const enemyCyborgs = this.incomingTroops.filter(t => t.owner === -1).map(t => t.cyborgs).reduce((t1, t2) => t1 + t2, 0);

        if (this.owner === 1) {
            let production = 0;
            
            /* this.incomingTroops.filter(t => t.owner === -1).forEach(troop => {
                production += troop.remainingTurns * this.production;
            }); */

            return enemyCyborgs - (this.cyborgs + myCyborgs + production);
        } else if (this.owner === -1) {
            let production = 0;

            /* this.incomingTroops.filter(t => t.owner === 1).forEach(troop => {
                production += troop.remainingTurns * this.production;
            }); */

            return (this.cyborgs + enemyCyborgs + production) - myCyborgs;
        } else {
            return (this.cyborgs + enemyCyborgs) - myCyborgs;
        }
    }

    distanceTo(factory) {
        return this.distances.get(factory.constructor.name === 'Factory' ? factory.id : factory);
    }
}

module.exports = Factory;