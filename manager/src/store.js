import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
	state: {
		accountId: ""
	},
	mutations: {
		setAccountId(state, value) {
			state.accountId = value;
		}
	},
	getters: {
		accountId: state => state.accountId
	}
})
