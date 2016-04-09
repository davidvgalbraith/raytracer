var express = require('express');
var Promise = require('bluebird');
var fs = Promise.promisifyAll(require('fs'));
var path = require('path');

var app = express();
var SERVER_PORT = 12345;

function crossDomain(req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    next();
}

app.use(crossDomain);

app.get('/list', function(req, res, next) {
    return fs.readdirAsync(path.resolve(__dirname, '../input'))
        .then(function(files) {
            res.send(JSON.stringify(files));
        })
        .catch(function(err) {
            console.error(err, 'there was a fire');
            next(err);
        });
});

app.get('/:filename', function(req, res, next) {
    var filepath = path.resolve(__dirname, '../input', req.params.filename);
    if (!filepath.endsWith('.json')) { filepath += '.json'; }
    return fs.readFileAsync(filepath)
        .then(function(scene) {
            res.send(scene);
        })
        .catch(function(err) {
            return next(err);
        });
});

app.listen(SERVER_PORT, function() {
    console.log('input server listening');
});
