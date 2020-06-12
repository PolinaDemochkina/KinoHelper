package sample;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Third window
 * @autor Polina Demochkina
 */
public class Step3Controller {

    @FXML
    private Button HomeButton;

    @FXML
    private Label Data;

    @FXML
    private ImageView Poster;

    @FXML
    private Button PrevButton;

    @FXML
    private Button NextButton;

    @FXML
    private Label Counter;

    private static int i = 0;

    @FXML
    void initialize() {
        GetFilm();

        NextButton.setOnAction(event -> {
            i++;
            if (i == Step2Controller.films.size())
                i = 0;
            GetFilm();
        });

        PrevButton.setOnAction(event -> {
            i--;
            if (i < 0)
                i = Step2Controller.films.size() - 1;
            GetFilm();
        });

        HomeButton.setOnAction(event -> {
            Stage stage = (Stage) HomeButton.getScene().getWindow();
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
            Step2Controller.films.removeAllElements();
            Step2Controller.images.removeAllElements();
            Step2Controller.randomNumbers.removeAllElements();
            Step2Controller.SkipFilms.removeAllElements();
            Step3Controller.i = 0;
            Step1Controller.stage.show();
        });
    }

    /**
     * Function to get a movie from the vector and display it
     */
    private void GetFilm() {
        Data.setText(Step2Controller.films.get(i).name + " (" + Step2Controller.films.get(i).date + ")");
        Poster.setImage(Step2Controller.images.get(i));
        Counter.setText(i + 1 + "/6");
    }
}
