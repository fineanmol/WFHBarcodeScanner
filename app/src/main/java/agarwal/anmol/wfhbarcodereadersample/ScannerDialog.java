package agarwal.anmol.wfhbarcodereadersample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import agarwal.anmol.wfhbarcodereader.BarcodeCaptureFragment;
import agarwal.anmol.wfhbarcodereader.WfhBarcodeScanner;
import online.devliving.mobilevisionpipeline.camera.CameraSourcePreview;

/**
 * Created by Anmol Agarwal  on 24/10/17.
 */

public class ScannerDialog extends DialogFragment implements BarcodeCaptureFragment.BarcodeScanningListener {

    public interface DialogResultListener{
        void onScanned(Barcode... barcode);
        void onFailed(String reason);
    }

    public static ScannerDialog instantiate(WfhBarcodeScanner.ScanningMode mode, DialogResultListener listener,
                                            @WfhBarcodeScanner.BarCodeFormat int... formats) {
        ScannerDialog fragment = new ScannerDialog();
        fragment.mListener = listener;
        Bundle args = new Bundle();
        if (mode != null) args.putSerializable(WfhBarcodeScanner.SCANNING_MODE, mode);

        if (formats != null && formats.length > 0) {
            int barcodeFormats = formats[0];

            if (formats.length > 1) {
                for (int i = 1; i < formats.length; i++) {
                    barcodeFormats = barcodeFormats | formats[i];
                }
            }

            args.putInt(WfhBarcodeScanner.BARCODE_FORMATS, barcodeFormats);
        }
        fragment.setArguments(args);
        return fragment;
    }

    private DialogResultListener mListener;
    WfhBarcodeScanner.ScanningMode mMode = WfhBarcodeScanner.ScanningMode.SINGLE_AUTO;
    int mFormats = Barcode.ALL_FORMATS;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof DialogResultListener){
            mListener = (DialogResultListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !getArguments().isEmpty()) {
            if (getArguments().containsKey(WfhBarcodeScanner.SCANNING_MODE))
                mMode = (WfhBarcodeScanner.ScanningMode) getArguments().getSerializable(WfhBarcodeScanner.SCANNING_MODE);

            if (getArguments().containsKey(WfhBarcodeScanner.BARCODE_FORMATS))
                mFormats = getArguments().getInt(WfhBarcodeScanner.BARCODE_FORMATS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BarcodeCaptureFragment fragment = BarcodeCaptureFragment.instantiate(mMode, CameraSourcePreview.PreviewScaleType.FIT_CENTER,
                mFormats);
        fragment.setListener(this);
        getChildFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onBarcodeScanned(Barcode barcode) {
        if(mListener != null) mListener.onScanned(barcode);
        dismiss();
    }

    @Override
    public void onBarcodesScanned(List<Barcode> barcodes) {
        if(mListener != null){
            Barcode[] codes = new Barcode[barcodes.size()];
            barcodes.toArray(codes);
            mListener.onScanned(codes);
        }
        dismiss();
    }

    @Override
    public void onBarcodeScanningFailed(String reason) {
        if(mListener != null) mListener.onFailed(reason);
        dismiss();
    }
}
