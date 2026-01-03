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

        this.db_ = this.client_.db("DSSV"); // Replace "mydatabase" with your database name
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

//Đăng ký user mới
CDBLTDD.prototype.registerUser = async function (userData) {
    // Kiểm tra username đã tồn tại chưa
    const exist = await this.db_.collection("users").findOne({ username: userData.username });
    if (exist) return { inserted: false, message: "Tài khoản đã tồn tại" };

    // Thêm user mới
    const result = await this.db_.collection("users").insertOne(userData);

    return { inserted: result.insertedId ? true : false };
}
