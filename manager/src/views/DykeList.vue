<template>
	<div class="page-container md-layout-row">
		<md-app md-waterfall md-mode="fixed">
			<md-app-toolbar class="md-primary">
				<md-button class="md-icon-button hide-lg" @click="showNavigation = !showNavigation">
					<md-icon>menu</md-icon>
				</md-button>
				<h3 class="md-title" style="flex: 1">{{ $t("app_drawer.menu_dykes") }}</h3>
				
				
				<md-menu md-direction="bottom-end">
					<md-button class="md-icon-button" md-menu-trigger>
						<md-icon>more_vert</md-icon>
					</md-button>

					<md-menu-content>
						<md-menu-item to="/dykes/new">{{ $t("dykes.menu.action_createDyke") }}</md-menu-item>
					</md-menu-content>
				</md-menu>
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
				
				<div v-else-if="dykeData">
					<md-list class="md-triple-line">
						<md-list-item v-if="!dykeData.length">
							<div class="md-list-item-text">
								<span>{{ $t("reports.no_dykes_title") }}</span>
								<span>{{ $t("reports.no_dykes_body") }}</span>
							</div>
						</md-list-item>
						
						<md-list-item v-for="(dykeItem, index) in dykeData" :to="`/dykes/${dykeItem.id}`" :key="`dyke_${index}`">
							<md-avatar class="md-avatar-icon">
								<md-icon>waves</md-icon>
							</md-avatar>

							<div class="md-list-item-text">
								<span>{{dykeItem.name}}</span>
								<span>{{dykeItem.city}}, {{dykeItem.state}}</span>
								<span>{{ $t("dykes.last_updated") }}: {{dykeItem.updatedAt | date}}</span>
							</div>
							<md-icon>chevron_right</md-icon>
						</md-list-item>
					</md-list>
				</div>
			</md-app-content>
		</md-app>
		
		<md-snackbar md-position="center" :md-duration="Infinity" :md-active.sync="showSnackbar.error" md-persistent>
			<span>{{ $t("app.error_server_data") }}</span>
			<md-button class="md-primary" @click="showSnackbar.error = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
		
		<md-snackbar md-position="center" :md-duration="Infinity" :md-active.sync="showSnackbar.success" md-persistent>
			<span>{{ $t("dykes.message_deleted") }}</span>
			<md-button class="md-primary" @click="showSnackbar.success = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
	</div>
</template>

<script>
import AppDrawer from "@/components/AppDrawer.vue"
import { AccountAPI, DykeAPI } from "@/scripts/ApiUtil"

export default {
	name: "dykeList",
	data: () => ({
		isWorking: false,
		showNavigation: false,
		showSnackbar: {
			error: false,
			success: false
		},
		dykeData: null
	}),
	components: {
		AppDrawer
	},
	methods: {
		back() {
			this.$router.go(-1);
		},
	},
	async created() {
		setTimeout(async () => {
			this.isWorking = true;
			
			var _dykeData = await DykeAPI.getDykes();
			if (_dykeData && _dykeData.status == 200) {
				this.dykeData = _dykeData.data;
			}
			
			if (this.dykeData) {
				this.isWorking = false;
			} else {
				this.showSnackbar = true;
			}
		}, 0);
		
		if (this.$route.query.deleted) {
			this.showSnackbar.success = true;
			this.$router.replace(this.$route.path)
		}
	},
	filters: {
		date(dateString) {
			return new Date(dateString).toLocaleString("en-US");
		}
	}
}
</script>
