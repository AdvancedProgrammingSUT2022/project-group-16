import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;

public class LoadGame extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/loadGame.fxml").toExternalForm()))));

        stage.show();
    }

    public void backToMenu(MouseEvent mouseEvent) throws Exception {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void loadAutoSave(MouseEvent mouseEvent) {
        NewGame.newGameMode = "autosave";
        try
        {
            createGame((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    public void loadSave1(MouseEvent mouseEvent) {
        NewGame.newGameMode = "save1";
        try
        {
            createGame((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    public void loadSave2(MouseEvent mouseEvent) {
        NewGame.newGameMode = "save2";
        try
        {
            createGame((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    public void loadSave3(MouseEvent mouseEvent) {
        NewGame.newGameMode = "save3";
        try
        {
            createGame((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    public void loadSave4(MouseEvent mouseEvent) {
        NewGame.newGameMode = "save4";
        try
        {
            createGame((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void createGame(Stage stage) throws Exception
    {
        Game game = new Game();
        game.start(stage);
        Main.audioClip.stop();
    }
}
