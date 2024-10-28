package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Map extends JPanel {
    private static final int MAP_SIZE = 1500;
    private static final int VIEW_SIZE = 500;
    private static final  int MOVE_X = 50, MOVE_Y = 50;
    private int viewX = 0, viewY = 0;

    //todo : Map 생성자
    public Map() {
        setPreferredSize(new Dimension(MAP_SIZE, MAP_SIZE));
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                moveView(e.getKeyCode());
            }
        });
        setFocusable(true);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMap(g);
    }

    //todo: 사용자 이동 간격 단위
    private void drawMap(Graphics g) {
        g.setColor(Color.GRAY);
        int number = 1; // 번호 초기화
        int jailSize = 3; // 감옥 크기 (3x3 셀)
        int jailStartX = (MAP_SIZE / 2) - (jailSize * MOVE_X / 2);
        int jailStartY = (MAP_SIZE / 2) - (jailSize * MOVE_Y / 2);

        for (int i = 0; i < MAP_SIZE; i += MOVE_X) {
            for (int j = 0; j < MAP_SIZE; j += MOVE_Y) {
                // 감옥 영역을 감지하여 색상 변경
                if (i >= jailStartX && i < jailStartX + jailSize * MOVE_X &&
                        j >= jailStartY && j < jailStartY + jailSize * MOVE_Y) {
                    g.setColor(Color.RED); // 감옥 색상
                    g.fillRect(i, j, MOVE_X, MOVE_Y);
                    g.setColor(Color.BLACK); // 번호 색상
                    g.drawString("Jail", i + 5, j + 15); // 감옥 표시
                } else {
                    g.setColor(Color.GRAY); // 일반 셀 색상
                    g.drawRect(i, j, MOVE_X, MOVE_Y);
                    g.setColor(Color.BLACK); // 번호 색상
                    g.drawString(String.valueOf(number), i + 5, j + 15); // 번호 그리기
                }
                number++; // 번호 증가
            }
        }
    }



    //todo: 사용자 이동 간격만큼 프레임 이동
    private void moveView(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
                viewY = Math.max(viewY - MOVE_Y, 0);
                break;
            case KeyEvent.VK_DOWN:
                viewY = Math.min(viewY + MOVE_Y, MAP_SIZE - VIEW_SIZE);
                break;
            case KeyEvent.VK_LEFT:
                viewX = Math.max(viewX - MOVE_X, 0);
                break;
            case KeyEvent.VK_RIGHT:
                viewX = Math.min(viewX + MOVE_X, MAP_SIZE - VIEW_SIZE);
                break;
        }
        scrollRectToVisible(new Rectangle(viewX, viewY, VIEW_SIZE, VIEW_SIZE));
    }

    //todo: 사용자 프레임 출력
    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Tag Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(VIEW_SIZE, VIEW_SIZE);
        Map map = new Map();
        JScrollPane scrollPane = new JScrollPane(map);
        scrollPane.setPreferredSize(new Dimension(VIEW_SIZE, VIEW_SIZE));
        frame.add(scrollPane);
        frame.setVisible(true);
    }
}
