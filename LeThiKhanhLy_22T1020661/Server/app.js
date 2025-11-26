var express = require('express');
var routes = require("./routes.js");

var app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

routes(app);

var server = app.listen(4380, function () {
    console.log("Server running on port", server.address().port);
});