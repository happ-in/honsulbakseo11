var mysql = require('mysql');

var conn = mysql.createPool({
    host : 'localhost',
    user : 'root',
    password : '',
    port : 3306,
    database : 'login'
});

module.exports = mysql.model('user', conn);