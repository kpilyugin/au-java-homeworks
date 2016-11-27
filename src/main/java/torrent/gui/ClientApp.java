package torrent.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import torrent.client.TorrentClient;
import torrent.client.TorrentFile;
import torrent.tracker.FileInfo;
import torrent.tracker.TorrentTracker;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class ClientApp extends Application {
    private TorrentClient torrentClient;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Start torrent client");
        primaryStage.setScene(new StartScene());
        primaryStage.show();
    }

    private void startClient(String trackerIp, String clientPort) throws Exception {
        InetSocketAddress trackerAddress = new InetSocketAddress(InetAddress.getByName(trackerIp), TorrentTracker.PORT);
        int port = Integer.parseInt(clientPort);
        torrentClient = new TorrentClient(trackerAddress, port);
        primaryStage.setTitle("Torrent client: port " + port);
        primaryStage.setScene(new ClientScene());
        primaryStage.setOnCloseRequest(event -> {
            try {
                torrentClient.end();
                System.exit(0);
            } catch (IOException ignored) {
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private class StartScene extends Scene {
        public StartScene() {
            super(new GridPane(), 600, 600);
            GridPane pane = (GridPane) getRoot();
            pane.setAlignment(Pos.CENTER);
            pane.setHgap(10);
            pane.setVgap(10);
            pane.setPadding(new Insets(25, 25, 25, 25));

            Label addressLabel = new Label("Tracker ip address:");
            pane.add(addressLabel, 0, 1);
            final TextField addressField = new TextField();
            pane.add(addressField, 1, 1);
            Label portLabel = new Label("Client port:");
            pane.add(portLabel, 0, 2);
            final TextField portField = new TextField();
            pane.add(portField, 1, 2);

            Button startButton = new Button("Start");
            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.BOTTOM_RIGHT);
            hbox.getChildren().add(startButton);
            pane.add(hbox, 1, 4);

            final Text errorMessage = new Text();
            pane.add(errorMessage, 1, 6);

            startButton.setOnAction(t -> {
                try {
                    startClient(addressField.getText(), portField.getText());
                } catch (Exception e) {
                    errorMessage.setText("Failed to start client: " + e.getMessage());
                }
            });
        }
    }

    private class ClientScene extends Scene {
        private final ObservableList<String> clientFiles;
        private final ListView<FileInfo> trackerFiles;

        public ClientScene() {
            super(new GridPane(), 600, 600);
            GridPane pane = (GridPane) getRoot();
            pane.setAlignment(Pos.CENTER);
            pane.setHgap(10);
            pane.setVgap(10);
            pane.setPadding(new Insets(25, 25, 25, 25));

            trackerFiles = new ListView<>();
            trackerFiles.setPrefSize(300, 200);
            trackerFiles.setPlaceholder(new Text("Tracker files"));
            pane.add(trackerFiles, 0, 2);

            ListView<String> loadingView = new ListView<>();
            loadingView.setPrefSize(300, 200);
            loadingView.setPlaceholder(new Text("Client files"));
            clientFiles = FXCollections.observableArrayList();
            loadingView.setItems(clientFiles);
            pane.add(loadingView, 1, 2);

            for (TorrentFile file : torrentClient.getFiles()) {
                String state = file.isFull() ? ": full, seeding" : ": loaded " + file.getParts().size() + "/" + file.totalParts();
                clientFiles.add(file.getName() + state);
            }

            trackerFiles.setOnMouseClicked(event -> {
                final FileInfo file = trackerFiles.getSelectionModel().getSelectedItem();
                if (file != null) {
                    showDownloadDialog(file);
                }
            });

            Button listButton = new Button("List files");
            pane.add(listButton, 0, 0);
            listButton.setOnAction(event -> listFiles());
            listFiles();

            Button uploadButton = new Button("Upload");
            pane.add(uploadButton, 1, 0);
            uploadButton.setOnAction(event -> {
                File file = showUploadDialog();
                if (file != null) {
                    try {
                        torrentClient.addFile(file);
                        listFiles();
                        clientFiles.add(file.getName() + ": full, seeding");
                    } catch (Exception e) {
                        showError("Failed to add file: " + e.getMessage());
                    }
                }
            });
        }

        private void listFiles() {
            try {
                List<FileInfo> files = torrentClient.listFiles();
                trackerFiles.setItems(FXCollections.observableArrayList(files));
            } catch (Exception e) {
                showError("Failed to list tracker files: " + e.getMessage());
            }
        }

        private File showUploadDialog() {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Upload file to tracker");
            return fileChooser.showOpenDialog(primaryStage);
        }

        private File showSaveDialog(String name) {
            FileChooser fileChooser = new FileChooser();
            String extension = "*" + (name.contains(".") ? name.substring(name.lastIndexOf(".")) : "");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Files", extension));
            fileChooser.setTitle("Save file " + name);
            return fileChooser.showSaveDialog(primaryStage);
        }

        private void showDownloadDialog(FileInfo file) {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);

            VBox vBox = new VBox(20);
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().add(new Text("File: " + file.getName()));
            vBox.getChildren().add(new Text("Size: " + FileInfo.toReadableSize(file.getSize())));

            dialog.setScene(new Scene(vBox, 300, 300));
            dialog.show();

            if (torrentClient.containsFile(file)) {
                vBox.getChildren().add(new Text("File is already downloaded"));
                return;
            }

            Button downloadButton = new Button("Download");
            vBox.getChildren().add(downloadButton);
            downloadButton.setOnAction(event -> {
                File fileTo = showSaveDialog(file.getName());
                dialog.close();
                if (fileTo == null) {
                    showError("Need to select file location");
                    return;
                }

                final int index = clientFiles.size();
                String initial = fileTo.getName() + ": loading started";
                clientFiles.add(initial);

                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        torrentClient.getFile(file, fileTo, this::updateProgress);
                        return null;
                    }
                };
                task.progressProperty().asObject().addListener((observable, oldValue, newValue) ->
                        clientFiles.set(index, file.getName() + ": loaded " + toPercent(newValue)));
                task.setOnSucceeded(value -> clientFiles.set(index, file.getName() + ": fully loaded, seeding"));
                task.setOnFailed(value -> clientFiles.set(index, file.getName() + ": loading failed"));
                Thread downloadThread = new Thread(task);
                downloadThread.setDaemon(true);
                downloadThread.start();
            });
        }

        private String toPercent(double progress) {
            return String.format("%.2f", (progress * 100)) + "%";
        }

        private void showError(String message) {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            dialog.setTitle("Error");

            StackPane root = new StackPane();
            VBox vBox = new VBox(20);
            vBox.setAlignment(Pos.CENTER);
            root.getChildren().add(new Text(message));
            Button closeButton = new Button("Close");
            closeButton.setOnAction(event -> dialog.close());
            root.getChildren().add(closeButton);
            dialog.setScene(new Scene(root, 300, 250));
            dialog.show();
        }
    }
}
