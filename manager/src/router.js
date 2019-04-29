import Vue from 'vue'
import Router from 'vue-router'

import { AuthAPI } from "@/scripts/ApiUtil"

Vue.use(Router)

const router = new Router({
	routes: [
		{
			path: '/login',
			name: 'login',
			component: () => import(/* webpackChunkName: "Login" */ './views/Login.vue'),
			meta: {
				noAuth: true
			}
		},
		{
			path: '/register',
			name: 'register',
			component: () => import(/* webpackChunkName: "Register" */ './views/Register.vue'),
			meta: {
				noAuth: true
			}
		},
		{
			path: '/',
			name: 'dashboard',
			component: () => import(/* webpackChunkName: "Dashboard" */ './views/Dashboard.vue')
		},
		{
			path: '/documentation*',
			name: 'documentation',
			component: () => import(/* webpackChunkName: "Docs" */ './views/Documentation.vue')
		},
		{
			path: '/reports',
			name: 'reportList',
			component: () => import(/* webpackChunkName: "ReportList" */ './views/ReportList.vue')
		},
		{
			path: '/reports/:reportId',
			name: 'reportView',
			component: () => import(/* webpackChunkName: "ReportView" */ './views/ReportView.vue')
		},
		{
			path: '/dykes',
			name: 'dykeList',
			component: () => import(/* webpackChunkName: "DykeList" */ './views/DykeList.vue')
		},
		{
			path: '/dykes/new',
			name: 'dykeAdd',
			component: () => import(/* webpackChunkName: "DykeAdd" */ './views/DykeAdd.vue')
		},
		{
			path: '/dykes/:dykeId',
			name: 'dykeEdit',
			component: () => import(/* webpackChunkName: "DykeEdit" */ './views/DykeEdit.vue')
		},
		{
			path: '/profile',
			name: 'profile',
			component: () => import(/* webpackChunkName: "Profile" */ './views/Profile.vue')
		}
	],
	linkActiveClass: "link-active"
});

router.beforeEach(async (to, from, next) => {
	if (!to.matched.some(_ => _.meta.noAuth)) {
		if (!window.$cookies.get("authToken")) {
			return next({
				path: "/login",
				query: {
					next: to.path != "/" ? to.path : undefined
				},
				replace: true
			});
		} else {
			const authData = await AuthAPI.verify();
			if (authData.status != 200) {
				return next({
					path: "/login",
					query: {
						next: to.path != "/" ? to.path : undefined
					},
					replace: true
				});
			} else {
				window.$cookies.set("authToken", authData.data["token"], (() => {
					const expiryDate = new Date();
					expiryDate.setHours(expiryDate.getHours() + 2);
					return expiryDate;
				})());
			}
		}
	}
	
	next();
});

export default router;