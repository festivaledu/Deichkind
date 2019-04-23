'use strict';

const crypto = require("crypto-js");

module.exports = (sequelize, DataTypes) => {
	const Report = sequelize.define('Report', {
		id: {
			type: DataTypes.STRING(32),
			primaryKey: true,
			unique: true,
			allowNull: false,
		},
		dykeId: {
			type: DataTypes.STRING(32),
			allowNull: false
		},
		title: {
			type: DataTypes.STRING,
			allowNull: false
		},
		latitude: DataTypes.STRING,
		longitude: DataTypes.STRING,
		accountId: {
			type: DataTypes.STRING(32),
			allowNull: false
		},
		deleted: {
			type: DataTypes.BOOLEAN,
			defaultValue: false
		}
	}, {
		hooks: {
			beforeCreate: (reportObj, options) => {
				reportObj.dataValues.id = crypto.SHA256(reportObj.dataValues.id).toString(crypto.enc.Hex).substr(0, 32);
				return reportObj;
			}
		}
	});
	Report.associate = function({ Account, Dyke, Comment, ReportPhoto }) {
		// associations can be defined here
		Report.belongsTo(Account, {
			foreignKey: "accountId"
		});
		Report.belongsTo(Dyke, {
			foreignKey: "dykeId"
		});
		Report.hasMany(Comment, {
			foreignKey: "reportId",
			as: "comments",
			onDelete: "CASCADE"
		});
		Report.hasMany(ReportPhoto, {
			foreignKey: "reportId",
			as: "photos",
			onDelete: "CASCADE"
		});
	};
	return Report;
};