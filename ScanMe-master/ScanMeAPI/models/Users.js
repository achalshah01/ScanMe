const mongoose = require("mongoose");

const UserSchema = mongoose.Schema({
	firstName: {
		type: String,
		required: true
	},
	lastName: {
		type: String,
		required: true
	},
	email: {
		type: String,
		required: true,
		unique: true
	},
	word: {
		type: String,
		required: true
	},
	passwordUser: {
		type: String,
		required: true
	},
	gender: {
		type: String,
		required: true
	},
	city: {
		type: String,
		required: true
	},
	phone: {
		type: Number,
		required: true
	}
});

module.exports = mongoose.model("user", UserSchema);
