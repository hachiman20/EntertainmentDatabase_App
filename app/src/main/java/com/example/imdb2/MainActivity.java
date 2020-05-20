package com.example.imdb2;
import java.lang.*;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.OkHttpClient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import okhttp3.Request;
import okhttp3.Response;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText editText;
    int flag = 0;
    static String sendId = "";

    protected static String getMyString(){
        return sendId;
    }

    public void movieName(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
        if(flag==0) {
            flag = 1;
            DownloadTask task = new DownloadTask();
            task.execute("https://imdb-internet-movie-database-unofficial.p.rapidapi.com/search/"+editText.getText().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        //imageView2 = (ImageView) findViewById(R.id.imageView2);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground (String...urls) {
            Log.i("lol","dsd");
            String result = "";
            URL url;
            Log.i("info", "2");
            try {
                url = new URL(urls[0]);

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("x-rapidapi-host", "imdb-internet-movie-database-unofficial.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "674b9c5c4fmshcb727fcf149ee9fp1f6410jsncbb1f4c0b818")
                        .build();

                Call call = client.newCall(request);

                Response response = call.execute();

                Log.i("lol3","Hii");

                result = response.body().string();

                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.i("info21", result);

            //textView2.setText(result);
            String movieInfo = "";
            String plot = "";

            try {
                JSONObject jsonObject = new JSONObject(result);

                    String movie_titles = jsonObject.getString("titles");
                    Log.i("info5", movie_titles);

                    JSONArray arr = new JSONArray(movie_titles);

                    sendId = "";
                    for(int i=0;i<arr.length();i++)
                    {
                        JSONObject jsonPart = arr.getJSONObject(i);

                        sendId += "http://www.omdbapi.com/?i="+jsonPart.getString("id") + "&apikey=7834e626" + "$";

                    }

                    getMyString();

                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    //intent.putExtra("name", flag);
                    startActivity(intent);
                    flag = 0;


            } catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find Entry", Toast.LENGTH_LONG).show();
            }
        }
    }
}