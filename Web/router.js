

const router = require('express').Router();
const controllers = require('./controllers');

router.get('/home',controllers.employe_overview);
router.get('/emp-info',controllers.employe_info);
module.exports = router;