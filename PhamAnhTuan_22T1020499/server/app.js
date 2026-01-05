var express = require('express');
var routes = require("./routes.js");
var DB = require("./_global/ltdd_db")
var app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

routes(app);

// Khởi tạo kết nối DB trước khi chạy server
DB.Init().then((result) => {
    if (!result) process.exit(1);
    var server = app.listen(4380, function () {
        console.log("app running on port.", server.address().port);
    });
});

