package edu.badpals.Enzium;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class TokenContract {

    private final PublicKey ownerPK;
    private final Address owner;
    private String name = null;
    private String symbol = "";
    private double totalSupply = 0d;
    private Double totalTokensSold = 0d;
    private Double tokenPrice = 0d;

    private final String REASONBECOUSE = "\"and, at least, it should be :\"";

    private final Map<PublicKey, Double> balances = new HashMap<PublicKey, Double>();

    /**
     * constructor
     */

    public TokenContract(Address owner) {
        this.owner = owner;
        this.ownerPK = owner.getPK();
    }

    /**
     * getters y setters
     */

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setTotalSupply(double totalSupply) {
        this.totalSupply = totalSupply;
    };

    public Address owner() {
        return this.owner;
    }

    public String name() {
        return this.name;
    }

    public String symbol() {
        return this.symbol;
    }

    public double totalSupply() {
        return this.totalSupply;
    }

    public void setTokenPrice(Double tokenPrice) {
        this.tokenPrice = tokenPrice;
    }

    public Double getTokenPrice() {
        return this.tokenPrice;
    }

    public Map<PublicKey, Double> getBalances() {
        return this.balances;
    }

    /**
     * Override
     */

    @Override
    public String toString() {
        return new StringBuilder()
                .append("\nname = ")
                .append(this.name())
                .append("\nsymbol = ")
                .append(this.symbol())
                .append("\ntotalSupply = ")
                .append(this.totalSupply())
                .append("\nowner PK = ")
                .append(this.ownerPK.hashCode()).toString();
    }

    /**
     * logica
     */

    public void addOwner(PublicKey PK, Double units) {
        getBalances().putIfAbsent(PK, units);
    }

    public int numOwners() {
        return this.getBalances().size();
    }

    public Double balanceOf(PublicKey owner) {
        return this.getBalances().getOrDefault(owner, 0d);
    }

    public void transfer(PublicKey recipient, Double units) {
        try {
            require(balanceOf(ownerPK) >= units);
            this.getBalances().compute(ownerPK, (pk, tokens) -> tokens - units);
            this.getBalances().put(recipient, balanceOf(recipient) + units);
        } catch (IllegalArgumentException e) {
            String msg_error = e.getMessage();
            StringBuilder error = new StringBuilder()
                    .append("There is no enough stock of tickets to resolve the transaction, and it failed because ")
                    .append(msg_error)
                    .append(" the stock was: ")
                    .append(balanceOf(ownerPK))
                    .append(REASONBECOUSE)
                    .append(units);
            System.out.println(error.toString());
        }
    }

    public void transfer(PublicKey sender, PublicKey recipient, Double units) {
        try {
            require(balanceOf(sender) >= units);
            this.getBalances().put(sender, balanceOf(sender) - units);
            this.getBalances().put(recipient, balanceOf(recipient) + units);
        } catch (IllegalArgumentException e) {
            String mesg_error = e.getMessage();
            StringBuilder error = new StringBuilder()
                    .append("There is no enough stock of tickets to resolve the transaction, and it failed because\"")
                    .append(mesg_error)
                    .append(" the stock was ")
                    .append(balanceOf(sender))
                    .append(REASONBECOUSE)
                    .append(units);
            System.out.println(error.toString());
        }
    }

    public void payable(PublicKey recipient, Double enziniums) {
        try {
            require(enziniums >= this.getTokenPrice());
            Double units = Math.floor(enziniums / tokenPrice);
            transfer(recipient, units);
            this.owner.transferEZI(enziniums);
        } catch (IllegalArgumentException e) {
            String msg_error = e.getMessage();
            StringBuilder error = new StringBuilder()
                    .append("You have not enough enziniums to buy the tickets, and it failed because ")
                    .append(msg_error)
                    .append(" your net worth was: ")
                    .append(this.owner.getBalance())
                    .append(REASONBECOUSE)
                    .append(this.getTokenPrice());
            System.out.println(error.toString());
        }
    }
    private void require(Boolean holds) throws IllegalArgumentException {
        if (Boolean.FALSE.equals(holds)) {
            throw new IllegalArgumentException("The arguments provided did not met the conditions to be executed,");
        }
    }

    public String owners() {
        StringBuilder owners = new StringBuilder();
        for (PublicKey pk : this.getBalances().keySet()) {
            if (!pk.equals(this.ownerPK)) {
                owners.append("Owner: ")
                        .append(pk.hashCode())
                        .append("\s")
                        .append(this.getBalances().get(pk))
                        .append("\s")
                        .append(this.symbol())
                        .append("\n");
            }
        }
        return owners.toString();
    }

    public int totalTokensSold() {
        this.getBalances().forEach((pk, units) -> this.totalTokensSold += units);
        this.totalTokensSold -= balanceOf(ownerPK);
        return this.totalTokensSold.intValue();
    }


}
