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

        const user = await this.getByUsername('bachtt_k46','020543');
        console.log(user);

        callback();
    } catch (error) {
        console.error("Error connecting to MongoDB:", error);
    }
}

CDBLTDD.prototype.getUsers = async function(){
  const users = await this.db_.collection("Users").find().toArray();
  return users;   // trả về mảng users
}

CDBLTDD.prototype.getByUsername = async function(user, pass){
  const u = await this.db_.collection("Users").findOne({ username: user, password: pass});
  return u;   // trả về user hoặc null
}