package tel.rostel.CheckInOut_Hua;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import tel.rostel.test2.R;

public class MainActivity extends AppCompatActivity {
    public String sss = new String();
    public String username = null;
    public String password = null;
    private String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +"/tel_data/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String [] userdata = new String[2];
        userdata[0] = "none";
        userdata[1] = "none";   
        Gson up_gson = new Gson();
        requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        File txtFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "checkInOutData.txt");
        if(txtFile.exists()) {
            if(isEtxStorWritable()) {
                if(checkPerm(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    try {
                        TextView txt = (TextView) findViewById(R.id.result);
                        TextView usr = (TextView)findViewById(R.id.userName);
                        TextView pwd = (TextView)findViewById(R.id.Password);
                        StringBuffer sb = new StringBuffer();
                        requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


                        FileInputStream fis = new FileInputStream(txtFile);
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader buf = new BufferedReader(isr);
                        String lines;
                        while ((lines = buf.readLine()) != null) {
                            sb.append(lines);
                        }
                        fis.close();
                        PassData upload_data = up_gson.fromJson(String.valueOf(sb), PassData.class);


                        userdata = sb.toString().split("\"");
                        usr.setText(upload_data.userName);
                        pwd.setText(upload_data.pass);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }








    }

    public void chIn (View v) throws IOException {
        String url_login = "https://rugde.teleows.com/cas/login?service=https%3A%2F%2F100p-rugde.teleows.com%3A443%2Fapp%2Fportal%2FloadPortal.action&_validateRequest_=null";




        new MyHttp1()
        {
            @SuppressLint("StaticFieldLeak")
            @Override public void onPostExecute(String result)
            {
                TextView txt = (TextView) findViewById(R.id.result);
                txt.setText(result);
                Gson gson = new Gson();
                String my_res = result;
                String ggg;
                ggg = result.split("\"")[3];
                if(ggg.equals("Check Out Success!") || (ggg.equals("Check In Success!"))) {
                    Toast t = Toast.makeText(getApplicationContext(), ggg, Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0,0);
                    t.show();
                }

            }
        }.execute("");




    }

    public void clearWindow (View v) {
        TextView result1 = (TextView)findViewById(R.id.result);
        Toast.makeText(this, "Хренак", Toast.LENGTH_LONG).show();


        result1.setText("");

    }

    public void showPwd (View v) {
        EditText pwd = (EditText)findViewById(R.id.Password);

        Toast t = Toast.makeText(this, pwd.getText().toString(), Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0,0);
        t.show();


    }

    private boolean isEtxStorWritable() {
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }else{
            return false;
        }
    }

    private boolean checkPerm(String permition) {
        int check = ContextCompat.checkSelfPermission(this, permition);
        return  (check == PackageManager.PERMISSION_GRANTED);
    }

    public void saveData (View view) {

        TextView txt = (TextView) findViewById(R.id.result);
        TextView result1 = (TextView)findViewById(R.id.userName);
        TextView result2 = (TextView)findViewById(R.id.Password);
        Gson gson = new Gson();
        PassData usData = new PassData(result1.getText().toString(), result2.getText().toString());
        String json = gson.toJson(usData);
        requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        if(isEtxStorWritable()) {
            if(checkPerm(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                File txtFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "checkInOutData.txt");

                try {

                    FileOutputStream fos = new FileOutputStream(txtFile);
                    fos.write(json.getBytes());
                    fos.close();

//                    LayoutInflater layoutInflater = getLayoutInflater();
//                    View layout = layoutInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_root));


                    Toast t = Toast.makeText(getApplicationContext(), "Данные сохранены", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast t = Toast.makeText(getApplicationContext(), "Нет разрешения на запись", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
            }
        } else {
            Toast t = Toast.makeText(getApplicationContext(), "Нет разрешения на запись", Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();

        }





    }

    public String [] showData (View v) {
        String [] userdata = new String[2];
        userdata[0] = "none";
        userdata[1] = "none";
        Gson up_gson = new Gson();
        if(isEtxStorWritable()) {
            if(checkPerm(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                try {
                    TextView txt = (TextView) findViewById(R.id.result);
                    TextView usr = (TextView)findViewById(R.id.userName);
                    TextView pwd = (TextView)findViewById(R.id.Password);
                    StringBuffer sb = new StringBuffer();
                    requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    File txtFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "checkInOutData.txt");
                    FileInputStream fis = new FileInputStream(txtFile);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader buf = new BufferedReader(isr);
                    String lines;
                    while ((lines = buf.readLine()) != null) {
                        sb.append(lines);
                    }
                    fis.close();
                    PassData upload_data = up_gson.fromJson(String.valueOf(sb), PassData.class);


                    userdata = sb.toString().split("\"");
                    usr.setText(upload_data.userName);
                    pwd.setText(upload_data.pass);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    return userdata;
    }

    public class MyHttp1 extends AsyncTask<String, Void, String> {


        @Override
        public String doInBackground(String... params) {




            EditText usr = (EditText)findViewById(R.id.userName);
            EditText pas = (EditText)findViewById(R.id.Password);



            username = usr.getText().toString();
            password = pas.getText().toString();




            String str="Пусто";
            try {
                String url_login = "https://rugde.teleows.com/cas/login";
                String url_2N = "https://rugde.teleows.com/cas/login";

                String url_checkin = "https://100p-rugde.teleows.com/app/pageservices/service.do";
                HttpClient httpClient = null;
                final CookieStore cookieStore = new BasicCookieStore();

                HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(cookieStore);
                httpClient = builder.build();

                HttpGet httpGet = new HttpGet(url_login);

                ClassicHttpResponse response = (ClassicHttpResponse)httpClient.execute(httpGet);
                String[] responce_first = EntityUtils.toString(response.getEntity()).split("\n");
//                String bodyN = EntityUtils.toString(response.getEntity());

                String roarand = null;
                String execution = null;
                for (int i = 0; i < responce_first.length;  i++) {
                    if (roarand == null) {
                        if (responce_first[i].contains("roarand")) {
                            roarand = responce_first[i].split("'")[5];
                        }
                    }
                    if (execution == null) {
                        if (responce_first[i].contains("execution")) {
                            execution = responce_first[i].split("\"")[5];
                        }
                    }
                }


                HttpPost httpPostN = new HttpPost(url_login);

                Header[] yyy =  response.getHeaders();
                String cookie_my = null;
                for(int i = 0; i < yyy.length-1; i++) {
                    System.out.println(yyy[i]);
                    if (yyy[i].toString().contains("Set-Cookie: JSESSIONID")) {
                        String hh = yyy[i].toString().split("=")[1];
                        cookie_my = hh.split(";")[0];

                        System.out.println("    !!!Set-Cookie: JSESSIONID: " + cookie_my);



                    }
                }

                cookie_my = "JSESSIONID=" + cookie_my + "; _LOCALE_=ru_RU; locale=ru_RU";




                httpPostN.setHeader("Cookie", cookie_my);
                httpPostN.setHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");

                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                formparams.add(new BasicNameValuePair("username", username));
                formparams.add(new BasicNameValuePair("password", password));
                formparams.add(new BasicNameValuePair("_eventId", "submit"));
                formparams.add(new BasicNameValuePair("pwdfirst", password.substring(0,2)));
                formparams.add(new BasicNameValuePair("pwdsecond", password.substring(2,5)));
                formparams.add(new BasicNameValuePair("pwdthird", password.substring(5, password.toString().length())));
                formparams.add(new BasicNameValuePair("roarand", roarand));
                formparams.add(new BasicNameValuePair("execution", execution));

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, StandardCharsets.UTF_8);
                httpPostN.setEntity(entity);



                response = (ClassicHttpResponse)httpClient.execute(httpPostN);
                String bodyN = EntityUtils.toString(response.getEntity());

                Header[] xxx =  response.getHeaders();


//промежут
                String url_1 = "https://100p-rugde.teleows.com/app/100p/spl/attendance_web/attendance_web_check_in_out.spl";
                HttpGet httpGet_mid = new HttpGet(url_1);
                httpGet_mid.setHeader("Cookie", cookie_my);
                httpGet_mid.setHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");

                ClassicHttpResponse httpResponse_mid = (ClassicHttpResponse)httpClient.execute(httpGet_mid);
                String bodyMid = EntityUtils.toString(httpResponse_mid.getEntity());





//отметка
                HttpPost httpPost_checkin = new HttpPost(url_checkin);
                List<NameValuePair> checkinparams = new ArrayList<NameValuePair>();
                checkinparams.add(new BasicNameValuePair("roarand", roarand));
                checkinparams.add(new BasicNameValuePair("serviceId", "attendance_web_check_in_out"));

                UrlEncodedFormEntity entity_checkin = new UrlEncodedFormEntity(checkinparams, StandardCharsets.UTF_8);
                httpPost_checkin.setEntity(entity_checkin);

                ClassicHttpResponse httpResponse_checkin = (ClassicHttpResponse)httpClient.execute(httpPost_checkin);
                bodyN = EntityUtils.toString(httpResponse_checkin.getEntity());



                str = bodyN;


            } catch (UnsupportedEncodingException uee){
                uee.printStackTrace();
            } catch (ClientProtocolException cpe){
                cpe.printStackTrace();
            } catch (IOException | ParseException ioe){
                ioe.printStackTrace();
            }
            return str;


        }

        @Override
        protected void onPostExecute(String result) {
            //might want to change "executed" for the returned string passed into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
        }


    }


}