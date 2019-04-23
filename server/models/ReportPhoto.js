'use strict';

const crypto = require("crypto-js");

module.exports = (sequelize, DataTypes) => {
	const ReportPhoto = sequelize.define('ReportPhoto', {
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
		reportId: {
			type: DataTypes.STRING(32),
			allowNull: false
		},
		photoData: {
			type: DataTypes.BLOB,
			allowNull: false,
		},
		photoMime: {
			type: DataTypes.STRING,
			allowNull: false,
		},
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
			beforeCreate: (reportPhotoObj, options) => {
				reportPhotoObj.dataValues.id = crypto.SHA256(reportPhotoObj.dataValues.id).toString(crypto.enc.Hex).substr(0, 32);
				return reportPhotoObj;
			}
		}
	});
	ReportPhoto.associate = function({ Account, Dyke, Comment, Report }) {
		// associations can be defined here
		ReportPhoto.belongsTo(Account, {
			foreignKey: "accountId"
		});
		ReportPhoto.belongsTo(Dyke, {
			foreignKey: "dykeId"
		});
		ReportPhoto.belongsTo(Report, {
			foreignKey: "reportId"
		});
	};
	return ReportPhoto;
};