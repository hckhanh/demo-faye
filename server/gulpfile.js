var gulp = require('gulp');
var nodemon = require('nodemon');

gulp.task('default', function() {
	nodemon({
		script: 'app.js',
	    ext: 'js',
	    ignore: ['node_modules', 'bower_components'],
	}).on('restart', function () {
  		console.log('restarted!');
	})
});