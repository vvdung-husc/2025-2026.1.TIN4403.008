const { MongoClient } = require('mongodb');
const uri = "mongodb://localhost:27017/ltdd";

var DBLTDD = new CDBLTDD();

module.exports = DBLTDD;

function CDBLTDD() {
    this.client_ = new MongoClient(uri, {});
    this.db_ = null;
}

CDBLTDD.prototype.Init = async function () {
    console.log('Connecting LTDD Database...');
    try {        
        await this.client_.connect();
        console.log("Connected to MongoDB!");
        //console.log(this.client_);

        this.db_ = this.client_.db("sinhvien"); // Replace "mydatabase" with your database name
        //console.log(this.db_);
        console.log('...MONGO Actived : [' + this.db_.databaseName + ']');

        // const user = await this.getByUsername('bachtt_k46','020543');
        // console.log(user);

        return true;
    } catch (error) {
        console.error("Error connecting to MongoDB:", error);
        return false;
    }
}

CDBLTDD.prototype.getUser = async function(user){
  const u = await this.db_.collection("svdb").findOne({username:user});
  return u;   // trả về user hoặc null
}
 
CDBLTDD.prototype.getUsers = async function(){ // lấy tất cả danh sách, không trả về _id
  const users = await this.db_.collection("svdb").find({}, { projection: { _id: 0 } }).toArray();
  return users;   // trả về mảng users
}

CDBLTDD.prototype.Authentication = async function(user, pass){
  const u = await this.db_.collection("svdb").findOne({ username: user, password: pass});
  return u;   // trả về user hoặc null
}

CDBLTDD.prototype.modifyUser = async function (user, modify){
    //console.log(modify);
    const oDoc = await this.db_.collection("svdb").updateOne({username:user},{$set:modify}); // cập nhật thông tin người dùng
    //console.log(oDoc);
    return oDoc;
} 
// hàm đăng ký người dùng
// --- SỬA TRONG FILE db_js.js ---

CDBLTDD.prototype.Register = async function (user, pass, email, fullname) {
    try {
        // 1. Kiểm tra xem username đã tồn tại chưa (Rất quan trọng)
        const existingUser = await this.db_.collection("svdb").findOne({ username: user });
        if (existingUser) {
            console.log("--> Đăng ký thất bại: Tài khoản đã tồn tại");
            return false; 
        }

        // 2. Nếu chưa có thì mới chèn vào MongoDB
        const result = await this.db_.collection("svdb").insertOne({
            username: user,
            password: pass,
            email: email,
            fullname: fullname,  
            created_at: new Date()
        });
        
        // Trả về true nếu Insert thành công
        return result.acknowledged; 
    } catch (error) {
        console.error("Lỗi Register DB:", error);
        return false;
    }
}