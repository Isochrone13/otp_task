public class Pawn extends ChessPiece {
    public Pawn(String color) {
        super(color); // конструктор родительского класса для установки цвета
    }

    // метод, который возвращает символ пешки
    @Override
    public String getSymbol() {
        return "P";
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

        // Определяем направление движения в зависимости от цвета
        int direction = this.color.equals("White") ? 1 : -1;

        // Если движемся по тому же столбцу
        if (column == toColumn) {
            // На одну клетку вперед
            if (line + direction == toLine) {
                // Проверяем, что впереди нет фигур
                return chessBoard.board[toLine][toColumn] == null;

            // На две клетки вперед с начальной позиции
            } else if (line + 2 * direction == toLine) {
                if ((this.color.equals("White") && line == 1) ||
                        (this.color.equals("Black") && line == 6)) {

                    // Проверяем, что впереди нет фигур
                    return chessBoard.board[line + direction][toColumn] == null &&
                            chessBoard.board[toLine][toColumn] == null;
                }
            }
        }
        // Если движемся по диагонали для взятия фигуры
        else if (Math.abs(column - toColumn) == 1 && line + direction == toLine) {
            ChessPiece destinationPiece = chessBoard.board[toLine][toColumn];
            // Проверяем, есть ли  целевой клетке фигура противника
            return destinationPiece != null && !destinationPiece.getColor().equals(this.color);
        }

        // В остальных случаях ход невозможен
        return false;
    }
}