

const express = require('express');
const sassMiddleware = require('node-sass-middleware');
const layout = require('express-ejs-layouts');
const path = require('path');
const app = express();
const port = 9996;
const bodyParser = require('body-parser');
//setting up static files view_engine and sass middleaware
app.use(sassMiddleware({
    src:path.join(__dirname,'/assets/scss'),
    dest:path.join(__dirname,'/assets/css'),
    debug:true, //untill production stage it should be true
    outputStyle:"expanded",
    prefix:"/css"
 }));
app.use(express.static('./assets'));
app.set('views',path.join(__dirname,'/views'));
app.set('view engine','ejs');
app.use(layout);
app.set("layout extractScripts", true);
app.set('layout extractStyles',true);
app.use(bodyParser.urlencoded());
app.use('/',require('./router'));
app.listen(port,() => {
    console.log('app :: listening on port',port);
});