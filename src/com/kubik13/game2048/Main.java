package com.kubik13.game2048;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        Controller controller = new Controller(model);
        JFrame game2048 = new JFrame();

        game2048.setTitle("2048");
        game2048.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game2048.setSize(450, 550);
        game2048.setResizable(false);

        game2048.add(controller.getView());

        game2048.setLocationRelativeTo(null);
        game2048.setVisible(true);



    }
}
