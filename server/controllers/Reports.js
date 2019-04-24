const express = require("express");
const router = express.Router();
const httpStatus = require("http-status");

const ErrorHandler = require("../helpers/ErrorHandler");

let asyncForEach = async (array, callback) => {
	for (let index = 0; index < array.length; index++) {
		await callback(array[index], index)
	}
}

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
			order: [["createdAt", "DESC"]],
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
			order: [["createdAt", "DESC"]],
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
			order: [["createdAt", "DESC"]],
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
			id: String.prototype.concat(reportObj.dykeId, reportObj.id, account.id, new Date().getTime()),
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
			}
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
			}
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

/**
 * GET /reports/:reportId/photos
 */
router.get("/:reportId/photos", (req, res) => {
	const { Report, ReportPhoto } = req.models;
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
		
		ReportPhoto.findAll({
			where: {
				reportId: reportObj.id,
				deleted: false
			},
			order: [["createdAt", "ASC"]],
			attributes: { exclude: ["photoData"] },
		}).then(photoList => {
			if (!photoList || !photoList.length) return res.status(httpStatus.NOT_FOUND).send({
				name: httpStatus[httpStatus.NOT_FOUND],
				code: httpStatus.NOT_FOUND,
				message: "Report does not have any photos"
			});
			
			return res.status(httpStatus.OK).send(photoList);
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * POST /reports/:reportId/photos
 */
router.post("/:reportId/photos", (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	if (!req.files || !Object.values(req.files).length) return res.status(httpStatus.BAD_REQUEST).send({
		name: httpStatus[httpStatus.BAD_REQUEST],
		code: httpStatus.BAD_REQUEST,
		message: "No photos supplied"
	});
	let photoFiles = Object.values(req.files);
	
	const { Report, ReportPhoto } = req.models;
	Report.findOne({
		where: {
			id: req.params.reportId,
			deleted: false
		}
	}).then(async reportObj => {
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
		
		await asyncForEach(photoFiles, async (photoFile, index) => {
			await ReportPhoto.create({
				id: String.prototype.concat(reportObj.dykeId, reportObj.id, account.id, photoFile.md5, new Date().getTime()),
				dykeId: reportObj.dykeId,
				reportId: reportObj.id,
				photoData: photoFile.data,
				photoMime: photoFile.mimetype,
				accountId: account.id
			}).catch(error => ErrorHandler(req, res, error));
		});
		
		return res.status(httpStatus.OK).send({
			name: "OK",
			code: httpStatus.OK
		});
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * DELETE /reports/:reportId/photos
 */
router.delete("/:reportId/photos", (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	const { Report, ReportPhoto } = req.models;
	Report.findOne({
		where: {
			id: req.params.reportId,
			deleted: false
		}
	}).then(async reportObj => {
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
		
		ReportPhoto.findAll({
			where: {
				reportId: reportObj.id,
				deleted: false
			},
			order: [["createdAt", "ASC"]],
			attributes: { exclude: ["photoData"] },
		}).then(async photoList => {
			if (!photoList || !photoList.length) return res.status(httpStatus.NOT_FOUND).send({
				name: httpStatus[httpStatus.NOT_FOUND],
				code: httpStatus.NOT_FOUND,
				message: "Report does not have any photos"
			});
			
			await asyncForEach(photoList, (photoItem, index) => {
				photoItem.destroy().catch(error => ErrorHandler(req, res, error));
			});
			
			return res.status(httpStatus.OK).send({
				name: "OK",
				code: httpStatus.OK
			});
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * GET /reports/:reportId/photos/:photoId
 */
router.get("/:reportId/photos/:photoId", (req, res) => {
	const { Report, ReportPhoto } = req.models;
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
		
		ReportPhoto.findOne({
			where: {
				id: req.params.photoId,
				reportId: reportObj.id,
				deleted: false
			},
			order: [["createdAt", "ASC"]],
			attributes: { exclude: ["photoData"] },
		}).then(photoObj => {
			if (!photoObj) return res.status(httpStatus.NOT_FOUND).send({
				name: httpStatus[httpStatus.NOT_FOUND],
				code: httpStatus.NOT_FOUND,
				message: `Report does not have any photo with identifier ${req.params.photoId}`
			});
			
			return res.status(httpStatus.OK).send(photoObj);
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * GET /reports/:reportId/photos/:photoId/file
 */
router.get("/:reportId/photos/:photoId/file", (req, res) => {
	const { Report, ReportPhoto } = req.models;
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
		
		ReportPhoto.findOne({
			where: {
				id: req.params.photoId,
				reportId: reportObj.id,
				deleted: false
			},
			order: [["createdAt", "ASC"]],
			attributes: ["photoData", "photoMime"]
		}).then(photoObj => {
			if (!photoObj) return res.status(httpStatus.NOT_FOUND).send({
				name: httpStatus[httpStatus.NOT_FOUND],
				code: httpStatus.NOT_FOUND,
				message: `Report does not have any photo with identifier ${req.params.photoId}`
			});
			
			res.header("Content-Type", photoObj.photoMime);
			res.write(photoObj.photoData, "binary");
			return res.end(undefined, "binary");
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

/**
 * DELETE /reports/:reportId/photos/:photoId
 */
router.delete("/:reportId/photos/:photoId", (req, res) => {
	const { account } = req;
	if (!account) return res.status(httpStatus.UNAUTHORIZED).send({
		name: httpStatus[httpStatus.UNAUTHORIZED],
		code: httpStatus.UNAUTHORIZED,
		message: "Invalid authorization token"
	});
	
	const { Report, ReportPhoto } = req.models;
	Report.findOne({
		where: {
			id: req.params.reportId,
			deleted: false
		}
	}).then(async reportObj => {
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
		
		ReportPhoto.findOne({
			where: {
				id: req.params.photoId,
				reportId: reportObj.id,
				deleted: false
			},
			order: [["createdAt", "ASC"]],
			attributes: { exclude: ["photoData"] },
		}).then(photoObj => {
			if (!photoObj) return res.status(httpStatus.NOT_FOUND).send({
				name: httpStatus[httpStatus.NOT_FOUND],
				code: httpStatus.NOT_FOUND,
				message: `Report does not have any photo with identifier ${req.params.photoId}`
			});
			
			photoObj.destroy().then(() => {
				return res.status(httpStatus.OK).send({
					name: "OK",
					code: httpStatus.OK
				});
			}).catch(error => ErrorHandler(req, res, error));
		}).catch(error => ErrorHandler(req, res, error));
	}).catch(error => ErrorHandler(req, res, error));
});

module.exports = router;