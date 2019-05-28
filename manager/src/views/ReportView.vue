<template>
	<div class="page-container md-layout-row">
		<vue-headful :title="`${$t('reports.view_report')} – ${$t('app.app_name')}`" />
		<md-app md-waterfall md-mode="fixed">
			<md-app-toolbar class="md-primary">
				<md-button class="md-icon-button" @click="back">
					<md-icon>arrow_back</md-icon>
				</md-button>
				
				<h3 class="md-title" style="flex: 1">{{ $t("reports.view_report") }} <span v-if="reportData">– {{ reportData.title }}</span><span v-if="reportData && reportData.resolved"> ({{ $t("reports.resolved") }})</span></h3>
				
				<md-menu md-direction="bottom-end">
					<md-button class="md-icon-button" md-menu-trigger>
						<md-icon>more_vert</md-icon>
					</md-button>

					<md-menu-content v-if="reportData">
						<md-menu-item @click="showDialog.addComment = true" :disabled="reportData.resolved">{{ $t("reports.menu.action_addComment") }}</md-menu-item>
						<md-menu-item @click="markReportAsResolved" :disabled="!isOwnedReport || reportData.resolved">{{ $t("reports.menu.action_markAsResolved") }}</md-menu-item>
						<md-menu-item @click="deleteReport" :disabled="!isOwnedReport">{{ $t("reports.menu.action_deleteReport") }}</md-menu-item>
					</md-menu-content>
				</md-menu>
			</md-app-toolbar>
			
			<md-app-drawer md-permanent="full">
				<AppDrawer />
			</md-app-drawer>
			
			<md-app-content>
				<transition name="loading" appear v-if="isWorking">
					<div class="loading">
						<md-progress-spinner md-mode="indeterminate" :md-diameter="30" :md-stroke="3" />
						<div class="loading-label">
							{{ $t("app.loading") }}
						</div>
					</div>
				</transition>
				<div v-else-if="reportData && dykeData">
					<md-subheader>{{ $t("reports.photos") }}</md-subheader>
					<div class="row">
						<a :href="`${currentOrigin}/api/reports/${reportData.id}/photos/${photoObj.id}/file`" target="_blank" class="col col-4 col-md-2 col-photo" v-for="(photoObj, index) in reportData.photos" :key="`photo_${index}`">
							<img :src="`${currentOrigin}/api/reports/${reportData.id}/photos/${photoObj.id}/file`" />
						</a>
					</div>
					
					<md-subheader>{{ $t("reports.details") }}</md-subheader>
					<md-list class="md-double-line">
						<md-list-item>
							<div class="md-list-item-text">
								<span>{{ $t("reports.dyke") }}</span>
								<span>{{ dykeData.name }} ({{ dykeData.city }}, {{ dykeData.state }})</span>
							</div>
						</md-list-item>
						<md-list-item v-for="(value, key, index) in reportData.details" :key="`detail_${index}`">
							<div class="md-list-item-text">
								<span>{{ $t(`reports.detailKeys.${key}`) }}</span>
								<span>{{ localizableHasKeyPath(`reports.detailValues.${key}.${value}`) ? $t(`reports.detailValues.${key}.${value}`) : value }}</span>
							</div>
						</md-list-item>
					</md-list>
					
					<md-subheader>{{ $t("reports.comments") }}</md-subheader>
					<md-list class="md-triple-line">
						<div v-for="(comment, index) in reportData.comments" :key="`comment_${index}`">
							<md-list-item>
								<md-avatar v-if="userData[comment.accountId].profileImage">
									<img :src="`${currentOrigin}/api/account/${comment.accountId}/avatar`">
								</md-avatar>
								<md-avatar class="md-avatar-icon" v-else>
									<md-ripple>{{ initials(userData[comment.accountId].username) }}</md-ripple>
								</md-avatar>

								<div class="md-list-item-text">
									<span>{{ userData[comment.accountId].username }}</span>
									<span>{{ comment.createdAt | date }}</span>
									<p class="text-wrap" v-html="comment.message"></p>
								</div>
							</md-list-item>

							<md-divider class="md-inset" v-if="index != reportData.comments.length - 1"></md-divider>
						</div>
					</md-list>
				</div>
			</md-app-content>
		</md-app>
		
		<md-snackbar md-position="center" :md-duration="Infinity" :md-active.sync="showSnackbar.error" md-persistent>
			<span>{{ $t("app.error_server_data") }}</span>
			<md-button class="md-primary" @click="showSnackbar.error = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
		
		<md-snackbar md-position="center" :md-duration="4000" :md-active.sync="showSnackbar.success" md-persistent>
			<span>{{ $t("reports.message_resolved") }}</span>
			<md-button class="md-primary" @click="showSnackbar.success = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
		
		<md-dialog :md-active.sync="showDialog.addComment">
			<md-dialog-title>{{ $t("reports.menu.action_addComment")}}</md-dialog-title>
			
			<form>
				<md-field>
					<label></label>
					<md-textarea maxlength="4000" v-model="postData.message" required></md-textarea>
				</md-field>
			</form>
			
			<md-dialog-actions>
				<md-button class="md-primary" @click="showDialog.addComment = false">{{ $t("app.close") }}</md-button>
				
				<md-progress-spinner md-mode="indeterminate" :md-diameter="30" :md-stroke="3" v-if="isWorking"></md-progress-spinner>
				<md-button class="md-primary" v-if="!isWorking" :disabled="$v.$invalid || isWorking" @click="saveComment">{{ $t("app.save") }}</md-button>
			</md-dialog-actions>
		</md-dialog>
		
		<md-dialog-alert
			:md-active.sync="showDialog.errorOperation"
			:md-content="showDialog.errorOperationMessage"
			:md-confirm-text="$t('app.ok')" />
	</div>
</template>

<style lang="less">
.col-photo {
	object-fit: contain;
}

@media all and (min-width: 601px) {
	.md-dialog {
		width: 50%;
	}
}
form {
	padding: 12px 16px;
	
	textarea {
		resize: none !important;
	}
}
</style>


<script>
import AppDrawer from "@/components/AppDrawer.vue"
import { AccountAPI, DykeAPI, ReportAPI } from "@/scripts/ApiUtil"
import { required } from "vuelidate/lib/validators";
import localizable from "@/localizable.json";

let asyncForEach = async (array, callback) => {
	for (let index = 0; index < array.length; index++) {
		await callback(array[index], index)
	}
}

export default {
	name: "ReportView",
	components: {
		AppDrawer
	},
	data: () => ({
		isWorking: false,
		reportData: null,
		dykeData: null,
		userData: {},
		showSnackbar: {
			error: false,
			success: false
		},
		showDialog: {
			addComment: false,
			errorOperation: false,
			errorOperationMessage: null
		},
		postData: {
			message: ""
		}
	}),
	validations: {
		postData: {
			message: { required }
		}
	},
	created() {
		setTimeout(this._loadReportData, 0);
	},
	methods: {
		back() {
			this.$router.go(-1);
		},
		async _loadReportData() {
			this.reportData = null;
			this.isWorking = true;
			
			try {
				var _reportData = await ReportAPI.getReportById(this.$route.params.reportId);
				if (_reportData && _reportData.status == 200) {
					this.reportData = _reportData.data;
				}
				
				var _dykeData = await DykeAPI.getDykeById(this.reportData.dykeId);
				if (_dykeData && _dykeData.status == 200) {
					delete _dykeData.comments;
					this.dykeData = _dykeData.data;
				}
				
				let users = [...new Set(this.reportData.comments.map(_ => _.accountId))];
				let self = this;
				await asyncForEach(users, async (user, index) => {
					var _userData = await AccountAPI.getUserById(user);
					if (_userData) {
						self.userData[user] = _userData.data;
					}
				});
			} catch (error) {
				console.log(error);
				this.showSnackbar.error = true;
			}
			
			if (this.reportData) {
				this.showDialog.addComment = false;
				this.isWorking = false;
			} else {
				this.showSnackbar.error = true;
			}
		},
		async markReportAsResolved() {
			let result = await ReportAPI.updateReportById(this.$route.params.reportId, {
				resolved: true
			});
			
			if (result.status != 200) {
				this.showDialog.errorOperation = true
				this.showDialog.errorOperationMessage = `${this.$t("app.error_operation")}\n\n${result.status} ${result.statusText}\n${result.data.message}`;
			} else {
				// this.$v.$reset();
				this.reportData.resolved = true;
				this.showSnackbar.success = true;
			}
		},
		async deleteReport() {
			if (!confirm(this.$t("reports.confirm_deleteReport"))) return;
			
			let result = await ReportAPI.deleteReportById(this.$route.params.reportId);
			
			if (result.status != 200) {
				this.showDialog.errorOperation = true
				this.showDialog.errorOperationMessage = `${this.$t("app.error_operation")}\n\n${result.status} ${result.statusText}\n${result.data.message}`;
			} else {
				this.$router.replace("/reports?deleted=1");
			}
		},
		async saveComment() {
			let result = await ReportAPI.addReportComment(this.$route.params.reportId, this.postData);
			
			if (result.status != 200) {
				this.showDialog.errorOperation = true
				this.showDialog.errorOperationMessage = `${this.$t("app.error_operation")}\n\n${result.status} ${result.statusText}\n${result.data.message}`;
			} else {
				// window.location.reload(true);
				this._loadReportData();
			}
		},
		initials(username) {
			let initials = username.replace(/\_|\:|\./g, " ").replace(/[^a-zA-Z-0-9_ ]/g, "").match(/\b\w/g);

			if (initials.length > 1) {
				initials = `${initials[0]}${initials[initials.length - 1]}`;
			} else if (initials.length) {
				initials = initials[0];
			}
			
			return initials;
		},
		localizableHasKeyPath(path) {
			path = `${navigator.language}.${path}`;
			let result = path.split(".").reduce((previous, current) => {
				return previous != null && typeof previous[current] !== "undefined" ? previous[current] : null
			}, localizable);
			
			return result != null;
		}
	},
	computed: {
		isOwnedReport() {
			return this.reportData != null && this.$store.getters.accountId === this.reportData.accountId
		},
		currentOrigin() {
			return (window.location.origin + window.location.pathname).replace(/\/$/, "");
		}
	},
	filters: {
		date(dateString) {
			return new Date(dateString).toLocaleString(navigator.language);
		}
	}
}
</script>
