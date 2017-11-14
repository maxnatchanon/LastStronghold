package logic;

import java.util.ArrayList;
import java.util.List;

import draw.GameScreen;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import share.IRenderable;
import share.RenderableHolder;

public class GameLogic {
	private Player player;
	private List<IRenderable> enemyList;
	private List<IRenderable> deadEnemyList;
	private List<IRenderable> bulletList;
	private int bulletState;
	private int enemyTick;
	private int currentEnemyTick;
	private int score;
	
	public GameLogic() {
		player = new Player();
		enemyList = new ArrayList<>();
		deadEnemyList = new ArrayList<>();
		bulletList = new ArrayList<>();
		bulletState = 0;
		enemyTick = 60;
		currentEnemyTick = 0;
		score = 0;
	}
	
	public void addEnemy(IRenderable enemy) {
		enemyList.add(enemy);
		RenderableHolder.getInstance().addEnemy(enemy);
	}
	
	public void addBullet(IRenderable bullet) {
		bulletList.add(bullet);
		RenderableHolder.getInstance().addBullet(bullet);
	}
	
	public void updateLogic() {
		readInput();
		updatePosition();
		checkIfEnemyDestroy();
		if (currentEnemyTick++ > enemyTick) generateEnemy();
		updateLevel();
		checkIfEndGame();
	}
	
	private void updatePosition() {
		// Update logic bullet list position
		for (IRenderable bullet : bulletList) {
			Bullet tmp = (Bullet) bullet;
			tmp.setPosition(tmp.getX() + tmp.velocityX, tmp.getY());
		}
		// Update RenderableHolder bullet list position
		for (IRenderable bullet : RenderableHolder.getInstance().getBullets()) {
			Bullet tmp = (Bullet) bullet;
			tmp.setPosition(tmp.getX() + tmp.velocityX, tmp.getY());
		}
		// Update logic enemy list position
		for (IRenderable enemy : enemyList) {
			Enemy tmp = (Enemy) enemy;
			tmp.setPosition(tmp.getX() + tmp.velocityX, tmp.getY());
		}
		// Update RenderableHolder enemy list position
		for (IRenderable enemy : RenderableHolder.getInstance().getEnemies()) {
			Enemy tmp = (Enemy) enemy;
			tmp.setPosition(tmp.getX() + tmp.velocityX, tmp.getY());
		}
	}

	private void generateEnemy() {
		int rand = (int) (Math.random() * 3 + 1);
		int position = (int) (Math.random() * 325 + 1);
		Enemy enemy;
		switch (rand) {
		case 1 :
			enemy = new EnemyRed(450, position, -17);
			break;
		case 2 :
			enemy = new EnemyBlue(450, position, -17);
			break;
		case 3 :
			enemy = new EnemyYellow(450, position, -17);
			break;
		default :
			enemy = new EnemyRed(450, position, -17);
			break;
		}
		enemyList.add((IRenderable) enemy);
		RenderableHolder.getInstance().addEnemy((IRenderable) enemy);
		currentEnemyTick = 0;
	}
	
	private void checkIfEnemyDestroy() {
		for (IRenderable bullet : bulletList) {
			for (IRenderable enemy : enemyList) {
				if (((Bullet) bullet).intersects((Enemy) enemy)) {
					bulletList.remove(bullet);
					RenderableHolder.getInstance().removeBullet(bullet);
					deadEnemyList.add(enemy);
					RenderableHolder.getInstance().addDeadEnemy(enemy);
					enemyList.remove(enemy);
					RenderableHolder.getInstance().removeEnemy(enemy);
					score += 100;
					RenderableHolder.getInstance().addScore(100);
					System.out.println("Score : " + score);
				}
			}
		}
	}
	
	private void fireBullet() {
		Bullet bullet;
		switch (bulletState) {
		case 0 :
			bullet = new BulletRed(player.getX() + 75, player.getY() + 37.5, 12);
			break;
		case 1 :
			bullet = new BulletBlue(player.getX() + 75, player.getY() + 37.5, 12);
			break;
		case 2 :
			bullet = new BulletYellow(player.getX() + 75, player.getY() + 37.5, 12);
			break;
		default :
			bullet = new BulletRed(player.getX() + 75, player.getY() + 37.5, 12);
			break;
		}
		addBullet((IRenderable) bullet);
		RenderableHolder.getInstance().addBullet((IRenderable) bullet);
	}

	private void endGame() {
		Alert alert = new Alert(AlertType.NONE, "GAME OVER", ButtonType.OK);
		alert.setContentText("Score : " + this.score);
		Platform.exit();
	}
	
	private void readInput() {
		if (GameScreen.inputs.contains("UP")) {
			player.update(0, -7);
			RenderableHolder.getInstance().updatePlayer(0, -7);
		}
		if (GameScreen.inputs.contains("DOWN")) {
			player.update(0, 7);
			RenderableHolder.getInstance().updatePlayer(0, 7);
		}
		if (GameScreen.inputs.contains("A")) {
			fireBullet();
		}
		if (GameScreen.inputs.contains("TAB")) {
			bulletState = (bulletState + 1) % 3;
			RenderableHolder.getInstance().setBulletState(bulletState);
		}
	}

	private void updateLevel() {
		if (score > 10000) {
			enemyTick = 10;
		} else if (score > 7000) {
			enemyTick = 30;
		} else if (score > 5000) {
			enemyTick = 40;
		} else if (score > 3000) {
			enemyTick = 50;
		}
	}
	
	private void checkIfEndGame() {
		for (IRenderable enemy : enemyList) {
			if (player.intersects((Enemy) enemy)) endGame();
			if (((Enemy) enemy).isOutOfScreen()) endGame();
		}
	}
	
}
