package citadels.Game;

import citadels.Game.GameManager;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.util.*;
import citadels.Building.Building;
import java.io.*;
import citadels.Character.GameCharacter;
import citadels.Player.HumanPlayer;
import citadels.Player.Player;
import citadels.Player.AIPlayer;
import citadels.Character.GameCharacterFactory;

/**
 * GameManager 的 辅助类
 * 一些方法，比如读取文件、处理一些特殊逻辑等等，我们会放到 game helper 里面
 * 确保我们的 game manager 的整洁
 */
public class GameHelper {

    private Random random;
    /* 行动顺序 */
    public static String[] actionOrder = { "Assassin", "Thief", "Magician", "King", "Bishop", "Merchant",
            "Architect", "Warlord" };

    private Player currentHumanPlayer;

    public GameHelper() {
        this.random = new Random();
        this.currentHumanPlayer = null;
    }

    /**
     * 文件读取和文件的放在了一个文件中
     */
    public void loadBuildingCards() {
        File cardsFile = null;
        Scanner fileScanner = null;
        try {
            cardsFile = new File(URLDecoder.decode(this.getClass().getResource("/citadels/cards.tsv").getPath(),
                    StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // 读取文件
        List<Building> allBuildings = new ArrayList<>();
        try {
            fileScanner = new Scanner(cardsFile);
            // 跳过第一行
            if (fileScanner.hasNextLine()) {
                fileScanner.nextLine();
            }

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                // 把每一行按照某一个字符进行风格
                String[] splitLine = line.split("\t");
                Building building = null;

                String name = splitLine[0];
                int qty = Integer.parseInt(splitLine[1]);
                String color = splitLine[2];
                int cost = Integer.parseInt(splitLine[3]);

                if (splitLine.length == 5) {
                    String description = splitLine[4];
                    building = new Building(name, qty, color, cost, description);
                } else {
                    building = new Building(name, qty, color, cost);
                }

                allBuildings.add(building);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        GameManager.allBuildingsCards = allBuildings;

        // 防止内存泄漏
        fileScanner.close();
    }

    /**
     * 获取玩家数量
     */
    public int getPlayerNumber(Scanner scanner) {
        int count = 0;
        // 我们需要去处理用户输入错误的情况
        while (true) {
            System.out.println("Enter how many players [4-7]: ");
            System.out.print("> ");

            if (scanner.hasNextInt()) {

                count = scanner.nextInt();

                // 检查范围
                if (count >= 4 && count <= 7) {
                    scanner.nextLine(); // 清空输入的缓存区
                    break; // 打断循环
                } else {
                    System.out.println("Error: Input must be between 4 and 7.");
                    continue;
                }
            } else {
                System.out.println("Error: Please enter a valid integer.");
                scanner.next(); // 清空输入的缓存区
            }
        }

        return count;
    }

    public List<Player> createPlayers(int playerNumber) {
        List<Player> players = new ArrayList<>();

        // 先创建 human player
        HumanPlayer humanPlayer = new HumanPlayer(1);
        players.add(humanPlayer);

        // 在创建 ai player
        for (int i = 1; i < playerNumber; i++) {
            AIPlayer aiPlayer = new AIPlayer(i + 1);
            players.add(aiPlayer);
        }

        return players;
    }

    public void shuffleDeck(List<Building> decks) {

        // 使用 collections 的默认方法即可
        Collections.shuffle(decks);
        System.out.println("Shuffling deck...");
    }

    public List<GameCharacter> loadCharacters() {
        List<GameCharacter> characters = GameCharacterFactory.createAllCharacters();
        return characters;
    }

    public void dealInitialCards(List<Player> players) {
        System.out.println("Dealing cards...");
        System.out.println("Starting Citadels with " + players.size() + " players...");

        // 循环每个玩家，random
        for (Player player : players) {

            List<Building> hands = new ArrayList<>();
            while (hands.size() < 4) {
                // 随机一个 index
                int randomIndex = this.random.nextInt(GameManager.allBuildingsCards.size());
                // 从所有卡牌中获取
                Building building = GameManager.allBuildingsCards.get(randomIndex);
                // 如果卡牌数量大于0，则添加到玩家手中，并减少卡牌数量
                if (building.getQty() > 0) {
                    hands.add(building);
                    building.substractQty(1);
                }
            }

            player.setHand(hands);
        }
    }

    /**
     * 分配皇冠
     * 是一个 void 方法，因为我们针对皇冠的记录，实际上是在 player 类中进行记录的
     * player.setCrown()
     */
    public void assignCrowns(List<Player> players, int round) {

        // 第一局的时候是随机分配
        if (round == 1) {
            // 随机一个玩家
            int randomIndex = this.random.nextInt(players.size());
            Player player = players.get(randomIndex);

            player.setHasCrown(true);
            System.out.println("Player " + player.getId() + " is the crowned player and goes first.");
            Collections.rotate(players, -randomIndex); // 进行排序
        } else {
            // 第二局往后谁是 king，谁有皇冠
            // 你需要去循环便利，然后呢，根据玩家手上的角色来进行判断
            // 你需要 set 一个 true 然后 set 一个 false
            // 先循环 hascrown 为 true 的角色 进行一个 remove

            // 如果没有一个 king角色，那么皇冠的位置保持不变
            boolean hasKing = false;
            for (Player player : players) {
                if (player.getCharacter().getName().equals("King")) {
                    hasKing = true;
                    break;
                }
            }

            // 在当前环节的角色中有 king 的话，我们再进行一个切换
            if (hasKing) {
                for (Player player : players) {
                    if (player.getHasCrown()) {
                        player.setHasCrown(false);
                        break;
                    }
                }

                // 再次循环所有的角色，然后呢，根据玩家手上的角色来进行判断
                for (int i = 0; i < players.size(); i++) {
                    Player player = players.get(i);
                    if (player.getCharacter().getName().equals("King")) {
                        player.setHasCrown(true);
                        System.out.println("Player " + player.getId() + " is the crowned player and goes first.");
                        Collections.rotate(players, -i); // 进行排序
                        break;
                    }
                }
            }
        }
    }

    public void chooseCharacter(List<Player> players, List<GameCharacter> character, Scanner scanner) {

        // 避免深浅拷贝的问题，防止直接操作，更改原 list
        List<GameCharacter> availableCharacters = character;
        System.out.println("Press t to process turns\n" + "================================\n"
                + "SELECTION PHASE\n" + "================================");

        while (true) {
            System.out.print("> ");
            String input = scanner.next();
            if (input.equals("t")) {
                break;
            } else {
                System.out.println("Invalid input, please enter t to start to choose character.");
            }
        }

        int playerCount = players.size();
        int faceUpCharacterCount = 0;
        int faceDownCharacterCount = 0;

        // 根据玩家的数量来决定 faceup 以及 facedown 的一些操作
        if (playerCount == 4) {
            faceUpCharacterCount = 2;
            faceDownCharacterCount = 1;
        } else if (playerCount == 5) {
            faceUpCharacterCount = 1;
            faceDownCharacterCount = 1;
        } else if (playerCount == 6 || playerCount == 7) {
            faceUpCharacterCount = 0;
            faceDownCharacterCount = 1;
        }

        // 创建两个元素，一个用于存储 faceup 的角色，一个用于存储 facedown 的角色
        List<GameCharacter> faceUpCharacters = new ArrayList<>();
        GameCharacter faceDownCharacter = null;

        for (int i = 0; i < faceUpCharacterCount; i++) {
            // 从中随机选取一个 faceup 的角色的 index
            int randomIndex = this.random.nextInt(availableCharacters.size());
            // 根据 index 获取到 faceup 的角色
            GameCharacter gameCharacter = availableCharacters.get(randomIndex);
            System.out.println("A " + gameCharacter.getName() + " was removed.");
            if (gameCharacter.getName().equals("King")) {
                System.out.println("The King cannot be visibly removed, trying again..");
                // 需要将 king 先 remove 掉，然后添到最后的位置上
                availableCharacters.remove(randomIndex);
                availableCharacters.add(gameCharacter);

                // 打印之前 removed 的角色
                // 我们需要从已有的 faceUpCharacters 中重新打印
                for (int j = 0; j < faceUpCharacters.size(); j++) {
                    System.out.println(faceUpCharacters.get(j).getName());
                }
                // 重新开始 i-- 代表回归到上一次的循环，然后下一次循环开始的时候就是 i++ 依旧会反问当前的 index 位置
                i--;
                continue;
            }
            // 如果不是king 的话，那么就直接 removed 即可
            // 将 faceup 的角色添加到 faceUpCharacters 列表中
            faceUpCharacters.add(gameCharacter);
            // 从 availableCharacters 列表中移除该角色
            availableCharacters.remove(randomIndex);
            System.out.println("A " + gameCharacter.getName() + " was removed.");
        }

        // 处理 facedown 的情况
        // facedown 可以是 king ，所以我们额外的条件
        int ramdomIndex = this.random.nextInt(availableCharacters.size());
        GameCharacter facedownCharacter = availableCharacters.get(ramdomIndex);

        // 从拥有皇冠的人开始 我们已经排好序了
        for (int i = 0; i < players.size(); i++) {
            // 因为我们有两个种 player
            // ai 的 player 和 human 的 player 实际上不是同一种操作
            Player player = players.get(i);
            if (i == players.size() - 1) {
                // 因为最后一个玩家的选择角色实际上是从 available 以及 facedown 中进行选择的
                // 把 face-down 给添加上
                availableCharacters.add(facedownCharacter);
            }

            if (player instanceof HumanPlayer) {
                player.chooseCharacter(availableCharacters, scanner);
            } else {
                player.chooseCharacter(availableCharacters, scanner);
            }
        }

        System.out.println("Character choosing is over, action round will now begin.");
    }

    // 创建一个角色和 player 的映射
    public Map<String, Player> createActionOrder(List<Player> players) {
        Map<String, Player> order = new HashMap<>();

        for (int i = 0; i < GameHelper.actionOrder.length; i++) {
            boolean findFlag = false;
            for (Player player : players) {

                // 如果玩家选择的角色与 order 中的名字相符合
                if (player.getCharacter().getName().equals(GameHelper.actionOrder[i])) {

                    // 把这个角色： 玩家 作为键值对放入我们的 map
                    order.put(GameHelper.actionOrder[i], player);
                    findFlag = true;
                    break;
                }
            }
            // 如果找不到这个角色，就提示
            if (!findFlag) {
                order.put(GameHelper.actionOrder[i], null);
            }
        }

        return order;
    }

    public void startActionRound(Map<String, Player> actionOrder, Scanner scanner, List<GameCharacter> characters) {
        System.out
                .println("================================\n" + "TURN PHASE\n" + "================================\n");

        List<Player> allPlayers = new ArrayList<>();
        for (Player player : actionOrder.values()) {
            allPlayers.add(player);
        }

        for (int i = 0; i < GameHelper.actionOrder.length; i++) {
            // 按照顺序来安排的玩家
            String character = GameHelper.actionOrder[i];
            Player player = actionOrder.get(character);
            System.out.println(i + 1 + ": " + character);
            boolean humanRound = false;

            if (player == null) {
                System.out.println("No one is the " + character);
            } else {
                // 如果玩家不为空，那么就进行一个行动
                // 分为人类和ai 两个操作
                // 人类通常需要 scanner 需要往 take turn 中传入 scanner 属性
                if (player instanceof HumanPlayer) {
                    humanRound = true;
                    this.currentHumanPlayer = player;
                    player.takeTurn(scanner, allPlayers, characters);
                } else {
                    player.takeTurn(scanner, allPlayers, characters);
                }
            }
            Command commandHandler = new Command();
            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine();
                if (command.equals("t")) {
                    // 如果是非人类回合，那么就 break 进入到下一个玩家进行操作
                    if (!humanRound) {
                        break;
                    }
                    // 你在人类环节是不能够输入 t 的
                    System.out.println("Invalid command");
                } else if (command.equals("hand")) {
                    commandHandler.hand(this.currentHumanPlayer);
                } else if (command.equals("gold")) {
                    commandHandler.gold(this.currentHumanPlayer);
                } else if (command.equals("end")) { // 退出当前的玩家环节
                    humanRound = false; // 就可以通过输入 t 来进入下一个玩家
                } else if (command.startsWith("build")) {
                    commandHandler.build(humanRound, command, player);
                } else if (command.startsWith("citadel") || command.startsWith("list") || command.startsWith("hand")
                        || command.startsWith("city")) {
                    commandHandler.city(command, allPlayers);
                } else if (command.equals("action")) {
                    // 执行角色特殊动作，待实现
                    commandHandler.action();
                } else if (command.startsWith("info")) {
                    // 待实先
                    commandHandler.info(command, this.currentHumanPlayer);
                } else if (command.equals("help")) {
                    // 待实现
                    commandHandler.help();
                } else if (command.equals("debug")) {
                    // 待实现
                    commandHandler.debug();
                } else if (command.equals("save")) {
                    // 待实现
                    commandHandler.save(scanner, allPlayers);
                } else if (command.equals("load")) {
                    // 待实先
                    commandHandler.load(scanner, allPlayers);
                } else {
                    System.out.println("Invalid command, Please input t to continue");
                }
            }

            for (GameCharacter currentCharacter : characters) {
                currentCharacter.setBuiltCount(0);
            }
        }
    }

    public boolean isGameOver(List<Player> players) {
        // 游戏结束的逻辑
        for (Player player : players) {
            if (player.getCity().size() >= 8) {
                return true;
            }
        }
        return false;
    }

    public void declareWinner(List<Player> players) {
        // 声明胜利者
        // 需要去判断谁是胜利者
        int biggestScore = 0;
        int maxIndex = 0;
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.getScore() > biggestScore) {
                biggestScore = player.getScore();
                maxIndex = i;
            }
        }
        System.out.println("Player " + (maxIndex + 1) + " wins with " + biggestScore + " points!");
        System.out.println("Game over, Player " + (maxIndex + 1) + " wins!");
    }
}
