const express = require("express");
const router = express.Router();
const Sequelize = require("sequelize");
const httpStatus = require("http-status");

const ErrorHandler = require("../helpers/ErrorHandler");
const UserRole = require("../helpers/UserRole");

const jwt = require("jsonwebtoken");
const { JWT_SECRET } = process.env;
const bcrypt = require("bcryptjs");

/**
 * POST /register
 */
router.post("/register", (req, res) => {
	const { Account } = req.models;
	
	if (!req.body.username || !req.body.password) return res.status(httpStatus.BAD_REQUEST).send({
		name: httpStatus[httpStatus.BAD_REQUEST],
		code: httpStatus.BAD_REQUEST,
		message: "No username, email or password provided"
	});
	
	Account.findOne({
		where: {
			[Sequelize.Op.or]: {
				username: req.body.username,
				email: req.body.email
			}
		}
	}).then(accountObj => {
		if (accountObj) return res.status(httpStatus.CONFLICT).send({
			name: httpStatus[httpStatus.CONFLICT],
			code: httpStatus.CONFLICT,
			message: "Username or E-Mail address already in use"
		});
		
		const salt = bcrypt.genSaltSync(10);
		const hashedPassword = bcrypt.hashSync(req.body.password, salt);
		
		Account.create({
			id: String.prototype.concat(req.body.username, req.body.email, hashedPassword, new Date().getTime()),
			username: req.body.username,
			email: req.body.email,
			password: hashedPassword,
			salt: salt,
			role: UserRole.USER,
			lastLogin: new Date().toUTCString
		}).then(accountObj => {
			let token = jwt.sign({
				userId: accountObj.id,
				email: accountObj.email,
				role: accountObj.role
			}, JWT_SECRET, {
				expiresIn: 7200
			});
			
			return res
				.status(httpStatus.OK)
				.cookie("authToken", token, {
					expiresIn: new Date().getTime() + (7200 * 1000),
					httpOnly: true
				})
				.json({
					auth: true,
					token: token,
					role: accountObj.role
				});
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * POST /login
 */
router.post("/login", (req, res) => {
	const { Account } = req.models;
	
	if (!req.body.username || !req.body.password) return res.status(httpStatus.BAD_REQUEST).send({
		name: httpStatus[httpStatus.BAD_REQUEST],
		code: httpStatus.BAD_REQUEST,
		message: "No username, email or password provided"
	});
	
	Account.findOne({
		where: {
			[Sequelize.Op.or]: {
				username: req.body.username,
				email: req.body.username
			}
		}
	}).then(accountObj => {
		if (!accountObj || (!accountObj.usernameValid(req.body.username) && !accountObj.emailValid(req.body.username))) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: "User not found"
		});
		
		if (!accountObj.passwordValid(req.body.password)) return res.status(httpStatus.UNAUTHORIZED).send({
			name: httpStatus[httpStatus.UNAUTHORIZED],
			code: httpStatus.UNAUTHORIZED,
			message: "Incorrect password"
		});
		
		accountObj.update({
			lastLogin: new Date().toUTCString()
		}).catch(error => ErrorHandler(req, res, error));
		
		let token = jwt.sign({
			userId: accountObj.id,
			email: accountObj.email,
			role: accountObj.role
		}, JWT_SECRET, {
			expiresIn: 7200
		});
		
		return res
			.status(httpStatus.OK)
			.cookie("authToken", token, {
				expiresIn: new Date().getTime() + (7200 * 1000),
				httpOnly: true
			})
			.json({
				auth: true,
				token: token,
				role: accountObj.role
			});
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * GET /verify
 */
router.get("/verify", (req, res) => {
	if (!req.account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid or no authorization token provided"
	});
	
	let token = jwt.sign({
		userId: req.account.id,
		email: req.account.email,
		role: req.account.role
	}, JWT_SECRET, {
		expiresIn: 7200
	});
	
	return res
		.status(httpStatus.OK)
		.cookie("authToken", token, {
			expiresIn: new Date().getTime() + (7200 * 1000),
			httpOnly: true
		})
		.json({
			auth: true,
			token: token,
			role: req.account.role
		});
});

module.exports = router;