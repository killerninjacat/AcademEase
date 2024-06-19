package com.nithinbalan.academease.activities;

import static com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF;
import static com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nithinbalan.academease.ClickListener;
import com.nithinbalan.academease.LongClickListener;
import com.example.academease.R;
import com.nithinbalan.academease.adapters.NotesAdapter;
import com.nithinbalan.academease.adapters.notesData;
import com.google.gson.Gson;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.io.InputStream;

public class NotesActivity extends AppCompatActivity {
    Button new_note,home,new_scan;
    String t,d;
    List<String> titles, descriptions;
    List<notesData> displaylist;
    private SharedPreferences sp;
    NotesAdapter notesAdapter;
    RecyclerView recyclerView;
    ClickListener clickListener;
    LongClickListener longClickListener;
    private Uri treeUri;
    private ActivityResultLauncher<IntentSenderRequest> scannerLauncher;
    @SuppressLint("SetTextI18n")
    public void deleteConfirmation(int index)
    {
        final Dialog dialog = new Dialog(NotesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_confirmation);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button yes,no;
        TextView textView;
        textView=dialog.findViewById(R.id.deleteText);
        textView.setText("Do you really want to delete this note?");
        yes=dialog.findViewById(R.id.yes_att);
        no=dialog.findViewById(R.id.no_att);
        Gson gson=new Gson();
        yes.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                titles.remove(index);
                descriptions.remove(index);
                displaylist.remove(index);
                notesAdapter.notifyDataSetChanged();
                String json=gson.toJson(titles);
                String json1=gson.toJson(descriptions);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("titles",json);
                editor.putString("descriptions",json1);
                editor.apply();
                dialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void editDeleteDialog(String title, String description, int index)
    {
        final Dialog dialog = new Dialog(NotesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.edit_delete_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button edit,delete;
        edit=dialog.findViewById(R.id.editAtt);
        delete=dialog.findViewById(R.id.deleteAtt);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConfirmation(index);
                dialog.dismiss();
            }
        });
        edit.setOnClickListener(v -> {
            editNotedialog(title,description,index);
            dialog.dismiss();
        });
        dialog.show();
    }
    public void eachNote(String title,String description)
    {
        Button close;
        TextView ti,de;
        final Dialog dialog = new Dialog(NotesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.each_note_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        close=dialog.findViewById(R.id.close1);
        ti=dialog.findViewById(R.id.titleindialog);
        de=dialog.findViewById(R.id.descriptionindialog);
        ti.setText(title);
        de.setText(description);
        close.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    @SuppressLint("SetTextI18n")
    public void editNotedialog(String title, String description, int index)
    {
        EditText titlebox,contentbox;
        TextView newNotebox;
        Button close,save;
        final Dialog dialog = new Dialog(NotesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.new_note_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dark_layout_rounded);
        titlebox=dialog.findViewById(R.id.titlebox);
        contentbox=dialog.findViewById(R.id.contentbox);
        newNotebox=dialog.findViewById(R.id.newNotebox);
        newNotebox.setText("Edit Note");
        titlebox.setText(title);
        contentbox.setText(description);
        close=dialog.findViewById(R.id.close);
        save=dialog.findViewById(R.id.save_note);
        Gson gson=new Gson();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                t=titlebox.getText().toString();
                d=contentbox.getText().toString();
                if(t.equals("") && d.equals("")) {
                    Toast.makeText(NotesActivity.this,"Add a title or description!",Toast.LENGTH_SHORT).show();
                }
                else {
                    titles.remove(index);
                    descriptions.remove(index);
                    titles.add(t);
                    descriptions.add(d);
                    for (notesData note : displaylist) {
                        if (note.getTitle().equals(title) && note.getDescription().equals(description)) {
                            displaylist.remove(note);
                            break;
                        }
                    }
                    displaylist.add(new notesData(t,d));
                    notesAdapter.notifyDataSetChanged();
                    String json=gson.toJson(titles);
                    String json1=gson.toJson(descriptions);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("titles",json);
                    editor.putString("descriptions",json1);
                    editor.apply();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
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
    @SuppressLint("NotifyDataSetChanged")
    public void newNote()
    {
        EditText titlebox,contentbox;
        Button close,save;
        final Dialog dialog = new Dialog(NotesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.new_note_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dark_layout_rounded);
        titlebox=dialog.findViewById(R.id.titlebox);
        contentbox=dialog.findViewById(R.id.contentbox);
        close=dialog.findViewById(R.id.close);
        save=dialog.findViewById(R.id.save_note);
        Gson gson=new Gson();
        close.setOnClickListener(v -> dialog.dismiss());
        save.setOnClickListener(v -> {
            t=titlebox.getText().toString();
            d=contentbox.getText().toString();
            if(t.equals("") && d.equals("")) {
                Toast.makeText(NotesActivity.this,"Add a title or description!",Toast.LENGTH_SHORT).show();
            }
            else {
                titles.add(t);
                descriptions.add(d);
                displaylist.add(new notesData(t,d));
                notesAdapter.notifyDataSetChanged();
                String json=gson.toJson(titles);
                String json1=gson.toJson(descriptions);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("titles",json);
                editor.putString("descriptions",json1);
                editor.apply();
                dialog.dismiss();
            }
        });
        dialog.show();
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
        new_note= findViewById(R.id.new_note);
        new_scan= findViewById(R.id.new_scan);
        recyclerView= findViewById(R.id.recyclerView);
        titles=new ArrayList<>();
        descriptions=new ArrayList<>();
        Gson gson=new Gson();
        sp = getSharedPreferences("com.example.academease", 0);
        String checkStorageString=sp.getString("FileStorageUri",null);
        if(checkStorageString==null) getFolderAccess();
        displaylist=new ArrayList<>();
       String json=sp.getString("titles",null);
       String json1=sp.getString("descriptions",null);
       Log.d("json", "json: "+json);
       titles=gson.fromJson(json,ArrayList.class);
       descriptions=gson.fromJson(json1,ArrayList.class);
       if(titles==null) {
           titles = new ArrayList<>();
           titles.add("PDF Notes");
       }
       if(descriptions==null) {
           descriptions = new ArrayList<>();
           descriptions.add("Click here");
       }
       if(titles!=null)
        for(int i=0;i<titles.size();i++)
       {
           displaylist.add(new notesData(titles.get(i),descriptions.get(i)));
       }
       clickListener = index -> {
           if(index==0) openSelectedDirectory();
           else eachNote(titles.get(index), descriptions.get(index));
       };
       longClickListener= index -> {
           if(index!=0) editDeleteDialog(titles.get(index), descriptions.get(index), index);
       };
        home= findViewById(R.id.home_n);
        home.setOnClickListener(v -> {
            startActivity(new Intent(NotesActivity.this,MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        });
        if(displaylist==null) {
            displaylist = new ArrayList<>();
        }
       notesAdapter =new NotesAdapter(displaylist,getApplication(),clickListener,longClickListener);
       recyclerView.setAdapter(notesAdapter);
       recyclerView.setLayoutManager(new LinearLayoutManager(NotesActivity.this));
        new_note.setOnClickListener(v -> newNote());
        new_scan.setOnClickListener(v -> newScan());
    }
    private void getFolderAccess(){
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent,1234);
    }
    private void openSelectedDirectory() {
        String storedUriString = sp.getString("FileStorageUri", null);
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