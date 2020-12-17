package com.example.weatherapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CircleImageView profile_img;
    TextView username_display;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    ProgressDialog pd;
    String txtJson;

    ImageView search;
    TextView tempText,
            descText,
            humidityText,
            Colombo_des,
            Colombo_humi,
            Colombo_temp,
            Galle_des,
            Galle_humi,
            Galle_temp,
            Kandy_des,
            Kandy_humi,
            Kandy_temp,
            Jaffna_des,
            Jaffna_humi,
            Jaffna_temp;
    EditText textField;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.search);
        tempText = findViewById(R.id.tempText);
        descText = findViewById(R.id.descText);
        humidityText = findViewById(R.id.humidityText);
        textField = findViewById(R.id.textField);

        Colombo_des = findViewById(R.id.colombo_des);
        Colombo_humi = findViewById(R.id.colombo_humi);
        Colombo_temp = findViewById(R.id.colombo_temp);

        Galle_des = findViewById(R.id.Galle_des);
        Galle_humi = findViewById(R.id.Galle_humi);
        Galle_temp = findViewById(R.id.galle_temp);

        Kandy_des = findViewById(R.id.Kandy_des);
        Kandy_humi = findViewById(R.id.Kandy_humi);
        Kandy_temp = findViewById(R.id.Kandy_temp);

        Jaffna_des = findViewById(R.id.Jaffna_des);
        Jaffna_humi = findViewById(R.id.Jaffna_humi);
        Jaffna_temp = findViewById(R.id.Jaffna_temp);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getWeatherData(textField.getText().toString().trim());

            }
        });

        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.nav_bar_open, R.string.nav_bar_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        new LoadColombo().execute();
        new LoadGalle().execute();
        new LoadKandy().execute();
        new LoadJaffna().execute();

    }


    private void getWeatherData(String name) {
        new JsonTask().execute("http://api.openweathermap.org/data/2.5/weather?q=" + name + "&appid=cffd6f32cb1cfa0675ecc6ec08f7d4bc");
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            txtJson = result;

            try {

                System.out.println(txtJson);

                JSONObject jo = new JSONObject(txtJson);

                JSONObject tem = jo.getJSONObject("main");
                String temprature = tem.getString("temp");
                String humidity = tem.getString("humidity");


                JSONObject ar1 = jo.getJSONArray("weather").getJSONObject(0);
                String description = ar1.getString("description");
                tempText.setText("Temperature :" + " " + (int) (Double.parseDouble(temprature) - 274.15) + " °C");
                descText.setText("Feels Like :" + " " + description);
                humidityText.setText("Humidity :" + " " + humidity);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.t3:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;

        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.t3) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            finish();
        }

        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }


        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private class LoadColombo extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();


        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=colombo&appid=cffd6f32cb1cfa0675ecc6ec08f7d4bc");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            txtJson = result;

            try {

                System.out.println(txtJson);

                JSONObject jo = new JSONObject(txtJson);

                JSONObject tem = jo.getJSONObject("main");
                String temprature = tem.getString("temp");
                String humidity = tem.getString("humidity");


                JSONObject ar1 = jo.getJSONArray("weather").getJSONObject(0);
                String description = ar1.getString("description");
                Colombo_temp.setText("Temperature :" + " " + (int) (Double.parseDouble(temprature) - 274.15) + " °C");
                Colombo_des.setText(description);
                Colombo_humi.setText("Humidity :" + " " + humidity);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private class LoadGalle extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();


        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=galle&appid=cffd6f32cb1cfa0675ecc6ec08f7d4bc");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            txtJson = result;

            try {

                System.out.println(txtJson);

                JSONObject jo = new JSONObject(txtJson);

                JSONObject tem = jo.getJSONObject("main");
                String temprature = tem.getString("temp");
                String humidity = tem.getString("humidity");


                JSONObject ar1 = jo.getJSONArray("weather").getJSONObject(0);
                String description = ar1.getString("description");
                Galle_temp.setText("Temperature :" + " " + (int) (Double.parseDouble(temprature) - 274.15) + " °C");
                Galle_des.setText(description);
                Galle_humi.setText("Humidity :" + " " + humidity);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private class LoadKandy extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();


        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=kandy&appid=cffd6f32cb1cfa0675ecc6ec08f7d4bc");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            txtJson = result;

            try {

                System.out.println(txtJson);

                JSONObject jo = new JSONObject(txtJson);

                JSONObject tem = jo.getJSONObject("main");
                String temprature = tem.getString("temp");
                String humidity = tem.getString("humidity");


                JSONObject ar1 = jo.getJSONArray("weather").getJSONObject(0);
                String description = ar1.getString("description");
                Kandy_temp.setText("Temperature :" + " " + (int) (Double.parseDouble(temprature) - 274.15) + " °C");
                Kandy_des.setText(description);
                Kandy_humi.setText("Humidity :" + " " + humidity);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private class LoadJaffna extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();


        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=jaffna&appid=cffd6f32cb1cfa0675ecc6ec08f7d4bc");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            txtJson = result;

            try {

                System.out.println(txtJson);

                JSONObject jo = new JSONObject(txtJson);

                JSONObject tem = jo.getJSONObject("main");
                String temprature = tem.getString("temp");
                String humidity = tem.getString("humidity");


                JSONObject ar1 = jo.getJSONArray("weather").getJSONObject(0);
                String description = ar1.getString("description");
                Jaffna_temp.setText("Temperature :" + " " + (int) (Double.parseDouble(temprature) - 274.15) + " °C");
                Jaffna_des.setText(description);
                Jaffna_humi.setText("Humidity :" + " " + humidity);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}