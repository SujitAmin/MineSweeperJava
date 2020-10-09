
//Cell will need to have knowledge of whether it's a bomb, a number, or a blank.
//We could potentially subclass Cell to hold this data,
//but I'm not sure that offers us much benefit.
//We could also have an enum TYPE {BOMB, NUMBER, BLANK}
//to describe the type of cell.
//We've chosen not to do this because BLANK is really a type of
//NUMBER cell, where the number is O.
//It's sufficient to just have an iSBomb flag.
//It's okay to have made different choices here.
//These aren't the only good choices.
//Explain the choices you make and their tradeoffs with your interviewer.
//We also need to store state for whether the cell is exposed or not.
//We probably do not want to subclass Cell for ExposedCell and UnexposedCell.
//This is a bad idea because Board holds a reference to the cells,
//and we'd have to change the reference when we flip a cell.
//And then what if other objects reference the instance of Cell?

public class Cell {
    private int row;
    private int column;
    private boolean isBomb;
    private int number;
    private boolean isExposed = false;
    private boolean isGuess = false;

    public Cell(int r, int c) {
        isBomb = false;
        number = 0;
        row = r;
        column = c;
    }

    public void setRowAndColumn(int r, int c) {
        row = r;
        column = c;
    }

    public void setBomb(boolean bomb) {
        isBomb = bomb;
        number = -1;
    }
    public void incrementNumber() {
        number++;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isBomb() {
        return isBomb;
    }
    public boolean isBlank() {
        return  number == 0;
    }
    public boolean isExposed() {
        return  isExposed;
    }
    public boolean flip() {
        isExposed = true;
        return !isBomb;
    }

    public boolean isGuess() {
        return isGuess;
    }
    public  boolean toggleGuess() {
        if (!isExposed) {
            isGuess = !isGuess;
        }
        return isGuess;
    }

    @Override
    public String toString() {
        return "";
    }
    public String getSurfaceState() {
        if (isExposed) {
            return getUndersideState();
        } else if (isGuess) {
            return "B ";
        } else {
            return "? ";
        }
    }

    public String getUndersideState() {
        if (isBomb) {
            return  "* ";
        } else if (number > 0) {
            return  Integer.toString(number) + "";
        } else {
            return  " ";
        }
    }
}
