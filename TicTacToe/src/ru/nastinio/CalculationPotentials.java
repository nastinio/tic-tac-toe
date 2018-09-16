package ru.nastinio;

public class CalculationPotentials {
    //Вспомогательный класс для умного хода компьютера

    private char[][] map;

    private int[][] mapPotential;                   //Для поиска полезного в плане выигрыша хода
    private int[][] mapCheck;                       //Отметки для рекурсии, проходили ли уже по ячейке

    public int[][] mapPreparatoryPotential;         //Отмечаем поля вокруг ячейки единицами и диагональные двойками
    private int[][] mapStreakPotential;             //Потенциалы по линиям

    private int[][] mapWinNextTurn;                 //Проставляем единицы, если ход ячейку дает выигрышную линию

    //Количество связных ячеек подряд
    private int GLOBAL_COUNT = 0;                   //Счетчик связных ячеек
    private int superPotential;

    //Направления для рекурсии
    //(int, чтобы не возиться с методом equals и использовать просто знак равенства)
    private final int VERTICAL_UP = 1;
    private final int VERTICAL_DOWN = 2;

    private final int HORIZONTAL_RIGHT = 3;
    private final int HORIZONTAL_LEFT = 4;

    private final int DIAGONAL_LEFT_UP = 5;
    private final int DIAGONAL_LEFT_DOWN = 6;

    private final int DIAGONAL_RIGHT_UP = 7;
    private final int DIAGONAL_RIGHT_DOWN = 8;

    //Стартовые переменные для рекурсии
    private int startY;
    private int startX;

    private char DOT_EMPTY;
    private int COUNT_WIN;

    CalculationPotentials(char[][] map, int countWin, char dotEmpty) {
        this.map = map;
        COUNT_WIN = countWin;
        superPotential = map.length * 2;
        DOT_EMPTY = dotEmpty;

        //Создаем матрицы потенциалов и проверки на проход рекурсии
        mapCheck = new int[map.length][map.length];
        initNullMapCheck();

        mapPreparatoryPotential = new int[map.length][map.length];
        initNullMapPreparatoryPotential();

        mapPotential = new int[map.length][map.length];
        initNullMapPotential();

        mapStreakPotential = new int[map.length][map.length];
        initNullMapStreakPotential();

        mapWinNextTurn = new int[map.length][map.length];
        initNullMapWinNextTurn();

    }

    public int[][] calcPotentialCurrentTurn(char symbol) {
        //Поиск наиболее полезной в плане выигрыша ячейки

        //Проходим по всем ячейкам подготовительной матрицы, которые !=0 и !=-1
        //Проставляем количество направлений, в которых потенциально можем получить выигрышную линию

        initNullMapPreparatoryPotential();
        setMapPreparatoryPotential(symbol);

        mapStreakPotential = mapPotential;
        mapPotential = mapPreparatoryPotential;

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (map[y][x] == DOT_EMPTY && mapPreparatoryPotential[y][x] >= 0) {
                    startY = y;
                    startX = x;
                    //System.out.printf("Стартовая точка: y=%d, x=%d.\n", y, x);

                    //На каждом проходе обнуляем матрицу проверки
                    //Вертикаль
                    initNullMapCheck();
                    calcCellPotentialCurrentTurn(startY - 1, startX, symbol, VERTICAL_UP);
                    initNullMapCheck();
                    calcCellPotentialCurrentTurn(startY + 1, startX, symbol, VERTICAL_DOWN);

                    //Прошли рекурсивно по вертикали и получили количество связных ячеек заданного символа и пустых ячеек
                    //Если их колиства достаточно для потенциальной победы, записываем единицу в mapPotential
                    //В mapStreakPotential записываем количество связных и пустых ячеек по направлению
                    //(Можно заменить на одну переменную и не таскать матрицу)

                    //Аналогично по всем направлениям

                    mapStreakPotential[y][x]++; //т.к. сама ячейка не учитывается
                    if (mapStreakPotential[y][x] >= COUNT_WIN) {
                        mapPotential[y][x]++;
                    }
                    mapStreakPotential[y][x] = 0;

                    //Горизонталь
                    initNullMapCheck();
                    calcCellPotentialCurrentTurn(startY, startX + 1, symbol, HORIZONTAL_RIGHT);
                    initNullMapCheck();
                    calcCellPotentialCurrentTurn(startY, startX - 1, symbol, HORIZONTAL_LEFT);

                    mapStreakPotential[y][x]++;
                    if (mapStreakPotential[y][x] >= COUNT_WIN) {
                        mapPotential[y][x]++;
                    }
                    mapStreakPotential[y][x] = 0;

                    //Левая диагональ
                    initNullMapCheck();
                    calcCellPotentialCurrentTurn(startY - 1, startX - 1, symbol, DIAGONAL_LEFT_UP);
                    initNullMapCheck();
                    calcCellPotentialCurrentTurn(startY + 1, startX + 1, symbol, DIAGONAL_LEFT_DOWN);

                    mapStreakPotential[y][x]++;
                    if (mapStreakPotential[y][x] >= COUNT_WIN) {
                        mapPotential[y][x]++;
                    }
                    mapStreakPotential[y][x] = 0;

                    //Правая диагональ
                    initNullMapCheck();
                    calcCellPotentialCurrentTurn(startY - 1, startX + 1, symbol, DIAGONAL_RIGHT_UP);
                    initNullMapCheck();
                    calcCellPotentialCurrentTurn(startY + 1, startX - 1, symbol, DIAGONAL_RIGHT_DOWN);

                    mapStreakPotential[y][x]++;
                    if (mapStreakPotential[y][x] >= COUNT_WIN) {
                        mapPotential[y][x]++;
                    }
                    mapStreakPotential[y][x] = 0;
                }
            }
        }

        return mapPotential;

    }

    public void calcCellPotentialCurrentTurn(int y, int x, char symbol, int direction) {
        //Рекурсивно проходим по полю и просчитываем длину потенциальной линии
        //Если в одном из направлений можно собрать выигрышную линию, т.е. достаточно свободных
        //ячеек и нет символов противника, ставим 1

        //На вход отправляем не саму стартовую точку, а следующую в очереди на проверку
        if (x >= 0 && x < map.length && y >= 0 && y < map.length &&
                mapCheck[y][x] == 0 &&
                (map[y][x] == symbol || map[y][x] == DOT_EMPTY)) {
            mapCheck[y][x] = 1;
            GLOBAL_COUNT++;

            switch (direction) {
                case VERTICAL_UP:
                    calcCellPotentialCurrentTurn(y - 1, x, symbol, VERTICAL_UP);
                    break;
                case VERTICAL_DOWN:
                    calcCellPotentialCurrentTurn(y + 1, x, symbol, VERTICAL_DOWN);
                    break;

                case HORIZONTAL_LEFT:
                    calcCellPotentialCurrentTurn(y, x - 1, symbol, HORIZONTAL_LEFT);
                    break;
                case HORIZONTAL_RIGHT:
                    calcCellPotentialCurrentTurn(y, x + 1, symbol, HORIZONTAL_RIGHT);
                    break;

                case DIAGONAL_LEFT_UP:
                    calcCellPotentialCurrentTurn(y - 1, x - 1, symbol, DIAGONAL_LEFT_UP);
                    break;
                case DIAGONAL_LEFT_DOWN:
                    calcCellPotentialCurrentTurn(y + 1, x + 1, symbol, DIAGONAL_LEFT_DOWN);
                    break;

                case DIAGONAL_RIGHT_UP:
                    calcCellPotentialCurrentTurn(y - 1, x + 1, symbol, DIAGONAL_RIGHT_UP);
                    break;
                case DIAGONAL_RIGHT_DOWN:
                    calcCellPotentialCurrentTurn(y + 1, x - 1, symbol, DIAGONAL_RIGHT_DOWN);
                    break;


            }
        } else {
            //System.out.println("Закончили проход GC="+GLOBAL_COUNT);
            mapStreakPotential[startY][startX] += GLOBAL_COUNT;
            GLOBAL_COUNT = 0;
        }
    }

    public int[][] calcPotentialNextWin(char symbol) {
        //Для поиска ячейки, ход в которую, принесет победную линию
        //Метод аналогичен calcPotentialCurrentTurn, но учитывает только 'свои' (переданные) символы

        initNullMapWinNextTurn();

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (map[y][x] == DOT_EMPTY) {
                    startY = y;
                    startX = x;
                    //System.out.printf("Стартовая точка: y=%d, x=%d.\n", y, x);

                    //На каждом проходе обнуляем матрицу проверки
                    //Вертикаль
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY - 1, startX, symbol, VERTICAL_UP);
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY + 1, startX, symbol, VERTICAL_DOWN);

                    mapWinNextTurn[y][x]++; //т.к. сама ячейка не учитывается
                    if (mapWinNextTurn[y][x] >= COUNT_WIN) {
                        //System.out.println("Нашли потенциально выигрышную позицию");
                        //System.out.println("mapWinNextTurn[y][x] = "+mapWinNextTurn[y][x]);
                        mapWinNextTurn[y][x] = 1;
                        return mapWinNextTurn;
                    } else {
                        mapWinNextTurn[y][x] = 0;
                    }

                    //Горизонталь
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY, startX + 1, symbol, HORIZONTAL_RIGHT);
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY, startX - 1, symbol, HORIZONTAL_LEFT);

                    mapWinNextTurn[y][x]++; //т.к. сама ячейка не учитывается
                    if (mapWinNextTurn[y][x] >= COUNT_WIN) {
                        //System.out.println("Нашли потенциально выигрышную позицию");
                        //System.out.println("mapWinNextTurn[y][x] = "+mapWinNextTurn[y][x]);
                        mapWinNextTurn[y][x] = 1;
                        return mapWinNextTurn;
                    } else {
                        mapWinNextTurn[y][x] = 0;
                    }

                    //Левая диагональ
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY - 1, startX - 1, symbol, DIAGONAL_LEFT_UP);
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY + 1, startX + 1, symbol, DIAGONAL_LEFT_DOWN);

                    mapWinNextTurn[y][x]++; //т.к. сама ячейка не учитывается
                    if (mapWinNextTurn[y][x] >= COUNT_WIN) {
                        //System.out.println("Нашли потенциально выигрышную позицию");
                        //System.out.println("mapWinNextTurn[y][x] = "+mapWinNextTurn[y][x]);
                        mapWinNextTurn[y][x] = 1;
                        return mapWinNextTurn;
                    } else {
                        mapWinNextTurn[y][x] = 0;
                    }

                    //Правая диагональ
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY - 1, startX + 1, symbol, DIAGONAL_RIGHT_UP);
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY + 1, startX - 1, symbol, DIAGONAL_RIGHT_DOWN);

                    mapWinNextTurn[y][x]++; //т.к. сама ячейка не учитывается
                    if (mapWinNextTurn[y][x] >= COUNT_WIN) {
                        //System.out.println("Нашли потенциально выигрышную позицию");
                        //System.out.println("mapWinNextTurn[y][x] = "+mapWinNextTurn[y][x]);
                        mapWinNextTurn[y][x] = 1;
                        return mapWinNextTurn;
                    } else {
                        mapWinNextTurn[y][x] = 0;
                    }
                }
            }
        }

        return mapWinNextTurn;

    }

    public void calcCellPotentialNextTurnWin(int y, int x, char symbol, int direction) {
        //На вход отправляем не саму стартовую точку, а следующую в очереди на проверку
        if (x >= 0 && x < map.length && y >= 0 && y < map.length &&
                mapCheck[y][x] == 0 &&
                map[y][x] == symbol) {
            mapCheck[y][x] = 1;
            GLOBAL_COUNT++;
            //System.out.println("GLOBAL_COUNT_UP=" + GLOBAL_COUNT);

            switch (direction) {
                case VERTICAL_UP:
                    calcCellPotentialNextTurnWin(y - 1, x, symbol, VERTICAL_UP);
                    break;
                case VERTICAL_DOWN:
                    calcCellPotentialNextTurnWin(y + 1, x, symbol, VERTICAL_DOWN);
                    break;

                case HORIZONTAL_LEFT:
                    calcCellPotentialNextTurnWin(y, x - 1, symbol, HORIZONTAL_LEFT);
                    break;
                case HORIZONTAL_RIGHT:
                    calcCellPotentialNextTurnWin(y, x + 1, symbol, HORIZONTAL_RIGHT);
                    break;

                case DIAGONAL_LEFT_UP:
                    calcCellPotentialNextTurnWin(y - 1, x - 1, symbol, DIAGONAL_LEFT_UP);
                    break;
                case DIAGONAL_LEFT_DOWN:
                    calcCellPotentialNextTurnWin(y + 1, x + 1, symbol, DIAGONAL_LEFT_DOWN);
                    break;

                case DIAGONAL_RIGHT_UP:
                    calcCellPotentialNextTurnWin(y - 1, x + 1, symbol, DIAGONAL_RIGHT_UP);
                    break;
                case DIAGONAL_RIGHT_DOWN:
                    calcCellPotentialNextTurnWin(y + 1, x - 1, symbol, DIAGONAL_RIGHT_DOWN);
                    break;


            }


        } else {
            mapWinNextTurn[startY][startX] += GLOBAL_COUNT;
            GLOBAL_COUNT = 0;

        }
    }

    public boolean isWinNextTurn(char symbol) {
        //Проверяет, есть ли ходы, которые принесут победу

        int[][] mapWinNextTurn = calcPotentialNextWin(symbol);
        int n = mapWinNextTurn.length;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (mapWinNextTurn[y][x] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public int[] getIndexWinTurn(char symbol) {
        //Возвращает координаты выигрышного хода
        int[] res = {0, 0};
        int[][] mapWinNextTurn = calcPotentialNextWin(symbol);
        int n = mapWinNextTurn.length;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (mapWinNextTurn[y][x] == 1) {
                    res[0] = y;
                    res[1] = x;
                    return res;
                }
            }
        }
        return res;
    }

    //Стартовые инициализирующие матрицы методы

    private void initNullMapPotential() {
        //Инициализируем -1 ячейки, где уже стоят какие-то символы X или O
        for (int y = 0; y < mapPotential.length; y++) {
            for (int x = 0; x < mapPotential.length; x++) {
                if (map[y][x] == DOT_EMPTY) {
                    mapPotential[y][x] = 0;
                } else {
                    mapPotential[y][x] = -1;
                }

            }
        }
    }

    private void initNullMapStreakPotential() {
        //Инициализируем -1 ячейки, где уже стоят какие-то символы X или O
        for (int y = 0; y < mapStreakPotential.length; y++) {
            for (int x = 0; x < mapStreakPotential.length; x++) {
                mapStreakPotential[y][x] = 0;


            }
        }
    }

    private void initNullMapPreparatoryPotential() {
        for (int y = 0; y < mapPreparatoryPotential.length; y++) {
            for (int x = 0; x < mapPreparatoryPotential.length; x++) {
                mapPreparatoryPotential[y][x] = 0;
            }
        }
    }

    private void initNullMapCheck() {
        for (int y = 0; y < mapCheck.length; y++) {
            for (int x = 0; x < mapCheck.length; x++) {
                mapCheck[y][x] = 0;
            }
        }
    }

    private void initNullMapWinNextTurn() {
        for (int y = 0; y < mapWinNextTurn.length; y++) {
            for (int x = 0; x < mapWinNextTurn.length; x++) {
                mapWinNextTurn[y][x] = 0;
            }
        }
    }

    public void setMapPreparatoryPotential(char symbol) {
        //Проставляет единицы вокруг переданного значка
        //и дополнительные единицы по диагонали
        int n = mapPreparatoryPotential.length;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (map[y][x] != DOT_EMPTY) {
                    mapPreparatoryPotential[y][x] = -1;
                    if (map[y][x] == symbol) {
                        if ((y - 1) >= 0 && (y - 1) < n && map[y - 1][x] == DOT_EMPTY) {
                            mapPreparatoryPotential[y - 1][x] = 1;
                        }
                        if ((y + 1) >= 0 && (y + 1) < n && map[y + 1][x] == DOT_EMPTY) {
                            mapPreparatoryPotential[y + 1][x] = 1;
                        }
                        if ((x - 1) >= 0 && (x - 1) < n && map[y][x - 1] == DOT_EMPTY) {
                            mapPreparatoryPotential[y][x - 1] = 1;
                        }
                        if ((x + 1) >= 0 && (x + 1) < n && map[y][x + 1] == DOT_EMPTY) {
                            mapPreparatoryPotential[y][x + 1] = 1;
                        }
                        //Диагональные с коэффициентом 2
                        if ((y - 1) >= 0 && (y - 1) < n && (x - 1) >= 0 && (x - 1) < n && map[y - 1][x - 1] == DOT_EMPTY) {
                            mapPreparatoryPotential[y - 1][x - 1] = 2;
                        }
                        if ((y - 1) >= 0 && (y - 1) < n && (x + 1) >= 0 && (x + 1) < n && map[y - 1][x + 1] == DOT_EMPTY) {
                            mapPreparatoryPotential[y - 1][x + 1] = 2;
                        }
                        if ((y + 1) >= 0 && (y + 1) < n && (x - 1) >= 0 && (x - 1) < n && map[y + 1][x - 1] == DOT_EMPTY) {
                            mapPreparatoryPotential[y + 1][x - 1] = 2;
                        }
                        if ((y + 1) >= 0 && (y + 1) < n && (x + 1) >= 0 && (x + 1) < n && map[y + 1][x + 1] == DOT_EMPTY) {
                            mapPreparatoryPotential[y + 1][x + 1] = 2;
                        }
                    }


                }
            }
        }

    }

    //Вспомогательные методы на печать матриц

    public void printMapPreparatoryPotential() {
        System.out.println("MapPreparatoryPotential");
        int n = mapPreparatoryPotential.length;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                System.out.printf("%2d ", mapPreparatoryPotential[y][x]);
            }
            System.out.println();
        }
    }

    public void printMapCheck() {
        System.out.println("MapCheck");
        for (int i = 0; i < mapCheck.length; i++) {
            for (int j = 0; j < mapCheck.length; j++) {
                System.out.printf("%2d ", mapCheck[i][j]);
            }
            System.out.println();
        }
    }

    public void printMapPotential() {
        System.out.println("MapPotential");
        for (int i = 0; i < mapPotential.length; i++) {
            for (int j = 0; j < mapPotential.length; j++) {
                System.out.printf("%2d ", mapPotential[i][j]);
            }
            System.out.println();
        }
    }

    //Методы для расчета количества связных ячеек по каждому из направлений
    //Нигде не используюются

    public int[][] getMapVerticalPotential(char symbol) {
        initNullMapPotential();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (map[y][x] == DOT_EMPTY) {
                    startY = y;
                    startX = x;
                    //System.out.printf("Стартовая точка: y=%d, x=%d.\n", y, x);

                    //На каждом проходе обнуляем матрицу проверки
                    //Вертикаль
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY - 1, startX, symbol, VERTICAL_UP);
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY + 1, startX, symbol, VERTICAL_DOWN);
                }
            }
        }
        return mapPotential;
    }

    public int[][] getMapHorizontalPotential(char symbol) {
        initNullMapPotential();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (map[y][x] == DOT_EMPTY) {
                    startY = y;
                    startX = x;
                    //System.out.printf("Стартовая точка: y=%d, x=%d.\n", y, x);

                    //Горизонталь
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY, startX + 1, symbol, HORIZONTAL_RIGHT);
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY, startX - 1, symbol, HORIZONTAL_LEFT);
                }
            }
        }
        return mapPotential;
    }

    public int[][] getMapDiagonalLeftPotential(char symbol) {
        initNullMapPotential();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (map[y][x] == DOT_EMPTY) {
                    startY = y;
                    startX = x;
                    //System.out.printf("Стартовая точка: y=%d, x=%d.\n", y, x);

                    //Левая диагональ
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY - 1, startX - 1, symbol, DIAGONAL_LEFT_UP);
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY + 1, startX + 1, symbol, DIAGONAL_LEFT_DOWN);

                }
            }
        }
        return mapPotential;
    }

    public int[][] getMapDiagonalRightPotential(char symbol) {
        initNullMapPotential();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (map[y][x] == DOT_EMPTY) {
                    startY = y;
                    startX = x;
                    //System.out.printf("Стартовая точка: y=%d, x=%d.\n", y, x);

                    //Правая диагональ
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY - 1, startX + 1, symbol, DIAGONAL_RIGHT_UP);
                    initNullMapCheck();
                    calcCellPotentialNextTurnWin(startY + 1, startX - 1, symbol, DIAGONAL_RIGHT_DOWN);
                }
            }
        }
        return mapPotential;
    }

}
