<template>
	<div>
		<md-toolbar class="md-primary">
			<md-avatar class="md-avatar-icon md-large md-primary" v-if="meData && meData.profileImage">
				<img :src="`${currentOrigin}/api/account/${meData.id}/avatar`">
			</md-avatar>
			<md-avatar class="md-avatar-icon md-large md-accent" v-else-if="meData && !meData.profileImage">
				<md-ripple>{{ initials }}</md-ripple>
			</md-avatar>
			<span v-if="meData">{{ meData.username }}</span>
			<span class="md-caption" v-if="meData">{{ meData.email }}</span>
		</md-toolbar>
		<md-list>
			<md-list-item to="/">
				<md-icon>home</md-icon>
				<span class="md-list-item-text">{{ $t("app_drawer.menu_dashboard") }}</span>
			</md-list-item>
			
			<md-list-item to="/reports">
				<md-icon>error</md-icon>
				<span class="md-list-item-text">{{ $t("app_drawer.menu_reports") }}</span>
			</md-list-item>

			<md-list-item to="/dykes">
				<md-icon>waves</md-icon>
				<span class="md-list-item-text">{{ $t("app_drawer.menu_dykes") }}</span>
			</md-list-item>
			
			<md-list-item to="/documentation">
				<md-icon>help</md-icon>
				<span class="md-list-item-text">{{ $t("app_drawer.menu_api") }}</span>
			</md-list-item>
			
			<md-divider></md-divider>
			
			<md-list-item to="/profile">
				<md-icon>person</md-icon>
				<span class="md-list-item-text">{{ $t("app_drawer.menu_profile") }}</span>
			</md-list-item>
			
			<md-list-item @click="signOut">
				<md-icon>power_off</md-icon>
				<span class="md-list-item-text">{{ $t("app_drawer.menu_signout") }}</span>
			</md-list-item>
		</md-list>
	</div>
</template>

<style lang="less">
.md-avatar .md-ripple {
	text-transform: uppercase;
}
</style>


<script>
import { AccountAPI } from "@/scripts/ApiUtil";

export default {
	name: 'app-drawer',
	data: () => ({
		meData: null
	}),
	async created() {
		setTimeout(async () => {
			
			var _meData = await AccountAPI.getMe();
			if (_meData && _meData.status == 200) {
				this.meData = _meData.data;
				this.$store.commit("setAccountId", this.meData.id)
			}
		}, 0);
	},
	methods: {
		signOut() {
			if (!confirm(this.$t("app_drawer.action_signout"))) return;
			
			this.$cookies.remove("authToken");
			this.$router.replace("/login");
		}
	},
	computed: {
		initials() {
			let initials = this.meData.username.replace(/\_|\:|\./g, " ").replace(/[^a-zA-Z-0-9_ ]/g, "").match(/\b\w/g);

			if (initials.length > 1) {
				initials = `${initials[0]}${initials[initials.length - 1]}`;
			} else if (initials.length) {
				initials = initials[0];
			}
			
			return initials;
		},
		currentOrigin() {
			return window.location.origin;
		}
	}
}
</script>
