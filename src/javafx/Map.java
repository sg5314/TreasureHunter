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
     * ゲーム終了判定インタフェース
     */
    public interface GameEndListener {
        void gameOver();

        void gameEnd();
        
        void lastone();
        
        void swordget();

		void swordrelease();

    }


    // マップの詳細情報
    private int mapSize; // 1マスのサイズ

    // マップのサイズ
    public final int mapWidth;
    public final int mapHeight;

    // フィールド内のキツネと木の位置を記録
    private Avatar[][] field;
    
    private Avatar[][] honeyfield;//宝箱専用マップ     剣の位置も入れている
    
    
    // キツネとクマ
    private ArrayList<Creature> creatures;

    // 木
    private ArrayList<Tree> trees;
    //private Tree obstacleTree = new Tree(null); // 外側の壁を表現するためのTree

    // はちみつ
    // 可変の配列を扱える(https://docs.oracle.com/javase/jp/8/docs/api/)
    private ArrayList<Honey> honeys;

    //ハチ
    private Hornet hornet;
    
    //剣
    private ArrayList<Sword> swords;

    //ゲーム終了イベントリスナー
    private GameEndListener listener;
    
    //宝の数
	private int score=0;

	private boolean killChanse=false;//敵を殺せる状態　初期状態＝false=殺せない     剣getしたらtrue
	
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
        
        trees = new ArrayList<Tree>();//木用のリストを初期化
        honeys = new ArrayList<Honey>();// はちみつ用のリストを初期化
        swords= new ArrayList<Sword>();// 剣用のリストを初期化
        setGamemap();////////////////////////////////////////////////////////////////
       
       
    }
    
    private void setGamemap() {
    	int x = 0;
    	try {
    		String cwd = System.getProperty("user.dir");
    		System.out.println(cwd);/////どこのディレクトリが実行されているか
    		
    		FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(new Stage());
    		//File file=new File("C:\\eclipse\\workspace\\javaFx_Game_backup\\bin\\class11/JAVAFx_GameMAP2.txt");////JAVAFx_GameMAP2.txtの読み込み
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
                        	 System.out.println("置けません");hantei=false;System.out.println(s);
                        	 break;
                    }
                }
                System.out.println(line);
                y++;
            }
            if (validField(field, y, x)&&hantei) {System.out.println("ファイルロード完了");}
             else {System.out.println("ロード失敗！");System.out.println(x+" "+y+" "+field.length+" "+field[0].length);}
            br.close();
		} catch (FileNotFoundException e) {System.out.println("ロード失敗1");} catch (IOException e) {System.out.println("ロード失敗2");}	
    	
    	//剣をセット
    	while(true) {
    		int xx = (int) (mapWidth * Math.random());
            int yy = (int) (mapHeight * Math.random());

            if (field[yy][xx] != null||honeyfield[yy][xx] !=null) {continue;}
          
            Sword sword = new Sword(new Point(xx, yy));
            swords.add(sword);
            honeyfield[yy][xx] = sword;
    		break;
    	}
    	
    	// ハチをセット
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
    	 // キツネとクマをセット
        creatures = new ArrayList<Creature>();
        setCreatures(5);
        
        settakarabako(10);/////////////////////////////////宝箱
        
        
 
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
     * Avatarを継承したものを画面に配置する
     */
    //敵の初期場所決定///////////////////////////////////////////////////////
    private void setCreatures(int count) {
    	double dis;
        for (int i = 0; i < count; i++) {
            int xx = (int) (mapWidth * Math.random());
            int yy = (int) (mapHeight * Math.random());
            dis=Math.sqrt((xx-hornet.getPositionX())*(xx-hornet.getPositionX())+(yy-hornet.getPositionY())*(yy-hornet.getPositionY()));
            
            if (field[yy][xx] != null||honeyfield[yy][xx] !=null) {i--;continue;}
            else if(dis<7){i--;continue;}
            
           System.out.println("敵"+i+"との距離:"+dis);
            
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

        // 背景を描画
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        // キツネとクマを描画
        paintCreatures(gc, creatures);

        // 木を描画
        paintTrees(gc, trees);

        // はちみつ描画
        paintHoneys(gc, honeys);
        
        //剣を描画
        paintSword(gc, swords);
        
        // ハチを描画
        paintHornet(gc,hornet);

    }
    


	private void paintSword(GraphicsContext gc, List<Sword> avatars) {
		// TODO 自動生成されたメソッド・スタブ
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
		// TODO 自動生成されたメソッド・スタブ
		gc.drawImage(hornet.image,
                hornet.getPositionX() * mapSize,
                hornet.getPositionY() * mapSize,
                mapSize,
                mapSize);
		if(killChanse) {
			gc.setFill(Color.RED);
            gc.setFont(Font.font("serif",10));
            gc.fillText("　剣"  ,
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
     * Creatureを継承したものを描画する
     */
    private void paintCreatures(GraphicsContext gc, List<Creature> creatures) {
        for (int i = 0; i < creatures.size(); i++) {
            Creature creature = creatures.get(i);
            gc.drawImage(creature.image,
                    creature.getPositionX() * mapSize,
                    creature.getPositionY() * mapSize,
                    mapSize,
                    mapSize);
            
            ///////////////////////////////////////移動体を示す番号
            /*gc.setFill(Color.RED);
            gc.setFont(Font.font("serif",10));
            gc.fillText("" + i,
                    creature.getPositionX() * mapSize,
                    creature.getPositionY() * mapSize,
                    mapSize);*/
       
        }
    }

    /**
     * Avatarを継承したものを描画する
     * はちみつ用にpaintAvatarsをオーバーロード
     * 可変配列に対応する
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
     * 次のステップを実行する
     */
    public void nextStep() {
        // キツネとクマを動かす
    	if(appearedAgainFlag) {Reappearcount++;System.out.println(Reappearcount);}
        moveAnimals(creatures);
        if (listener != null) {
        // ゲームクリアを判定する
        if(swords.size()==0&&killChanse) {listener.swordget();}
        if(swords.size()==0&&killChanse==false) {listener.swordrelease();}
        if(honeys.size() == 1) {listener.lastone();}
        if(honeys.size() == 0 && hornet.getHitpoint() > 0) {listener.gameEnd();}
        else if(hornet.getHitpoint() <= 0) { System.out.println("敵に当たった");listener.gameOver();} }// ゲームオーバー
        if(Reappearcount==10) {Reappearcount=0;appearedAgainFlag=false;System.out.println("モンスター再投入");appeareAgain();}
        }
        
    /**
     * ハチを動かす
     */
    public void moveHornet(int key) {
        move(hornet, key);///////////////////////////////////////////////////////////蜂
    }

    private void move(Creature creature, int key) {////////mover の動いた時の Action　　　　　　　蜂メイン
    	Avatar avatar = nextAvatar(creature, key);//その方向に行くと何があるか
        
    	
    	// 動物の位置を予めクリアしておく
    	field[creature.position.y][creature.position.x] = null;
    	

        if (avatar == null) {
            creature.move(key);
        }else if(creature==hornet&&avatar instanceof Bear) {//蜂がクマに行く       	死ぬ
        	if(killChanse) {
        		creature.move(key);
        		removeenemy(creature);
        		killChanse=false;//剣は使えない
        	}else {creature.move(key);creature.damage();}

        }else if (avatar instanceof Hornet) {//クマが蜂に行く	死ぬ
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
		 
        if (creature==hornet&&honeyfield[creature.position.y][creature.position.x]instanceof Honey) {//蜂の移動先に蜂蜜                                     point!!!!            
            getHoney(creature);//蜂蜜を消す
            this.score+=1;
           }
        else if(creature==hornet&&honeyfield[creature.position.y][creature.position.x]instanceof Sword) {
        	getSword(creature);
        	killChanse=true;
        }
	}

	private void removeenemy(Avatar avatar) {//メインキャラが敵に行く
		Creature creature = (Creature) field[avatar.getPositionY()][avatar.getPositionX()];      
        field[avatar.getPositionY()][avatar.getPositionX()] = null;
        creatures.remove(creatures.indexOf(creature));System.out.println("敵が消える"); 
        appearedAgainFlag=true;
	}

	private void removeenemy2(Avatar avatar) {//敵がメインキャラに行く      
        field[avatar.getPositionY()][avatar.getPositionX()] = null;
        creatures.remove(creatures.indexOf(avatar));System.out.println("敵が消える");  
        appearedAgainFlag=true;
	}

	private void appeareAgain() {
		for(;;) {
		int xx = (int) (mapWidth * Math.random());
        int yy = (int) (mapHeight * Math.random());
        double dis=Math.sqrt((xx-hornet.getPositionX())*(xx-hornet.getPositionX())+(yy-hornet.getPositionY())*(yy-hornet.getPositionY()));
        
        if (field[yy][xx] != null) {continue;}
        else if(dis<7){continue;}
        
       System.out.println("再投入の敵"+"との距離:"+dis);
        
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
        swords.remove(swords.indexOf(sword));System.out.println("剣が消える");
	}

	/**
     * ターゲットがはちみつの上に移動した場合の処理
     */
    private void getHoney(Avatar avatar) {
        Honey honey = (Honey) honeyfield[avatar.getPositionY()][avatar.getPositionX()];      
        honeyfield[avatar.getPositionY()][avatar.getPositionX()] = null;
        honeys.remove(honeys.indexOf(honey));System.out.println("蜂蜜が消える");
    }
    
    

	/**
     * ターゲットのマップ移動先にあるものを返すす
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

        // 画面サイズを超えていた場合に空白を返すことで移動を可能にする
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
 		//背景
		go.setFill(Color.WHITE);	
		go.fillRect(0, 0, this.getWidth(), this.getHeight());
		//中の文字
		go.setFont(Font.font("serif",50));
		go.setFill(Color.RED);
		go.fillText("GAMEOVER",300,300); 
		go.setFill(Color.BLACK);
		go.fillText("宝箱"+this.score+"個獲得できていました", 150,350);
	}

	public void gameEnd() {
		// TODO 自動生成されたメソッド・スタブ
		GraphicsContext ge = getGraphicsContext2D();
 		//背景
		ge.setFill(Color.WHITE);	
		ge.fillRect(0, 0, this.getWidth(), this.getHeight());
		//中の文字
		ge.setFill(Color.BLACK);
		ge.setFont(Font.font("serif",50));
		ge.fillText("GAMECLEAR",300,300);
		ge.fillText("おめでとうございます", 200,350);
	}
	
	////////////////////////////スタート画面
	public void startscreenpaint() {
		GraphicsContext st = getGraphicsContext2D();
		//背景
		st.setFill(Color.WHITE);	
		st.fillRect(0, 0, this.getWidth(), this.getHeight());
		//中の文字
		st.setFill(Color.BLACK);
		st.setFont(Font.font("serif",50));
		st.fillText("宝さがしゲーム",250,100); 
		st.setFont(Font.font("serif",20));
		st.fillText("マップに出てくる10個の宝を　↓ → ← ↑ キーを使いモンスターから逃げながら獲得する",10,300); 
		st.fillText("Playボタンをクリック!!",300,450); 
		}
	
	private void moveAnimals(List<Creature> animals) {//////////////////////////////////敵の動きを決定する//敵には0～3まで割り振られてる  	 
		for(int i=0;i<animals.size();i++) {moveEnemy(animals,i);}
    }
	private void moveEnemy(List<Creature> animals,int No) {
		 //playerとenemyの距離差分を取得する
		int xDistance=animals.get(No).getPositionX()-hornet.getPositionX();
		int yDistance=animals.get(No).getPositionY()-hornet.getPositionY();
		//playerとenemyに距離がある場合
		  if(xDistance == 0){
		    //x座標が同じ場合y座標のみ移動
			  if(animals.get(No).getPositionY()-hornet.getPositionY()<0)	move(animals.get(No),2);
			  else                                                 		move(animals.get(No),8);
		  }else if(yDistance == 0){
		    //y座標が同じ場合x座標のみ移動
			  if(animals.get(No).getPositionX()-hornet.getPositionX()<0)move(animals.get(No),6);
			  else                                                     move(animals.get(No),4);
		  }else{
		    //どちらも差がある場合
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
