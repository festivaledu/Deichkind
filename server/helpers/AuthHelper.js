const Sequelize = require("sequelize");
const jwt = require("jsonwebtoken");

const { JWT_SECRET } = process.env;

module.exports = (req, res, next) => {
	const { Account } = req.models;
	
	const authHeader = req.headers["authorization"];
	let authToken = authHeader ? authHeader.split(" ")[1] : undefined;
	
	if ((!authToken || typeof authToken === "undefined") && req.cookies["authToken"]) {
		authToken = req.cookies["authToken"];
	}
	
	if (!authToken) return next();
	
	
	jwt.verify(authToken, JWT_SECRET, (error, decoded) => {
		if (error) return next();
		
		Account.findOne({
			where: {
				id: String(decoded.userId)
			},
			attributes: ["id", "username", "email", "role", [Sequelize.fn("COUNT", Sequelize.col("profileImage")), "profileImage"], "lastLogin", "createdAt"]
		}).then(accountObj => {
			if (accountObj) {
				req.account = accountObj;
			}
			
			return next();
		});
	});
}