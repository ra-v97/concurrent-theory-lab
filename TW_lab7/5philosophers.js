var async = require("async");

var N =5;

var times = [];
for (var i = 0; i < N; i++) {
    times.push(0);
}

var Fork = function(name) {
    this.state = 0;
	this.maxWaitingTime = 4096;
	//this.maxTrials = 10;
	this.name = name;
    return this;
}

Fork.prototype.acquire = function(id,cb) { 
    // zaimplementuj funkcje acquire, tak by korzystala z algorytmu BEB
    // (http://pl.wikipedia.org/wiki/Binary_Exponential_Backoff), tzn:
    // 1. przed pierwsza proba podniesienia widelca Filozof odczekuje 1ms
    // 2. gdy proba jest nieudana, zwieksza czas oczekiwania dwukrotnie
    //    i ponawia probe itd.
	//var trials = 0;
	var time = 1; //next try waiting time in setTimeout;
	var getFork = function (waitingTime, fork) {
        setTimeout(function () {
            if (fork.state == 1) {
                if (time < fork.maxWaitingTime) time *= 2;
                //if (trials > fork.maxTrials) {
				//	console.log("Too many attempts error");
                //}
                //else {
                    //trials++;
					times[id]+=waitingTime;
					var wt = Math.floor(Math.random() * (time-1)) + 1;
                    getFork(wt, fork);
					console.log("Philosopher "+id +" is waiting for "+wt)
                //}
            } else {
                fork.state = 1;
				console.log("Fork "+ fork.name+" is up");
                if(cb) cb();
            }
        }, waitingTime);
    };
    getFork(1, this);
}

Fork.prototype.release = function(cb) { 
    this.state = 0; 
	console.log("Fork "+ this.name+" is released");
	if(cb)cb();
}

var Philosopher = function(id, forks) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.f2 = (id+1) % forks.length;
    return this;
}

Philosopher.prototype.eat = function printAsync(id,cb) {
   var eatingTime = Math.floor((Math.random()*100)+1);
   //var eatingTime =0;
   setTimeout(function() {
       console.log("Philosopher "+id+" is eating for "+eatingTime+"ms.");
       if (cb) cb();
   }, eatingTime);
}

Philosopher.prototype.startNaive = function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
		eat=this.eat,
		philosopher = this;
    // zaimplementuj rozwiazanie naiwne
    // kazdy filozof powinien 'count' razy wykonywac cykl
    // podnoszenia widelcow -- jedzenia -- zwalniania widelcow
	if(count > 0){
		async.waterfall([
			function(callback){
				forks[f1].acquire(id,callback)
			},
			function(callback){
				forks[f2].acquire(id,callback)
			},
			function(callback){
				eat(id,callback);
			},
			function(callback){
				forks[f1].release(callback)
			},
			function(callback){
				forks[f2].release(callback)
			},
		], function(err,result) {
			//error handling
			philosophers[id].startNaive(count - 1);
		}); 
			
	}else{
		console.log('done!');
	}
}

Philosopher.prototype.startAsym = function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
		eat=this.eat,
		philosopher = this;
    // zaimplementuj rozwiazanie asymetryczne
    // kazdy filozof powinien 'count' razy wykonywac cykl
    // podnoszenia widelcow -- jedzenia -- zwalniania widelcow
	if(count > 0){
		async.waterfall([
			function(callback){
				if(id%2==0){
					forks[f1].acquire(id,callback)
				}
				else{
					forks[f2].acquire(id,callback)
				}
			},
			function(callback){
				if(id%2==0){
					forks[f2].acquire(id,callback)
				}
				else{
					forks[f1].acquire(id,callback)
				}
			},
			function(callback){
				eat(id,callback);
			},
			function(callback){
				forks[f1].release(callback)
			},
			function(callback){
				forks[f2].release(callback)
			},
		], function(err,result) {
			//error handling
			philosophers[id].startAsym(count - 1);
		}); 
			
	}else{
		console.log("Philosopher "+this.id+" id done!");
	}
}

var Conductor = function (philosophersCount) {
    this.philosophers = philosophersCount - 1;
    return this;
};

Conductor.prototype.ask = function (id,cb) {
	var time = 1;
	var cndctr = this;
	var waiting_time=0;
    if(this.philosophers>0){
		this.philosophers-=1;
		if(cb)cb();
	}
	else{
		waiting_time = Math.floor(Math.random() * (time-1)) + 1;
		times[id]+=waiting_time;
        setTimeout(function () {
            time *= 2;
            cndctr.ask(cb);  
        },waiting_time );
	}
};

Conductor.prototype.release = function (cb) {
    this.philosophers+=1;
	if(cb)cb();
};

Philosopher.prototype.startConductor = function(conductor,count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
		eat=this.eat;
    // zaimplementuj rozwiazanie z kelnerem
    // kazdy filozof powinien 'count' razy wykonywac cykl
    // podnoszenia widelcow -- jedzenia -- zwalniania widelcow
	if(count > 0){
		async.waterfall([
			function(callback){
				conductor.ask(id,callback)
			},
			function(callback){
				forks[f1].acquire(id,callback)
			},
			function(callback){
				forks[f2].acquire(id,callback)
			},
			function(callback){
				eat(id,callback);
			},
			function(callback){
				forks[f1].release(callback)
			},
			function(callback){
				forks[f2].release(callback)
			},
			function(callback){
				conductor.release(callback)
			},
		], function(err,result) {
			//error handling
			philosophers[id].startConductor(conductor,count - 1);
		}); 
			
	}else{
		console.log("Philosopher "+this.id+" id done!");
	}
}

var K =20;
var forks = [];
var philosophers = []
var conductor = new Conductor(N);


for (var i = 0; i < N; i++) {
    forks.push(new Fork(i+1));
}

for (var i = 0; i < N; i++) {
    philosophers.push(new Philosopher(i, forks));
}

for (var i = 0; i < N; i++) {
    //philosophers[i].startConductor(conductor,K);
	philosophers[i].startConductor(conductor,K);
}

// const createCsvWriter = require('csv-writer').createObjectCsvWriter;
// const csvWriter = createCsvWriter({
    // path: 'data.csv',
    // header: [
        // {id: 'algorithm', title: 'ALGORITHM'},
        // {id: 'philosophers', title: 'PHILOSOPHERS_NUMBER'},
		// {id: 'attempts', title: 'ATTEMPTS'},
		// {id: 'single_time', title: 'TIME'}
    // ]
// });

// var records = [];

// setTimeout(function(){
	// for(var i = 0; i < N ; i++) {
		// records.push({algorithm:'asymetric',philosophers:N,attempts:K,single_time:times[i]})
		// console.log("Philosopher "+i+" blocked for "+times[i]);
	// }
	// csvWriter.writeRecords(records)       // returns a promise
    // .then(() => {
        // console.log('...Done');
    // });
// },18000)


	

