const express = require("express");
const router = express.Router();
const httpStatus = require("http-status");

const ErrorHandler = require("../helpers/ErrorHandler");

/**
 * GET /reports
 */
router.get("/", (req, res) => {
	const { Report, Comment } = req.models;
	
	Report.findAll({
		where: { deleted: false },
		order: [["createdAt", "DESC"]],
		include: [{
			model: Comment,
			as: "comments",
			where: { deleted: false },
			separate: true,
			order: [["createdAt", "ASC"]],
		}]
	}).then(reportList => {
		if (!reportList || !reportList.length) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: "There are no reports to show"
		});
		
		return res.status(httpStatus.OK).send(reportList);
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * GET /reports/:reportId
 */
router.get("/:reportId", (req, res) => {
	const { Report, Comment } = req.models;
	
	Report.findOne({
		where: {
			id: req.params.reportId,
			deleted: false
		},
		order: [["createdAt", "DESC"]],
		include: [{
			model: Comment,
			as: "comments",
			where: { deleted: false },
			separate: true,
			order: [["createdAt", "ASC"]],
		}],
	}).then(reportObj => {
		if (!reportObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `No report with identifier ${req.params.reportId} found`
		});
		
		return res.status(httpStatus.OK).send(reportObj);
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * DELETE /reports/:reportId
 */
router.delete("/:reportId", (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	const { Report } = req.models;
	
	Report.findOne({
		where: {
			id: req.params.reportId,
			deleted: false
		}
	}).then(reportObj => {
		if (!reportObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `No report with identifier ${req.params.reportId} found`
		});
		
		if (reportObj.accountId != account.id) return res.status(httpStatus.UNAUTHORIZED).send({
			name: httpStatus[httpStatus.UNAUTHORIZED],
			code: httpStatus.UNAUTHORIZED,
			message: "You are not allowed to perform this action"
		});
		
		reportObj.update({
			deleted: true
		}).then(() => {
			return res.status(httpStatus.OK).send({
				name: httpStatus[httpStatus.OK],
				code: httpStatus.OK
			})
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * GET /reports/:reportId/comments
 */
router.get("/:reportId/comments", (req, res) => {
	const { Report, Comment } = req.models;
	Report.findOne({
		where: {
			id: req.params.reportId,
			deleted: false
		}
	}).then(reportObj => {
		if (!reportObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `No report with identifier ${req.params.reportId} found`
		});
		
		return Comment.findAll({
			where: {
				reportId: reportObj.id,
				deleted: false
			},
			order: [["createdAt", "ASC"]],
		})
	}).then(commentList => {
		if (!commentList || !commentList.length) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: "Report does not have any comments"
		});
		
		return res.status(httpStatus.OK).send(commentList);
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * POST /reports/:reportId/comment
 */
router.post("/:reportId/comment", (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	if (!req.body.message) return res.status(httpStatus.BAD_REQUEST).send({
		name: httpStatus[httpStatus.BAD_REQUEST],
		code: httpStatus.BAD_REQUEST,
		message: "No comment message supplied"
	});
	
	const { Report, Comment } = req.models;
	Report.findOne({
		where: {
			id: req.params.reportId,
			deleted: false
		}
	}).then(reportObj => {
		if (!reportObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `No report with identifier ${req.params.reportId} found`
		});
		
		Comment.create({
			id: String.prototype.concat(reportObj.id, account.id, new Date().getTime()),
			dykeId: reportObj.dykeId,
			reportId: reportObj.id,
			message: req.body.message,
			accountId: account.id
		}).then(commentObj => {
			return res.status(httpStatus.OK).send(commentObj);
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * GET /reports/:reportId/comments/:commentId
 */
router.get("/:reportId/comments/:commentId", (req, res) => {
	const { Report, Comment } = req.models;
	Report.findOne({
		where: {
			id: req.params.reportId,
			deleted: false
		}
	}).then(reportObj => {
		if (!reportObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `No report with identifier ${req.params.reportId} found`
		});
		
		return Comment.findOne({
			where: {
				id: req.params.commentId,
				reportId: reportObj.id,
				deleted: false
			},
			order: [["createdAt", "ASC"]],
		});
	}).then(commentObj => {
		if (!commentObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `Report does not have any comment with identifier ${req.params.commentId}`
		});
		
		return res.status(httpStatus.OK).send(commentObj);
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * DELETE /reports/:reportId/comments/:commentId
 */
router.delete("/:reportId/comments/:commentId", (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	const { Report } = req.models;
	
	Report.findOne({
		where: {
			id: req.params.reportId,
			deleted: false
		}
	}).then(reportObj => {
		if (!reportObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `No report with identifier ${req.params.reportId} found`
		});
		
		return Comment.findOne({
			where: {
				id: req.params.commentId,
				reportId: reportObj.id,
				deleted: false
			},
			order: [["createdAt", "ASC"]],
		});
	}).then(commentObj => {
		if (!commentObj) return res.status(httpStatus.NOT_FOUND).send({
			name: httpStatus[httpStatus.NOT_FOUND],
			code: httpStatus.NOT_FOUND,
			message: `Report does not have any comment with identifier ${req.params.commentId}`
		});
		
		if (commentObj.accountId != account.id) return res.status(httpStatus.UNAUTHORIZED).send({
			name: httpStatus[httpStatus.UNAUTHORIZED],
			code: httpStatus.UNAUTHORIZED,
			message: "You are not allowed to perform this action"
		});
		
		commentObj.update({
			deleted: true
		}).then(() => {
			return res.status(httpStatus.OK).send({
				name: httpStatus[httpStatus.OK],
				code: httpStatus.OK
			})
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

module.exports = router;