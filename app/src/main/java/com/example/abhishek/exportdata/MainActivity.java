package com.example.abhishek.exportdata;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import DataConverter.ConverterFactory;
import DataConverter.IConverter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //variable to store database name
    String dbName;

    //declare 2 edit boxes
    EditText dbEditText;

    //other global variables
    Boolean dbBool = false, tbBool = false;
    String JSON_STRING, json_string;
    JSONObject mJSONObject;
    JSONArray mJSONArray;
    String tbName;

    //declare 2 buttons
    Button btnAdd, btnRemove, btnExport;

    //declear alert-dialog box to give response if database not found
    AlertDialog alertDialogue;

    //declear list view
    ListView lvLeft, lvRight;

    //variable for storing table names from database
    final ArrayList<String> tbList = new ArrayList<String>();

    //variable to fetch selected data from left side list
    final ArrayList<String> datafromLeftList = new ArrayList<String>();

    //String variable to get the selected data from lists
    String valueFromLeftList = "";
    String valueFromRightList = "";


    //variable to store export type from spinner
    String exType;

    //IConverter interface initialize;
    IConverter iConverter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get view from UI Editbox,Left list,Right list
        dbEditText = (EditText) findViewById(R.id.dbNameText);
        lvLeft = (ListView) findViewById(R.id.listView1);
        lvRight = (ListView) findViewById(R.id.listView2);

        //click to fetch table from the database if exists
        Button btnCheck = (Button) findViewById(R.id.btnCheckDbName);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvLeft.setAdapter(null);
                lvRight.setAdapter(null);
                datafromLeftList.clear();
                tbList.clear();
//                valueFromLeftList ="";

                //get database name from user
                dbName = dbEditText.getText().toString();

                //pass database name to the databasehelper class to do the database works
                final String method = "checkDataBase";
                DBHelper dataBaseHelper = new DBHelper();
                dataBaseHelper.execute(method, dbName);

            }
        });

        //click to add data to the right side list
        btnAdd = (Button) findViewById(R.id.moveToRightButton);

        //"+" button click event to pass data from left to right list
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check selected data from left list
                if (valueFromLeftList == "") {
                    Toast.makeText(MainActivity.this, "Select an item first ", Toast.LENGTH_SHORT).show();
                } else {
                    //add selected data from left list to arraylist to show in right list
                    datafromLeftList.add(valueFromLeftList);

                    //create list view
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, datafromLeftList);

                    //show view in the list
                    lvRight.setAdapter(adapter1);

                    //remove selected data from left side list(tblist is the arraylist used in the left side list)
                    tbList.remove(valueFromLeftList);

                    //uopdate the right side list
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, tbList);
                    lvLeft.setAdapter(adapter);

                    //remove data from the string variable used to store selected data from left list
                    valueFromLeftList = "";

                }

            }
        });

        //select data from right side list
        lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //select data from right side list
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int j = 0; j < parent.getChildCount(); j++) {
                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                }
                view.setBackgroundColor(Color.LTGRAY);

                //store the selected data from right side list
                valueFromRightList = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
            }
        });

        //click data to remove data from right side list
        btnRemove = (Button) findViewById(R.id.moveToLeftButton);

        //"-" button click event to pass data from right to left list
        btnRemove.setOnClickListener(new View.OnClickListener() {

            //check the selected data from right side list
            @Override
            public void onClick(View v) {
                if (valueFromRightList.equals("")) {
                    Toast.makeText(MainActivity.this, "Select an item first ", Toast.LENGTH_SHORT).show();
                } else {

                    //remove selected data from the arraylist used in right side list
                    datafromLeftList.remove(valueFromRightList);

                    //show the updated view of right side list
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, datafromLeftList);
                    lvRight.setAdapter(adapter1);

                    //update the left side list
                    tbList.add(valueFromRightList);
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, tbList);
                    lvLeft.setAdapter(adapter);

                    ///remove data from the string variable used to store selected data from right list
                    valueFromRightList = "";

                }
            }
        });

        /*collect ecport types from Confid.xml file
        * by using ConverterFactory class*/
        ArrayList<String> arrayList = new ConverterFactory(MainActivity.this).getExportType();
        final Spinner exportType = (Spinner) findViewById(R.id.selectType_spinner);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exportType.setAdapter(adapter);
        exportType.setOnItemSelectedListener(this);

        //button for export type selection
        btnExport = (Button) findViewById(R.id.btn_select_export_type);

        //export button click event
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String method = "fetchTableData";
                if (valueFromRightList.equals("")) {
                    Toast.makeText(MainActivity.this, "Select Item first from the right side list ", Toast.LENGTH_SHORT).show();
                } else {
                    DBHelper dbHelper = new DBHelper();
                    dbHelper.execute(method, valueFromRightList, dbName);
                }

            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        exType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //class to do all the database oriented works
    public class DBHelper extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];

            if (method.equals("checkDataBase")) {

                String dataBaseUrl = new GetURL().getUrl() + "MetadataServlet";
                String dbName = params[1];
                StringBuilder stringBuilder = null;
                dbBool = true;
                try {
                    URL url = new URL(dataBaseUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String data = URLEncoder.encode("dbName", "UTF-8") + "=" + URLEncoder.encode(dbName, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    Log.d("#", String.valueOf(httpURLConnection.getResponseCode()));
                    if(httpURLConnection.getResponseCode()==200){
                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                        stringBuilder = new StringBuilder();
                        while ((JSON_STRING = bufferedReader.readLine()) != null) {
                            stringBuilder.append(JSON_STRING + "\n");
                        }
                        bufferedReader.close();
                        inputStream.close();
                        httpURLConnection.disconnect();
                        return stringBuilder.toString().trim();
                    }else if(httpURLConnection.getResponseCode()==404){
                        return "";
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (method.equals("fetchTableData")) {
                String dataBaseUrl = new GetURL().getUrl() + "TableContentServlet";
                String tbName = params[1];
                String dbName = params[2];

                try {
                    URL url = new URL(dataBaseUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String data = URLEncoder.encode("dbName", "UTF-8") + "=" + URLEncoder.encode(dbName, "UTF-8") + "&" +
                            URLEncoder.encode("tbName", "UTF-8") + "=" + URLEncoder.encode(tbName, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((JSON_STRING = bufferedReader.readLine()) != null) {
                        stringBuilder.append(JSON_STRING + "\n");
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    tbBool = true;
                    return stringBuilder.toString().trim();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            alertDialogue = new AlertDialog.Builder(MainActivity.this).create();
            alertDialogue.setTitle("Data Report");
        }

        @Override
        protected void onPostExecute(String s) {

            if (dbBool == true) {
                dbBool = false;
            Log.d("#",s);
                if (s.equals("")) {
                    alertDialogue.setMessage("Error occured. Check DataBase Name.");
                    alertDialogue.show();
                    cancel(true);
                } else {
                    json_string = s;
                    try {
                        mJSONObject = new JSONObject(json_string);
                        mJSONArray = mJSONObject.getJSONArray("server_response");

                        for (int i = 0; i < mJSONArray.length(); i++) {
                            JSONObject jo = mJSONArray.getJSONObject(i);
                            tbName = jo.optString("tableName");
                            tbList.add(tbName);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //show list of table names of the database on the left side list
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, tbList);
                    lvLeft.setAdapter(adapter);

                    //select data from left side list
                    lvLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                            for (int j = 0; j < parent.getChildCount(); j++) {
                                parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                            }
                            view.setBackgroundColor(Color.LTGRAY);

                            //store the selected data from left side list
                            valueFromLeftList = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();

                        }
                    });


                }
            } else if (tbBool == true) {
                json_string = s;

                try {

                    iConverter = new ConverterFactory(MainActivity.this).getConverter(exType);

                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                boolean bool = iConverter.Convert(valueFromRightList, json_string);

                /*Toast messages on successful or unsucessful file saving*/
                if (bool == true) {
                    Toast.makeText(MainActivity.this, "File saved successfully ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error occured . Try again ", Toast.LENGTH_SHORT).show();

                }

            }
        }

    }

}