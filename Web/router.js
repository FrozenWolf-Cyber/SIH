

const router = require('express').Router();
const controllers = require('./controllers');

router.get('/emp_list',controllers.emp_list);
router.get('/emp_overview',controllers.employe_overview);
router.get('/create_emp',controllers.create_emp);
router.get('/emp-info',controllers.employe_info);
module.exports = router;