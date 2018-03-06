/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tic.tac.toe;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.MouseEvent;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

/**
 *
 * @author shaimaanabil
 */
public class TicTacToe extends Application {

    private Game game;
    Tic root = new Tic();
    Menu root1 = new Menu();
    private int xCount = 0;
    private boolean eventFlag1 = true;
    private boolean eventFlag2 = true;
    private int oCount = 0;
    private int userChoice;
    SingleGame currentGame;
    NetGame netGame;
    boolean isListener;
    int port;
    Server server;
    Client client;
    boolean canPlay;
    String host;
    private Stage stage;
    private Scene scene, scene1;
    Thread guiUpdateThread;

    AudioClip win = new AudioClip(getClass().getResource("won.wav").toString());

    @Override
    public void start(Stage primaryStage) {

        stage = primaryStage;
        scene = new Scene(root, 699, 609);
        scene1 = new Scene(root1, 699, 609);
        primaryStage.setScene(scene1);
        primaryStage.show();
        primaryStage.setResizable(false);
        root1.label.setOnMouseClicked((MouseEvent event) -> {
            userChoice = 0;
            try {
                userMode(0);
            } catch (Exception ex) {
                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
            }
            primaryStage.hide();
            primaryStage.setScene(scene);
            primaryStage.show();
            root.imageView10.setImage(new Image(TicTacToe.this.getClass().getResource("computer.png").toExternalForm()));
        });
        root1.label0.setOnMouseClicked((MouseEvent event) -> {
            root1.pane0.setVisible(true);
            root1.imageView3.setOpacity(0.2);
            eventFlag1 = false;
            System.out.println("label");
            userChoice = 2;
        });
        root1.label1.setOnMouseClicked((MouseEvent event) -> {
            userChoice = 1;
            try {
                userMode(1);
            } catch (Exception ex) {
                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
            }
            primaryStage.hide();
            primaryStage.setScene(scene);
            primaryStage.show();
            root.imageView10.setImage(new Image(TicTacToe.this.getClass().getResource("icon-person-blue.png").toExternalForm()));
        });
        root1.imageView3.setOnMouseClicked((MouseEvent event) -> {
            if (eventFlag1) {
                root1.pane16.setVisible(true);
            }
        });
        root1.imageView10.setOnMouseClicked((MouseEvent event) -> {
            root1.pane16.setVisible(false);
        });
        root1.imageView9.setOnMouseClicked((MouseEvent event) -> {
            primaryStage.close();
            System.exit(0);
        });
        root1.text0.setOnMouseClicked((MouseEvent event) -> {
            root1.pane8.setVisible(true);
            root1.pane0.setVisible(false);
        });
        root1.imageView4.setOnMouseClicked((MouseEvent event) -> {
            root1.pane0.setVisible(false);
            root1.imageView3.setOpacity(1.0);
            eventFlag1 = true;
        });
        //host button
        root1.text.setOnMouseClicked((MouseEvent event) -> {
            root1.pane3.setVisible(true);
            root1.pane0.setVisible(false);
            try {
                // argv[0]: "0" == client, "1" == host argv[1]: Ip argv[2]: port
                userMode(2, "1");
            } catch (Exception ex) {
                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        root1.imageView5.setOnMouseClicked((MouseEvent event) -> {
            root1.pane3.setVisible(false);
            root1.pane0.setVisible(false);
            root1.imageView3.setOpacity(1.0);
            eventFlag1 = true;
        });
        root1.imageView7.setOnMouseClicked((MouseEvent event) -> {
            root1.pane8.setVisible(false);
            root1.pane0.setVisible(false);
            root1.imageView3.setOpacity(1.0);
            eventFlag1 = true;
        });
        root1.imageView8.setOnMouseClicked((MouseEvent event) -> {
            root1.pane8.setVisible(true);
            root1.pane14.setVisible(false);
            eventFlag1 = true;
        });
        root1.imageView6.setOnMouseClicked((MouseEvent event) -> {
            try {
                String port = root1.textField0.getText();
                String ip = root1.textField1.getText();
                //argv[0]: "0" == client, "1" == host argv[1]: Ip argv[2]: port
                userChoice = 2;
                userMode(2, "0", ip, port);
                root1.pane8.setVisible(false);
                primaryStage.hide();
                primaryStage.setScene(scene);
                primaryStage.show();
                root.imageView10.setImage(new Image(TicTacToe.this.getClass().getResource("online.png").toExternalForm()));
            } catch (Exception e) {
                e.printStackTrace();
                root1.pane14.setVisible(true);
                root1.text8.setText("There Was A Problem Connecting To Server, Please Try Again.");
            }
        });
        root1.text3.setOnMouseClicked((MouseEvent event) -> {
            //server ip
        });
        root1.text4.setOnMouseClicked((MouseEvent event) -> {
            //server port
        });
        root1.textField0.setOnMouseClicked((MouseEvent event) -> {
            //client ip
        });
        root1.textField1.setOnMouseClicked((MouseEvent event) -> {
            //client port
        });
        primaryStage.setResizable(false);

        for (int i = 0; i < 9; i++) {
            final int location = i;
            root.cells[i].setOnMouseClicked((MouseEvent event) -> {
                switch (userChoice) {
                    case 0:
                        game.move(location, game.currentPlayer);
                        currentGame.computerMove();
                        break;
                    case 1:
                        game.move(location, game.currentPlayer);
                        break;
                    case 2:
                        if (netGame.myTurn()) {
                            try {
                                netGame.move(location);
                            } catch (Exception ex) {
                                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                }
                redraw();
            });
        }

        root.imageView9.setOnMouseClicked((MouseEvent event) -> {
            if (eventFlag2) {
                root.pane11.setVisible(true);
                root.gridPane.setOpacity(0.2);
                root.imageView8.setOpacity(0.2);
                root.imageView9.setOpacity(0.2);
                root.imageView12.setOpacity(0.2);
            }
        });
        root.imageView14.setOnMouseClicked((MouseEvent event) -> {
            eventFlag2 = true;
            root.imageView8.setOpacity(1.0);
            root.imageView9.setOpacity(1.0);
            root.imageView12.setOpacity(1.0);
            root.pane9.setVisible(false);
            root.gridPane.setOpacity(1.0);
            game.board.reset();
            redraw();
        });
        root.imageView15.setOnMouseClicked((MouseEvent event) -> {
            root.pane10.setVisible(false);
            game.board.reset();
            redraw();
        });
        root.imageView16.setOnMouseClicked((MouseEvent event) -> {
            root.pane10.setVisible(false);
            primaryStage.hide();
            primaryStage.setScene(scene1);
            primaryStage.show();
            game.board.reset();
            redraw();
            oCount = 0;
            xCount = 0;
            root.label.setText(String.valueOf(xCount));
            root.label0.setText(String.valueOf(oCount));

        });
        root.imageView17.setOnMouseClicked((MouseEvent event) -> {
            eventFlag2 = true;
            root.imageView8.setOpacity(1.0);
            root.imageView9.setOpacity(1.0);
            root.imageView12.setOpacity(1.0);
            root.gridPane.setOpacity(1.0);
            root.pane11.setVisible(false);
            primaryStage.hide();
            primaryStage.setScene(scene1);
            primaryStage.show();
            game.board.reset();
            redraw();
            oCount = 0;
            xCount = 0;
            root.label.setText(String.valueOf(xCount));
            root.label0.setText(String.valueOf(oCount));
        });
        root.imageView18.setOnMouseClicked((MouseEvent event) -> {
            root.pane11.setVisible(false);
            eventFlag2 = true;
            root.imageView8.setOpacity(1.0);
            root.imageView9.setOpacity(1.0);
            root.imageView12.setOpacity(1.0);
            root.gridPane.setOpacity(1.0);

        });
        root.imageView19.setOnMouseClicked((MouseEvent event) -> {
            primaryStage.close();
            System.exit(0);
        });
        root.imageView8.setOnMouseClicked((MouseEvent event) -> {
            if (eventFlag2) {
                game.board.reset();
                redraw();

            }
        });
    }

    public void redraw() {
        Board.State[][] board = game.board.toArray();

        for (int i = 0; i < 9; i++) {
            switch (board[i / 3][i % 3]) {
                case X:
                    root.cells[i].setImage(new Image(TicTacToe.this.getClass().getResource("X.png").toExternalForm()));
                    root.cells[i].setFitHeight(118.0);
                    root.cells[i].setFitWidth(126.0);
                    root.cells[i].setLayoutX(7.0);
                    root.cells[i].setLayoutY(2.0);
                    root.cells[i].setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.32), 30, 0.5, 0, 0);");
                    break;
                case O:
                    root.cells[i].setImage(new Image(TicTacToe.this.getClass().getResource("O.png").toExternalForm()));
                    root.cells[i].setFitHeight(87.0);
                    root.cells[i].setFitWidth(87.0);
                    root.cells[i].setLayoutX(20.0);
                    root.cells[i].setLayoutY(13.0);
                    root.cells[i].setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.32), 30, 0.5, 0, 0);");
                    break;
                default:
                    root.cells[i].setImage(null);
                    break;
            }
        }
        if (game.isWinner()) {
            eventFlag2 = false;
            root.imageView8.setOpacity(0.2);
            root.imageView9.setOpacity(0.2);
            root.imageView12.setOpacity(0.2);
            root.gridPane.setOpacity(0.2);
            root.pane9.setVisible(true);
            root.imageView14.setImage(new Image(getClass().getResource("ok.png").toExternalForm()));

            if (game.currentPlayer.icon == Board.State.X) {
                root.imageView13.setImage(new Image(getClass().getResource("winnerx.png").toExternalForm()));
                xCount++;
                root.label.setText(String.valueOf(xCount));
                win.play();
            } else {
                root.imageView13.setImage(new Image(getClass().getResource("winnero.png").toExternalForm()));
                oCount++;
                root.label0.setText(String.valueOf(oCount));
                win.play();

            }
        } else if (game.isFull()) {
            eventFlag2 = false;
            root.pane10.setVisible(true);
            root.imageView8.setOpacity(0.2);
            root.imageView9.setOpacity(0.2);
            root.imageView12.setOpacity(0.2);
        }
    }

    /**
     * Initializes the the game object and application based on the user's
     * choice.
     *
     * @param userChoice either 0 (vs computer), 1 (vs human on the same
     * machine) or 2 (vs player over network)
     * @param argv optional arguments for the network mode, there are three of
     * them argv[0]: "0" == client, "1" == host argv[1]: Ip argv[2]: port
     */
    public void userMode(int userChoice, String... argv) {
        switch (userChoice) {
            case 0:
            case 1:
                currentGame = new SingleGame();
                game = currentGame;
                break;
            case 2:
                isListener = argv[0].equals("1");
                guiUpdateThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (isListener) {
                            try {
                                System.out.println("creating server");
                                // is host
                                server = new Server(0);
                                server.start();
                                host = server.getIp();
                                // showing server ip to the host
                                root1.text3.setText(host);
                            } catch (Exception ex) {
                                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            // showing server port to the host
                            String port = Integer.toString(server.getPort());
                            root1.text4.setText(port);
                            game = server.game;
                            netGame = server.game;
                            while (server.connection() == null);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    root1.pane8.setVisible(false);
                                    stage.hide();
                                    stage.setScene(scene);
                                    stage.show();
                                    root.imageView10.setImage(new Image(TicTacToe.this.getClass().getResource("online.png").toExternalForm()));
                                }
                            });
                        } else {
                            // is client, has connected to server
                            host = argv[1];
                            port = Integer.parseInt(argv[2]);
                            try {
                                client = new Client(host, port);
                            } catch (Exception ex) {
                                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            client.start();
                            canPlay = true;
                            game = client.game;
                            netGame = client.game;
                            while (client.connection() == null);
                            System.out.println("client out");
                        }

                        String move = null;
                        while (!game.isFull() && !game.isWinner()) {
                            try {
                                move = netGame.readMessage();
                            } catch (Exception ex) {
                                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                netGame.move(Integer.parseInt(move));
                            } catch (Exception ex) {
                                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            redraw();
                        }
                    }
                });
                guiUpdateThread.start();
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
