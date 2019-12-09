package sirs.spykid.guardian.model;

import java.security.Key;

import sirs.spykid.util.ChildId;
import sirs.spykid.util.SharedKey;

public class BeaconUser {

    private Key key;
    private ChildId child;

    public BeaconUser(SharedKey key, ChildId childId) {
        this.key = key;
        this.child = child;
    }

    public Key getKey() {
        return key;
    }

    public ChildId getChild() {
        return child;
    }
}
