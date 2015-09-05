var http = require('http'),
	faye = require('faye'),
	express = require('express'),
	bodyParser = require('body-parser'),
	mongoose = require('mongoose'),
	userCtrl = require('./controllers/user.controller');

var app = express(),
	server = http.createServer(app);

var bayeux = new faye.NodeAdapter({ mount: '/chat' });
bayeux.attach(server);
var client = bayeux.getClient();

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

app.use('/js', express.static('bower_components/jquery/dist'));

app.use(express.static('public'));

app.use(function (req, res, next) { // __dirname is the current dir of the current js file.
	req.dirname = __dirname;
	next();
});

app.use('/', require('./routes'));

client.subscribe('/register', function (message) { // message: { sessionId: String, username: String }
	console.log('Register new user');
	userCtrl.addUser(message.sessionId, message.username);

	client.publish('/register/' + message.sessionId, { userId: message.sessionId });
	client.publish('/join', { text: '<b>' + message.username + '</b> has joined.' });
	console.log('Sent user register information');
});

client.subscribe('/public/server', function (message) { // message: { userId: String, text: String }
	userCtrl.getUser(message.userId, function (index, user) {
		if (!user) return;

		client.publish('/public', { text: '<b>' + user.username + '</b>: ' + message.text });
	});
});


server.listen(8001);