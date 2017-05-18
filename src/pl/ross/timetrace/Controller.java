package pl.ross.timetrace;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Controller {
    @FXML public SplitPane canvasPane;
    public ScrollPane scrollPane;
    public TextArea textArea;
    public BorderPane mainPane;
    @FXML private Canvas canvas;

    public void initialize(){
    }

    private String loadFromFile(String path){
        String stringFromFile = "";
        try {
            stringFromFile = new String(Files.readAllBytes(Paths.get(path)), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringFromFile;
    }


    public void run(ActionEvent actionEvent) {
        String code = textArea.getText();
        ScriptInterpreter scriptInterpreter = new ScriptInterpreter();
        scriptInterpreter.runScript(canvas,code);
    }

    public void save(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("png files (*.png","*.png");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());

        if(file != null){
            try {
                WritableImage writableImage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
                canvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
            }
        }
    }
}
