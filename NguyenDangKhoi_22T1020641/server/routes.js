const DB = require("./_database/ltdd_db")

var appRouter = function (app) {

  app.get("/", function (req, res) {
    res.status(200).send("Welcome to RESTFUL API - NODEJS - TINK46");
  });

  app.get("/users", async function (req, res) {
    const u = await DB.getUsers();
    res.status(200).json(u);
    //res.status(200).send("RESTFUL API (/users)- NODEJS - TINK46");
  });

  app.post("/userinfo", function (req, res) {
    res.status(200).send("USERINFO API");
  });
  
  app.post("/login", async function (req, res) {
		var user = req.body.username;
    var pass = req.body.password;	  
    const u = await DB.getByUsername(user,pass);
    if (u){//ĐĂNG NHẬP THÀNH CÔNG
      res.status(200).json(u);
    }
    else{//ĐĂNG NHẬP LỖI
      res.status(302).send("FAILED - LOGIN API [" + user + "/" + pass +"]");
    }
  });
  
  app.post("/register", async function(req, res) {
    const { username, password, fullname} = req.body;

    if (!username || !password || !fullname) {
      return res.status(302).send("MISSING FIELDS");
    }

    const newUser = await DB.createUser(username, password, fullname);

      if (newUser) {
        res.status(200).json({ message: "USER REGISTERED", user: newUser });
      } else {
        res.status(302).send("USER EXISTED [" + username + "]");
      }
  });
  
}

module.exports = appRouter;