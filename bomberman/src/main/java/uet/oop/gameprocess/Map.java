package uet.oop.gameprocess;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import uet.oop.entities.Bomb;
import uet.oop.entities.items.BombsItem;
import uet.oop.entities.items.FlameItem;
import uet.oop.entities.items.Item;
import uet.oop.entities.items.Portal;
import uet.oop.entities.items.SpeedItem;
import uet.oop.entities.items.WallpassItem;
import uet.oop.entities.movingentities.Bomber;
import uet.oop.entities.movingentities.enemies.Balloom;
import uet.oop.entities.movingentities.enemies.Doll;
import uet.oop.entities.movingentities.enemies.Enemy;
import uet.oop.entities.movingentities.enemies.Kondoria;
import uet.oop.entities.movingentities.enemies.Minvo;
import uet.oop.entities.movingentities.enemies.Oneal;

public class Map {
    private int row; // numbers of row
    private int column; // numbers of column
    private char[][] map; // 2D char array to save non-moving enetities: grass, brick, wall

    private Bomber bomber; // bomber
    private final List<Enemy> enemies; // enemies
    private final List<Bomb> bombs; // bombs
    private final List<Item> items; // power-up items

    private final Portal portal; // portal

    public Map() throws FileNotFoundException {
        this.row = 1;
        this.column = 1;

        this.bomber = new Bomber();
        this.portal = new Portal();
        this.bombs = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public Map(Map m) throws FileNotFoundException {
        setRow(m.getRow());
        setColumn(m.getColumn());
        setMap(m.getMap());

        this.bomber = new Bomber(m.bomber);
        this.portal = new Portal(m.portal);
        this.bombs = new ArrayList<>(m.getBombs());
        this.enemies = new ArrayList<>(m.getEnemy());
        this.items = new ArrayList<>(m.getItems());
    }

    public Map(int row, int column, char[][] map) throws FileNotFoundException {
        this.row = row;
        this.column = column;

        this.bomber = new Bomber();
        this.portal = new Portal();
        this.bombs = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();

        setMap(map);
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setBomber(Bomber bomber) {
        this.bomber = bomber;
    }

    public Bomber getBomber() {
        return bomber;
    }

    public List<Enemy> getEnemy() {
        return enemies;
    }

    public Portal getPortal() {
        return portal;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public List<Item> getItems() {
        return items;
    }

    /**
     * set up map from a 2D char array:
     *
     * @param map 2D char array from external file
     * @throws FileNotFoundException when the file is not found
     */
    public void setMap(char[][] map) throws FileNotFoundException {
        this.map = new char[this.row][this.column];
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.column; j++) {
                switch (map[i][j]) {
                    case 'x': // portal
                        this.portal.setY(i);
                        this.portal.setX(j);
                        this.map[i][j] = '*';
                        break;
                    case 'p': // bomber
                        this.bomber = new Bomber(j, i);
                        this.map[i][j] = ' ';
                        break;
                    case '1': // ballom
                        Balloom balloom = new Balloom(j, i);
                        enemies.add(balloom);
                        this.map[i][j] = ' ';
                        break;
                    case '2': // oneal
                        Oneal oneal = new Oneal(j, i);
                        enemies.add(oneal);
                        this.map[i][j] = ' ';
                        break;
                    case '3': // doll
                        Doll doll = new Doll(j, i);
                        enemies.add(doll);
                        this.map[i][j] = ' ';
                        break;
                    case '4': // minvo
                        Minvo minvo = new Minvo(j, i);
                        enemies.add(minvo);
                        this.map[i][j] = ' ';
                        break;
                    case '5': // kondoria
                        Kondoria kondoria = new Kondoria(j, i);
                        enemies.add(kondoria);
                        this.map[i][j] = ' ';
                        break;
                    case 's': // speed item
                        SpeedItem speedItem = new SpeedItem(j, i);
                        items.add(speedItem);
                        this.map[i][j] = '*';
                        break;
                    case 'f': // flame item
                        FlameItem flameItem = new FlameItem(j, i);
                        items.add(flameItem);
                        this.map[i][j] = '*';
                        break;
                    case 'w': // wallpass item
                        WallpassItem wallpassItem = new WallpassItem(j, i);
                        items.add(wallpassItem);
                        this.map[i][j] = '*';
                        break;
                    case 'n': // bombs item
                        BombsItem bombsItem = new BombsItem(j, i);
                        items.add(bombsItem);
                        this.map[i][j] = '*';
                        break;
                    default:
                        this.map[i][j] = map[i][j];
                        break;
                }
            }
        }
    }

    public char[][] getMap() {
        return map;
    }

    /**
     * set up map from input File
     * 
     * @param inputFile File
     * @throws FileNotFoundException when file not found
     */
    public void setMapFromFile(ReadFromFile inputFile) throws FileNotFoundException {
        this.setRow(inputFile.getRow_read());
        this.setColumn(inputFile.getColumn_read());
        this.setMap(inputFile.getMap_read());
    }
}