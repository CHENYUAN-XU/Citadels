package citadels.Character;

import java.util.List;
import java.util.Scanner;
import citadels.Player.Player;
import citadels.Building.Building;

public class Warlord extends GameCharacter {
    public Warlord() {
        super(8, "Warlord",
                "Gains one gold for each red (military) district in their city. You can destroy one district of your choice by paying one fewer gold than its building cost. You cannot destroy a district in a city with 8 or more districts.");
    }

    @Override
    public void executeAIAction(List<Player> allPlayers, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeAIAction'");
    }

    @Override
    public void executeHumanAction(Scanner scanner, List<GameCharacter> characters) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeHumanAction'");
    }

    @Override
    public void chooseAIBuilding() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chooseAIBuilding'");
    }

    @Override
    public void collectIncome() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'collectIncome'");
    }
}
