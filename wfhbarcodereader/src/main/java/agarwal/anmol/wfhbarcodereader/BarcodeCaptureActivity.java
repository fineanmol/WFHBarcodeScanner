
package agarwal.anmol.wfhbarcodereader;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public final class BarcodeCaptureActivity extends AppCompatActivity implements BarcodeCaptureFragment.BarcodeScanningListener {
    private static final String TAG = "Barcode-reader";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.barcode_capture_activity);

        BarcodeCaptureFragment fragment = null;
        WfhBarcodeScanner.ScanningMode mode = null;
        @WfhBarcodeScanner.BarCodeFormat int[] formats = null;

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(WfhBarcodeScanner.SCANNING_MODE)) {
                mode = (WfhBarcodeScanner.ScanningMode) getIntent().getExtras().getSerializable(WfhBarcodeScanner.SCANNING_MODE);
            }

            if (getIntent().getExtras().containsKey(WfhBarcodeScanner.BARCODE_FORMATS)) {
                //noinspection WrongConstant
                formats = getIntent().getExtras().getIntArray(WfhBarcodeScanner.BARCODE_FORMATS);
            }
        }

        fragment = BarcodeCaptureFragment.instantiate(mode, formats);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
        Log.d(TAG, "fragment added");
    }

    @Override
    public void onBarcodeScanned(Barcode barcode) {
        Log.d("BARCODE-SCANNER", "got barcode in scanner");
        Intent data = new Intent();
        data.putExtra(WfhBarcodeScanner.BarcodeObject, barcode);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBarcodesScanned(List<Barcode> barcodes) {
        Log.d("BARCODE-SCANNER", "got s in scanner");
        Intent data = new Intent();
        data.putParcelableArrayListExtra(WfhBarcodeScanner.BarcodeObjects, (ArrayList<Barcode>) barcodes);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBarcodeScanningFailed(String reason) {
        new AlertDialog.Builder(this).setTitle("Could not scan barcode")
                .setMessage(reason)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }
}
