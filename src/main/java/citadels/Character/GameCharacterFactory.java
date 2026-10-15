package citadels.Character;

import java.util.*;
import citadels.Character.*;

/**
 * 角色工厂类，负责创建和管理游戏中的角色
 */
public class GameCharacterFactory {

    public static List<GameCharacter> createAllCharacters() {
        List<GameCharacter> characters = new ArrayList<>();
        // 按照顺序 add
        characters.add(new Assassin());
        characters.add(new Thief());
        characters.add(new Magician());
        characters.add(new King());
        characters.add(new Bishop());
        characters.add(new Merchant());
        characters.add(new Architect());
        characters.add(new Warlord());

        return characters;
    }
}
