

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
    console.log(req.query.emp_details);
    const emp_details = JSON.parse(req.query.emp_details);
    console.log(emp_details);
    return res.render('emp_info',{
        emp_details:emp_details
    });
};