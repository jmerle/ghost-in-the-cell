const Troop = require('./Troop');

class Strategy {
    execute(board) {
        let moves = ['MSG :D'];

        if (board.bombsAvailable > 0) {
            const bombSources = board.factories.filter(f => f.owner === 1);
            const bombTargets = board.factories.filter(f => f.owner === -1 && f.production >= 2 && f.cyborgs >= 5 && f.getScore() > 0 && f.incomingBombs.length === 0);

            if (bombSources.length > 0 && bombTargets.length > 0) {
                const target = bombTargets.sort((f1, f2) => f1.getScore() < f2.getScore())[0];
                const source = bombSources.sort((f1, f2) => f1.distanceTo(target) > f2.distanceTo(target))[0];
                moves.push('BOMB ' + source.id + ' ' + target.id);
                board.bombsAvailable--;
            }
        }

        while (moves.length < 100) {
            const move = this.getMove(board);

            if (move) {
                moves.push(move);
            } else {
                break;
            }
        }

        printErr(moves.length);

        return moves;
    }

    getMove(board) {
        const myFactories = board.factories.filter(f => f.owner === 1);
        
        for (let i = 0, iMax = myFactories.length; i < iMax; i++) {
            const factory = myFactories[i];

            if (factory.cyborgs <= 0) continue;

            if (board.turn > 2 && factory.cyborgs >= 10 && factory.getScore() <= -10 && factory.production < 3) {
                factory.cyborgs -= 10;
                return 'INC ' + factory.id;
            }
            
            const targets = board.factories.filter(f => f.id !== factory.id && f.getScore() + 1 > 0);

            if (targets.length > 0) {
                const target = targets.sort((f1, f2) => f1.distanceTo(factory) > f2.distanceTo(factory))[0];
                const cyborgsToSend = target.getScore() + 1;
                
                factory.cyborgs -= cyborgsToSend;
                target.incomingTroops.push(new Troop(1, factory, target, cyborgsToSend, factory.distanceTo(target)));

                return 'MOVE ' + factory.id + ' ' + target.id + ' ' + cyborgsToSend;
            }
        }

        return false;
    }
}

module.exports = Strategy;