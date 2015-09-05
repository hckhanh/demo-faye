var users = [];

exports.getUser = function (userId, callback) {
	for (var i = users.length - 1; i >= 0; i--) {
		if (users[i].id === userId) {
			callback(i, users[i]);
		}
	}
	callback();
}

exports.removeUser = function  (userId) {
	for (var i = users.length - 1; i >= 0; i--) {
		if (users.id === userId)
			users.splice(i, 1);
	};
}

exports.addUser = function  (userId, username) {
	users.push({ id: userId, username: username });
}
