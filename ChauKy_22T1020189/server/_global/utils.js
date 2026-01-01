module.exports = new CUtils();

function CUtils() {}

// Hàm trả về kết quả API theo định dạng chuẩn {r:code, m:msg}
// code > 0 : thành công, code <=0 : lỗi
// msg : thông điệp trả về
CUtils.prototype.apiResult = function(code, msg, res){
  var oResult = {};

  if (code == undefined || !code) oResult["r"] = 0;
  else oResult["r"] = parseInt(code);
  if (msg == undefined || !msg ) oResult["m"] = "WebService Restful API";
  else oResult["m"] = msg;

  var status = oResult["r"] > 0 ? 200 : 503;
  res.status(Status).json(oResult);
}