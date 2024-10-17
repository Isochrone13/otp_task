public abstract class ChessPiece {
    String color; // цвет фигуры "White" или "Black"
    boolean check = true; // переменная для отслеживания первого хода (используется в рокировке)

    // Конструктор устанавливающий цвет фигуры
    public ChessPiece(String color) {
        this.color = color;
    }

    // Метод возвращающий цвет фигуры
    public String getColor() {
        return color;
    }

    // Абстрактный метод для проверки возможности хода фигуры
    public abstract boolean canMoveToPosition(
            ChessBoard chessBoard, int line, int column, int toLine, int toColumn);

    // Абстрактный метод для получения символа фигуры
    public abstract String getSymbol();
}