package ru.nastinio;

import java.util.Random;
import java.util.Scanner;

public class GameLogic {
    private int FIELD_SIZE = 5;                 //Размер игрового поля
    private int COUNT_WIN = 3;                  //Количество символов в линии, которые нужно собрать для победы
    private int MIN_COUNT_WIN = 3;              //Минимальное значение COUNT_WIN
    private char[][] map;                       //Матрица игрового поля

    private final char DOT_EMPTY = ' ';
    private final char DOT_X = 'X';
    private final char DOT_O = 'O';

    private char DOT_USER;
    private char DOT_COMPUTER;

    private Scanner scanner = new Scanner(System.in);
    private Random random = new Random();

    private int COUNT_TURN = 0;


    public void systemSmartTurn() {
        CalculationPotentials calcPot = new CalculationPotentials(map, COUNT_WIN, DOT_EMPTY);

        if (calcPot.isWinNextTurn(DOT_COMPUTER)) {
            //Следующий ход компьютера - выигрышный
            int[] indexWinTurn = calcPot.getIndexWinTurn(DOT_COMPUTER);
            map[indexWinTurn[0]][indexWinTurn[1]] = DOT_COMPUTER;
            //System.out.printf("Компьютер пошел на [%d][%d]\n", indexWinTurn[0] + 1, indexWinTurn[1] + 1);
        } else {
            if (calcPot.isWinNextTurn(DOT_USER)) {
                //Если следующий ход человека выигрышный - блокируем
                int[] indexWinTurn = calcPot.getIndexWinTurn(DOT_USER);
                map[indexWinTurn[0]][indexWinTurn[1]] = DOT_COMPUTER;
                //System.out.printf("Компьютер пошел на [%d][%d]\n", indexWinTurn[0] + 1, indexWinTurn[1] + 1);
            } else {
                int[][] mapPotential;
                if (DOT_COMPUTER == DOT_X) {
                    //Компьютер ходит первым - попробуем выиграть
                    if (COUNT_TURN == 0) {
                        int y = map.length / 2;
                        int x = map.length / 2;
                        map[y][x] = DOT_COMPUTER;
                        //System.out.printf("Компьютер пошел на [%d][%d]\n", y + 1, x + 1);
                    } else {
                        mapPotential = calcPot.calcPotentialCurrentTurn(DOT_COMPUTER);

                        //System.out.println("Матрица потенциалов "+DOT_COMPUTER);
                        //printIntegerMap(mapPotential);

                        int[] indexMaxElements = getIndexOfMaxElement(mapPotential);
                        int y = indexMaxElements[0];
                        int x = indexMaxElements[1];
                        map[y][x] = DOT_COMPUTER;
                        //System.out.printf("Компьютер пошел на [%d][%d]\n", y + 1, x + 1);
                    }
                } else {
                    //Блокируем ходы игрока
                    mapPotential = calcPot.calcPotentialCurrentTurn(DOT_USER);

                    //System.out.println("Матрица потенциалов " + DOT_USER);
                    //printIntegerMap(mapPotential);

                    int[] indexMaxElements = getIndexOfMaxElement(mapPotential);
                    int y = indexMaxElements[0];
                    int x = indexMaxElements[1];
                    map[y][x] = DOT_COMPUTER;
                    //System.out.printf("Компьютер пошел на [%d][%d]\n", y + 1, x + 1);

                }

            }

        }

        COUNT_TURN++;


    }

    public void systemSimpleTurn() {
        //Ход компьютера, генерируемый рандомно на незанятую ячейку
        boolean exitWhile = true;
        while (exitWhile) {
            int x = random.nextInt(FIELD_SIZE);
            int y = random.nextInt(FIELD_SIZE);

            if (map[x][y] == DOT_EMPTY) {
                map[x][y] = DOT_COMPUTER;
                System.out.printf("Компьютер пошел на [%d][%d]\n", x + 1, y + 1);
                exitWhile = false;
            }
        }
    }

    public void humanTurn(int y, int x) {
        map[y][x] = DOT_USER;
        COUNT_TURN++;
    }

    public boolean isHumanTurnFirst() {
        //Розыгрыш права первого хода и присвоение значений переменным DOT_USER и DOT_COMPUTER соответственно
        //Крестики всегда ходят первыми
        //0-игрок, 1-компьютер

        Random rand = new Random();
        int coinToss = rand.nextInt(2);
        if (coinToss == 0) {
            DOT_USER = DOT_X;
            DOT_COMPUTER = DOT_O;
            //System.out.println("Поздравляю! Ваш ход первый");
            return true;
        } else {
            DOT_USER = DOT_O;
            DOT_COMPUTER = DOT_X;
            //System.out.println("Увы, компьютер ходит первым");
            return false;
        }
    }

    //Методы на определение конца игры
    //Можно дорутить имеющиеся рекурсивные и избавиться от этих

    public boolean isGameOver(char symbol) {
        if (checkWinStreak(symbol)) {
            System.out.println("Победили " + symbol);
            return true;
        }
        if (isMapFull()) {
            System.out.println("Ничья");
            return true;
        }
        return false;
    }

    public boolean isMapFull() {
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (map[i][j] == DOT_EMPTY) return false;
            }
        }
        return true;
    }

    public boolean checkLine(char symbol) {
        int countEquality = 0;
        //Пройдем по строкам
        for (int i = 0; i < FIELD_SIZE; i++) {
            countEquality = 0;
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (map[i][j] == symbol) {
                    countEquality++;
                } else {
                    countEquality = 0;
                }
                if (countEquality >= COUNT_WIN) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkColumn(char symbol) {
        int countEquality = 0;
        for (int j = 0; j < FIELD_SIZE; j++) {
            countEquality = 0;
            for (int i = 0; i < FIELD_SIZE; i++) {
                if (map[i][j] == symbol) {
                    countEquality++;
                } else {
                    countEquality = 0;
                }
                if (countEquality >= COUNT_WIN) {
                    return true;
                }
            }
            //System.out.printf("Столбец %d количество %c = %d \n", j,symbol,countEquality);
            /*if (countEquality >= COUNT_WIN) {
                return true;
            }*/
        }
        return false;
    }

    public boolean checkAllRightDiagonals(char symbol) {
        //Последовательно пройдем по всем побочным диагоналям матрицы
        int n = FIELD_SIZE;
        int countEquality = 0;
        for (int b = 0; b < 2 * n - 1; b++) {
            countEquality = 0;
            if (b < n) {
                for (int x = 0; x <= b; x++) {
                    //x - столбцы; y - строки
                    int y = b - x;
                    /*//Заполнение символами для проверки корректности
                    System.out.printf("b=%d x=%d y=b-x=%d \n",b,x,y);
                    map[y][x] = symbol;
                    System.out.printf("Прошли по [%d][%d] \n", y,x);*/
                    if (map[y][x] == symbol) {
                        countEquality++;
                        //System.out.printf("Нашли один символ %c, подряд их %d \n", symbol, countEquality);
                    } else {
                        countEquality = 0;
                    }
                    if (countEquality >= COUNT_WIN) {
                        return true;
                    }
                }
            } else { //b>=n, т.е. спустились ниже полной диагонали и нужно ограничивать проход по х
                for (int x = b - (n - 1); x < n; x++) {
                    int y = b - x;
                    /*System.out.printf("b=%d x=%d y=%d \n",b,x,y);
                    map[y][x] = symbol;
                    System.out.printf("Прошли по [%d][%d] \n", y,x);*/
                    if (map[y][x] == symbol) {
                        countEquality++;
                    } else {
                        countEquality = 0;
                    }
                    if (countEquality >= COUNT_WIN) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkAllLeftDiagonals(char symbol) {
        //Последовательно пройдем по всем главным диагоналям матрицы
        int n = FIELD_SIZE;
        int countEquality = 0;
        for (int b = -(n - 1); b < 2 * n - 1; b++) {
            countEquality = 0;
            if (b >= 0) { //Нижняя часть
                for (int x = 0; x <= (n - 1) - b; x++) {
                    //x - столбцы; y - строки
                    int y = b + x;
                    if (map[y][x] == symbol) {
                        countEquality++;
                        //System.out.printf("Нашли один символ %c, подряд их %d \n", symbol, countEquality);
                    } else {
                        countEquality = 0;
                    }
                    if (countEquality >= COUNT_WIN) {
                        return true;
                    }
                }


            } else { //b<0, т.е. поднялись выше полной диагонали
                for (int x = Math.abs(b); x < n; x++) {
                    int y = b + x;
                    if (map[y][x] == symbol) {
                        countEquality++;
                    } else {
                        countEquality = 0;
                    }
                    if (countEquality >= COUNT_WIN) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkWinStreak(char symbol) {
        //Проверяем, появилась ли выигрышная строка строкам/столбцам и всем диагоналям левым/правым
        /*System.out.println("По строкам " + checkLine(symbol));
        System.out.println("По столбцам " + checkColumn(symbol));
        System.out.println("По правым диагоналям " + checkAllRightDiagonals(symbol));
        System.out.println("По левым диагоналям " + checkAllLeftDiagonals(symbol));*/

        return (checkLine(symbol) || checkColumn(symbol) || checkAllRightDiagonals(symbol) || checkAllLeftDiagonals(symbol));
    }

    //Инициализирующие и вспомогательные методы

    public void initMap() {
        map = new char[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                map[i][j] = DOT_EMPTY;
            }
        }
    }

    public void setFieldSize(int fieldSize) {
        this.FIELD_SIZE = fieldSize;
    }

    public void setCountWin(int countWin) {
        this.COUNT_WIN = countWin;
    }

    public void resetSettingsForNewGame() {
        initMap();
        COUNT_TURN = 0;
    }

    public char[][] getGameMap() {
        return this.map;
    }

    public void setGameMap(char[][] map) {
        this.map = map;
    }

    public int getMax(int[][] matrix) {
        int max = matrix[0][0];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] > max) {
                    max = matrix[i][j];
                }
            }
        }
        return max;
    }

    private void printIntegerMap(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.printf("%2d ", matrix[i][j]);
            }
            System.out.println();
        }

    }

    public int[] getIndexOfMaxElement(int[][] matrix) {
        int y = 0;
        int x = 0;
        int max = matrix[y][x];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] > max) {
                    max = matrix[i][j];
                    y = i;
                    x = j;
                }
            }
        }
        int[] res = {y, x};
        return res;
    }

    public char getDotUser() {
        return DOT_USER;
    }

    public char getDotComputer() {
        return DOT_COMPUTER;
    }

    //Недоработанная штука для отрисовки выигрышной линии
    public int[][] getIndexWinStreak(char symbol) {
        int[] indexWinStreakY = new int[COUNT_WIN];
        int[] indexWinStreakX = new int[COUNT_WIN];

        //По строкам
        if (checkLine(symbol)) {
            int countEquality = 0;
            //Пройдем по строкам
            int k = 0;
            for (int i = 0; i < FIELD_SIZE; i++) {
                countEquality = 0;
                for (int j = 0; j < FIELD_SIZE; j++) {
                    if (map[i][j] == symbol) {
                        indexWinStreakY[k] = i;
                        indexWinStreakX[k] = j;
                        k++;
                        countEquality++;
                    } else {
                        countEquality = 0;
                        k = 0;
                        for (int h = 0; h < indexWinStreakY.length; h++) {
                            indexWinStreakX[h] = 0;
                            indexWinStreakY[h] = 0;
                        }
                    }
                }
            }
        }

        //По столбцам
        if (checkColumn(symbol)) {
            int k = 0;
            int countEquality = 0;
            for (int j = 0; j < FIELD_SIZE; j++) {
                countEquality = 0;
                for (int i = 0; i < FIELD_SIZE; i++) {
                    if (map[i][j] == symbol) {
                        indexWinStreakY[k] = i;
                        indexWinStreakX[k] = j;
                        k++;
                        countEquality++;
                    } else {
                        countEquality = 0;
                        k = 0;
                        for (int h = 0; h < indexWinStreakY.length; h++) {
                            indexWinStreakX[h] = 0;
                            indexWinStreakY[h] = 0;
                        }
                    }
                }
            }
        }

        System.out.print("Y: ");
        for (int i = 0; i < indexWinStreakY.length; i++) {
            System.out.print(indexWinStreakY[i] + " ");
        }
        System.out.println();
        System.out.print("X: ");
        for (int i = 0; i < indexWinStreakX.length; i++) {
            System.out.print(indexWinStreakX[i] + " ");
        }
        System.out.println();

        int[][] result = {indexWinStreakY, indexWinStreakX};
        return result;

    }

    //Искуственная задержка для имитации мыслительного процесса системы
    public void sleep() {
        //Делаем вид, что система обдумывает ход
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Методы для консоли
    public void systemSimpleTurnConsole() {
        boolean exitWhile = true;
        while (exitWhile) {
            int x = random.nextInt(FIELD_SIZE);
            int y = random.nextInt(FIELD_SIZE);

            if (map[x][y] == DOT_EMPTY) {
                map[x][y] = DOT_COMPUTER;
                System.out.printf("Компьютер пошел на [%d][%d]\n", x + 1, y + 1);
                exitWhile = false;
            }
        }
    }

    public boolean isCellValid(String forX, String forY) {
        /*Проверим введенные пользователем данные на корректность, т.е.:
        1)Ввод цифр
        2)Ввод координат, принадлежащих полю
        3)Ввод незанятой ячейки*/
        try {
            int x = Integer.parseInt(forX);
            int y = Integer.parseInt(forY);
            //Проверям, попадают ли координаты в поле
            if (x - 1 >= 0 && x - 1 < FIELD_SIZE && y - 1 >= 0 && y - 1 < FIELD_SIZE) {
                //Проверяем, не занята ли ячейка
                if (map[x - 1][y - 1] == DOT_EMPTY) {
                    return true;
                } else {
                    System.out.println("Ячейка занята");
                    return false;
                }
            } else return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void printMap(char[][] map) {
        int n = map.length;
        for (int i = 0; i <= n; i++) {
            System.out.printf("%d ", i);
        }
        System.out.println();
        for (int i = 0; i < n; i++) {
            System.out.printf("%d ", i + 1);
            for (int j = 0; j < n; j++) {
                System.out.printf("%c ", map[i][j]);
            }
            System.out.println();
        }
    }

    public void humanTurnConsole() {
        System.out.println("Введите координаты хода");
        String forX = scanner.next();
        String forY = scanner.next();

        //Проверим введенные координаты на корректность
        if (isCellValid(forX, forY)) {
            //Отрисуем ход игрока
            //Используем parseInt без обработки исключений, т.к. проверили данные на валидность выше
            map[Integer.parseInt(forX) - 1][Integer.parseInt(forY) - 1] = DOT_USER;
        } else {
            System.out.println("Координаты некорректны. Попробуйте еще разок");
            humanTurnConsole();
        }

    }

    /*public void gameOnConsole() {
        System.out.println("Давай сыграем в крестики-нолики ;)");
        try {
            //Получим размер поля от игрока
            System.out.println("Введи размер игрового поля. Минимум 3, иначе будет скучно");
            FIELD_SIZE = Integer.parseInt(scanner.next());
            if (FIELD_SIZE % 2 == 0) {
                FIELD_SIZE++;
                System.out.println("Четное поле нам не подходит. Пусть будет " + FIELD_SIZE);
            }
            if (FIELD_SIZE < MIN_COUNT_WIN) {
                System.out.println("Так неинтересно. Сыграй лучше 3х3 :)");
                FIELD_SIZE = MIN_COUNT_WIN;
            }

            try {
                //Получим количество совпадающих символов для победы
                System.out.printf("Введи количество символов в линии для победы. Минимум %d, иначе будет скучно\n", MIN_COUNT_WIN);
                COUNT_WIN = Integer.parseInt(scanner.next());
                if ((COUNT_WIN < FIELD_SIZE - 1 || COUNT_WIN < MIN_COUNT_WIN) && COUNT_WIN != MIN_COUNT_WIN) {
                    COUNT_WIN = MIN_COUNT_WIN;
                    System.out.printf("Так неинтересно. Пусть будет %d :)\n", COUNT_WIN);
                }
                System.out.printf("Ок, для победы нужно собрать %d символов в линии\n", COUNT_WIN);
            } catch (NumberFormatException e) {
                System.out.printf("Ну что же ты, число некорректно. Пусть будет %d\n", FIELD_SIZE - 1);
                COUNT_WIN = FIELD_SIZE - 1;
            }

            //Розыгрыш права первого хода
            isHumanTurnFirst();

            //Старт самой игры
            System.out.println("Сейчас сыграем ;)");
            initMap();
            printMap();
            gameProcess();

        } catch (NumberFormatException e) {
            System.out.println("Такую матрицу мы не состряпаем. Попробуй еще раз ввести число");
            gameOn();
        }

        //Выход на повтор игры
        System.out.println("Сыграем еще? Если да, введи: 1");
        String restartUser = scanner.next();
        if (restartUser.equals("1")) {
            gameOn();
        } else {
            System.out.println("Пока-пока!");
        }
    }*/

    /*public void gameOn() {
        isHumanTurnFirst();
        System.out.println("Сейчас сыграем ;)");
        initMap();
        printMap();
        gameProcess();
    }*/

    /*private void gameProcess() {
        //Крестики ходят первыми
        if (DOT_USER == DOT_X) {
            while (true) {
                humanTurn();
                COUNT_TURN++;
                //printMap();
                gameMapWindow.drawMap(map);
                if (isGameOver(DOT_USER)) {
                    COUNT_TURN = 0;
                    break;
                }

                //systemSimpleTurn();
                systemSmartTurn();
                COUNT_TURN++;
                //printMap();
                gameMapWindow.drawMap(map);
                if (isGameOver(DOT_COMPUTER)) {
                    COUNT_TURN = 0;
                    break;
                }
            }
        } else { //Первый ход компьютера
            while (true) {
                //systemSimpleTurn();
                systemSmartTurn();
                COUNT_TURN++;
                //printMap();
                gameMapWindow.drawMap(map);
                if (isGameOver(DOT_COMPUTER)) {
                    COUNT_TURN = 0;
                    break;
                }

                humanTurn();
                COUNT_TURN++;
                //printMap();
                gameMapWindow.drawMap(map);
                if (isGameOver(DOT_USER)) {
                    COUNT_TURN = 0;
                    break;
                }
            }

        }

    }*/


}
