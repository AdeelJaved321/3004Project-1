package com.example.wiffle_adeel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    EditText place;
    TextView weatherMain;
    TextView weatherDescription;
    String City;


    String apiKey = "d347328678a5eb693250b4aa687d02a8";
    String apiKey2 = "92d6ab8c95f2ea76b6eddc0ba488ae81";
    private final String CHANNEL_ID = "personal_notifications";
    Button btlocation;
    TextView tvlatitude,tvlongitude, tvcity, tv_updatedCity;
    ImageView image;

    FusedLocationProviderClient fusedLocationProviderClient;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat formatter= new SimpleDateFormat("MMMM-dd-yyyy 'at' HH:mm z");
        SimpleDateFormat plannerFormatter= new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormatter= new SimpleDateFormat("HH:mm");

        Date date = new Date(System.currentTimeMillis());

        String dateString = formatter.format(date);
        String timeString = timeFormatter.format(date);
        final String plannerDate = plannerFormatter.format(date);
        setContentView(R.layout.activity_main);

        //assign variable
        btlocation=findViewById(R.id.locationButton);
        tvlatitude=findViewById(R.id.tv_latitude);
        tvlongitude=findViewById(R.id.tv_longitude);
        tvcity=findViewById(R.id.tv_city);
        Button plannerButton = (Button) findViewById(R.id.plannerButton);
        tv_updatedCity= (TextView)findViewById(R.id.tv_updatedCity);

        image = (ImageView) findViewById(R.id.icon);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);


        TextView dateTime = (TextView) findViewById(R.id.dateTime);
        dateTime.setText(dateString);
        dateTime.setTextSize(15);


        weatherMain = findViewById(R.id.weatherMain);
        weatherMain.setTextSize(15);
        weatherDescription = findViewById(R.id.Description);
        weatherDescription.setTextSize(15);
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            getLocation();
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ){
                getLocation();
            }
        }
        getWeather();
        FiveDayForecast();

        final TextView tripTitle = (TextView) findViewById(R.id.trips_title);
        final String tripsWord = " Your Trips";
        tripTitle.setText(tripsWord);
        tripTitle.setTextSize(20);

        plannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlanner();
                getTrips(plannerDate);
            }
        });


    }

    public void openPlanner(){
        Intent intent = new Intent(this, Planner.class);
        startActivity(intent);
    }

    public void btn_showDialog(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        View mView =getLayoutInflater().inflate(R.layout.activity_popup,null);


        //here we can change the city variable to equal the inputed edit_location button
        final EditText tv_updatedCity =(EditText)mView.findViewById(R.id.edit_Location);
        Button btn_cancel = (Button)mView.findViewById(R.id.btn_cancel);
        Button btn_ok = (Button)mView.findViewById(R.id.btn_ok);

        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        btn_cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                alertDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                //for this i just created a city variable that lets us store a city variable
                City=(tv_updatedCity.getText().toString());
                tvcity.setText(City);
                getWeather();
                FiveDayForecast();
                //System.out.println(City);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0]+ grantResults[1]
                == PackageManager.PERMISSION_GRANTED)){
            getLocation();
        }else{
            Toast.makeText(getApplicationContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")

    private void getLocation(){

        LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE
        );
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled((LocationManager.NETWORK_PROVIDER))) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    final Location location = task.getResult();
                    if(location !=null){
                        tvlatitude.setText(String.valueOf(location.getLatitude()));

                        tvlongitude.setText(String.valueOf(location.getLongitude()));

                        tvcity.setText((getCityName(location.getLatitude(),location.getLongitude())));

                    }
                    else{
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult){
                                Location location1 = locationResult.getLastLocation();

                                tvlatitude.setText((String.valueOf(location1.getLatitude())));

                                tvlongitude.setText(String.valueOf(location1.getLongitude()));

                                tvcity.setText((getCityName(location1.getLatitude(),location1.getLongitude())));
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                    getWeather();
                    FiveDayForecast();
                }
            });

        }else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void getWeather(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        API api = retrofit.create(API.class);
        Call<connectAPI> API = api.getWeather(tvcity.getText().toString(),apiKey);
        API.enqueue(new Callback<connectAPI>() {
            @Override
            public void onResponse(Call<connectAPI> call, Response<connectAPI> response) {

                Double Kalv_Temp = null;
                if (response.body() != null) {
                    Kalv_Temp = response.body().getMain().getTemp();
                    Integer Celc_Temp = (int) (Kalv_Temp - 273.15); //calv to celc
                    String weatherString = String.valueOf(Celc_Temp) + " ℃ in " + tvcity.getText().toString() ;
                    weatherMain.setText(weatherString);

                    String w = response.body().getWeather().get(0).getMain();
                    String icon = response.body().getWeather().get(0).getIcon();
                    weatherDescription.setText(w);
                    if(icon.equals("01d")){
                        image.setImageResource(R.drawable.h01d);
                    }
                    if(icon.equals("01n")){
                        image.setImageResource(R.drawable.h01n);
                    }
                    if(icon.equals("02d")){
                        image.setImageResource(R.drawable.h02d);
                    }
                    if(icon.equals("02n")){
                        image.setImageResource(R.drawable.h02n);
                    }
                    if(icon.equals("03d")){
                        image.setImageResource(R.drawable.h03d);
                    }
                    if(icon.equals("03n")){
                        image.setImageResource(R.drawable.h03n);
                    }
                    if(icon.equals("04d")){
                        image.setImageResource(R.drawable.h04d);
                    }
                    if(icon.equals("04n")){
                        image.setImageResource(R.drawable.h04n);
                    }
                    if(icon.equals("09d")){
                        image.setImageResource(R.drawable.h09d);
                    }
                    if(icon.equals("09n")){
                        image.setImageResource(R.drawable.h09n);
                    }
                    if(icon.equals("10d")){
                        image.setImageResource(R.drawable.h10d);
                    }
                    if(icon.equals("10n")){
                        image.setImageResource(R.drawable.h10n);
                    }
                    if(icon.equals("11d")){
                        image.setImageResource(R.drawable.h11d);
                    }
                    if(icon.equals("11n")){
                        image.setImageResource(R.drawable.h11n);
                    }
                    if(icon.equals("13d")){
                        image.setImageResource(R.drawable.h13d);
                    }
                    if(icon.equals("13n")){
                        image.setImageResource(R.drawable.h13d);
                    }
                    if(icon.equals("50d")){
                        image.setImageResource(R.drawable.h50d);
                    }
                    if(icon.equals("50n")){
                        image.setImageResource(R.drawable.h50n);
                    }

                }

            }


            @Override
            public void onFailure(Call<connectAPI> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();


            }
        });
    }

    public void FiveDayForecast(){
        Retrofit retrofit1 = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        API api = retrofit1.create(API.class);
        Call<ListClass> API = api.FiveDayForecast(tvcity.getText().toString().trim(),apiKey);
        API.enqueue(new Callback<ListClass>() {
            @Override
            public void onResponse(Call<ListClass> call, Response<ListClass> response) {
                LinearLayout weatherLayout = (LinearLayout) findViewById(R.id.weekly_scroll);
                String fiveDayString = "\n" + "\n" + "  ";
                if (response.body() != null) {
                    for (int i = 0; i < 5; i++) {
                        Double w = response.body().getForecast().get(i * 8).getMain().getTemp();
                        int celc = (int) (w - 273.15);
                        Date newDate = new Date(System.currentTimeMillis() + 86400000 * i);
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM-dd");
                        String icon = response.body().getForecast().get(8*i).getWeather().get(0).getIcon();
                        ImageView img = findIcon(i);
                        if(icon.equals("01d")){
                            img.setImageResource(R.drawable.h01d);
                        }
                        if(icon.equals("01n")){
                            img.setImageResource(R.drawable.h01n);
                        }
                        if(icon.equals("02d")){
                            img.setImageResource(R.drawable.h02d);
                        }
                        if(icon.equals("02n")){
                            img.setImageResource(R.drawable.h02n);
                        }
                        if(icon.equals("03d")){
                            img.setImageResource(R.drawable.h03d);
                        }
                        if(icon.equals("03n")){
                            img.setImageResource(R.drawable.h03n);
                        }
                        if(icon.equals("04d")){
                            img.setImageResource(R.drawable.h04d);
                        }
                        if(icon.equals("04n")){
                            img.setImageResource(R.drawable.h04n);
                        }
                        if(icon.equals("09d")){
                            img.setImageResource(R.drawable.h09d);
                        }
                        if(icon.equals("09n")){
                            img.setImageResource(R.drawable.h09n);
                        }
                        if(icon.equals("10d")){
                            img.setImageResource(R.drawable.h10d);
                        }
                        if(icon.equals("10n")){
                            img.setImageResource(R.drawable.h10n);
                        }
                        if(icon.equals("11d")){
                            img.setImageResource(R.drawable.h11d);
                        }
                        if(icon.equals("11n")){
                            img.setImageResource(R.drawable.h11n);
                        }
                        if(icon.equals("13d")){
                            img.setImageResource(R.drawable.h13d);
                        }
                        if(icon.equals("13n")){
                            img.setImageResource(R.drawable.h13d);
                        }
                        if(icon.equals("50d")){
                            img.setImageResource(R.drawable.h50d);
                        }
                        if(icon.equals("50n")){
                            img.setImageResource(R.drawable.h50n);
                        }

                        fiveDayString = fiveDayString + Integer.toString(celc) + " ℃ on " + dateFormatter.format(newDate) + "\n" + "\n" + "\n" + "  ";
                    }
                    TextView fiveDayText = (TextView) findViewById(R.id.week);
                    fiveDayText.setTextSize(16);
                    fiveDayText.setText(fiveDayString);

                }
            }
            @Override
            public void onFailure(Call<ListClass> call, Throwable t) {

            }
        });
    }


    public String getCityName(double lat, double lon){
        String curCity= "";

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addressList;
        try{
            addressList = geocoder.getFromLocation(lat, lon, 10);
            if(addressList.size() >0){
                for(Address adr: addressList){
                    if(adr.getLocality() != null && adr.getLocality().length() >0 ){
                        curCity=adr.getLocality();
                        break;
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();

        }
        return curCity;

}
    public void displayNotification(View view){

        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.h01n);
        TextView tripText = (TextView) findViewById(R.id.trips_text);
        builder.setContentTitle("New Trip");
        builder.setContentText("You have a new trip planned :) : " + tripText.getText().toString());
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);

        NotificationManagerCompat notifyManagerCompact= NotificationManagerCompat.from(this);
        int notification_ID = 001;
        notifyManagerCompact.notify(notification_ID,builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notifications";
            String description = "include all personal notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getTrips(String plannerDate) {
        if(!Planner.getPlannerData(plannerDate).equals("")) {
            LinearLayout tripLayout = (LinearLayout) findViewById(R.id.trips);
            TextView tripText = (TextView) findViewById(R.id.trips_text);
            String text = "    " + Planner.getPlannerData(plannerDate);
            tripText.setText(text);
            displayNotification(tripText);
        }
    }


    public ImageView findIcon(int iconNumber) {
        ImageView img = null;
        if (iconNumber == 0) {
            img = (ImageView) findViewById(R.id.icon0);}
        if (iconNumber == 1) {
            img = (ImageView) findViewById(R.id.icon1);}
        if (iconNumber == 2) {
            img = (ImageView) findViewById(R.id.icon2);}
        if (iconNumber == 3) {
            img = (ImageView) findViewById(R.id.icon3);}
        if (iconNumber == 4) {
            img = (ImageView) findViewById(R.id.icon4);}
        return img;

    }
}