package org.shadoware.passprotect;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Target;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        load();
    }

    private void load() {
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                String textUrlEncoded = url.replace("data:text/plain;charset=utf-8,", "");
                try {
                    String csv = URLDecoder.decode(textUrlEncoded, "utf-8");
                    downloadFile("passprotect.csv", csv);
                    Toast.makeText(getApplicationContext(), "File is downloaded in passprotect.csv", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Can't download the file", Toast.LENGTH_LONG).show();
                }
            }
        });
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        myWebView.loadUrl("https://passprotect.shadoware.org");
    }


    public void downloadFile(final String filename, String blob) throws IOException {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (mExternalStorageAvailable && mExternalStorageWriteable) {
            File SDCardRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            // create a new file, specifying the path, and the filename
            // which we want to save the file as.
            File file = new File(SDCardRoot, filename);

            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            ostream.write(blob.getBytes());
            ostream.close();
        } else {
            Toast.makeText(this, "External storage not found or not writable", Toast.LENGTH_LONG).show();
        }
    }

}
