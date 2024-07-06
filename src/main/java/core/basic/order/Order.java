package core.basic.order;

public class Order {
    private Long memberId;
    private String memberName;
    private int itemPrice;
    private int discountPrice;

    public Order(Long memberId, String memberName, int itemPrice, int discountPrice) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.itemPrice = itemPrice;
        this.discountPrice = discountPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", itemPrice=" + itemPrice +
                ", discountPrice=" + discountPrice +
                '}';
    }

    public int calculatePrice() {
        return itemPrice - discountPrice;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }
}
