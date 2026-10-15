package citadels.Character;

import citadels.Player.*;
import citadels.Character.GameCharacter;
import citadels.Character.Warlord;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import citadels.Building.Building;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MerchantTest {
    // 测试数据
    private List<Player> players;
    private List<GameCharacter> characters;
    private Warlord warlord;
    private Player warlordPlayer;

    /**
     * 在每个测试前设置测试环境
     */
    @BeforeEach
    public void setUp() {
        // 初始化角色
        characters = new ArrayList<>();
        characters.add(new Assassin());
        characters.add(new Thief());
        characters.add(new Magician());
        characters.add(new King());
        characters.add(new Bishop());
        characters.add(new Merchant());
        characters.add(new Architect());
        characters.add(new Warlord());

        // 初始化玩家
        players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            // 创建一个匿名Player子类
            if (i == 0) {
                players.add(new HumanPlayer(i));
            } else {
                players.add(new AIPlayer(i));
            }
        }

        // 将角色分配给玩家
        for (int i = 0; i < players.size(); i++) {
            GameCharacter character = characters.get(i);
            character.setPlayer(players.get(i));
            players.get(i).setCharacter(character);
        }

        // 设置军阀和对应的玩家
        warlord = (Warlord) characters.get(7);
        warlordPlayer = players.get(3);
        warlord.setPlayer(warlordPlayer);
        warlordPlayer.setCharacter(warlord);

        // 为玩家0添加一些建筑
        players.get(0).getCity().add(new Building("Red Building 1", 1, "red", 3));
        players.get(0).getCity().add(new Building("Blue Building 1", 1, "blue", 4));

        // 为玩家1添加一些建筑
        players.get(1).getCity().add(new Building("Green Building 1", 1, "green", 2));
        players.get(1).getCity().add(new Building("Yellow Building 1", 1, "yellow", 5));

        // 为玩家2添加一些建筑
        players.get(2).getCity().add(new Building("Purple Building 1", 1, "purple", 6));
        players.get(2).getCity().add(new Building("Red Building 2", 1, "red", 2));

        // 为战争领主玩家添加一些红色建筑
        players.get(3).getCity().add(new Building("Red Military District 1", 1, "red", 3));
        players.get(3).getCity().add(new Building("Red Military District 2", 1, "red", 5));
    }

    @Test
    public void testWarlordIncome() {
        // 记录初始金币
        int initialGold = warlordPlayer.getGold();

        // 收集特殊收入
        warlord.collectIncome();

        // 验证：应该获得2金币(两个红色建筑)
        assertEquals(initialGold, warlordPlayer.getGold());
    }

    /**
     * 测试战争领主选择建筑的策略
     */
    @Test
    public void testWarlordBuildingStrategy() {
        // 初始化一些测试建筑
        List<Building> testHand = new ArrayList<>();
        testHand.add(new Building("Cheap Green Building", 1, "green", 1));
        testHand.add(new Building("Expensive Green Building", 1, "green", 5));
        testHand.add(new Building("Medium Red Building", 1, "red", 3));
        testHand.add(new Building("Expensive Red Building", 1, "red", 6));

        // 设置战争领主的手牌
        warlordPlayer.setHand(testHand);
        warlordPlayer.setGold(10); // 足够建造任何建筑

        // 让战争领主选择建筑
        Building chosenBuilding = warlord.chooseHumanBuilding("Expensive Red Building");

        // 验证：应该选择最贵的红色建筑
        assertNotNull(chosenBuilding);
        assertEquals("red", chosenBuilding.getColor());
        assertEquals(6, chosenBuilding.getCost());
    }
}
