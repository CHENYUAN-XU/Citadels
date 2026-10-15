package citadels.Character;

import java.util.List;
import java.util.Scanner;
import citadels.Player.Player;
import citadels.Building.Building;

public class Merchant extends GameCharacter {
    public Merchant() {
        super(6, "Merchant", "Gains one gold for each green (trade) district in their city. Gains one extra gold.");
    }

    @Override
    public void executeAIAction(List<Player> allPlayers, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        Player player = this.getPlayer();
        player.setGold(player.getGold() + 1);
    }

    @Override
    public void executeHumanAction(Scanner scanner, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        Player player = this.getPlayer();
        player.setGold(player.getGold() + 1);

    }

    @Override
    public void chooseAIBuilding() {
        // TODO Auto-generated method stub
        List<Building> hand = getPlayer().getHand();
        Building bestBuilding = null;

        // 商人优先建造绿色(商业)建筑，增加收入
        for (Building building : hand) {
            if (getPlayer().getGold() >= building.getCost() &&
                    building.getColor().equals("green")) {
                if (bestBuilding == null || building.getCost() > bestBuilding.getCost()) {
                    bestBuilding = building;
                }
            }
        }

        // 如果没有绿色建筑或无法支付，选择能支付的最贵建筑
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

        // 商人的特殊收入：每个绿色（商业）地区获得1金币
        int greenBuildingCount = player.getBuildingsOfColor("green").size();
        if (greenBuildingCount > 0) {
            player.setGold(player.getGold() + greenBuildingCount);
            System.out.println("Merchant obtain " + greenBuildingCount + " gold (from green district).");
        }
    }

}
