package citadels.Building;

/** 这个你普通的建筑就可以用了 */
public class Building implements SpecialAbility {
    /* 建筑的名称 */
    private String name;

    /* 建筑的数量 */
    private int qty;

    /* 建筑的颜色 */
    private String color;

    /* 建筑的花费 */
    private int cost;

    /* 建筑的描述 */
    private String description;

    public Building(String name, int qty, String color, int cost) {
        this.name = name;
        this.qty = qty;
        this.color = color;
        this.cost = cost;
    }

    public Building(String name, int qty, String color, int cost, String description) {
        this.name = name;
        this.qty = qty;
        this.color = color;
        this.cost = cost;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public int getQty() {
        return this.qty;
    }

    public void substractQty(int qty) {
        this.qty -= qty;
    }

    public void addQty(int qty) {
        this.qty += qty;
    }

    public String getColor() {
        return this.color;
    }

    public int getCost() {
        return this.cost;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public void specialEffect() {
        // TODO Auto-generated method stub
        if (this.name == "Haunted City") {
        }

        if (this.name == "Keep") {

        }
    }
}
