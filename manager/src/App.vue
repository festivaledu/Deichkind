<template>
	<div id="app">
		<router-view/>
	</div>
</template>

<style lang="less">
html {
	--md-theme-default-primary: #0277BD !important;
	--md-theme-default-primary-on-background: var(--md-theme-default-primary) !important;
	--md-theme-default-accent: #8BC34A !important;
	--md-theme-default-accent-on-background: var(--md-theme-default-accent) !important;
}

.router-link-active .md-icon.md-theme-default.md-icon-font {
	color: var(--md-theme-default-primary);
}

.md-app {
	min-height: 100vh;
	max-height: 100vh;
}

.md-button.md-theme-default[disabled] {
	color: rgba(0, 0, 0, 0.26) !important;
}
.md-toolbar button.md-button[disabled] i.md-icon {
	color: rgba(255, 255, 255, 0.38) !important;
}

.md-drawer .md-toolbar {
	height: 144px;
	padding: 0 16px;
	flex-flow: column wrap;
	align-items: flex-start;
	align-content: flex-start;
	justify-content: center;
	
	.md-avatar {
		margin: 0 0 8px;
	}
	
	span {
		color: #FFF;
		
		&.md-caption {
			opacity: 0.74;
		}
	}
}

.md-app-container .md-toolbar {
	flex-wrap: nowrap;
}

.md-list p.text-wrap {
	overflow: visible;
	white-space: normal;
	word-break: break-word;
}

.md-list.md-theme-default .router-link-active,
.md-list.md-theme-default .router-link-exact-active {
	.md-icon {
		color: var(--md-theme-default-icon-on-background, rgba(0, 0, 0, 0.54)) !important
	}
	
	.md-list-item-content {
		color: var(--md-theme-default-text-primary-on-background, rgba(0, 0, 0, 0.87)) !important
	}
}

.md-content {
	padding: 0 !important;
	height: auto !important;
	min-height: 100%;
}

.md-table-cell p {
	margin-top: 0;
	
	&:last-child {
		margin-bottom: 0;
	}
}

.md-field.md-required label:after {
	content: '' !important;
}

.loading {
	position: absolute;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	background: #fff;
	transition: opacity .3s;
	will-change: opacity;
	font-size: 15px;
}
.loading-leave-active {
	display: none;
}
.loading-label {
	margin-top: 16px;
}

.row {
	margin-left: 0;
	margin-right: 0;
}

@media all and (min-width: 600px) {
	.md-toolbar button.md-button.hide-lg:first-child {
		display: none;
		
		& ~ .md-title {
			margin-left: 8px;
		}
	}
	
	.md-app-drawer {
		width: 320px !important;
		max-width: 50vw !important;
	}
}
</style>
