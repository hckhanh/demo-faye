var http = require('http'),
	faye = require('faye'),
	express = require('express'),
	bodyParser = require('body-parser'),
	mongoose = require('mongoose');

var app = express(),
	server = http.createServer(app);

var bayeux = new faye.NodeAdapter({ mount: '/chat' });
bayeux.attach(server);

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

app.use('/js', express.static('node_modules/faye/browser'));

app.use('/js', express.static('bower_components/jquery/dist'));

app.use(express.static('public'));

app.use(function (req, res, next) { // __dirname is the current dir of the current js file.
	req.dirname = __dirname;
	next();
});

app.use('/', require('./routes'));

var users = [];

function getUser (clientId, callback) {
	for (var i = users.length - 1; i >= 0; i--) {
		if (users[i]._id == clientId) {
			callback(i, users[i]);
		}
	};
	callback();
}

function removeUser (clientId) {
	for (var i = users.length - 1; i >= 0; i--) {
		if (users._id == clientId)
			users.splice(i, 1);
	};
}

bayeux.on('subscribe', function (clientId, channel) {
	if (channel == '/join') {
		users.push({ _id: clientId, username: '' });
	}
});

bayeux.on('disconnect', function (clientId) {
	getUser(clientId, function (index, user) {
		if (!user) return;

		bayeux.getClient().publish('/public', { text: '<b>' + user.username + '</b> has left.' });
		users.splice(index, 1);
	});
});

bayeux.on('unsubscribe', function (clientId, channel) {
	if (channel == '/public') {
		removeUser(clientId);
	}
});

bayeux.on('publish', function (clientId, channel, message) {
	switch (channel) {
		case '/register':
			getUser(clientId, function (index, user) {
				user.username = message.text;
				bayeux.getClient().publish('/join', { text: '<b>' + user.username + '</b> has joined.' });
			});
			break;
		case '/server/public':
			getUser(clientId, function (index, user) {
				bayeux.getClient().publish('/public', { text: '<b>' + user.username + '</b> ' + message.text });
			});
			break;
	}
});

server.listen(8001);