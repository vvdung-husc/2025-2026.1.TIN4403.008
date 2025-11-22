var appRouter = function (app) {

    app.get("/", function (req, res) {
        res.status(200).send("Welcome to  - TINK46");
    });

    app.get("/users", function (req, res) {
        res.status(200).send("RESTFUL API (/users)- TINK46");
    });

    app.post("/userinfo", function (req, res) {
        res.status(200).send("USERINFO API");
    });

    app.post("/login", function (req, res) {
        var user = req.body.username;
        var pass = req.body.password;

        if (user == "vvdung" && pass == '111222')
        res.status(200).send("ĐĂNG NHẬP THÀNH CÔNG [" + user + "/" + pass + "]");
        else
        res.status(200).send("FAILED - LOGIN API [" + user + "/" + pass + "]");
    });

    app.post("/register", function (req, res) {
        res.status(200).send("REGISTER API");
    });
    };
    module.exports = appRouter;
