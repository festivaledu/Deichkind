const express = require("express");
const router = express.Router();
const httpStatus = require("http-status");

const ErrorHandler = require("../helpers/ErrorHandler");

/**
 * GET /account/me
 */
router.get("/me", (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	return res.status(httpStatus.OK).send({
		id: account.id,
		username: account.username,
		email: account.email,
		role: account.role,
		lastLogin: account.lastLogin,
		createdAt: account.createdAt
	});
});

/**
 * PUT /me
 */
router.put("/me", (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	account.update(Object.assign(req.body, {
		id: account.id,
		role: account.role,
		lastLogin: account.lastLogin,
		createdAt: account.createdAt,
		updatedAt: account.updatedAt
	})).then(accountObj => {
		return res.status(httpStatus.OK).send({
			id: account.id,
			username: account.username,
			email: account.email,
			role: account.role,
			lastLogin: account.lastLogin,
			createdAt: account.createdAt
		});
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * DELETE /account/me
 */
router.delete("/me", (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	account.destroy().then(() => {
		return res.status(httpStatus.OK).send({
			name: "OK",
			code: httpStatus.OK
		});
	}).catch(error => ErrorHandler(req, res, error));
});



/**
 * GET /account/:userId
 */
router.get("/:userId", (req, res) => {
	const { Account } = req.models;
	
	Account.findOne({
		where: {
			id: req.params.userId
		},
		attributes: ["id", "username", "role", "createdAt"]
	}).then(accountObj => {
		if (!accountObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: "User not found"
		});
		
		return res.status(httpStatus.OK).send(accountObj);
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * GET /account/:userId/avatar
 */
router.get("/:userId/avatar", (req, res) => {
	const { Account } = req.models;
	
	Account.findOne({
		where: {
			id: req.params.userId
		},
		attributes: ["profileImage"]
	}).then(accountObj => {
		if (!accountObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: "User not found"
		});
		
		
		if (!accountObj.profileImage) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: "User does not have any profile image"
		});
		
		return res.status(httpStatus.OK).send(accountObj);
	}).catch(error => ErrorHandler(req, res, error));
});

module.exports = router;