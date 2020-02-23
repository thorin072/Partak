package com.delaquess.doodlz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class AWS extends Activity {

    private static volatile TransferUtility transferUtility;

    private PictureDobot pack;
    private String jsonResponse;
    private String url;
    private byte[] BitmapUser;
    private String file_sd_name = "/coordinate.json";
    private String file_sd = "/coordinate.jpg";
    private QrAuth qr;
    private ImageView QrView;
    private ImageView AwsView;
    private TextView AwsStatus;
    private TextView QrStatus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aws);
        QrView = (ImageView) findViewById(R.id.qr_logo);
        AwsView = (ImageView) findViewById(R.id.aws_logo);
        AwsStatus = (TextView) findViewById(R.id.aws_status);
        QrStatus = (TextView) findViewById(R.id.qr_text);
        Intent intent = this.getIntent();

        if (intent.getExtras() != null) {
            url = intent.getStringExtra("url");
            jsonResponse = intent.getStringExtra("json");
            BitmapUser = intent.getByteArrayExtra("bitmap_user");
        }


        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("YourMainActivity",
                        "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        uploadWithTransferUtility(url, jsonResponse);

    }


    public void uploadWithTransferUtility(String url, String pack) {

        AWSConfiguration config = new AWSConfiguration(this);

        transferUtility = TransferUtility.builder()
                .context(this)
                .awsConfiguration(config)
                .s3Client(new AmazonS3Client(new CognitoCachingCredentialsProvider(this, config)))
                .build();

        getJSONFILE(file_sd_name, pack);

        TransferObserver uploadObserver =
                transferUtility.upload("public/dobotS3/" + url, new File(this.getExternalFilesDir(
                        Environment.DIRECTORY_DOWNLOADS).getPath(), file_sd_name));

        url = url.substring(0, url.length() - 4);

        TransferObserver uploadObserver2 =
                transferUtility.upload("public/dobotS3/" + url + "jpg", new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), file_sd));

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new UploadListener());
        uploadObserver2.setTransferListener(new UploadListener());
    }

    private class UploadListener implements TransferListener {

        @Override
        public void onStateChanged(int id, TransferState state) {

            Log.d("YourActivity", "onStateChanged: " + id + ", " + state.toString());


            // If upload error, failed or network disconnect
            if (state == TransferState.WAITING || state == TransferState.FAILED ||
                    state == TransferState.WAITING_FOR_NETWORK) {

                AwsView.setImageResource(R.drawable.logo_error_aws);
                AwsStatus.setText(R.string.message_error_aws);
                QrView.setImageResource(R.drawable.logo_error_qr);
                QrStatus.setText(R.string.message_qr_error);

                Log.d("MESS AWS", "S O S A T");
            } else {

                AwsView.setImageResource(R.drawable.logo_good_aws);
                AwsStatus.setText(R.string.message_success_aws);
                qr = new QrAuth();
                QrView.setImageBitmap(qr.qrGeneration(url));
                QrStatus.setText(R.string.message_qr_success);
                //Log.d("MESS AWS", "BLAYAT HIS WORK");
            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
            int percentDone = (int) percentDonef;

            Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
        }

        @Override
        public void onError(int id, Exception ex) {
            ex.printStackTrace();
        }
    }


    public void getJSONFILE(String jsonName, String pack) {

        File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), jsonName);
        try {

            FileWriter writer = new FileWriter(file, false);
            writer.write(pack);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


