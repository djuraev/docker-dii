// Sample node.js web app for Pluralsight Docker CI course
// For demonstration purposes only
'use strict';

var express = require('express'),
    app = express();

app.set('views', 'views');
app.set('view engine', 'pug');
app.use(express.json());

app.get('/', function(req, res) {
    res.render('home', {
  });
});

app.post('/log', function(req, res) {
    var ts = new Date().toISOString();
    var entry = req.body || {};
    console.log('[' + ts + '] ' + (entry.message || JSON.stringify(entry)));
    res.status(204).end();
});

app.listen(8080);
module.exports.getApp = app;
