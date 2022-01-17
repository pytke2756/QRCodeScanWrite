package com.example.qrcodescanwrite;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {

    private Button btnScan;
    private Button btnGenerate;
    private TextView txtQrResult;
    private ImageView ivQrOutput;
    private EditText etQrInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Kamera megnyitása qr code lekérése
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                //kód typus scannelése
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                //kamera cím adás
                intentIntegrator.setPrompt("Qr code olvasás");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.initiateScan();
            }
        });
        
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seged = etQrInput.getText().toString();
                if (seged.isEmpty()){
                    Toast.makeText(MainActivity.this, "Nem lehet üres a mező", Toast.LENGTH_SHORT).show();
                }else{
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        //A szövegből QR_CODE bitmátrixot készitünk
                        BitMatrix bitMatrix = multiFormatWriter.encode(seged, BarcodeFormat.QR_CODE,
                                500,500);
                        //bitmátrixot át kell írni bitmapra, ezt tudja kezelni az imageveiw
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        ivQrOutput.setImageBitmap(bitmap);
                    }catch (WriterException we){
                        we.printStackTrace();
                    }
                }
            }
        });
    }
    
    //azért kell nekünk, mert amikor a kamera megnyílik és leolvassa a qr codeot akkor utána az
    // adatot visszadja az activitynek (magának)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //megvizsgáljuk, hogy valós scan hívás volt e
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                //kiléptunk a scannerből
                Toast.makeText(this, "Kiléptél a scannerből", Toast.LENGTH_SHORT).show();
            }else{
                txtQrResult.setText(result.getContents());

                //qr codeban url van megrpóbáljuk megynitni
                try {
                    Uri url = Uri.parse(result.getContents());
                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(intent);
                }catch (Exception ex){
                    Log.d("URL ERROR", ex.toString());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init(){
        btnScan = findViewById(R.id.btn_scan);
        btnGenerate = findViewById(R.id.btn_generate);
        txtQrResult = findViewById(R.id.txt_qr_result);
        ivQrOutput = findViewById(R.id.iv_qr_output);
        etQrInput = findViewById(R.id.et_qr_input);
    }
}