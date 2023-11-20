package Models;

public enum Ranking {
    BRONZE("Bronze"),
    SILVER("Silver"),
    GOLD("Gold"),
    PLATINUM("Platinum"),
    DIAMOND("Diamond"),
    MASTER("Master"),
    GRANDMASTER("Grandmaster"),
    CHALLENGER("Challenger");

    private final String value;

    private Ranking(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
