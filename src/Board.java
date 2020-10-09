import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

//Board will need to have an array of all the Ce11 objects.
//A two-dimension array will work just fine
//We'll probably want Board to keep state of
//how many unexposed cells there are. We'll track this as we go,
//so we don't have to continuously count it.
//Board will also handle some of the basic algorithms:
//  • Initializing the board and laying out the bombs.
//  • Flipping a cell.
//  • Expanding blank areas.
//It will receive the game plays from the Game object and carry them out.
//It will then need to return the result of the play,
//which could be any of {clicked a bomb and lost, clicked out of bounds,
//clicked an already exposed area, clicked a blank area and still playing,
//clicked a blank area and won, clicked a number and won}.
//This is really two different items that need to be returned:
//successful (whether or not the play was successfully made) and a game state
//(won, lost, playing). We'll use an additional GamePlayResult to return this data.
//We'll also use a GamePlay class to hold the move that the player plays.
//We need to use a row, column, and then a flag to indicate whether this was an actual flip
//or the user was just marking this as a "guess" at a possible bomb.
public class Board {
    private int nRows;
    private int nColumns;
    private int nBombs = 0;
    private Cell[][] cells;
    private Cell[] bombs;
    private int numUnexposedRemaining;

    public Board(int rows, int columns, int bombs) {
        nRows = rows;
        nColumns = columns;
        nBombs = bombs;
        initializeBoard();
        shuffleBoard();
        setNumberedCells();

        numUnexposedRemaining = nRows * nColumns - nBombs;
    }

    private void initializeBoard() {
        cells = new Cell[nRows][nColumns];
        bombs = new Cell[nBombs];
        for(int r = 0 ; r < nRows; r++) {
            for(int c = 0 ; c < nColumns; c++) {
                cells[r][c] = new Cell(r,c);
            }
        }
        for(int i = 0; i < nBombs ; i++) {
            int r = (i / nColumns);
            int c = (i - r * nColumns) % nColumns;
            System.out.println("I am row Number: " + r);
            System.out.println("I am column Number: i= " + i + " r =" + r + " nColumns= " + nColumns + " c= "+ c );
            bombs[i] = cells[r][c];
            bombs[i].setBomb(true);
        }
    }
    //check for this...
    private void shuffleBoard() {
        int nCells = nRows * nColumns;
        Random random = new Random();
        for(int index1 = 0; index1 < nCells; index1++) {
            int index2 = index1 + random.nextInt(nCells - index1);
            if (index1 != index2) {
                // Get cell at index1
                int row1 = index1/nColumns;
                int column1 = (index1 - row1 * nColumns) % nColumns;
                Cell cell1 = cells[row1][column1];

                // Get cell at index2.
                int row2 = index2 / nColumns;
                int column2 = (index2 - row2 * nColumns)  % nColumns;
                Cell cell2 = cells[row2][column2];

                //Swap
                cells[row1][column1] = cell2;
                cell2.setRowAndColumn(row1, column1);
                cells[row2][column1] = cell1;
                cell1.setRowAndColumn(row2, column2);

            }
        }
    }

    private  boolean inBounds(int row, int column) {
        return  row >= 0 && row < nRows && column >= 0 && column < nColumns;
    }

    /**
     * Set the cells around the bombs to the right number. Although
     * the bombs have been shuffled, the refernce in the bombs the
     * reference in the bombs array is still to same object.
     */
    private void setNumberedCells() {
        //offsets of 8 surrounding cells
        int[][] deltas = {
                {-1, 1}, {-1, 0}, {-1,1},
                {0,-1}          , {0,1},
                {1,-1},  {1, 0},  {1,1}
        };
        for (Cell bomb : bombs) {
            System.out.println(bomb);
            int row = bomb.getRow();
            int col = bomb.getColumn();
            for (int[] delta : deltas) {
                int r = row + delta[0];
                int c = col + delta[1];
                if (inBounds(r,c)) {
                    cells[r][c].incrementNumber();
                }
            }
        }
    }

    public void printBoard(boolean showUnderSide) {
        System.out.println();
        System.out.print(" ");
        for (int i = 0; i < nColumns; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0; i < nColumns; i++) {
            System.out.print("--");
        }
        System.out.println();
        for (int r = 0; r < nRows; r++) {
            System.out.print(r + "| ");
            for (int c = 0; c < nColumns; c++) {
                if (showUnderSide) {
                    System.out.print(cells[r][c].getUndersideState());
                } else {
                    System.out.print(cells[r][c].getSurfaceState());
                }
            }
            System.out.println();
        }
    }

    public boolean flipCell(Cell cell) {
        if (!cell.isExposed() && !cell.isGuess()) {
            cell.flip();
            numUnexposedRemaining--;
            return true;
        }
        return  false;
    }

    public void expandBlank(Cell cell) {
        int[][] deltas = {
                {-1, 1}, {-1, 0}, {-1,1},
                {0,-1}          , {0,1},
                {1,-1},  {1, 0},  {1,1}
        };
        Queue<Cell> toExplore = new LinkedList<Cell>();
        toExplore.add(cell);

        while(!toExplore.isEmpty()) {
            Cell current = toExplore.remove();

            for (int[] delta : deltas) {
                int r = current.getRow() + delta[0];
                int c = current.getColumn() + delta[1];

                if (inBounds(r,c)) {
                    Cell neighbor = cells[r][c];
                    if (flipCell(neighbor) && neighbor.isBlank()) {
                        toExplore.add(neighbor);
                    }
                }
            }
        }
    }

    public UserPlayResult playFlip(UserPlay play) {
        Cell cell = getCellAtLocation(play);
        if (cell == null) {
            return  new UserPlayResult(false, Game.GameState.RUNNING);
        }
        if (play.isGuess()){
            boolean guessResult = cell.toggleGuess();
            return new UserPlayResult(guessResult, Game.GameState.RUNNING);
        }
        boolean result = flipCell(cell);
        if (cell.isBomb()) {
            return new UserPlayResult(result, Game.GameState.LOST);
        }

        if (cell.isBlank()) {
            expandBlank(cell);
        }
        if (numUnexposedRemaining == 0) {
            return new UserPlayResult(result, Game.GameState.WON);
        }
        return  new UserPlayResult(result, Game.GameState.RUNNING);
    }

    private Cell getCellAtLocation(UserPlay play) {
        int row = play.getRow();
        int col = play.getColumn();
        if(!inBounds(row, col)) {
            return null;
        }
        return cells[row][col];
    }
    public int getNumUnexposedRemaining() {
        return numUnexposedRemaining;
    }
}
