package com.nithinbalan.academease.activities;

import static com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF;
import static com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nithinbalan.academease.ClickListener;
import com.nithinbalan.academease.LongClickListener;
import com.example.academease.R;
import com.google.gson.Gson;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Objects;
import java.io.InputStream;

public class NotesActivity extends AppCompatActivity {
    Button new_scan,open,choose;
    private SharedPreferences sp;
    ClickListener clickListener;
    LongClickListener longClickListener;
    private Uri treeUri;
    private ActivityResultLauncher<IntentSenderRequest> scannerLauncher;

    public void newScan() {
        GmsDocumentScannerOptions options = new GmsDocumentScannerOptions.Builder()
                .setGalleryImportAllowed(true)
                .setPageLimit(50)
                .setResultFormats(RESULT_FORMAT_PDF)
                .setScannerMode(SCANNER_MODE_FULL)
                .build();
        GmsDocumentScanner scanner = GmsDocumentScanning.getClient(options);
        scanner.getStartScanIntent(this)
                .addOnSuccessListener(intentSender ->
                        scannerLauncher.launch(new IntentSenderRequest.Builder(intentSender).build()))
                .addOnFailureListener(e -> {
                    Log.e("DocumentScanner", "Failed to start document scanning", e);
                    Toast.makeText(this, "Failed to start document scanning", Toast.LENGTH_SHORT).show();
                        });
    }
    private void savePdfInScopedStorage(Context context, String url, String fileName) throws IOException {
        Uri storedUri=sp.getString("FileStorageUri",null)!=null?Uri.parse(sp.getString("FileStorageUri",null)):null;
        if(storedUri==null) {
            Log.e("uri", "null");
            return;
        }
        DocumentFile directory=DocumentFile.fromTreeUri(getApplicationContext(), storedUri);
        if (directory == null || !directory.exists()) {
            Toast.makeText(context, "Directory not found or inaccessible", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentFile newFile = directory.createFile("application/pdf", fileName);
        if (newFile == null) {
            Toast.makeText(context, "Failed to create file in the specified directory", Toast.LENGTH_SHORT).show();
            return;
        }

        try (InputStream input = new URL(url).openStream();
             OutputStream output = context.getContentResolver().openOutputStream(newFile.getUri())) {
            if (output == null) {
                throw new IOException("Failed to open output stream.");
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            Toast.makeText(context, fileName + " saved to chosen directory", Toast.LENGTH_SHORT).show();
            openSelectedDirectory();
        } catch (IOException e) {
            Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_SHORT).show();
            throw e;
        }
    }
    private void setFileName(Uri uri) {
        final Dialog dialog=new Dialog(NotesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.file_name);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        EditText name=dialog.findViewById(R.id.namebox);
        Button save=dialog.findViewById(R.id.save);
        Log.d("uri", "setFileName: "+uri.toString());
        save.setOnClickListener(v -> {
            String fileName=name.getText().toString().trim();
            if(fileName.equals(""))
                Toast.makeText(NotesActivity.this,"Enter a file name!",Toast.LENGTH_SHORT).show();
            else
            {
                try {
                    savePdfInScopedStorage(NotesActivity.this, uri.toString(), fileName + ".pdf");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_notes) {
                return true;
            } else if (itemId == R.id.nav_timetable) {
                startActivity(new Intent(NotesActivity.this, TimetableActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(NotesActivity.this, MainActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (itemId == R.id.nav_attendance) {
                startActivity(new Intent(NotesActivity.this, AttendanceActivity.class));
                overridePendingTransition(0,0);
                return true;
            }

            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_notes);

        open = findViewById(R.id.openScannedNotesButton);
        choose = findViewById(R.id.chooseFolderButton);

        open.setOnClickListener(view -> openSelectedDirectory());

        choose.setOnClickListener(view -> getFolderAccess());

        scannerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartIntentSenderForResult(),
                        status -> {
                            if (status.getResultCode() == RESULT_OK) {
                                GmsDocumentScanningResult result = GmsDocumentScanningResult.fromActivityResultIntent(status.getData());
                                assert result != null;
                                for (GmsDocumentScanningResult.Page page : Objects.requireNonNull(result.getPages())) {
                                    Uri imageUri = page.getImageUri();
                                }

                                GmsDocumentScanningResult.Pdf pdf = result.getPdf();
                                assert pdf != null;
                                Uri pdfUri = pdf.getUri();
                                int pageCount = pdf.getPageCount();
                                setFileName(pdfUri);
                            }
                        });
        new_scan= findViewById(R.id.new_scan);
        sp = getSharedPreferences("com.example.academease", 0);
        String checkStorageString=sp.getString("FileStorageUri",null);
        if(checkStorageString==null) getFolderAccess();
        new_scan.setOnClickListener(v -> newScan());
    }
    private void getFolderAccess(){
        Toast.makeText(this,"Select the folder where you want to store your notes",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent,1234);
    }
    private void openSelectedDirectory() {
        String storedUriString = sp.getString("FileStorageUri", null);
        if(storedUriString==null) getFolderAccess();
        else {
            DocumentFile directory = DocumentFile.fromTreeUri(getApplicationContext(), Uri.parse(storedUriString));
            if (directory == null || !directory.exists()) {
                Toast.makeText(this, "Directory not found or inaccessible", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(directory.getUri());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(intent);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        //Log.d("onActivityResult","visited");
        if(resultCode==RESULT_OK){
            treeUri=data.getData();
            if(treeUri!=null){
                getContentResolver().takePersistableUriPermission(treeUri,Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("FileStorageUri",data.getData().toString());
                editor.apply();
            }
        }
        else Log.e("FileUtility", resultCode+"");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NotesActivity.this,MainActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}