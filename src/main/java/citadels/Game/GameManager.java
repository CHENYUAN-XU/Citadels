package citadels.Game;

import java.io.File;
import java.util.*;
import citadels.Building.Building;
import citadels.Player.Player;
import citadels.Character.GameCharacter;

// javadoc 的写法
/**
 * 游戏管理类，负责控制游戏流程和管理游戏状态
 * 我们会在这里进行游戏初始化、游戏开始、游戏的结束判定等等
 */
public class GameManager {

    // 既然还是一个游戏的管理器，里面的应该存储的信息

    /* 游戏内的玩家列表 */
    private List<Player> players;

    /* 游戏内的角色列表 */
    // character 在 java 中是一个已经有了的类，所以我们这里需要去起一个新的名字
    private List<GameCharacter> allCharacters;

    /* Building 的建筑信息 */
    // 从文件中读取的信息
    // 做一个全局共享的
    // 也就是说任何的类 可以通过 gameManager 来获取当前的 building cards 的信息
    // 为了尽可能的满足单一职责
    public static List<Building> allBuildingsCards;

    /* 玩家和角色映射 */
    // 为了方便，如果说你想直接获取玩家的角色，你可以直接通过map 来实现
    // 否则你就需要频繁的调用 player 中的 getCharacter 方法
    // 为了满足我们的单一职责，我们也变成一个 全局共享的
    // 当你在任何位置分配角色的时候，你可以直接通过 gameManager进行赋值
    public static Map<String, Player> characterMap;

    /* scanner 扫描器，因为你需要从控制台来读取内容 */
    // 一定要尽可能的少创建 scanner ，只是用一个 scanner确保 scanner 的一致性
    private Scanner scanner;

    /* GameHelper 的 object */
    private GameHelper gameHelper;

    private int playerNumber;

    /* 当前的 round */
    // 有时候不同的 round 会执行不同的操作
    private int currentRound;

    public static boolean debug = false;

    public GameManager() {
        // 构造函数里面初始化
        this.players = new ArrayList<>();
        this.allCharacters = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        GameManager.characterMap = new HashMap<>();
        GameManager.allBuildingsCards = new ArrayList<>();
        this.gameHelper = new GameHelper();
        this.playerNumber = 0;
        this.currentRound = 0;
    }

    /**
     * 初始化游戏
     * 满足单一原则，也就是说 需要在 initgame 里面实现
     * 1. 读取 building 的建筑信息
     * 2. 加载 character 信息
     * 3. 初始化玩家的信息
     * 4. 获取玩家的角色
     * 5. 分配 building 卡牌
     * 6. 洗牌等等等
     * 尽可能把上面所有的信息拆开，然后进行初始化
     * 每一条都是一个function
     */
    public void initGame() {

        // 读取building 的建筑信息
        this.gameHelper.loadBuildingCards();

        // 询问玩家数量
        this.playerNumber = this.gameHelper.getPlayerNumber(this.scanner);

        // 创建玩家
        this.players = this.gameHelper.createPlayers(this.playerNumber);

        // 洗牌
        this.gameHelper.shuffleDeck(GameManager.allBuildingsCards);

        // 加载character 信息
        this.allCharacters = this.gameHelper.loadCharacters();

        // 分发卡牌
        this.gameHelper.dealInitialCards(this.players);

        System.out.println("You are player 1");

    }

    /**
     * 开始游戏
     * 执行游戏的住循环，包括确认皇冠的持有者、角色的选择以及角色行动
     */

    public void startGame() {

        // 游戏开始
        // 需要一个元素 来去决定游戏是否还会继续运行
        boolean gameOver = false;
        while (!gameOver) {
            // 开始一个 round
            this.currentRound++;

            // 确认皇冠的所有者
            // 分两个逻辑 第一个是 第一局的时候是随机分配
            // 第二区往后是谁是 king，谁来拿到皇冠
            this.gameHelper.assignCrowns(this.players, this.currentRound);

            // 选择角色 是从 拥有皇冠的人开始
            // 我们最好对这个进行一个排序
            // 排序在 assignCrowns 中已经完成
            this.gameHelper.chooseCharacter(this.players, this.allCharacters, this.scanner);

            // 我们需要根据 character 的角色顺序来决定谁来行动
            // 循环行动顺序来决定谁来行动 组成一个 map
            GameManager.characterMap = this.gameHelper.createActionOrder(this.players);

            // 开始行动
            this.gameHelper.startActionRound(GameManager.characterMap, this.scanner, this.allCharacters);

            // 判断游戏是否结束
            gameOver = this.gameHelper.isGameOver(this.players);
        }

        // 游戏结束
        // 需要去判断谁是胜利者
        this.gameHelper.declareWinner(this.players);

        this.scanner.close();
    }
}
