package citadels.Game;

import citadels.Player.*;
import java.util.List;
import java.util.Scanner;

public class Command {

    public void hand(Player player) {

    }

    public void info(String command, Player player) {
        // 我们需要对 command 进行解析，然后判断是建筑还是角色
    }

    public void city(String command, List<Player> allPlayers) {
        String[] splitLine = command.split(" ");
        Player targetPlayer = null;
        if (splitLine.length == 1) {
            // 没有输入玩家序号
            for (Player player : allPlayers) {
                if (player instanceof HumanPlayer) {
                    targetPlayer = player;
                    break;
                }
            }
        } else {
            String playerIndex = splitLine[1];
            // 具体的方法来传入
            targetPlayer = allPlayers.get(Integer.parseInt(playerIndex) - 1);
        }

        // 使用 target player 来进行一些输出
    }

    public void gold(Player player) {

    }

    public void build(boolean humanRound, String command, Player player) {
        if (humanRound) {
            // 需要去获取到 build 后面的内容
            String buildingName = command.split(" ")[1];
            player.getCharacter().chooseHumanBuilding(buildingName);
        } else {
            System.out.println("It is not your turn.");
        }
    }

    public void help() {

    }

    public void debug() {
        // 等于 true 的时候，就变成 false
        // 等于 false 的时候，就变成 true
        // 取反值
        GameManager.debug = !GameManager.debug;
    }

    public void save(Scanner scanner, List<Player> players) {
        Player.save(scanner, players);
    }

    public void load(Scanner scanner, List<Player> players) {
        Player.load(scanner, players);
    }

    public void action() {

    }
}
