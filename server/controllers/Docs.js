const express = require("express");
const router = express.Router();
const httpStatus = require("http-status");

router.get("/", (req, res) => {
	return res.status(httpStatus.OK).send({
		"GET /docs": {
			method: "GET",
			description: "Shows a list of available API endpoints and their documentation"
		},
		"/account": {
			"GET /me": {
				method: "GET",
				description: "Gets all details for the currently logged in user",
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token"]
				}
			},
			"PUT /me": {
				method: "PUT",
				description: "Updates the currently logged in user profile with the data contained in the body",
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token"]
				}
			},
			"DELETE /me": {
				method: "DELETE",
				description: "Deletes the currently logged in user account and associated items"
			},
			"GET /me/avatar": {
				method: "GET",
				description: "Gets the profile image of the currently signed in user",
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token"],
					[httpStatus.NOT_FOUND]: ["User does not have any profile image"]
				}
			},
			"PUT /me/avatar": {
				method: "PUT",
				description: "Updates the profile image of the currently signed in user",
				parameters: {
					file: "The image file to be used as an avatar"
				},
				errors: {
					[httpStatus.BAD_REQUEST]: ["No avatar file specified"],
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token"],
				}
			},
			"GET /:userId": {
				method: "GET",
				description: "Gets all public details for a specific user (username, E-Mail, last login)",
				errors: {
					[httpStatus.NOT_FOUND]: ["User not found"]
				}
			},
			"GET /:userId/avatar": {
				method: "GET",
				description: "Gets the profile image of a specific user",
				errors: {
					[httpStatus.NOT_FOUND]: ["User not found", "User does not have any profile image"]
				}
			},
		},
		"/auth": {
			"POST /register": {
				method: "POST",
				description: "Creates a new user and returns an authToken",
				parameters: {
					username: "The new user's display name",
					email: "The new user's E-Mail address (only used for authentication)",
					password: "A SHA512 sum of the user's password"
				},
				errors: {
					[httpStatus.BAD_REQUEST]: ["No username, email or password provided"],
					"409": ["Username or E-Mail address already in use"]
				}
			},
			"POST /login": {
				method: "POST",
				description: "Checks a user's credentials and returns an authToken if successful",
				parameters: {
					username: "The user's display name or E-Mail address",
					password: "A SHA512 sum of the user's password"
				},
				errors: {
					[httpStatus.BAD_REQUEST]: ["No username, email or password provided"],
					[httpStatus.UNAUTHORIZED]: ["Incorrect password"],
					[httpStatus.NOT_FOUND]: ["User not found"]
				}
			},
			"GET /verify": {
				method: "GET",
				description: "Verifys a user's authToken and returns a new one if it's valid",
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid or no authorization token provided"]
				}
			},
			"POST /token": {
				method: "POST",
				description: "Generates a new authToken using a specified refresh token",
				parameters: {
					token: "The current valid non-expired refresh token"
				},
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid or no authorization token provided"]
				}
			}
		},
		"/dykes": {
			"GET /": {
				method: "GET",
				description: "Returns a list of every dyke available, including reports and comments",
				errors: {
					[httpStatus.NOT_FOUND]: ["No dykes found"]
				}
			},
			"POST /new": {
				method: "POST",
				description: "Creates a new dyke object and stores the specified KML file in the database",
				parameters: {
					name: "The dyke's public display name",
					city: "The city the dyke is located in or related to",
					state: "The state the dyke is located in (not to be mixed up with country)",
					file: "The dyke's KML file to be displayed in a map view"
				},
				errors: {
					[httpStatus.BAD_REQUEST]: ["No dyke file specified"],
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token"],
					[httpStatus.CONFLICT]: ["Dyke with name {name} already exists"]
				}
			},
			"GET /:dykeId": {
				method: "GET",
				description: "Returns a dyke object by its database id",
				errors: {
					[httpStatus.NOT_FOUND]: ["No dyke with identifier {dykeId} found"]
				}
			},
			"PUT /:dykeId": {
				method: "PUT",
				description: "Updates a dyke object with the given values",
				parameters: {
					name: "The dyke's public display name",
					file: "The dyke's KML file to be displayed in a map view"
				},
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token", "You are not allowed to perform this action"],
					[httpStatus.NOT_FOUND]: ["No dyke with identifier {dykeId} found"]
				}
			},
			"DELETE /:dykeId": {
				method: "DELETE",
				description: "Deletes a dyke object with the given database id",
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token", "You are not allowed to perform this action"],
					[httpStatus.NOT_FOUND]: ["No dyke with identifier {dykeId} found"]
				}
			},
			"GET /:dykeId/file": {
				method: "GET",
				description: "Gets the KML map file for a specific dyke object",
				errors: {
					[httpStatus.NOT_FOUND]: ["No dyke with identifier {dykeId} found"]
				}
			},
			"GET /:dykeId/reports": {
				method: "GET",
				description: "Returns a list of reports for a specific dyke",
				errors: {
					[httpStatus.NOT_FOUND]: ["No dyke with identifier {dykeId} found", "Dyke does not have any reports"]
				}
			},
			"POST /:dykeId/reports/new": {
				method: "POST",
				description: "Creates a new report for a specific dyke object",
				parameters: {
					title: "The public display name of the created report",
					message: "The description text of the created report",
					latitude: "The latitudinal position of the issue",
					longitude: "The longitudinal position of the issue",
					position: "The position of the issue on a schematic drawing of the dyke",
					details: "A JSON object representing various details of the issue"
				},
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token"],
					[httpStatus.NOT_FOUND]: ["No dyke with identifier {dykeId} found"]
				}
			}
		},
		"/reports": {
			"GET /": {
				method: "GET",
				description: "Returns a list of every report available, including comments",
				errors: {
					[httpStatus.NOT_FOUND]: ["There are no reports to show"]
				}
			},
			"GET /:reportId": {
				method: "GET",
				description: "Returns a report by its database id",
				errors: {
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found"]
				}
			},
			"PUT /:reportId": {
				method: "PUT",
				description: "Updates a report by its database id",
				parameters: {
					title: "The public display name of the created report",
					message: "The description text of the created report",
					latitude: "The latitudinal position of the issue",
					longitude: "The longitudinal position of the issue",
					position: "The position of the issue on a schematic drawing of the dyke",
					details: "A JSON object representing various details of the issue",
					resolved: "Marks a report as resolved"
				},
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token", "You are not allowed to perform this action"],
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found"]
				}
			},
			"DELETE /:reportId": {
				method: "DELETE",
				description: "Deletes a specified report",
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token", "You are not allowed to perform this action"],
					[httpStatus.NOT_FOUND]: ["No dyke with identifier {dykeId} found"]
				}
			},
			"GET /:reportId/comments": {
				method: "GET",
				description: "Gets a list of comments linked to a specific report",
				errors: {
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found", "Report does not have any comments"]
				}
			},
			"POST /:reportId/comment": {
				method: "POST",
				description: "Adds a new comment to a report",
				parameters: {
					message: "The content of the created comment"
				},
				errors: {
					[httpStatus.BAD_REQUEST]: ["No comment message supplied"],
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token"],
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found"]
				}
			},
			"GET /:reportId/comments/:commentId": {
				method: "GET",
				description: "Gets the details of a specific comment linked to a report",
				errors: {
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found", "Report does not have any comment with identifier {commentId}"]
				}
			},
			"DELETE /:reportId/comments/:commentId": {
				method: "DELETE",
				description: "Deletes a specific comment linked to a report",
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token", "You are not allowed to perform this action"],
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found", "Report does not have any comment with identifier {commentId}"]
				}
			},
			"GET /:reportId/photos": {
				method: "GET",
				description: "Gets a list of photos linked to a specific report",
				errors: {
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found", "Report does not have any photos"]
				}
			},
			"POST /:reportId/photos": {
				method: "POST",
				description: "Adds a list of photos to a report",
				parameters: {
					files: "A list of files to add to a report. The FormData class requires a name for each file uploaded (see Object), which can be anything since the API only cares about the list of files itself (see Array)"
				},
				errors: {
					[httpStatus.BAD_REQUEST]: ["No photos supplied"],
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token", "You are not allowed to perform this action"],
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found"]
				}
			},
			"DELETE /:reportId/photos": {
				method: "DELETE",
				description: "Deletes every photo associated to a report",
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token", "You are not allowed to perform this action"],
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found", "Report does not have any photos"]
				}
			},
			"GET /:reportId/photos/:photoId": {
				method: "GET",
				description: "Gets the details of a specific photo linked to a report",
				errors: {
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found", "Report does not have any photo with identifier {commentId}"]
				}
			},
			"GET /:reportId/photos/:photoId/file": {
				method: "GET",
				description: "Gets the binary data for a photo linked to a report",
				errors: {
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found", "Report does not have any photo with identifier {commentId}"]
				}
			},
			"DELETE /:reportId/photos/:photoId": {
				method: "DELETE",
				description: "Deletes a specific photo associated to a report",
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token", "You are not allowed to perform this action"],
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found", "Report does not have any photo with identifier {photoId}"]
				}
			}
		}
	});
});

module.exports = router;