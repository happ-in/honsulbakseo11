package com.example.honsulbakseo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegistActivity extends AppCompatActivity {

    EditText userID, userPW, userREPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        userID = (EditText)findViewById(R.id.regist_ID);
        userPW = (EditText)findViewById(R.id.password);
        userREPW = (EditText)findViewById(R.id.repassword);

        Button cancel_btn = (Button) findViewById(R.id.cancel);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Button ok_btn = (Button) findViewById(R.id.okbtn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = userID.getText().toString();
                String pw = userPW.getText().toString();
                String repw = userREPW.getText().toString();

                if(pw.compareTo(repw)==0){
                    new JSONTask2().execute("http://192.168.0.2:3000/regist", id, pw);
                } else{
                    String msg = "비밀번호가 일치하지 않습니다.";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class JSONTask2 extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("userID", args[1]);
                jsonObject.accumulate("userPW", args[2]);

                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try {

                    /* 서버연결 */
                    URL url = new URL(args[0]);
                    conn = (HttpURLConnection) url.openConnection();

//                    conn.setReadTimeout(100000); // no action for 10 sec then error
//                    conn.setConnectTimeout(15000); // no connect for 15sec then it does not work and disconnect

                    // Setting control cache
                    // Cache-Control ?
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "application.json");
                    conn.setRequestProperty("Accept", "test/html");

                    // android could get and post sth from server
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();


                    /* Android -> Server 파라미터 값 전달 */
                    OutputStream outStream = conn.getOutputStream();
                    // create buffer and then write data
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    Log.v("Server", "server");

                    /* Server -> Android */
                    InputStream inputStream = conn.getInputStream(); // 입력 스트림 생성
                    reader = new BufferedReader(new InputStreamReader(inputStream)); // 속도를 향상시키고 부하를 줄이기 위한 버퍼 선언

                    Log.v("Server", "Init StringBuffer");
                    StringBuffer buffer = new StringBuffer(); // 실제 데이터를 받는 버퍼

                    Log.v("server : ", "Line Attach");
                    String line = ""; // line별 stream 을 받기 위한 tmp 변수
                    // 실제 reader node.js 서버에서 데이터를 가져온다.
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String result = buffer.toString();

                    if (result.compareTo("success") == 0) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }

                    return result;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // 종료되면 연결을 disconnect 하고 버퍼를 닫아준다.
                    if (conn != null) {
                        conn.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
