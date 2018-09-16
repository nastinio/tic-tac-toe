package ru.nastinio;

import de.javasoft.plaf.synthetica.SyntheticaSimple2DLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameMapWindow extends JFrame {
    private GameLogic gameLogic = new GameLogic();
    private char[][] map;

    private final char DOT_EMPTY = ' ';
    private final char DOT_X = 'X';
    private final char DOT_O = 'O';

    private char DOT_USER;
    private char DOT_COMPUTER;

    private int FIELD_SIZE = 5;
    private int COUNT_WIN = 3;
    private int MIN_COUNT_WIN = 3;

    private JPanel containerForMapPanel;
    private JPanel panelMap;

    JPanel panelInitialSettings = new JPanel();
    JTextField forFieldSize;
    JTextField forCountWin;

    int[][] indexWinStreak;
    boolean isWinStreak = false;

    //Указывает, чей сейчас ход. 1 - компьютер, 0 - человек
    private String CURRENT_GAMER;
    private String computer = "computer";
    private String user = "user";


    GameMapWindow() {
            try{
                UIManager.setLookAndFeel(new SyntheticaSimple2DLookAndFeel());
                //UIManager.setLookAndFeel(new SyntheticaStandardLookAndFeel());
                //UIManager.setLookAndFeel(new NimbusLookAndFeel());
            }catch(Exception e){}

            setTitle("Крестики-нолики");
            setSize(550, 520);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            //setResizable(false);

            setVisible(true);

    }

    public void drawWelcomePanel() {
        setLayout(new BorderLayout());

        add(panelInitialSettings, BorderLayout.NORTH);
        panelInitialSettings.setLayout(new GridLayout(1, 5));

        JLabel lblForFieldSize = new JLabel("Размер поля");
        panelInitialSettings.add(lblForFieldSize);

        forFieldSize = new JTextField();
        forFieldSize.setColumns(3);
        forFieldSize.setText("3");
        panelInitialSettings.add(forFieldSize);

        JLabel lblForCountWin = new JLabel("Количество символов для победы");
        panelInitialSettings.add(lblForCountWin);

        forCountWin = new JTextField();
        forCountWin.setColumns(3);
        forCountWin.setText("3");
        panelInitialSettings.add(forCountWin);

        JButton btnStart = new JButton("Старт");
        panelInitialSettings.add(btnStart);

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //drawWelcomePanel();
                panelInitialSettings.remove(btnStart);

                JButton btnRestart = new JButton("Рестарт");
                panelInitialSettings.add(btnRestart);
                btnRestart.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        gameLogic.resetSettingsForNewGame();
                        panelInitialSettings.removeAll();
                        remove(panelInitialSettings);
                        remove(containerForMapPanel);

                        drawWelcomePanel();
                    }
                });

                startGame();

            }
        });

        repaint();
        revalidate();
    }

    public void startGame(){
        isWinStreak = false;
        try {
            //Получим размер поля от игрока
            FIELD_SIZE = Integer.parseInt(forFieldSize.getText());
            if (FIELD_SIZE < MIN_COUNT_WIN) {
                System.out.println("Так неинтересно. Сыграй лучше 3х3 :)");
                FIELD_SIZE = MIN_COUNT_WIN;
            } else {
                if (FIELD_SIZE % 2 == 0) {
                    FIELD_SIZE++;
                    System.out.println("Четное поле нам не подходит. Пусть будет " + FIELD_SIZE);
                }
            }
            //System.out.println("FIELD_SIZE=" + FIELD_SIZE);

            try {
                //Получим количество символов в строке для победы
                COUNT_WIN = Integer.parseInt(forCountWin.getText());
                if (((COUNT_WIN < FIELD_SIZE - 1 || COUNT_WIN < MIN_COUNT_WIN) && COUNT_WIN != MIN_COUNT_WIN) || COUNT_WIN > FIELD_SIZE) {
                    COUNT_WIN = MIN_COUNT_WIN;
                    System.out.printf("Так неинтересно. Пусть будет %d :)\n", COUNT_WIN);
                }
                //System.out.println("COUNT_WIN=" + COUNT_WIN);

                //Передаем полученные данные
                gameLogic.setFieldSize(FIELD_SIZE);
                gameLogic.setCountWin(COUNT_WIN);
                gameLogic.initMap();
                //map = gameLogic.getGameMap();
                //Запускаем основную логику
                containerForMapPanel = new JPanel(new BorderLayout());
                add(containerForMapPanel, BorderLayout.CENTER);

                //Определим право первого хода
                String lblWelcomeText;
                if (gameLogic.isHumanTurnFirst()) {
                    lblWelcomeText = "Судьба распорядилась, что Ваш ход первый";
                    CURRENT_GAMER = user;
                } else {
                    lblWelcomeText = "Судьба распорядилась, что первым пойдет компьютер";
                    CURRENT_GAMER = computer;
                }

                DOT_USER = gameLogic.getDotUser();
                DOT_COMPUTER = gameLogic.getDotComputer();

                JLabel lblWelcome = new JLabel(lblWelcomeText);
                containerForMapPanel.add(lblWelcome, BorderLayout.NORTH);

                //Повторяющаяся часть
                //Зацикливаем перерисовку игрового поля в зависимости от ходов
                panelMap = new JPanel();
                containerForMapPanel.add(panelMap, BorderLayout.CENTER);
                panelMap.setLayout(new GridLayout(FIELD_SIZE, FIELD_SIZE));

                //Если первый ход компьютера - отрисовываем сразу с ним
                if (CURRENT_GAMER.equals(computer)) {
                    gameLogic.systemSmartTurn();
                    //gameLogic.systemSimpleTurn();
                    CURRENT_GAMER = user;
                    drawMap(gameLogic.getGameMap());
                } else{
                    drawMap(gameLogic.getGameMap());
                }

                repaint();
                revalidate();


            } catch (NumberFormatException nfe) {
                System.out.println("Не пойдет. Введите данные в нормальном формате");
                drawWelcomePanel();
            }

        } catch (NumberFormatException nfe) {
            System.out.println("Не пойдет. Введите данные в нормальном формате");
            drawWelcomePanel();
        }

    }

    public void drawMap(char[][] map) {
        //Отрисовка игрового поля
        panelMap.removeAll();

        JButton[][] btnMap = new JButton[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                btnMap[i][j] = new ButtonTicTac(map[i][j], i, j);
                panelMap.add(btnMap[i][j]);

                //Подсветка выигрышных линий
                if(isWinStreak){
                    btnMap[i][j].setEnabled(false);

                    /*System.out.println("Появилась выигрышная линия, подсветим ее");
                    int[] indexWinStreakY = new int[COUNT_WIN];
                    int[] indexWinStreakX = new int[COUNT_WIN];

                    for(int q=0;q<COUNT_WIN;q++){
                        indexWinStreakY[q]=indexWinStreak[0][q];
                        indexWinStreakX[q]=indexWinStreak[1][q];
                    }

                    for(int a=0;a<COUNT_WIN;a++){
                        if(i==indexWinStreakY[a] && j==indexWinStreakX[a]){
                            btnMap[i][j].setBackground(Color.ORANGE);
                        }
                    }*/
                }

                //Защита от повторного нажатия пользователем кнопки
                if( (map[i][j]) != DOT_EMPTY){
                    btnMap[i][j].setEnabled(false);
                }

                btnMap[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //Дальше нужно перерисовать игровую панель
                        if (CURRENT_GAMER.equals(user)) {
                            ButtonTicTac btn = (ButtonTicTac) e.getSource();
                            gameLogic.humanTurn(btn.getYCoordinate(), btn.getXCoordinate());

                            //Проверки на конец игры
                            if (gameLogic.isGameOver(DOT_USER)){
                                System.out.println("GAME OVER!!!");
                                //Отрисовать конец игры. Заблокировать кнопки и вывести на рестарт
                                //Печать координат выигрышной строки
                                //indexWinStreak = gameLogic.getIndexWinStreak(DOT_USER);
                                isWinStreak = true;

                                drawMap(gameLogic.getGameMap());

                            }else{
                                isWinStreak = false;
                                //Сразу ход компьютера, без дополнительных нажатий
                                gameLogic.systemSmartTurn();
                                //gameLogic.systemSimpleTurn();

                                //Проверки на конец игры
                                if (gameLogic.isGameOver(DOT_COMPUTER)){
                                    drawMap(gameLogic.getGameMap());
                                    System.out.println("GAME OVER!!!");

                                    //Печать координат выигрышной строки
                                    //indexWinStreak = gameLogic.getIndexWinStreak(DOT_COMPUTER);
                                    isWinStreak = true;


                                }else{
                                    isWinStreak = false;
                                    drawMap(gameLogic.getGameMap());

                                }

                            }
                        }
                    }
                });

            }
        }

        repaint();
        revalidate();

    }


}
