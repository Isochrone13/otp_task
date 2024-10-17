public class Queen extends ChessPiece {
    public Queen(String color) {
        super(color); // конструктор родительского класса для установки цвета
    }

    // метод, который возвращает символ королевы
    @Override
    public String getSymbol() {
        return "Q";
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

        // Королева может ходить как ладья или слон
        if (line == toLine || column == toColumn ||
                Math.abs(toLine - line) == Math.abs(toColumn - column)) {

            // Проверяем путь как у ладьи и слона
            if (line == toLine || column == toColumn) {
                // Если движение как у ладьи
                Rook tempRook = new Rook(this.color);
                return tempRook.canMoveToPosition(chessBoard, line, column, toLine, toColumn);
            }
            else {
                // Если движение как у слона
                Bishop tempBishop = new Bishop(this.color);
                return tempBishop.canMoveToPosition(chessBoard, line, column, toLine, toColumn);
            }
        }

        // В остальных случаях ход невозможен
        return false;
    }
}