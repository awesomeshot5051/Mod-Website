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
    private final String JSON_FILE_PATH = "D:\\Mod Website\\Mod-Website\\modManager\\src\\main\\resources\\mods.json";

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

        // New mod name input
        TextField newModField = new TextField();
        grid.add(new Label("New Mod Name:"), 0, 1);
        grid.add(newModField, 1, 1);

        // Add mod button
        Button addModButton = new Button("Add Mod");
        grid.add(addModButton, 2, 1);

        // File name
        TextField fileNameField = new TextField();
        grid.add(new Label("File Name:"), 0, 2);
        grid.add(fileNameField, 1, 2);

        // Mod loader
        TextField modLoaderField = new TextField();
        grid.add(new Label("Mod Loader:"), 0, 3);
        grid.add(modLoaderField, 1, 3);

        // File chooser
        Button fileChooserButton = new Button("Choose File");
        grid.add(fileChooserButton, 0, 4);

        // Download URL
        TextField downloadUrlField = new TextField();
        grid.add(new Label("Download URL:"), 0, 5);
        grid.add(downloadUrlField, 1, 5);

        // Add/Update button
        Button addUpdateButton = new Button("Add/Update Mod");
        grid.add(addUpdateButton, 1, 6);

        // Load JSON data
        loadJsonData();

        // Populate mod selection
        updateModSelection(modSelect);

        // File chooser action
        fileChooserButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            String selectedMod = modSelect.getValue();

            if (selectedFile != null) {
                fileNameField.setText(selectedFile.getName());

                // Make sure a mod is selected
                if (selectedMod != null && !selectedMod.isEmpty()) {
                    // Replace spaces with %20 for URL encoding
                    String modUrlName = selectedMod.replace(" ", "%20");
                    downloadUrlField.setText("https://github.com/awesomeshot5051/Mod-Website/raw/refs/heads/main/Mods/" + modUrlName + "/" + selectedFile.getName());
                } else {
                    showAlert("Please select a mod first!");
                }
            }
        });


        // Add new mod action
        addModButton.setOnAction(e -> {
            String newModName = newModField.getText();
            if (!newModName.isEmpty() && !modsData.has(newModName)) {
                modsData.put(newModName, new JSONObject());
                modsData.getJSONObject(newModName).put("name", newModName);
                modsData.getJSONObject(newModName).put("image", "path/to/default/image.jpg");
                modsData.getJSONObject(newModName).put("files", new JSONArray());
                modSelect.getItems().add(newModName);
                newModField.clear();
                saveJsonData();
            } else {
                showAlert("Mod already exists or no name entered!");
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

        Scene scene = new Scene(grid, 500, 300);
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
