package com.kubik13.game2048;

import java.util.*;

//Из возможных улучшений можно попробовать увеличить глубину анализа
// эффективности хода и проверить, сможет ли твой алгоритм набрать
// максимально возможный счет в 839,732 очков.
// добавить полностью авто игру

public class Model {
    private static final int  FIELD_WIDTH = 4;
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
    int score;
    int maxTile;
    int moves;

    private Stack<Tile[][]> previousStates = new Stack<>(); //хранит предыдущие состояния поля
    private Stack<Integer> previousScores = new Stack<>(); // хранит предыдущие состояния счета
    private boolean isSaveNeeded = true;

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public Model() {
        resetGameTiles();
        score = 0;
        maxTile = 0;
        moves = 0;
    }

    private void addTile(){
        List<Tile> emptyTiles= getEmptyTiles();
        if (emptyTiles.size() > 0) {
            emptyTiles.get((int)(Math.random()*emptyTiles.size())).value = (Math.random() < 0.9 ? 2 : 4);
        }
    }

    private List<Tile> getEmptyTiles(){
        ArrayList<Tile> result = new ArrayList<>();
        for (int i =0; i < FIELD_WIDTH; i++){
            for (int ii =0; ii <FIELD_WIDTH; ii++){
                if (gameTiles[i][ii].isEmpty()) result.add(gameTiles[i][ii]);
            }
        }
        return result;
    }

    public void resetGameTiles(){
        for (int i =0; i < FIELD_WIDTH; i++){
            for (int ii =0; ii <FIELD_WIDTH; ii++){
                gameTiles[i][ii] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private boolean compressTiles(Tile[] tiles){
        boolean res = false;
        int[] numberTiles = new int[tiles.length];
        int a = 0;
        for(int i =0; i < tiles.length; i++){
            if (tiles[i].value != 0) {
                numberTiles[a] = tiles[i].value;
                a++;
            }
        }
        for(int i =0; i < tiles.length; i++){
            if (tiles[i].value != numberTiles[i]) res = true;
            tiles[i].value = numberTiles[i];
        }
        return res;

    }
    private boolean mergeTiles(Tile[] tiles){
        boolean res = false;
        for(int i =0; i < tiles.length-1; i++){
            if (tiles[i].value == tiles[i+1].value){
                tiles[i].value = tiles[i].value * 2;
                tiles[i+1].value = 0;
                if (tiles[i].value != 0) res = true;
                score = score + tiles[i].value;

                if (tiles[i].value > maxTile)
                    maxTile = tiles[i].value;
            }
        }
        compressTiles(tiles);
        return res;
    }

    public void left(){
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean add = false;
        for (int i =0; i < gameTiles.length; i++){
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                add = true;
            }
        }
        if (add) addTile();
        isSaveNeeded = true;
    }

    private void rotateField(){ //rotate 90 degrees anti-clockwise
        int N = FIELD_WIDTH - 1;
        for (int x = 0; x < FIELD_WIDTH / 2; x++){
            for (int y = x; y < FIELD_WIDTH - x - 1; y++){
                int temp = gameTiles[x][y].value;
                gameTiles[x][y].value = gameTiles[y][N - x].value;
                gameTiles[y][N - x].value = gameTiles[N -x][N -y].value;
                gameTiles[N -x][N -y].value = gameTiles[N -y][x].value;
                gameTiles[N -y][x].value = temp;
            }
        }
    }

    public void up(){
        saveState(gameTiles);
        rotateField();
        left();
        rotateField();rotateField();rotateField();
    }

    public void right(){
        saveState(gameTiles);
        rotateField();rotateField();
        left();
        rotateField();rotateField();
    }

    public void down(){
        saveState(gameTiles);
        rotateField();rotateField();rotateField();
        left();
        rotateField();
    }

    public boolean canMove(){
        for (int i =0; i < FIELD_WIDTH; i++){
            for (int j =0; j <FIELD_WIDTH; j++){
                if (gameTiles[i][j].value == 0) return true;
                if (j + 1 < FIELD_WIDTH && gameTiles[i][j].value == gameTiles[i][j+1].value) return true;
                if (j - 1 >= 0 && gameTiles[i][j].value == gameTiles[i][j-1].value) return true;
                if (i + 1 < FIELD_WIDTH && gameTiles[i][j].value == gameTiles[i+1][j].value) return true;
                if (i - 1 >= 0 && gameTiles[i][j].value == gameTiles[i-1][j].value) return true;
            }
        }
        return false;
    }

    private void saveState(Tile[][] field){ // cоздание клона поля и помещение в стек
        Tile[][] tmp = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i =0; i < FIELD_WIDTH; i++){
            for (int ii =0; ii <FIELD_WIDTH; ii++){
                tmp[i][ii] = new Tile(field[i][ii].value);
            }
        }
        previousStates.push(tmp);
        previousScores.push(score);
        isSaveNeeded = false;
        moves++;
    }

    public void rollback(){ // востановление клона из стека
        if (!previousScores.isEmpty() && !previousStates.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
            moves--;
        }
    }

    public void randomMove(){
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0:
                left(); break;
            case 1 :
                up();break;
            case 2:
                right();break;
            case 3:
                down();break;
        }
    }

    public boolean hasBoardChanged(){
        int values = 0;
        int oldValues = 0;
        for (int i =0; i < FIELD_WIDTH; i++){
            for (int ii =0; ii <FIELD_WIDTH; ii++){
                values = values + gameTiles[i][ii].value;
                oldValues = oldValues + previousStates.peek()[i][ii].value;
            }
        }
        return values != oldValues;
    }

    public MoveEfficiency getMoveEfficiency(Move move){
        move.move();
        if (!hasBoardChanged()) {
            rollback();
            return new MoveEfficiency(-1, 0, move);
        }
        MoveEfficiency me = new MoveEfficiency(getEmptyTiles().size(),score,move);
        rollback();
        return me;
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue<>(4, Collections.reverseOrder());
        queue.offer(getMoveEfficiency(this::left));
        queue.offer(getMoveEfficiency(this::right));
        queue.offer(getMoveEfficiency(this::up));
        queue.offer(getMoveEfficiency(this::down)); // указатели на методы
        queue.poll().getMove().move();
    }



    /*public static void main(String[] args) {
        Model model = new Model();
        model.gameTiles = new Tile[4][4];
        model.gameTiles[0][0] = new Tile(1);
        model.gameTiles[0][1] = new Tile(2);
        model.gameTiles[0][2] = new Tile(3);
        model.gameTiles[0][3] = new Tile(4);
            model.gameTiles[1][0] = new Tile(5);
            model.gameTiles[1][1] = new Tile(6);
            model.gameTiles[1][2] = new Tile(7);
            model.gameTiles[1][3] = new Tile(8);
        model.gameTiles[2][0] = new Tile(9);
        model.gameTiles[2][1] = new Tile(10);
        model.gameTiles[2][2] = new Tile(11);
        model.gameTiles[2][3] = new Tile(12);
            model.gameTiles[3][0] = new Tile(13);
            model.gameTiles[3][1] = new Tile(14);
            model.gameTiles[3][2] = new Tile(15);
            model.gameTiles[3][3] = new Tile(16);

        for (int i =0; i < FIELD_WIDTH; i++){
            for (int ii =0; ii <FIELD_WIDTH; ii++){
                System.out.print(model.gameTiles[i][ii].value + "-");
            }
            System.out.println();
        }
        model.rotateField();
        //model.left();
        System.out.println();

        for (int i =0; i < FIELD_WIDTH; i++){
            for (int ii =0; ii <FIELD_WIDTH; ii++){
                System.out.print(model.gameTiles[i][ii].value + "-");
            }
            System.out.println();
        }


    }*/
}
