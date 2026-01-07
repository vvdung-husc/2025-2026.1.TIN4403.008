const { MongoClient } = require('mongodb');
const uri = "mongodb://localhost:27017/db";

var DBLTDD = new CDBLTDD();
module.exports = DBLTDD;

function CDBLTDD() {
    this.client_ = new MongoClient(uri, {});
    this.db_ = null;
}

CDBLTDD.prototype.Init = async function () {
    console.log('Connecting DB Database...');
    try {
        await this.client_.connect();
        console.log("Connected to MongoDB!");

        this.db_ = this.client_.db("db");
        console.log('...MONGO Actived : [' + this.db_.databaseName + ']');

        return true;
    } catch (error) {
        console.error("Error connecting to MongoDB:", error);
        return false;
    }
};

CDBLTDD.prototype.getUser = async function (user) {
    return await this.db_
        .collection("users")
        .findOne({ username: user });
};

CDBLTDD.prototype.getUsers = async function () {
    return await this.db_
        .collection("users")
        .find({}, { projection: { _id: 0 } })
        .toArray();
};

CDBLTDD.prototype.Authentication = async function (user, pass) {
    return await this.db_
        .collection("users")
        .findOne({ username: user, password: pass });
};

CDBLTDD.prototype.modifyUser = async function (user, modify) {
    return await this.db_
        .collection("users")
        .updateOne(
            { username: user },
            { $set: modify }
        );
};

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
        });

        return result.acknowledged;
    } catch (error) {
        console.error("Register error:", error);
        return false;
    }
};