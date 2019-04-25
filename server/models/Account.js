'use strict';

const crypto = require("crypto-js");
const bcrypt = require("bcryptjs");

module.exports = (sequelize, DataTypes) => {
	const Account = sequelize.define('Account', {
		id: {
			type: DataTypes.STRING(32),
			primaryKey: true,
			unique: true,
			allowNull: false,
		},
		username: {
			type: DataTypes.STRING,
			unique: true,
			allowNull: false
		},
		email: {
			type: DataTypes.STRING,
			unique: true,
			allowNull: false
		},
		password: {
			type: DataTypes.STRING,
			allowNull: false
		},
		role: {
			type: DataTypes.INTEGER,
			defaultValue: -1
		},
		profileImage: {
			type: DataTypes.BLOB
		},
		profileImageMime: {
			type: DataTypes.STRING
		},
		lastLogin: {
			type: DataTypes.DATE
		}
	}, {
		hooks: {
			beforeCreate: (accountObj, options) => {
				accountObj.dataValues.id = crypto.SHA256(accountObj.dataValues.id).toString(crypto.enc.Hex).substr(0, 32);
				return accountObj;
			}
		}
	});
	Account.associate = function(models) {
		// associations can be defined here
	};
	
	Account.prototype.usernameValid = function(_username) {
		return this.username === _username;
	}
	Account.prototype.emailValid = function(_email) {
		return this.email === _email;
	}
	Account.prototype.passwordValid = function(_password) {
		return bcrypt.compareSync(_password, this.password);
	}
	
	return Account;
};