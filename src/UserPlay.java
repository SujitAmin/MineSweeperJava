public class UserPlay {
    public int row;
    private int column;
    private boolean isGuess;

    private UserPlay(int r, int c, boolean guess) {
        setRow(r);
        setColumn(c);
        isGuess = guess;
    }

    public static UserPlay fromString(String input) {
        boolean isGuess = false;
        if (input.length() > 0 && input.charAt(0) == 'B') {
            isGuess = true;
            input = input.substring(1);
        }

        if (!input.matches("\\d* \\d+")) {
            return null;
        }
        String[] parts = input.split(" ");
        try {
            int r = Integer.parseInt(parts[0]);
            int c = Integer.parseInt(parts[1]);
            return new UserPlay(r, c,isGuess);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isGuess() {
        return isGuess;
    }
    public boolean isMove() {
        return !isMove();
    }
    private void setColumn(int c) {
        column = c;
    }

    private void setRow(int r) {
        row = r;
    }
}
