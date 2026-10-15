package citadels;

import citadels.Game.GameManager;

public class App {

    public App() {

        // 创建一个 game manager 的实例。然后调用 initgame 以及 startgame 方法
        GameManager gameManager = new GameManager();
        gameManager.initGame();
        gameManager.startGame();

    }

    public static void main(String[] args) {
        App app = new App();
    }

}
