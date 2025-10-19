package com.laraid.vci.enums;

public enum CardStatus {
    ISSUED,
    ACTIVE,
    BLOCKED,
    EXPIRED,
    DEACTIVATED,
    SUSPENDED,
    CLOSED;

    public boolean canActivate() {
        return this == ISSUED;
    }
    public boolean isUsable() {
        return this == ACTIVE;
    }

    public boolean isTerminal() {
        return this == CLOSED || this == EXPIRED;
    }
}
