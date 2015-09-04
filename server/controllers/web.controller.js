exports.index = function(req, res) {
	res.sendFile(req.dirname + '/views/index.html');
};

exports.errorNotFound = function(req, res) {
	res.sendStatus(404);
};