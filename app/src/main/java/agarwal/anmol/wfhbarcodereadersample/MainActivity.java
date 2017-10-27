package agarwal.anmol.wfhbarcodereadersample;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.*;

import agarwal.anmol.wfhbarcodereader.WfhBarcodeScanner;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final int REQ_CODE = 12;

    TextView result;
    Button scanButton, scanDialogButton, scanImage ,btnPost;
    EditText barcodeTypes;
    Spinner modeSpinner;
    ImageView imageHolder;

    WfhBarcodeScanner.ScanningMode mMode = null;
    @WfhBarcodeScanner.BarCodeFormat
    int[] mFormats = null;

    Barcode mBarcode;
    List<Barcode> mBarcodes;

    final static HashMap<Integer, String> TYPE_MAP;
    final static String[] barcodeTypeItems;

    static {
        TYPE_MAP = new HashMap<>();

        TYPE_MAP.put(Barcode.ALL_FORMATS, "All Formats");
        TYPE_MAP.put(Barcode.AZTEC, "Aztec");
        TYPE_MAP.put(Barcode.CALENDAR_EVENT, "Calendar Event");
        TYPE_MAP.put(Barcode.CODABAR, "Codabar");
        TYPE_MAP.put(Barcode.CODE_39, "Code 39");
        TYPE_MAP.put(Barcode.CODE_93, "Code 93");
        TYPE_MAP.put(Barcode.CODE_128, "Code 128");
        TYPE_MAP.put(Barcode.CONTACT_INFO, "Contact Info");
        TYPE_MAP.put(Barcode.DATA_MATRIX, "Data Matrix");
        TYPE_MAP.put(Barcode.DRIVER_LICENSE, "Drivers License");
        TYPE_MAP.put(Barcode.EAN_8, "EAN 8");
        TYPE_MAP.put(Barcode.EAN_13, "EAN 13");
        TYPE_MAP.put(Barcode.EMAIL, "Email");
        TYPE_MAP.put(Barcode.GEO, "Geo");
        TYPE_MAP.put(Barcode.ISBN, "ISBN");
        TYPE_MAP.put(Barcode.ITF, "ITF");
        TYPE_MAP.put(Barcode.PDF417, "PDF 417");
        TYPE_MAP.put(Barcode.PHONE, "Phone");
        TYPE_MAP.put(Barcode.QR_CODE, "QR Code");
        TYPE_MAP.put(Barcode.PRODUCT, "Product");
        TYPE_MAP.put(Barcode.SMS, "SMS");
        TYPE_MAP.put(Barcode.UPC_A, "UPC A");
        TYPE_MAP.put(Barcode.UPC_E, "UPC E");
        TYPE_MAP.put(Barcode.TEXT, "Text");
        TYPE_MAP.put(Barcode.URL, "URL");

        List<String> items = new ArrayList<>(TYPE_MAP.values());
        Collections.sort(items);
        String[] tempArray = new String[items.size()];
        tempArray = items.toArray(tempArray);
        barcodeTypeItems = tempArray;
    }
    Person person;
    boolean[] checkedStates = new boolean[TYPE_MAP.size()];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // we are going to use asynctask to prevent network on main thread exception
        // new PostDataAsyncTask().execute();
        result = (TextView) findViewById(R.id.result);
        scanButton = (Button) findViewById(R.id.scan);
        scanDialogButton = (Button) findViewById(R.id.scan_dialog);
        barcodeTypes = (EditText) findViewById(R.id.barcode_types);
        modeSpinner = (Spinner) findViewById(R.id.scanner_mode);
        scanImage = (Button) findViewById(R.id.photo_button);
        imageHolder = (ImageView) findViewById(R.id.captured_photo);
        btnPost = (Button)findViewById(R.id.btnupload);
        // add click listener to Button "POST"
        btnPost.setOnClickListener(this);



        ArrayAdapter adapter = new SpinnerAdapter(this, R.layout.simple_spinner_item);
        modeSpinner.setAdapter(adapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mMode = WfhBarcodeScanner.ScanningMode.SINGLE_AUTO;
                        break;

                    case 1:
                        mMode = WfhBarcodeScanner.ScanningMode.SINGLE_MANUAL;
                        break;

                    case 2:
                        mMode = WfhBarcodeScanner.ScanningMode.MULTIPLE;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        scanButton.setOnClickListener(this);
        scanDialogButton.setOnClickListener(this);
        barcodeTypes.setOnClickListener(this);

        scanImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApp(MainActivity.this, "agarwal.anmol.wfhcameraclick");
            }


        });
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
            //throw new packageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == scanButton.getId()) {
            new WfhBarcodeScanner.Builder()
                    .setScanningMode(mMode)
                    .setFormats(mFormats)
                    .build()
                    .launchScanner(this, REQ_CODE);

            //Intent intent = new Intent(this, IssueDebugActivity.class);
            //startActivity(intent);
        } else if (view.getId() == barcodeTypes.getId()) {
            showBarcodeTypesPicker();
        } else if (view.getId() == scanDialogButton.getId()) {
            ScannerDialog dialog = ScannerDialog.instantiate(mMode, new ScannerDialog.DialogResultListener() {
                @Override
                public void onScanned(Barcode... barcode) {
                    if (barcode.length > 1) {
                        mBarcode = null;
                        mBarcodes = new ArrayList<Barcode>();

                        for (Barcode b : barcode) {
                            mBarcodes.add(b);
                        }
                    } else {
                        mBarcodes = null;
                        mBarcode = barcode[0];
                    }

                    updateBarcodeInfo();
                }

                @Override
                public void onFailed(String reason) {

                }
            }, mFormats);

            Log.d("MAIN", "showing scanner dialog");
            dialog.show(getSupportFragmentManager(), "SCANNER");
        }


 /*       switch(view.getId()){
            case R.id.btnupload:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                // call AsynTask to perform network operation on separate thread
                new HttpAsyncTask().execute("http://hmkcode.appspot.com/jsonservlet");
                break;
    }
    */
    }

    void showBarcodeTypesPicker() {
        final boolean[] checkedItems = Arrays.copyOf(checkedStates, checkedStates.length);

        new AlertDialog.Builder(this)
                .setTitle("Select Types")
                .setMultiChoiceItems(barcodeTypeItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedStates = checkedItems;
                        mFormats = getUpdatedFormats();
                    }
                })
                .show();
    }

    @WfhBarcodeScanner.BarCodeFormat
    int[] getUpdatedFormats() {
        List<Integer> formats = new ArrayList<>();
        String barcodes = "";
        int count = 0;
        for (int i = 0; i < checkedStates.length; i++) {
            if (checkedStates[i]) {
                int format = getFormatForValue(barcodeTypeItems[i]);
                if (format != -1) {
                    formats.add(format);
                    barcodes = barcodes + (count > 0 ? ", " : "") + barcodeTypeItems[i];
                    count++;
                }
            }
        }


        if (count > 0) {
            @WfhBarcodeScanner.BarCodeFormat int[] iFormats = new int[formats.size()];
            int i = 0;
            for (Integer f : formats) {
                iFormats[i] = f;
                i++;
            }

            barcodeTypes.setText(barcodes);
            return iFormats;
        } else {
            barcodeTypes.setText(null);
            return null;
        }
    }

    @WfhBarcodeScanner.BarCodeFormat
    int getFormatForValue(String value) {
        for (Map.Entry<Integer, String> entry : TYPE_MAP.entrySet()) {
            if (entry.getValue().equals(value)) return entry.getKey();
        }

        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE) {
            if (resultCode == RESULT_OK && data != null
                    && data.getExtras() != null) {
                Log.d("BARCODE-SCANNER", "onActivityResult inside block called");
                if (data.getExtras().containsKey(WfhBarcodeScanner.BarcodeObject)) {
                    mBarcode = data.getParcelableExtra(WfhBarcodeScanner.BarcodeObject);
                    mBarcodes = null;
                } else if (data.getExtras().containsKey(WfhBarcodeScanner.BarcodeObjects)) {
                    mBarcodes = data.getParcelableArrayListExtra(WfhBarcodeScanner.BarcodeObjects);
                    mBarcode = null;
                }
                updateBarcodeInfo();
            } else {
                mBarcode = null;
                mBarcodes = null;
                updateBarcodeInfo();
            }
        }
    }

    void updateBarcodeInfo() {
        StringBuilder builder = new StringBuilder();

        if (mBarcode != null) {
            Log.d("BARCODE-SCANNER", "got barcode");
            builder.append("Type: " + getBarcodeFormatName(mBarcode.format) +
                    "\nData: " + mBarcode.rawValue + "\n\n");
        }

        if (mBarcodes != null) {
            Log.d("BARCODE-SCANNER", "got barcodes");
            for (Barcode barcode : mBarcodes) {
                builder.append("Type: " + getBarcodeFormatName(barcode.format) +
                        "\nData: " + barcode.rawValue + "\n\n");
            }
        }

        if (builder.length() > 0)
            result.setText(builder.toString());
        else result.setText("");
    }

    String getBarcodeFormatName(int format) {
        return TYPE_MAP.get(format);
    }

    class ModeData {
        String name;
        String description;

        ModeData(String title, String message) {
            name = title;
            description = message;
        }
    }

    class SpinnerAdapter extends ArrayAdapter<ModeData> {
        final String[] Modes = {"Single - Auto", "Single - Manual", "Multiple"};
        final String[] Descriptions = {"Returns the first barcode it detects", "Returns the single barcode user tapped on",
                "Returns all the detected barcodes after tap"};

        /**
         * Constructor
         *
         * @param context  The current context.
         * @param resource The resource ID for a layout file containing a TextView to use when
         */
        public SpinnerAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return Modes.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null)
                view = LayoutInflater.from(getContext()).inflate(R.layout.simple_spinner_item, parent, false);

            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(Modes[position]);
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null)
                view = LayoutInflater.from(getContext()).inflate(R.layout.simple_spinner_dropdown_item, parent, false);

            TextView title = (TextView) view.findViewById(R.id.title);
            TextView message = (TextView) view.findViewById(R.id.message);

            title.setText(Modes[position]);
            message.setText(Descriptions[position]);
            return view;
        }
    }


 /*   public static String POST(String url, Person person){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
          //  HttpClientConnection httpclient = new DefaultBHttpClientConnection(url);

           HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name", result.getName());


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {


            person.setName(result.getText().toString());


            return POST(urls[0],person);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate(){
        if(result.getText().toString().trim().equals(""))
            return false;

        else
            return true;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
*/

}

