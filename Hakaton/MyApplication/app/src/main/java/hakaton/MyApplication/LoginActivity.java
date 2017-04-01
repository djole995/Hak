package hakaton.MyApplication;

import android.net.Network;
import android.net.http.SslCertificate;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import static java.sql.Types.NULL;

public class LoginActivity extends AppCompatActivity {

    EditText textUsername;
    EditText textPassword;
    Button btnLogin;
    JSONObject jsonObject;
    HttpURLConnection connetion;

    URL url;
    HttpURLConnection client;

    InetAddress serverAddr;

    boolean postFinished;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.buttonLogin2);
        textUsername = (EditText) findViewById(R.id.userName);
        textPassword = (EditText) findViewById(R.id.password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = textUsername.getText().toString();
                String password = textPassword.getText().toString();

                if(username.isEmpty()) {
                    Toast badInputNotify = Toast.makeText(getApplicationContext(),
                            R.string.bad_username_input, Toast.LENGTH_SHORT);
                    badInputNotify.setGravity(Gravity.BOTTOM, 0, 20);
                    badInputNotify.show();
                    return;
                }
                else if(password.isEmpty()) {
                    Toast badInputNotify = Toast.makeText(getApplicationContext(),
                            R.string.bad_password_input, Toast.LENGTH_SHORT);
                    badInputNotify.setGravity(Gravity.BOTTOM, 0, 20);
                    badInputNotify.show();
                    return;
                }

                // Invoking put
                String[] data = new String[3];
                data[0] = getString(R.string.login_url);
                data[1] = textUsername.getText().toString();
                data[2] = textPassword.getText().toString();

                String ret;

                postFinished = false;
                new PostRequest().execute(data);
                new GetRequest().execute(data);

            }

        });
    }

    private class GetRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            while(!postFinished) {
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String ret = "SUCCESS";
            String jsonString;
            URL url = null;
            BufferedReader in = null;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedReader(new InputStreamReader(url.openStream()));

                char[] buffer = new char[1024];

                jsonString = new String();

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line + "\n");
                }

                in.close();

                jsonString = sb.toString();

                if(jsonString.toString().isEmpty()) {
                    Log.d("GetRequest", "Get request failed!");
                }
                else {
                    Log.d("GetRequest", jsonString);
                }

            } catch (Exception e) {
                e.printStackTrace();
                ret = "FAILED";
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (urlConnection != null) {
                    try {
                        urlConnection.disconnect();
                    }
                    catch (Exception e) {
                        ret = "FAILED";
                        e.printStackTrace();
                    }
                }
            }

            return ret;
        }
    }

    private class PostRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String ...args) {
            String ret = "SUCCESS";
            URL url;
            HttpURLConnection urlConnection = null;
            BufferedWriter writer = null;
            BufferedReader reader = null;


            try {
                url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                JSONObject jsonObject = new JSONObject();



                String userName = args[1];
                String password = args[2];

                jsonObject.put("user", userName);
                jsonObject.put("pass", password);

                Log.d("PUT_REQUEST", jsonObject.toString());

                writer = new BufferedWriter(
                        new OutputStreamWriter(
                                urlConnection.getOutputStream(), "UTF-8"));

                writer.write(jsonObject.toString());

                writer.close();

                String responseMessage = urlConnection.getResponseMessage();

              /*  if(responseMessage == "-1") {
                    Log.d("Post", "Invalid username");
                    return null;
                }*/




                /* TODO */
                /*Open post login activity */

                //Log.d("POST_REQUEST", responseMessage);

                /*reader = new BufferedReader(
                        new InputStreamReader(
                                url.openStream()));

                StringBuilder sb = new StringBuilder();
                String jsonString = new String();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                jsonString = sb.toString();

                Log.d("POST_REQUEST", jsonString);*/

            }
            catch (Exception e) {
                e.printStackTrace();
                ret = "FAILED";
            }
            finally {
                    try {
                        if(urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ret = "FAILED";
                    }
            }

            postFinished = true;
            return ret;
        }
    }
}


