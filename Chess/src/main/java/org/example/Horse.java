// Объявляем фигуру, наследуем от абстрактного класса ChessPiece
public class Horse extends ChessPiece {
    // Получаем в конструктор параметр color и передаём его в конструктор родительского класса ChessPiece
    public Horse(String color) {
        super(color);
    }

    @Override
    public String getSymbol() {
        return "H";
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
        int jumpLine = Math.abs(toLine - line);
        int jumpColumn = Math.abs(toColumn - column);

        // Проверяем, соответствует ли ход коня перемещению буквой Г
        // 2 клетки по вертикали и 1 по горизонтали или 1 по вертикали и 2 по горизонтали
        if ((jumpLine == 2 && jumpColumn == 1) || (jumpLine == 1 && jumpColumn == 2)) {
            // Проверяем, что на целевой позиции нет своей фигуры
            return !isFriendlyPiece(board, toLine, toColumn);
        }

        return false;
    }
}
