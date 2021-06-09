/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfpro;

import java.awt.image.BufferedImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.File;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 *
 * @author Majesty
 */
public class PdfPro extends Application {
    
    String pdfPath = "";
    int pdfScale = 1;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();


        /*int width = 590;
        int height = 840;
        PDDocument doc = PDDocument.load(new File("samples.pdf"));
        PDFRenderer renderer = new PDFRenderer(doc);
        BufferedImage img = renderer.renderImage(0, 2);

        Pane pane = new Pane();
        WritableImage fxImage = SwingFXUtils.toFXImage(img, null);
        ImageView imageView = new ImageView(fxImage);
        pane.getChildren().add(imageView);
        Scene scene = new Scene(pane, width, height);
        primaryStage.setTitle(getClass().getName());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();*/
    }
}
