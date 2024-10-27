// Объявляем фигуру, наследуем от абстрактного класса ChessPiece
public class King extends ChessPiece {
    // Получаем в конструктор параметр color и передаём его в конструктор родительского класса ChessPiece
    public King(String color) {
        super(color);
    }

    @Override
    public String getSymbol() {
        return "K";
    }

    // Проверяем, что фигура может пойти на выбранную клетку
    @Override
    public boolean canMoveToPosition(
            ChessBoard board, int line, int column, int toLine, int toColumn) {

        // Проверяем, что позиция внутри доски
        if (!isValidPosition(line, column) || !isValidPosition(toLine, toColumn)) {
            return false;
        }

        // Проверяем, что фигура не остаётся на том же месте
        if (isSamePosition(line, column, toLine, toColumn)) {
            return false;
        }

        // Вычисляем разницу между начальной и целевой строками и столбцами
        int moveLine = Math.abs(toLine - line);
        int moveColumn = Math.abs(toColumn - column);

        // Проверяем, что король перемещается не более чем на одну клетку в любую из сторон
        if (moveLine <= 1 && moveColumn <= 1) {
            if (!isFriendlyPiece(board, toLine, toColumn)) {
                // Проверяем, не будет ли король под шахом
                return !isUnderAttack(board, toLine, toColumn);
            }
        }

        return false;
    }

    // Проверка, находится ли клетка под атакой
    public boolean isUnderAttack(ChessBoard board, int line, int column) {
        if (!isValidPosition(line, column)) {
            return false;
        }
        // Проходим по всем клеткам доски для проверки атакующих фигур
        for (int i = 0; i < board.board.length; i++) {
            for (int j = 0; j < board.board[i].length; j++) {
                ChessPiece piece = board.board[i][j]; // получаем фигуру на текущей позиции
                // Проверяем, есть ли фигура и принадлежит ли она противнику
                if (piece != null && !piece.getColor().equals(this.getColor())) {
                    // Проверяем, может ли противник атаковать клетку
                    if (piece.canMoveToPosition(board, i, j, line, column)) {
                        return true; // если хотя бы одна фигура может атаковать клетку, возвращаем true
                    }
                }
            }
        }

        // Если ни одна фигура не может атаковать клетку, возвращаем false
        return false;
    }
}