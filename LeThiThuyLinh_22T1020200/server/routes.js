

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
    if (user == "vvdung" && pass == '111222')
    	res.status(200).send("ĐĂNG NHẬP THANH CONG [" + user + "/" + pass +"]");
	  else		
			res.status(200).send("FAILED - LOGIN API [" + user + "/" + pass +"]");
  });
  
  app.post("/register", function (req, res) {
     var user = req.body.username;
    var pass = req.body.password;

    // Kiểm tra tài khoản đã tồn tại
    var exist = users.find(u => u.username === user);

    if (exist)
      res.status(200).send("REGISTER FAILED - USERNAME ĐÃ TỒN TẠI");
    else {
      users.push({ username: user, password: pass });
      res.status(200).send("TẠO TÀI KHOẢN THÀNH CÔNG [" + user + "]");
    }
  });

  app.post("/update", function (req, res) {
    var user = req.body.username;
    var newpass = req.body.newpassword;

    // Tìm tài khoản
    var acc = users.find(u => u.username === user);

    if (!acc)
      res.status(200).send("UPDATE FAILED - USER KHÔNG TỒN TẠI");
    else {
      acc.password = newpass;  // cập nhật mật khẩu
      res.status(200).send("CẬP NHẬT THÀNH CÔNG [" + user + "]");
    }
  });

}

module.exports = appRouter;
