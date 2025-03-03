package com.nithinbalan.academease.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import org.json.JSONException;

import java.io.OutputStream;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.academease.R;
import com.google.gson.Gson;
import com.nithinbalan.academease.adapters.AppUpdateHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class MainActivity extends AppCompatActivity {
    Button timetable,attendance, notes, wb, gh;
    int fresh;
    private KonfettiView konfettiView;
    List<String> fridayClasses, mondayClasses, tuesdayClasses, wednesdayClasses, thursdayClasses, saturdayClasses, sundayClasses;
    List<Double> fridayTimes, mondayTimes, tuesdayTimes, wednesdayTimes, thursdayTimes, saturdayTimes, sundayTimes;
    Gson gson;
    String myKey;
    private SharedPreferences sp;
    List<String> tips;
    String name,name1;
    TextView welcome;
    SharedPreferences.Editor editor;
    LocalDateTime current;
    public void explode() {
        EmitterConfig emitterConfig = new Emitter(100L, TimeUnit.MILLISECONDS).max(100);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .spread(360)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 30f)
                        .position(new Position.Relative(0.5, 0.3))
                        .build()
        );
        EmitterConfig emitterConfig1 = new Emitter(100L, TimeUnit.MILLISECONDS).max(50);
        konfettiView.start(
                new PartyFactory(emitterConfig1)
                        .spread(360)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 30f)
                        .position(new Position.Relative(0.5, 0.3))
                        .build()
        );
    }

    public String enterApiKey(){
        myKey = "";
        EditText keybox;
        Button ok;
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.enter_key);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        keybox=dialog.findViewById(R.id.key_box);
        ok=dialog.findViewById(R.id.ok);
        final CountDownLatch latch = new CountDownLatch(1);
        ok.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String key=keybox.getText().toString();
                key=key.trim();
                if(key.isEmpty())
                    Toast.makeText(MainActivity.this,"Enter a key.",Toast.LENGTH_SHORT).show();
                else {
                    myKey = key;
                    dialog.dismiss();
                    latch.countDown();
                }
            }
        });
        dialog.show();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return myKey;
    }
    public void enterName()
    {
        EditText namebox;
        Button ok;
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.enter_name);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        namebox=dialog.findViewById(R.id.namebox);
        ok=dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                name=namebox.getText().toString();
                name=name.trim();
                if(name.isEmpty())
                    Toast.makeText(MainActivity.this,"Enter a name.",Toast.LENGTH_SHORT).show();
                else {
                    editor.putString("username", name);
                    fresh=1;
                    if (current.getHour() >= 5 && current.getHour() <= 11)
                        welcome.setText("GOOD MORNING, " + name + "!");
                    else if (current.getHour() >= 11 && current.getHour() <= 15)
                        welcome.setText("GOOD AFTERNOON, " + name + "!");
                    else if (current.getHour() > 15 && current.getHour() <= 20)
                        welcome.setText("GOOD EVENING, " + name + "!");
                    else if (current.getHour() > 20 && current.getHour() <= 23)
                        welcome.setText("GOOD NIGHT, " + name + "!");
                    else welcome.setText("SLEEP WELL! DON'T STAY UP TOO LATE, " + name + "!");
                    Log.d("name",name);
                    editor.putInt("newUser",fresh);
                    editor.apply();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
    private final ActivityResultLauncher<IntentSenderRequest> updateResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                if (result.getResultCode() != Activity.RESULT_OK) {
                    Toast.makeText(
                            this,
                            "Update failed. Please try again later.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    private String sendToGeminiAPI(String base64Image, String key) {
        HttpURLConnection connection = null;
        try {
            // Gemini API endpoint
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + key);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String requestBody = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"parts\": [\n" +
                    "        {\"text\": \"Parse this timetable image and extract all class schedules, times and course names in a structured format. Return the data in a JSON format. I just need the day, start time and course name.\"}, \n" +
                    "        {\"inline_data\": {\"mime_type\": \"image/jpeg\", \"data\": \"" + base64Image + "\"}}\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (java.util.Scanner scanner = new java.util.Scanner(connection.getInputStream(), "UTF-8")) {
                    scanner.useDelimiter("\\A");
                    return scanner.hasNext() ? scanner.next() : "";
                }
            } else {
                try (java.util.Scanner scanner = new java.util.Scanner(connection.getErrorStream(), "UTF-8")) {
                    scanner.useDelimiter("\\A");
                    String errorResponse = scanner.hasNext() ? scanner.next() : "";
                    Log.e("GeminiAPI", "Error response: " + errorResponse);
                    return null;
                }
            }
        } catch (Exception e) {
            Log.e("GeminiAPI", "Error sending request to Gemini API", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String extractReadableContent(String apiResponse) {
        try {
            JSONObject jsonResponse = new JSONObject(apiResponse);
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() > 0) {
                    return parts.getJSONObject(0).getString("text");
                }
            }
            return "No content found in the response";
        } catch (Exception e) {
            Log.e("ExtractContent", "Error extracting content from API response", e);
            return "Error parsing response: " + e.getMessage();
        }
    }

    private void showTimetableResultDialog(String apiResponse) {
        try {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_timetable_result);
            Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView resultText = dialog.findViewById(R.id.result_text);
            Button saveButton = dialog.findViewById(R.id.save_button);
            Button cancelButton = dialog.findViewById(R.id.cancel_button);

            resultText.setText(extractReadableContent(apiResponse));

            saveButton.setOnClickListener(v -> {
                saveTimetableData(apiResponse);
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Timetable imported successfully!", Toast.LENGTH_SHORT).show();
            });

            cancelButton.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        } catch (Exception e) {
            Log.e("TimetableResult", "Error processing API response", e);
            Toast.makeText(MainActivity.this,
                    "Error processing timetable data: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public int convertToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        if(hours<7) hours+=12;
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }


    // Save timetable data to your local storage
    private void saveTimetableData(String apiResponse) {
        try {
            JSONObject jsonResponse = new JSONObject(apiResponse);
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() > 0) {
                    String timetableData = parts.getJSONObject(0).getString("text");
                    Log.d("TimetableData", "Parsed timetable data: " + timetableData);

                    SharedPreferences sp = getSharedPreferences("com.example.academease", 0);
                    SharedPreferences.Editor editor = sp.edit();

                    try {
                        int startIndex = timetableData.indexOf('[');
                        int endIndex = timetableData.lastIndexOf(']') + 1;

                        if (startIndex >= 0 && endIndex > startIndex) {
                            timetableData = timetableData.substring(startIndex, endIndex);
                        }

                        JSONArray jsonArray = new JSONArray(timetableData);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String day = jsonObject.getString("day");
                            
                            String time = jsonObject.has("time") ?
                                    jsonObject.getString("time") :
                                    jsonObject.getString("start_time");

                            if(time.charAt(0) == '0') {
                                time = time.substring(1);
                            }

                            if(time.length() > 5) {
                                time = time.substring(0, 5);
                            }
                            int len=time.length();
                            if(time.charAt(len-1) == '-') {
                                time=time.substring(0,len-1);
                            }

                            Log.d("time", "time: " + time);

                            String course = jsonObject.has("course") ?
                                    jsonObject.getString("course") :
                                    jsonObject.getString("course_name");

                            if (day.equalsIgnoreCase("Monday")) {
                                Log.d("course", "course: " + course);
                                mondayClasses.add(course);
                                Log.d("mondayClasses", "mondayClasses: " + mondayClasses);
                                int timeInMinutes = convertToMinutes(time);
                                mondayTimes.add(Double.parseDouble(timeInMinutes + ""));
                            } else if (day.equalsIgnoreCase("Tuesday")) {
                                tuesdayClasses.add(course);
                                int timeInMinutes = convertToMinutes(time);
                                tuesdayTimes.add(Double.parseDouble(timeInMinutes + ""));
                            } else if (day.equalsIgnoreCase("Wednesday")) {
                                wednesdayClasses.add(course);
                                int timeInMinutes = convertToMinutes(time);
                                wednesdayTimes.add(Double.parseDouble(timeInMinutes + ""));
                            } else if (day.equalsIgnoreCase("Thursday")) {
                                thursdayClasses.add(course);
                                int timeInMinutes = convertToMinutes(time);
                                thursdayTimes.add(Double.parseDouble(timeInMinutes + ""));
                            } else if (day.equalsIgnoreCase("Friday")) {
                                fridayClasses.add(course);
                                int timeInMinutes = convertToMinutes(time);
                                fridayTimes.add(Double.parseDouble(timeInMinutes + ""));
                            } else if (day.equalsIgnoreCase("Saturday")) {
                                saturdayClasses.add(course);
                                int timeInMinutes = convertToMinutes(time);
                                saturdayTimes.add(Double.parseDouble(timeInMinutes + ""));
                            } else if (day.equalsIgnoreCase("Sunday")) {
                                sundayClasses.add(course);
                                int timeInMinutes = convertToMinutes(time);
                                sundayTimes.add(Double.parseDouble(timeInMinutes + ""));
                            }

                            // Log the extracted values
                            Log.d("TimetableParser", "Day: " + day + ", Time: " + time + ", Course: " + course);
                        }
                    } catch (JSONException e) {
                        Log.e("TimetableParser", "Error parsing JSON", e);
                    }

                    String json= gson.toJson(mondayClasses);
                    String json1= gson.toJson(mondayTimes);
                    editor.putString("mondayClasses",json);
                    editor.putString("mondayTimes",json1);
                    json= gson.toJson(tuesdayClasses);
                    json1= gson.toJson(tuesdayTimes);
                    editor.putString("tuesdayClasses",json);
                    editor.putString("tuesdayTimes",json1);
                    json= gson.toJson(wednesdayClasses);
                    json1= gson.toJson(wednesdayTimes);
                    editor.putString("wednesdayClasses",json);
                    editor.putString("wednesdayTimes",json1);
                    json= gson.toJson(thursdayClasses);
                    json1= gson.toJson(thursdayTimes);
                    editor.putString("thursdayClasses",json);
                    editor.putString("thursdayTimes",json1);
                    json= gson.toJson(fridayClasses);
                    json1= gson.toJson(fridayTimes);
                    editor.putString("fridayClasses",json);
                    editor.putString("fridayTimes",json1);
                    json= gson.toJson(saturdayClasses);
                    json1= gson.toJson(saturdayTimes);
                    editor.putString("saturdayClasses",json);
                    editor.putString("saturdayTimes",json1);
                    json= gson.toJson(sundayClasses);
                    json1= gson.toJson(sundayTimes);
                    editor.putString("sundayClasses",json);
                    editor.putString("sundayTimes",json1);
                    editor.apply();

                    Toast.makeText(MainActivity.this, "Timetable imported successfully!", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Log.e("TimetableData", "No content found in the response");
                Toast.makeText(MainActivity.this, "No timetable data found in the response", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("ExtractContent", "Error extracting content from API response", e);
            Toast.makeText(MainActivity.this,
                    "Error parsing timetable data: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        // Show loading dialog
                        Dialog loadingDialog = new Dialog(MainActivity.this);
                        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        loadingDialog.setCancelable(false);
                        loadingDialog.setContentView(R.layout.dialog_loading);
                        loadingDialog.show();

                        // Process the image in background
                        new Thread(() -> {
                            try {
                                // Convert image to base64
                                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                                String base64Image = bitmapToBase64(bitmap);
                                
                                final String[] key = new String[1];
                                CountDownLatch latch = new CountDownLatch(1);
                                runOnUiThread(() -> {
                                    key[0] = enterApiKey();
                                    latch.countDown();
                                });
                                latch.await();
                                String response = sendToGeminiAPI(base64Image, key[0]);

                                // Update UI on main thread
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    loadingDialog.dismiss();
                                    if (response != null) {
                                        // Show success dialog with parsed timetable
                                        showTimetableResultDialog(response);
                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                "Failed to process timetable image",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (Exception e) {
                                Log.e("ImportTimetable", "Error processing image", e);
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    loadingDialog.dismiss();
                                    Toast.makeText(MainActivity.this,
                                            "Error processing image: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                            }
                        }).start();
                    }
                }
            });

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        sp = getSharedPreferences("com.example.academease", MODE_PRIVATE);

        gson = new Gson();

        mondayClasses = new ArrayList<>();
        tuesdayClasses = new ArrayList<>();
        wednesdayClasses = new ArrayList<>();
        thursdayClasses = new ArrayList<>();
        fridayClasses = new ArrayList<>();
        saturdayClasses = new ArrayList<>();
        sundayClasses = new ArrayList<>();
        mondayTimes = new ArrayList<>();
        tuesdayTimes = new ArrayList<>();
        wednesdayTimes = new ArrayList<>();
        thursdayTimes = new ArrayList<>();
        fridayTimes = new ArrayList<>();
        saturdayTimes = new ArrayList<>();
        sundayTimes = new ArrayList<>();

        mondayClasses =gson.fromJson(sp.getString("mondayClasses",null),ArrayList.class);
        mondayTimes =gson.fromJson(sp.getString("mondayTimes",null),ArrayList.class);
        tuesdayClasses =gson.fromJson(sp.getString("tuesdayClasses",null),ArrayList.class);
        tuesdayTimes =gson.fromJson(sp.getString("tuesdayTimes",null),ArrayList.class);
        wednesdayClasses =gson.fromJson(sp.getString("wednesdayClasses",null),ArrayList.class);
        wednesdayTimes =gson.fromJson(sp.getString("wednesdayTimes",null),ArrayList.class);
        thursdayClasses =gson.fromJson(sp.getString("thursdayClasses",null),ArrayList.class);
        thursdayTimes =gson.fromJson(sp.getString("thursdayTimes",null),ArrayList.class);
        fridayClasses =gson.fromJson(sp.getString("fridayClasses",null),ArrayList.class);
        fridayTimes =gson.fromJson(sp.getString("fridayTimes",null),ArrayList.class);
        saturdayClasses =gson.fromJson(sp.getString("saturdayClasses",null),ArrayList.class);
        saturdayTimes =gson.fromJson(sp.getString("saturdayTimes",null),ArrayList.class);
        sundayClasses =gson.fromJson(sp.getString("sundayClasses",null),ArrayList.class);
        sundayTimes =gson.fromJson(sp.getString("sundayTimes",null),ArrayList.class);

        if(mondayClasses==null) mondayClasses=new ArrayList<>();
        if(tuesdayClasses==null) tuesdayClasses=new ArrayList<>();
        if(wednesdayClasses==null) wednesdayClasses=new ArrayList<>();
        if(thursdayClasses==null) thursdayClasses=new ArrayList<>();
        if(fridayClasses==null) fridayClasses=new ArrayList<>();
        if(saturdayClasses==null) saturdayClasses=new ArrayList<>();
        if(sundayClasses==null) sundayClasses=new ArrayList<>();
        if(mondayTimes==null) mondayTimes=new ArrayList<>();
        if(tuesdayTimes==null) tuesdayTimes=new ArrayList<>();
        if(wednesdayTimes==null) wednesdayTimes=new ArrayList<>();
        if(thursdayTimes==null) thursdayTimes=new ArrayList<>();
        if(fridayTimes==null) fridayTimes=new ArrayList<>();
        if(saturdayTimes==null) saturdayTimes=new ArrayList<>();
        if(sundayTimes==null) sundayTimes=new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+ (API 34+)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, 2);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 (API 33)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 2);
            }
        } else {
            // Android 12 and below (API 32-)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        }

        AppUpdateHelper appUpdateHelper =  new AppUpdateHelper(this, updateResultLauncher);
        getLifecycle().addObserver(appUpdateHelper);
        new Handler(Looper.getMainLooper()).postDelayed(appUpdateHelper::checkForUpdates, 3000);

        // Inside MainActivity.java, modify the importTimetable button click listener:

        Button importTimetable = findViewById(R.id.import_tt);

// Create ActivityResultLauncher for gallery selection


        importTimetable.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        timetable= findViewById(R.id.button);
        attendance= findViewById(R.id.button2);
        notes= findViewById(R.id.button3);
        konfettiView = findViewById(R.id.konfettiView);
        wb= findViewById(R.id.button4);
        gh= findViewById(R.id.gh_link);
        TextView madeWith = findViewById(R.id.madeWith);
        tips = new ArrayList<>();
        tips.add("Report bugs and share your thoughts at nithin.appdev01@gmail.com.");
        tips.add("Long press the welcome message to edit your name.");
        tips.add("Feel free to share the app with your friends!");
        tips.add("Have you tried out the widget yet?");
        tips.add("Tap the number of classes in the calendar page to view your dates.");
        tips.add("Most list items and text items are long-clickable.");
        tips.add("Heads up! Uninstalling erases all your local data.");
        tips.add("Organize PDFs scanned in the app by creating sub-folders.");
        tips.add("Tip: You can save your doodle by clicking on the save icon.");
        tips.add("Long press a subject in the attendance page to edit.");
        tips.add("Fork the project on GitHub to contribute!");
        tips.add("Long press a subject in the timetable page to edit.");
        tips.add("Long press a note in the notes page to edit the content.");
        tips.add("Have you tried the PDF scanner?");
        madeWith.setOnLongClickListener(v -> {
            explode();
            Toast.makeText(MainActivity.this,tips.get((int)(Math.random()*tips.size())),Toast.LENGTH_SHORT).show();
            return true;
        });
//        gh.setOnClickListener(v -> {
//            Uri uri = Uri.parse("https://github.com/killerninjacat/StudentCompanion");
//            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent1);
//        });

        welcome= findViewById(R.id.welcome);
        current = LocalDateTime.now();
        SharedPreferences sp = getSharedPreferences("com.example.academease", 0);
        fresh= sp.getInt("newUser",0);
        if(fresh==0)
        {
            enterName();
        }
        else {
            name1 = sp.getString("username", "User");
            if (current.getHour() >= 5 && current.getHour() <= 11)
                welcome.setText("GOOD MORNING, " + name1 + "!");
            else if (current.getHour() >= 11 && current.getHour() <= 15)
                welcome.setText("GOOD AFTERNOON, " + name1 + "!");
            else if (current.getHour() > 15 && current.getHour() <= 20)
                welcome.setText("GOOD EVENING, " + name1 + "!");
            else if (current.getHour() > 20 && current.getHour() <= 23)
                welcome.setText("GOOD NIGHT, " + name1 + "!");
            else welcome.setText("SLEEP WELL! DON'T STAY UP TOO LATE, " + name1 + "!");
            Log.d("name1", name1);
        }
        editor = sp.edit();
        timetable.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this, TimetableActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        });
        attendance.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this, AttendanceActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        });
        notes.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this, NotesActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        });
        wb.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this, WhiteboardActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        });
        welcome.setOnLongClickListener(v -> {
            enterName();
            return true;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Permission", "Permission denied");
            }
        }
    }

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            this.finishAffinity();
        }
        else { Toast.makeText(getBaseContext(), "Press the back button again to exit the app", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }
}