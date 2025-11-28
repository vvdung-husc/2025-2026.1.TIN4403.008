

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
    // if (user == "vvdung" && pass == '111222')
    // 	res.status(200).send("ĐĂNG NHẬP THANH CONG [" + user + "/" + pass +"]");
	  // else		
		// 	res.status(200).send("FAILED - LOGIN API [" + user + "/" + pass +"]");
  });
  
  app.post("/register", async function (req, res) {
    var user = req.body.username;
    var pass = req.body.password;	 
    var email = req.body.email;
    var fullname = req.body.fullname;
    const u = await DB.register(user, pass, email, fullname);
    if (u){//ĐĂNG KÝ THÀNH CÔNG
      res.status(200).json(u);
    }
    else{//ĐĂNG KÝ LỖI
      res.status(302).send("FAILED - REGISTER API [" + user + "/" + pass +"]");
    }
  });

  app.post("/update", async function (req, res){
    var user = req.body.username;
    var pass = req.body.password;	 
    var email = req.body.email;
    var fullname = req.body.fullname;
    const u = await DB.update( user, pass, email, fullname);
    if (u){//CẬP NHẬT THÀNH CÔNG
      res.status(200).json(u);
    }
    else{//CẬP NHẬT LỖI
      res.status(302).send("FAILED - UPDATE API [" + user + "/" + pass +"]");
    }
  });

}

module.exports = appRouter;

