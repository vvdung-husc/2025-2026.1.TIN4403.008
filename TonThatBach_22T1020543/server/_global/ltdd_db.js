const { MongoClient } = require('mongodb');
const uri = "mongodb://localhost:27017";

var DBLTDD = new CDBLTDD();

module.exports = DBLTDD;

function CDBLTDD() {
    this.client_ = new MongoClient(uri, {});
    this.db_ = null;
}

CDBLTDD.prototype.Init = async function (cb) {
    console.log('Connecting LTDD Database...');
    try {        
        await this.client_.connect();
        console.log("Connected to MongoDB!");

        this.db_ = this.client_.db("Ltdd");
        console.log('...MONGO Actived : [' + this.db_.databaseName + ']');

        if (cb) cb(null, true); // 🔥 DÒNG QUYẾT ĐỊNH
    } catch (error) {
        console.error("Error connecting to MongoDB:", error);
        if (cb) cb(error, null);
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

CDBLTDD.prototype.createUser = async function (user) {
  const o = await this.db_.collection("users").insertOne(user);
  return o;
}

CDBLTDD.prototype.createUser = async function (user) {
    const result = await this.db_
        .collection("users")
        .insertOne(user);
    return result;
}