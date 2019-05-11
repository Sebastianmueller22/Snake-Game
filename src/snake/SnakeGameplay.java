package snake;

import java.util.ArrayList;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * a standalone Class that creates a SnakeGame Remake
 * 
 * @author Sebastian
 *
 */

public class SnakeGameplay extends Application {

	private Pane root = new Pane();
	private int appWidth = 600;
	private int appHeight = 600;
	private int cubeSize = 5;
	private int xDir = 0;
	private int yDir = 0;
	private double posX = appWidth / 2;
	private double posY = appHeight / 2;
	private int score = 0;
	Rectangle food;
	boolean intersectedWithFood = false;
	ArrayList<Rectangle> snake = new ArrayList<Rectangle>();
	Text gameOver = new Text();
	Text scoreText = new Text();

	private Parent createContent() {
		root.setPrefSize(appWidth, appHeight);
		root.getChildren().add(gameOver);
		snake.add(new Rectangle(appWidth / 2, appHeight / 2, cubeSize, cubeSize));
		snake.get(0).setFill(Color.BROWN);
		root.getChildren().add(snake.get(0));

		// sets the text that appears when the snake dies
		gameOver.setVisible(false);
		gameOver.setScaleX(2);
		gameOver.setScaleY(2);
		gameOver.setFill(Color.YELLOW);
		gameOver.setText("Game Over");
		gameOver.setTranslateX(appWidth / 2 - 60);
		gameOver.setTranslateY(appHeight / 2 + 40);

		newFood();

		AnimationTimer timer = new AnimationTimer() {
			private long lastUpdate = 0;

			@Override
			public void handle(long now) {
				if (now - lastUpdate >= 39_000_000) {
					update();
					lastUpdate = now;
				}
			}
		};
		timer.start();

		return root;
	}

	/**
	 * creates a new FoodBlock at a random Spot
	 * 
	 */
	public void newFood() {
		Random randomPosition = new Random();
		root.getChildren().remove(food);
		this.food = new Rectangle(cubeSize, cubeSize);
		root.getChildren().add(food);
		food.setTranslateX(randomPosition.nextInt(appWidth - 5));
		food.setTranslateY(randomPosition.nextInt(appHeight - 5));
		food.setFill(Color.YELLOW);
		intersectedWithFood = false;
	}

	/**
	 * checks for intersection with the randomly created foodblock.
	 */
	public boolean foodIntersection() {
		intersectedWithFood = snake.get(snake.size() - 1).getBoundsInParent().intersects(food.getBoundsInParent());
		return intersectedWithFood;
	}

	/**
	 * checks if the snake bites its tail
	 */

	public boolean headIntersection() {
		boolean intersection = false;
		for (int i = 0; i < snake.size() - 2; i++) {
			Shape intersectionWithTail = Shape.intersect(snake.get(snake.size() - 1), snake.get(i));
			// Width is always -1, unless an intersection occurred.
			if (intersectionWithTail.getBoundsInLocal().getWidth() != -1)
				intersection = true;
		}
		return intersection;

	}

	/**
	 * ends the game
	 * 
	 */
	public void endGame() {
		// running = false;
		snake.clear();
		gameOver.setVisible(true);
		root.getChildren().clear();
		root.getChildren().add(gameOver);
		gameOver.setVisible(true);
	}

	/**
	 * creates the score Text and increments the score, so that it can be updated
	 * continuously.
	 */
	public void score() {
		score++;
		root.getChildren().remove(scoreText);
		root.getChildren().add(scoreText);
		scoreText.setText("Score:" + score);
		scoreText.setVisible(true);
		scoreText.setScaleX(2);
		scoreText.setScaleY(2);
		scoreText.setFill(Color.BLUE);
		scoreText.setTranslateX(appWidth - 70);
		scoreText.setTranslateY(20);
	}

	/**
	 * removes the element at spot 0, so that the illusion of movement is created
	 */
	public void remove() {
		root.getChildren().remove(snake.get(0));
		snake.remove(0);
	}

	/**
	 * handles movement is called once for every frame it works by adding a new
	 * Rectangle as the head of the snake in the spot that is determined by the
	 * position of the old head, plus the direction in which the snake is moving.
	 * Then, the last element of the snake (stored in an ArrayList) is deleted and
	 * thus the illusion is created, that the hole snake moved by one CubeSize.
	 */
	public void move() {
		// checks, if there was User Input
		if (xDir != 0 || yDir != 0) {
			snake.add(new Rectangle(cubeSize, cubeSize));
			root.getChildren().add(snake.get(snake.size() - 1));
			snake.get(snake.size() - 1).setFill(Color.BROWN);
			snake.get(snake.size() - 1).setTranslateX(posX + xDir);
			snake.get(snake.size() - 1).setTranslateY(posY + yDir);
			// remove only, if the snake didn't eat the food
			if (!foodIntersection()) {
				remove();
			} else {
				newFood();
				System.out.println("this works");
				score();
			}
			// variables needed to know where to add the new head of the snake.
			posX = snake.get(snake.size() - 1).getTranslateX();
			posY = snake.get(snake.size() - 1).getTranslateY();
		}
	}

	/**
	 * updates the pane and calls the move method
	 * 
	 * intersections are also handled here. TODO: Out of Bounds Exception when the
	 * snake hits the walls and itself? 0.o
	 */

	public void update() {
		move();

		if (headIntersection())
			endGame();
		System.out.println("snakeSize:" + snake.size());

		if (snake.get(snake.size() - 1).getTranslateX() - xDir < 0
				|| snake.get(snake.size() - 1).getTranslateX() + cubeSize > appWidth) {
			endGame();
		}
		if (snake.get(snake.size() - 1).getTranslateY() < 0
				|| snake.get(snake.size() - 1).getTranslateY() + cubeSize > appHeight) {
			endGame();
		}

	}

	/**
	 * Sets scene up
	 * 
	 * sets the Direction of the Snake in Case of a UserEvent to a new value
	 * 
	 * 
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("still learning");
		Scene scene = new Scene(createContent());
		scene.setFill(Color.BLACK);
		primaryStage.setScene(scene);
		primaryStage.show();
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			case LEFT:
				if (xDir != 10) {
					xDir = -10;
					yDir = 0;
				}
				break;
			case RIGHT:
				if (xDir != -10) {
					xDir = 10;
					yDir = 0;
				}
				break;
			case UP:
				if (yDir != 10) {
					yDir = -10;
					xDir = 0;
				}
				break;
			case DOWN:
				if (yDir != -10) {
					xDir = 0;
					yDir = 10;
				}
				break;
			case SPACE:
				break;
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

}
