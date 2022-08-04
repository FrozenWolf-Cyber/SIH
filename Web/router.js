

const router = require('express').Router();
const controllers = require('./controllers');

router.get('/home',controllers.employe_overview);
module.exports = router;