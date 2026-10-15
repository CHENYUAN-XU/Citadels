package citadels.Player;

import java.util.*;
import citadels.Character.GameCharacter;
import citadels.Building.*;
import citadels.Game.GameManager;

public class AIPlayer extends Player {

    private Random random;

    public AIPlayer(int id) {
        super(id);
        this.random = new Random();
    }

    @Override
    public void chooseCharacter(List<GameCharacter> availableCharacters, Scanner scanner) {

        GameCharacter selectedCharacter = null;

        // 逻辑 1： 如果ai 玩家手牌小于 2 张，会优先选择建筑师或魔术师
        if (this.getHand().size() < 2) {
            // 循环 availableCharacters 列表 寻找建筑师
            for (int i = 0; i < availableCharacters.size(); i++) {
                GameCharacter gameCharacter = availableCharacters.get(i);
                if (gameCharacter.getName().equals("Architect") || gameCharacter.getName().equals("Magician")) {
                    selectedCharacter = gameCharacter;
                    availableCharacters.remove(i); // 从 available 中删除角色
                    if (GameManager.debug) {
                        System.out.println("Player " + this.getId() + " choose" + gameCharacter.getName() + ".");
                    }
                    System.out.println("Player " + this.getId() + " choose a character.");
                    break;
                }
            }
        }
        // 逻辑 2，金币大于 5 考虑军阀来摧毁对方的建筑
        if (selectedCharacter == null && this.getGold() > 5) {
            for (int i = 0; i < availableCharacters.size(); i++) {
                GameCharacter gameCharacter = availableCharacters.get(i);
                if (gameCharacter.getName().equals("Warlord")) {
                    selectedCharacter = gameCharacter;
                    availableCharacters.remove(i); // 从 available 中删除角色
                    if (GameManager.debug) {
                        System.out.println("Player " + this.getId() + " choose" + gameCharacter.getName() + ".");
                    }
                    System.out.println("Player " + this.getId() + " choose a character.");
                    break;
                }
            }
        }

        // 逻辑3: 如果金币少（小于3），考虑选择商人
        if (selectedCharacter == null && this.getGold() < 3) {
            for (int i = 0; i < availableCharacters.size(); i++) {
                GameCharacter gameCharacter = availableCharacters.get(i);
                if (gameCharacter.getName().equals("Merchant")) {
                    selectedCharacter = gameCharacter;
                    availableCharacters.remove(i); // 从 available 中删除角色
                    if (GameManager.debug) {
                        System.out.println("Player " + this.getId() + " choose" + gameCharacter.getName() + ".");
                    }
                    System.out.println("Player " + this.getId() + " choose a character.");
                    break;
                }
            }
        }

        // 逻辑4: 如果城市中有多个相同颜色的建筑，考虑选择对应的角色
        if (selectedCharacter == null) {
            int redCount = 0, blueCount = 0, greenCount = 0, yellowCount = 0;
            for (Building building : this.getCity()) {
                if (building.getColor().equals("red")) {
                    redCount++;
                } else if (building.getColor().equals("blue")) {
                    blueCount++;
                } else if (building.getColor().equals("green")) {
                    greenCount++;
                } else if (building.getColor().equals("yellow")) {
                    yellowCount++;
                }
            }

            if (redCount > blueCount && redCount > greenCount && redCount > yellowCount) {
                for (int i = 0; i < availableCharacters.size(); i++) {
                    GameCharacter gameCharacter = availableCharacters.get(i);
                    if (gameCharacter.getName().equals("Warload")) {
                        selectedCharacter = gameCharacter;
                        availableCharacters.remove(i); // 从 available 中删除角色
                        if (GameManager.debug) {
                            System.out.println("Player " + this.getId() + " choose" + gameCharacter.getName() + ".");
                        }
                        System.out.println("Player " + this.getId() + " choose a character.");
                        break;
                    }
                }
            }

            if (selectedCharacter == null && blueCount > redCount && blueCount > greenCount
                    && blueCount > yellowCount) {
                for (int i = 0; i < availableCharacters.size(); i++) {
                    GameCharacter gameCharacter = availableCharacters.get(i);
                    if (gameCharacter.getName().equals("Bishop")) {
                        selectedCharacter = gameCharacter;
                        availableCharacters.remove(i); // 从 available 中删除角色
                        if (GameManager.debug) {
                            System.out.println("Player " + this.getId() + " choose" + gameCharacter.getName() + ".");
                        }
                        System.out.println("Player " + this.getId() + " choose a character.");
                        break;
                    }
                }
            }

            if (selectedCharacter == null && greenCount > redCount && greenCount > blueCount
                    && greenCount > yellowCount) {
                for (int i = 0; i < availableCharacters.size(); i++) {
                    GameCharacter gameCharacter = availableCharacters.get(i);
                    if (gameCharacter.getName().equals("Merchant")) {
                        selectedCharacter = gameCharacter;
                        availableCharacters.remove(i); // 从 available 中删除角色
                        if (GameManager.debug) {
                            System.out.println("Player " + this.getId() + " choose" + gameCharacter.getName() + ".");
                        }
                        System.out.println("Player " + this.getId() + " choose a character.");
                        break;
                    }
                }
            }

            if (selectedCharacter == null && yellowCount > redCount && yellowCount > blueCount
                    && yellowCount > greenCount) {
                for (int i = 0; i < availableCharacters.size(); i++) {
                    GameCharacter gameCharacter = availableCharacters.get(i);
                    if (gameCharacter.getName().equals("King")) {
                        selectedCharacter = gameCharacter;
                        availableCharacters.remove(i); // 从 available 中删除角色
                        if (GameManager.debug) {
                            System.out.println("Player " + this.getId() + " choose" + gameCharacter.getName() + ".");
                        }
                        System.out.println("Player " + this.getId() + " choose a character.");
                        break;
                    }
                }
            }
        }

        // 逻辑5: 如果前面的所有逻辑都没有找到合适的角色，随机选择一个
        if (selectedCharacter == null) {
            int randomIndex = this.random.nextInt(availableCharacters.size());
            selectedCharacter = availableCharacters.get(randomIndex);
            availableCharacters.remove(randomIndex);
            if (GameManager.debug) {
                System.out.println("Player " + this.getId() + " choose" + selectedCharacter.getName() + ".");
            }
            System.out.println("Player " + this.getId() + " choose a character.");
        }
        // 将选择的角色赋值给 player
        this.setCharacter(selectedCharacter);
        selectedCharacter.setPlayer(this);

        while (true) {
            // 因为是 ai 的玩家，所以用户只能在这里输入 t 来进行下一个选择
            System.out.print("> ");
            String input = scanner.next();
            if (input.equals("t")) {
                break;
            } else {
                System.out.println("It is not your turn. Press t to continue with other player turns.");
            }
        }
    }

    @Override
    public void takeTurn(Scanner scanner, List<Player> allPlayers, List<GameCharacter> characters) {
        // 检测当前玩家是否被刺杀
        if (this.getCharacter().getIsAssassinated()) {
            System.out.println(this.getCharacter().getName() + " is assassinated, skip this turn.");
            return;
        }

        // 如果当前玩家没有被刺杀，那么就进行一个行动
        // 分为两个逻辑，第一个是执行特殊操作，第二是询问是金币还是卡牌（这个询问在 ai 处是 random）

        // 执行操作
        this.getCharacter().executeAIAction(allPlayers, characters);

        // 抽卡还是金币
        this.aiCardAndGold();

        // 执行建造
        this.getCharacter().chooseAIBuilding();

        // 收集金币
        this.getCharacter().collectIncome();
    }

}
