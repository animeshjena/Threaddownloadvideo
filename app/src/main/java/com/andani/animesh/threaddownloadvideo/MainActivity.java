package com.andani.animesh.threaddownloadvideo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener
{
    private ListView items;
    private String[] urls;
    private LinearLayout loadingsec;
    private EditText editText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items=(ListView)findViewById(R.id.itemlist);
        urls=getResources().getStringArray(R.array.url_address);
        loadingsec=(LinearLayout)findViewById(R.id.loading_section);
        editText=(EditText)findViewById(R.id.maintext);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        items.setOnItemClickListener(this);
    }
    public void Download_image(View view)
    {
        String text= editText.getText().toString();
        Thread myThread=new Thread(new Downloadthread(text));
        myThread.start();


    }
    public boolean downloadurlimage(String urldata)
    {
        boolean success=false;
        URL url=null;
        HttpURLConnection urlconnection=null;
        InputStream inputStream=null;
        File file=null;
        FileOutputStream fileOutputStream=null;
        try {
            url=new URL(urldata);
            urlconnection= (HttpURLConnection) url.openConnection();
            inputStream=urlconnection.getInputStream();

            file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+Uri.parse(urldata).getLastPathSegment());

            fileOutputStream=new FileOutputStream(file);

            int read=-1;
            byte[] buffer=new byte[1024];
            while((read=inputStream.read(buffer))!=-1)
            {
                //Log.d("ani","input");
                fileOutputStream.write(buffer,0,read);

            }
            success=true;
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
        finally {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingsec.setVisibility(View.GONE);
                }
            });
            if (urlconnection!=null)
            {
                urlconnection.disconnect();
            }
            if(inputStream!=null)
            {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream!=null)
            {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        editText.setText(urls[position]);
    }


    private  class Downloadthread implements Runnable
    {
        private String text;
        public Downloadthread(String text)
        {
            this.text=text;
        }
        @Override
        public void run()
        {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingsec.setVisibility(View.VISIBLE);
                }
            });
            downloadurlimage(text);
        }
    }

}
