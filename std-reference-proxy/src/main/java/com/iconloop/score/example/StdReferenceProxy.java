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

public class StdReferenceProxy {
    private Address owner;
    private Address ref;

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

    public StdReferenceProxy(Address _ref) {
        this.owner = Context.getOrigin();
        this.ref = _ref;
    }

    @External()
    public void transferOwnership(Address newOwner) {
        Context.require(Context.getOrigin().equals(this.owner), "Origin is not the owner");
        this.owner = newOwner;
    }

    @External()
    public void setRef(Address newRef) {
        Context.require(Context.getOrigin().equals(this.owner), "Origin is not the owner");
        this.ref = newRef;
    }

    @External(readonly=true)
    public Address owner() {
        return this.owner;
    }

    @External(readonly=true)
    public Address ref() {
        return this.ref;
    }

    @External(readonly=true)
    public List<BigInteger> getReferenceData(String base, String quote) {
        return (List<BigInteger>) Context.call(List.class, this.ref, "getReferenceData", base, quote);
    }

    @External(readonly=true)
    public List<List<BigInteger>> getReferenceDataBulk(String[] bases, String[] quotes) {
        return (List<List<BigInteger>>) Context.call(List.class, this.ref, "getReferenceDataBulk", bases, quotes);
    }

    @External(readonly=true)
    public List<List<BigInteger>> _getReferenceDataBulk(String _bases, String _quotes) {
        String[] bases = _split(_bases, ",");
        String[] quotes = _split(_quotes, ",");
        return (List<List<BigInteger>>) Context.call(List.class, Context.getAddress(), "getReferenceDataBulk", bases, quotes);
    }

    @Payable
    public void fallback() {
        // just receive incoming funds
    }
}
