const Buffer = require('buffer/').Buffer;
const DB = require("./_global/ltdd_db.js")
const UTILS = require('./_global/utils.js');

var appRouter = function (app) {

  app.get("/", function (req, res) {
    UTILS.apiResult(1, 'WebService RESTFUL API - TIN4403 - Mobile Programming', res);
  });

  app.get("/users", async function (req, res) {
    const u = await DB.getUsers();
    UTILS.apiResult(1, JSON.stringify(u), res);
  });

  app.post("/userinfo", function (req, res) {
    var token = req.headers.token;
    POST_userInfo(token, res);
  });

  app.post("/login", async function (req, res) {
    var user = req.body.username;
    var pass = req.body.password;
    POST_login(user, pass, res);
  });

app.post("/register", async function (req, res) {
    var user = req.body.username;
    var pass = req.body.password;
    var email = req.body.email;
    var fullname = req.body.fullname;

    console.log("Nhận yêu cầu đăng ký:", user);

    // Chạy hàm xử lý logic
    await POST_register(user, pass, email, fullname, res);
});
  async function POST_register(user, pass, email, fullname, res) {
    if (!user || user.length < 3) {
        return UTILS.apiResult(0, "Tài khoản phải từ 3 ký tự", res);
    }
    if (!pass || pass.length < 6) {
        return UTILS.apiResult(0, "Mật khẩu phải từ 6 ký tự", res);
    }

    const check = await DB.getUser(user);
    if (check) {
        return UTILS.apiResult(0, "Tài khoản này đã tôn tại", res);
    }

    const result = await DB.Register(user, pass, email, fullname); 

    if (result) {
        UTILS.apiResult(1, "Đăng ký thành công!", res);
    } else {
        UTILS.apiResult(0, "Lỗi khi lưu vào cơ sở dữ liệu", res);
    }
}
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
    POST_userUpdate(token, info, res);
  });
}

module.exports = appRouter;

async function POST_login(user, pass, res) {
  if (user == undefined || !user || user.length < 3) {
    return UTILS.apiResult(-1, "Tài khoản không hợp lệ", res);
  }
  if (pass == undefined || !pass || pass.length < 6) {
    return UTILS.apiResult(-2, "Mật khẩu không hợp lệ", res);
  }

  const u = await DB.Authentication(user, pass);
  if (!u) {

    return UTILS.apiResult(-3, "Thông tin tài khoản không chính xác", res);
  }

  // 3. Tạo Token (Base64)
  var user_ = {};
  user_["u"] = user;
  user_["t"] = ~~(Date.now() / 1000);
  var token = Buffer.from(JSON.stringify(user_), 'utf8').toString('base64');

  var isUpdated = (u.fullname && u.fullname.trim() !== "") ? true : false;

  res.json({
    r: 1,
    m: token,
    updated: isUpdated
  });
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

function decodeToken(token) {
  var oResult = {};
  if (token == undefined || !token) {
    oResult['error'] = -1;
    oResult['message'] = "Yêu cầu đăng nhập trước khi sử dụng";
    return oResult;
  }

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

  var curSeconds = ~~(Date.now() / 1000);
  if (curSeconds - user_.t > (60 * 5)) {
    oResult['error'] = -3;
    oResult['message'] = "Hết thời gian đăng nhập, đăng nhập lại để lấy token";
    return oResult;
  }

  oResult['error'] = 1;
  oResult['message'] = user_;

  return oResult;
}