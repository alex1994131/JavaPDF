/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfpro;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.scene.layout.Pane;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author Majesty
 */
public class FXMLDocumentController {
    
    @FXML private Pagination pagination ;
        @FXML private Pane pdfPane ;
	public String pdfPath = "";
	private FileChooser fileChooser ;
	
        PDDocument pdfDoc;
        PDFRenderer renderer;
        BufferedImage pdfImg;
        BufferedImage pdfAnnotatorImg;
        WritableImage fxImage;
//	private PageDimensions currentPageDimensions ;
//	private ExecutorService imageLoadService ;
        
        int[] image_data;
        List<int[]> neighbour_list;
        List<Integer> edge_list;
        int width = 0;
        int height = 0;
        int delta = 100;
        int boundary = 220;
        
	// ************ Initialization *************
	
	public void initialize() {
            fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.home")).toFile());
            fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf", "*.PDF")); 
	}
        
	// ************** Event Handlers ****************
	
        private void loadPdf() throws IOException {
            if(pdfPath.isEmpty()) return;
            pdfDoc = PDDocument.load(new File(pdfPath));
//                //Instantiate PDFTextStripper class  
//            PDFTextStripper pdfStripper = new PDFTextStripper();  
//
//            //Retrieving text from PDF document  
//                  String text = pdfStripper.getText(pdfDoc);  
//                  System.out.println("Text in PDF\n---------------------------------");  
//                  System.out.println(text);  
            renderer = new PDFRenderer(pdfDoc);
            pdfImg = renderer.renderImage(0, 1);
            fxImage = SwingFXUtils.toFXImage(pdfImg, null);
            ImageView imageView = new ImageView(fxImage);
            pdfPane.getChildren().clear();
            pdfPane.getChildren().add(imageView);
        }
        
	@FXML private void loadFile() throws IOException {
            final File file = fileChooser.showOpenDialog(pagination.getScene().getWindow());
            if (file != null) {
                pdfPath = file.getAbsolutePath();
                loadPdf();
            }
	}
        
	private void updateImage() {
            final Graphics2D graphics2D = pdfImg.createGraphics();
            graphics2D.setPaint ( Color.RED );
            int x, y;
            for(int i=0; i< edge_list.size(); i++) {
                x = edge_list.get(i) % width;
                y = edge_list.get(i) / width;
                graphics2D.fillRect (x,y,2,2);
//                graphics2D.drawOval ( x, y, 1, 1 );
            }
            graphics2D.dispose ();
            
            fxImage = SwingFXUtils.toFXImage(pdfImg, null);
            ImageView imageView = new ImageView(fxImage);
            pdfPane.getChildren().clear();
            pdfPane.getChildren().add(imageView);
//            ImageIO.write ( image, "png", new File ( "C:\\image.png" ) );
	}
        
        
        @FXML private void extractPoints() {
            width = pdfImg.getWidth();
            height = pdfImg.getHeight();
            
            image_data = new int[width*height];
            neighbour_list = new ArrayList<int[]>();
            edge_list = new ArrayList<Integer>();
            
            for(int y=0; y<height; y++) {
                for(int x=0; x<width; x++) {
                   Color c = new Color(pdfImg.getRGB(x, y));
                   int wb = 0;
                   if (((c.getRed() + c.getGreen() + c.getBlue())/3) > boundary) wb = 255;
                   image_data[y*width+x] = wb;
                   Color newC = new Color(wb, wb, wb, 255);
                   pdfImg.setRGB(x, y, newC.getRGB());
                   
                   int[] neighbours = new int[8];
                   if (x > 0 && y > 0) neighbours[0] = (y-1)*width+x-1;
                   else neighbours[0] = -1;
                   
                   if (y > 0) neighbours[1] = (y-1)*width+x;
                   else neighbours[1] = -1;
                   
                   if (x < width-1 && y > 0) neighbours[2] = (y-1)*width+x+1;
                   else neighbours[2] = -1;
                   
                   if (x > 0) neighbours[3] = y*width+x-1;
                   else neighbours[3] = -1;
                   
                   if (x < width -1) neighbours[4] = y*width+x+1;
                   else neighbours[4] = -1;
                   
                   if (x > 0 && y < height-1) neighbours[5] = (y+1)*width+x-1;
                   else neighbours[5] = -1;
                   
                   if (y < height-1) neighbours[6] = (y+1)*width+x;
                   else neighbours[6] = -1;
                   
                   if (x < width-1 && y < height-1) neighbours[7] = (y+1)*width+x+1;
                   else neighbours[7] = -1;
                   
                   neighbour_list.add(neighbours);
               }
            }
            
            for(int i=0; i<width * height; i++) {
                int cur_value = image_data[i];
                int[] diff_list = new int[8];
                int[] neighbours = neighbour_list.get(i);
                for(int j=0; j<neighbours.length; j++) {
                    if (neighbours[j] == -1)continue;
                    int diff = Math.abs(image_data[neighbours[j]] - cur_value);
                    if (diff > delta ) diff_list[j] = 1;
                }
//                if (diff_list[0] == 1 && diff_list[1] == 0 && diff_list[3] == 0) {
                if (diff_list[7] == 1 &&diff_list[6] == 1 && diff_list[5] == 1 &&diff_list[4] == 1 &&diff_list[2] == 1 && diff_list[1] == 0 && diff_list[3] == 0) {
                    edge_list.add(i);
                    continue;
                }
//                if (diff_list[1] == 0 && diff_list[2] == 1 && diff_list[4] == 0) {
                if (diff_list[0] == 1 && diff_list[3] == 1 && diff_list[5] == 1 && diff_list[6] == 1 && diff_list[7] == 1 && diff_list[1] == 0 && diff_list[4] == 0) {
                    edge_list.add(i);
                    continue;
                }
//                if (diff_list[3] == 0 && diff_list[5] == 1 && diff_list[6] == 0) {
                if (diff_list[0] == 1 && diff_list[1] == 1 && diff_list[2] == 1 && diff_list[4] == 1 && diff_list[7] == 1 && diff_list[3] == 0 && diff_list[6] == 0) {
                    edge_list.add(i);
                    continue;
                }
//                if (diff_list[4] == 0 && diff_list[6] == 0 && diff_list[7] == 1) {
                if (diff_list[0] == 1 && diff_list[1] == 1 && diff_list[2] == 1 && diff_list[3] == 1 && diff_list[5] == 1 && diff_list[4] == 0 && diff_list[6] == 0) {
                    edge_list.add(i);
                    continue;
                }
            }
            
            System.out.println("Oaaa");
            updateImage();
        }    
    
}
