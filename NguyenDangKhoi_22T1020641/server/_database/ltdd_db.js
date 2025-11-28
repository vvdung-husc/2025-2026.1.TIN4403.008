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

        this.db_ = this.client_.db("ltdd"); // Replace "mydatabase" with your database name
        //console.log(this.db_);
        console.log('...MONGO Actived : [' + this.db_.databaseName + ']');

        const user = await this.getByUsername('khoind_k46','020641');
        console.log(user);

        callback();
    } catch (error) {
        console.error("Error connecting to MongoDB:", error);
    }
}

CDBLTDD.prototype.getUsers = async function(){
  const users = await this.db_.collection("users").find().toArray();
  return users;   // trả về mảng users
}

CDBLTDD.prototype.getByUsername = async function(user, pass){
  const u = await this.db_.collection("users").findOne({ username: user, password: pass});
  return u;   // trả về user hoặc null
}

CDBLTDD.prototype.createUser = async function(username, password, fullname) {
    const existingUser = await this.db_.collection("users").findOne({ username: username });
    if (existingUser) {
        return null;
    }

    const result = await this.db_.collection("users").insertOne({
        username: username,
        password: password,
        fullname: fullname
    });

    if (result.insertedId) {
        return { _id: result.insertedId, username: username, password: password, fullname: fullname };
    } else {
        return null;
    }
}
