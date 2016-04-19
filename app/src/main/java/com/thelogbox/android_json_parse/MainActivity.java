package com.thelogbox.android_json_parse;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

interface MyCallBack {
  void onDownloadCompleted(String webdata);
}

public class MainActivity extends AppCompatActivity implements MyCallBack {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button btnclick = (Button) findViewById(R.id.btnClick);
    assert btnclick != null;
    btnclick.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        processWebData();
      }
    });


  }

  private String loadData() {

    String jsonString = "{\"item\":{\"name\":\"John Doe\",\"numbers\":[{\"id\":\"1\"},{\"id\":\"2\"}]}}";
    return jsonString;

  }

  public void onDownloadCompleted(String webdata) {
    //System.out.println(webdata);
    loadWebData(webdata);
  }

  private void loadWebData(String webdata) {
    try {
      JSONObject obj = new JSONObject(webdata);
      JSONObject responsedata = obj.getJSONObject("responseData");
      JSONArray array = responsedata.getJSONArray("results");

      for(int i = 0; i < array.length(); i++) {
        System.out.println(array.getJSONObject(i).getString("url"));
      }


    }
    catch(JSONException joe) {
      joe.printStackTrace();
    }
  }

  private void processWebData() {
    new WebTask(MainActivity.this, this).execute();
  }

  public void processData() {

    TextView tv = (TextView) findViewById(R.id.textView);

    StringBuilder sb = new StringBuilder();

    try {
      JSONObject obj = new JSONObject(loadData());
      JSONObject item = obj.getJSONObject("item");

      String name = item.getString("name");
      JSONArray num = item.getJSONArray("numbers");
      sb.append(name);
      sb.append("\n");
      for(int i = 0; i < num.length(); i++) {
        sb.append("\t");
        sb.append(num.getJSONObject(i).getString("id"));
        sb.append("\n");
      }

      System.out.println(sb.toString());
      tv.setText(sb.toString());

    }
    catch(JSONException joe) {
      joe.printStackTrace();
    }
  }

  /*inner classes*/

  class WebTask extends AsyncTask<Void, Void, String> {

    MyCallBack mycallback;
    Context ctx;

    public WebTask(Context ctx, MyCallBack mycallback) {
      this.ctx = ctx;
      this.mycallback = mycallback;
    }

    @Override
    protected String doInBackground(Void... params) {
      String returnValue = "";
      String googleUrl = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
      StringBuilder sb = new StringBuilder();

      try {
        String search = URLEncoder.encode("android programming","UTF-8");
        URL url = new URL(googleUrl + search);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        BufferedReader buf = new BufferedReader(isr, 1024*8);

        String strline = null;
        while((strline = buf.readLine()) != null) {
          sb.append(strline);
        }
      }
      catch(MalformedURLException mal) {
        mal.printStackTrace();
      }
      catch(IOException ioe) {
        ioe.printStackTrace();
      }

      return sb.toString();
    }

    @Override
    protected void onPostExecute(String aString) {
      mycallback.onDownloadCompleted(aString);
    }
  }

}
