package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class Controller {

    @FXML
    private TextArea textArea;

    @FXML
    private Button clearButton;

    @FXML
    private Canvas canvas;

    @FXML
    private void initialize() {
        clearButton.setOnAction(event -> clearTextArea());

        textArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && event.isShiftDown()) {
                textArea.appendText("\n");
            } else if (event.getCode() == KeyCode.ENTER) {
                generateQR();
            }
        });
    }

    @FXML
    public void generateQR() {
        String text = textArea.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        // set QR code encoding parameters
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        try {
            // generate QR code matrix
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 1000, 1000, hints);

            // convert QR code matrix to JavaFX image
            Image image = toFXImage(bitMatrix);

            // draw QR code image onto canvas
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            graphicsContext.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void saveQR() {
        // get the image from the canvas
        WritableImage writableImage = canvas.snapshot(null, null);

        // create a buffered image from the writable image
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        // create a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save QR Code Image");
        fileChooser.setInitialFileName("qrcode.png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));

        // show the dialog and get the selected file
        File file = fileChooser.showSaveDialog(new Stage());

        // save the image to file
        if (file != null) {
            try {
                ImageIO.write(bufferedImage, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Image toFXImage(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelWriter.setColor(x, y, bitMatrix.get(x, y) ? javafx.scene.paint.Color.BLACK : javafx.scene.paint.Color.WHITE);
            }
        }
        return writableImage;
    }

    public void clearTextArea() {
        textArea.clear();
    }
}