public class ChessBoard {
    public ChessPiece[][] board = new ChessPiece[8][8]; // creating a field for game
    String nowPlayer;

    public ChessBoard(String nowPlayer) {
        this.nowPlayer = nowPlayer;
    }

    public String nowPlayerColor() {
        return this.nowPlayer;
    }

    public boolean moveToPosition(int startLine, int startColumn, int endLine, int endColumn) {
        if (checkPos(startLine) && checkPos(startColumn)) {

            // Проверяем, есть ли фигура в начальной позиции
            if (board[startLine][startColumn] == null) return false;

            // Проверяем, что ходит правильный игрок
            if (!nowPlayer.equals(board[startLine][startColumn].getColor())) return false;

            // Проверяем, может ли фигура пойти в указанную позицию
            if (board[startLine][startColumn].canMoveToPosition(
                    this, startLine, startColumn, endLine, endColumn)) {

                // Если в целевой позиции своя фигура, ход невозможен
                if (board[endLine][endColumn] != null &&
                        board[endLine][endColumn].getColor().equals(nowPlayer)) {
                    return false;
                }

                // Перемещаем фигуру
                board[endLine][endColumn] = board[startLine][startColumn];

                // Убираем фигуру с предыдущей позиции
                board[startLine][startColumn] = null;

                // Устанавливаем флаг, что фигура уже двигалась
                board[endLine][endColumn].check = false;

                // Передаем ход другому игроку
                this.nowPlayer = this.nowPlayerColor().equals("White") ? "Black" : "White";
                return true;
            } else return false;
        } else return false;
    }

    public void printBoard() {  //print board in console
        System.out.println("Turn " + nowPlayer);
        System.out.println();
        System.out.println("Player 2(Black)");
        System.out.println();
        System.out.println("\t0\t1\t2\t3\t4\t5\t6\t7");

        for (int i = 7; i > -1; i--) {
            System.out.print(i + "\t");
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null) {
                    System.out.print(".." + "\t");
                } else {
                    System.out.print(board[i][j].getSymbol() + board[i][j].getColor().substring(0, 1).toLowerCase() + "\t");
                }
            }
            System.out.println();
            System.out.println();
        }
        System.out.println("Player 1(White)");
    }

    public boolean checkPos(int pos) {
        return pos >= 0 && pos <= 7;
    }

    public boolean castling0() {
        int line = nowPlayer.equals("White") ? 0 : 7; // определяем строку в зависимости от игрока
        int kingColumn = 4;                           // столбец короля
        int rookColumn = 0;                           // столбец ладьи для рокировки
        ChessPiece king = board[line][kingColumn];    // получаем координаты короля
        ChessPiece rook = board[line][rookColumn];    // получаем координаты ладьи

        // Проверяем, что король и ладья на месте и не двигались
        if (king instanceof King && rook instanceof Rook &&
                king.getColor().equals(nowPlayer) && rook.getColor().equals(nowPlayer) &&
                king.check && rook.check) {

            // Проверяем, что между ними нет фигур
            for (int i = rookColumn + 1; i < kingColumn; i++) {
                if (board[line][i] != null) return false;
            }

            // Проверяем, что король не под ударом и не проходит через бьющиеся клетки
            if (!((King) king).isUnderAttack(this, line, kingColumn) &&
                !((King) king).isUnderAttack(this, line, kingColumn - 1) &&
                !((King) king).isUnderAttack(this, line, kingColumn - 2)) {

                // Перемещаем короля и ладью
                board[line][kingColumn - 2] = king;
                board[line][rookColumn + 3] = rook;
                board[line][kingColumn] = null;
                board[line][rookColumn] = null;

                // Устанавливаем флаги, что они двигались
                king.check = false;
                rook.check = false;

                // Передаем ход
                this.nowPlayer = this.nowPlayerColor().equals("White") ? "Black" : "White";
                return true;
            }
        }

        // Рокировка невозможна
        return false;
    }

    public boolean castling7() {
        int line = nowPlayer.equals("White") ? 0 : 7; // определяем строку в зависимости от игрока
        int kingColumn = 4;                           // столбец короля
        int rookColumn = 7;                           // столбец ладьи для рокировки
        ChessPiece king = board[line][kingColumn];    // получаем координаты короля
        ChessPiece rook = board[line][rookColumn];    // получаем координаты ладьи

        // Проверяем условия для рокировки
        if (king instanceof King && rook instanceof Rook &&
                king.getColor().equals(nowPlayer) && rook.getColor().equals(nowPlayer) &&
                king.check && rook.check) {

            // Проверяем, что между ними нет фигур
            for (int i = kingColumn + 1; i < rookColumn; i++) {
                if (board[line][i] != null) return false;
            }

            // Проверяем, что король не под ударом и не проходит через опасные клетки
            if (!((King) king).isUnderAttack(this, line, kingColumn) &&
                !((King) king).isUnderAttack(this, line, kingColumn + 1) &&
                !((King) king).isUnderAttack(this, line, kingColumn + 2)) {

                // Перемещаем короля и ладью
                board[line][kingColumn + 2] = king;
                board[line][rookColumn - 2] = rook;
                board[line][kingColumn] = null;
                board[line][rookColumn] = null;

                // Устанавливаем флаги
                king.check = false;
                rook.check = false;

                // Передаем ход
                this.nowPlayer = this.nowPlayerColor().equals("White") ? "Black" : "White";
                return true;
            }
        }

        // Рокировка невозможна
        return false;
    }
}