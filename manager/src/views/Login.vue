<template>
	<div class="page-container md-layout-row">
		<vue-headful :title="`${$t('login.toolbar_title')} – ${$t('app.app_name')}`" />
		<md-toolbar class="md-primary">
			<h3 class="md-title">{{ $t("login.toolbar_title") }}</h3>
		</md-toolbar>
		
		<md-card class="login-card">
			<md-card-header>
				<div class="md-title">{{ $t("login.welcome") }} {{ $t("app.app_name") }}</div>
			</md-card-header>

			<md-card-content>
				<form class="md-layout">
					<md-field>
						<label>{{ $t("login.fields.username") }}</label>
						<md-input v-model="user.username" @input="$v.$touch()" @keyup="keyUp" required :disabled="isWorking"></md-input>
					</md-field>
					
					<md-field>
						<label>{{ $t("login.fields.password") }}</label>
						<md-input type="password" v-model="user.password" @input="$v.$touch()" @keyup="keyUp" required :disabled="isWorking"></md-input>
					</md-field>
				</form>
				
				<p class="error" v-if="authData && authData.message">{{ $t("login.error_message") }}:<br>{{ authData.message }}</p>
			</md-card-content>

			<md-card-actions>
				<md-progress-spinner md-mode="indeterminate" :md-diameter="30" :md-stroke="3" v-if="isWorking"></md-progress-spinner>
				<md-button class="md-primary" to="/register" :disabled="isWorking">{{ $t("login.action_register") }}</md-button>
				<md-button class="md-primary" @click="login" :disabled="$v.user.$invalid || isWorking">{{ $t("login.action_signin") }}</md-button>
			</md-card-actions>
		</md-card>
		
		<md-dialog-alert
			:md-active.sync="showDialog.errorOperation"
			:md-content="showDialog.errorOperationMessage"
			:md-confirm-text="$t('app.ok')" />
	</div>
</template>

<style lang="less">
.md-card.login-card {
	max-width: 500px;
	margin: 0 auto;
}

p.error {
	color: #ff1744;
}
</style>


<script>
import crypto from "crypto-js";
import { AuthAPI, DykeAPI } from "@/scripts/ApiUtil";
import { required } from "vuelidate/lib/validators";


export default {
	name: "login",
	data: () => ({
		isWorking: false,
		showDialog: {
			errorOperation: false,
			errorOperationMessage: null
		},
		authData: null,
		user: {
			username: "",
			password: ""
		}
	}),
	validations: {
		user: {
			username: { required },
			password: { required },
		}
	},
	created() {
		if (this.$cookies.get("authToken")) {
			if (this.$route.query.next) {
				this.$router.replace(this.$route.query.next);
			} else {
				this.$router.replace("/");
			}
		}
	},
	methods: {
		keyUp(e) {
			if (e.keyCode == 13 && !this.$v.user.$invalid) {
				this.login();
			}
		},
		async login() {
			this.isWorking = true;
			let _authData = await AuthAPI.login({
				username: this.user.username,
				password: crypto.SHA512(this.user.password).toString(crypto.enc.Hex)
			});
			
			if (_authData.status == 200) {
				// For some reason, we need this while we are in development mode
				this.$cookies.set("authToken", _authData.data["token"], (() => {
					const expiryDate = new Date();
					expiryDate.setHours(expiryDate.getHours() + 2);
					return expiryDate;
				})());
				
				if (this.$route.query.next) {
					this.$router.replace(this.$route.query.next);
				} else {
					this.$router.replace("/");
				}
			} else {
				this.authData = {
					status: _authData.status,
					message: _authData.data.message
				}
				this.isWorking = false;
				
				if (_authData.status == 500) {
					this.showDialog.errorOperation = true
					this.showDialog.errorOperationMessage = `${this.$t("login.error_message")}:<br>${_authData.data.message}`;
				}
			}
		}
	}
}
</script>
