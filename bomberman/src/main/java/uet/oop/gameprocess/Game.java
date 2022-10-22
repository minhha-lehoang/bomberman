package uet.oop.gameprocess;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import uet.oop.entities.Bomb;
import uet.oop.entities.items.Item;
import uet.oop.entities.movingentities.Balloom;
import uet.oop.entities.movingentities.Bomber;
import uet.oop.entities.movingentities.Doll;
import uet.oop.entities.movingentities.Enemy;
import uet.oop.entities.movingentities.Minvo;
import uet.oop.entities.movingentities.Oneal;

public class Game implements HandleImage {
    private int level;
    private long score;
    private boolean GameOver;
    private boolean QuitGame;
    private boolean passLevel;

    private List<Map> maps;

    private Map gameMap;
    private Bomber bomber;

    public GameCanvas gameCanvas;

    private Image grassImage;
    private Image wallImage;
    private Image brickImage;

    private long before;

    public Game(List<Map> maps, GameCanvas canvas) throws FileNotFoundException {
        this.maps = maps;
        gameMap = maps.get(0);
        this.bomber = gameMap.getBomber();

        this.gameCanvas = canvas;

        score = 0;
        GameOver = false;
        QuitGame = false;
        passLevel = true;

        setGrassImage(getImage("grass.png"));
        setBrickImage(getImage("brick.png"));
        setWallImage(getImage("wall.png"));
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void setBrickImage(Image brickImage) {
        this.brickImage = brickImage;
    }

    public void setGrassImage(Image grassImage) {
        this.grassImage = grassImage;
    }

    public void setWallImage(Image wallImage) {
        this.wallImage = wallImage;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isGameOver() {
        return GameOver;
    }

    public boolean isQuitGame() {
        return QuitGame;
    }

    public void setGameOver(boolean gameOver) {
        GameOver = gameOver;
    }

    public void setBefore(long before) {
        this.before = before;
    }

    public long getBefore() {
        return before;
    }

    public boolean isPassLevel() {
        return passLevel;
    }

    public List<Map> getMaps() {
        return maps;
    }

    private void drawBackground() {
        gameCanvas.context.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gameCanvas.context.setFill(Color.LINEN);
        gameCanvas.context.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    };

    public void drawMap() {
        for (int i = 0; i < gameMap.getRow(); i++) {
            for (int j = 0; j < gameMap.getColumn(); j++) {
                if (gameMap.getMap()[i][j] == '#') {

                    render(gameCanvas.context, wallImage, j, i);

                } else if (gameMap.getMap()[i][j] == '*') {

                    render(gameCanvas.context, brickImage, j, i);

                } else {

                    render(gameCanvas.context, grassImage, j, i);

                }
            }
        }
    };

    public void drawItem() {
        if (!gameMap.getPortal().isHide()) {
            render(gameCanvas.context, gameMap.getPortal().getImage(), gameMap.getPortal().getX(),
                    gameMap.getPortal().getY());
        }

        for (Item item : gameMap.getItems()) {
            if (!item.isHide()) {
                render(gameCanvas.context, item.getImage(), item.getX(), item.getY());
            }
        }
    }

    public void drawMovingEntities() {
        render(gameCanvas.context, bomber.getImage(), bomber.getOldX(), bomber.getOldY());

        for (Enemy enemy : gameMap.getEnemy()) {
            render(gameCanvas.context, enemy.getImage(), enemy.getOldX(), enemy.getOldY());
        }
    }

    private void drawBomb() {
        for (Bomb bomb : gameMap.getBombs()) {
            render(gameCanvas.context, bomb.getImage(), bomb.getX(), bomb.getY());

            for (Bomb edge : bomb.getEdges()) {
                if (edge != null) {
                    if (gameMap.getMap()[edge.getY()][edge.getX()] != '#') {
                        if (gameMap.getMap()[edge.getY()][edge.getX()] == '*') {
                            render(gameCanvas.context, bomb.tempBrickImage, edge.getX(), edge.getY());
                        }
                        render(gameCanvas.context, edge.getImage(), edge.getX(), edge.getY());
                    }
                }
            }

            for (Bomb flame : bomb.getRightFlames()) {
                render(gameCanvas.context, flame.getImage(), flame.getX(), flame.getY());
            }

            for (Bomb flame : bomb.getDownFlames()) {
                render(gameCanvas.context, flame.getImage(), flame.getX(), flame.getY());
            }

            for (Bomb flame : bomb.getLeftFlames()) {
                render(gameCanvas.context, flame.getImage(), flame.getX(), flame.getY());
            }

            for (Bomb flame : bomb.getUpFlames()) {
                render(gameCanvas.context, flame.getImage(), flame.getX(), flame.getY());
            }
        }
    }

    public void drawScene() {
        drawBackground();
        drawMap();
        drawItem();
        drawMovingEntities();

        if (!gameMap.getBombs().isEmpty()) {
            drawBomb();
        }
    };

    public void toNextLevel() {
        level = level + 1;
        if (maps.size() >= level) {
            gameMap = maps.get(level - 1);
            passLevel = false;
            bomber = gameMap.getBomber();
        }
    }

    public void update() throws FileNotFoundException {

        bomber.update(gameMap);

        if (!bomber.isAlive()) {
            if (System.currentTimeMillis() - bomber.getTimesincedead() > 400) {
                bomber = new Bomber(1, 1);
                gameMap.setBomber(bomber);
            } else {
                if (System.currentTimeMillis() - bomber.getDeadBeforeTime() > 100)
                    bomber.DEAD();
            }
        }

        List<Enemy> toRemoveEnemies = new ArrayList<>();
        for (Enemy enemy : gameMap.getEnemy()) {

            enemy.update(gameMap);

            if (!enemy.isAlive()) {
                if (System.currentTimeMillis() - enemy.getTimesincedead() > 400) {
                    toRemoveEnemies.add(enemy);
                } else {
                    if (System.currentTimeMillis() - enemy.getDeadBeforeTime() > 100)
                        enemy.DEAD();
                }
            }
        }
        gameMap.getEnemy().removeAll(toRemoveEnemies);
        if (this.gameMap.getEnemy().isEmpty()) {
            passLevel = true;
            gameMap.getPortal().ACTIVATE();
        }

        List<Bomb> toRemoveBombs = new ArrayList<>();
        for (Bomb bomb : gameMap.getBombs()) {
            if (!bomb.isExplode() && !bomb.isPlace()) {
                toRemoveBombs.add(bomb);
            } else {
                bomb.update(gameMap);
            }
        }
        gameMap.getBombs().removeAll(toRemoveBombs);

        List<Item> toRemoveItems = new ArrayList<>();
        for (Item item : gameMap.getItems()) {
            if (bomber.getX() == item.getX() && bomber.getY() == item.getY()) {
                item.beingReceived(bomber);
            }

            if (item.isReceived()) {
                toRemoveItems.add(item);
            }
        }
        gameMap.getItems().removeAll(toRemoveItems);

        if (bomber.getX() == gameMap.getPortal().getX() && bomber.getY() == gameMap.getPortal().getY()
                && gameMap.getPortal().isActivate()) {
            toNextLevel();
        }
    }

    public void handle() {
        gameCanvas.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case RIGHT:
                case D:
                    if (bomber.legal_move(gameMap, bomber.getY(), bomber.getX() + 1) && !bomber.isMoving()) {
                        bomber.setDirection(0);
                        bomber.setStepCount(0);
                        bomber.setOldX(bomber.getDoubleX());
                        bomber.setDoubleX(bomber.getDoubleX() + 1);
                        bomber.setX((int) bomber.getDoubleX());
                        bomber.setMoving(true);

                        bomber.setTimeBefore(System.currentTimeMillis());
                        System.out.println("Go Right");
                    }
                    break;
                case DOWN:
                case S:
                    if (bomber.legal_move(gameMap, bomber.getY() + 1, bomber.getX()) && !bomber.isMoving()) {
                        bomber.setDirection(1);
                        bomber.setStepCount(0);
                        bomber.setOldY(bomber.getDoubleY());
                        bomber.setDoubleY(bomber.getDoubleY() + 1);
                        bomber.setY((int) bomber.getDoubleY());
                        bomber.setMoving(true);

                        bomber.setTimeBefore(System.currentTimeMillis());
                        System.out.println("Go Down");
                    }
                    break;
                case LEFT:
                case A:
                    if (bomber.legal_move(gameMap, bomber.getY(), bomber.getX() - 1) && !bomber.isMoving()) {
                        bomber.setDirection(2);
                        bomber.setStepCount(0);
                        bomber.setOldX(bomber.getDoubleX());
                        bomber.setDoubleX(bomber.getDoubleX() - 1);
                        bomber.setX((int) bomber.getDoubleX());
                        bomber.setMoving(true);

                        bomber.setTimeBefore(System.currentTimeMillis());
                        System.out.println("Go Left");
                    }
                    break;
                case UP:
                case W:
                    if (bomber.legal_move(gameMap, bomber.getY() - 1, bomber.getX()) && !bomber.isMoving()) {
                        bomber.setDirection(3);
                        bomber.setStepCount(0);
                        bomber.setOldY(bomber.getDoubleY());
                        bomber.setDoubleY(bomber.getDoubleY() - 1);
                        bomber.setY((int) bomber.getDoubleY());
                        bomber.setMoving(true);

                        bomber.setTimeBefore(System.currentTimeMillis());
                        System.out.println("Go Up");
                    }
                    break;

                case ESCAPE:
                    QuitGame = true;
                    System.out.println("Quit Game");
                    break;

                case SPACE:
                    if (bomber.getBombs() > 0) {
                        Bomb bomb = new Bomb(bomber);
                        gameMap.getBombs().add(bomb);

                        System.out.println("Place Bomb. Bombs remain: " + bomber.getBombs());
                    }
                    break;
                default:
                    break;
            }
        });
        for (Enemy enemy : gameMap.getEnemy()) {
            long now = System.currentTimeMillis();
            if (now - enemy.getTimeBefore() > 1000 && (enemy instanceof Doll || enemy instanceof Minvo)) {
                enemy.MOVE(gameMap);
            } else if (now - enemy.getTimeBefore() > 1500 && enemy instanceof Oneal) {
                enemy.MOVE(gameMap);
            } else if (now - enemy.getTimeBefore() > 2000 && enemy instanceof Balloom) {
                enemy.MOVE(gameMap);
            }

        }
        before = System.nanoTime();
    }
}
