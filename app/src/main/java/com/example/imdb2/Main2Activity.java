package com.example.imdb2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main2Activity extends AppCompatActivity {

    LinearLayout linearLayout;

    int i = 0;                                      // String Array's index
    String[] str = new String[100];                 // Array of Strings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        try {
            String url = MainActivity.getMyString(), url2 = "";

            int i=0;

            while(i<url.length())
            {
                if(url.charAt(i)=='$')
                {
                    Log.i("momo",url2);
                    Main2Activity.DownloadTask task2 = new Main2Activity.DownloadTask();
                    task2.execute(url2);
                    url2 = "";

                }
                else
                    url2 += Character.toString(url.charAt(i));
                i++;
            }


        }catch(Exception ex){
            Toast.makeText(getApplicationContext(),"Invalid Entry", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
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
                        .build();

                Call call = client.newCall(request);

                Response response = call.execute();

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
            Log.i("info", result);

            //textView2.setText(result);
            String movieInfo = "";
            String title = "";
            String content = "";

            try {
                JSONObject jsonObject = new JSONObject(result);

                Log.i("info", "lol");
                title = "<strong>" + jsonObject.getString("Title") + "</strong>";

                //x-------------------x----------------------------    RUNTIME     -----------------x---------------------------x------------------------x

                String runtime = "";
                try {
                    runtime = jsonObject.getString("Runtime");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!runtime.equals("") && !runtime.equals("N/A"))
                    content += "<br><br><strong>Runtime : &nbsp;</strong>" + runtime;

                //x-------------------x----------------------------     RATED     -----------------x---------------------------x------------------------x

                String rated = "";
                try {
                    rated = jsonObject.getString("Rated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!rated.equals("") && !rated.equals("N/A"))
                    content += "<br><br><strong>Rated : &nbsp;</strong>" + rated;

                //x-------------------x----------------------------     GENRE     -----------------x---------------------------x------------------------x

                String genre = "";
                try {
                    genre = jsonObject.getString("Genre");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!genre.equals("") && !genre.equals("N/A"))
                    content += "<br><br><strong>Genre : &nbsp;</strong>" + genre;

                //x-------------------x----------------------------    RELEASED     -----------------x---------------------------x------------------------x

                String released = "";
                try {
                    released = jsonObject.getString("Released");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!released.equals("") && !released.equals("N/A"))
                    content += "<br><br><strong>Released : &nbsp;</strong>" + released;

                //x-------------------x----------------------------     RATINGS     -----------------x---------------------------x------------------------x

                String rate = "", sauce = "", value = "", val2 = "";
                int val = 0;
                try {
                    rate = jsonObject.getString("Ratings");
                    JSONArray arr = new JSONArray(rate);

                    for (int i = 0; i < Math.min(2, arr.length()); i++) {
                        JSONObject jsonPart = arr.getJSONObject(i);

                        try {
                            sauce = jsonPart.getString("Source");
                            value = jsonPart.getString("Value");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!sauce.equals("") && !sauce.equals("N/A")) {
                            if (val == 0) {
                                int j = 0;
                                while (value.charAt(j) != '/') {
                                    val2 += Character.toString(value.charAt(j));
                                    j++;
                                }
                                content += "<br><br><strong>Rating : </strong> <u>&nbsp;IMDb</u> - " + val2;
                                val = 1;
                            } else {
                                content += "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<u>Rotten Tomatoes</u> - " + value;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //x-------------------x----------------------------     PLOT     -----------------x---------------------------x------------------------x

                String plot = "";
                try {
                    plot = jsonObject.getString("Plot");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!plot.equals("") && !plot.equals("N/A"))
                    content += "<br><br>" + "<strong>" + "\n\nPlot : &nbsp;" + "</strong>" + plot;

                if (content != "") {
                    TextView textView = new TextView(getApplicationContext());
                    linearLayout.addView(textView);

                    ImageView imageView = new ImageView(getApplicationContext());
                    linearLayout.addView(imageView);


                    textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


                    TextView textView2 = new TextView(getApplicationContext());

                    linearLayout.addView(textView2);

                    String url2 = jsonObject.getString("Poster");
                    Log.i("mymy", url2);

                    textView.setText(Html.fromHtml(title));

                    textView2.setText(Html.fromHtml(content));

                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                    textView.setTextColor(Color.parseColor("#ff000000"));
                    textView2.setTextColor(Color.parseColor("#ff000000"));

                    if (!url2.equals("") && !url2.equals("N/A")) {
                        new ImageLoadTask(url2, imageView).execute();
                        imageView.setAdjustViewBounds(true);
                        imageView.setPadding(100, 70, 100, 50);
                    }
                    textView2.setPadding(0,0,0,90);
                    //content += "\nPlot : " +  + "\n\nRuntime : " + jsonObject.getString("Runtime");//+ "\n\nRating : " + jsonObject.getString("rating") + "\n\nRating Votes : " + jsonObject.getString("rating_votes") + "\n";
                    Log.i("info11", title);

                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //x----------------------------x-----------------------x--------------------------------x---------------------------x--------------------------x

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            try {
                this.url = url;
                this.imageView = imageView;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                Log.i("my","1");
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                Log.i("my","2");
                InputStream input = connection.getInputStream();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;

                Bitmap bp = BitmapFactory.decodeStream(input,null, options);

                Log.i("my","4");
                return bp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            try {
                imageView.setImageBitmap(result);
                imageView.setAlpha((float) 1.0);
            }catch(Exception ed){
                ed.printStackTrace();
            }
        }
    }
}