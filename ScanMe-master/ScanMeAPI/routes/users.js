const express = require("express");
const router = express.Router();
const User = require("../models/Users");
//const { check, validationResult } = require("express-validator");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const config = require("config");
const auth = require("../middleware/auth");

router.post(
	"/getOne",

	(req, res) => {
		const { email, password } = req.body;

		User.findOne({ email, word: password }, (err, post) => {
			if (post) {
				return res.send({ user: post });
			} else {
				return res.send({ msg: "nodata" });
			}
		});
	}
);

router.post("/", (req, res) => {
	const {
		firstName,
		lastName,
		gender,
		city,
		email,
		phone,
		password
	} = req.body;

	User.findOne({ email }, (err, post) => {
		if (err) {
			return err;
		}
		if (post) {
			return res.status(400).json({ msg: "User already exists" });
		} else {
			user = new User({
				firstName,
				lastName,
				email,
				word: password,
				gender,
				city,
				phone
			});
			bcrypt.genSalt(10, (err, salt) => {
				bcrypt.hash("12345", salt, (err, hash) => {
					user.passwordUser = hash;

					user.save(err => {
						//return res.send("user saves");
						const payload = {
							user: {
								id: user.id
							}
						};
						jwt.sign(
							payload,
							config.get("jewSecret"),
							{
								expiresIn: 360000
							},
							(error, token) => {
								if (error) throw err;
								res.json({ id: user.id });
							}
						);
					});

					//return res.send(err);
				});
			});
		}
	});
});

router.put("/", auth, (req, res) => {
	if (req) {
		const { payToken } = req.body;

		User.findByIdAndUpdate(
			{ _id: req.user.id },
			{ $set: { payToken: payToken } },
			(err, itemList) => {
				if (itemList) {
					return res.json(itemList);
				} else {
					return res.send(err);
				}
			}
		);
	}
});

module.exports = router;
