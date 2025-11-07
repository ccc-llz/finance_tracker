package com.elec5619p5g1.finance_tracker.Enum;

public enum Role {
    USER(0),
    ADMIN(1);

    int id;

    Role(int id) { this.id = id; }

    public int getId() { return id; }
}
