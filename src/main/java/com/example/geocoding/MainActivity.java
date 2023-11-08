package com.example.geocoding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import com.opencsv.CSVReader;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate variables
        GeoDatabase geo_db = GeoDatabase.getInstance(this);
        TextView latText = findViewById(R.id.Latitude), longText = findViewById(R.id.Longitude), addText = findViewById(R.id.Address);
        Button load = findViewById(R.id.Load), delete = findViewById(R.id.Delete), add = findViewById(R.id.Add), update = findViewById(R.id.Update);
        EditText getAddress = findViewById(R.id.SearchAddress), idEdit = findViewById(R.id.AddId), latEdit = findViewById(R.id.AddLatitude), longEdit = findViewById(R.id.AddLongitude);

        //resets db
        geo_db.resetTable();

        //loads locations from location.csv
        loadLocations(this.getApplicationContext());


        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location loc;
                String address = getAddress.getText().toString();
                if (address.isEmpty()){
                    Toast.makeText(MainActivity.this, "Address is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    loc = geo_db.getLocByAddress(address);
                    if (loc != null) {
                        latText.setText(String.format(Locale.getDefault(), "Latitude: %f", loc.getLatitude()));
                        longText.setText(String.format(Locale.getDefault(), "Longitude: %f", loc.getLongitude()));
                        addText.setText(String.format(Locale.getDefault(), "Address: %s", loc.getAddress()));
                    } else {
                        Toast.makeText(MainActivity.this, "Address Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geo_db.deleteLocByAddress(getAddress.getText().toString());
                Toast.makeText(MainActivity.this, "Address Deleted", Toast.LENGTH_SHORT).show();
            }

        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = Double.parseDouble(latEdit.getText().toString());
                double longitude = Double.parseDouble(longEdit.getText().toString());
                String address = getAddress(longitude, latitude);
                Location loc = new Location(longitude, latitude, address);
                geo_db.addLoc(loc);
                Toast.makeText(MainActivity.this, "Address Updated", Toast.LENGTH_SHORT).show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = Integer.parseInt(idEdit.getText().toString());
                double latitude = Double.parseDouble(latEdit.getText().toString());
                double longitude = Double.parseDouble(longEdit.getText().toString());
                String address = getAddress(longitude, latitude);
                geo_db.updateLoc(id, latitude,longitude, address);
            }
        });
    }

    private void loadLocations(Context context){
        GeoDatabase geo_db = GeoDatabase.getInstance(context);
        InputStream ins = this.getResources().openRawResource(R.raw.locations);
        CSVReader csvReader = new CSVReader(new InputStreamReader(ins));
        double longitude, latitude;
        String[] line;
        Location loc;
        String address;
        try {
            while((line = csvReader.readNext()) != null) {
                latitude = Double.parseDouble(line[0]);
                longitude = Double.parseDouble(line[1]);
                address = getAddress(longitude, latitude);
                loc = new Location(longitude, latitude, address);
                geo_db.addLoc(loc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAddress (double longitude, double latitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> address;
        StringBuilder sAddress = new StringBuilder();
        try {
            address = geocoder.getFromLocation(latitude, longitude, 1);
            sAddress.append(address.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sAddress.toString();
    }
}