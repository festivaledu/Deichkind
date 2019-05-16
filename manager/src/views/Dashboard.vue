<template>
	<div class="page-container md-layout-row">
		<vue-headful :title="`${$t('app_drawer.menu_dashboard')} – ${$t('app.app_name')}`" />
		<md-app md-waterfall md-mode="fixed">
			<md-app-toolbar class="md-primary">
				<md-button class="md-icon-button hide-lg" @click="showNavigation = !showNavigation">
					<md-icon>menu</md-icon>
				</md-button>
				<h3 class="md-title">{{ $t("app.app_name") }}</h3>
			</md-app-toolbar>
			
			<md-app-drawer md-permanent="full" :md-active.sync="showNavigation">
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
					<md-list class="md-triple-line">
						<md-subheader v-if="reportData.length">{{ $t("reports.title") }}</md-subheader>
						
						<md-list-item v-if="!reportData.length">
							<div class="md-list-item-text">
								<span>{{ $t("reports.no_reports_title") }}</span>
								<span>{{ $t("reports.no_reports_body") }}</span>
							</div>
						</md-list-item>
						
						<md-list-item v-for="(reportItem, index) in reportData.slice(0,3)" :to="`/reports/${reportItem.id}`" :key="`report_${index}`">
							<md-avatar v-if="userData[reportItem.accountId].profileImage">
								<img :src="`${currentOrigin}/api/account/${reportItem.accountId}/avatar`">
							</md-avatar>
							<md-avatar class="md-avatar-icon" v-else>
								<md-ripple>{{ initials(userData[reportItem.accountId].username) }}</md-ripple>
							</md-avatar>

							<div class="md-list-item-text">
								<p>{{ reportItem.title }} <span v-if="reportItem.resolved">({{ $t("reports.resolved") }})</span></p>
								<span>{{ reportItem.comments[0].message | br }}</span>
								<span>{{ userData[reportItem.accountId].username }} – {{ dykeData.find(_ => _.id === reportItem.dykeId).name }} – {{ reportItem.comments[0].createdAt | date }}</span>
							</div>
							<md-icon>chevron_right</md-icon>
						</md-list-item>
					</md-list>
						
					<md-list v-if="reportData.length > 3">
						<md-list-item to="/reports">
							<div class="md-list-item-text">
								<span>{{ $t("dashboard.action_showmore") }}</span>
							</div>
							<md-icon>chevron_right</md-icon>
						</md-list-item>
					</md-list>
						
					<md-list class="md-triple-line">
						<md-subheader v-if="dykeData.length">{{ $t("dykes.title") }}</md-subheader>
						
						<md-list-item v-if="!dykeData.length">
							<div class="md-list-item-text">
								<span>{{ $t("dykes.no_dykes_title") }}</span>
								<span>{{ $t("dykes.no_dykes_body") }}</span>
							</div>
						</md-list-item>
						
						<md-list-item v-for="(dykeItem, index) in dykeData.slice(0,3)" :to="`/dykes/${dykeItem.id}`" :key="`dyke_${index}`">
							<md-avatar class="md-avatar-icon">
								<md-icon>waves</md-icon>
							</md-avatar>

							<div class="md-list-item-text">
								<span>{{ dykeItem.name }}</span>
								<span>{{ dykeItem.city }}, {{ dykeItem.state }}</span>
								<span>{{ $t("dykes.last_updated") }}: {{ dykeItem.updatedAt | date }}</span>
							</div>
							<md-icon>chevron_right</md-icon>
						</md-list-item>
					</md-list>
					
					<md-list v-if="dykeData.length > 3">
						<md-list-item to="/dykes">
							<div class="md-list-item-text">
								<span>{{ $t("dashboard.action_showmore") }}</span>
							</div>
							<md-icon>chevron_right</md-icon>
						</md-list-item>
					</md-list>
				</div>
				
				<md-button class="md-fab md-fab-bottom-right" to="/dykes/new">
					<md-icon>add</md-icon>
				</md-button>
			</md-app-content>
		</md-app>
		
		<md-snackbar md-position="center" :md-duration="Infinity" :md-active.sync="showSnackbar.error" md-persistent>
			<span>{{ $t("app.error_server_data") }}</span>
			<md-button class="md-primary" @click="showSnackbar.error = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
	</div>
</template>

<script>
import AppDrawer from "@/components/AppDrawer.vue"
import { AccountAPI, AuthAPI, DykeAPI, ReportAPI } from "@/scripts/ApiUtil"

let asyncForEach = async (array, callback) => {
	for (let index = 0; index < array.length; index++) {
		await callback(array[index], index)
	}
}

export default {
	name: 'dashboard',
	data: () => ({
		isWorking: false,
		showNavigation: false,
		showSnackbar: {
			error: false
		},
		reportData: null,
		dykeData: null,
		userData: {},
	}),
	components: {
		AppDrawer
	},
	async created() {
		setTimeout(async () => {
			this.isWorking = true;
			
			try {
				var _reportData = await ReportAPI.getReports();
				if (_reportData && _reportData.status == 200) {
					this.reportData = _reportData.data;
				}
				
				var _dykeData = await DykeAPI.getDykes();
				if (_dykeData && _dykeData.status == 200) {
					delete _dykeData.comments;
					this.dykeData = _dykeData.data;
				}
				
				let self = this;
				
				let comments = Array.prototype.concat.apply([], this.reportData.map(reportObj => reportObj.comments.map(commentObj => commentObj.accountId)));
				let users = [...new Set(comments)];
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
			
			if (this.reportData && this.dykeData) {
				this.isWorking = false;
			} else {
				this.showSnackbar.error = true;
			}
		}, 0);
	},
	methods: {
		initials(username) {
			let initials = username.replace(/\_|\:|\./g, " ").replace(/[^a-zA-Z-0-9_ ]/g, "").match(/\b\w/g);

			if (initials.length > 1) {
				initials = `${initials[0]}${initials[initials.length - 1]}`;
			} else if (initials.length) {
				initials = initials[0];
			}
			
			return initials;
		}
	},
	computed: {
		currentOrigin() {
			return (window.location.origin + window.location.pathname).replace(/\/$/, "");
		}
	},
	filters: {
		date(dateString) {
			return new Date(dateString).toLocaleString(navigator.language);
		},
		br(input) {
			return input.replace(/(\<br\>|\n)/g, " ")
		}
	}
}
</script>
