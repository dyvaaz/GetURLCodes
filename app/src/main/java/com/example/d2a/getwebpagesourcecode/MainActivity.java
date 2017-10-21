package com.example.d2a.getwebpagesourcecode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    TextView result_HTML;
    Spinner spin;
    EditText text_url;
    ArrayAdapter<CharSequence> list_spinner;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spin = (Spinner) findViewById(R.id.spinner_protokol);
        text_url = (EditText) findViewById(R.id.input_link);
        result_HTML = (TextView) findViewById(R.id.isi_HTML);
        loading = (ProgressBar) findViewById(R.id.progress_loading);

        list_spinner = ArrayAdapter.createFromResource(this, R.array.protokol, android.R.layout.simple_spinner_item);
        list_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(list_spinner);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage());
            }
        });

        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Really Exit")
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void coba(View view) {
        String link_url, protokol, url;
        protokol = spin.getSelectedItem().toString();
        url = text_url.getText().toString();

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        if (!url.isEmpty()) {
            if (url.contains(".") && !(url.contains(" "))) {
                if (checkConnection()) {
                    result_HTML.setText("");
                    loading.setVisibility(View.VISIBLE);

                    link_url = protokol + url;

                    Bundle bundle = new Bundle();
                    bundle.putString("url_link", link_url);
                    getSupportLoaderManager().restartLoader(0, bundle, this);

                } else {
                    Toast.makeText(this, "check your internet connection", Toast.LENGTH_SHORT).show();
                    result_HTML.setText("No Internet Connection");

                }
            } else {
                result_HTML.setText("Invalid URL");

            }

        } else {
            result_HTML.setText("URL can\'t empty");
        }


    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new GetHTMLSource(this, args.getString("url_link"));
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        loading.setVisibility(View.GONE);
        result_HTML.setText(data);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
