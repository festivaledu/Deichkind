<template>
	<div class="page-container md-layout-row">
		<vue-headful :title="`${$t('dykes.add_dyke')} – ${$t('app.app_name')}`" />
		<md-app md-waterfall md-mode="fixed">
			<md-app-toolbar class="md-primary">
				<md-button class="md-icon-button" @click="back">
					<md-icon>arrow_back</md-icon>
				</md-button>
				<h3 class="md-title" style="flex: 1">{{ $t("dykes.add_dyke") }}</h3>
				
				<!-- <md-button class="md-icon-button" :disabled="!$v.$anyDirty || isWorking" >
					<md-icon>clear</md-icon>
				</md-button> -->
				<md-button class="md-icon-button" @click="saveDyke" :disabled="$v.$invalid || !$v.$anyDirty || isWorking" >
					<md-icon>check</md-icon>
				</md-button>
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
						<md-field>
							<label>{{ $t("dykes.fields.name") }}</label>
							<md-input v-model="dykeData.name" @input="$v.$touch()" required :disabled="isWorking"></md-input>
						</md-field>
						
						<md-field>
							<label>{{ $t("dykes.fields.city") }}</label>
							<md-input v-model="dykeData.city" @input="$v.$touch()" required :disabled="isWorking"></md-input>
						</md-field>
						
						<md-field>
							<label>{{ $t("dykes.fields.state") }}</label>
							<md-input v-model="dykeData.state" @input="$v.$touch()" required :disabled="isWorking"></md-input>
						</md-field>
						
						<md-field>
							<label>{{ $t("dykes.fields.file") }}</label>
							<md-file v-model="dykeData.file" @input="$v.$touch()" accept="application/vnd.google-earth.kml+xml, .kml" :disabled="isWorking" @change="fileSelected" :md-change="fileSelected" ref="fuckingFileSelector"/>
						</md-field>
					</form>
				</div>
			</md-app-content>
		</md-app>
		
		<md-dialog-alert
			:md-active.sync="showDialog.errorOperation"
			:md-content="showDialog.errorOperationMessage"
			:md-confirm-text="$t('app.ok')" />
	</div>
</template>

<style lang="less">
form {
	padding: 12px 16px;
}
</style>


<script>
import AppDrawer from "@/components/AppDrawer.vue";
import { AccountAPI, DykeAPI } from "@/scripts/ApiUtil"
import { required } from "vuelidate/lib/validators";

export default {
	name: "dykeAdd",
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
		dykeData: {}
	}),
	components: {
		AppDrawer
	},
	validations: {
		dykeData: {
			name: { required },
			city: { required },
			state: { required }
		}
	},
	methods: {
		back() {
			if (!this.$v.$anyDirty || confirm(this.$t("app.confirm_cancel"))) {
				this.$router.go(-1);
			}
		},
		fileSelected(e) {
			// this.dykeData.file = e.target.files[0];
			// this.$v.$touch();
		},
		async saveDyke() {
			this.isWorking = true;
			
			const formData = new FormData();
			for (var key in this.dykeData) {
				formData.append(key, this.dykeData[key]);
			}
			formData.append("file", this.$refs["fuckingFileSelector"].$refs["inputFile"].files[0]);

			let result = await DykeAPI.addDyke(formData);
			
			if (result.status != 200) {
				this.isWorking = false;
				
				this.showDialog.errorOperation = true
				this.showDialog.errorOperationMessage = `${this.$t("app.error_operation")}<br><br>${result.status} ${result.statusText}<br>${result.data.message}`;
			} else {
				this.$router.replace(`/dykes/${result.data.id}?created=1`)
			}
		}
	}
}
</script>
