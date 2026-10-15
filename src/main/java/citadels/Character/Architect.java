package citadels.Character;

import java.util.List;
import java.util.Scanner;
import java.util.Random;

import citadels.Player.Player;
import citadels.Building.Building;
import citadels.Game.GameManager;

public class Architect extends GameCharacter {
    private Random random;

    public Architect() {
        super(7, "Architect", "Gains two extra district cards. Can build up to 3 districts per turn.");
        this.random = new Random();
    }

    // 参考 assassin class
    @Override
    public void executeAIAction(List<Player> allPlayers, List<GameCharacter> characters) {
        // 从所有的 building 中建造三个
        for (int i = 0; i < 3; i++) {
            List<Building> allBuildings = this.getPlayer().getHand();
            int randomIndex = this.random.nextInt(allBuildings.size());
            Building selectedCard = allBuildings.get(randomIndex);
            if (selectedCard.getQty() > 0) {
                this.getPlayer().addCity(selectedCard);
                selectedCard.substractQty(1);
                this.getPlayer().setGold(this.getPlayer().getGold() - selectedCard.getCost());
            }
        }
    }

    @Override
    public void executeHumanAction(Scanner scanner, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        // 从所有的 building 中建造三个
        int count = 0;
        while (count < 3) {
            List<Building> allBuildings = this.getPlayer().getHand();
            System.out.println("You have the following buildings in your hand:");
            for (int j = 0; j < allBuildings.size(); j++) {
                System.out.println((j + 1) + ". " + allBuildings.get(j).getName());
            }
            System.out.println("Please enter the number of the building you want to build:");
            int buildingIndex = scanner.nextInt() - 1;
            Building selectedCard = allBuildings.get(buildingIndex);
            if (selectedCard.getQty() > 0) {
                this.getPlayer().addCity(selectedCard);
                selectedCard.substractQty(1);
                this.getPlayer().setGold(this.getPlayer().getGold() - selectedCard.getCost());
                count++;
            } else {
                System.out.println("You don't have enough buildings in your hand.");
            }
        }
    }

    @Override
    public void chooseAIBuilding() {
        // TODO Auto-generated method stub
        List<Building> hand = getPlayer().getHand();
        int goldAvailable = getPlayer().getGold();
        Building bestBuilding = null;

        // 建筑师策略：优先建造低成本建筑，因为可以建造多个
        // 但如果快接近游戏结束，则优先建造最贵的建筑

        // 检查是否接近游戏结束（城市已有6个或以上建筑）
        boolean nearEndGame = getPlayer().getCity().size() >= 6;

        if (nearEndGame) {
            // 游戏后期，优先建造最贵的建筑
            int highestCost = -1;
            for (Building building : hand) {
                if (goldAvailable >= building.getCost()) {
                    if (bestBuilding == null || building.getCost() > highestCost) {
                        bestBuilding = building;
                        highestCost = building.getCost();
                    }
                }
            }
        } else {
            // 游戏前期，优先建造便宜的建筑，以便最大化每回合的建造数量
            int lowestCost = Integer.MAX_VALUE;
            for (Building building : hand) {
                if (goldAvailable >= building.getCost()) {
                    if (bestBuilding == null || building.getCost() < lowestCost) {
                        bestBuilding = building;
                        lowestCost = building.getCost();
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
        return;
    }

}
