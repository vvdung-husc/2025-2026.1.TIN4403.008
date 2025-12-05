var async = require('async');

var appRouter = function (app) {

  app.get("/", function (req, res) {
    res.status(200).send("Welcome to RESTFUL API - NODEJS - TINK46");
  });

  app.get("/users", function (req, res) {
    res.status(200).send("RESTFUL API (/users)- NODEJS - TINK46");
  });

  app.post("/userinfo", function (req, res) {
    res.status(200).send("USERINFO API");
  });
  
  app.post("/login", function (req, res) {
    var user = req.body.username;
    var pass = req.body.password;
    if (user == "vvdung")
      res.status(200).send("HÀM ĐĂNG NHẬP [" + user + "/" + pass + "]");
    else
      res.status(200).send("LOGIN API [" + user + "/" + pass + "]");
  });
  
  app.post("/register", function (req, res) {
    res.status(200).send("REGISTER API");
  });

}

module.exports = appRouter;
