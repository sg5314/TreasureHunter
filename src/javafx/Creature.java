package javafx;

import java.awt.Point;

class Creature extends Avatar {
    private int hitpoint = 20;
    private int healCount;

    public Creature(Point p, String avatarImage) {
        super(p, avatarImage);
    }

    public int getHitpoint() {
        return hitpoint;
    }

    public int getHealCount() {
        return healCount;
    }

    public void move(int key) {
        if (hitpoint <= 0) return;

        switch (key) {
            case 6:
                position.x++;
                break;
            case 4:
                position.x--;
                break;
            case 8:
                position.y--;
                break;
            case 2:
                position.y++;
                break;
        }
        
        position.x=(27+position.x)%27;//27=map_Width
        
        
    }

    

    /**
     * hitpoint‚ª0ˆÈ‰º‚É‚È‚Á‚½ê‡false‚ð•Ô‚·
     * @return
     */
    public boolean damage() {System.out.println("hitpoint = 0;");
        hitpoint = 0;
        return hitpoint > 0;
        
    }
}
