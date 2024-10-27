// Объявляем фигуру, наследуем от абстрактного класса ChessPiece
public class Pawn extends ChessPiece {
    // Получаем в конструктор параметр color и передаём его в конструктор родительского класса ChessPiece
    public Pawn(String color) {
        super(color);
    }

    @Override
    public String getSymbol() {
        return "P";
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

        // Определяем направление движения пешки в зависимости от цвета
        // Белые пешки движутся вверх (увеличиваем номер строки)
        // Черные пешки движутся вниз (уменьшаем номер строки)
        int direction = this.getColor().equals("White") ? 1 : -1;
        // Определяем начальную строку для пешки (ход на две клетки)
        // Белые пешки начинают с строки 1, черные с строки 6
        int startLine = this.getColor().equals("White") ? 1 : 6;

        // Пешка двигается по тому же столбцу
        if (column == toColumn) {
            // На одну клетку вперёд
            if (line + direction == toLine) {
                return board.board[toLine][toColumn] == null;
            }
            // На две клетки вперёд с начальной позиции
            if (line == startLine && line + 2 * direction == toLine) {
                return board.board[line + direction][toColumn] == null &&
                        board.board[toLine][toColumn] == null;
            }
        }
        // Пешка бьёт по диагонали
        else if (Math.abs(column - toColumn) == 1 && line + direction == toLine) {
            // Проверяем, что на целевой позиции находится фигура противника
            return isOpponentPiece(board, toLine, toColumn);
        }

        // Если ни одно из условий не выполнено, ход невозможен
        return false;
    }
}