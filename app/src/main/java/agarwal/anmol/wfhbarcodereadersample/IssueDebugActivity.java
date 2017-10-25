package agarwal.anmol.wfhbarcodereadersample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import agarwal.anmol.wfhbarcodereader.BarcodeCaptureFragment;
import agarwal.anmol.wfhbarcodereader.WfhBarcodeScanner;

/**
 * Created by Anmol Agarwal  on 24/10/17.
 */

public class IssueDebugActivity extends AppCompatActivity implements BarcodeCaptureFragment.BarcodeScanningListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_16_layout);

        showScanner();
    }

    void showScanner(){
        BarcodeCaptureFragment fragment = BarcodeCaptureFragment.instantiate(WfhBarcodeScanner.ScanningMode.SINGLE_AUTO, Barcode.ALL_FORMATS);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout_camera_root, fragment)
                .commit();
    }

    @Override
    public void onBarcodeScanned(Barcode barcode) {
        logBarcode(barcode);
        finish();
    }

    @Override
    public void onBarcodesScanned(List<Barcode> barcodes) {
        for(Barcode barcode:barcodes){
            logBarcode(barcode);
        }
        finish();
    }

    @Override
    public void onBarcodeScanningFailed(String reason) {
        Log.d("ISSUE-DEBUG", "Scanning failed: " + reason);
        finish();
    }

    void logBarcode(Barcode barcode){
        Log.d("ISSUE-DEBUG", "type: " + barcode.valueFormat + "\ndata: " + barcode.displayValue);
    }
}
