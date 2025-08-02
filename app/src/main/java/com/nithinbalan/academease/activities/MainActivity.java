package com.nithinbalan.academease.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.WindowCompat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.academease.BuildConfig;
import com.example.academease.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.nithinbalan.academease.adapters.AppUpdateHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
    int fresh,useCount;
    private KonfettiView konfettiView;
    List<String> tips;
    String name,name1,myKey;
    TextView welcome;
    SharedPreferences.Editor editor;
    private SharedPreferences sp;
    List<String> fridayClasses, mondayClasses, tuesdayClasses, wednesdayClasses, thursdayClasses, saturdayClasses, sundayClasses;
    List<Double> fridayTimes, mondayTimes, tuesdayTimes, wednesdayTimes, thursdayTimes, saturdayTimes, sundayTimes;
    Gson gson;
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
                    "        {\"text\": \"Parse this timetable image and extract all class schedules, times and course names in a structured format. Return the data in a JSON format with the following keys: day, start_time, course_name. I just need the day, start time and course name. Make sure to detect the correct time and slot for each class. The slot code is horizontally next to the corresponding course. The course_name must be the actual name corresponding to the slot code.\"}, \n" +
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

    @SuppressLint("SetTextI18n")
    public void promptForApiKey(ApiKeyCallback callback) {
        EditText keybox;
        Button ok,close,defaultKey,editKey;
        TextView title;
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        String userKey = sp.getString("userKey", null);
        dialog.setContentView(R.layout.enter_key);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        keybox = dialog.findViewById(R.id.key_box);
        ok = dialog.findViewById(R.id.ok_key);
        close = dialog.findViewById(R.id.close_key);
        defaultKey = dialog.findViewById(R.id.def_key);
        editKey = dialog.findViewById(R.id.edit_key);
        title = dialog.findViewById(R.id.textView3);
        if(userKey != null && !userKey.isEmpty()) {
            keybox.setVisibility(View.INVISIBLE);
            ok.setText("Saved Key ("+userKey+")");
            defaultKey.setVisibility(View.VISIBLE);
            title.setText("Choose the API key to use");
        }
        else editKey.setVisibility(View.INVISIBLE);

        editKey.setOnClickListener(view -> {
            keybox.setVisibility(View.VISIBLE);
            defaultKey.setVisibility(View.GONE);
            ok.setText(" Save Key and use ");
            title.setText("Enter your new key");
            editKey.setVisibility(View.INVISIBLE);
        });

        ok.setOnClickListener(v -> {
            String key;
            if(userKey!=null && ok.getText() != " Save Key and use ") key = userKey;
            else {
                key = keybox.getText().toString().trim();
                editor.putString("userKey", key);
                editor.apply();
            }
            if (key.isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter a key.", Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                callback.onApiKeyProvided(key);
            }
        });
        close.setOnClickListener(view -> {
            dialog.dismiss();
            callback.onDialogClosed();
        });
        defaultKey.setOnClickListener(view -> {
            if(useCount>=4)
            {
                Toast.makeText(MainActivity.this,"You have exceeded the maximum number of uses for the default key.",Toast.LENGTH_SHORT).show();
                return;
            }
            useCount++;
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("useCount",useCount);
            editor.apply();
            dialog.dismiss();
            callback.onApiKeyProvided(myKey);
        });
        dialog.show();
    }

    // Callback interface for API key
    public interface ApiKeyCallback {
        void onApiKeyProvided(String apiKey);
        void onDialogClosed();
    }

    // At the top of your MainActivity class, replace the existing galleryLauncher with:
    private final ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
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
                            InputStream imageStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                            String base64Image = bitmapToBase64(bitmap);

                            runOnUiThread(() -> {
                                promptForApiKey(new ApiKeyCallback() {
                                    @Override
                                    public void onApiKeyProvided(String apiKey) {
                                        // Continue with API request in background
                                        new Thread(() -> {
                                            String response = sendToGeminiAPI(base64Image, apiKey);

                                            // Update UI on main thread
                                            runOnUiThread(() -> {
                                                loadingDialog.dismiss();
                                                if (response != null) {
                                                    showTimetableResultDialog(response);
                                                } else {
                                                    Toast.makeText(MainActivity.this,
                                                            "Failed to process timetable image. Check your API key and try again.",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }).start();
                                    }

                                    @Override
                                    public void onDialogClosed() {
                                        loadingDialog.dismiss();
                                    }
                                });
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
            });

    private void showTimetableImportInstructions() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.query_dialog);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int dialogHeight = (int)(screenHeight * 0.75);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        ImageView start = dialog.findViewById(R.id.import_timetable_icon);
        start.setOnClickListener(view -> {
            photoPickerLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        dialog.findViewById(R.id.ok_button).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_timetable) {
                startActivity(new Intent(MainActivity.this, TimetableActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (itemId == R.id.nav_notes) {
                startActivity(new Intent(MainActivity.this, NotesActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (itemId == R.id.nav_attendance) {
                startActivity(new Intent(MainActivity.this, AttendanceActivity.class));
                overridePendingTransition(0,0);
                return true;
            }

            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        SharedPreferences sp1 = getSharedPreferences("com.example.academease", MODE_PRIVATE);

        useCount = sp1.getInt("useCount", 0);

        gson = new Gson();

        myKey = BuildConfig.API_KEY;

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

        mondayClasses =gson.fromJson(sp1.getString("mondayClasses",null),ArrayList.class);
        mondayTimes =gson.fromJson(sp1.getString("mondayTimes",null),ArrayList.class);
        tuesdayClasses =gson.fromJson(sp1.getString("tuesdayClasses",null),ArrayList.class);
        tuesdayTimes =gson.fromJson(sp1.getString("tuesdayTimes",null),ArrayList.class);
        wednesdayClasses =gson.fromJson(sp1.getString("wednesdayClasses",null),ArrayList.class);
        wednesdayTimes =gson.fromJson(sp1.getString("wednesdayTimes",null),ArrayList.class);
        thursdayClasses =gson.fromJson(sp1.getString("thursdayClasses",null),ArrayList.class);
        thursdayTimes =gson.fromJson(sp1.getString("thursdayTimes",null),ArrayList.class);
        fridayClasses =gson.fromJson(sp1.getString("fridayClasses",null),ArrayList.class);
        fridayTimes =gson.fromJson(sp1.getString("fridayTimes",null),ArrayList.class);
        saturdayClasses =gson.fromJson(sp1.getString("saturdayClasses",null),ArrayList.class);
        saturdayTimes =gson.fromJson(sp1.getString("saturdayTimes",null),ArrayList.class);
        sundayClasses =gson.fromJson(sp1.getString("sundayClasses",null),ArrayList.class);
        sundayTimes =gson.fromJson(sp1.getString("sundayTimes",null),ArrayList.class);

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

        AppUpdateHelper appUpdateHelper =  new AppUpdateHelper(this, updateResultLauncher);
        getLifecycle().addObserver(appUpdateHelper);
        new Handler(Looper.getMainLooper()).postDelayed(appUpdateHelper::checkForUpdates, 3000);

        ExtendedFloatingActionButton importTimetable = findViewById(R.id.import_tt);
        FloatingActionButton query = findViewById(R.id.query);

        query.setOnClickListener(view -> showTimetableImportInstructions());


        importTimetable.setOnClickListener(v -> {
            photoPickerLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
        timetable= findViewById(R.id.timetableButton);
        attendance= findViewById(R.id.attendanceButton);
        notes= findViewById(R.id.notesButton);
        konfettiView = findViewById(R.id.konfettiView);
        wb= findViewById(R.id.whiteboardButton);
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

        welcome= findViewById(R.id.welcome);
        current = LocalDateTime.now();
        sp = getSharedPreferences("com.example.academease", 0);
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