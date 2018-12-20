;from s2smsdisasm macros.asm
.macro Engine_FillMemory args value
    ld    hl, $C001
    ld    de, $C002
    ld    bc, $1FEE
    ld    (hl), value
    ldir
.endm
