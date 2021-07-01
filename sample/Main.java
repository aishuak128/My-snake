package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
//import javafx.event.EventHandler;
//import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
//import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Point;
//import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;



public class Main extends Application {
    private static final int WIDTH = 1020;
    private static final int HEIGHT = 1020;
    private static final int ROWS = 34;
    private static final int COLUMNS = ROWS;
    private static final int SQUARE_SIZE = WIDTH / ROWS;
    private static final String[] FOODS_IMAGE = new String[]{"/img/animated-gifs-ants-01.gif", "/img/animated-gifs-ants-03.gif", "/img/animated-gifs-ants-24.gif",
            "/img/animated-gifs-ants-43.gif", "/img/animated-gifs-ants-47.gif", "/img/ant-waving-animation.gif"};

    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private GraphicsContext gc;
    private List<Point> snakeBody = new ArrayList();
    private Point snakeHead;
    private Image foodImage;
    private int foodX;
    private int foodY;
    private boolean gameOver;
    private int currentDirection;
    private int score = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Beshenaya zmeya");
        Group root = new Group();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
//        Button restart_but = new Button("restart");
//        root.getChildren().add(restart_but);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        gc = canvas.getGraphicsContext2D();

        scene.setOnKeyPressed(event -> {     //new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    if (currentDirection != LEFT) {
                        currentDirection = RIGHT;
                    }
                } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                    if (currentDirection != RIGHT) {
                        currentDirection = LEFT;
                    }
                } else if (code == KeyCode.UP || code == KeyCode.W) {
                    if (currentDirection != DOWN) {
                        currentDirection = UP;
                    }
                } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                    if (currentDirection != UP) {
                        currentDirection = DOWN;
                    }
                }
//            }
        });

        for (int i = 0; i < 3; i++) {
            snakeBody.add(new Point(2, ROWS / 2));
        }
        snakeHead = snakeBody.get(0);
        generateFood();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(80), e -> run(gc)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void run(GraphicsContext gc) {
//        System.out.println(snakeBody);
        if (gameOver) {
            drawGameOver(gc);
            return;
        }
        drawBackground(gc);
        drawFood(gc);
        drawSnake(gc);
        drawScore();

        for (int i = snakeBody.size() - 1; i >= 1; i--) {
            snakeBody.get(i).x = (int)snakeBody.get(i - 1).getX();
            snakeBody.get(i).y = (int)snakeBody.get(i - 1).getY();
//            snakeBody.set(i, snakeBody.get(i - 1));
//            snakeBody.get(i) = ((int)snakeBody.get(i - 1).getX() , (int)snakeBody.get(i - 1).getY());
        }

        switch (currentDirection) {
            case RIGHT -> moveRight();
            case LEFT -> moveLeft();
            case UP -> moveUp();
            case DOWN -> moveDown();
        }

        gameOver();
        eatFood();
    }
    private void drawGameOver(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.setFont(new Font("Comic Sans MS", 70));
        gc.fillText("Ty proigral", WIDTH / 3.5, (HEIGHT / 2));
        gc.setFill(Color.web("ff0000"));
        gc.fillRoundRect(snakeHead.getX() * SQUARE_SIZE, snakeHead.getY() * SQUARE_SIZE, SQUARE_SIZE - 1, SQUARE_SIZE - 1, 35, 35);

    }
//-_______________________________________________________________________________________
    private void drawBackground(GraphicsContext gc) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if ((i + j) % 2 == 0) {
                    gc.setFill(Color.web("b7b7b7"));
                } else {
                    gc.setFill(Color.web("c1c1c1"));
                }
                gc.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void generateFood() {
            foodX = (int) (Math.random() * ROWS);
            foodY = (int) (Math.random() * COLUMNS);

            for (Point snake : snakeBody) {
                if (snake.getX() == foodX && snake.getY() == foodY) {
                    generateFood();
                }
            }
            foodImage = new Image(FOODS_IMAGE[(int) (Math.random() * FOODS_IMAGE.length)]);
    }

    private void drawFood(GraphicsContext gc) {
        gc.drawImage(foodImage, foodX * SQUARE_SIZE, foodY * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
    }

    private void drawSnake(GraphicsContext gc) {
        gc.setFill(Color.web("38761d"));
        gc.fillRoundRect(snakeHead.getX() * SQUARE_SIZE, snakeHead.getY() * SQUARE_SIZE, SQUARE_SIZE - 1, SQUARE_SIZE - 1, 20, 20);
        for (int i = 1; i < snakeBody.size(); i++) {
            gc.fillRect(snakeBody.get(i).getX() * SQUARE_SIZE, snakeBody.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE - 1,
                    SQUARE_SIZE - 1);
        }
    }

    private void moveRight() { snakeHead.x++; }
    private void moveLeft() {  snakeHead.x--; }
    private void moveUp() {    snakeHead.y--; }
    private void moveDown() {  snakeHead.y++; }

    public void gameOver() {
        if (snakeHead.x < 0 || snakeHead.y < 0 || snakeHead.x * SQUARE_SIZE >= WIDTH || snakeHead.y * SQUARE_SIZE >= HEIGHT) {
            gameOver = true;
        }
        for (int i = 1; i < snakeBody.size(); i++) {
            if (snakeHead.getX() == snakeBody.get(i).getX() && snakeHead.getY() == snakeBody.get(i).getY()) {
                gc.setFill(Color.web("ff0000"));
                gc.fillRect(snakeBody.get(i).getX() * SQUARE_SIZE, snakeBody.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE - 1,
                        SQUARE_SIZE - 1);
                gameOver = true;
                break;
            }
        }
    }

    private void eatFood() {
        if (snakeHead.getX() == foodX && snakeHead.getY() == foodY) {
            snakeBody.add(new Point(-1, -1));
            generateFood();
            score += 5;
        }
    }

    private void drawScore() {
        gc.setFill(Color.web("38761d"));
        gc.setFont(new Font("Comic Sans MS", 35));
        gc.fillText("Score: " + score, 10, 990);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
