package citadels.Character;

import java.util.List;
import java.util.Scanner;
import citadels.Player.Player;
import citadels.Building.Building;

public class Bishop extends GameCharacter {
    public Bishop() {
        super(5, "Bishop", "");
    }

    // 参考 assassin class
    @Override
    public void executeAIAction(List<Player> allPlayers, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void executeHumanAction(Scanner scanner, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void chooseAIBuilding() {
        // TODO Auto-generated method stub
        List<Building> hand = getPlayer().getHand();
        Building bestBuilding = null;

        // 主教优先建造蓝色(宗教)建筑，增加收入
        for (Building building : hand) {
            if (getPlayer().getGold() >= building.getCost() &&
                    building.getColor().equals("blue")) {
                if (bestBuilding == null || building.getCost() > bestBuilding.getCost()) {
                    bestBuilding = building;
                }
            }
        }

        // 如果没有蓝色建筑或无法支付，选择能支付的最贵建筑
        if (bestBuilding == null) {
            for (Building building : hand) {
                if (getPlayer().getGold() >= building.getCost()) {
                    if (bestBuilding == null || building.getCost() > bestBuilding.getCost()) {
                        bestBuilding = building;
                    }
                }
            }
        }

        bestBuilding.substractQty(1);
        getPlayer().addCity(bestBuilding);
        getPlayer().setGold(getPlayer().getGold() - bestBuilding.getCost());
    }

    @Override
    public void collectIncome() {
        // TODO Auto-generated method stub
        Player player = this.getPlayer();
        // 主教的特殊收入：每个蓝色（宗教）地区获得1金币
        int blueBuildingCount = player.getBuildingsOfColor("blue").size();
        if (blueBuildingCount > 0) {
            player.setGold(player.getGold() + blueBuildingCount);
            System.out.println("Bishop obtain " + blueBuildingCount + " gold (from blue district).");
        }
    }

}