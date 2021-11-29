package com.example.androidride.webviewbrowser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.view.inputmethod.InputMethodManager;

import android.widget.Toast;
import android.view.KeyEvent;


public class MainActivity extends AppCompatActivity
{
private Toolbar toolbar;
private EditText edittext;
private WebView webview;
//private ImageView imageview;
private String Share_url,Title_url;
private ProgressBar progressbar;
String filename,directoryName;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialise();
        webview.loadUrl("file:///android_asset/demo.html");
        setSupportActionBar(toolbar);
        webview.getSettings().setJavaScriptEnabled(true);

        webview.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressbar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);

                if(newProgress==100)
                {
                progressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                //set favicon to imageview
                //imageview.setImageBitmap(icon);
            }
        });

        webview.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                progressbar.setVisibility(View.VISIBLE);

                if(!"file:///android_asset/demo.html".equals(url))
                {
                edittext.setText(url);
                }
                else
                {
                edittext.setText("");
                }
                
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view,String url)
            {
                Share_url=url;
                super.onPageFinished(view,url);
            }
            

           
        });

        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, final String userAgent, String contentDisposition, String mimetype, long contentLength)
            {
             //checking runtime permissions  
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        
                        downloadDialog(url,userAgent,contentDisposition,mimetype);

                    } else {
                        
                        //requesting permissions
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                }
                else {
                    //Code for devices below API 23 or Marshmallow
                    downloadDialog(url,userAgent,contentDisposition,mimetype);

                }
               
            }
        });
    }
    @Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if((keyCode==KeyEvent.KEYCODE_BACK)&&this.webview.canGoBack())
    {
        webview.goBack();
        return true;
    }

    return super.onKeyDown(keyCode, event);
}

    public void goBack(View view)
    {
        if(webview.canGoBack())
            webview.goBack();
    }

    public void goForward(View view)
    {
        if(webview.canGoForward())
            webview.goForward();
    }

    public void goHome(View view)
    {
                webview.loadUrl("file:///android_asset/demo.html");
        }

    public void refresh(View view)
    {
        webview.reload();
    }

    public void share(View view)
    {
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,Share_url);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"URL");
        startActivity(Intent.createChooser(shareIntent,"Share with your friends"));
    }
    public void go(View view)
    {
        String text=edittext.getText().toString();
        searchOrLoad(text);
    }

    void searchOrLoad(String txt)
    {
        if(Patterns.WEB_URL.matcher(txt.toLowerCase()).matches())
        {

            if(txt.contains("http://")||txt.contains("https://"))
            {
                webview.loadUrl(txt);
            }
            else
            {
                webview.loadUrl("http://"+txt);
            }
        }
        else
        {
            webview.loadUrl("https://www.google.com/search?q="+txt);
        }
        hideKeyboard();

    }

    private void initialise()
    {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        edittext=(EditText)findViewById(R.id.search_load_edit_text);
        webview=(WebView)findViewById(R.id.webview);
        progressbar=(ProgressBar)findViewById(R.id.progressbar);
        //imageview=(ImageView)findViewById(R.id.toolbar_search_imageview_favicon);
    }

    public void hideKeyboard()
    {
        InputMethodManager inputMethodManager= (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view=getCurrentFocus();
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
    
    public void downloadDialog(final String url,final String userAgent,String contentDisposition,String mimetype)
    {
        //file name
        final String filename = URLUtil.guessFileName(url,contentDisposition,mimetype);

        //Creates AlertDialog.
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        //title of Dialog
        builder.setTitle("Download");
        //Message of Dialog.
        builder.setMessage("Do you want to save " +filename);

            //if YES button clicks
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                   //DownloadManager.Request created with url.
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                   //cookie
                String cookie=CookieManager.getInstance().getCookie(url);
                   //Add cookie and User-Agent to request
                request.addRequestHeader("Cookie",cookie);
                request.addRequestHeader("User-Agent",userAgent);

                   //file scanned by MediaScannar
                request.allowScanningByMediaScanner();
                //Download is visible and its progress, after completion too.
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                //DownloadManager created
                DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                //Saving file in Download folder
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                //download enqued
                downloadManager.enqueue(request);
            }
        });
        //If Cancel button clicks
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) 
            {
            //cancel the dialog if Cancel clicks        
             dialog.cancel();
            }

        });

        //Shows alertdialog
        builder.create().show();

    }

    }
