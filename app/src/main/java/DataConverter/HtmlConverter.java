package DataConverter;

import android.util.Log;

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
public class HtmlConverter implements IConverter{
    @Override
    public Boolean Convert(String TableName, String Data) {

        Log.d("Abhi","in html convertr");
        /* Local variables*/


        String json_string;
        JSONObject mJSONObject;
        JSONArray mJSONArray = null;
        BufferedWriter bw = null;
        FileWriter fw;

          /* declare Path to create file */

        String fpath = "/sdcard/" + TableName + ".html";


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

        try {
            /* html head and title */

            String htmlCOntentHead = "<html>\n" +
                    "<head>" +
                    "<title>" + TableName + "</title></head>\n";
            bw.append(htmlCOntentHead);

                 /* html body including table with column names and \
            * rows with data*/

            String htmlContentBody = "<body>\n<h>Table name : " + TableName + "</h>\n<table style='margin-top:30px;' border='1'>\n";
            bw.append(htmlContentBody);

            JSONObject jo = mJSONArray.getJSONObject(0);
            Iterator<String> keys = jo.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String htmlCOntentBodyColumn = "<th>" + key + "</th>\n";
                bw.append(htmlCOntentBodyColumn);
            }


            for (int i = 0; i < mJSONArray.length(); i++) {
                JSONObject jobj = mJSONArray.getJSONObject(i);
                String htmltrStart = "<tr>\n";
                bw.append(htmltrStart);
                Iterator<String> kys = jobj.keys();
                while (kys.hasNext()) {
                    String key = (String) kys.next();
                    String value = String.valueOf(jobj.get(key));
                    String htmlCOntentBodyRow = "<td>" + value + "</td>\n";
                    bw.append(htmlCOntentBodyRow);
                }
                String htmltrClose = "</tr>\n";
                bw.append(htmltrClose);
            }

            String htmlCOntentEnd = "</table>\n</body>\n</html>";
            bw.append(htmlCOntentEnd);
            bw.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }
}
