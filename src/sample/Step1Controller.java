package sample;

import java.io.*;
import java.util.ResourceBundle;
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
import org.json.simple.parser.ParseException;

/**
 * First window
 * @autor Polina Demochkina
 */
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

    /**
     * All available genres
     */
    private final Vector<CheckBox> genres = new Vector<>(16);

    static boolean age = false;

    static Stage stage;

    /**
     * Genre IDs
     */
    static Vector<Integer> IDs = new Vector<>(1, 1);

    private final ResourceBundle resourceStep1 = ResourceBundle.getBundle("sample.resources");

    @FXML
    void initialize() {
        NextButton.setDisable(true);

        genres.add(AdventureCheck);
        genres.add(ComedyCheck);
        genres.add(ThrillerCheck);
        genres.add(HorrorsCheck);
        genres.add(MysteryCheck);
        genres.add(ActionCheck);
        genres.add(CartoonCheck);
        genres.add(FantasyCheck);
        genres.add(RomanceCheck);
        genres.add(ScienceCheck);
        genres.add(HistoryCheck);
        genres.add(DocumentaryCheck);
        genres.add(DramaCheck);
        genres.add(FamilyCheck);
        genres.add(CrimeCheck);
        genres.add(WarCheck);

        for (CheckBox check : genres) {
            check.setOnAction(event -> BlockCheckboxes(genres));
        }

        NextButton.setOnAction(event -> {
            NextButton.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/Step2.fxml"));

            try {
                Connection();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = loader.getRoot();
            stage = new Stage();
            stage.setTitle("KinoHelper");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        });
    }

    /**
     * Function for disabling checkboxes
     * @param genres - vector of available genres
     */
    private void BlockCheckboxes(Vector<CheckBox> genres) {
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

    /**
     * Function for sending a request and parsing the result
     * @throws IOException
     * @throws ParseException - when an erroneous result is returned by the request
     */
    private void Connection() throws IOException, ParseException {

        String link = resourceStep1.getString("Step1.Connection.link");

        JSONArray jsonOfGenres = BackendUtil.Connection(link, "genres");

        for (Object jsonOfGenre : jsonOfGenres) {
            JSONParser jsonParser = new JSONParser();
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
    }
}