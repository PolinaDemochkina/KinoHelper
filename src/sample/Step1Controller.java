package sample;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Step1Controller {
    @FXML
    private Button NextButton;

    @FXML
    private CheckBox AdventureCheck;

    @FXML
    private CheckBox ComedyCheck;

    @FXML
    private CheckBox FantasyCheck;

    @FXML
    private CheckBox CartoonCheck;

    @FXML
    private CheckBox ActionCheck;

    @FXML
    private CheckBox WarCheck;

    @FXML
    private CheckBox HorrorsCheck;

    @FXML
    private CheckBox ThrillerCheck;

    @FXML
    private CheckBox CrimeCheck;

    @FXML
    private CheckBox FamilyCheck;

    @FXML
    private CheckBox DramaCheck;

    @FXML
    private CheckBox DocumentaryCheck;

    @FXML
    private CheckBox HistoryCheck;

    @FXML
    private CheckBox ScienceCheck;

    @FXML
    private CheckBox RomanceCheck;

    @FXML
    private CheckBox MysteryCheck;

    @FXML
    private CheckBox Age;

    private final CheckBox[] genres = new CheckBox[16];

    static boolean age = false;

    static Stage stage;

    static Vector<Integer> IDs = new Vector<>(1, 1);

    @FXML
    void initialize() {
        NextButton.setDisable(true);

        genres[0] = AdventureCheck;
        genres[1] = ComedyCheck;
        genres[2] = ThrillerCheck;
        genres[3] = HorrorsCheck;
        genres[4] = MysteryCheck;
        genres[5] = ActionCheck;
        genres[6] = CartoonCheck;
        genres[7] = FantasyCheck;
        genres[8] = RomanceCheck;
        genres[9] = ScienceCheck;
        genres[10] = HistoryCheck;
        genres[11] = DocumentaryCheck;
        genres[12] = DramaCheck;
        genres[13] = FamilyCheck;
        genres[14] = CrimeCheck;
        genres[15] = WarCheck;

        for (CheckBox check : genres) {
            check.setOnAction(event -> BlockCheckboxes(genres));
        }

        NextButton.setOnAction(event -> {
            try {
                CreateConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }

            NextButton.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();

            boolean ok = false;

            while (!ok) {
                InputStream stream = getClass().getResourceAsStream("/sample/Step2.fxml");
                try {
                    loader.load(stream);
                    stream.close();
                    ok = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Parent root = loader.getRoot();
            stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        });
    }

    private void BlockCheckboxes(CheckBox[] genres) {
        int count = 0;
        for (CheckBox check : genres) {
            check.setDisable(false);
            if (check.isSelected()) {
                count++;
            }
        }
        if (count == 3) {
            for (CheckBox check : genres) {
                if (!check.isSelected()) {
                    check.setDisable(true);
                }
            }
        }
        NextButton.setDisable(count == 0);
    }

    private void CreateConnection() throws IOException {
        boolean ok = false;

        while (!ok) {
            final URL url = new URL("https://api.themoviedb.org/3/genre/movie/list?api_key=d7abc796a142f8479c6c117dce5a0c41&language=en-US");
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            try (InputStream in = new BufferedInputStream(con.getInputStream());
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonOfData = (JSONObject) jsonParser.parse(reader);
                String strOfGenres = jsonOfData.get("genres").toString();
                JSONArray jsonOfGenres = (JSONArray) jsonParser.parse(strOfGenres);

                for (Object jsonOfGenre : jsonOfGenres) {
                    String genre = jsonOfGenre.toString();
                    JSONObject jsonOfName = (JSONObject) jsonParser.parse(genre);
                    String name = jsonOfName.get("name").toString();
                    for (CheckBox check : genres) {
                        if (check.isSelected() && check.getText().equals(name)) {
                            String id = jsonOfName.get("id").toString();
                            IDs.add(Integer.parseInt(id));
                        }
                    }
                }
                if (Age.isSelected())
                    age = true;

                con.disconnect();

                ok = true;

            } catch (final Exception ex) {
                System.out.println("Couldn't connect to the server. Retrying.. \nThere might be a problem with your " +
                        "internet connection, please make sure your connection is stable.");
            }
        }
    }

}