package citadels.Character;

import java.util.*;
import citadels.Player.Player;
import citadels.Building.Building;
import java.util.Scanner;
import citadels.Game.GameManager;

public class Magician extends GameCharacter {
    public Magician() {
        super(3, "Magician",
                "Can either exchange their hand with another player’s, or discard any number of district cards face down to the bottom of the deck and draw an equal number of cards from thedistrict deck (can only do this once per turn).");
    }

    @Override
    public void executeAIAction(List<Player> allPlayers, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        // AI魔术师决策逻辑
        Player myPlayer = this.getPlayer();
        int handSize = myPlayer.getHand().size();

        // 1. 分析自己的手牌情况
        int handValue = 0; // 总金额
        int lowValueCards = 0; // 低价值卡牌(成本<3)
        int highValueCards = 0; // 高价值卡牌(成本>=4)
        boolean hasExpensiveCards = false; // 是否有高成本牌(成本>=5)

        for (Building card : myPlayer.getHand()) {
            handValue += card.getCost();
            if (card.getCost() < 3) {
                lowValueCards++;
            } else if (card.getCost() >= 4) {
                highValueCards++;
            }

            if (card.getCost() >= 5) {
                hasExpensiveCards = true;
            }
        }

        // 计算平均手牌价值
        double avgCardValue = handSize > 0 ? (double) handValue / handSize : 0;

        // 2. 分析其他玩家情况
        Player bestExchangeTarget = null;
        int bestHandSize = -1;
        Player richestPlayer = null;
        int maxGold = -1;

        for (Player player : allPlayers) {
            if (player.getId() == myPlayer.getId())
                continue; // 跳过自己

            int playerHandSize = player.getHand().size();
            int playerGold = player.getGold();

            // 寻找手牌最多的玩家
            if (playerHandSize > bestHandSize) {
                bestHandSize = playerHandSize;
                bestExchangeTarget = player;
            }

            // 寻找金币最多的玩家(可能意味着他们有高价值手牌)
            if (playerGold > maxGold) {
                maxGold = playerGold;
                richestPlayer = player;
            }
        }

        // 3. 决策逻辑

        // 情况A: 如果我们手牌少或者手牌价值低，考虑与手牌多的玩家交换
        if ((handSize <= 1 || avgCardValue < 3.0) && bestExchangeTarget != null && bestHandSize > handSize + 1) {
            // 交换手牌策略 - 当我们手牌较少且对方手牌较多
            System.out.println("Magician choose to exchange hand with " + bestExchangeTarget.getCharacter().getName());

            // 执行交换
            List<Building> myHand = new ArrayList<>(myPlayer.getHand());
            List<Building> targetHand = new ArrayList<>(bestExchangeTarget.getHand());

            myPlayer.getHand().clear();
            myPlayer.setHand(targetHand);

            bestExchangeTarget.getHand().clear();
            bestExchangeTarget.setHand(myHand);
            return;
        }

        // 情况B: 如果我们有足够的金币，但手牌质量不佳，考虑与金币最多的玩家交换
        if (myPlayer.getGold() >= 5 && avgCardValue < 3.5 && richestPlayer != null &&
                richestPlayer.getHand().size() >= 2) {
            // 富有玩家可能保留了高价值建筑牌
            System.out.println("Magician choose to exchange hand with " + richestPlayer.getCharacter().getName());

            // 执行交换
            List<Building> myHand = new ArrayList<>(myPlayer.getHand());
            List<Building> targetHand = new ArrayList<>(richestPlayer.getHand());

            myPlayer.getHand().clear();
            myPlayer.setHand(targetHand);

            richestPlayer.getHand().clear();
            richestPlayer.setHand(myHand);
            return;
        }

        // 情况C: 如果有很多低价值卡牌，考虑弃牌并抽取新卡
        if (lowValueCards >= 2 && handSize >= 3) {
            System.out.println("Magician choose to discard and draw new cards");

            // 找出价值低的卡牌进行弃置
            List<Building> hand = myPlayer.getHand();
            List<Building> cardsToDiscard = new ArrayList<>();

            for (Building card : hand) {
                // 低价值的卡牌
                if (card.getCost() < 3) {
                    cardsToDiscard.add(card);
                }
            }

            // 从手牌中移除弃置的卡牌
            if (!cardsToDiscard.isEmpty()) {
                hand.removeAll(cardsToDiscard);
                System.out
                        .println("Magician discard " + cardsToDiscard.size() + " low value cards, and draw new cards.");
                // 随机生成等量的新卡
                int i = 0;
                while (i < cardsToDiscard.size()) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(GameManager.allBuildingsCards.size());
                    Building selectedCard = GameManager.allBuildingsCards.get(randomIndex);
                    // 如果 building 中没有足够的数量，则随机生成等量的新卡
                    if (selectedCard.getQty() > 0) {
                        selectedCard.substractQty(1);
                        myPlayer.addHand(selectedCard);
                        i++;
                    }
                }
            }
            return;
        }

        // 情况D: 如果我们手牌很好(有高价值卡牌)但数量不够，考虑弃置1张低价值卡牌
        if (hasExpensiveCards && handSize < 3 && lowValueCards >= 1) {
            System.out.println("Magician choose to discard some low value cards to draw new cards");

            // 只弃置最多2张低价值卡牌
            List<Building> hand = myPlayer.getHand();
            List<Building> cardsToDiscard = new ArrayList<>();
            int discardCount = 0;

            for (Building card : hand) {
                if (card.getCost() < 3 && discardCount < 2) {
                    cardsToDiscard.add(card);
                    discardCount++;
                }
            }

            if (!cardsToDiscard.isEmpty()) {
                hand.removeAll(cardsToDiscard);
                System.out.println("Magician choose to discard some low value cards to draw new cards");
                // 随机生成等量的新卡
                int i = 0;
                while (i < cardsToDiscard.size()) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(GameManager.allBuildingsCards.size());
                    Building selectedCard = GameManager.allBuildingsCards.get(randomIndex);
                    // 如果 building 中没有足够的数量，则随机生成等量的新卡
                    if (selectedCard.getQty() > 0) {
                        myPlayer.addCity(selectedCard);
                        i++;
                    }
                }
            }
            return;
        }

        // 情况E: 默认情况，查看是否有任何玩家手牌明显比我们多
        if (bestExchangeTarget != null && bestHandSize > handSize + 2) {
            System.out.println("Magician choose to exchange hand with " + bestExchangeTarget.getCharacter().getName());

            // 执行交换
            List<Building> myHand = new ArrayList<>(myPlayer.getHand());
            List<Building> targetHand = new ArrayList<>(bestExchangeTarget.getHand());

            myPlayer.getHand().clear();
            myPlayer.setHand(targetHand);

            bestExchangeTarget.setHand(myHand);
        } else {
            System.out.println("Magician choose not to use special ability, current hand is good.");
        }
    }

    @Override
    public void executeHumanAction(Scanner scanner, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        List<Player> players = new ArrayList<>();
        for (GameCharacter character : characters) {
            if (character.getPlayer() != null) {
                players.add(character.getPlayer());
            }
        }
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (p != this.getPlayer()) {
                System.out.println((i + 1) + ". " + p.getId() + " (手牌数量: " + p.getHand().size() + ")");
            }
        }

        int choice = scanner.nextInt();

        if (choice > 0 && choice <= players.size()) {
            Player targetPlayer = players.get(choice - 1);
            if (targetPlayer != this.getPlayer()) {
                List<Building> myHand = new ArrayList<>(this.getPlayer().getHand());
                List<Building> targetHand = new ArrayList<>(targetPlayer.getHand());

                this.getPlayer().getHand().clear();
                this.getPlayer().setHand(targetHand);

                targetPlayer.setHand(myHand);

                System.out.println("成功与 " + targetPlayer.getId() + " 交换手牌！");
            } else {
                System.out.println("不能与自己交换手牌！");
            }
        } else {
            System.out.println("无效选择！");
        }
    }

    @Override
    public void chooseAIBuilding() {
        // TODO Auto-generated method stub
        List<Building> hand = getPlayer().getHand();
        int goldAvailable = getPlayer().getGold();
        Building bestBuilding = null;
        boolean nearEndGame = getPlayer().getCity().size() >= 6;

        // 魔术师策略：平衡资源使用
        // 1. 首先考虑中等成本(3-4)的建筑
        for (Building building : hand) {
            if (goldAvailable >= building.getCost() &&
                    building.getCost() >= 3 && building.getCost() <= 4) {
                if (bestBuilding == null ||
                        (nearEndGame && building.getCost() > bestBuilding.getCost()) ||
                        (!nearEndGame && building.getCost() < bestBuilding.getCost())) {
                    bestBuilding = building;
                }
            }
        }

        // 2. 如果没有找到合适的中等成本建筑，选择当前能支付的最贵建筑
        if (bestBuilding == null) {
            for (Building building : hand) {
                if (goldAvailable >= building.getCost()) {
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
        return;
    }

}
