package com.example.honsulbakseo;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

public class RecipeWriteFragment extends Fragment {
    EditText alchol_name, needs_alchol, recipe_alchol, feature_alchol;
    Spinner main_alchol;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_write, container, false);

        alchol_name = (EditText)view.findViewById(R.id.alchol_name);
        needs_alchol = (EditText)view.findViewById(R.id.needs_alchol);
        recipe_alchol = (EditText)view.findViewById(R.id.recipe_alchol);
        feature_alchol = (EditText)view.findViewById(R.id.feature_alchol);
        main_alchol = (Spinner)view.findViewById(R.id.main_alchol);

        Button cancel_btn = (Button)view.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.frame, new RecipeBoardFragment()).commit();
            }
        });

        Button ok_btn = (Button)view.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = alchol_name.getText().toString();
                String main = main_alchol.getSelectedItem().toString();
                String needs = needs_alchol.getText().toString();
                String recipe = recipe_alchol.getText().toString();
                String feature = feature_alchol.getText().toString();

                new JSONTask().execute("http://192.168.0.5:3000/recipe", name, main, needs, recipe, feature);

                Log.v("Log alchol_name", name);
            }
        });

        return view;
    }

    public class JSONTask extends AsyncTask<String, String, String>  {
        String[] schema = {"alchol_name", "main_alchol", "needs", "recipe", "feature"};
        @Override
        protected String doInBackground(String... args) {
            try{
                JSONObject jsonObject = new JSONObject();

                for (int i=1; i<args.length; i++){
                    jsonObject.accumulate(schema[i-1], args[i]);
                }

                Log.v("server", jsonObject.toString());

                HttpURLConnection conn = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(args[0]);
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "application.json");
                    conn.setRequestProperty("Accept", "test/html");

                    conn.setDoInput(true);
                    conn.setDoInput(true);
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    writer.write(jsonObject.toString());

                    // flussh 호출 전까지 스트림에 출력되지 않고 버퍼 대기기
                    writer.flush();
                    writer.close();

                    InputStream inputStream = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String result = buffer.toString();

                    return result;
                }catch (MalformedURLException e){
                    e.printStackTrace();

                }catch(IOException e){
                    e.printStackTrace();
                }finally {
                    if(conn != null){
                        conn.disconnect();
                    }try{
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.compareTo("success_recipe")==0){
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.frame, new RecipeBoardFragment()).commit();
            }
        }
    }

}
