
const Buffer = require('buffer/').Buffer;
const DB = require("./_global/ltdd_db")
const UTILS = require('./_global/utils.js');

var appRouter = function (app) {

  app.get("/", function (req, res) {
    UTILS.apiResult(1, 'WebService RESTFUL API - TIN4403 - Mobile Programming', res);
  });

  app.get("/users", async function (req, res) {
    const u = await DB.getUsers();
    UTILS.apiResult(1, JSON.stringify(u), res);
    //res.status(200).json(u);
    //res.status(200).send("RESTFUL API (/users)- NODEJS - TINK46");
  });

  app.post("/userinfo", function (req, res) {
    var token = req.headers.token;
    POST_userInfo(token, res);
    //res.status(200).send("USERINFO API");
  });

  app.post("/login", async function (req, res) {
    var user = req.body.username;
    var pass = req.body.password;
    POST_login(user, pass, res);
    // const u = await DB.Authentication(user,pass);
    // if (u){//ĐĂNG NHẬP THÀNH CÔNG
    //   res.status(200).json(u);
    // }
    // else{//ĐĂNG NHẬP LỖI
    //   res.status(302).send("FAILED - LOGIN API [" + user + "/" + pass +"]");
    // }
    // if (user == "vvdung" && pass == '111222')
    // 	res.status(200).send("ĐĂNG NHẬP THANH CONG [" + user + "/" + pass +"]");
    // else		
    // 	res.status(200).send("FAILED - LOGIN API [" + user + "/" + pass +"]");
  });

  // app.post("/register", function (req, res) {
  //   res.status(200).send("REGISTER API");
  // });
  app.post("/register", function (req, res) {
    var fullname = req.body.fullname;
    var user = req.body.username;
    var pass = req.body.password;

    POST_register(fullname, user, pass, res);
  });

  
  
  app.post("/userupdate", function (req, res) {
    var token = req.headers.token;
    var pass = req.body.password;
    var name = req.body.fullname;
    var email = req.body.email;

    var info = {
      password: pass ? pass.toString() : null,
      fullname: name ? name.toString() : null,
      email: email ? email.toString() : null
    }
    //console.log(info);
    POST_userUpdate(token, info, res);
  });
}

module.exports = appRouter;

async function POST_login(user, pass, res) {
  if (user == undefined || !user || user.length < 3) {
    UTILS.apiResult(-1, "Tài khoản không hợp lệ", res);
    return;
  }
  if (pass == undefined || !pass || pass.length < 6) {
    UTILS.apiResult(-2, "Mật khẩu không hợp lệ", res);
    return;
  }

  if (!(await DB.Authentication(user, pass))) {
    UTILS.apiResult(-3, "Thông tin tài khoản không chính xác", res);
    return;
  }

  //Chuyển object thành chuổi Base64 - sử dụng cho các hàm sau khi đã login thành công
  var user_ = {};
  user_["u"] = user;       //tên tài khoản đã đăng nhập
  user_["t"] = ~~(Date.now() / 1000); //thời gian đăng nhập (epoch second) - có thể dùng để yêu cầu đăng nhập lại nếu vượt quá thời gian xxx
  var token = Buffer.from(JSON.stringify(user_), 'utf8').toString('base64');
  //console.log(token);
  UTILS.apiResult(1, token, res);
}
async function POST_userInfo(token, res) {
  //console.log("userinfo TOKEN: [",token,"]");    

  var oResult = decodeToken(token);
  if (oResult.error != 1) {
    UTILS.apiResult(oResult.error, oResult.message, res);
    return;
  }

  var oToken = oResult.message;
  var u = await DB.getUser(oToken.u);
  if (!u) {
    UTILS.apiResult(-4, "Không tìm thấy tài khoản - Token không hợp lệ", res);
    return;
  }

  UTILS.apiResult(1, u, res);
}
async function POST_userUpdate(token, info, res) {
  //console.log("userUpdate TOKEN: [",token,"]");

  var oResult = decodeToken(token);
  if (oResult.error != 1) {
    UTILS.apiResult(oResult.error, oResult.message, res);
    return;
  }

  var mod_ = {};
  var count = 0;
  if (!(info.password == undefined || !info.password || info.password.length < 6)) {
    count++;
    mod_['password'] = info.password;
  }

  if (!(info.fullname == undefined || !info.fullname)) {
    count++;
    mod_['fullname'] = info.fullname;
  }

  if (!(info.email == undefined || !info.email)) {
    count++;
    mod_['email'] = info.email;
  }

  var oToken = oResult.message;

  if (count == 0) {
    UTILS.apiResult(-4, "[" + oToken.u + "] Không có gì để cập nhật", res);
    return;
  }

  var u = await DB.modifyUser(oToken.u, mod_);
  if (!u || !u.matchedCount) {
    UTILS.apiResult(-5, "Không tìm thấy tài khoản - Token không hợp lệ", res);
    return;
  }
  if (!u.modifiedCount) {
    UTILS.apiResult(1, "[" + oToken.u + "] Dữ liệu không thay đổi", res);
    return;
  }
  UTILS.apiResult(1, "[" + oToken.u + "] Cập nhật thành công", res);
}

//Hàm giải mã token đã được mã hóa từ hàm POST_login khi đăng nhập thành công
function decodeToken(token) {
  var oResult = {};
  if (token == undefined || !token) {
    oResult['error'] = -1;
    oResult['message'] = "Yêu cầu đăng nhập trước khi sử dụng";
    return oResult;
  }

  //Chuyển token từ Base64 => Object - có dạng như ở phần login thành công
  var user_;
  try {
    var plain = Buffer.from(token, 'base64').toString('utf8');
    //console.log(plain);
    user_ = JSON.parse(plain);
    //console.log(user_);//Không hiển thị nếu Parse lỗi
  }
  catch (e) {
    //console.log(e);
    oResult['error'] = -101;
    oResult['message'] = "Token -> JSON không hợp lệ";
    return oResult;
  }

  if (user_.u == undefined || !user_.u ||
    user_.t == undefined || !user_.t) {
    oResult['error'] = -2;
    oResult['message'] = "JSON không hợp lệ";
    return oResult;
  }

  //kiểm tra thời gian đã logined, tính theo seconds
  var curSeconds = ~~(Date.now() / 1000);
  if (curSeconds - user_.t > (60 * 5)) { //5phut
    oResult['error'] = -3;
    oResult['message'] = "Hết thời gian, yêu cầu đăng nhập lại để lấy token";
    return oResult;
  }

  oResult['error'] = 1;
  oResult['message'] = user_;

  return oResult;
}

//register
async function POST_register(fullname, user, pass, res) {

  // kiểm tra fullname
  if (fullname == undefined || !fullname || fullname.length < 3) {
    UTILS.apiResult(-1, "Họ tên không hợp lệ", res);
    return;
  }

  // kiểm tra username
  if (user == undefined || !user || user.length < 3) {
    UTILS.apiResult(-2, "Tài khoản không hợp lệ", res);
    return;
  }

  // kiểm tra password
  if (pass == undefined || !pass || pass.length < 6) {
    UTILS.apiResult(-3, "Mật khẩu không hợp lệ", res);
    return;
  }

  // kiểm tra trùng username
  var u = await DB.getUser(user);
  if (u) {
    UTILS.apiResult(-4, "Tài khoản đã tồn tại", res);
    return;
  }

  // tạo user mới
  var newUser = {
    fullname: fullname,
    username: user,
    password: pass
  };

  var r = await DB.insertUser(newUser);
  if (!r) {
    UTILS.apiResult(-5, "Đăng ký không thành công", res);
    return;
  }

  UTILS.apiResult(1, "Đăng ký thành công", res);
}
//