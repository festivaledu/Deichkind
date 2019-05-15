const express = require("express");
const router = express.Router();
const httpStatus = require("http-status");
const xml2js = require("xml2js");

const ErrorHandler = require("../helpers/ErrorHandler");

/**
 * GET /dykes
 */
router.get("/", (req, res) => {
	const { Dyke, Report, Comment } = req.models;
	
	Dyke.findAll({
		// include: [{
		// 	model: Report,
		// 	as: "reports",
		// 	where: { deleted: false },
		// 	separate: true,
		// 	attributes: { exclude: ["kmlFile"] },
		// 	order: [["createdAt", "DESC"]],
		// 	include: [{
		// 		model: Comment,
		// 		as: "comments",
		// 		where: { deleted: false },
		// 		separate: true,
		// 		order: [["createdAt", "ASC"]],
		// 	}]
		// }],
		attributes: { exclude: ["kmlFile"] }
	}).then(dykeList => {
		// if (!dykeList || !dykeList.length) return res.status(httpStatus.NOT_FOUND).send({
		// 	name: httpStatus[httpStatus.NOT_FOUND],
		// 	code: httpStatus.NOT_FOUND,
		// 	message: "No dykes found"
		// });
		
		return res.status(httpStatus.OK).send(dykeList);
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * POST /dykes/new
 */
router.post("/new", async (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	const { Dyke } = req.models;
	let dykeData = req.body;
	
	let dykeObj = await Dyke.findOne({
		where: { name: dykeData.name }
	});
	if (dykeObj) return res.status(httpStatus.CONFLICT).send({
		name: "Conflict",
		code: httpStatus.CONFLICT,
		message: `Dyke with name ${dykeData.name} already exists`
	});
	
	if (!req.files || !req.files.file) return res.status(httpStatus.BAD_REQUEST).send({
		name: httpStatus[httpStatus.BAD_REQUEST],
		code: httpStatus.BAD_REQUEST,
		message: "No dyke file specified"
	});
	let dykeFile = req.files.file;
	
	const xmlParser = new xml2js.Parser();
	let parsed = await new Promise(function(resolve, reject) {
		try {
			xmlParser.parseString(dykeFile.data, (err, result) => {
				if (err) {
					reject(false);
				} else {
					resolve(result);
				}
			});
		} catch (err) {
			reject(false);
		}
	}).catch(some => {
		
	});

	if (!parsed) return res.status(httpStatus.BAD_REQUEST).send({
		name: httpStatus[httpStatus.BAD_REQUEST],
		code: httpStatus.BAD_REQUEST,
		message: "Dyke file does not contain valid XML"
	});
	
	Dyke.create(Object.assign(dykeData, {
		id: String.prototype.concat(dykeData.name, new Date().getTime()),
		kmlFile: dykeFile.data,
		accountId: account.id,
	})).then(dykeObj => {
		delete dykeObj.dataValues.kmlFile;
		
		return res.status(httpStatus.OK).send(dykeObj);
	}).catch(error => ErrorHandler(req, res, error));
});



/**
 * GET /dykes/:dykeId
 */
router.get("/:dykeId", (req, res) => {
	const { Dyke, Report, Comment } = req.models;
	
	Dyke.findOne({
		where: { id: req.params.dykeId },
		include: [{
			model: Report,
			as: "reports",
			where: { deleted: false },
			separate: true,
			attributes: { exclude: ["kmlFile"] },
			order: [["createdAt", "DESC"]],
			include: [{
				model: Comment,
				as: "comments",
				where: { deleted: false },
				separate: true,
				order: [["createdAt", "ASC"]],
			}]
		}],
		attributes: { exclude: ["kmlFile"] }
	}).then(dykeObj => {
		if (!dykeObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `No dyke with identifier ${req.params.dykeId} found`
		});
		
		return res.status(httpStatus.OK).send(dykeObj);
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * PUT /dykes/:dykeId
 */
router.put("/:dykeId", async (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	const { Dyke } = req.models;
	let dykeData = req.body;
	let dykeFile = req.files && req.files.file ? req.files.file : undefined;
	
	if (dykeFile) {
		const xmlParser = new xml2js.Parser();
		let parsed = await new Promise(function(resolve, reject) {
			try {
				xmlParser.parseString(dykeFile.data, (err, result) => {
					if (err) {
						reject(false);
					} else {
						resolve(result);
					}
				});
			} catch (err) {
				reject(false);
			}
		}).catch(some => {
			
		});

		if (!parsed) return res.status(httpStatus.BAD_REQUEST).send({
			name: httpStatus[httpStatus.BAD_REQUEST],
			code: httpStatus.BAD_REQUEST,
			message: "Dyke file does not contain valid XML"
		});
	}
	
	Dyke.findOne({
		where: { id: req.params.dykeId }
	}).then(dykeObj => {
		if (!dykeObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `No dyke with identifier ${req.params.dykeId} found`
		});
		
		if (dykeObj.accountId != account.id) return res.status(httpStatus.UNAUTHORIZED).send({
			name: httpStatus[httpStatus.UNAUTHORIZED],
			code: httpStatus.UNAUTHORIZED,
			message: "You are not allowed to perform this action"
		});
		
		dykeObj.update(Object.assign(dykeData, {
			id: dykeObj.id,
			kmlFile: dykeFile ? dykeFile : undefined,
			accountId: dykeObj.accountId,
			createdAt: dykeObj.createdAt,
			updatedAt: dykeObj.updatedAt
		})).then(dykeObj => {
			delete dykeObj.dataValues.kmlFile;
			
			return res.status(httpStatus.OK).send(dykeObj);
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * DELETE /dykes/:dykeId
 */
router.delete("/:dykeId", (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	const { Dyke } = req.models;
	
	Dyke.findOne({
		where: { id: req.params.dykeId }
	}).then(dykeObj => {
		if (!dykeObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `No dyke with identifier ${req.params.dykeId} found`
		});
		
		if (dykeObj.accountId != account.id) return res.status(httpStatus.UNAUTHORIZED).send({
			name: httpStatus[httpStatus.UNAUTHORIZED],
			code: httpStatus.UNAUTHORIZED,
			message: "You are not allowed to perform this action"
		});
		
		dykeObj.destroy().then(() => {
			return res.status(httpStatus.OK).send({
				name: "OK",
				code: httpStatus.OK
			});
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * GET /dykes/:dykeId/file
 */
router.get("/:dykeId/file", (req, res) => {
	const { Dyke } = req.models;
	
	Dyke.findOne({
		where: { id: req.params.dykeId },
		attributes: ["kmlFile"]
	}).then(dykeObj => {
		if (!dykeObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `No dyke with identifier ${req.params.dykeId} found`
		});
		
		res.write(dykeObj.kmlFile, "binary");
		return res.end(undefined, "binary");
	}).catch(error => ErrorHandler(req, res, error));
});



/**
 * GET /dykes/:dykeId/reports
 */
router.get("/:dykeId/reports", (req, res) => {
	const { Dyke, Report, Comment } = req.models;
	
	Dyke.findOne({
		where: { id: req.params.dykeId },
	}).then(dykeObj => {
		if (!dykeObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `No dyke with identifier ${req.params.dykeId} found`
		});
		
		return Report.findAll({
			where: {
				dykeId: dykeObj.id,
				deleted: false
			},
			order: [["createdAt", "DESC"]],
			include: [{
				model: Comment,
				as: "comments",
				where: { deleted: false },
				separate: true,
				order: [["createdAt", "ASC"]],
			}]
		});
	}).then(reportList => {
		// if (!reportList || !reportList.length) return res.status(httpStatus.NOT_FOUND).send({
		// 	name: httpStatus[httpStatus.NOT_FOUND],
		// 	code: httpStatus.NOT_FOUND,
		// 	message: "Dyke does not have any reports"
		// });
		
		return res.status(httpStatus.OK).send(reportList);
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * POST /dykes/:dykeId/reports/new
 */
router.post("/:dykeId/reports/new", async (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	const { Dyke, Report, Comment } = req.models;
	let reportData = req.body;
	
	let dykeObj = await Dyke.findOne({
		where: {
			id: req.params.dykeId,
		}
	});
	if (!dykeObj) return res.status(httpStatus.NOT_FOUND).send({
		name: httpStatus[httpStatus.NOT_FOUND],
		code: httpStatus.NOT_FOUND,
		message: "Dyke not found"
	});
	
	Report.create(Object.assign(reportData, {
		id: String.prototype.concat(dykeObj.id, account.id, new Date().getTime()),
		dykeId: dykeObj.id,
		accountId: account.id,
		resolved: false,
		deleted: false
	})).then(reportObj => {
		Comment.create({
			id: String.prototype.concat(dykeObj.id, reportObj.id, account.id, new Date().getTime()),
			dykeId: dykeObj.id,
			reportId: reportObj.id,
			message: reportData.message,
			accountId: account.id
		}).then(commentObj => {
			return res.status(httpStatus.OK).send({
				report: reportObj,
				comment: commentObj
			});
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

module.exports = router;