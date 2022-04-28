package javafx;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// JavaFX Canvas
public class Map extends Canvas {

    /**
     * �Q�[���I������C���^�t�F�[�X
     */
    public interface GameEndListener {
        void gameOver();

        void gameEnd();
        
        void lastone();
        
        void swordget();

		void swordrelease();

    }


    // �}�b�v�̏ڍ׏��
    private int mapSize; // 1�}�X�̃T�C�Y

    // �}�b�v�̃T�C�Y
    public final int mapWidth;
    public final int mapHeight;

    // �t�B�[���h���̃L�c�l�Ɩ؂̈ʒu���L�^
    private Avatar[][] field;
    
    private Avatar[][] honeyfield;//�󔠐�p�}�b�v     ���̈ʒu������Ă���
    
    
    // �L�c�l�ƃN�}
    private ArrayList<Creature> creatures;

    // ��
    private ArrayList<Tree> trees;
    //private Tree obstacleTree = new Tree(null); // �O���̕ǂ�\�����邽�߂�Tree

    // �͂��݂�
    // �ς̔z���������(https://docs.oracle.com/javase/jp/8/docs/api/)
    private ArrayList<Honey> honeys;

    //�n�`
    private Hornet hornet;
    
    //��
    private ArrayList<Sword> swords;

    //�Q�[���I���C�x���g���X�i�[
    private GameEndListener listener;
    
    //��̐�
	private int score=0;

	private boolean killChanse=false;//�G���E�����ԁ@������ԁ�false=�E���Ȃ�     ��get������true
	
	private int Reappearcount=0;

	private boolean appearedAgainFlag=false;


    public Map(int mapWidth, int mapHeight, int size, int avatarCount) {
        super(mapWidth * size, mapHeight * size);
        

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.mapSize = size;


        field = new Avatar[mapHeight][mapWidth];
        honeyfield=new Avatar[mapHeight][mapWidth];
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                field[j][i] = null;
                honeyfield[j][i]=null;
            }
        }
        
        trees = new ArrayList<Tree>();//�ؗp�̃��X�g��������
        honeys = new ArrayList<Honey>();// �͂��݂p�̃��X�g��������
        swords= new ArrayList<Sword>();// ���p�̃��X�g��������
        setGamemap();////////////////////////////////////////////////////////////////
       
       
    }
    
    private void setGamemap() {
    	int x = 0;
    	try {
    		String cwd = System.getProperty("user.dir");
    		System.out.println(cwd);/////�ǂ��̃f�B���N�g�������s����Ă��邩
    		
    		FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(new Stage());
    		//File file=new File("C:\\eclipse\\workspace\\javaFx_Game_backup\\bin\\class11/JAVAFx_GameMAP2.txt");////JAVAFx_GameMAP2.txt�̓ǂݍ���
            BufferedReader br = new BufferedReader(new FileReader(file));
			boolean hantei=true;
            String line;
            int y = 0;
            while ((line = br.readLine()) != null) {

                String[] ss = line.split("\t");
                for (x = 0; x < ss.length; x++) {
                    String s = ss[x];
                    switch (s) {
                        
                        case "t":
                        	Tree tree = new Tree(new Point(x, y));
                            trees.add(tree);
                            field[y][x] = tree;
                            break;
                        case "n":
                        	field[y][x] = null;
                        	break;
                         default:
                        	 System.out.println("�u���܂���");hantei=false;System.out.println(s);
                        	 break;
                    }
                }
                System.out.println(line);
                y++;
            }
            if (validField(field, y, x)&&hantei) {System.out.println("�t�@�C�����[�h����");}
             else {System.out.println("���[�h���s�I");System.out.println(x+" "+y+" "+field.length+" "+field[0].length);}
            br.close();
		} catch (FileNotFoundException e) {System.out.println("���[�h���s1");} catch (IOException e) {System.out.println("���[�h���s2");}	
    	
    	//�����Z�b�g
    	while(true) {
    		int xx = (int) (mapWidth * Math.random());
            int yy = (int) (mapHeight * Math.random());

            if (field[yy][xx] != null||honeyfield[yy][xx] !=null) {continue;}
          
            Sword sword = new Sword(new Point(xx, yy));
            swords.add(sword);
            honeyfield[yy][xx] = sword;
    		break;
    	}
    	
    	// �n�`���Z�b�g
        while (true) {
            int xx = (int) (mapWidth * Math.random());
            int yy = (int) (mapHeight * Math.random());

            if (field[yy][xx] != null) {
                continue;
            }
            hornet = new Hornet(new Point(xx, yy));
            field[yy][xx] = hornet;
            break;
        }
    	 // �L�c�l�ƃN�}���Z�b�g
        creatures = new ArrayList<Creature>();
        setCreatures(5);
        
        settakarabako(10);/////////////////////////////////��
        
        
 
        for(int i=0;i<mapHeight;i++) {
        	for(int j=0;j<mapWidth;j++) {
        		System.out.print(honeyfield[i][j]+" ");
        	}System.out.println();}
        System.out.println();
        for(int i=0;i<mapHeight;i++) {
        	for(int j=0;j<mapWidth;j++) {
        		System.out.print(field[i][j]+" ");
        	}System.out.println();}
        System.out.println();
        
	}

	private void settakarabako(int count) {
        for (int i = 0; i < count; i++) {
            int xx = (int) (mapWidth * Math.random());
            int yy = (int) (mapHeight * Math.random());

            if (field[yy][xx] != null||honeyfield[yy][xx] !=null) {
                i--;
                continue;
            }

            Honey honey = new Honey(new Point(xx, yy));
            honeys.add(honey);
            honeyfield[yy][xx] = honey;
           
        }
    }
	
	/**
     * Avatar���p���������̂���ʂɔz�u����
     */
    //�G�̏����ꏊ����///////////////////////////////////////////////////////
    private void setCreatures(int count) {
    	double dis;
        for (int i = 0; i < count; i++) {
            int xx = (int) (mapWidth * Math.random());
            int yy = (int) (mapHeight * Math.random());
            dis=Math.sqrt((xx-hornet.getPositionX())*(xx-hornet.getPositionX())+(yy-hornet.getPositionY())*(yy-hornet.getPositionY()));
            
            if (field[yy][xx] != null||honeyfield[yy][xx] !=null) {i--;continue;}
            else if(dis<7){i--;continue;}
            
           System.out.println("�G"+i+"�Ƃ̋���:"+dis);
            
            Creature creature=new Bear(new Point(xx, yy));
            creatures.add(creature);           
            field[yy][xx] = creature;
        }
      
    }
   

	private boolean validField(Avatar[][] field2, int y, int x) {
		if (field.length > 0 && field.length != y) return false;
        if (field[0].length > 0 && field[0].length != x) return false;
        return true;
	}

	public int getscore() {
    	return this.score;
    }

    public void setGameEndListener(GameEndListener listener) {
        this.listener = listener;
    }

   
    public void paint() {
    	
        GraphicsContext gc = getGraphicsContext2D();

        // �w�i��`��
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        // �L�c�l�ƃN�}��`��
        paintCreatures(gc, creatures);

        // �؂�`��
        paintTrees(gc, trees);

        // �͂��݂`��
        paintHoneys(gc, honeys);
        
        //����`��
        paintSword(gc, swords);
        
        // �n�`��`��
        paintHornet(gc,hornet);

    }
    


	private void paintSword(GraphicsContext gc, List<Sword> avatars) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		  for (int i = 0; i < avatars.size(); i++) {
	            Avatar avatar = avatars.get(i);
	            gc.drawImage(avatar.image,
	                    avatar.getPositionX() * mapSize,
	                    avatar.getPositionY() * mapSize,
	                    mapSize,
	                    mapSize);
	        }
	}

	private void paintHornet(GraphicsContext gc, Hornet h) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		gc.drawImage(hornet.image,
                hornet.getPositionX() * mapSize,
                hornet.getPositionY() * mapSize,
                mapSize,
                mapSize);
		if(killChanse) {
			gc.setFill(Color.RED);
            gc.setFont(Font.font("serif",10));
            gc.fillText("�@��"  ,
            		hornet.getPositionX() * mapSize,
            		hornet.getPositionY() * mapSize,
                    mapSize);
		}
	}
	private void paintTrees(GraphicsContext gc, List<Tree> avatars) {
        for (int i = 0; i < avatars.size(); i++) {
            Avatar avatar = avatars.get(i);
            gc.drawImage(avatar.image,
                    avatar.getPositionX() * mapSize,
                    avatar.getPositionY() * mapSize,
                    mapSize,
                    mapSize);
        }
    }

    /**
     * Creature���p���������̂�`�悷��
     */
    private void paintCreatures(GraphicsContext gc, List<Creature> creatures) {
        for (int i = 0; i < creatures.size(); i++) {
            Creature creature = creatures.get(i);
            gc.drawImage(creature.image,
                    creature.getPositionX() * mapSize,
                    creature.getPositionY() * mapSize,
                    mapSize,
                    mapSize);
            
            ///////////////////////////////////////�ړ��̂������ԍ�
            /*gc.setFill(Color.RED);
            gc.setFont(Font.font("serif",10));
            gc.fillText("" + i,
                    creature.getPositionX() * mapSize,
                    creature.getPositionY() * mapSize,
                    mapSize);*/
       
        }
    }

    /**
     * Avatar���p���������̂�`�悷��
     * �͂��݂p��paintAvatars���I�[�o�[���[�h
     * �ϔz��ɑΉ�����
     */
    private void paintHoneys(GraphicsContext gc, List<Honey> avatars) {
        for (int i = 0; i < avatars.size(); i++) {
            Avatar avatar = avatars.get(i);
            gc.drawImage(avatar.image,
                    avatar.getPositionX() * mapSize,
                    avatar.getPositionY() * mapSize,
                    mapSize,
                    mapSize);
        }
    }

    /**
     * ���̃X�e�b�v�����s����
     */
    public void nextStep() {
        // �L�c�l�ƃN�}�𓮂���
    	if(appearedAgainFlag) {Reappearcount++;System.out.println(Reappearcount);}
        moveAnimals(creatures);
        if (listener != null) {
        // �Q�[���N���A�𔻒肷��
        if(swords.size()==0&&killChanse) {listener.swordget();}
        if(swords.size()==0&&killChanse==false) {listener.swordrelease();}
        if(honeys.size() == 1) {listener.lastone();}
        if(honeys.size() == 0 && hornet.getHitpoint() > 0) {listener.gameEnd();}
        else if(hornet.getHitpoint() <= 0) { System.out.println("�G�ɓ�������");listener.gameOver();} }// �Q�[���I�[�o�[
        if(Reappearcount==10) {Reappearcount=0;appearedAgainFlag=false;System.out.println("�����X�^�[�ē���");appeareAgain();}
        }
        
    /**
     * �n�`�𓮂���
     */
    public void moveHornet(int key) {
        move(hornet, key);///////////////////////////////////////////////////////////�I
    }

    private void move(Creature creature, int key) {////////mover �̓��������� Action�@�@�@�@�@�@�@�I���C��
    	Avatar avatar = nextAvatar(creature, key);//���̕����ɍs���Ɖ������邩
        
    	
    	// �����̈ʒu��\�߃N���A���Ă���
    	field[creature.position.y][creature.position.x] = null;
    	

        if (avatar == null) {
            creature.move(key);
        }else if(creature==hornet&&avatar instanceof Bear) {//�I���N�}�ɍs��       	����
        	if(killChanse) {
        		creature.move(key);
        		removeenemy(creature);
        		killChanse=false;//���͎g���Ȃ�
        	}else {creature.move(key);creature.damage();}

        }else if (avatar instanceof Hornet) {//�N�}���I�ɍs��	����
        	Hornet h = (Hornet) avatar;
        	if(killChanse) {
        		creature.move(key);
        		removeenemy2(creature);
        		killChanse=false;
        	}else {creature.move(key); h.damage();}         
        } 
        
        field[creature.position.y][creature.position.x] = creature;
        honeycount(creature);
       
    }
    
	private void honeycount(Creature creature) {
		 
        if (creature==hornet&&honeyfield[creature.position.y][creature.position.x]instanceof Honey) {//�I�̈ړ���ɖI��                                     point!!!!            
            getHoney(creature);//�I��������
            this.score+=1;
           }
        else if(creature==hornet&&honeyfield[creature.position.y][creature.position.x]instanceof Sword) {
        	getSword(creature);
        	killChanse=true;
        }
	}

	private void removeenemy(Avatar avatar) {//���C���L�������G�ɍs��
		Creature creature = (Creature) field[avatar.getPositionY()][avatar.getPositionX()];      
        field[avatar.getPositionY()][avatar.getPositionX()] = null;
        creatures.remove(creatures.indexOf(creature));System.out.println("�G��������"); 
        appearedAgainFlag=true;
	}

	private void removeenemy2(Avatar avatar) {//�G�����C���L�����ɍs��      
        field[avatar.getPositionY()][avatar.getPositionX()] = null;
        creatures.remove(creatures.indexOf(avatar));System.out.println("�G��������");  
        appearedAgainFlag=true;
	}

	private void appeareAgain() {
		for(;;) {
		int xx = (int) (mapWidth * Math.random());
        int yy = (int) (mapHeight * Math.random());
        double dis=Math.sqrt((xx-hornet.getPositionX())*(xx-hornet.getPositionX())+(yy-hornet.getPositionY())*(yy-hornet.getPositionY()));
        
        if (field[yy][xx] != null) {continue;}
        else if(dis<7){continue;}
        
       System.out.println("�ē����̓G"+"�Ƃ̋���:"+dis);
        
        Creature creature=new Bear(new Point(xx, yy));
        creatures.add(creature);           
        field[yy][xx] = creature;
        break;
		}/////////////////////////////////////////////////////////////////////////////////////////////////
		while(true) {
    		int xx = (int) (mapWidth * Math.random());
            int yy = (int) (mapHeight * Math.random());

            if (field[yy][xx] != null||honeyfield[yy][xx] !=null) {continue;}
          
            Sword sword = new Sword(new Point(xx, yy));
            swords.add(sword);
            honeyfield[yy][xx] = sword;
    		break;
    	}
	}

	private void getSword(Avatar avatar) {
		Sword sword = (Sword) honeyfield[avatar.getPositionY()][avatar.getPositionX()];      
        honeyfield[avatar.getPositionY()][avatar.getPositionX()] = null;
        swords.remove(swords.indexOf(sword));System.out.println("����������");
	}

	/**
     * �^�[�Q�b�g���͂��݂̏�Ɉړ������ꍇ�̏���
     */
    private void getHoney(Avatar avatar) {
        Honey honey = (Honey) honeyfield[avatar.getPositionY()][avatar.getPositionX()];      
        honeyfield[avatar.getPositionY()][avatar.getPositionX()] = null;
        honeys.remove(honeys.indexOf(honey));System.out.println("�I����������");
    }
    
    

	/**
     * �^�[�Q�b�g�̃}�b�v�ړ���ɂ�����̂�Ԃ���
     *
     * @return Avatar
     */
    private Avatar nextAvatar(Avatar avatar, int key) {///after move nanigaaru
        int newValue;
        switch (key) {
            case 6:
                newValue = avatar.position.x + 1;
                if (newValue < mapWidth) {
                    return field[avatar.position.y][newValue];
                }
                break;

            case 4:
                newValue = avatar.position.x - 1;
                if (0 <= newValue) {
                    return field[avatar.position.y][newValue];
                }
                break;

            case 8:
                newValue = avatar.position.y - 1;
                if (0 <= newValue) {
                    return field[newValue][avatar.position.x];
                }
                break;

            case 2:
                newValue = avatar.position.y + 1;
                if (newValue < mapHeight) {
                    return field[newValue][avatar.position.x];
                }
                break;
        }

        // ��ʃT�C�Y�𒴂��Ă����ꍇ�ɋ󔒂�Ԃ����Ƃňړ����\�ɂ���
        return null;
    }

    public boolean checkDanger() {
  		for(int i=-2; i<=2; i++) {
  			for(int j=-2; j<=2; j++) {
  				int x = hornet.position.x + j;
  				int y = hornet.position.y + i;
  				try {
  					
  					if(field[y][x] instanceof Bear) {
  						return true;
  					}
  				}catch(ArrayIndexOutOfBoundsException e) {continue;}
  			}}
  		return false;
  	}
  
    

	public void gameover() {
		GraphicsContext go = getGraphicsContext2D();
 		//�w�i
		go.setFill(Color.WHITE);	
		go.fillRect(0, 0, this.getWidth(), this.getHeight());
		//���̕���
		go.setFont(Font.font("serif",50));
		go.setFill(Color.RED);
		go.fillText("GAMEOVER",300,300); 
		go.setFill(Color.BLACK);
		go.fillText("��"+this.score+"�l���ł��Ă��܂���", 150,350);
	}

	public void gameEnd() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		GraphicsContext ge = getGraphicsContext2D();
 		//�w�i
		ge.setFill(Color.WHITE);	
		ge.fillRect(0, 0, this.getWidth(), this.getHeight());
		//���̕���
		ge.setFill(Color.BLACK);
		ge.setFont(Font.font("serif",50));
		ge.fillText("GAMECLEAR",300,300);
		ge.fillText("���߂łƂ��������܂�", 200,350);
	}
	
	////////////////////////////�X�^�[�g���
	public void startscreenpaint() {
		GraphicsContext st = getGraphicsContext2D();
		//�w�i
		st.setFill(Color.WHITE);	
		st.fillRect(0, 0, this.getWidth(), this.getHeight());
		//���̕���
		st.setFill(Color.BLACK);
		st.setFont(Font.font("serif",50));
		st.fillText("�󂳂����Q�[��",250,100); 
		st.setFont(Font.font("serif",20));
		st.fillText("�}�b�v�ɏo�Ă���10�̕���@�� �� �� �� �L�[���g�������X�^�[���瓦���Ȃ���l������",10,300); 
		st.fillText("Play�{�^�����N���b�N!!",300,450); 
		}
	
	private void moveAnimals(List<Creature> animals) {//////////////////////////////////�G�̓��������肷��//�G�ɂ�0�`3�܂Ŋ���U���Ă�  	 
		for(int i=0;i<animals.size();i++) {moveEnemy(animals,i);}
    }
	private void moveEnemy(List<Creature> animals,int No) {
		 //player��enemy�̋����������擾����
		int xDistance=animals.get(No).getPositionX()-hornet.getPositionX();
		int yDistance=animals.get(No).getPositionY()-hornet.getPositionY();
		//player��enemy�ɋ���������ꍇ
		  if(xDistance == 0){
		    //x���W�������ꍇy���W�݈̂ړ�
			  if(animals.get(No).getPositionY()-hornet.getPositionY()<0)	move(animals.get(No),2);
			  else                                                 		move(animals.get(No),8);
		  }else if(yDistance == 0){
		    //y���W�������ꍇx���W�݈̂ړ�
			  if(animals.get(No).getPositionX()-hornet.getPositionX()<0)move(animals.get(No),6);
			  else                                                     move(animals.get(No),4);
		  }else{
		    //�ǂ������������ꍇ
		   int x= Math.abs(xDistance); int y= Math.abs(yDistance);
		    if(y<x){
		    	if(animals.get(No).getPositionY()-hornet.getPositionY()<0)	move(animals.get(No),2);
		    	else                                                 		move(animals.get(No),8);
		    }else{
		    	if(animals.get(No).getPositionX()-hornet.getPositionX()<0)move(animals.get(No),6);
				  else                                                     move(animals.get(No),4);       
		    }
		  }
	}

	
}
