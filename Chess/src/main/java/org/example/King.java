public class King extends ChessPiece {
    public King(String color) {
        super(color); // конструктор родительского класса для установки цвета
    }

    // метод, который возвращает символ короля
    @Override
    public String getSymbol() {
        return "K";
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

        // Вычисляем разницу в координатах
        int deltaLine = Math.abs(toLine - line);
        int deltaColumn = Math.abs(toColumn - column);

        // Король может двигаться на одну клетку в любом направлении
        if ((deltaLine <= 1 && deltaColumn <= 1)) {
            ChessPiece destinationPiece = chessBoard.board[toLine][toColumn];

            // Проверяем, что конечная клетка свободна или занята фигурой противника
            if (destinationPiece == null || !destinationPiece.getColor().equals(this.color)) {

                // Проверяем, не находится ли клетка под ударом
                return !isUnderAttack(chessBoard, toLine, toColumn);
            }
        }

        // В остальных случаях ход невозможен
        return false;
    }

    // Метод для проверки, находится ли клетка под ударом
    public boolean isUnderAttack(ChessBoard board, int line, int column) {

        // Проверяем валидность позиции
        if (!isValidPosition(line, column)) return false;
        // Проходим по всем клеткам доски
        for (int i = 0; i < board.board.length; i++) {
            for (int j = 0; j < board.board[i].length; j++) {
                ChessPiece piece = board.board[i][j];

                // Если на клетке есть фигура противника
                if (piece != null && !piece.getColor().equals(this.color)) {

                    // Проверяем, может ли она атаковать данную клетку
                    if (piece.canMoveToPosition(board, i, j, line, column)) {
                        return true; // клетка под ударом
                    }
                }
            }
        }
        return false; // Клетка безопасна
    }
}