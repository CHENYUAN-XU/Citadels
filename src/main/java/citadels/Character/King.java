package citadels.Character;

import java.util.List;

import citadels.Player.Player;
import citadels.Building.Building;
import java.util.Scanner;

public class King extends GameCharacter {
    public King() {
        super(4, "King",
                "Gains one gold for each yellow (noble) district in their city. They receive the crown token and will be the first to choose characters on the next round.");
    }

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

        // 国王优先建造黄色(贵族)建筑，增加收入
        for (Building building : hand) {
            if (getPlayer().getGold() >= building.getCost() &&
                    building.getColor().equals("yellow")) {
                if (bestBuilding == null || building.getCost() > bestBuilding.getCost()) {
                    bestBuilding = building;
                }
            }
        }

        // 如果没有黄色建筑或无法支付，选择能支付的最贵建筑
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
        int count = 0;
        for (Building building : player.getCity()) {
            if (building.getColor().equals("yellow")) {
                count += 1;
            }
        }
        player.setGold(player.getGold() + count);
    }
}
