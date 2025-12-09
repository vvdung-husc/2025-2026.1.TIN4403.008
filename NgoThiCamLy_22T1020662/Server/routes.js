var appRouter = function (app) {

    app.get("/", function (req, res) {
        res.status(200).send("Welcome to  - TINK46");
    });

    app.get("/users", async function (req, res) {
    const u = await DB.getUsers();
    res.status(200).json(u);
    });

    app.post("/userinfo", function (req, res) {
        res.status(200).send("USERINFO API");
    });

    app.post("/login", async function (req, res) {
		var user = req.body.username;
    var pass = req.body.password;
    const u = await DB.getByUsername(user,pass);
    if (u){                         //dang nhap thanh cong
        res.status(200).json(u);
    }
    else{                           //dang nhap loi
        res.status(302).send("FAILED - LOGIN API [" + user + "/" + pass +"]");
    }
    
    });

    app.post("/register", function (req, res) {
        res.status(200).send("REGISTER API");
    });
    };
    module.exports = appRouter;
