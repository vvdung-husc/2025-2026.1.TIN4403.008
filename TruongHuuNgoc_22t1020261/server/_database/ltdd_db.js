const { MongoClient } = require('mongodb');
const uri = "mongodb://localhost:27017";

var DBLTDD = new CDBLTDD();

module.exports = DBLTDD;

function CDBLTDD() {
  this.client_ = new MongoClient(uri, {});
  this.db_ = null;
}

CDBLTDD.prototype.Init = async function (callback) {
  console.log('Connecting LTDD Database...');

  try {

    await this.client_.connect();
    console.log("Connected to MongoDB!");
    //console.log(this.client_);

    this.db_ = this.client_.db("DSSV"); // Replace "mydatabase" with your database name
    //console.log(this.db_);
    console.log('...MONGO Actived : [' + this.db_.databaseName + ']');

    const user = await this.getByUsername('bachtt_k46', '020543');
    console.log(user);

    callback();
  } catch (error) {
    console.error("Error connecting to MongoDB:", error);
  }
}

CDBLTDD.prototype.getUsers = async function () {
  const users = await this.db_.collection("Users").find().toArray();
  return users;   // trả về mảng users
}

CDBLTDD.prototype.getByUsername = async function (user, pass) {
  const u = await this.db_.collection("Users").findOne({ username: user, password: pass });
  return u;   // trả về user hoặc null
}

CDBLTDD.prototype.register = async function (user, pass, email, fullname) {
  const u = await this.db_.collection("Users").insertOne({ username: user, password: pass, email: email, fullname: fullname });
  return u;   // trả về user hoặc null
}

CDBLTDD.prototype.update = async function (user, pass, email, fullname) {
    // Định nghĩa các trường cần cập nhật
    const updateFields = {
        password: pass,
        email: email,
        fullname: fullname
    };

    // Sử dụng $set để chỉ cập nhật các trường được chỉ định, giữ lại các trường khác
    const result = await this.db_.collection("Users").updateOne(
        { username: user }, // Tiêu chí lọc: Tìm theo username
        { $set: updateFields } // Toán tử cập nhật $set
    );

    // Trả về kết quả cập nhật của MongoDB (bao gồm { matchedCount: 1, modifiedCount: 1, ... })
    return result;
}