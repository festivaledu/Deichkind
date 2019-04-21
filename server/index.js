require("dotenv").config();
const express = require("express");
const morgan = require("morgan");
const cors = require("cors");
const bodyParser = require("body-parser");
const cookieParser = require("cookie-parser");
const fileupload = require("express-fileupload");

//#region HTTP Server
const httpServer = express();
const controllers = require("./controllers");

httpServer.use(morgan("\x1b[34m[INFO]\x1b[0m [:date[iso]] :remote-addr \":method :url HTTP/:http-version\" :status (:res[content-length] bytes)"));

httpServer.use(cors());
httpServer.use(bodyParser.json());
httpServer.use(cookieParser());
httpServer.use(fileupload());

httpServer.use("/", (req, res, next) => {
	req.models = db.models;
	return next();
});
//httpServer.use(controllers);

httpServer.listen(3000, () => {
	console.log('Server is up on port 3000');
});
//#endregion
