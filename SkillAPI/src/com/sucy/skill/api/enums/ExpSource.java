package com.sucy.skill.api.enums;

public enum ExpSource
{

    MOB            (0x1),
    BLOCK_BREAK    (0x10),
    BLOCK_PLACE    (0x100),
    CRAFT          (0x1000),
    COMMAND        (0x10000),
    SPECIAL        (0x100000),
    INITIALIZATION (0x1000000);

    private int id;

    private ExpSource(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
}
