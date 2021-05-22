package utility;

import java.util.ArrayList;
import java.util.Date;

import character.Enemy;
import config.Config;
import gui.GameMap;
import logic.GameState;
import sound.SoundManager;
import utility.ResourceManager.ImageResource;
import utility.ResourceManager.SceneResource;
import utility.ResourceManager.SoundResource;
import weapon.Gun;

public class WaveManager {

	private static ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
	private static int[] enemyPerWaveList = { 1, 2, 2, 3, 3, 4, 4, 5, 5, 5, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 13,
			13, 14, 14, 15, 15, 16, 16, 17, 17, 0 };
	private static int wave = 0;
	private static String displayWaveText = "";
	private static long displayWaveTextTimestamp = 0, waveEndTimestamp = 0;
	private static long enemySpawnTimeDelay = 0, lastEnemySpawnTime = 0;
	private static boolean isWaveEnd = false;
	private static boolean isPauseBetweenWaveEnd = false;
	private static boolean isSpawningEnemy = false;
	private static boolean isEliteSpawn = false;
	private static int enemyPerWave, enemySpawnedInCurrentWave;

	public static void initialize() {
		wave = 0;
		displayWaveText = "";
		displayWaveTextTimestamp = 0;
		startNewWave();
	}

	private static void startNewWave() {
		isWaveEnd = false;
		wave += 1;
		isEliteSpawn = false;
		// End game
		if (GameState.getGameMode().getGameModeName() == "Normal" && wave == 31) {
			SoundManager.playSoundEffect(SoundResource.ENDING_GOOD, 0.5);
			GameState.setSceneResource(SceneResource.ENDING_GOOD);
		}

		displayWaveText = "Wave " + Integer.toString(WaveManager.getWave());
		displayWaveTextTimestamp = (new Date()).getTime();

		if (GameState.getGameMode().getGameModeName() == "Normal") {
			enemyPerWave = enemyPerWaveList[wave - 1];
		} else if (GameState.getGameMode().getGameModeName() == "Endless") {
			enemyPerWave = (wave % 10 == 0) ? wave + 3 : wave;
		}
		enemySpawnedInCurrentWave = 0;
		lastEnemySpawnTime = (new Date()).getTime();
		enemySpawnTimeDelay = (long) (Config.MINIMUM_WAVE_ENEMY_SPAWN_TIME_DELAY + (Math.random()
				* (Config.MAXIMUM_WAVE_ENEMY_SPAWN_TIME_DELAY - Config.MINIMUM_WAVE_ENEMY_SPAWN_TIME_DELAY)));
		isSpawningEnemy = true;

		Logger.log("Start Wave " + wave);
	}

	private static void spawnEnemy() {
		int randomSpawnTile = (int) (Math.random() * GameMap.getEnemySpawnableTile().size());
		Position spawnLocation = new Position(
				(int) (GameMap.getEnemySpawnableTile().get(randomSpawnTile).X * Config.TILE_W + (Config.TILE_W / 2)),
				(int) (GameMap.getEnemySpawnableTile().get(randomSpawnTile).Y * Config.TILE_H) + (Config.TILE_H / 2));

		ImageResource enemyImageResource, gunImageResource;
		String name;
		int health, armor, speed, attackDamage, bulletSpeed, moneyDrop;
		double attackSpeed;

		int enemyType = randomEnemyType();

		switch (enemyType) {
		case 0:
			enemyImageResource = ImageResource.SPRITE_KNIGHT;
			gunImageResource = ImageResource.GUN_AK47;
			name = "Knight Rifle";
			health = calculateStat(60);
			armor = 0;
			speed = 1;
			attackDamage = calculateStat(10);
			attackSpeed = 0.75;
			bulletSpeed = 3;
			moneyDrop = 3;
			break;
		case 1:
			enemyImageResource = ImageResource.SPRITE_KNIGHT;
			gunImageResource = ImageResource.GUN_PISTOL;
			name = "Knight Pistol";
			health = calculateStat(40);
			armor = 0;
			speed = 2;
			attackDamage = calculateStat(10);
			attackSpeed = 0.5;
			bulletSpeed = 3;
			moneyDrop = 2;
			break;
		case 2:
			enemyImageResource = ImageResource.SPRITE_ELITE_KNIGHT;
			gunImageResource = ImageResource.GUN_AK47;
			name = "Elite Knight";
			health = calculateStat(120);
			armor = 0;
			speed = 1;
			attackDamage = calculateStat(14);
			attackSpeed = 1;
			bulletSpeed = 5;
			moneyDrop = calculateStat(6);
			break;
		case 3:
			enemyImageResource = ImageResource.SPRITE_ELITE_KNIGHT;
			gunImageResource = ImageResource.GUN_MACHINEGUN;
			name = "Super Elite Knight";
			health = calculateStat(120) * 4;
			armor = 0;
			speed = 1;
			attackDamage = calculateStat(14);
			attackSpeed = 1.5;
			bulletSpeed = 5;
			moneyDrop = calculateStat(30);
			break;
		default:
			enemyImageResource = ImageResource.SPRITE_KNIGHT;
			gunImageResource = ImageResource.GUN_AK47;
			name = "Knight Rifle";
			health = calculateStat(60);
			armor = 0;
			speed = 1;
			attackDamage = calculateStat(10);
			attackSpeed = 1;
			bulletSpeed = 3;
			moneyDrop = 3;
			break;
		}
		enemyList
				.add(new Enemy(enemyImageResource, Config.CHARACTER_W, Config.CHARACTER_H, name, health, armor, speed,
						new Gun(gunImageResource, attackDamage, attackSpeed, ImageResource.ENEMY_BULLET, bulletSpeed,
								10, 10, Config.ENEMY_TEAM, Config.ZINDEX_ENEMY),
						Config.ENEMY_TEAM, moneyDrop, spawnLocation));
	}

	public static int calculateStat(int normalStat) {
		return normalStat + ((normalStat / 2) * (wave / 10));
	}

	public static int randomEnemyType() {
		int randomEnemyType = (Math.random() > 0.7 ? 1 : 0);
		if (!isEliteSpawn) {
			if (wave % 10 == 0) {
				randomEnemyType = 3;
				isEliteSpawn = true;
			} else if ((wave == 5 || wave == 15)) {
				randomEnemyType = 2;
				isEliteSpawn = true;
			}
		} else if (wave > 20 && randomEnemyType == 1) {
			randomEnemyType = 1 + (Math.random() > 0.7 ? 1 : 0);
		}
		return randomEnemyType;
	}

	public static int getWave() {
		return wave;
	}

	public static String getDisplayWaveText() {
		return displayWaveText;
	}

	public static void update() {
		if (GameState.getSceneResource() == SceneResource.PLAYING) {
			if (!GameState.isPause()) {
				if (isSpawningEnemy == true && (new Date()).getTime() - lastEnemySpawnTime > enemySpawnTimeDelay) {
					spawnEnemy();
					enemySpawnedInCurrentWave += 1;
					lastEnemySpawnTime = (new Date()).getTime();
					enemySpawnTimeDelay = (long) (Config.MINIMUM_WAVE_ENEMY_SPAWN_TIME_DELAY + (Math.random()
							* (Config.MAXIMUM_WAVE_ENEMY_SPAWN_TIME_DELAY - Config.MINIMUM_WAVE_ENEMY_SPAWN_TIME_DELAY)));
					if (enemySpawnedInCurrentWave == enemyPerWave) {
						isSpawningEnemy = false;
					}
				}

				if (isPauseBetweenWaveEnd) {
					waveEndTimestamp += GameState.getLastPauseDulation();
					isPauseBetweenWaveEnd = false;
				}
				for (int i = enemyList.size() - 1; i >= 0; i--) {
					if (enemyList.get(i).isDestroyed()) {
						enemyList.remove(i);
					}
				}
				if (!isWaveEnd && enemyList.isEmpty() && enemySpawnedInCurrentWave == enemyPerWave) {
					Logger.log(String.format("Wave %d End", wave));
					isWaveEnd = true;
					waveEndTimestamp = (new Date()).getTime();
				}
				if (isWaveEnd) {
					displayWaveText = String.format("Wave %d Begin In %d Second(s)", wave + 1,
							(Config.DELAY_BETWEEN_WAVE - ((new Date()).getTime() - waveEndTimestamp)) / 1000);
					if ((new Date()).getTime() - waveEndTimestamp >= Config.DELAY_BETWEEN_WAVE) {
						startNewWave();
					}
				}
				if (!isWaveEnd
						&& (new Date()).getTime() - displayWaveTextTimestamp >= Config.DISPLAY_WAVE_TEXT_DURATION) {
					displayWaveText = "";
				}
			} else if (isWaveEnd == true) {
				isPauseBetweenWaveEnd = true;
			}
		}
	}

	public static boolean isWaveEnd() {
		return isWaveEnd;
	}

	public static int forceStartNewWave() {
		if (isWaveEnd) {
			int timeLeft = (int) (Config.DELAY_BETWEEN_WAVE - ((new Date()).getTime() - waveEndTimestamp)) / 1000;

			waveEndTimestamp = (new Date()).getTime() - Config.DELAY_BETWEEN_WAVE;
			return timeLeft;
		} else {
			return 0;
		}
	}
}
