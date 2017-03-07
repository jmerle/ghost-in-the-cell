/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};

/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {

/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;

/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};

/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);

/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;

/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}


/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;

/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;

/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";

/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	__webpack_require__(1);
	__webpack_require__(4);
	__webpack_require__(5);
	__webpack_require__(2);
	__webpack_require__(7);
	__webpack_require__(6);
	module.exports = __webpack_require__(3);


/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	const Factory = __webpack_require__(2);
	const Troop = __webpack_require__(3);
	const Bomb = __webpack_require__(4);

	class Board {
	    constructor(factoryCount) {
	        this.factories = [];
	        this.troops = [];
	        this.bombs = [];

	        for (let i = 0; i < factoryCount; i++) {
	            this.factories[i] = new Factory(i);
	        }
	        
	        this.turn = -1;
	        this.bombsAvailable = 2;
	    }

	    addTroop(owner, sourceID, destinationID, production, remainingTurns) {
	        const troop = new Troop(owner, this.factories[sourceID], this.factories[destinationID], production, remainingTurns);
	        this.troops.push(troop);
	        this.factories[destinationID].incomingTroops.push(troop);
	    }

	    addBomb(owner, sourceID, destinationID, remainingTurns) {
	        const bomb = new Bomb(owner, this.factories[sourceID], destinationID !== -1 ? this.factories[destinationID] : null, remainingTurns);
	        this.bombs.push(bomb);
	        if (destinationID !== -1) {
	            this.factories[destinationID].incomingBombs.push(bomb);
	        }
	    }

	    initRound() {
	        this.troops = [];
	        this.bombs = [];
	        this.factories.forEach(factory => {
	            factory.incomingTroops = [];
	            factory.incomingBombs = [];
	        });

	        this.turn++;
	    }
	}

	module.exports = Board;

/***/ },
/* 2 */
/***/ function(module, exports) {

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

/***/ },
/* 3 */
/***/ function(module, exports) {

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

/***/ },
/* 4 */
/***/ function(module, exports) {

	class Bomb {
	    constructor(owner, source, destination, remainingTurns) {
	        this.owner = owner;
	        this.source = source;
	        this.destination = destination;
	        this.remainingTurns = remainingTurns;
	    }
	}

	module.exports = Bomb;

/***/ },
/* 5 */
/***/ function(module, exports, __webpack_require__) {

	const Board = __webpack_require__(1);
	const Strategy = __webpack_require__(6);

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

/***/ },
/* 6 */
/***/ function(module, exports, __webpack_require__) {

	const Troop = __webpack_require__(3);

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

/***/ },
/* 7 */
/***/ function(module, exports, __webpack_require__) {

	const Bot = __webpack_require__(5);

	const bot = new Bot();
	bot.run();

/***/ }
/******/ ]);