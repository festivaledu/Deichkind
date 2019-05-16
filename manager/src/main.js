import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import 'vue-material/dist/vue-material.min.css'
import 'vue-material/dist/theme/default.css'
import VueMaterial from 'vue-material';
Vue.use(VueMaterial);

import VueCookies from "vue-cookies";
Vue.use(VueCookies);

import Vuelidate from "vuelidate";
Vue.use(Vuelidate);

import VueHeadful from "vue-headful";
Vue.component("vue-headful", VueHeadful);

import VueI18n from 'vue-i18n';
import messages from './localizable.json';
Vue.use(VueI18n);
const i18n = new VueI18n({
	locale: navigator.language,
	fallbackLocale: "en-US",
	messages: messages,
});

Vue.config.productionTip = false

new Vue({
	router,
	store,
	i18n,
	render: h => h(App)
}).$mount('#app')
