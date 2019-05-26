'use strict';

const Sequelize = require("sequelize");

module.exports = (sequelize, DataTypes) => {
	const Token = sequelize.define('Token', {
		id: {
			type: DataTypes.TEXT,
			primaryKey: true,
			unique: true,
			allowNull: false,
		},
		content: {
			type: DataTypes.JSON,
			allowNull: false
		},
		expires: {
			type: DataTypes.DATE,
			allowNull: false
		}
	}, {
		hooks: {
			beforeCreate(tokenObj, options) {
				Token.findAll({
					where: {
						expires: {
							[Sequelize.Op.lt]: new Date()
						}
					}
				}).then(tokenList => {
					tokenList.forEach(tokenObj => {
						tokenObj.destroy();
					});
				});
			}
		}
	});
	
	return Token;
};