package br.com.smartloto.domain;

public enum LotteryType {
    MEGA_SENA(6, 60),
    LOTOFACIL(15, 25);

    private final int quantity;
    private final int maxNumber;

    LotteryType(int quantity, int maxNumber) {
        this.quantity = quantity;
        this.maxNumber = maxNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMaxNumber() {
        return maxNumber;
    }
}
