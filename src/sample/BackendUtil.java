package sample;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackendUtil {
    // Функция отправляющая запросы
    static public JSONArray Connection(String link, String keyword) throws IOException {
        boolean ok = false;
        JSONArray jsonOfGenres = null;

        while (!ok) {
            final URL url = new URL(link);
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            try (InputStream in = new BufferedInputStream(con.getInputStream());
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonOfData = (JSONObject) jsonParser.parse(reader);
                String strOfGenres = jsonOfData.get(keyword).toString();
                jsonOfGenres = (JSONArray) jsonParser.parse(strOfGenres);

                con.disconnect();
                ok = true;
            } catch (ParseException ignored) {}
        }
        return jsonOfGenres;
    }
}
