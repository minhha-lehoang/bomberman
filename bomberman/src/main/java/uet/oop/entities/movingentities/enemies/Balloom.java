package uet.oop.entities.movingentities.enemies;

import java.io.FileNotFoundException;
import java.util.Random;

import uet.oop.gameprocess.Map;

public class Balloom extends Enemy {

    @Override
    public void setupImage() throws FileNotFoundException {
        super.setupImage();

        for (int i = 0; i < 3; i++) {

            super.enemy_images[0][i] = getImage("balloom_right" + i + ".png", "sprites");

            super.enemy_images[1][i] = getImage("balloom_left" + i + ".png", "sprites");
        }

        super.enemy_dead[0] = getImage("balloom_dead.png", "sprites");
    }

    public Balloom(int x, int y) throws FileNotFoundException {
        super(x, y);
        super.setDirection(0);
        super.setAbleToPassWall(false);

        setDelaytime(100);
        setSpeed(0.5);
        setStep(getSpeed() / 8);
        setStepCount(0);

        setupImage();
        setImage(enemy_images[0][0]);
    }

    public void MOVE(Map map) {
        if (!isAlive()) {
            return;
        }

        Random random = new Random();
        int nextDirection = random.nextInt(4);

        switch (nextDirection) {
            case 0:
                if (legal_move(map, getY(), getX() + 1)) {
                    setDirection(nextDirection);
                    setOldX(getDoubleX());
                    setDoubleX(getDoubleX() + 1);
                    setX((int) getDoubleX());
                    setMoving(true);
                }
                break;
            case 1:
                if (legal_move(map, getY() + 1, getX())) {
                    setDirection(nextDirection);
                    setOldY(getDoubleY());
                    setDoubleY(getDoubleY() + 1);
                    setY((int) getDoubleY());
                    setMoving(true);
                }
                break;
            case 2:
                if (legal_move(map, getY(), getX() - 1)) {
                    setDirection(nextDirection);
                    setOldX(getDoubleX());
                    setDoubleX(getDoubleX() - 1);
                    setX((int) getDoubleX());
                    setMoving(true);
                }
                break;
            case 3:
                if (legal_move(map, getY() - 1, getX())) {
                    setDirection(nextDirection);
                    setOldY(getDoubleY());
                    setDoubleY(getDoubleY() - 1);
                    setY((int) getDoubleY());
                    setMoving(true);
                }
                break;
            default:
                break;
        }

        setTimeBefore(System.currentTimeMillis());
        setTimebeforeeachstep(System.currentTimeMillis());
    }

}
