<template>
	<div class="page-container md-layout-row">
		<md-app md-waterfall md-mode="fixed">
			<md-app-toolbar class="md-primary">
				<md-button class="md-icon-button hide-lg" @click="showNavigation = !showNavigation" v-if="currentRoutePath == '/documentation'">
					<md-icon>menu</md-icon>
				</md-button>
				<md-button class="md-icon-button" @click="back" v-else>
					<md-icon>arrow_back</md-icon>
				</md-button>
				
				<h3 class="md-title">{{ currentRoute.length ? currentRouteDisplayName : "API Documentation" }}</h3>
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
				
				<md-list class="md-double-line" v-else-if="docsData && !currentRouteData.method">
					<md-list-item v-for="(routeKey, index) in currentRouteKeys" :key="`docs_${index}`" :to="`${currentRoutePath}${internalURLForRoute(routeKey, currentRouteData[routeKey].method)}`">
						<div class="md-list-item-text">
							<span>{{ routeKey }}</span>
						</div>
						<md-icon>chevron_right</md-icon>
					</md-list-item>
				</md-list>
				
				<md-list class="md-double-line" v-else-if="docsData">
					<md-list-item>
						<div class="md-list-item-text">
							<span>Route</span>
							<p class="text-wrap">{{ currentRoutePath }}</p>
						</div>
					</md-list-item>
					
					<md-list-item v-if="currentRouteData.method">
						<div class="md-list-item-text">
							<span>Method</span>
							<span>{{ currentRouteData.method }}</span>
						</div>
					</md-list-item>
					
					<md-list-item v-if="currentRouteData.description">
						<div class="md-list-item-text">
							<span>Description</span>
							<p class="text-wrap">{{ currentRouteData.description }}</p>
						</div>
					</md-list-item>
					
					<md-subheader v-if="currentRouteData.parameters">Parameters</md-subheader>
					<md-table v-if="currentRouteData.parameters">
						<md-table-row v-for="(key, index) in Object.keys(currentRouteData.parameters)" :key="`params_${index}`">
							<md-table-cell>{{ key }}</md-table-cell>
							<md-table-cell>{{ currentRouteData.parameters[key] }}</md-table-cell>
						</md-table-row>
					</md-table>
					
					<md-subheader v-if="currentRouteData.errors">Possible Errors</md-subheader>
					<md-table v-if="currentRouteData.errors">
						<md-table-row v-for="(key, index) in Object.keys(currentRouteData.errors)" :key="`errors_${index}`">
							<md-table-cell>{{ key }}</md-table-cell>
							<md-table-cell>
								<p v-for="(error, index) in currentRouteData.errors[key]" :key="`error_${index}`">{{ error }}</p>
							</md-table-cell>
						</md-table-row>
					</md-table>
				</md-list>
			</md-app-content>
		</md-app>
		
		<md-snackbar md-position="center" :md-duration="Infinity" :md-active.sync="showSnackbar" md-persistent>
			<span>Failed to load route {{currentRouteDisplayName }}!</span>
			<md-button class="md-primary" @click="showSnackbar = false">Close</md-button>
		</md-snackbar>
	</div>
</template>

<style lang="less">

</style>


<script>
import { DocsAPI } from "@/scripts/ApiUtil";

import AppDrawer from "@/components/AppDrawer";

export default {
	name: "docs",
	components: {
		AppDrawer
	},
	data: () => ({
		isWorking: false,
		showNavigation: false,
		showSnackbar: false,
		docsData: null
	}),
	async created() {
		setTimeout(async () => {
			this.isWorking = true;
			
			var _docsData = await DocsAPI.getDocs();
			if (_docsData && _docsData.status == 200) {
				this.docsData = _docsData.data;
			}
			
			if (this.docsData) {
				this.isWorking = false;
			} else {
				this.showSnackbar = true;
			}
		}, 0);
		
		
	},
	methods: {
		back() {
			this.showSnackbar = false;
			this.$router.go(-1);
		},
		internalURLForRoute(route, method) {
			if (!method) return route;
			
			return `${route.replace(/(GET|POST|PUT|DELETE) /g, "")}?method=${method}`;
		}
	},
	computed: {
		currentRoute() {
			return this.$route.fullPath.replace(/^\/documentation/, "");
		},
		currentRouteData() {
			var route = this.docsData;
			let routeKeys = this.currentRoute.match(/(\/\:?\w*)/g);
			if (!routeKeys || !routeKeys.length) {
				return route;
			}
			
			for (var i=0; i < routeKeys.length; i++) {
				var recurseKeyCheck = (current, list) => {
					var _route = list.join("");
					
					if (route.method) return route;
					
					if (route[_route]) {
						return route[current];
					} else {
						let _key = `${this.$route.query.method} ${_route}`;
						if (route[_key]) {
							return route[_key];
						} else {
							if (i == routeKeys.length - 1) {
								this.showSnackbar = true;
								return {};
							}
							
							return recurseKeyCheck(current, list.slice(0, list.length - 1))
						}
					}
				}
				
				route = recurseKeyCheck(routeKeys[i], routeKeys.slice(i, routeKeys.length));
			}
			
			return route;
		},
		currentRouteKeys() {
			return Object.keys(this.currentRouteData)
		},
		currentRoutePath() {
			return this.$route.path;
		},
		currentRouteDisplayName() {
			let _route = this.currentRoutePath.replace(/^\/documentation/, "");
			if (this.$route.query.method) {
				return `${this.$route.query.method} ${_route}`;
			}
			return _route;
		}
	}
}
</script>
