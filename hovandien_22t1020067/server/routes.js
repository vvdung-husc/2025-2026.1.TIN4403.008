

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
  
  // app.post("/register", function (req, res) {
  //   res.status(200).send("REGISTER API");
  // });





  app.post("/register", async function (req, res) {
    var user = req.body.username;
    var pass = req.body.password;
    var fullname = req.body.fullname;

    // gọi DB
    const exists = await DB.getByUsernameOnly(user);

    if(exists){
        return res.status(409).send("USERNAME ALREADY EXISTS");
    }

    const newUser = await DB.registerUser(user, pass, fullname);
    res.status(200).json(newUser);
});


// UPDATE — nhận bất kì trường nào
app.post("/update", async function (req, res) {
    var user = req.body.username;       // username để tìm user cần sửa
    var data = req.body;                // tất cả các trường gửi lên

    // xoá field username khỏi data (vì username ko muốn SET đè chính nó)
    delete data.username;

    const updated = await DB.updateUser(user, data);

    if(updated.modifiedCount > 0){
        res.status(200).send("UPDATED SUCCESSFULLY");
    } else {
        res.status(404).send("USER NOT FOUND OR NO CHANGE");
    }
});

}

module.exports = appRouter;

