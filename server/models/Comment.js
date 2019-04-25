'use strict';

const crypto = require("crypto-js");

module.exports = (sequelize, DataTypes) => {
	const Comment = sequelize.define('Comment', {
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
		message: {
			type: DataTypes.TEXT,
			allowNull: false
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
			beforeCreate: (commentObj, options) => {
				commentObj.dataValues.id = crypto.SHA256(commentObj.dataValues.id).toString(crypto.enc.Hex).substr(0, 32);
				return commentObj;
			}
		}
	});
	
	Comment.associate = function({ Account, Dyke, Report }) {
		// associations can be defined here
		// Comment.belongsTo(Account, {
		// 	foreignKey: "accountId"
		// });
		
		// Comment.belongsTo(Dyke, {
		// 	foreignKey: "dykeId"
		// });
		
		// Comment.belongsTo(Report, {
		// 	foreignKey: "reportId"
		// });
	};
	
	return Comment;
};