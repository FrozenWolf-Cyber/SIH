

console.log('Anand in all controllers');
const fs = require('fs');
const base64 = require('node-base64-image');
const { base64encode, base64decode } = require('nodejs-base64');
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
    return res.redirect('/emp_list');
};


module.exports.createProfileImage = async (req,res) => {

    try{
        // var buf = Buffer.from(req.body.base64, 'base64');
        // fs.writeFileSync("new-profile.txt", buf);
        // var base64Data = req.body.base64.replace(/^data:image\/png;base64,/, "");
        // fs.writeFile("out.png", base64Data, 'base64', function(err) {
        //       console.log(err);
        // });
        // console.log(req.body.base64);
        // await base64.decode(req.body.base64, { fname: 'example', ext: 'jpg' });
        function saveImage(filename, data){
            var myBuffer = new Buffer(data.length);
            for (var i = 0; i < data.length; i++) {
                myBuffer[i] = data[i];
            }
            fs.writeFile(filename, myBuffer, function(err) {
                if(err) {
                    console.log(err);
                } else {
                    console.log("The file was saved!");
                }
            });
          }
          saveImage("not_profile.jpg", req.body);
        return res.json({
            message:'not: profile_pic saved'
        });
    }
    catch(err)
    {
        console.log(err);
        return res.json({
            message:'not:possible to save'
        });
    }

};