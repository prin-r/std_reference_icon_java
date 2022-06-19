package com.iconloop.score.example;

import score.Address;
import score.Context;
import score.DictDB;
import score.VarDB;
import score.annotation.EventLog;
import score.annotation.External;
import score.annotation.Payable;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class StdReferenceBasic {
    private Address owner;
    private final BigInteger E9;
    private final DictDB<Address, Boolean> isRelayer;
    private final DictDB<String, BigInteger> rates;
    private final DictDB<String, BigInteger> resolveTimes;
    private final DictDB<String, BigInteger> requestIDs;

    private String[] _split(String s, String sep) {
        int sepCount = 1;
        for (int i = 0; i < s.length(); i++) {
            if (s.substring(i, i + 1).equals(sep)) {
                sepCount++;
            }
        }

        String[] tmp = new String[sepCount];
        int j = 0;
        while (s.length() > 0) {
            int i = s.indexOf(sep);
            if (i < 0) {
                tmp[j] = s;
                break;
            } else {
                tmp[j] = s.substring(0, i);
            }
            s = s.substring(i + 1);
            j++;
        }
        return tmp;
    }

    public StdReferenceBasic() {
        this.owner = Context.getOrigin();
        this.isRelayer = Context.newDictDB("isRelayer", Boolean.class);
        this.rates = Context.newDictDB("rates", BigInteger.class);
        this.resolveTimes = Context.newDictDB("resolveTimes", BigInteger.class);
        this.requestIDs = Context.newDictDB("requestIDs", BigInteger.class);

        this.isRelayer.set(this.owner, true);
        this.E9 = new BigInteger("1000000000");
    }

    @External(readonly=true)
    public Address owner() {
        return this.owner;
    }

    @External(readonly=true)
    public String isRelayer(Address relayer) {
        return this.isRelayer.getOrDefault(relayer, false) ? "YES": "NO";
    }

    @External(readonly=true)
    public Map<String, BigInteger> getRefData(String symbol) {
        if (symbol.equals("USD")) {
            return Map.of(
                "rate",
                this.E9,
                "last_update",
                BigInteger.valueOf(Context.getBlockTimestamp()),
                "request_id",
                BigInteger.ZERO
            );
        }

        BigInteger rate = rates.getOrDefault(symbol, BigInteger.ZERO);
        BigInteger resolveTime = resolveTimes.getOrDefault(symbol, BigInteger.ZERO);
        BigInteger requestID = requestIDs.getOrDefault(symbol, BigInteger.ZERO);

        Context.require(resolveTime.compareTo(BigInteger.ZERO) > 0, "REFDATANOTAVAILABLE");

        return Map.of(
            "rate",
            rate,
            "last_update",
            resolveTime,
            "request_id",
            requestID
        );
    }

    @External(readonly=true)
    public Map<String, BigInteger> get_reference_data(String base, String quote) {
        Map<String,?> b = Context.call(Map.class, Context.getAddress(), "getRefData", base);
        Map<String,?> q = Context.call(Map.class, Context.getAddress(), "getRefData", quote);
        BigInteger b0 = (BigInteger) b.get("rate");
        BigInteger b1 = (BigInteger) b.get("last_update");
        BigInteger q0 = (BigInteger) q.get("rate");
        BigInteger q1 = (BigInteger) q.get("last_update");

        return Map.of(
            "rate",
            b0.multiply(this.E9.multiply(this.E9)).divide(q0),
            "last_update_base",
            b1,
            "last_update_quote",
            q1
        );
    }

    @External(readonly=true)
    public List<Map<String, BigInteger>> get_reference_data_bulk(String[] bases, String[] quotes) {
        Context.require(bases.length == quotes.length, "Size of bases and quotes must be equal");
        Map<String, BigInteger>[] acc = new Map[bases.length];
        for (int i = 0; i < bases.length; i++) {
            acc[i] = (Map<String, BigInteger>) Context.call(Map.class, Context.getAddress(), "get_reference_data", bases[i], quotes[i]);
        }
        return List.of(acc);
    }

    @External(readonly=true)
    public List<Map<String, BigInteger>> _get_reference_data_bulk(String _bases, String _quotes) {
        String[] bases = _split(_bases, ",");
        String[] quotes = _split(_quotes, ",");
        return (List<Map<String, BigInteger>>) Context.call(List.class, Context.getAddress(), "get_reference_data_bulk", bases, quotes);
    }

    @External()
    public void transferOwnership(Address newOwner) {
        Context.require(Context.getOrigin().equals(this.owner), "Origin is not the owner");
        this.owner = newOwner;
    }

    @External()
    public void addRelayer(Address relayer) {
        Context.require(Context.getOrigin().equals(this.owner), "Origin is not the owner");
        this.isRelayer.set(relayer, true);
    }

    @External()
    public void removeRelayer(Address relayer) {
        Context.require(Context.getOrigin().equals(this.owner), "Origin is not the owner");
        this.isRelayer.set(relayer, false);
    }

    @External
    public void relay(String _symbols, String _rates, String resolveTime, String requestID) {
        Context.require(this.isRelayer.getOrDefault(Context.getOrigin(), false), "NOTARELAYER");

        String[] symbols = _split(_symbols, ",");
        String[] rates = _split(_rates, ",");

        Context.require(rates.length == symbols.length, "BADRATESLENGTH");

        for (int idx = 0; idx < symbols.length; idx++) {
            this.rates.set(symbols[idx], new BigInteger(rates[idx]));
            this.resolveTimes.set(symbols[idx], new BigInteger(resolveTime).multiply(new BigInteger("1000000")));
            this.requestIDs.set(symbols[idx], new BigInteger(requestID));

            RefDataUpdate(
                symbols[idx],
                rates[idx],
                resolveTime,
                requestID
            );
        }
    }

    @Payable
    public void fallback() {
        // just receive incoming funds
    }

    @EventLog
    protected void RefDataUpdate(String symbol, String rate, String resolveTime, String requestID) {}
}
