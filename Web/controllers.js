

module.exports.employe_overview = (req,res) => {
    console.log('Anand in home');
    return res.render('home');
}
module.exports.employe_info = (req,res) => {
    console.log('Anand in emp_info');
    console.log(req.query.emp_details);
    const emp_details = JSON.parse(req.query.emp_details);
    console.log(emp_details);
    return res.render('emp_info',{
        emp_details:emp_details
    });
};