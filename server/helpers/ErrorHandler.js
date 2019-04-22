const httpStatus = require("http-status");

module.exports = (req, res, error) => {
	console.log(error);
	return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
		name: httpStatus[httpStatus.INTERNAL_SERVER_ERROR],
		code: httpStatus.INTERNAL_SERVER_ERROR,
		message: error.message
	});
}