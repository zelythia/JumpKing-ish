const mysql = require('mysql'); 
const express = require('express');
const { host, user, password, database} = require('./config.json');

const app = express();
const PORT = 3000;

const db = mysql.createConnection({
    host: host,
    user: user,
    password: password,
    database: database
});
db.connect(function(err) {
    if (err) throw err;
    console.log("Connected!");
});

app.use(express.json());
app.listen(
    PORT,
    () => console.log("listening on localhost:3000")
);


app.get('/jkapi/test', (req, res) => {
    res.status(200).send();
});


app.get('/jkapi/top', (req, result) => {
    
    db.query(`SELECT * FROM jumpkingish WHERE 1 ORDER BY (1000*score)/time DESC LIMIT 100`, (err, res, fields) => {
        if(err){
            result.status(400).send();
            return;
        }

        var data = {"top": []};
        for(var d of res){
            data.top.push(d);
        }<

        result.status(200).send(data);
    });
});  


app.get('/jkapi/top/:offset', (req, result) => {
    db.query(`SELECT * FROM jumpkingish WHERE 1 ORDER BY (1000*score)/time DESC LIMIT 100 OFFSET ${req.params.offset}`, (err, res, fields) => {
        if(err){
            result.status(400).send();
            return;
        }

        var data = {"top": []};
        for(var d of res){
            data.top.push(d);
        }

        result.status(200).send(data);
    });
});

app.post("/jkapi/add/:name", (req, res) => {
    db.query(`INSERT INTO jumpkingish (name, score, time) VALUES ('${req.params.name}', '${req.body.score}', '${req.body.time}');`, (err, r, fields) => {
        if(err){
            console.log(err);
            res.status(400).send();
            return;
        }

        res.status(200).send();
    });
});

/*

final JList<String> list = new JList<String>(array);

JScrollPane scrollPane = new JScrollPane();
scrollPane.setViewportView(list);
list.setLayoutOrientation(JList.VERTICAL);



*/