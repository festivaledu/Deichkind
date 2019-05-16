<template>
	<div class="page-container md-layout-row">
		<vue-headful :title="`${$t('dykes.edit_dyke')} – ${$t('app.app_name')}`" />
		<md-app md-waterfall md-mode="fixed">
			<md-app-toolbar class="md-primary">
				<md-button class="md-icon-button" @click="back">
					<md-icon>arrow_back</md-icon>
				</md-button>
				<h3 class="md-title" style="flex: 1">{{ $t("dykes.edit_dyke") }} <span v-if="dykeData">– {{ dykeData.name }}</span></h3>
				
				<!-- <md-button class="md-icon-button" :disabled="!$v.$anyDirty || isWorking" >
					<md-icon>clear</md-icon>
				</md-button> -->
				<md-button class="md-icon-button" @click="saveDyke" :disabled="!isOwnedDyke || $v.$invalid || !$v.$anyDirty || isWorking" >
					<md-icon>check</md-icon>
				</md-button>
				<md-menu md-direction="bottom-end">
					<md-button class="md-icon-button" md-menu-trigger>
						<md-icon>more_vert</md-icon>
					</md-button>

					<md-menu-content>
						<md-menu-item @click="deleteDyke" :disabled="!isOwnedDyke">{{ $t("dykes.menu.action_deleteDyke") }}</md-menu-item>
					</md-menu-content>
				</md-menu>
			</md-app-toolbar>
			
			<md-app-drawer md-permanent="full">
				<AppDrawer />
			</md-app-drawer>

			<md-app-content>
				<transition name="loading" appear v-if="isWorking && !dykeData">
					<div class="loading">
						<md-progress-spinner md-mode="indeterminate" :md-diameter="30" :md-stroke="3" />
						<div class="loading-label">
							{{ $t("app.loading") }}
						</div>
					</div>
				</transition>
				
				<div v-else-if="dykeData">
					<form class="md-layout">
						<p class="error" v-if="!isOwnedDyke">{{ $t("dykes.error_not_allowed") }}</p>
						
						<md-field>
							<label>{{ $t("dykes.fields.name") }}</label>
							<md-input v-model="dykeData.name" @input="$v.$touch()" required :disabled="isWorking || !isOwnedDyke"></md-input>
						</md-field>
						
						<md-field>
							<label>{{ $t("dykes.fields.city") }}</label>
							<md-input v-model="dykeData.city" @input="$v.$touch()" required :disabled="isWorking || !isOwnedDyke"></md-input>
						</md-field>
						
						<md-field>
							<label>{{ $t("dykes.fields.state") }}</label>
							<md-input v-model="dykeData.state" @input="$v.$touch()" required :disabled="isWorking || !isOwnedDyke"></md-input>
						</md-field>
						
						<md-field>
							<label>{{ $t("dykes.fields.replaceKML") }}</label>
							<md-file v-model="replacementKML" @input="$v.$touch()" accept="application/vnd.google-earth.kml+xml, .kml" :disabled="isWorking || !isOwnedDyke" ref="fuckingFileSelector" />
						</md-field>
					</form>
				</div>
			</md-app-content>
		</md-app>
		
		<md-snackbar md-position="center" :md-duration="Infinity" :md-active.sync="showSnackbar.error" md-persistent>
			<span>{{ $t("app.error_server_data") }}</span>
			<md-button class="md-primary" @click="showSnackbar.error = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
		
		<md-snackbar md-position="center" :md-duration="4000" :md-active.sync="showSnackbar.success" md-persistent>
			<span>{{ $t("dykes.message_updated") }}</span>
			<md-button class="md-primary" @click="showSnackbar.success = false">{{ $t("app.close") }}</md-button>
		</md-snackbar>
		
		<md-dialog-alert
			:md-active.sync="showDialog.errorOperation"
			:md-content="showDialog.errorOperationMessage"
			:md-confirm-text="$t('app.ok')" />
	</div>
</template>

<style lang="less">
form {
	padding: 12px 16px;
	
	p.error {
		color: #ff1744;
	}
}
</style>


<script>
import AppDrawer from "@/components/AppDrawer.vue"
import { AccountAPI, DykeAPI } from "@/scripts/ApiUtil"
import { required } from "vuelidate/lib/validators"

export default {
	name: "dykeList",
	data: () => ({
		isWorking: false,
		showSnackbar: {
			error: false,
			success: false
		},
		showDialog: {
			errorOperation: false,
			errorOperationMessage: null
		},
		dykeData: null,
		
		replacementKML: null
	}),
	components: {
		AppDrawer
	},
	validations: {
		dykeData: {
			name: { required },
			city: { required },
			state: { required },
		}
	},
	async created() {
		setTimeout(async () => {
			this.isWorking = true;
			
			var _dykeData = await DykeAPI.getDykeById(this.$route.params.dykeId);
			if (_dykeData && _dykeData.status == 200) {
				delete _dykeData.comments;
				this.dykeData = _dykeData.data;
			}
			
			if (this.dykeData) {
				this.isWorking = false;
			} else {
				this.showSnackbar.error = true;
			}
		}, 0);
		
		if (this.$route.query.created) {
			this.showSnackbar.success = true;
			this.$router.replace(this.$route.path)
		}
	},
	methods: {
		back() {
			if (!this.$v.$anyDirty || confirm(this.$t("app.confirm_cancel"))) {
				this.$router.go(-1);
			}
		},
		async saveDyke() {
			this.isWorking = true;
			
			let formData = new FormData();
			for (var key in this.dykeData) {
				formData.append(key, this.dykeData[key]);
			}
			formData.append("file", this.$refs["fuckingFileSelector"].$refs["inputFile"].files[0]);
			
			let result = await DykeAPI.updateDykeById(this.$route.params.dykeId, formData);
			
			if (result.status != 200) {
				this.isWorking = false;
				
				this.showDialog.errorOperation = true
				this.showDialog.errorOperationMessage = `${this.$t("app.error_operation")}<br><br>${result.status} ${result.statusText}<br>${result.data.message}`;
			} else {
				this.$v.$reset();
				this.isWorking = false;
				this.showSnackbar.success = true;
			}
		},
		async deleteDyke() {
			if (!confirm(this.$t("dykes.confirm_deleteDyke"))) return;
			
			let result = await DykeAPI.deleteDykeById(this.$route.params.dykeId);
			
			if (result.status != 200) {
				this.isWorking = false;
				
				this.showDialog.errorOperation = true
				this.showDialog.errorOperationMessage = `${this.$t("app.error_operation")}<br><br>${result.status} ${result.statusText}<br>${result.data.message}`;
			} else {
				this.$router.replace("/dykes?deleted=1");
			}
		}
	},
	computed: {
		isOwnedDyke() {
			return this.dykeData != null && this.$store.getters.accountId === this.dykeData.accountId
		}
	}
}
</script>
		