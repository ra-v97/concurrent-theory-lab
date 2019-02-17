var async = require("async");

function printAsync(s, cb) {
   var delay = Math.floor((Math.random()*1000)+500);
   setTimeout(function() {
       console.log(s);
       if (cb) cb();
   }, delay);
}

function waterfallLoop(executionCount){
	if(executionCount > 0){
		async.waterfall([
			function(callback){
				printAsync("1",callback)
			},
			function(callback){
				printAsync("2",callback)
			},
			function(callback){
				printAsync("3",callback)
			},
		], function(err,result) {
			//error handling
			 waterfallLoop(executionCount-1);
		}); 
			
	}else{
		console.log('done!');
	}
}

waterfallLoop(4);