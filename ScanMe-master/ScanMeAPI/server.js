const express = require("express");
const app = express();
const connectDB = require("./config/db");

connectDB();
app.get("/", (req, res) => {
	res.json("hello");
});

app.use(express.json({ extended: false }));

app.use("/api/users", require("./routes/users"));
//app.use("/api/auth", require("./routes/auth"));
app.use("/api/contact", require("./routes/contact"));

const port = process.env.PORT || 3000;
app.listen(port, () => {
	console.log(`server started on ${port}`);
});
