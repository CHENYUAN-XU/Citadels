package citadels.Player;

import java.util.*;
import citadels.Building.Building;
import citadels.Character.GameCharacter;
import citadels.Character.GameCharacterFactory;
import citadels.Game.GameManager;
import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class Player {
    /* 玩家的 id */
    // 循环的 int i
    private int id;

    /* 玩家拥有的金币数量 */
    private int gold;

    /* 玩家手中的建筑卡牌 */
    private List<Building> hand;

    /* 玩家拥有的建筑 */
    private List<Building> city;

    /* 玩家当前的角色 */
    private GameCharacter character;

    /* 玩家是否有皇冠 */
    private boolean hasCrown;

    /* random 随机数 */
    private Random random;

    public Player(int id) {
        this.id = id;
        this.gold = 2;
        this.hand = new ArrayList<>(); // 初始化的时候都是空的
        this.city = new ArrayList<>();
        this.hasCrown = false;
        this.character = null;
        this.random = new Random();
    }

    /* 变成抽象方法是因为 每个玩家的选择方式不一样 */
    /* 区分开 ai 和 人类的区别 */
    public abstract void chooseCharacter(List<GameCharacter> availableCharacters, Scanner scanner);

    /* 每个玩家回合的逻辑 */
    /* 区分开人类和 ai，因为 ai 以自我判断为主，人类以 scanner 询问为主 */
    public abstract void takeTurn(Scanner scanner, List<Player> allPlayers, List<GameCharacter> characters);

    /* 默认方法抽卡还是 2 金币 */
    public void aiCardAndGold() {
        int action = this.random.nextInt(2);
        if (action == 0) { // 金币
            this.setGold(this.getGold() + 2);
        } else { // 抽卡
            // 如果获取的 building 没有数量了，那么就重新获取
            while (true) {
                int buildingIndex = this.random.nextInt(GameManager.allBuildingsCards.size());
                Building building = GameManager.allBuildingsCards.get(buildingIndex);
                if (building.getQty() > 0) {
                    this.addHand(building);
                    building.substractQty(1);
                    break;
                }
            }
        }
    }

    /* 人类抽卡和金币 需要传入一个 scanner */
    public void humanCardAndGold(Scanner scanner) {
        while (true) {
            System.out.println("Collect 2 gold or draw two cards and pick one [gold/cards]: ");
            String choice = scanner.nextLine();
            if (choice.equals("gold")) {
                this.setGold(this.getGold() + 2);
                break;
            } else if (choice.equals("cards")) {
                // 抽一张卡
                while (true) {
                    int buildingIndex = this.random.nextInt(GameManager.allBuildingsCards.size());
                    Building building = GameManager.allBuildingsCards.get(buildingIndex);
                    if (building.getQty() > 0) {
                        this.addHand(building);
                        building.substractQty(1);
                        break;
                    }
                }
            } else {
                System.out.println("Invalid input. Please enter gold or cards.");
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public int getGold() {
        return this.gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public List<Building> getHand() {
        return this.hand;
    }

    public List<Building> getHandOfColor(String color) {
        List<Building> buildings = new ArrayList<>();
        for (Building building : this.hand) {
            if (building.getColor().equals(color)) {
                buildings.add(building);
            }
        }
        return buildings;
    }

    public void addHand(Building building) {
        this.hand.add(building);
    }

    public void setHand(List<Building> hand) {
        this.hand = hand;
    }

    public void removeHand(Building building) {

        for (int i = 0; i < this.hand.size(); i++) {
            if (this.hand.get(i).getName().equals(building.getName())) {
                this.hand.get(i).substractQty(1);
                break;
            }
        }
    }

    public List<Building> getCity() {
        return this.city;
    }

    public List<Building> getBuildingsOfColor(String color) {
        List<Building> buildings = new ArrayList<>();
        for (Building building : this.city) {
            if (building.getColor().equals(color)) {
                buildings.add(building);
            }
        }
        return buildings;
    }

    public void addCity(Building building) {
        this.city.add(building);
    }

    public void removeCity(Building building) {
        this.city.remove(building);
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public void setCharacter(GameCharacter character) {
        this.character = character;
    }

    public boolean getHasCrown() {
        return this.hasCrown;
    }

    public void setHasCrown(boolean hasCrown) {
        this.hasCrown = hasCrown;
    }

    public int getScore() {
        int count = 0;
        for (Building building : this.city) {
            count += building.getCost();
        }
        return count;
    }

    /**
     * 将游戏中所有玩家信息保存到JSON文件，并提示用户输入文件名
     * 
     * @param scanner 用于读取用户输入的Scanner对象
     * @param players 要保存的玩家列表
     * @return 是否保存成功
     */
    public static boolean save(Scanner scanner, List<Player> players) {
        try {
            // 询问用户输入文件名
            System.out.println("请输入保存文件名 (例如：game_save.json)：");
            String fileName = scanner.nextLine();

            // 如果用户没有指定.json后缀，自动添加
            if (!fileName.toLowerCase().endsWith(".json")) {
                fileName += ".json";
            }

            // 创建saves目录（如果不存在）
            File savesDir = new File("saves");
            if (!savesDir.exists()) {
                savesDir.mkdir();
            }

            // 构建完整的文件路径
            String filePath = "saves" + File.separator + fileName;

            // 创建JSON对象存储游戏信息
            JSONObject gameJson = new JSONObject();

            // 保存玩家列表信息
            JSONArray playersJson = new JSONArray();
            for (Player player : players) {
                JSONObject playerJson = new JSONObject();
                playerJson.put("id", player.id);
                playerJson.put("gold", player.gold);
                playerJson.put("hasCrown", player.hasCrown);
                playerJson.put("playerType", player.getClass().getSimpleName());

                // 保存角色信息
                if (player.character != null) {
                    JSONObject characterJson = new JSONObject();
                    characterJson.put("name", player.character.getName());
                    characterJson.put("rank", player.getCharacter().getRank());
                    playerJson.put("character", characterJson);
                }

                // 保存手牌信息
                JSONArray handJson = new JSONArray();
                for (Building building : player.hand) {
                    JSONObject buildingJson = new JSONObject();
                    buildingJson.put("name", building.getName());
                    buildingJson.put("cost", building.getCost());
                    buildingJson.put("color", building.getColor());
                    buildingJson.put("qty", building.getQty());
                    handJson.add(buildingJson);
                }
                playerJson.put("hand", handJson);

                // 保存城市建筑信息
                JSONArray cityJson = new JSONArray();
                for (Building building : player.city) {
                    JSONObject buildingJson = new JSONObject();
                    buildingJson.put("name", building.getName());
                    buildingJson.put("cost", building.getCost());
                    buildingJson.put("color", building.getColor());
                    buildingJson.put("qty", building.getQty());
                    cityJson.add(buildingJson);
                }
                playerJson.put("city", cityJson);

                playersJson.add(playerJson);
            }

            // 放游戏剩余hand的信息
            JSONArray allHandsJson = new JSONArray();
            for (Building building : GameManager.allBuildingsCards) {
                JSONObject buildingJson = new JSONObject();
                buildingJson.put("name", building.getName());
                buildingJson.put("cost", building.getCost());
                buildingJson.put("color", building.getColor());
                buildingJson.put("qty", building.getQty());
                allHandsJson.add(buildingJson);
            }

            gameJson.put("players", playersJson);
            gameJson.put("allHands", allHandsJson);

            // 将JSON对象写入文件
            try (FileWriter fileWriter = new FileWriter(filePath)) {
                fileWriter.write(gameJson.toJSONString());
                fileWriter.flush();

                System.out.println("游戏已成功保存到: " + filePath);
                return true;
            }
        } catch (IOException e) {
            System.err.println("保存游戏信息失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 从JSON文件加载玩家信息
     * 
     * @param scanner 用于读取用户输入的Scanner对象
     * @param players 当前游戏中的玩家列表，会被加载的数据替换
     * @return 是否加载成功
     */
    public static boolean load(Scanner scanner, List<Player> players) {
        try {
            // 检查saves目录是否存在
            File savesDir = new File("saves");
            if (!savesDir.exists() || !savesDir.isDirectory()) {
                System.out.println("没有找到保存目录。没有可加载的存档。");
                return false;
            }

            // 列出可用的存档文件
            File[] saveFiles = savesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

            if (saveFiles == null || saveFiles.length == 0) {
                System.out.println("没有找到可用的存档文件。");
                return false;
            }

            System.out.println("可用的存档文件:");
            for (int i = 0; i < saveFiles.length; i++) {
                System.out.println((i + 1) + ". " + saveFiles[i].getName());
            }

            // 询问用户选择哪个文件
            System.out.println("请输入要加载的存档文件编号，或输入文件名：");
            String userInput = scanner.nextLine();

            String filePath;
            try {
                // 尝试将输入解析为数字（文件编号）
                int fileIndex = Integer.parseInt(userInput) - 1;
                if (fileIndex >= 0 && fileIndex < saveFiles.length) {
                    filePath = saveFiles[fileIndex].getPath();
                } else {
                    System.out.println("无效的文件编号。");
                    return false;
                }
            } catch (NumberFormatException e) {
                // 如果输入不是数字，则视为文件名
                if (!userInput.toLowerCase().endsWith(".json")) {
                    userInput += ".json";
                }
                filePath = "saves" + File.separator + userInput;

                // 检查文件是否存在
                File file = new File(filePath);
                if (!file.exists() || !file.isFile()) {
                    System.out.println("找不到文件：" + filePath);
                    return false;
                }
            }

            // 解析JSON文件
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject gameJson = (JSONObject) obj;

            // 使用GameCharacterFactory创建所有角色
            List<GameCharacter> allCharacters = GameCharacterFactory.createAllCharacters();

            // 加载玩家列表
            List<Player> loadedPlayers = new ArrayList<>();
            JSONArray playersJson = (JSONArray) gameJson.get("players");

            for (Object playerObj : playersJson) {
                JSONObject playerJson = (JSONObject) playerObj;

                // 获取玩家类型
                String playerType = (String) playerJson.get("playerType");
                int playerId = ((Long) playerJson.get("id")).intValue();

                // 根据类型创建相应的玩家对象
                Player player;
                if (playerType.equals("AIPlayer")) {
                    player = new AIPlayer(playerId);
                } else if (playerType.equals("HumanPlayer")) {
                    player = new HumanPlayer(playerId);
                } else {
                    System.out.println("未知的玩家类型: " + playerType);
                    continue;
                }

                // 设置基本属性
                player.gold = ((Long) playerJson.get("gold")).intValue();
                player.hasCrown = (Boolean) playerJson.get("hasCrown");

                // 加载角色信息
                if (playerJson.containsKey("character")) {
                    JSONObject characterJson = (JSONObject) playerJson.get("character");
                    String characterName = (String) characterJson.get("name");

                    // 查找匹配的角色
                    for (GameCharacter character : allCharacters) {
                        if (character.getName().equals(characterName)) {
                            player.character = character;
                            character.setPlayer(player);
                            break;
                        }
                    }
                }

                // 加载手牌信息
                JSONArray handJson = (JSONArray) playerJson.get("hand");
                for (Object buildingObj : handJson) {
                    JSONObject buildingJson = (JSONObject) buildingObj;
                    String name = (String) buildingJson.get("name");
                    int cost = ((Long) buildingJson.get("cost")).intValue();
                    String color = (String) buildingJson.get("color");
                    int qty = ((Long) buildingJson.get("qty")).intValue();

                    Building building = new Building(name, cost, color, qty);
                    player.hand.add(building);
                }

                // 加载城市建筑信息
                JSONArray cityJson = (JSONArray) playerJson.get("city");
                for (Object buildingObj : cityJson) {
                    JSONObject buildingJson = (JSONObject) buildingObj;
                    String name = (String) buildingJson.get("name");
                    int cost = ((Long) buildingJson.get("cost")).intValue();
                    String color = (String) buildingJson.get("color");
                    int qty = ((Long) buildingJson.get("qty")).intValue();

                    Building building = new Building(name, cost, color, qty);
                    player.city.add(building);
                }

                loadedPlayers.add(player);
            }

            // 清空并替换传入的玩家列表
            players.clear();
            players.addAll(loadedPlayers);

            System.out.println("游戏已成功从 " + filePath + " 加载");
            return true;

        } catch (IOException | ParseException e) {
            System.err.println("加载游戏信息失败: " + e.getMessage());
            return false;
        }
    }

}
