;from s2smsdisasm macros.asm
.macro Engine_FillMemory args value, value2, value3
    ld    hl, $C001
    ld    de, $C002
    ld    bc, value2
    ld    (hl), value
    ldir
.endm
