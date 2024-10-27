public abstract class ChessPiece {
    String color; // переменная для хранения цвета фигуры
    boolean check = true; // была ли фигура уже перемещена

    // Инициализируем цвет фигуры
    public ChessPiece(String color) {
        this.color = color;
    }

    // Метод для получения цвета фигуры
    public String getColor() {
        return color;
    }

    // Проверяем, что позиция внутри доски
    protected boolean isValidPosition(int line, int column) {
        return line >= 0 && line <= 7 && column >= 0 && column <= 7;
    }

    // Проверяем, что фигура не остаётся на том же месте
    protected boolean isSamePosition(int line, int column, int toLine, int toColumn) {
        return line == toLine && column == toColumn;
    }

    // Проверяем, что на целевой позиции находится фигура противника
    protected boolean isOpponentPiece(ChessBoard board, int toLine, int toColumn) {
        // Получаем фигуру на целевой позиции
        ChessPiece piece = board.board[toLine][toColumn];
        // Возвращаем true, если фигура существует и её цвет отличается от цвета текущей фигуры
        return piece != null && !piece.getColor().equals(this.getColor());
    }

    // Проверяем, что на целевой позиции находится своя фигура
    protected boolean isFriendlyPiece(ChessBoard board, int toLine, int toColumn) {
        // Получаем фигуру на целевой позиции
        ChessPiece piece = board.board[toLine][toColumn];
        // Возвращаем true, если фигура существует и её цвет совпадает с цветом текущей фигуры
        return piece != null && piece.getColor().equals(this.getColor());
    }

    // Абстрактный метод для проверки, может ли фигура переместиться на указанную позицию
    public abstract boolean canMoveToPosition(
            ChessBoard board, int line, int column, int toLine, int toColumn);

    // Абстрактный метод для получения символа фигуры
    public abstract String getSymbol();
}