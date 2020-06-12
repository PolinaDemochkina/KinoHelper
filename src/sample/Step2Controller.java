package sample;

import java.io.*;
import java.text.MessageFormat;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Second window
 * @autor Polina Demochkina
 */
public class Step2Controller {

    @FXML
    private Button PrevButton;

    @FXML
    private Label Data;

    @FXML
    private ImageView Poster;

    @FXML
    private Button WatchedAndDidNotLiked;

    @FXML
    private Button WatchedAndLiked;

    @FXML
    private Button DidNotWatch;

    @FXML
    private Label Counter;

    /**
     * Vector of recommended movies
     */
    static Vector<Film> films = new Vector<>(6);

    /**
     * Vector of posters for the recommended movies
     */
    static Vector<Image> images = new Vector<>(6);

    private String title, release_date, poster_path, id;

    private static int page = 1, i = 0;

    static final Vector<Integer> randomNumbers = new Vector<>(6);

    /**
     * Vector of movies that the user had already watched
     */
    static final Vector<String> SkipFilms = new Vector<>(1, 1);

    private JSONArray jsonOfFilms = new JSONArray();

    private final ResourceBundle resourceStep2 = ResourceBundle.getBundle("sample.resources");

    @FXML
    void initialize() throws IOException, ParseException {
        Data.setWrapText(true);
        GetRandomNumbers();
        Check();

        WatchedAndLiked.setOnAction(event -> {
            try {
                AddFilm();
            } catch (IOException | ParseException ignored) {}
        });
        DidNotWatch.setOnAction(event -> {
            try {
                DoNotWatched();
            } catch (IOException | ParseException ignored) {}
        });
        WatchedAndDidNotLiked.setOnAction(event -> {
            try {
                DoNotLiked();
            } catch (IOException | ParseException ignored) {}
        });

        PrevButton.setOnAction(event -> {
            Stage stage = (Stage) PrevButton.getScene().getWindow();
            stage.close();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/Step1.fxml"));

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = loader.getRoot();
            Step1Controller.stage.setScene(new Scene(root));
            Step1Controller.IDs.clear();
            Step1Controller.age = false;
            films.removeAllElements();
            images.removeAllElements();
            randomNumbers.removeAllElements();
            SkipFilms.removeAllElements();
            i = 0;
            page = 1;
            Step1Controller.stage.show();
        });
    }

    /**
     * Movie class
     */
    public static class Film {
        String name;
        String date;
        int id;

        /**
         * Constructor - new movie creation
         * @param name - movie title
         * @param date - release date
         * @param id - movie id in the database
         */
        Film(String name, String date, int id) {
            this.name = name;
            this.date = date;
            this.id = id;
        }
    }

    /**
     * Function to receive a movie and display it
     * @param genre - genre ID
     * @throws IOException
     * @throws ParseException - when an erroneous result is returned by the request
     */
    private void Connection(int genre) throws IOException, ParseException {

        String link = MessageFormat.format(resourceStep2.getString("Step2.Connection.link"), Step1Controller.age, page, String.valueOf(genre));

        if (i == 0)
            jsonOfFilms = BackendUtil.Connection(link, "results");

        ParseJSON(jsonOfFilms);

        boolean flag = false;
        for (String skipFilm : SkipFilms) {
            if (title.equals(skipFilm)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            if (poster_path != null) {
                Image poster = new Image(MessageFormat.format(resourceStep2.getString("Step2.poster_path"), poster_path));
                Poster.setImage(poster);
            } else {
                InputStream input = getClass().getResourceAsStream(resourceStep2.getString("Step2.poster_path.error"));
                Image poster = new Image(input);
                Poster.setImage(poster);
            }

            Data.setText(title + " (" + release_date + ")");
            Counter.setText(films.size() + "/6");
        }
    }

    /**
     * Function to check if enough movies have been selected. If not, the Connection function is called.
     * @throws IOException
     * @throws ParseException - when an erroneous result is returned by the request
     */
    private void Check () throws IOException, ParseException {
        if (films.size() == 6)
            NextStep();
        else {
            if (i == 5) {
                page++;
                i = 0;
                randomNumbers.removeAllElements();
                GetRandomNumbers();
            }
            if (Step1Controller.IDs.size() == 1) {
                Connection(Step1Controller.IDs.get(0));
            } else if (Step1Controller.IDs.size() == 2) {
                if (films.size() < 3)
                    Connection(Step1Controller.IDs.get(0));
                else
                    Connection(Step1Controller.IDs.get(1));
            } else if (Step1Controller.IDs.size() == 3) {
                if (films.size() < 2)
                    Connection(Step1Controller.IDs.get(0));
                else if (films.size() < 4)
                    Connection(Step1Controller.IDs.get(1));
                else
                    Connection(Step1Controller.IDs.get(2));
            }
        }
    }

    /**
     * "Watched and liked" button. The function sends a request, receives the recommended movie and adds it to the film vector.
     * @throws IOException
     * @throws ParseException - when an erroneous result is returned by the request
     */
    private void AddFilm () throws IOException, ParseException {
        String link = MessageFormat.format(resourceStep2.getString("Step2.AddFilm.link"), id);

        JSONArray NewJsonOfFilms = BackendUtil.Connection(link, "results");
        if (randomNumbers.get(i) < NewJsonOfFilms.size()) {

            ParseJSON(NewJsonOfFilms);

            boolean flag = false;
            for (String skipFilm : SkipFilms) {
                if (title.equals(skipFilm)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                Film film = new Film(title, release_date, Integer.parseInt(id));
                films.add(film);

                if (poster_path != null) {
                    Image poster = new Image(MessageFormat.format(resourceStep2.getString("Step2.poster_path"), poster_path));
                    images.add(poster);
                } else {
                    InputStream input = getClass().getResourceAsStream(resourceStep2.getString("Step2.poster_path.error"));
                    Image poster = new Image(input);
                    images.add(poster);
                }
                SkipFilms.add(title);
            }
        }
        i++;
        Check();
    }


    /**
     * "Watched and disliked" button
     * @throws IOException
     * @throws ParseException
     */
    private void DoNotLiked () throws IOException, ParseException {
        SkipFilms.add(title);
        i++;
        Check();
    }

    /**
     * "Didn't watch" button
     * @throws IOException
     * @throws ParseException
     */
    private void DoNotWatched () throws IOException, ParseException {
        i++;
        Check();
    }

    /**
     * Function to switch to the third window
     */
    private void NextStep () {
        PrevButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/Step3.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        i = 0;
        page = 1;
        Parent root = loader.getRoot();
        Stage Stage = new Stage();
        Stage.setTitle("KinoHelper");
        Stage.setScene(new Scene(root));
        Stage.setResizable(false);
        Stage.showAndWait();
    }

    /**
     * Function to parse JSONs received from requests
     * @param arr - JSON dictionary of movies returned by a request
     * @throws ParseException - when an erroneous result is returned by the request for image
     */
    private void ParseJSON (JSONArray arr) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        String StrOfFilm = arr.get(randomNumbers.get(i)).toString();
        JSONObject jsonOfFilm = (JSONObject) jsonParser.parse(StrOfFilm);
        title = jsonOfFilm.get("title").toString();
        release_date = jsonOfFilm.get("release_date").toString();
        id = jsonOfFilm.get("id").toString();
        try {
            poster_path = jsonOfFilm.get("poster_path").toString();
        } catch (Exception e) {
            poster_path = null;
        }
    }

    /**
     * Function to fill the randomNumbers vector
     */
    private void GetRandomNumbers () {
        boolean flag = false;
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            int number = rand.nextInt(20);
            for (int randomNumber : randomNumbers) {
                if (number == randomNumber) {
                    flag = true;
                    break;
                }
            }
            if (!flag)
                randomNumbers.add(number);
            else {
                i--;
                flag = false;
            }
        }
    }
}