import Controllers.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            URI uri = LoadGame.class.getClassLoader().getResource("savedGames/" + "autosave.json").toURI();
            Path path = Paths.get(uri);
            if(path.toFile().exists() && path.toFile().isFile() && path.toFile().length() > 0)
            {
                createGame((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
            }
            else
            {
                System.err.println("error loading autosave");
            }
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
            URI uri = LoadGame.class.getClassLoader().getResource("savedGames/" + "save1.json").toURI();
            Path path = Paths.get(uri);
            if(path.toFile().exists() && path.toFile().isFile() && path.toFile().length() > 0)
            {
                createGame((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
            }
            else
            {
                System.err.println("error loading save1");
            }
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
            URI uri = LoadGame.class.getClassLoader().getResource("savedGames/" + "save2.json").toURI();
            Path path = Paths.get(uri);
            if(path.toFile().exists() && path.toFile().isFile() && path.toFile().length() > 0)
            {
                createGame((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
            }
            else
            {
                System.err.println("error loading save2");
            }
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
            URI uri = LoadGame.class.getClassLoader().getResource("savedGames/" + "save3.json").toURI();
            Path path = Paths.get(uri);
            if(path.toFile().exists() && path.toFile().isFile() && path.toFile().length() > 0)
            {
                createGame((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
            }
            else
            {
                System.err.println("error loading save3");
            }
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
            URI uri = LoadGame.class.getClassLoader().getResource("savedGames/" + "save4.json").toURI();
            Path path = Paths.get(uri);
            if(path.toFile().exists() && path.toFile().isFile() && path.toFile().length() > 0)
            {
                createGame((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
            }
            else
            {
                System.err.println("error loading save4");
            }
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
