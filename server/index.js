require("dotenv").config();
const express = require("express");
const morgan = require("morgan");
const cors = require("cors");
const bodyParser = require("body-parser");
const cookieParser = require("cookie-parser");
const fileupload = require("express-fileupload");

//#region Database Setup
const fs = require('fs');
const path = require('path');
const Sequelize = require('sequelize');
const models = require("./models");
const env = process.env.NODE_ENV || 'development';
const config = require(__dirname + '/config/config.json')[env];
const db = {};

let sequelize;
if (config.use_env_variable) {
	sequelize = new Sequelize(process.env[config.use_env_variable], config);
} else {
	sequelize = new Sequelize(config.database, config.username, config.password, config);
}

sequelize.query = function() {
	return Sequelize.prototype.query.apply(this, arguments).catch(err => {
		console.log(err.message);
	});
}

db.models = {};
Object.keys(models).forEach(model => {
	// sequelize['import'](model);
	db.models[model] = models[model](sequelize, Sequelize);
});

Object.keys(db.models).forEach(modelName => {
	if (db.models[modelName].associate) {
		db.models[modelName].associate(db.models);
	}
});

db.sequelize = sequelize;
db.Sequelize = Sequelize;

db.sequelize.sync().then(() => {
	db.models.Token.destroy({
		truncate: true
	});
});
//#endregion

//#region HTTP Server
const httpServer = express();
const serveStatic = require("serve-static");
const controllers = require("./controllers");

httpServer.use(morgan("\x1b[34m[INFO]\x1b[0m [:date[iso]] :remote-addr \":method :url HTTP/:http-version\" :status (:res[content-length] bytes)"));

httpServer.use(cors());
httpServer.use(bodyParser.json());
httpServer.use(cookieParser());
httpServer.use(fileupload());

httpServer.use("/", serveStatic(path.join(__dirname, "dist")));

httpServer.use("/api", (req, res, next) => {
	req.models = db.models;
	return next();
});
httpServer.use("/api", controllers);

httpServer.listen(62224, () => {
	console.log('Server is up on port 62224');
});
//#endregion
