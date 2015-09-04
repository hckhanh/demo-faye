var router = require('express').Router();
var web = require('../controllers/web.controller');

router.get('/', web.index);

router.all('*', web.errorNotFound);

module.exports = router;