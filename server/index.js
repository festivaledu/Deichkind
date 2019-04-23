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

db.sequelize.sync();
//#endregion

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
