package citadels.Player;

import java.util.*;
import citadels.Character.GameCharacter;

public class HumanPlayer extends Player {
    public HumanPlayer(int id) {
        super(id);
    }

    @Override
    public void chooseCharacter(List<GameCharacter> availableCharacters, Scanner scanner) {
        // 给一个提示
        System.out.println("It is your turn to choose a character.");

        // 因为选的角色可能不存在，所以我们要开启一个循环
        while (true) {
            System.out.print("> ");
            String input = scanner.next();
            // 需要都变小写操作
            // 开启循环查看是否有这个角色
            boolean found = false;
            for (int i = 0; i < availableCharacters.size(); i++) {
                GameCharacter gameCharacter = availableCharacters.get(i);
                if (gameCharacter.getName().toLowerCase().equals(input.toLowerCase())) {
                    this.setCharacter(gameCharacter);
                    gameCharacter.setPlayer(this);
                    availableCharacters.remove(i);
                    found = true;
                    break;
                }
            }
            // 如果找不到这个角色，就提示
            if (!found) {
                System.out.println("The character you inputted is not available.");
            } else {
                // 如果找到了就会退出循环
                break;
            }
        }

    }

    @Override
    public void takeTurn(Scanner scanner, List<Player> allPlayers, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        System.out.println("Your turn.");

        // executeHumanAction()
        this.getCharacter().executeHumanAction(scanner, characters);

        // 抽卡还是金币
        this.humanCardAndGold(scanner);
    }
}
