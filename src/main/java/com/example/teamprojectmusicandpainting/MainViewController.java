package com.example.teamprojectmusicandpainting;

import com.example.teamprojectmusicandpainting.domain.User;
import com.example.teamprojectmusicandpainting.service.Service;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainViewController {
    private Service service;
    private User loggedUser;
    public String musicDirectoryPath = "src/main/java/Music";

    // Tables
    public TableView userPlaylistTabelView;
    public TableView allMusicTableView;
    //Table Column
    public TableColumn<String, String> userPlaylistColumn;
    public TableColumn<String, String> musicNameColumn;

    //Button
    public Button allMusicButton;
    public Button musicPlaylistButton;
    public Button playButton;
    public Button stopButton;
    public Button addMusicToPlaylistButton;
    public Button deleteMusicFromPlaylistButton;

    public ColorPicker colorPicker;

    private Media media;
    private MediaPlayer mediaPlayer;
    private String selectedMusicTitle = "";

    public Pane paintingPane;
    private GraphicsContext gc;

    public TextField brushSizeTextField;
    public VBox paintVbox;


    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public void setService(Service service, User user) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        allMusicTableView.setVisible(false);
        userPlaylistTabelView.setVisible(false);

        deleteMusicFromPlaylistButton.setVisible(false);
        addMusicToPlaylistButton.setVisible(false);

        playButton.setVisible(false);
        stopButton.setVisible(false);

        paintVbox.setVisible(false);
    }

    public List<File> getAllMusic(){
        List<File> musicListFiles = new ArrayList<>();
        File musicDirectory = new File(musicDirectoryPath);
        if (musicDirectory.isDirectory()) {
            File[] files = musicDirectory.listFiles();
            if (files != null) {
                for (File file : files){
                    if (file.isFile())
                        musicListFiles.add(file);
                    }
                }
            }
        return musicListFiles;
    }


    // I want to get the information based on the click and then be able to play based on this information
    public void getSelectedMusic(){
        allMusicTableView.setOnMouseClicked(event -> {
            int rowIndex = allMusicTableView.getSelectionModel().getSelectedIndex();

            // Accesăm prima coloană și valoarea ei
            TableColumn<String, String> firstColumn = (TableColumn<String, String>) allMusicTableView.getColumns().get(0);
            selectedMusicTitle = firstColumn.getCellData(rowIndex);

        });

        userPlaylistTabelView.setOnMouseClicked(event -> {
            int rowIndex = userPlaylistTabelView.getSelectionModel().getSelectedIndex();

            // Accesăm prima coloană și valoarea ei
            TableColumn<String, String> firstColumn = (TableColumn<String, String>) userPlaylistTabelView.getColumns().get(0);
            selectedMusicTitle = firstColumn.getCellData(rowIndex);

        });
    }

    public void playSelectetSong(){
        //if a song is already started, we close it
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.stop();
        }
        //we go through the songs and when we find the selected one, we play it
        File musicDirectory = new File(musicDirectoryPath);
        if (musicDirectory.isDirectory()) {
            File[] files = musicDirectory.listFiles();
            if (files != null) {
                for (File file : files){
                    if (file.getName().equals(selectedMusicTitle)){
                        media = new Media(file.toURI().toString());
                        mediaPlayer = new MediaPlayer(media);
                        mediaPlayer.play();
                    }
                }
            }
        }
    }

    public void stopSelectedSong(){
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.stop();
        }
    }


    public void handleShowMusic() {
        paintingPane.setVisible(false);
        allMusicTableView.setVisible(true);
        userPlaylistTabelView.setVisible(false);
        addMusicToPlaylistButton.setVisible(true);
        deleteMusicFromPlaylistButton.setVisible(false);

        playButton.setVisible(true);
        stopButton.setVisible(true);

        paintVbox.setVisible(false);

        List<File> musicListFile = getAllMusic();
        List<String> musicNames = new ArrayList<>();
        for (File file : musicListFile) {
            musicNames.add(file.getName());
        }

        ObservableList<String> musicListData = FXCollections.observableArrayList(musicNames);

        musicNameColumn = new TableColumn<>("Music Name");
        musicNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        allMusicTableView.getColumns().setAll(musicNameColumn);
        allMusicTableView.setItems(musicListData);

        getSelectedMusic();
    }

    public void handleShowUserPlaylist(){
        userPlaylistTabelView.refresh();
        paintingPane.setVisible(false);
        allMusicTableView.setVisible(false);
        userPlaylistTabelView.setVisible(true);
        addMusicToPlaylistButton.setVisible(false);
        deleteMusicFromPlaylistButton.setVisible(true);

        playButton.setVisible(true);
        stopButton.setVisible(true);

        paintVbox.setVisible(false);

        System.out.println("Prima " + loggedUser.getUserMusicPlaylist());

        /* user_music_playlist it s a string and we split music names after "," */
        List<String> userMmusicNames =  Arrays.asList(loggedUser.getUserMusicPlaylist().split(","));

        ObservableList<String> musicListData = FXCollections.observableArrayList(userMmusicNames);

        userPlaylistColumn = new TableColumn<>("Music Name");
        userPlaylistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        userPlaylistTabelView.getColumns().setAll(userPlaylistColumn);
        userPlaylistTabelView.setItems(musicListData);

        getSelectedMusic();

    }

    public void addMusicToPlaylist(){
        /*Titlul melodiei selectate il adatug in user_music_playlist*/

        String userMusicPlaylist = loggedUser.getUserMusicPlaylist();
        userMusicPlaylist = userMusicPlaylist + "," + selectedMusicTitle;
        loggedUser.setMyPlaylist(userMusicPlaylist);
        service.updateUserMusicPlaylist(loggedUser, userMusicPlaylist);
    }

    public void deleteMusicFromPlaylist() {
        String musicToDelete = selectedMusicTitle;
        musicToDelete = "," + musicToDelete;
        String userMusicPlaylist = loggedUser.getUserMusicPlaylist();
        System.out.println(userMusicPlaylist);
        userMusicPlaylist = userMusicPlaylist.replace(musicToDelete, "");
        System.out.println(userMusicPlaylist);
        loggedUser.setMyPlaylist(userMusicPlaylist);
        service.updateUserMusicPlaylist(loggedUser, userMusicPlaylist);

        handleShowUserPlaylist();
    }

    public void handleShowPaintingPane(){
        paintingPane.setVisible(true);
        allMusicTableView.setVisible(false);
        userPlaylistTabelView.setVisible(false);
        addMusicToPlaylistButton.setVisible(false);
        deleteMusicFromPlaylistButton.setVisible(false);

        playButton.setVisible(false);
        stopButton.setVisible(false);

        paintVbox.setVisible(true);


        Canvas canvas = new Canvas(935.0, 679.0);
        gc = canvas.getGraphicsContext2D();

        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            gc.setStroke(newValue);
        });


        // Set initial drawing settings
        gc.setStroke(colorPicker.getValue());
        brushSizeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double lineWidth = Double.parseDouble(newValue);
                gc.setLineWidth(lineWidth);
            } catch (NumberFormatException e) {
            }
        });


        // Handle mouse events to draw on the canvas
        canvas.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                gc.beginPath();
                gc.moveTo(event.getX(), event.getY());
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                gc.lineTo(event.getX(), event.getY());
                gc.stroke();
            }
        });

        paintingPane.getChildren().add(canvas);
    }


}
