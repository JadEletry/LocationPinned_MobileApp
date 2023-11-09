package com.example.location_pinned;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public EditText addressInput, latitudeInput, longitudeInput, addressQuery, locationIdInput, updateLocationIdInput, newAddressInput, newLatitudeInput, newLongitudeInput;
    private Button addLocationButton, deleteLocationButton, updateLocationButton, queryLocationButton;

    private LocationDatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private RecyclerView locationRecyclerView;

    private LocationAdapter locationAdapter;
    private List<LocationModel> locationList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new LocationDatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();

        // Initialize UI components
        addressInput = findViewById(R.id.addressInput);
        latitudeInput = findViewById(R.id.latitudeInput);
        longitudeInput = findViewById(R.id.longitudeInput);
        addressQuery = findViewById(R.id.addressQuery);
        addLocationButton = findViewById(R.id.addLocationButton);
        deleteLocationButton = findViewById(R.id.deleteLocationButton);
        updateLocationButton = findViewById(R.id.updateLocationButton);
        queryLocationButton = findViewById(R.id.queryLocationButton);
        locationIdInput = findViewById(R.id.locationIdInput);


        // Set up ListView with location data
        locationRecyclerView = findViewById(R.id.locationRecyclerView);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Read coordinates from the input file and perform geocoding
        locationList = readCoordinatesAndGeocode();
        locationAdapter = new LocationAdapter(locationList);
        locationRecyclerView.setAdapter(locationAdapter);


        // Set up click listeners for buttons and implement corresponding actions
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user inputs from the EditText fields
                String address = addressInput.getText().toString();
                double latitude = Double.parseDouble(latitudeInput.getText().toString());
                double longitude = Double.parseDouble(longitudeInput.getText().toString());

                // Add the location to the database
                addLocationToDatabase(address, latitude, longitude);

            }
        });

        deleteLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the location ID entered by the user
                String locationIdText = locationIdInput.getText().toString();

                if (!locationIdText.isEmpty()) {
                    long locationId = Long.parseLong(locationIdText);

                    // Delete the location from the database
                    deleteLocationFromDatabase(locationId);

                } else {
                    // Handle the case where the location ID is not provided
                }
            }
        });

        updateLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the location ID, new address, new latitude, and new longitude entered by the user
                String locationIdText = updateLocationIdInput.getText().toString();
                String newAddress = newAddressInput.getText().toString();
                String newLatitudeText = newLatitudeInput.getText().toString();
                String newLongitudeText = newLongitudeInput.getText().toString();

                if (!locationIdText.isEmpty() && !newAddress.isEmpty() && !newLatitudeText.isEmpty() && !newLongitudeText.isEmpty()) {
                    long locationId = Long.parseLong(locationIdText);
                    double newLatitude = Double.parseDouble(newLatitudeText);
                    double newLongitude = Double.parseDouble(newLongitudeText);

                    // Update the location in the database
                    updateLocationInDatabase(locationId, newAddress, newLatitude, newLongitude);

                } else {
                    // Handle the case where required data is not provided

                }
            }
        });

        queryLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the address entered by the user
                String queryAddress = addressQuery.getText().toString();

                // Call the query method with the entered address
                queryLocation(queryAddress);
            }
        });

    }

    private List<LocationModel> readCoordinatesAndGeocode() {
        List<LocationModel> locations = new ArrayList<>();

        // Read coordinates from the input file and perform geocoding
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("coordinates.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] coordinates = line.split(",");
                double latitude = Double.parseDouble(coordinates[0].trim());
                double longitude = Double.parseDouble(coordinates[1].trim());

                // Perform geocoding to get the address
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                String addressString = "Address not found";
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    addressString = address.getAddressLine(0);
                }

                // Create a LocationModel object with the incremented ID
                long locationId = insertLocationIntoDatabase(addressString, latitude, longitude);
                LocationModel location = new LocationModel(locationId, addressString, latitude, longitude);
                locations.add(location);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Always return the locations, whether they were inserted or not
        return locations;
    }

    private long insertLocationIntoDatabase(String address, double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put("address", address);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        return database.insert("location", null, values);
    }

    // Implement other methods and button click listeners as needed
    public void addLocationToDatabase(String address, double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put("address", address);
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        long newRowId = database.insert("location", null, values);

        if (newRowId != -1) {
            // Successfully added to the database
            // Update the locationList with the new location
            LocationModel newLocation = new LocationModel(newRowId, address, latitude, longitude); // Include the ID
            locationList.add(newLocation);

            // Notify the adapter that the data has changed
            locationAdapter.notifyDataSetChanged();

            // Clear input fields
            addressInput.setText("");
            latitudeInput.setText("");
            longitudeInput.setText("");
        } else {
            // Error adding to the database
        }
    }


    public void deleteLocationFromDatabase(long locationId) {
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(locationId)};

        int deletedRows = database.delete("location", selection, selectionArgs);

        if (deletedRows > 0) {
            // Successfully deleted from the database

            // Find and remove the location with the specified ID from locationList
            for (LocationModel location : locationList) {
                if (location.getId() == locationId) {
                    locationList.remove(location);
                    break;
                }
            }

            // Notify the adapter that the data has changed
            locationAdapter.notifyDataSetChanged();

            // Clear the location ID input field
            locationIdInput.setText("");
        } else {
            // Location not found or error deleting
        }
    }

    private void updateLocationInDatabase(long locationId, String newAddress, double newLatitude, double newLongitude) {
        ContentValues values = new ContentValues();
        values.put("address", newAddress);
        values.put("latitude", newLatitude);
        values.put("longitude", newLongitude);

        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(locationId)};

        int updatedRows = database.update("location", values, selection, selectionArgs);

        if (updatedRows > 0) {
            // Successfully updated in the database


            // Find and update the location with the specified ID in locationList
            for (LocationModel location : locationList) {
                if (location.getId() == locationId) {
                    location.setAddress(newAddress);
                    location.setLatitude(newLatitude);
                    location.setLongitude(newLongitude);
                    break;
                }
            }

            // Notify the adapter that the data has changed
            locationAdapter.notifyDataSetChanged();

            // Clear input fields
            updateLocationIdInput.setText("");
            newAddressInput.setText("");
            newLatitudeInput.setText("");
            newLongitudeInput.setText("");

        } else {
            // Location not found or error updating
        }
    }

    private void queryLocation(String queryAddress) {
        // Specify the columns to retrieve in the query
        String[] columns = {"latitude", "longitude"};

        // Query the database for the given address
        Cursor cursor = database.query(
                "location",
                columns, // Select specific columns
                "address = ?",
                new String[]{queryAddress},
                null,
                null,
                null
        );

        // Find the TextView for displaying the result
        TextView resultTextView = findViewById(R.id.resultTextView);

        // Check if a location with the given address was found
        if (cursor.moveToFirst()) {
            // Retrieve the latitude and longitude
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

            // Update the UI with the result
            resultTextView.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
        } else {
            // Handle the case where the address is not found in the database
            resultTextView.setText("Location not found");
        }

        cursor.close();
    }


}


