const { MongoClient } = require('mongodb');
const uri = "mongodb://localhost:27017";

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

        this.db_ = this.client_.db("ltdd"); // Replace "mydatabase" with your database name
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
  const u = await this.db_.collection("users").findOne({username:user});
  return u;   // trả về user hoặc null
}

CDBLTDD.prototype.getUsers = async function(){//không trả về _id
  const users = await this.db_.collection("users").find({}, { projection: { _id: 0 } }).toArray();
  return users;   // trả về mảng users
}

CDBLTDD.prototype.Authentication = async function(user, pass){
  const u = await this.db_.collection("users").findOne({ username: user, password: pass});
  return u;   // trả về user hoặc null
}

CDBLTDD.prototype.modifyUser = async function (user, modify){
    //console.log(modify);
    const oDoc = await this.db_.collection("users").updateOne({username:user},{$set:modify});
    //console.log(oDoc);
    return oDoc;
} 

CDBLTDD.prototype.addUser = async function (newUser) {
    try {
        // 1. Kiểm tra xem tài khoản đã tồn tại chưa
        const existingUser = await this.db_.collection("users").findOne({ username: newUser.username });
        if (existingUser) {
            console.log("Tài khoản đã tồn tại:", newUser.username);
            return null; // Trả về null nếu trùng tên đăng nhập
        }

        // 2. Thêm người dùng mới vào collection "users"
        const result = await this.db_.collection("users").insertOne(newUser);
        return result; // Trả về kết quả thêm thành công
    } catch (error) {
        console.error("Lỗi khi thêm User vào MongoDB:", error);
        return null;
    }
}
