package javafx;

import java.awt.Point;

import javafx.scene.image.Image;

//GUI AWT�t���[��
//�ۑ�05�z�z�\�[�X�i�ύX�s�j
public abstract class Avatar {
    public final Image image;

    // x��y������Point�N���X (https://docs.oracle.com/javase/jp/8/docs/api/)
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