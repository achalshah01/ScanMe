const mongoose = require("mongoose");
const Items = mongoose.Schema({
	userId: {
		type: String,
		required: true
	},
	contactId: {
		type: String,
		required: true
	},
	dateAdd: {
		type: Date,
		default: Date.now
	}
});

module.exports = mongoose.model("contact", Items);
