package javafx;

import java.awt.Point;

import javafx.scene.image.Image;

//GUI AWTフレーム
//課題05配布ソース（変更不可）
public abstract class Avatar {
    public final Image image;

    // xとyを持つPointクラス (https://docs.oracle.com/javase/jp/8/docs/api/)
    protected Point position;


    public Avatar(Point p, String avatarImage) {
        position = p;
        String dirName = (getClass().getResource("./")).toString();
        image = new Image(dirName+avatarImage);
    }

    public int getPositionX() {
        return position.x;
    }

    public int getPositionY() {
        return position.y;
    }
    
    
}