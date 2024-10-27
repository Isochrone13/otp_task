// Объявляем фигуру, наследуем от абстрактного класса ChessPiece
public class Rook extends ChessPiece {
    // Получаем в конструктор параметр color и передаём его в конструктор родительского класса ChessPiece
    public Rook(String color) {
        super(color);
    }

    @Override
    public String getSymbol() {
        return "R";
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

        // Проверяем, что ладья движется по прямой
        if (line == toLine || column == toColumn) {
            // Проверяем, что путь до целевой позиции свободен от других фигур
            if (isPathClear(board, line, column, toLine, toColumn)) {
                // Проверяем, что на целевой позиции нет своей фигуры
                return !isFriendlyPiece(board, toLine, toColumn);
            }
        }
        // Если ни одно из условий не выполнено, ход невозможен
        return false;
    }

    // Проверка, что путь свободен
    private boolean isPathClear(ChessBoard board, int line, int column, int toLine, int toColumn) {
        // Вычисляем направление движения по строкам и столбцам
        int moveLine = Integer.compare(toLine, line);
        int moveColumn = Integer.compare(toColumn, column);

        // Устанавливаем текущую позицию на следующую клетку в направлении движения
        int currentLine = line + moveLine;
        int currentColumn = column + moveColumn;

        // Проходим по клеткам до целевой позиции
        while (currentLine != toLine || currentColumn != toColumn) {
            // Если на текущей клетке уже есть фигура, путь блокирован
            if (board.board[currentLine][currentColumn] != null) {
                return false;
            }
            // Переходим на следующую клетку в направлении движения
            currentLine += moveLine;
            currentColumn += moveColumn;
        }

        // Если все промежуточные клетки свободны, путь свободен
        return true;
    }
}