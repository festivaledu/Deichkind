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
				},
				exampleResponse: {
					id: "0",
					username: "username",
					email: "username@example.com",
					role: 2,
					lastLogin: "1970-01-01T01:00:00.000Z",
					updatedAt: "1970-01-01T01:00:00.000Z",
				}
			},
			"PUT /me": {
				method: "PUT",
				description: "Updates the currently logged in user profile with the data contained in the body",
				parameters: {
					username: "Sets the new username",
					password: "An SHA512 hash of the updated password",
					email: "The updated E-Mail address"
				},
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token"]
				},
				exampleRequest: {
					username: "updated",
					password: "b109f3b[...]cacbc86",
					email: "updated@example.com",
				},
				exampleResponse: {
					id: "0",
					username: "updated",
					email: "updated@example.com",
					role: 2,
					lastLogin: "1970-01-01T01:00:00.000Z",
					updatedAt: "1970-01-02T0:32:17.000Z",
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
				},
				exampleResponse: {
					"id": "0",
					"username": "username",
					"profileImage": 0,
					"role": 2,
					"createdAt": "2019-05-26T07:41:36.663Z"
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
					[httpStatus.CONFLICT]: ["Username or E-Mail address already in use"]
				},
				exampleRequest: {
					username: "username",
					email: "username@example.com",
					password: "b109f3b[...]cacbc86"
				},
				exampleResponse: {
					auth: true,
					token: "eyJhbGc[...]LRa-ca0",
					refreshToken: "eyJhbGc[...]-4RjabA",
					role: 2
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
				},
				exampleRequest: {
					username: "username@example.com",
					password: "b109f3b[...]cacbc86"
				},
				exampleResponse: {
					auth: true,
					token: "eyJhbGc[...]LRa-ca0",
					refreshToken: "eyJhbGc[...]-4RjabA",
					role: 2
				}
			},
			"GET /verify": {
				method: "GET",
				description: "Verifys a user's authToken and returns a new one if it's valid",
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid or no authorization token provided"]
				},
				exampleResponse: {
					auth: true,
					token: "eyJhbGc[...]LRa-ca0",
					role: 2
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
				},
				exampleRequest: {
					token: "eyJhbGc[...]LRa-ca0",
				},
				exampleResponse: {
					token: "eyJhbGc[...]-4RjabA"
				}
			}
		},
		"/dykes": {
			"GET /": {
				method: "GET",
				description: "Returns a list of every dyke available, including reports and comments",
				errors: {
					[httpStatus.NOT_FOUND]: ["No dykes found"]
				},
				exampleResponse: [
					{
						"id": "5feceb66ffc86f38d952786c6d696c79",
						"name": "Räber Spring",
						"city": "Suderburg",
						"state": "Niedersachsen",
						"accountId": "5feceb66ffc86f38d952786c6d696c79",
						"createdAt": "2019-04-29T14:37:40.753Z",
						"updatedAt": "2019-04-29T14:37:40.753Z"
					},
					{
						"id": "f2649dfb6ee12e5cea8c6a47aa04bfb1",
						"name": "Unnamed Dyke",
						"city": "Unknown City",
						"state": "Unknown State",
						"accountId": "5feceb66ffc86f38d952786c6d696c79",
						"createdAt": "2019-05-26T07:08:53.859Z",
						"updatedAt": "2019-05-26T07:08:53.859Z"
					}
				]
			},
			"POST /new": {
				method: "POST",
				description: "Creates a new dyke object and stores the specified KML file in the database. Data is supplied using Form Data.",
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
				},
				exampleResponse: {
					"name": "Unnamed Dyke",
					"city": "Unknown City",
					"state": "Unknown State",
					"id": "89158e756f2c0a4153af3b7dfdd2d9e6",
					"accountId": "5feceb66ffc86f38d952786c6d696c79",
					"updatedAt": "2019-05-26T07:05:04.509Z",
					"createdAt": "2019-05-26T07:05:04.509Z"
				}
			},
			"GET /:dykeId": {
				method: "GET",
				description: "Returns a dyke object by its database id",
				errors: {
					[httpStatus.NOT_FOUND]: ["No dyke with identifier {dykeId} found"]
				},
				exampleResponse: {
					"id": "5feceb66ffc86f38d952786c6d696c79",
					"name": "Räber Spring",
					"city": "Suderburg",
					"state": "Niedersachsen",
					"accountId": "5feceb66ffc86f38d952786c6d696c79",
					"createdAt": "2019-04-29T14:37:40.753Z",
					"updatedAt": "2019-04-29T14:37:40.753Z"
				}
			},
			"PUT /:dykeId": {
				method: "PUT",
				description: "Updates a dyke object with the given values. Data is supplied using Form Data.",
				parameters: {
					name: "The dyke's public display name",
					city: "The city the dyke is located in or related to",
					state: "The state the dyke is located in (not to be mixed up with country)",
					file: "The dyke's KML file to be displayed in a map view"
				},
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token", "You are not allowed to perform this action"],
					[httpStatus.NOT_FOUND]: ["No dyke with identifier {dykeId} found"]
				},
				exampleResponse: {
					"id": "f2649dfb6ee12e5cea8c6a47aa04bfb1",
					"name": "Updated Dyke",
					"city": "Updated City",
					"state": "Updated State",
					"accountId": "5feceb66ffc86f38d952786c6d696c79",
					"createdAt": "2019-05-26T07:08:53.859Z",
					"updatedAt": "2019-05-26T07:08:53.859Z"
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
				},
				exampleResponse: [
					{
						"id": "5feceb66ffc86f38d952786c6d696c79",
						"dykeId": "5feceb66ffc86f38d952786c6d696c79",
						"title": "Alles kaputt!",
						"latitude": "0",
						"longitude": "0",
						"position": "pos",
						"details": {
							"type": "followup",
							"waterLoss": "isolated",
							"waterCondition": "muddy",
							"leakageType": "flowing",
							"deformationType": "slipped-topsoil"
						},
						"accountId": "5feceb66ffc86f38d952786c6d696c79",
						"resolved": false,
						"deleted": false,
						"createdAt": "2019-04-29T14:37:40.757Z",
						"updatedAt": "2019-04-29T14:37:40.757Z",
						"comments": [
							{
								"id": "5feceb66ffc86f38d952786c6d696c79",
								"dykeId": "5feceb66ffc86f38d952786c6d696c79",
								"reportId": "5feceb66ffc86f38d952786c6d696c79",
								"message": "Alles kaputt!",
								"accountId": "5feceb66ffc86f38d952786c6d696c79",
								"deleted": false,
								"createdAt": "2019-04-29T14:37:40.760Z",
								"updatedAt": "2019-04-29T14:37:40.760Z"
							}
						]
					}
				]
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
				},
				exampleRequest: {
					title: "Report Title",
					message: "Report Message",
					latitude: 50.3633,
					longitude: 14.8429,
					position: "[Unknown format]",
					details: {
						type: "initial",
						waterLoss: "followup",
						waterCondition: "clear",
						leakageType: "flowing",
						deformationType: "upheavel"
					}
				},
				exampleResponse: {
					report: {
						title: "Report Title",
						latitude: 50.3633,
						longitude: 14.8429,
						position: "[Unknown format]",
						details: {
							type: "initial",
							waterLoss: "followup",
							waterCondition: "clear",
							leakageType: "flowing",
							deformationType: "upheavel"
						},
						id: "eaaafe0ea3d66d791b48c9deb9b0f8cb",
						dykeId: "5feceb66ffc86f38d952786c6d696c79",
						accountId: "5feceb66ffc86f38d952786c6d696c79",
						resolved: false,
						deleted: false,
						updatedAt: "2019-05-26T07:20:48.098Z",
						createdAt: "2019-05-26T07:20:48.098Z"
					},
					comment: {
						deleted: false,
						id: "2f377c3de5bd599c9a8fb2531a7060cf",
						dykeId: "5feceb66ffc86f38d952786c6d696c79",
						reportId: "eaaafe0ea3d66d791b48c9deb9b0f8cb",
						message: "Report Message",
						accountId: "5feceb66ffc86f38d952786c6d696c79",
						updatedAt: "2019-05-26T07:20:48.107Z",
						createdAt: "2019-05-26T07:20:48.107Z"
					}
				}
			}
		},
		"/reports": {
			"GET /": {
				method: "GET",
				description: "Returns a list of every report available, including comments",
				errors: {
					[httpStatus.NOT_FOUND]: ["There are no reports to show"]
				},
				exampleResponse: [
					{
						id: "eaaafe0ea3d66d791b48c9deb9b0f8cb",
						dykeId: "5feceb66ffc86f38d952786c6d696c79",
						title: "Report Title",
						latitude: "50.3633",
						longitude: "14.8429",
						position: "[Unknown format]",
						details: {
							type: "initial",
							waterLoss: "followup",
							waterCondition: "clear",
							leakageType: "flowing",
							deformationType: "upheavel"
						},
						accountId: "5feceb66ffc86f38d952786c6d696c79",
						resolved: false,
						deleted: false,
						createdAt: "2019-05-26T07:20:48.098Z",
						updatedAt: "2019-05-26T07:20:48.098Z",
						comments: [
							{
								id: "2f377c3de5bd599c9a8fb2531a7060cf",
								dykeId: "5feceb66ffc86f38d952786c6d696c79",
								reportId: "eaaafe0ea3d66d791b48c9deb9b0f8cb",
								message: "Report Message",
								accountId: "5feceb66ffc86f38d952786c6d696c79",
								deleted: false,
								createdAt: "2019-05-26T07:20:48.107Z",
								updatedAt: "2019-05-26T07:20:48.107Z"
							}
						],
						photos: []
					},
					{
						id: "5feceb66ffc86f38d952786c6d696c79",
						dykeId: "5feceb66ffc86f38d952786c6d696c79",
						title: "Alles kaputt!",
						latitude: "0",
						longitude: "0",
						position: "pos",
						details: {
							type: "followup",
							waterLoss: "isolated",
							waterCondition: "muddy",
							leakageType: "flowing",
							deformationType: "slipped-topsoil"
						},
						accountId: "5feceb66ffc86f38d952786c6d696c79",
						resolved: false,
						deleted: false,
						createdAt: "2019-04-29T14:37:40.757Z",
						updatedAt: "2019-04-29T14:37:40.757Z",
						comments: [
							{
								id: "5feceb66ffc86f38d952786c6d696c79",
								dykeId: "5feceb66ffc86f38d952786c6d696c79",
								reportId: "5feceb66ffc86f38d952786c6d696c79",
								message: "Alles kaputt!",
								accountId: "5feceb66ffc86f38d952786c6d696c79",
								deleted: false,
								createdAt: "2019-04-29T14:37:40.760Z",
								updatedAt: "2019-04-29T14:37:40.760Z"
							}
						],
						photos: [
							{
								id: "5feceb66ffc86f38d952786c6d696c79",
								photoMime: "image/png",
								createdAt: "2019-04-29T14:37:40.762Z",
								updatedAt: "2019-04-29T14:37:40.762Z"
							}
						]
					}
				]
			},
			"GET /:reportId": {
				method: "GET",
				description: "Returns a report by its database id",
				errors: {
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found"]
				},
				exampleResponse: {
					id: "5feceb66ffc86f38d952786c6d696c79",
					dykeId: "5feceb66ffc86f38d952786c6d696c79",
					title: "Alles kaputt!",
					latitude: "0",
					longitude: "0",
					position: "pos",
					details: {
						type: "followup",
						waterLoss: "isolated",
						waterCondition: "muddy",
						leakageType: "flowing",
						deformationType: "slipped-topsoil"
					},
					accountId: "5feceb66ffc86f38d952786c6d696c79",
					resolved: false,
					deleted: false,
					createdAt: "2019-04-29T14:37:40.757Z",
					updatedAt: "2019-04-29T14:37:40.757Z",
					comments: [
						{
							id: "5feceb66ffc86f38d952786c6d696c79",
							dykeId: "5feceb66ffc86f38d952786c6d696c79",
							reportId: "5feceb66ffc86f38d952786c6d696c79",
							message: "Alles kaputt!",
							accountId: "5feceb66ffc86f38d952786c6d696c79",
							deleted: false,
							createdAt: "2019-04-29T14:37:40.760Z",
							updatedAt: "2019-04-29T14:37:40.760Z"
						}
					],
					photos: [
						{
							id: "5feceb66ffc86f38d952786c6d696c79",
							photoMime: "image/png",
							createdAt: "2019-04-29T14:37:40.762Z",
							updatedAt: "2019-04-29T14:37:40.762Z"
						}
					]
				}
			},
			"PUT /:reportId": {
				method: "PUT",
				description: "Updates a report by its database id",
				parameters: {
					title: "The public display name of the created report",
					latitude: "The latitudinal position of the issue",
					longitude: "The longitudinal position of the issue",
					position: "The position of the issue on a schematic drawing of the dyke",
					details: "A JSON object representing various details of the issue",
					resolved: "Marks a report as resolved"
				},
				errors: {
					[httpStatus.UNAUTHORIZED]: ["Invalid authorization token", "You are not allowed to perform this action"],
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found"]
				},
				exampleRequest: {
					title: "Updated Title",
					resolved: true
				},
				exampleResponse: {
					id: "5feceb66ffc86f38d952786c6d696c79",
					dykeId: "5feceb66ffc86f38d952786c6d696c79",
					title: "Updated Title",
					latitude: "0",
					longitude: "0",
					position: "pos",
					details: {
						type: "followup",
						waterLoss: "isolated",
						waterCondition: "muddy",
						leakageType: "flowing",
						deformationType: "slipped-topsoil"
					},
					accountId: "5feceb66ffc86f38d952786c6d696c79",
					resolved: true,
					deleted: false,
					createdAt: "2019-04-29T14:37:40.757Z",
					updatedAt: "2019-04-29T14:37:40.757Z"
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
				},
				exampleResponse: [
					{
						id: "5feceb66ffc86f38d952786c6d696c79",
						dykeId: "5feceb66ffc86f38d952786c6d696c79",
						reportId: "5feceb66ffc86f38d952786c6d696c79",
						message: "Alles kaputt!",
						accountId: "5feceb66ffc86f38d952786c6d696c79",
						deleted: false,
						createdAt: "2019-04-29T14:37:40.760Z",
						updatedAt: "2019-04-29T14:37:40.760Z"
					}
				]
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
				},
				exampleRequest: {
					message: "This is a description of damage on a dyke"
				},
				exampleResponse: {
					deleted: false,
					id: "fb6a49c8047787c044e85a15ed57254f",
					dykeId: "5feceb66ffc86f38d952786c6d696c79",
					reportId: "eaaafe0ea3d66d791b48c9deb9b0f8cb",
					message: "This is a description of damage on a dyke",
					accountId: "5feceb66ffc86f38d952786c6d696c79",
					updatedAt: "2019-05-26T07:30:25.077Z",
					createdAt: "2019-05-26T07:30:25.077Z"
				}
			},
			"GET /:reportId/comments/:commentId": {
				method: "GET",
				description: "Gets the details of a specific comment linked to a report",
				errors: {
					[httpStatus.NOT_FOUND]: ["No report with identifier {reportId} found", "Report does not have any comment with identifier {commentId}"]
				},
				exampleResponse: {
					id: "5feceb66ffc86f38d952786c6d696c79",
					dykeId: "5feceb66ffc86f38d952786c6d696c79",
					reportId: "5feceb66ffc86f38d952786c6d696c79",
					message: "Alles kaputt!",
					accountId: "5feceb66ffc86f38d952786c6d696c79",
					deleted: false,
					createdAt: "2019-04-29T14:37:40.760Z",
					updatedAt: "2019-04-29T14:37:40.760Z"
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
				},
				exampleResponse: [
					{
						id: "5feceb66ffc86f38d952786c6d696c79",
						dykeId: "5feceb66ffc86f38d952786c6d696c79",
						reportId: "5feceb66ffc86f38d952786c6d696c79",
						photoMime: "image/png",
						accountId: "5feceb66ffc86f38d952786c6d696c79",
						deleted: false,
						createdAt: "2019-04-29T14:37:40.762Z",
						updatedAt: "2019-04-29T14:37:40.762Z"
					}
				]
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
				},
				exampleResponse: {
					id: "5feceb66ffc86f38d952786c6d696c79",
					dykeId: "5feceb66ffc86f38d952786c6d696c79",
					reportId: "5feceb66ffc86f38d952786c6d696c79",
					photoMime: "image/png",
					accountId: "5feceb66ffc86f38d952786c6d696c79",
					deleted: false,
					createdAt: "2019-04-29T14:37:40.762Z",
					updatedAt: "2019-04-29T14:37:40.762Z"
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