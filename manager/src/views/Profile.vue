<template>
	<div class="page-container md-layout-row">
		<vue-headful :title="`${$t('app_drawer.menu_profile')} – ${$t('app.app_name')}`" />
		<md-app md-waterfall md-mode="fixed">
			<md-app-toolbar class="md-primary">
				<md-button class="md-icon-button hide-lg" @click="showNavigation = !showNavigation">
					<md-icon>menu</md-icon>
				</md-button>
				<h3 class="md-title">{{ $t("app_drawer.menu_profile") }}</h3>
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
				
				<md-list v-else>
					<md-list-item @click="$refs['profileImageSelector'].click()">
						<!-- <md-avatar class="md-avatar-icon">
							<md-icon>waves</md-icon>
						</md-avatar> -->

						<div class="md-list-item-text">
							<span>{{ $t("profile.change_avatar") }}</span>
							<input type="file" accept="image/png, image/jpeg" style="position: absolute; top: 9999px; left: 9999px; visibility: hidden" ref="profileImageSelector" @change="profileImageSelected" />
						</div>
						<md-icon>chevron_right</md-icon>
					</md-list-item>
					
					<md-list-item @click="models.username = ''; showDialog.changeUsername = true">
						<div class="md-list-item-text">
							<span>{{ $t("profile.change_username") }}</span>
						</div>
						<md-icon>chevron_right</md-icon>
					</md-list-item>
					
					<md-list-item @click="models.email = ''; showDialog.changeEmail = true">
						<div class="md-list-item-text">
							<span>{{ $t("profile.change_email") }}</span>
						</div>
						<md-icon>chevron_right</md-icon>
					</md-list-item>
					
					<md-list-item @click="models.password = models.passwordConf = ''; showDialog.changePassword = true">
						<div class="md-list-item-text">
							<span>{{ $t("profile.change_password") }}</span>
						</div>
						<md-icon>chevron_right</md-icon>
					</md-list-item>
				</md-list>
			</md-app-content>
		</md-app>
		
		<md-dialog-prompt
			:md-active.sync="showDialog.changeUsername"
			v-model="models.username"
			:md-title="$t('profile.change_username_dialog.title')"
			:md-input-placeholder="$t('profile.change_username_dialog.placeholder')"
			:md-cancel-text="$t('app.cancel')"
			:md-confirm-text="$t('app.ok')"
			@md-confirm="changeUsername(models.username)" />
			
		<md-dialog-prompt
			:md-active.sync="showDialog.changeEmail"
			v-model="models.email"
			:md-title="$t('profile.change_email_dialog.title')"
			:md-input-placeholder="$t('profile.change_email_dialog.placeholder')"
			:md-cancel-text="$t('app.cancel')"
			:md-confirm-text="$t('app.ok')"
			@md-confirm="changeEmail(models.email)" />
			
		<md-dialog :md-active.sync="showDialog.changePassword" :md-click-outside-to-close="false" :md-close-on-esc="false">
			<md-dialog-title>{{ $t("profile.change_password_dialog.title") }}</md-dialog-title>
			
			<md-dialog-content>
				<md-field>
					<md-input type="password" v-model="models.password" :placeholder="$t('profile.change_password_dialog.placeholder')"></md-input>
				</md-field>
				<md-field>
					<md-input type="password" v-model="models.passwordConf" :placeholder="$t('profile.change_password_dialog.placeholder_confirm')"></md-input>
				</md-field>
			</md-dialog-content>
			
			<md-dialog-actions>
				<md-button class="md-primary" @click="showDialog.changePassword = false">{{ $t("app.close") }}</md-button>
				<md-button class="md-primary" @click="showDialog.changePassword = false; changePassword(models.password, models.passwordConf)">{{ $t("app.ok") }}</md-button>
			</md-dialog-actions>
		</md-dialog>
		
		<md-snackbar md-position="center" :md-duration="Infinity" :md-active.sync="showSnackbar.genericError" md-persistent>
			<span>{{ $t("profile.errors.generic") }}</span>
			<md-button class="md-primary" @click="showSnackbar.genericError = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
		
		<md-snackbar md-position="center" :md-duration="4000" :md-active.sync="showSnackbar.avatarSuccess">
			<span>{{ $t("profile.avatar_success") }}</span>
			<md-button class="md-primary" @click="showSnackbar.avatarSuccess = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
		<md-snackbar md-position="center" :md-duration="4000" :md-active.sync="showSnackbar.usernameSuccess">
			<span>{{ $t("profile.username_success") }}</span>
			<md-button class="md-primary" @click="showSnackbar.usernameSuccess = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
		<md-snackbar md-position="center" :md-duration="4000" :md-active.sync="showSnackbar.emailSuccess">
			<span>{{ $t("profile.email_success") }}</span>
			<md-button class="md-primary" @click="showSnackbar.emailSuccess = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
		<md-snackbar md-position="center" :md-duration="4000" :md-active.sync="showSnackbar.passwordSuccess">
			<span>{{ $t("profile.password_success") }}</span>
			<md-button class="md-primary" @click="showSnackbar.passwordSuccess = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
		
		<md-dialog-alert
			:md-active.sync="showDialog.errorOperation"
			:md-content="showDialog.errorOperationMessage"
			:md-confirm-text="$t('app.ok')" />
	</div>
</template>


<script>
import crypto from "crypto-js";
import { AccountAPI } from "@/scripts/ApiUtil"
import AppDrawer from "@/components/AppDrawer.vue"

export default {
	name: "profile",
	components: {
		AppDrawer
	},
	data: () => ({
		isWorking: false,
		showNavigation: false,
		showDialog: {
			changeUsername: false,
			changeEmail: false,
			changePassword: false,
			errorOperation: false,
			errorOperationMessage: null
		},
		showSnackbar: {
			genericError: false,
			avatarSuccess: false,
			usernameSuccess: false,
			emailSuccess: false,
			passwordSuccess: false
		},
		models: {
			username: "",
			email: "",
			password: "",
			passwordConf: ""
		}
	}),
	created() {
		setTimeout(async () => {
			//this.isWorking = true
		}, 0);
	},
	methods: {
		async profileImageSelected(e) {
			if (!e.target.files[0]) return;
			
			let formData = new FormData();
			formData.append("file", e.target.files[0]);

			let result = await AccountAPI.updateMyAvatar(formData);
			if (result.status == 200) {
				this.showSnackbar.avatarSuccess = true;
			} else {
				this.showSnackbar.genericError = true;
			}
		},
		async changeUsername(username) {
			if (!username) return;
			
			let result = await AccountAPI.updateMe({
				username: username
			});
			if (result.status == 200) {
				this.showSnackbar.usernameSuccess = true;
			} else {
				this.showSnackbar.genericError = true;
			}
		},
		async changeEmail(email) {
			if (!email) return;
			
			let result = await AccountAPI.updateMe({
				email: email
			});
			if (result.status == 200) {
				this.showSnackbar.emailSuccess = true;
			} else {
				this.showSnackbar.genericError = true;
			}
		},
		async changePassword(password, passwordConf) {
			if (!password || !passwordConf) return;
			
			if (password.length < 8) {
				this.showDialog.errorOperation = true
				this.showDialog.errorOperationMessage = this.$t("profile.errors.password_too_short");
				return;
			}
			
			if (password.localeCompare(passwordConf) != 0) {
				this.showDialog.errorOperation = true
				this.showDialog.errorOperationMessage = this.$t("profile.errors.passwords_not_matching");
				return;
			}
			
			let result = await AccountAPI.updateMe({
				password: crypto.SHA512(password).toString(crypto.enc.Hex)
			});
			if (result.status == 200) {
				this.showSnackbar.passwordSuccess = true;
			} else {
				this.showSnackbar.genericError = true;
			}
		}
	}
}
</script>
