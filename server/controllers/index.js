const fs = require("fs");
const path = require("path");
const basename = path.basename(__filename);
const express = require("express");
const router = express.Router();

router.use("/", require("../helpers/AuthHelper"));

fs
	.readdirSync(__dirname)
	.filter(file => {
		return (file.indexOf('.') !== 0) && (file !== basename) && ((file.slice(-3) === '.js' | fs.lstatSync(path.join(__dirname, file)).isDirectory()));
	})
	.forEach(file => {
		const _basename = path.basename(file, ".js");
		router.use(`/${_basename.toLowerCase()}`, require(path.join(__dirname, file)));
	});

module.exports = router;