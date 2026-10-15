package citadels.Character;

import java.util.List;
import java.util.Scanner;
import citadels.Player.Player;
import citadels.Building.Building;

public abstract class GameCharacter {
    /* 角色的排名 */
    private int rank;

    /* 角色的名称 */
    private String name;

    /* 角色是否被刺杀 */
    private boolean isAssassinated;

    /* 角色是否被盗窃 */
    private boolean isStolen;

    /* 角色的 player */
    private Player player;

    /* 角色的描述 */
    private String description;

    /* 已经建造的数量 */
    private int builtCount;

    public GameCharacter(int rank, String name, String description) {
        this.rank = rank;
        this.name = name;
        this.isAssassinated = false;
        this.isStolen = false;
        this.player = null;
        this.description = description;
        this.builtCount = 0;
    }

    public abstract void executeAIAction(List<Player> allPlayers, List<GameCharacter> characters);

    public abstract void executeHumanAction(Scanner scanner, List<GameCharacter> characters);

    public abstract void collectIncome();

    public abstract void chooseAIBuilding();

    public Building chooseHumanBuilding(String buildingName) {
        // 判断玩家的 gold 是否足够
        if (this.getName().equals("Architect")) {
            if (this.builtCount >= 3) {
                System.out.println("You have built 3 buildings, you can't build anymore.");
                return null;
            }
        } else {
            if (this.builtCount >= 1) {
                System.out.println("You have built 1 building, you can't build anymore.");
                return null;
            }
        }

        List<Building> allHand = this.player.getHand();
        Building hand = null;
        for (Building building : allHand) {
            if (building.getName().equals(buildingName)) {
                hand = building;
            }
        }

        if (hand != null && this.player.getGold() >= hand.getCost()) {
            this.player.setGold(this.player.getGold() - hand.getCost());
            hand.substractQty(1);
            this.player.addCity(hand);
            return hand;
        } else {
            System.out.println("You don't have enough gold to build this building.");
        }
        return null;
    }

    public int getRank() {
        return this.rank;
    }

    public String getName() {
        return this.name;
    }

    public boolean getIsAssassinated() {
        return this.isAssassinated;
    }

    public void setIsAssassinated(boolean isAssassinated) {
        this.isAssassinated = isAssassinated;
    }

    public boolean getIsStolen() {
        return this.isStolen;
    }

    public void setIsStolen(boolean isStolen) {
        this.isStolen = isStolen;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getDescription() {
        return this.description;
    }

    public int getBuiltCount() {
        return this.builtCount;
    }

    public void setBuiltCount(int builtCount) {
        this.builtCount = builtCount;
    }
}
