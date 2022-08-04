

const express = require('express');
const sassMiddleware = require('node-sass-middleware');
const path = require('path');
const app = express();
const port = 9996;

//setting up static files view_engine and sass middleaware
app.use(sassMiddleware({
    src:path.join(__dirname,'/assets/scss'),
    dest:path.join(__dirname,'/assets/css'),
    debug:true, //untill production stage it should be true
    outputStyle:"compressed",
    prefix:"/css"
 }));
app.use(express.static('./assets'));
app.set('views',path.join(__dirname,'/templates'));
app.set('view engine','ejs');

app.listen(port,() => {
    console.log('app :: listening on port',port);
});