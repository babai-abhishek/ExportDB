package DataConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by abhi on 18/8/16.
 */
public class CsvConverter implements IConverter {
    @Override
    public Boolean Convert(String TableName, String Data) {

        /* Local variables*/

        String json_string;
        JSONObject mJSONObject;
        JSONArray mJSONArray = null;
        BufferedWriter bw = null;
        FileWriter fw;

        /* declare Path to create file */

        String fpath = "/sdcard/" + TableName + ".csv";

        /* Store the json data into local variables*/

        json_string = Data;
        try {
            mJSONObject = new JSONObject(json_string);
            mJSONArray = mJSONObject.getJSONArray("server_response");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        /* create file to store exported data*/

        try {
            File file = new File(fpath);
            file.createNewFile();
            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jo = null;
        try {
            jo = mJSONArray.getJSONObject(0);
            Iterator<String> keys = jo.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String CsvCOntentBodyColumn = key + "\t";
                bw.append(CsvCOntentBodyColumn);
            }
            bw.append("\n\n");

            for (int i = 0; i < mJSONArray.length(); i++) {
                JSONObject jobj = mJSONArray.getJSONObject(i);
                Iterator<String> kys = jobj.keys();
                while (kys.hasNext()) {
                    String key = (String) kys.next();
                    String value = String.valueOf(jobj.get(key));
                    String csvBodyRow = value + ",\t";
                    bw.append(csvBodyRow);
                }
                bw.append("\n");

            }
            bw.close();
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
