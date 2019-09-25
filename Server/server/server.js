var mysql = require('mysql');
const express = require('express');
const app = express();

//mysql information for connection
var conn = mysql.createPool({
    host : 'localhost',
    user : 'root',
    password : '',
    port : 3306,
    database : 'login'
});

// get : 서버에 정보를 요청할때
app.get('/user', function(req, res, next){

    // get req value ID, PW
    var userID = req.query.id;
    var userPW = req.query.pw;

   if(userID && userPW){
       conn.getConnection(function (err, connection) {
           if(err){
               console.log("error : ", err);
           }
           else{
               connection.query('select * from login id=?', [userID], function (err, rows, fields) {
                   if(err){
                       console.log("error : ", err);
                   }
                   else{
                       if(rows==""){
                           console.log("No data");
                           res.send("No data")
                           return ;
                       }

                       // pw는 req의 파라미터 값으로 가져온 sql 실행문의 pw값 이다.
                       var pw = row[0].userPW;
                       if(pw!= userPW){
                           console.log("Password Missmatch");
                           res.send("Password Missmatch");
                       }
                       else{
                           console.log("Login Success");
                           res.send("Login Success");
                       }
                   }
               });
           }
           connection.release();
       });
   }
});


// post : 서버에 클라이언트가 정보를 전달할 때
app.post('/login', function(req, res, next){
    var inputData;

    // req.on('data') 메소드를 사용하여 post 데이터가 들어오면 data를 사용하여 저장
    req.on('data', function(data){
        // JSON.parse(data) : 인수로 전달받은 문자열을 자바스크립트 객체로 변환하여 반환
       inputData = JSON.parse(data);
    });

    // 더 이상 데이터가 들어오지 않는다면 end 이벤트가 실행
    req.on('end', function(){
        var userID = inputData.userID;
        var userPW = inputData.userPW;

        if(userID && userPW){
            conn.getConnection(function (err, connection) {
                if(err){
                    console.log("err : ", err);
                }
                else{
                    connection.query("select * from login where id=?", [userID], function(err, rows, fields){
                        if(err){
                            res.write("fail", function(err){
                               console.log("send fail");
                               res.end();
                            });
                        }

                        else{
                            var pw ="";
                            for(var i in rows){
                                pw = rows[i].pw;

                                if(pw != userPW){
                                    console.log("Password Missmatch");
                                    res.write("fail", function(err){
                                        console.log("fail");
                                        res.end();
                                    });
                                }

                                else{
                                    console.log("Correct");
                                    res.write("success", function(err){
                                        console.log("success");
                                        res.end();
                                    });
                                }
                            }
                        }
                    });
                }
                connection.release();
            });
        }
    });
});

app.post('/regist', function (req, res, err) {
    var inputData;

    req.on('data', function(data){
        inputData = JSON.parse(data);
    });

    req.on('end', function(){
       var userID = inputData.userID;
       var userPW = inputData.userPW;

       if(userID && userPW){
           conn.getConnection(function (err, connection) {
               if(err){
                   console.log("err : ",err);
               }
               else{
                   connection.query('insert into login values(?,?)', [userID, userPW], function (err, rows, fields) {
                      if(err){
                          res.write("fail", function(err){
                             console.log("send fail");
                             res.end();
                          });
                      }

                      else{
                          console.log("Correct");
                          res.write("success", function(err) {
                              console.log("success");
                              res.end();
                          });
                      }
                   });
               }
           })
       }
    });
});

app.listen(3000, ()=>{
   console.log("Server running on 3000...");
});