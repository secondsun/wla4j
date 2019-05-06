;from s2smsdisasm macros.asm
.macro RepeatLast
    .db $E7, \1>>8
.endm