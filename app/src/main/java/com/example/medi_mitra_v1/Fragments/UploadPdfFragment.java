package com.example.medi_mitra_v1.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;



import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medi_mitra_v1.Client.ClientMainActivity;
import com.example.medi_mitra_v1.Models.PdfInfoModel;
import com.example.medi_mitra_v1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kerols.pdfconverter.CallBacks;
import com.kerols.pdfconverter.ImageToPdf;
import com.kerols.pdfconverter.InSize;
import com.kerols.pdfconverter.PdfImageSetting;
import com.kerols.pdfconverter.PdfPage;
import com.kerols.pdfconverter.Value;

import java.io.File;

public class UploadPdfFragment extends Fragment {
    private int Permission_Storage_Id=2;
    EditText pdfName,docName,hName;
    Button select,upload;
    TextView noti;
    File f;
    int PICK_IMAGE_MULTIPLE = 1;
    Uri PDFUri;
    PdfPage pdfPage;
    PdfImageSetting mPdfImageSetting;

    ImageToPdf imageToPdf;
    ImageButton GalleryIB,PdfIB;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    boolean SelectPdf=false,SelectImage=false;

    public UploadPdfFragment()
    {
        // Required empty public constructor
    }




    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        pdfName.setText("");
        hName.setText("");
        PDFUri=null;
        docName.setText("");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upload_pdf, container, false);
        storage  = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        pdfPage = new PdfPage(getContext());
        pdfPage.setPageSize(Value.IMAGE_SIZE);
        imageToPdf = new ImageToPdf(pdfPage,getContext());
        mPdfImageSetting= new PdfImageSetting();
        mPdfImageSetting.setImageSize(InSize.IMAGE_SIZE);
        mPdfImageSetting.setMargin(0,5,10,10);
        pdfPage.add(mPdfImageSetting);
        GalleryIB =v.findViewById(R.id.selectGalleryIBUPF);
        PdfIB = v.findViewById(R.id.selectPdfIBUPF);
        upload = v.findViewById(R.id.UploadToDatabaseUPF);
        noti = v.findViewById(R.id.PdfNameUPF);
        pdfName =v.findViewById(R.id.EtPdfNameUPF);
        docName =v.findViewById(R.id.EtDoctorNameUPF);
        hName =v.findViewById(R.id.EtHospitalNameUPF);
        PdfIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    selectOption();
                }
                else {
                    try {
                        getStoragePermission();
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        GalleryIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStoragePermission();
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {

                    openGallery();

                }
                else {
                    getStoragePermission();

                }
            }
        });



        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( TextUtils.isEmpty(pdfName.getText().toString()))
                    pdfName.setError("Please Enter Pdf Name.");
                else if(PDFUri==null ) {
                    noti.setTextColor(getResources().getColor(R.color.red));
                    noti.setText("Please Select Pdf");
                }
                else{
                    uploadFile(PDFUri);
                }
            }
        });


        return v;
    }

    private void uploadFile(Uri pdfUri) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File...");
        progressDialog.setProgress(0);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = storage.getReference();
        String timestamp = System.currentTimeMillis()+"";
        storageReference.child("Users").child(uid).child("Uploads").child(timestamp).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete());
                        Uri uri = uriTask.getResult();
                        DatabaseReference databaseReference = database.getReference();
                        PdfInfoModel obj = new PdfInfoModel(pdfName.getText().toString(),docName.getText().toString(),hName.getText().toString(),uri.toString(),timestamp,"false","false");
                        databaseReference.child("Users").child(uid).child("Uploads").child(timestamp).setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.flFragment, new ViewListFragment(), "NewFragmentTag");
                                    ft.commit();
                                    Toast.makeText(getActivity(), "Task Completed", Toast.LENGTH_SHORT).show();
                                    try {
                                        if(f.exists())
                                            f.delete();
                                    }
                                    catch (Exception e)
                                    {

                                    }

                                }
                                else
                                {
                                    Toast.makeText(getActivity(), "Task Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        progressDialog.hide();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        int currentProgress = (int)(100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        progressDialog.setProgress(currentProgress);
                    }
                });

    }



    private void selectOption() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);  //open file manager
        startActivityForResult(intent,0);

    }
    private void openGallery()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }
    protected void getStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Permission_Storage_Id);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        }
        else {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Storage Permission Needed")
                        .setMessage("Storage Permission is Needed to Access External Files")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Permission_Storage_Id);
                                Intent intent=new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri=Uri.fromParts("package", getContext().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                Toast.makeText(getContext(),"Grant Permission from Permission Tab",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Permission_Storage_Id);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Permission_Storage_Id)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getContext(),"Grant Permission from Permission Tab",Toast.LENGTH_SHORT).show();
                selectOption();
            }
            else
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Permission_Storage_Id);
                Intent intent=new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri=Uri.fromParts("package", getContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                Toast.makeText(getContext(),"Grant Permission from Permission Tab",Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(getContext(), "Onresults false", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check valid selection
        if(requestCode==0  && resultCode==RESULT_OK && data != null)
        {
            PDFUri = data.getData();
            noti.setText(PDFUri.getLastPathSegment());
            noti.setTextColor(getResources().getColor(R.color.teal_700));
        }
        else if(requestCode==1  && resultCode==RESULT_OK && data != null)
        {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Converting Image to Pdf...");
            progressDialog.setProgress(0);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            imageToPdf.DataToPDF(data,
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            "ImageToPdf.pdf"), new CallBacks() {
                        @Override
                        public void onFinish(String path) {
                            progressDialog.hide();
                            Toast.makeText(getContext(), "Conversion Successful", Toast.LENGTH_SHORT).show();
                            f= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/ImageToPdf.pdf");
                            PDFUri = Uri.fromFile(f);
                            noti.setText(PDFUri.getLastPathSegment());
                        }
                        @Override
                        public void onError(Throwable throwable) {
                            Toast.makeText(getContext(),"onError",Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onError: ", throwable );
                            progressDialog.hide();
                        }
                        @Override
                        public void onProgress(int progress , int max) {

                            int currentProgress =(int) progress/max;
                            progressDialog.setProgress(currentProgress);
                            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    imageToPdf.Cancel();
                                }
                            });
                        }
                        @Override
                        public void onCancel() {
                            progressDialog.hide();
                        }
                        @Override
                        public void onStart() {
                            Toast.makeText(getContext(),"Converting Images to Pdf",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
        {
            Toast.makeText(getContext(), "Selection Error", Toast.LENGTH_SHORT).show();
        }


    }
}