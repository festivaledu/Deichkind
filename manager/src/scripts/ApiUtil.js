import { get, post, put, delete as _delete } from "axios";
// const apiUrl = `${window.location.origin}/api`;
const apiUrl = "http://localhost:3000"

export class AccountAPI {
	static async getMe() {
		return await get(`${apiUrl}/account/me`, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
	
	static async updateMe(profileDetails) {
		return await put(`${apiUrl}/account/me`, profileDetails, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
	
	static async deleteMe() {
		return await _delete(`${apiUrl}/account/me`, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
	
	static async getMyAvatar() {
		return await get(`${apiUrl}/account/me/avatar`, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
	
	static async updateMyAvatar(avatarData) {
		return await put(`${apiUrl}/account/me/avatar`, avatarData, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
	
	static async getUserById(userId) {
		return await get(`${apiUrl}/account/${userId}`).catch(_ => _.response);
	}
	
	static async getUserAvatarById(userId) {
		return await get(`${apiUrl}/account/${userId}/avatar`).catch(_ => _.response);
	}
}

export class AuthAPI {
	static async register(registerData) {
		return await post(`${apiUrl}/auth/register`, registerData).catch(_ => _.response);
	}
	
	static async login(loginData) {
		return await post(`${apiUrl}/auth/login`, loginData).catch(_ => _.response);
	}
	
	static async verify() {
		return await get(`${apiUrl}/auth/verify`, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
}

export class DocsAPI {
	static async getDocs() {
		return await get(`${apiUrl}/docs`).catch(_ => _.response);
	}
}

export class DykeAPI {
	static async getDykes() {
		return await get(`${apiUrl}/dykes`).catch(_ => _.response);
	}
	
	static async addDyke(dykeData) {
		return await post(`${apiUrl}/dykes/new`, dykeData, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`,
				'Content-Type': 'multipart/form-data'
			}
		}).catch(_ => _.response);
	}
	
	static async getDykeById(dykeId) {
		return await get(`${apiUrl}/dykes/${dykeId}`).catch(_ => _.response);
	}
	
	static async updateDykeById(dykeId, dykeData) {
		return await put(`${apiUrl}/dykes/${dykeId}`, dykeData, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
	
	static async deleteDykeById(dykeId) {
		return await _delete(`${apiUrl}/dykes/${dykeId}`, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
	
	static async getDykeFileById(dykeId) {
		return await get(`${apiUrl}/dykes/${dykeId}/file`).catch(_ => _.response);
	}
	
	static async getDykeReportsById(dykeId) {
		return await get(`${apiUrl}/dykes/${dykeId}/reports`).catch(_ => _.response);
	}
	
	static async addDykeReport(dykeId, reportData) {
		return await post(`${apiUrl}/dykes/${dykeId}`, reportData, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
}

export class ReportAPI {
	static async getReports() {
		return await get(`${apiUrl}/reports`).catch(_ => _.response);
	}
	
	static async getReportById(reportId) {
		return await get(`${apiUrl}/reports/${reportId}`).catch(_ => _.response);
	}
	
	static async updateReportById(reportId, reportData) {
		return await put(`${apiUrl}/reports/${reportId}`, reportData, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
	
	static async deleteReportById(reportId) {
		return await _delete(`${apiUrl}/reports/${reportId}`, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
	
	static async getReportComments(reportId) {
		return await get(`${apiUrl}/reports/${reportId}/comments`).catch(_ => _.response);
	}
	
	static async addReportComment(reportId, commentData) {
		return await post(`${apiUrl}/reports/${reportId}/comment`, commentData, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
	
	static async getReportCommentById(reportId, commentId) {
		return await get(`${apiUrl}/reports/${reportId}/comments/${commentId}`).catch(_ => _.response);
	}
	
	static async deleteReportCommentById(reportId, commentId) {
		return await post(`${apiUrl}/reports/${reportId}/comment/${commentId}`, {
			headers: {
				"Authorization": `Bearer ${window.$cookies.get("authToken")}`
			}
		}).catch(_ => _.response);
	}
}