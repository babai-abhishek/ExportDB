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
 * Created by abhi on 23/8/16.
 */
public class XmlConverter implements IConverter {
    @Override
    public Boolean Convert(String TableName, String Data) {

        /* Local variables*/

        String json_string;
        JSONObject mJSONObject;
        JSONArray mJSONArray = null;
        BufferedWriter bw = null;
        FileWriter fw;

        /* declare Path to create file */

        String fpath = "/sdcard/" + TableName + ".xml";

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
            String xmlVersion ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            bw.append(xmlVersion);

            String rootTagStart = "<dataset>";
            bw.append(rootTagStart);

            for (int i = 0; i < mJSONArray.length(); i++) {
                JSONObject jobj = mJSONArray.getJSONObject(i);
                String subRootTagStart = "<data>";
                bw.append(subRootTagStart);

                Iterator<String> kys = jobj.keys();
                while (kys.hasNext()) {

                    String key = (String) kys.next();

                    String value = String.valueOf(jobj.get(key));
                    String element = "<"+key+">"+value+"</"+key+">";
                    bw.append(element);

                }
                String subRootagEnd = "</data>";
                bw.append(subRootagEnd);
            }

            String rootTagEnd = "</dataset>";
            bw.append(rootTagEnd);
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
