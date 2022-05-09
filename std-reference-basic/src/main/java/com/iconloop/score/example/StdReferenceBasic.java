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
    public List<BigInteger> getRefData(String symbol) {
        if (symbol.equals("USD")) {
            return List.of(this.E9, BigInteger.valueOf(Context.getBlockTimestamp()), BigInteger.ZERO);
        }

        BigInteger rate = rates.getOrDefault(symbol, BigInteger.ZERO);
        BigInteger resolveTime = resolveTimes.getOrDefault(symbol, BigInteger.ZERO);
        BigInteger requestID = requestIDs.getOrDefault(symbol, BigInteger.ZERO);

        Context.require(resolveTime.compareTo(BigInteger.ZERO) > 0, "REFDATANOTAVAILABLE");

        return List.of(rate, resolveTime, requestID);
    }

    @External(readonly=true)
    public List<BigInteger> getReferenceData(String base, String quote) {
        List<?> b = Context.call(List.class, Context.getAddress(), "getRefData", base);
        List<?> q = Context.call(List.class, Context.getAddress(), "getRefData", quote);
        BigInteger b0 = (BigInteger) b.get(0);
        BigInteger b1 = (BigInteger) b.get(1);
        BigInteger q0 = (BigInteger) q.get(0);
        BigInteger q1 = (BigInteger) q.get(1);
        return List.of(b0.multiply(this.E9.multiply(this.E9)).divide(q0), b1, q1);
    }

    @External(readonly=true)
    public List<List<BigInteger>> getReferenceDataBulk(String[] bases, String[] quotes) {
        Context.require(bases.length == quotes.length, "Size of bases and quotes must be equal");
        List<BigInteger>[] acc = new List[bases.length];
        for (int i = 0; i < bases.length; i++) {
            acc[i] = (List<BigInteger>) Context.call(List.class, Context.getAddress(), "getReferenceData", bases[i], quotes[i]);
        }
        return List.of(acc);
    }

    @External(readonly=true)
    public List<List<BigInteger>> _getReferenceDataBulk(String _bases, String _quotes) {
        String[] bases = _split(_bases, ",");
        String[] quotes = _split(_quotes, ",");
        return (List<List<BigInteger>>) Context.call(List.class, Context.getAddress(), "getReferenceDataBulk", bases, quotes);
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
    public void relay(String _symbols, String _rates, String _resolveTimes, String _requestIDs) {
        Context.require(this.isRelayer.getOrDefault(Context.getOrigin(), false), "NOTARELAYER");

        String[] symbols = _split(_symbols, ",");
        String[] rates = _split(_rates, ",");
        String[] resolveTimes = _split(_resolveTimes, ",");
        String[] requestIDs = _split(_requestIDs, ",");
        int len = symbols.length;

        Context.require(rates.length == len, "BADRATESLENGTH");
        Context.require(resolveTimes.length == len, "BADRESOLVETIMESLENGTH");
        Context.require(requestIDs.length == len, "BADREQUESTIDSLENGTH");

        for (int idx = 0; idx < len; idx++) {
            this.rates.set(symbols[idx], new BigInteger(rates[idx]));
            this.resolveTimes.set(symbols[idx], new BigInteger(resolveTimes[idx]).multiply(new BigInteger("1000000")));
            this.requestIDs.set(symbols[idx], new BigInteger(requestIDs[idx]));

            RefDataUpdate(
                symbols[idx],
                rates[idx],
                resolveTimes[idx],
                requestIDs[idx]
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
