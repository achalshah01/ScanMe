const express = require("express");
const router = express.Router();
const User = require("../models/Users");
const Contact = require("../models/Contacts");
const Saved = require("../models/Saved");

//const { check, validationResult } = require("express-validator");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const config = require("config");
const auth = require("../middleware/auth");

const ArrayList = require("arraylist");

router.post("/add", (req, res) => {
	const { userId, contactId } = req.body;

	User.findById({ _id: contactId }, (err, post) => {
		if (err) {
			return err;
		}
		if (post) {
			contact = new Contact({
				userId,
				contactId
			});
			saved=new Saved({
				userId,
				contactId
			})
			saved.save(err=>{

			})
			contact.save(err => {
				//return res.send("user saves");
				res.json("added");
			});
		} else {
		}
	});
});

router.post("/getall", (req, res) => {
	const { contactId } = req.body;
	var con = new ArrayList();
	var conList = new ArrayList();
	Contact.find({ contactId }, (err, post) => {
		if (err) {
			return err;
		}
		if (post) {
			post.forEach(contact => {
				con.add(contact.userId);
			});
			res.json(post);
		}
	});
});

router.post("/Addedgetall", (req, res) => {
	const { contactId } = req.body;
	var con = new ArrayList();
	var conList = new ArrayList();
	Saved.find({ userId:contactId }, (err, post) => {
		if (err) {
			return err;
		}
		if (post) {
			post.forEach(contact => {
				con.add(contact.userId);
			});
			res.json(post);
		}
	});
});

router.post("/getOne", (req, res) => {
	const { userId } = req.body;
	var con = new ArrayList();
	var conList = new ArrayList();
	User.findOne({ _id: userId }, (err, post) => {
		if (err) {
			return err;
		}
		if (post) {
			res.json(post);
		}
	});
});

router.post("/delete", (req, res) => {
	const { userId ,flag,contactId} = req.body;
	var con = new ArrayList();
	var conList = new ArrayList();

	if(flag=="contact"){
		Contact.deleteMany({ userId: userId,contactId:contactId }, (err, post) => {
			if (err) {
				return err;
			}
	
			if (post) {
				res.json(post);
			}
		});
	}if(flag=="saved"){
		Saved.deleteMany({ contactId: userId,userId:contactId }, (err, post) => {
			if (err) {
				return err;
			}
	
			if (post) {
				res.json(post);
			}
		});	
	}
	
});

router.post("/update", (req, res) => {
	if (req) {
		const { userId, firstName, lastName, phone, city } = req.body;

		User.findByIdAndUpdate(
			{ _id: userId },
			{ $set: { firstName, lastName, phone, city } },
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
