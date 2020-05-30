package sample;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
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

    static Vector <Film> films = new Vector<>(1, 1);

    static Vector <Image> images = new Vector<>(6);

    private String title, release_date, poster_path, id;

    private static int page = 1, i = 0;

    static final Vector <Integer> randomNumbers = new Vector<>(6);

    static final Vector <String> SkipFilms = new Vector<>(1, 1);

    private JSONArray jsonOfFilms = new JSONArray();

    @FXML
    void initialize() throws IOException, ParseException {
        GetRandomNumbers();
        Check();

        WatchedAndLiked.setOnAction(event -> {
            try {
                AddFilm(Integer.parseInt(id));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });
        DidNotWatch.setOnAction(event -> {
            try {
                DoNotWatched();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });
        WatchedAndDidNotLiked.setOnAction(event -> {
            try {
                DoNotLiked();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
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

    public static class Film {
        String name;
        String date;
        int id;

        Film(String name, String date, int id){
            this.name = name;
            this.date = date;
            this.id = id;
        }
    }

    private void Connection(int genre) throws IOException, ParseException {
        if (i == 0) {
            final URL url = new URL("https://api.themoviedb.org/3/discover/movie?api_key=" +
                    "d7abc796a142f8479c6c117dce5a0c41&language=en-US&sort_by=popularity.desc&" +
                    "include_adult=" + Step1Controller.age + "&include_video=false&page=" + page + "&with_genres=" + genre);
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            try (InputStream in = new BufferedInputStream(con.getInputStream());
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonOfData = (JSONObject) jsonParser.parse(reader);
                String strOfResults = jsonOfData.get("results").toString();
                jsonOfFilms = (JSONArray) jsonParser.parse(strOfResults);

                con.disconnect();

            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }

        ParseJSON(jsonOfFilms);

        boolean flag = false;
        for (String skipFilm : SkipFilms) {
            if (title.equals(skipFilm)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            Image poster = new Image("http://image.tmdb.org/t/p/original" + poster_path);
            Poster.setImage(poster);

            Data.setText(title + " (" + release_date + ")");
        }
    }

    private void Check() throws IOException, ParseException {
        if (films.size() == 6)
            NextStep();
        else {
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
            i++;

            if (i == 6) {
                page++;
                i = 0;
                randomNumbers.removeAllElements();
                GetRandomNumbers();
            }
        }
    }

    private void AddFilm (int ID) throws IOException, ParseException {
        final URL url = new URL("https://api.themoviedb.org/3/movie/" + ID + "/" +
                "similar?api_key=d7abc796a142f8479c6c117dce5a0c41&language=en-US&page=1");
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        try (InputStream in = new BufferedInputStream(con.getInputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonOfData = (JSONObject) jsonParser.parse(reader);
            String strOfResults = jsonOfData.get("results").toString();

            if (!strOfResults.equals("[]")) {
                JSONArray NewJsonOfFilms = (JSONArray) jsonParser.parse(strOfResults);
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
                        Image poster = new Image("http://image.tmdb.org/t/p/original" + poster_path);
                        films.add(film);
                        images.add(poster);
                        SkipFilms.add(title);
                    }
                }
            }
            con.disconnect();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        Check();
    }

    private void DoNotLiked() throws IOException, ParseException {
        SkipFilms.add(title);
        Check();
    }

    private void DoNotWatched() throws IOException, ParseException {
        Check();
    }

    private void NextStep() {
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
        Stage.setScene(new Scene(root));
        Stage.setResizable(false);
        Stage.showAndWait();
    }

    private void ParseJSON(JSONArray arr) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        String StrOfFilm = arr.get(randomNumbers.get(i)).toString();
        JSONObject jsonOfFilm = (JSONObject) jsonParser.parse(StrOfFilm);
        title = jsonOfFilm.get("title").toString();
        release_date = jsonOfFilm.get("release_date").toString();
        poster_path = jsonOfFilm.get("poster_path").toString();
        id = jsonOfFilm.get("id").toString();
    }

    private void GetRandomNumbers() {
        boolean flag = false;
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            int number = rand.nextInt(20);
            for (Integer randomNumber : randomNumbers) {
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