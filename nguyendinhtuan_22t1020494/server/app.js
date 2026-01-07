var express = require('express');
var routes = require("./routes.js");
var DB = require("./db/db_js.js");
var app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

routes(app);

DB.Init().then((result) => {
    if (!result) process.exit(1);
    var server = app.listen(4380, function () {
        console.log("app running on port:", server.address().port);
    });
});