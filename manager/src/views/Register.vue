<template>
	<div class="page-container md-layout-row">
		<md-toolbar class="md-primary">
			<md-button class="md-icon-button" @click="back">
				<md-icon>arrow_back</md-icon>
			</md-button>
			<h3 class="md-title">{{ $t("register.toolbar_title") }}</h3>
		</md-toolbar>
		
		<md-card class="login-card">
			<md-card-header>
				<div class="md-title">{{ $t("register.welcome") }} {{ $t("app.app_name") }}</div>
			</md-card-header>

			<md-card-content>
				<form class="md-layout" novalidate>
					<md-field :class="{'md-invalid': authData && authData.status == 409}">
						<label>{{ $t("register.fields.username") }}</label>
						<md-input v-model="user.username" @input="$v.$touch()" required :disabled="isWorking"></md-input>
						<span class="md-helper-text">{{ $t("register.field_required") }}</span>
						<span class="md-error" v-if="authData && authData.status == 409">{{ $t("register.errors.409") }}</span>
					</md-field>
					
					<md-field :class="{'md-invalid': ($v.user.email.$dirty && $v.user.email.$invalid) || authData && authData.status == 409}">
						<label>{{ $t("register.fields.email") }}</label>
						<md-input type="email" v-model="user.email" @input="$v.$touch()" required :disabled="isWorking"></md-input>
						<span class="md-helper-text">{{ $t("register.field_required") }}</span>
						<span class="md-error" v-if="$v.user.email.$dirty && $v.user.email.$invalid">{{ $t("register.errors.invalid_email") }}</span>
						<span class="md-error" v-if="authData && authData.status == 409">{{ $t("register.errors.409") }}</span>
					</md-field>
					
					<md-field :class="{'md-invalid': $v.user.password.$dirty && !$v.user.password.minLength}">
						<label>{{ $t("register.fields.password") }}</label>
						<md-input type="password" v-model="user.password" @input="$v.$touch()" required :disabled="isWorking"></md-input>
						<span class="md-helper-text">{{ $t("register.field_required") }}</span>
						<span class="md-error" v-if="!$v.user.password.minLength">{{ $t("register.errors.password_too_short") }}</span>
					</md-field>

					<md-field :class="{'md-invalid': $v.user.passwordConf.$dirty && !$v.user.passwordConf.sameAsPassword}">
						<label>{{ $t("register.fields.password_confirm") }}</label>
						<md-input type="password" v-model="user.passwordConf" @input="$v.$touch()" required :disabled="isWorking"></md-input>
						<span class="md-helper-text">{{ $t("register.field_required") }}</span>
						<span class="md-error" v-if="!$v.user.passwordConf.sameAsPassword">{{ $t("register.errors.passwords_not_matching") }}</span>
					</md-field>
				</form>
			</md-card-content>
			
			<md-card-actions>
				<md-progress-spinner md-mode="indeterminate" :md-diameter="30" :md-stroke="3" v-if="isWorking"></md-progress-spinner>
				<md-button class="md-primary" @click="register" :disabled="$v.user.$invalid || isWorking">{{ $t("register.action_register") }}</md-button>
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
import { required, email, sameAs, minLength } from "vuelidate/lib/validators";


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
			email: "",
			password: "",
			passwordConf: ""
		}
	}),
	validations: {
		user: {
			username: { required },
			email: { required, email },
			password: { required, minLength: minLength(8) },
			passwordConf: { required, sameAsPassword: sameAs("password")}
			
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
		back() {
			if (!this.$v.$anyDirty || confirm(this.$t("app.confirm_cancel"))) {
				this.$router.go(-1);
			}
		},
		async register() {
			this.isWorking = true;
			
			try {
				let _authData = await AuthAPI.register({
					username: this.user.username,
					email: this.user.email,
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
			} catch (error) {
				console.log(error);
				this.isWorking = false
			}
		}
	}
}
</script>
