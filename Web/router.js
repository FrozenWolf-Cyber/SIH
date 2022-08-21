

const router = require('express').Router();
const controllers = require('./controllers');

router.get('/',controllers.render_sign_in);
router.post('/auth',controllers.auth);
router.get('/emp_list',controllers.emp_list);
router.get('/emp_overview',controllers.employe_overview);
router.get('/create_emp',controllers.create_emp);
router.get('/emp-info',controllers.employe_info);
router.post('/profile_img',controllers.createProfileImage);

module.exports = router;