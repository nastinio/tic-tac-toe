package ru.nastinio;


public class Main {

    public static void main(String[] args) {

        GameMapWindow gameMapWindow = new GameMapWindow();
        gameMapWindow.drawWelcomePanel();

        //Вспомогательные штуки для тестирования работы методов на расчет потенциалов
        /*GameLogic gl = new GameLogic();
        int FIELD_SIZE = 5;
        int COUNT_WIN = 5;
        char DOT_EMPTY = ' ';
        char DOT_X = 'X';
        char DOT_O = 'O';

        char[][] map = new char[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                map[i][j] = DOT_EMPTY;
            }
        }
//        map[2][2] = DOT_O;
//        map[3][2] = DOT_O;
//        map[4][2] = DOT_O;
//
//        map[4][2] = DOT_O;
//        map[4][3] = DOT_O;
//        map[4][4] = DOT_O;
        gl.printMap(map);

        CalculationPotentials calc = new CalculationPotentials(map, COUNT_WIN);

        int[][] mapWinNextTurn = calc.calcPotentialCurrentTurn(DOT_O);
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                System.out.printf("%2d", mapWinNextTurn[i][j]);
            }
            System.out.println();
        }

        int[] indexMaxElements = gl.getIndexOfMaxElement(mapWinNextTurn);
        int y = indexMaxElements[0];
        int x = indexMaxElements[1];
        System.out.printf("Компьютер пойдет на [%d][%d]\n", y + 1, x + 1);

        System.out.println(calc.isWinNextTurn(DOT_O));
        System.out.println(calc.getIndexWinTurn(DOT_O)[0] + " " + calc.getIndexWinTurn(DOT_O)[1]);*/

    }

}
