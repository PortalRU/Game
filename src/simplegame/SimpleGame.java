/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplegame;

import java.util.ArrayList;
import java.util.Iterator;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 *
 * @author Ilnar
 */
public class SimpleGame extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button Start = new Button();
        Button How = new Button();
        Button Exit = new Button();
        Start.setText("Start");
        Start.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                StackPane secondaryLayout = new StackPane();

                Scene newScene = new Scene(secondaryLayout);
                Stage newWindow = new Stage();
                newWindow.setTitle("New Game");
                newWindow.setScene(newScene);
                newWindow.setX(primaryStage.getX() - 500);
                newWindow.setY(primaryStage.getY() - 200);
                Canvas screen = new Canvas(1000, 1000);
                secondaryLayout.getChildren().add(screen);
                GraphicsContext gc1 = screen.getGraphicsContext2D();
                Image map = new Image("map.png");
                newWindow.show();
                ArrayList<String> input = new ArrayList<String>();

                newScene.setOnKeyPressed(
                        new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent e) {
                        String code = e.getCode().toString();
                        if (!input.contains(code)) {
                            input.add(code);
                        }
                    }
                });
                newScene.setOnKeyReleased(
                        new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent e) {
                        String code = e.getCode().toString();
                        input.remove(code);
                    }
                });
                Sprite player = new Sprite();
                player.setImage("char.png");
                player.setPosition(560, 840);

                ArrayList<Sprite> coinList = new ArrayList<Sprite>();

                for (int i = 0; i < 45; i++) {
                    Sprite coin = new Sprite();
                    coin.setImage("coin.png");
                    double px = 800 * Math.random() + 50;
                    double py = 700 * Math.random() + 50;
                    coin.setPosition(px, py);
                    coinList.add(coin);
                }
                IntValue lastNanoTime = new IntValue(System.nanoTime());

                IntValue coinCount = new IntValue(0);

                new AnimationTimer() {
                    public void handle(long currentNanoTime) {
                        // calculate time since last update.
                        double elapsedTime = (currentNanoTime - lastNanoTime.value) / 1000000000.0;
                        lastNanoTime.value = currentNanoTime;

                        // game logic
                        int speed = 50;
                        player.setVelocity(0, 0);
                        if (input.contains("SHIFT")) {
                            speed = speed * 4;
                        }
                        if (input.contains("LEFT")) {
                            player.addVelocity(-speed, 0);
                        }
                        if (input.contains("RIGHT")) {
                            player.addVelocity(speed, 0);
                        }
                        if (input.contains("UP")) {
                            player.addVelocity(0, -speed);
                        }
                        if (input.contains("DOWN")) {
                            player.addVelocity(0, speed);
                        }

                        player.update(elapsedTime);

                        // collision detection
                        Iterator<Sprite> coinIter = coinList.iterator();
                        while (coinIter.hasNext()) {
                            Sprite coin = coinIter.next();
                            if (player.intersects(coin)) {
                                coinIter.remove();
                                coinCount.value++;
                            }
                        }

                        // render
                        gc1.clearRect(0, 0, 1000, 1000);
                        gc1.drawImage(map, 0, 0);
                        player.render(gc1);

                        for (Sprite coin : coinList) {
                            coin.render(gc1);
                        }
                        String pointsText = "Coins: " + (1 * coinCount.value);
                        gc1.setFill(Color.YELLOW);
                        gc1.setStroke(Color.BLACK);
                        gc1.setLineWidth(2);
                        Font theFont = Font.font("Times New Roman", FontWeight.BOLD, 40);
                        gc1.setFont(theFont);
                        gc1.fillText(pointsText, 800, 36);
                        gc1.strokeText(pointsText, 800, 36);
                        if (coinCount.value == 45) {
                            Button close = new Button();
                            close.setText("Claim reward");
                            close.setOnAction(new EventHandler<ActionEvent>() {

                                @Override
                                public void handle(ActionEvent event) {
                                    System.out.println("Finish");
                                    // get a handle to the stage
                                    Stage stage = (Stage) close.getScene().getWindow();
                                    // do what you have to do
                                    stage.close();
                                }
                            });
                            String winText = "Congratulation!!!\n"
                                    + "     YOU WIN";
                            gc1.setFill(Color.RED);
                            gc1.setStroke(Color.BLACK);
                            gc1.setLineWidth(2);
                            Font winFont = Font.font("Times New Roman", FontWeight.BOLD, 50);
                            gc1.setFont(winFont);
                            gc1.fillText(winText, 300, 500);
                            gc1.strokeText(winText, 300, 500);

                        }
                    }

                }.start();

            }

        });
        //Button How = new Button();
        How.setText("Help");
        How.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("CONTROLS:\n"
                        + "Arrows to move\n"
                        + "SHIFT to speed up\n"
                        + "HOW TO PLAY:\n"
                        + "Collect 45 coins to win.");
            }
        });
        Exit.setText("Exit");
        Exit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // get a handle to the stage
                Stage stage = (Stage) Exit.getScene().getWindow();
                // do what you have to do
                stage.close();
            }
        });

        primaryStage.setTitle("Simple game");
        Pane root = new Pane();
        Scene theScene = new Scene(root);
        primaryStage.setScene(theScene);

        Canvas canvas = new Canvas(234, 336);
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getStylesheets().add("css/buttonStyle.css");
        Image menuBG = new Image("menuBG.png");
        root.getChildren().add(Start);
        Start.relocate(10, 10);
        root.getChildren().add(How);
        How.relocate(10, 70);
        root.getChildren().add(Exit);
        Exit.relocate(10, 130);
        gc.drawImage(menuBG, 0, 0);
        primaryStage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
