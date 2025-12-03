var express = require('express');
var bodyParser = require('body-parser');
var async       = require('async');
var routes = require("./routes.js");

var DB = require("./_database/ltdd_db")

var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

routes(app);

// var server = app.listen(4380, function () {
//   console.log("app running on port.", server.address().port);
// });  
DB.Init(function (err, result){
    if (err) process.exit(1);
    var server = app.listen(4380, function () {
        console.log("app running on port.", server.address().port);
    });    
});