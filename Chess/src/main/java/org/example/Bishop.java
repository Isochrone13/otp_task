public class Bishop extends ChessPiece {
    public Bishop(String color) {
        super(color); // конструктор родительского класса для установки цвета
    }

    // метод, который возвращает символ слона
    @Override
    public String getSymbol() {
        return "B";
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

        // Проверяем, что фигура движется ли по диагонали
        if (Math.abs(toLine - line) == Math.abs(toColumn - column)) {

            // Определяем направление движения
            int deltaLine = (toLine > line) ? 1 : -1;
            int deltaColumn = (toColumn > column) ? 1 : -1;

            // Проверяем каждую клетку по пути
            int currentLine = line + deltaLine;
            int currentColumn = column + deltaColumn;

            while (currentLine != toLine && currentColumn != toColumn) {
                if (chessBoard.board[currentLine][currentColumn] != null) return false;
                currentLine += deltaLine;
                currentColumn += deltaColumn;
            }

            // Проверяем конечную клетку
            ChessPiece destinationPiece = chessBoard.board[toLine][toColumn];
            return destinationPiece == null || !destinationPiece.getColor().equals(this.getColor());
        }

        // Если движение не по диагонали, ход невозможен
        return false;
    }
}