import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ModManager extends Application {

    private JSONObject modsData;
    private final String JSON_FILE_PATH = "mods.json";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mod Manager");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        // Mod selection
        ComboBox<String> modSelect = new ComboBox<>();
        grid.add(new Label("Select Mod:"), 0, 0);
        grid.add(modSelect, 1, 0);

        // File name
        TextField fileNameField = new TextField();
        grid.add(new Label("File Name:"), 0, 1);
        grid.add(fileNameField, 1, 1);

        // Mod loader
        TextField modLoaderField = new TextField();
        grid.add(new Label("Mod Loader:"), 0, 2);
        grid.add(modLoaderField, 1, 2);

        // File chooser
        Button fileChooserButton = new Button("Choose File");
        grid.add(fileChooserButton, 0, 3);

        // Download URL
        TextField downloadUrlField = new TextField();
        grid.add(new Label("Download URL:"), 0, 4);
        grid.add(downloadUrlField, 1, 4);

        // Add/Update button
        Button addUpdateButton = new Button("Add/Update Mod");
        grid.add(addUpdateButton, 1, 5);

        // Load JSON data
        loadJsonData();

        // Populate mod selection
        updateModSelection(modSelect);

        // File chooser action
        fileChooserButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                fileNameField.setText(selectedFile.getName());
                // Here you would typically upload the file to GitHub and get the raw URL
                // For now, we'll just use a placeholder
                downloadUrlField.setText("https://raw.githubusercontent.com/yourusername/yourrepo/main/path/to/" + selectedFile.getName());
            }
        });

        // Add/Update action
        addUpdateButton.setOnAction(e -> {
            String selectedMod = modSelect.getValue();
            String fileName = fileNameField.getText();
            String modLoader = modLoaderField.getText();
            String downloadUrl = downloadUrlField.getText();

            if (selectedMod != null && !fileName.isEmpty() && !modLoader.isEmpty() && !downloadUrl.isEmpty()) {
                addOrUpdateMod(selectedMod, fileName, modLoader, downloadUrl);
                saveJsonData();
                updateModSelection(modSelect);
                clearFields(fileNameField, modLoaderField, downloadUrlField);
            } else {
                showAlert("Please fill all fields");
            }
        });

        Scene scene = new Scene(grid, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadJsonData() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            modsData = new JSONObject(content);
        } catch (Exception e) {
            modsData = new JSONObject();
            e.printStackTrace();
        }
    }

    private void saveJsonData() {
        try {
            Files.write(Paths.get(JSON_FILE_PATH), modsData.toString(4).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateModSelection(ComboBox<String> modSelect) {
        modSelect.getItems().clear();
        for (String modName : modsData.keySet()) {
            modSelect.getItems().add(modName);
        }
    }

    private void addOrUpdateMod(String modName, String fileName, String modLoader, String downloadUrl) {
        if (!modsData.has(modName)) {
            modsData.put(modName, new JSONObject());
            modsData.getJSONObject(modName).put("name", modName);
            modsData.getJSONObject(modName).put("image", "path/to/default/image.jpg");
            modsData.getJSONObject(modName).put("files", new JSONArray());
        }

        JSONArray files = modsData.getJSONObject(modName).getJSONArray("files");
        JSONObject fileObj = new JSONObject();
        fileObj.put("fileName", fileName);
        fileObj.put("modLoader", modLoader);
        fileObj.put("downloadUrl", downloadUrl);

        // Remove existing entry with the same file name if it exists
        for (int i = 0; i < files.length(); i++) {
            if (files.getJSONObject(i).getString("fileName").equals(fileName)) {
                files.remove(i);
                break;
            }
        }

        files.put(fileObj);
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}