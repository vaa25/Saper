package saperobj.v2;

import javafx.util.Pair;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Vlasov Alexander
 * Date: 03.08.2014
 * Time: 10:55
 * To change this template use File | Settings | File Templates.
 * 0..8 - value
 * 9 - unopened
 * 10 - bomb
 * 11 - flag
 * 12 - explode
 * @author Alexander Vlasov
 */
public class PlayField {
    public static final int UNOPENED=9;
    public static final int FLAG=11;
    public static final int BOMB=10;
    public static final int EXPLODE=12;


    private int[][] field;    //0..8 - value , 10 - bomb
    private boolean[][] opened;  //false = unopened
    private boolean[][] flags;// false = not flagged
    int width, height, amount, left;

    public PlayField(int width, int height, int amount) {
        this.width = width;
        this.height = height;
        this.amount = amount;
        field=new int[width][height];
        opened =new boolean[width][height];
        flags=new boolean[width][height];
//        setMines();
    }
    public void setMines(){
        int left=amount;
        while (left>0){
            Random random=new Random();
            int x=random.nextInt(width);
            int y=random.nextInt(height);
            if (field[x][y]!=BOMB){
                field[x][y]=BOMB;
                left--;
            }

        }
        setDigits();
    }
    private boolean isBomb(int x, int y){
        return field[x][y]==BOMB;
    }
    private void setDigits(){
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (field[x][y]!=BOMB){
                    field[x][y]=0;
                    if (x > 0) if (isBomb(x-1,y))field[x][y]++;
                    if (y > 0) if (isBomb(x,y-1))field[x][y]++;
                    if (x > 0 && y > 0) if (isBomb(x-1,y-1))field[x][y]++;
                    if (x < width - 1) if (isBomb(x+1,y))field[x][y]++;
                    if (y < height - 1) if (isBomb(x,y+1))field[x][y]++;
                    if (x < width - 1 && y < height - 1) if (isBomb(x+1,y+1))field[x][y]++;
                    if (x > 0 && y < height - 1) if (isBomb(x-1,y+1))field[x][y]++;
                    if (x < width - 1 && y > 0) if (isBomb(x+1,y-1))field[x][y]++;
                }
            }
        }
    }
    public void setMine(int x, int y){
        field[x][y]=BOMB;
        setDigits();
    }
    public void click(Pair<Integer, Integer>coord){
        click(coord.getKey(),coord.getValue());
    }
    public void click(int x, int y){
        if (opened[x][y])return;
        opened[x][y]=true;
        if (field[x][y]==BOMB){
            field[x][y]=EXPLODE;
            return;
        }
        if (field[x][y]==0) {
            if (x > 0) click(x-1,y);
            if (y > 0) click(x,y-1);
            if (x > 0 && y > 0) click(x-1,y-1);
            if (x < width - 1) click(x+1,y);
            if (y < height - 1) click(x,y+1);
            if (x < width - 1 && y < height - 1) click(x+1,y+1);
            if (x > 0 && y < height - 1) click(x-1,y+1);
            if (x < width - 1 && y > 0) click(x+1,y-1);
        }
    }
    public void click2(Pair<Integer,Integer> coord){
        click2(coord.getKey(),coord.getValue());
    }
    public void click2(int x, int y){
        if (!opened[x][y]){
            flags[x][y]=!flags[x][y];
            opened[x][y]=!opened[x][y];
        }
    }

    public int get(int x, int y){
        if (opened[x][y]){
            if (flags[x][y])return FLAG;
            return field[x][y];
        }
        else return UNOPENED;
    }

    public void print(){
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y]==BOMB)System.out.print("* ");
                else
                if (field[x][y]==0)System.out.print("  ");
                else System.out.print(field[x][y]+" ");
            }
            System.out.println();

        }
        System.out.println();
    }
}
