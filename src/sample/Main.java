package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.lang.System.exit;

public class Main extends Application {

    private static final int column = 15;
    private static final int row = 20;
    private static final int rectwidth = 40;
    private static final int rectHeight = 40;
    private static final int W = row*rectwidth;
    private static final int H = column*rectHeight;

    private static final int[] dx = {1,1,1,0,0,-1,-1,-1};
    private static final int[] dy = {1,0,-1,-1,1,1,0,-1};

    Cell grid[][]= new Cell[row][column];

    Scene scene;

    Pane root;


    int opencount;

    public class Cell extends StackPane {
        private int w,h;
        private boolean bombed, clicked;
        private Rectangle fence;
        private Text inside;

        public int getW() {
            return w;
        }

        public void setW(int w) {
            this.w = w;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }

        public boolean isBombed() {
            return bombed;
        }

        public void setBombed(boolean bombed) {
            this.bombed = bombed;
        }

        public boolean isClicked() {
            return clicked;
        }

        public void setClicked(boolean clicked) {
            this.clicked = clicked;
        }

        public Rectangle getFence() {
            return fence;
        }

        public void setFence(Rectangle fence) {
            this.fence = fence;
        }

        public Text getInside() {
            return inside;
        }

        public void setInside(Text inside) {
            this.inside = inside;
        }
    }


    public void dfs(int x, int y){
        if(grid[x][y].isClicked())return;
        grid[x][y].setClicked(true);
        Text text = grid[x][y].getInside();
        text.setVisible(true);
        grid[x][y].setInside(text);

        Rectangle rect = grid[x][y].getFence();
        rect.setFill(Color.SNOW);

        grid[x][y].setFence(rect);

        if(grid[x][y].isBombed()){
            System.out.println("You Lose");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            exit(0);
        }
        opencount++;
        if(opencount == W*H){
            System.out.println("You Win");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            exit(0);
        }
        if(text.getText()==""){
            for(int i=0;i<8;i++){
                int X = x+dx[i];
                int Y = y+dy[i];
                if(X<0 || X>=row || Y<0 || Y>=column)continue;
                if(grid[x][y].isBombed())continue;
                dfs(X,Y);
            }
        }
    }

    private Parent playGame(){
        root =new Pane();
        root.setPrefSize(W,H);

        opencount = 0;

        Random rand = new Random();

        List<Boolean> bomberman = new ArrayList<Boolean>();

        for(int i=0;i<row*column;i++){
            if(i<(row*column)/6)bomberman.add(true);
            else bomberman.add(false);
            if(i==0)continue;
            int id = rand.nextInt(i);
            Collections.swap(bomberman,i,id);
        }

        for(int x=0;x<row;x++){
            for(int y=0;y<column;y++) {
                grid[x][y] =  new Cell();
                grid[x][y].setBombed(bomberman.get(x*column+y));
                if(bomberman.get(x*column+y))opencount++;
                grid[x][y].setClicked(false);
                grid[x][y].setH(rectHeight);
                grid[x][y].setW(rectwidth);
                grid[x][y].setTranslateX(x*rectwidth);
                grid[x][y].setTranslateY(y*rectHeight);

                Rectangle rect = new Rectangle(rectwidth-2, rectHeight-2);
                rect.setStroke(Color.BLACK);
                rect.setFill(Color.LIGHTBLUE);
                grid[x][y].setFence(rect);
                root.getChildren().add(grid[x][y]);
            }
        }

        for(int x=0;x<row;x++){
            for(int y=0;y<column;y++){
                Text text = new Text();
                text.setFont(Font.font(16));
                text.setVisible(false);
                if(grid[x][y].isBombed()){
                    text.setText("B");
                    grid[x][y].setInside(text);
                    continue;
                }
                int cnt=0;
                for(int i=0;i<8;i++){
                    int X = x+dx[i];
                    int Y = y+dy[i];
                    if(X<0 || X>=row || Y<0 || Y>=column)continue;
                    if(grid[X][Y].isBombed())cnt++;
                }
                if(cnt==0)text.setText("");
                else text.setText(Integer.toString(cnt));
                grid[x][y].setInside(text);
            }
        }
        for(int x=0;x<row;x++){
            for(int y=0;y<column;y++){
                grid[x][y].getChildren().addAll(grid[x][y].getFence(), grid[x][y].getInside());
                int finalX = x;
                int finalY = y;
                grid[x][y].setOnMouseClicked(event -> dfs(finalX, finalY));

            }
        }


        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        scene = new Scene(playGame());

        primaryStage.setScene(scene);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
