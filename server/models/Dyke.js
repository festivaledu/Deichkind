'use strict';

const crypto = require("crypto-js");

module.exports = (sequelize, DataTypes) => {
	const Dyke = sequelize.define('Dyke', {
		id: {
			type: DataTypes.STRING(32),
			allowNull: false,
			primaryKey: true,
		},
		name: {
			type: DataTypes.STRING,
			allowNull: false
		},
		city: {
			type: DataTypes.STRING,
			allowNull: false
		},
		state: {
			type: DataTypes.STRING,
			allowNull: false
		},
		kmlFile: {
			type: DataTypes.BLOB
		},
		accountId: {
			type: DataTypes.STRING(32),
			allowNull: false
		}
	}, {
		hooks: {
			beforeCreate: (dykeObj, options) => {
				dykeObj.dataValues.id = crypto.SHA256(dykeObj.dataValues.id).toString(crypto.enc.Hex).substr(0, 32);
				return dykeObj;
			}
		}
	});
	Dyke.associate = function({Account, Report}) {
		// associations can be defined here
		// Dyke.belongsTo(Account, {
		// 	foreignKey: "accountId",
		// });
		Dyke.hasMany(Report, {
			foreignKey: "dykeId",
			as: "reports",
			onDelete: "CASCADE"
		});
	};
	return Dyke;
};