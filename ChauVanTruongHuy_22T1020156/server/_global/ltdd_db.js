const { MongoClient } = require('mongodb');
const uri = "mongodb://localhost:27017/ltdd";

var DBLTDD = new CDBLTDD();
module.exports = DBLTDD;

// =======================
// Khởi tạo đối tượng DB
// =======================
function CDBLTDD() {
    this.client_ = new MongoClient(uri, {});
    this.db_ = null;
}

// =======================
// Hàm kết nối MongoDB
// =======================
CDBLTDD.prototype.Init = async function () {
    console.log('Connecting LTDD Database...');
    try {
        await this.client_.connect();
        console.log("Connected to MongoDB!");

        // 👉 DÙNG DATABASE ltdd
        this.db_ = this.client_.db("ltdd");
        console.log('...MONGO Actived : [' + this.db_.databaseName + ']');

        return true;
    } catch (error) {
        console.error("Error connecting to MongoDB:", error);
        return false;
    }
};

// =======================
// Lấy thông tin 1 user
// =======================
CDBLTDD.prototype.getUser = async function (user) {
    return await this.db_
        .collection("users")
        .findOne({ username: user });
};

// =======================
// Lấy danh sách users
// =======================
CDBLTDD.prototype.getUsers = async function () {
    return await this.db_
        .collection("users")
        .find({}, { projection: { _id: 0 } })
        .toArray();
};

// =======================
// Xác thực đăng nhập
// =======================
CDBLTDD.prototype.Authentication = async function (user, pass) {
    return await this.db_
        .collection("users")
        .findOne({ username: user, password: pass });
};

// =======================
// Cập nhật user
// =======================
CDBLTDD.prototype.modifyUser = async function (user, modify) {
    return await this.db_
        .collection("users")
        .updateOne(
            { username: user },
            { $set: modify }
        );
};

// =======================
// Đăng ký user mới
// =======================
CDBLTDD.prototype.Register = async function (user, pass, email, fullname) {
    try {
        const col = this.db_.collection("users");

        // Kiểm tra trùng username
        const existingUser = await col.findOne({ username: user });
        if (existingUser) {
            console.log("Register failed: username exists");
            return false;
        }

        const result = await col.insertOne({
            username: user,
            password: pass,
            email: email,
            fullname: fullname,
            created_at: new Date()
        });

        return result.acknowledged;
    } catch (error) {
        console.error("Register error:", error);
        return false;
    }
};
