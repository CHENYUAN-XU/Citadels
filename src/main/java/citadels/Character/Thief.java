package citadels.Character;

import java.util.List;
import java.util.Scanner;
import citadels.Player.Player;
import citadels.Building.Building;

public class Thief extends GameCharacter {
    public Thief() {
        super(2, "Thief",
                "Select another character whom you wish to rob. When a player reveals that character to take his turn, you immediately take all of his gold. You cannot rob the Assassin or the killed character.");
    }

    @Override
    public void executeAIAction(List<Player> allPlayers, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeAIAction'");
    }

    @Override
    public void executeHumanAction(Scanner scanner, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        System.out.print("Please choose the character you want to steal gold.");
        for (int i = 0; i < characters.size(); i++) {
            GameCharacter c = characters.get(i);
            // 不能盗窃自己、刺客或已被刺杀的角色
            if (c.getRank() != this.getRank() && c.getRank() != 1 && !c.getIsAssassinated()) {
                System.out.println((i + 1) + ". " + c.getName());
            }
        }

        int choice = scanner.nextInt();
        if (choice > 0 && choice <= characters.size()) {
            GameCharacter target = characters.get(choice - 1);
            if (target.getRank() != this.getRank() && target.getRank() != 1 && !target.getIsAssassinated()) {
                target.setIsStolen(true);
                System.out.println(target.getName() + " is robbed!");
            } else {
                System.out.println(
                        "Invalid target! You cannot steal gold from yourself, assassin, or a character that has been assassinated.");
            }
        } else {
            System.out.println("Invalid choice!");
        }
    }

    @Override
    public void chooseAIBuilding() {
        // TODO Auto-generated method stub
        // AI盗贼决策逻辑

        // 角色评分系统 - 不能盗窃刺客(1)或被刺杀的角色
        int kingScore = 0;
        int bishopScore = 0;
        int merchantScore = 0;
        int architectScore = 0;
        int magicianScore = 0;
        int warlordScore = 0;

        // // 1. 分析游戏状态和玩家情况
        // for (Player player : players) {
        // // 跳过自己
        // if (player.getId() == this.getPlayer().getId())
        // continue;

        // // 分析对手建筑和资源
        // int citySize = player.getCity().size();
        // int gold = player.getGold();
        // int yellowCount = player.getBuildingsOfColor("yellow").size();
        // int blueCount = player.getBuildingsOfColor("blue").size();
        // int greenCount = player.getBuildingsOfColor("green").size();
        // int redCount = player.getBuildingsOfColor("red").size();

        // // 2. 根据对手情况评估哪个角色更有价值盗窃

        // // 国王(King)评分 - 黄色建筑多，可能会有较多金币收入
        // if (yellowCount >= 2)
        // kingScore += 4;
        // if (citySize >= 6)
        // kingScore += 2; // 接近获胜的玩家可能会选国王获得先手权

        // // 主教(Bishop)评分 - 蓝色建筑多，可能会有较多金币收入
        // if (blueCount >= 2)
        // bishopScore += 4;

        // // 商人(Merchant)评分 - 绿色建筑多+额外金币，最好的盗窃目标
        // if (greenCount >= 2)
        // merchantScore += 5;
        // merchantScore += 3; // 商人有额外金币收入

        // // 建筑师(Architect)评分 - 通常不是很好的盗窃目标，因为会花费金币建造多个建筑
        // if (gold >= 6)
        // architectScore += 2; // 仅当金币很多时

        // // 魔术师(Magician)评分 - 通常不是好的盗窃目标
        // // 保持较低评分，因为魔术师常常没有太多金币

        // // 军阀(Warlord)评分 - 红色建筑多，可能会有较多金币收入
        // if (redCount >= 2)
        // warlordScore += 4;
        // }

        // 3. 根据游戏进展调整评分

        // // 根据游戏阶段调整策略
        // int gameStage = 0;
        // for (Player player : players) {
        // gameStage = Math.max(gameStage, player.getCity().size());
        // }

        // // 游戏中后期提高商人和国王的优先级
        // if (gameStage >= 5) {
        // merchantScore += 2;
        // kingScore += 2;
        // }

        // // 4. 检查刺客是否已经刺杀了某个角色，避免盗窃该角色
        // for (GameCharacter character : characters) {
        // if (character.isAssassinated()) {
        // switch (character.getName()) {
        // case "King":
        // kingScore = -100;
        // break;
        // case "Bishop":
        // bishopScore = -100;
        // break;
        // case "Merchant":
        // merchantScore = -100;
        // break;
        // case "Architect":
        // architectScore = -100;
        // break;
        // case "Magician":
        // magicianScore = -100;
        // break;
        // case "Warlord":
        // warlordScore = -100;
        // break;
        // }
        // }
        // }

        // // 5. 根据评分选择目标
        // int maxScore = Math.max(kingScore, Math.max(bishopScore,
        // Math.max(merchantScore, Math.max(architectScore,
        // Math.max(magicianScore, warlordScore)))));

        // String targetCharacterName = "";

        // // 按照优先级从高到低选择目标
        // if (maxScore == merchantScore && merchantScore > 0) {
        // targetCharacterName = "Merchant"; // 商人是最佳盗窃目标，因为有绿色建筑收入和额外金币
        // } else if (maxScore == kingScore && kingScore > 0) {
        // targetCharacterName = "King"; // 国王可能从黄色建筑获益
        // } else if (maxScore == bishopScore && bishopScore > 0) {
        // targetCharacterName = "Bishop"; // 主教可能从蓝色建筑获益
        // } else if (maxScore == warlordScore && warlordScore > 0) {
        // targetCharacterName = "Warlord"; // 军阀可能从红色建筑获益
        // } else if (maxScore == architectScore && architectScore > 0) {
        // targetCharacterName = "Architect"; // 建筑师可能有剩余金币
        // } else if (maxScore == magicianScore && magicianScore > 0) {
        // targetCharacterName = "Magician"; // 魔术师不是理想目标，但可能有一些金币
        // }

        // // 6. 执行盗窃
        // for (GameCharacter character : characters) {
        // if (character.getName().equals(targetCharacterName) &&
        // character.getRank() != 1 && !character.isAssassinated()) {
        // character.setRobbed(true);
        // System.out.println("The Thief choose a character to steal gold.");
        // return;
        // }
        // }

        // 7. 如果没有找到最佳目标，寻找任何可行的目标
        // for (GameCharacter character : allCharacters) {
        // if (character.getRank() != this.getRank() &&
        // character.getRank() != 1 && !character.isAssassinated()) {
        // character.setRobbed(true);
        // System.out.println("The Thief choose a character to steal gold.");
        // return;
        // }
        // }
    }

    @Override
    public void collectIncome() {
        // TODO Auto-generated method stub
        return;
    }

}
