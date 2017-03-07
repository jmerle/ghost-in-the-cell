const Board = require('./Board');
const Strategy = require('./Strategy');

class Bot {
    constructor() {
        this.board = null;
        this.strategy = new Strategy();
    }

    run() {
        this.initGameData();

        while (true) {
            this.updateGameData();

            print(this.strategy.execute(this.board).join(';'));
        }
    }

    initGameData() {
        this.board = new Board(parseInt(readline()));

        for (let i = 0, iMax = parseInt(readline()); i < iMax; i++) {
            const data = readline().split(' ').map(Number);
            this.board.factories[data[0]].distances.set(data[1], data[2]);
            this.board.factories[data[1]].distances.set(data[0], data[2]);
        }
    }

    updateGameData() {
        this.board.initRound();

        for (let i = 0, iMax = parseInt(readline()); i < iMax; i++) {
            const data = readline().split(' ').map((val, i) => i !== 1 ? parseInt(val) : val);

            switch (data[1]) {
                case 'FACTORY':
                    this.board.factories[data[0]].update(data[2], data[3], data[4], data[5]);
                    break;
                case 'TROOP':
                    this.board.addTroop(data[2], data[3], data[4], data[5], data[6]);
                    break;
                case 'BOMB':
                    this.board.addBomb(data[2], data[3], data[4], data[5]);
                    break;
                default:
                    printErr('Unknown entity: ' + data[1]);
            }
        }
    }
}

module.exports = Bot;