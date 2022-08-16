

console.log('Anand in all controllers');


module.exports.emp_list = (req,res) => {
    return res.render('emp_list');
}
module.exports.employe_overview = (req,res) => {
    return res.render('emp_overview');
}
module.exports.create_emp = (req,res) => {
    return res.render('create_emp');
}
module.exports.employe_overview
 module.exports.employe_info = (req,res) => {
    console.log('Anand in emp_info');
    console.log(req.query.emp_no);
    return res.render('emp_info',{
        emp_no:req.query.emp_no
    });
};

module.exports.render_sign_in = (req,res) =>{
    return res.render('sign_in',{layout:false});
};
module.exports.isAuth = false;


module.exports.auth = (req,res) => {
    console.log(req.body);
    return res.render('emp_list');
};