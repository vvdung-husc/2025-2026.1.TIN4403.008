

const DB = require("./_database/ltdd_db")
const jwt = require("jsonwebtoken");
var appRouter = function (app) {

  app.get("/", function (req, res) {
    res.status(200).send("Welcome to RESTFUL API - NODEJS - TINK46");
  });

  app.get("/users", async function (req, res) {
    const u = await DB.getUsers();
    res.status(200).json(u);
    //res.status(200).send("RESTFUL API (/users)- NODEJS - TINK46");
  });

  // app.post("/userinfo", function (req, res) {
  //   res.status(200).send("USERINFO API");
  // });
  //const jwt = require("jsonwebtoken");

// middleware kiểm tra token
function auth(req, res, next){
    const token = req.headers.authorization;

    if(!token){
        return res.status(401).json({r:0, msg:"Missing token"});
    }

    try{
        const decoded = jwt.verify(token, "SECRET_KEY");
        req.user = decoded;
        next();
    }catch(err){
        return res.status(401).json({r:0, msg:"Invalid token"});
    }
}


  const { ObjectId } = require("mongodb");

app.post("/userinfo", auth, async function(req,res){
    const userId = req.user.uid;

    const u = await DB.db_.collection("users")
                .findOne({_id: new ObjectId(userId)});

    if(!u){
        return res.status(404).json({r:0, msg:"User not found"});
    }

    return res.status(200).json({
        r:1,
        m:u
    });
});





app.post("/login", async function (req, res) {
  var user = req.body.username;
  var pass = req.body.password;
  
  const u = await DB.getByUsername(user, pass);

  if (!u) {
    return res.status(401).json({
      r: 0,
      message: "Sai tài khoản hoặc mật khẩu"
    });
  }

  const token = jwt.sign(
    {
      uid: u._id.toString(), 
      username: u.username
    },
    "SECRET_KEY",
    { expiresIn: "1h" }
  );

  return res.status(200).json({
    r: 1,
    message: "Đăng nhập thành công",
    token: token
  });
});


  // app.post("/login", async function (req, res) {
	// 	var user = req.body.username;
  //   var pass = req.body.password;	  
  //   const u = await DB.getByUsername(user,pass);
  //   if (u){//ĐĂNG NHẬP THÀNH CÔNG
  //     res.status(200).json(u);
  //   }
  //   else{//ĐĂNG NHẬP LỖI
  //     res.status(302).send("FAILED - LOGIN API [" + user + "/" + pass +"]");
  //   }
    // if (user == "vvdung" && pass == '111222')
    // 	res.status(200).send("ĐĂNG NHẬP THANH CONG [" + user + "/" + pass +"]");
	  // else		
		// 	res.status(200).send("FAILED - LOGIN API [" + user + "/" + pass +"]");
  //});
  
  // app.post("/register", function (req, res) {
  //   res.status(200).send("REGISTER API");
  // });





app.post("/register", async function (req, res) {
    var user = req.body.username;
    var pass = req.body.password;
    var fullname = req.body.fullname;
    var email = req.body.email;

    // kiểm tra trùng username
    const exists = await DB.getByUsernameOnly(user);

    if (exists) {
        return res.status(409).json({
            r: 0,
            msg: "USERNAME ALREADY EXISTS"
        });
    }

    // đăng ký
    const newUser = await DB.registerUser(user, pass, fullname, email);

    return res.status(200).json({
        r: 1,
        msg: "Register success",
        m: newUser
    });
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

