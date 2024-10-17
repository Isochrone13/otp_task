public class Rook extends ChessPiece {
    public Rook(String color) {
        super(color); // конструктор родительского класса для установки цвета
    }

    // метод, который возвращает символ ладьи
    @Override
    public String getSymbol() {
        return "R";
    }

    @Override
    public String getColor() {
        return super.getColor(); // получаем цвет фигуры из родительского класса
    }

    // Вспомогательный метод для проверки, находится ли позиция на доске
    private boolean isValidPosition(int line, int column) {
        return line >= 0 && line <= 7 && column >= 0 && column <= 7;
    }

    @Override
    public boolean canMoveToPosition(
        ChessBoard chessBoard, int line, int column, int toLine, int toColumn) {

        // Проверяем, что начальная и конечная позиции находятся на доске
        if (!isValidPosition(line, column) || !isValidPosition(toLine, toColumn)) return false;

        // Проверяем, что фигура не осталась на том же месте
        if (line == toLine && column == toColumn) return false;

        // Проверяем, движемся ли по прямой
        if (line == toLine || column == toColumn) {
            if (line == toLine) {

                // Движение по горизонтали
                int deltaColumn = (toColumn > column) ? 1 : -1;
                int currentColumn = column + deltaColumn;
                while (currentColumn != toColumn) {
                    if (chessBoard.board[line][currentColumn] != null) return false;
                    currentColumn += deltaColumn;
                }
            }
            else {

                // Движение по вертикали
                int deltaLine = (toLine > line) ? 1 : -1;
                int currentLine = line + deltaLine;
                while (currentLine != toLine) {
                    if (chessBoard.board[currentLine][column] != null) return false;
                    currentLine += deltaLine;
                }
            }

            // Проверяем конечную клетку
            ChessPiece destinationPiece = chessBoard.board[toLine][toColumn];
            return destinationPiece == null || !destinationPiece.getColor().equals(this.getColor());
        }

        // В остальных случаях ход невозможен
        return false;
    }
}