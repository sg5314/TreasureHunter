package javafx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JLabel;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FxApp extends Application implements EventHandler, Map.GameEndListener {
    private Label scoreLabel,DirectorLabel,lastoneLabel;//
    private Button playButton;//ボタン（スタート時のみ）
    private Canvas rightcv,leftcv,topcv;
    protected GraphicsContext rightgc;
    protected GraphicsContext leftgc;
    protected GraphicsContext topgc;
    private Stage stage;
    private int duration = 400; // アニメーション実行ミリ秒
    double time = 0.0; // 経過時間（秒）
    private Map map; // マップ
    private Timeline timeline; // アニメーションの動きを扱う
	private boolean isRunning=false;
	private boolean moveStartjudge=false;
	private boolean finishjudge=false;
	private Scene sc;//playボタンを押してからエンターキーを押すことで開始させるため
	private boolean gameover=false;
	private boolean gameend=false;
	private boolean lastone=false;;
	private boolean movekey=false;
	private boolean swordflag=false;
	

    public static void main(String[] args){
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
  
        //Canvas
        rightcv = new Canvas(60, 16*30);///新しいキャンバスを作成
        rightgc= rightcv.getGraphicsContext2D();
        rightgc.setFill(Color.BLACK);//////初期値は黒
        rightgc.fillRect(0, 0, 60, 16*30);
        
		leftcv = new Canvas(60, 16*30);
		leftgc= leftcv.getGraphicsContext2D();
		leftgc.setFill(Color.BLACK);//初期値は黒
		leftgc.fillRect(0, 0, 60, 16*30);
		
		
		topcv = new Canvas(30*27+120, 35.2);//細かな調整が必要
		topgc= topcv.getGraphicsContext2D();
		topgc.setFill(Color.BLACK);//初期値は黒
		topgc.fillRect(0, 0, 30*27+120, 35.2);//細かな調整が必要*/
		
        // map
        map = new Map(27, 16, 30, 4);
        map.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
        map.setGameEndListener(this);

        // button
        playButton = new Button(" Play ");
        playButton.setFont(Font.font("Serif", 24));
        playButton.setOnAction(this);

        scoreLabel = new Label("");////////////////
        scoreLabel.setTextFill(Color.YELLOW);
        scoreLabel.setFont(Font.font("Serif", 24));
        
        DirectorLabel = new Label("Enterを押してゲームスタート");////////////////
		DirectorLabel.setTextFill(Color.RED);
		DirectorLabel.setFont(Font.font("Serif", 24));
		DirectorLabel.setVisible(false);
		
		lastoneLabel = new Label("");////////////////
		lastoneLabel.setTextFill(Color.RED);
		lastoneLabel.setFont(Font.font("Serif", 24));
		lastoneLabel.setVisible(false);
        
        // フッターパネル
        FlowPane footerFlowPane = new FlowPane();
        footerFlowPane.setBackground(new Background(new BackgroundFill(Color.BLACK,null,null)));
        footerFlowPane.getChildren().add(scoreLabel);
        footerFlowPane.getChildren().add(playButton);
        footerFlowPane.getChildren().add(DirectorLabel);
        footerFlowPane.getChildren().add(lastoneLabel);
        footerFlowPane.setAlignment(Pos.CENTER);
        footerFlowPane.setHgap(50);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(map);
        borderPane.setBottom(footerFlowPane);
        borderPane.setLeft(leftcv);
        borderPane.setRight(rightcv);
        borderPane.setTop(topcv);

        // シーンの作成
        sc = new Scene(borderPane, map.getWidth()+120, map.getHeight() + 80);////////////////

        // シーンをステージに追加
        primaryStage.setScene(sc);
        
        // ステージの表示
        primaryStage.setTitle("Treasure Hunter");
        primaryStage.show();

        // 画面の初期描画
        updatescoreLabel();
        map.startscreenpaint();
        startApp();///////////////////
    }
    
	private void startApp() {
        timeline = new Timeline(new KeyFrame(
                new Duration(duration),
                new EventHandler<ActionEvent>() {
                    

					@Override
                    public void handle(ActionEvent event) {
                        map.requestFocus();
                        
                        
                        if(isRunning&&moveStartjudge) {
                        map.nextStep();
                        time += duration / 1000.0;
                        time = (int) ((1000 * time) + 0.01) / 1000.0;
                        
                        
                        if(map.checkDanger()) {////////////////////////mapクラスにある3×3マス内に敵が入るとtrueを返す
                        	rightgc= rightcv.getGraphicsContext2D();
                        	rightgc.setFill(Color.RED);
                        	rightgc.fillRect(0, 0, 60, 16*30);
                        	leftgc= leftcv.getGraphicsContext2D();
                        	leftgc.setFill(Color.RED);
                        	leftgc.fillRect(0, 0, 60, 16*30);
                        }else {
                        	rightgc= rightcv.getGraphicsContext2D();
                        	rightgc.setFill(Color.BLACK);
                        	rightgc.fillRect(0, 0, 60, 16*30);
                        	leftgc= leftcv.getGraphicsContext2D();
                        	leftgc.setFill(Color.BLACK);
                        	leftgc.fillRect(0, 0, 60, 16*30);
                        }
                         updatescoreLabel();
                         updateDirectorLabel();
                         map.paint();
                         if(finishjudge) 	{//gameoverかgameEndになった知らせ
                        	 rightgc.setFill(Color.BLACK);rightgc.fillRect(0, 0, 60, 16*30);
                        	 leftgc.setFill(Color.BLACK);leftgc.fillRect(0, 0, 60, 16*30);
                        	 if(gameover)	map.gameover();
                        	 if(gameend)	map.gameEnd();
                     	}
                        }
                    }
                }
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @Override
    public void handle(Event event) {
        if (event instanceof KeyEvent) {
            KeyEvent e = (KeyEvent) event;
            KeyCode k = e.getCode();
            
            switch(k) {
            case ENTER:
            	moveStartjudge=true;
            	DirectorLabel.setVisible(false);
            	movekey=true;
            	break;
			default:
				break;
            }
            if(movekey) {
            switch (k) {
                case UP:
                    map.moveHornet(8);
                    break;
                case DOWN:
                    map.moveHornet(2);
                    break;
                case LEFT:
                    map.moveHornet(4);
                    break;
                case RIGHT:
                    map.moveHornet(6);
                    break;
                default:
                    break;
            }}
            
        } else if (event.getTarget().equals(playButton)) {
        	sc.setOnKeyReleased(this);////////////////playボタンを押してからエンターキーを押すことで開始させるため   &  連続移動防止
        	map.paint();//スタート画面　→　　マップ表示    	
        	playButton.setVisible(false);//スタートボタンの削除
        	isRunning=true;
        	DirectorLabel.setVisible(true);
        }
        
             
    }


	@Override
    public void gameOver() {
    	moveStartjudge=false;
    	finishjudge=true;
        isRunning=false;
        gameover=true;
        timeline.pause();
        scoreLabel.setVisible(false);
        DirectorLabel.setVisible(false);
        lastoneLabel.setVisible(false);
        lastone=false;
        swordflag=false;
    }

    @Override
    public void gameEnd() {
    	moveStartjudge=false;
    	finishjudge=true;
        isRunning=false;
        gameend=true;
        timeline.pause();
        DirectorLabel.setVisible(false);
        lastoneLabel.setVisible(false);
        scoreLabel.setVisible(false);
        lastone=false;
        swordflag=false;
    }
    
    public void lastone() {  	
    	lastone=true;
    }
    
    public void swordget() {
    	swordflag=true;
    	DirectorLabel.setVisible(true);
    }
    
    public void swordrelease() {
    	swordflag=false;
    	DirectorLabel.setVisible(false);
    }

    private void updatescoreLabel() {
        String str="宝の数 "+map.getscore();
        scoreLabel.setText(str);
    }
    
    private void updateDirectorLabel() {
    	if(lastone) {
    	lastoneLabel.setText("ラスト1個！！");
    	lastoneLabel.setVisible(true);}
    	else if(swordflag) {
    	DirectorLabel.setText("剣を取得！！");
        DirectorLabel.setVisible(true);
    	}
    	
    }
       
    
}

