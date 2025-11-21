var express = require('express');
var bodyParser = require('body-parser');
var async       = require('async');
var routes = require("./routes.js");


var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

routes(app);

var server = app.listen(4380, function () {
  console.log("app running on port.", server.address().port);
});  
