public class Horse extends ChessPiece {
    public Horse(String color) {
        super(color); // конструктор родительского класса для установки цвета
    }

    // метод, который возвращает символ коня
    @Override
    public String getSymbol() {
        return "H";
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

        // Вычисляем разницу по строкам и столбцам
        int deltaLine = Math.abs(toLine - line);
        int deltaColumn = Math.abs(toColumn - column);

        // Проверяем, соответствует ли ход букве "Г"
        if ((deltaLine == 2 && deltaColumn == 1) || (deltaLine == 1 && deltaColumn == 2)) {

            // Получаем фигуру в целевой позиции
            ChessPiece destinationPiece = chessBoard.board[toLine][toColumn];

            // Если там нет фигуры или фигура противника, то ход возможен
            return destinationPiece == null || !destinationPiece.getColor().equals(this.getColor());
        }

        return false; // в остальных случаях ход невозможен
    }
}
