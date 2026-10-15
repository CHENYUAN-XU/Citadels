package citadels.Character;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import citadels.Player.Player;
import citadels.Building.Building;

public class Assassin extends GameCharacter {
    private Random random;

    public Assassin() {
        super(1, "Assassin", "Select another character whom you wish to kill. The killed character loses their turn.");
        this.random = new Random();
    }

    /**
     * 对于 ai 来说，会去选择一个玩家进行刺杀
     * 刺杀也会存在一个特殊的逻辑，来进行处理
     * 会有一个优先级在
     * 这个优先级你可以随意发挥
     */
    @Override
    public void executeAIAction(List<Player> allPlayers, List<GameCharacter> characters) {
        // AI刺客决策逻辑
        GameCharacter targetCharacter = null; // ai 想去刺杀的角色

        // 优先级评分系统
        int kingScore = 0;
        int thiefScore = 0; // 没有办法刺杀盗贼
        int magicianScore = 0;
        int bishopScore = 0;
        int merchantScore = 0;
        int architectScore = 0;
        int warlordScore = 0;

        // 分析所有玩家的状态
        for (Player player : allPlayers) {
            // 跳过自己
            if (this.getPlayer().getId() == player.getId()) {
                continue;
            }

            // 分析对手建筑和资源情况
            int citySize = player.getCity().size(); // 有多少建筑
            int gold = player.getGold(); // 有多少金币
            int handSize = player.getHand().size(); // 有多少卡牌

            // 建筑颜色统计
            int redCount = player.getHandOfColor("red").size();
            int blueCount = player.getHandOfColor("blue").size();
            int yellowCount = player.getHandOfColor("yellow").size();
            int greenCount = player.getHandOfColor("green").size();

            // 根据颜色进行评分
            // 颜色的评分是你自己定的逻辑

            // 国王的评分
            if (yellowCount >= 2) {
                kingScore += 3;
            }
            if (citySize >= 6) {
                kingScore += 2; // 接近获胜玩家可能会选国王来获得优先权
            }

            // 盗贼(Thief)评分 - 金币多的玩家是盗贼的好目标，但我们不杀盗贼
            // 我们增加盗贼评分是为了后续刺杀可能被盗贼偷的角色
            if (gold >= 5)
                thiefScore += 2;

            // 魔术师(Magician)评分 - 手牌少的玩家可能会选魔术师
            if (handSize <= 1)
                magicianScore += 3;
            if (this.getPlayer().getHand().size() <= 1)
                magicianScore += 1; // 我方手牌少，防止被交换

            // 主教(Bishop)评分 - 蓝色建筑多的玩家可能会选主教
            if (blueCount >= 2)
                bishopScore += 3;
            if (citySize >= 5)
                bishopScore += 2; // 城市较大可能会选主教防止被摧毁

            // 商人(Merchant)评分 - 绿色建筑多或金币少的玩家可能会选商人
            if (greenCount >= 2)
                merchantScore += 3;
            if (gold <= 2)
                merchantScore += 2;

            // 建筑师(Architect)评分 - 手牌多或金币多的玩家可能会选建筑师
            if (handSize >= 3)
                architectScore += 3;
            if (gold >= 6)
                architectScore += 3;
            if (citySize >= 5)
                architectScore += 4; // 接近完成城市的玩家选建筑师威胁最大

            // 军阀(Warlord)评分 - 红色建筑多的玩家可能会选军阀
            if (redCount >= 2)
                warlordScore += 3;
            // 如果有玩家城市快完成，AI可能会选军阀来摧毁
            for (Player otherPlayer : allPlayers) {
                if (otherPlayer.getId() != player.getId() && otherPlayer.getCity().size() >= 6) {
                    warlordScore += 3;
                    break;
                }
            }
        }

        // 如果AI金币较少，提高刺杀商人的优先级
        if (this.getPlayer().getGold() <= 2)
            merchantScore += 2;

        // 如果AI的建筑即将被摧毁，提高刺杀军阀的优先级
        if (this.getPlayer().getCity().size() >= 5 && this.getPlayer().getCity().size() < 8) {
            warlordScore += 4;
        }

        // 如果AI手牌少，提高刺杀建筑师的优先级
        if (this.getPlayer().getHand().size() <= 2)
            architectScore += 3;

        // 如果AI有重要建筑，提高刺杀盗贼的优先级防止被偷
        if (this.getPlayer().getGold() >= 5)
            thiefScore += 2;

        // 4. 根据评分确定最终目标
        int maxScore = Math.max(kingScore, Math.max(bishopScore,
                Math.max(merchantScore, Math.max(architectScore,
                        Math.max(magicianScore, warlordScore)))));

        String targetCharacterName = "";

        // 按照威胁级别从高到低选择目标
        if (maxScore == architectScore) {
            targetCharacterName = "Architect"; // 建筑师威胁最大，优先刺杀
        } else if (maxScore == warlordScore) {
            targetCharacterName = "Warlord"; // 军阀可能摧毁我方建筑
        } else if (maxScore == kingScore) {
            targetCharacterName = "King"; // 国王有先手优势且可能从黄色建筑获益
        } else if (maxScore == bishopScore) {
            targetCharacterName = "Bishop"; // 主教可能从蓝色建筑获益且不能被军阀摧毁
        } else if (maxScore == merchantScore) {
            targetCharacterName = "Merchant"; // 商人可能从绿色建筑获益且有额外金币
        } else if (maxScore == magicianScore) {
            targetCharacterName = "Magician"; // 魔术师可能交换手牌
        } else {
            // 默认情况，随机选择一个高级别角色
            String[] highTierCharacters = { "Architect", "Warlord", "King" };
            targetCharacterName = highTierCharacters[random.nextInt(highTierCharacters.length)];
        }

        // 5. 执行刺杀
        for (GameCharacter character : characters) {
            if (character.getName().equals(targetCharacterName)) {
                character.setIsAssassinated(true);
                System.out.println("The Assassin choose to assassinate: " + targetCharacterName);
                return;
            }
        }

        // 6. 如果没有找到目标角色，随机选择一个角色暗杀(除了自己)
        for (GameCharacter character : characters) {
            if (character.getRank() != this.getRank() && !character.getIsAssassinated()) {
                character.setIsAssassinated(true);
                System.out.println("The Assassin choose to assassinate: " + character.getName());
                return;
            }
        }
    }

    @Override
    public void executeHumanAction(Scanner scanner, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        System.out.println("Who do you want to assassinate? Choose a character from 2-8:");
        int choice = 0;
        while (true) {
            System.out.print("> ");
            // 读取输入
            choice = scanner.nextInt();
            if (choice >= 2 && choice <= 8) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a number between 2 and 8.");
            }
        }

        // 根据输入选择角色
        GameCharacter targetCharacter = characters.get(choice - 1);
        if (targetCharacter.getPlayer() != null) {
            targetCharacter.setIsAssassinated(true);
        } else {
            System.out.println("There is no " + targetCharacter.getName() + " in the game.");
        }
    }

    /**
     * AI刺客选择建造建筑
     * <p>
     * AI控制的刺客会优先选择便宜的建筑来建造，以便快速累积城市数量。
     * 这种策略适合刺客的角色定位，因为刺客的主要能力是控制而非经济。
     * </p>
     * 
     * @return 选择要建造的建筑，如果没有可建造的建筑则返回null
     */
    @Override
    public void chooseAIBuilding() {
        // TODO Auto-generated method stub
        // 获取玩家所有的卡牌
        List<Building> buildings = this.getPlayer().getHand();

        Building bestBuilding = null;

        int lowestCost = -1000;

        for (Building building : buildings) {
            if (building.getCost() < lowestCost) {
                lowestCost = building.getCost();
                bestBuilding = building;
            }
        }

        // 建造城市
        this.getPlayer().addCity(bestBuilding);

        // 移除手牌
        this.getPlayer().removeHand(bestBuilding);
    }

    /**
     * 刺客不需要收集金币
     */
    @Override
    public void collectIncome() {
        // TODO Auto-generated method stub
        return;
    }

}
