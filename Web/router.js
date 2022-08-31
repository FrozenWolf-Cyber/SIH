

const router = require('express').Router();
const controllers = require('./controllers');
const superagent = require('superagent');
router.get('/',controllers.render_sign_in);
router.post('/auth',controllers.auth);
router.get('/emp_list',controllers.emp_list);
router.get('/emp_overview',controllers.employe_overview);
router.get('/create_emp',controllers.create_emp);
router.get('/emp-info',controllers.employe_info);
router.post('/profile_img',controllers.createProfileImage);


// let davis;
// router.post('/set_davis',(req,res) => {
//     console.log(req.body); 
//     davis = req.body;
//     return res.json({
//         message:'setted'
//     });
// });

router.post('/get_davis',(req,res) => {
    superagent
      .post('http://sih-smart-attendance.herokuapp.com/get_info')
      .send({emp_no:'a'+req.query.emp_no+'a'}) // sends a JSON post body
      .set('accept', 'json')
      .end((err, data) => {
      
        if(err)
        {
            console.log(err);
            return res.json({
                message:'you not got the davis',
            });
        }
        console.log(data);
        return res.json({
            message:'you got the davis',
        });
  });

//   var emp_data;
// $.ajax('http://sih-smart-attendance.herokuapp.com/get_info', {
//         type: 'POST',  
//         data:{emp_no:'a'+emp_no+'a'},
//         success: function (data, status, xhr) {
//              emp_data = data; 
//              console.log(data);
//              empProfileRender(data);
//              davis(data);          
//         }
//     });
    
})
module.exports = router;