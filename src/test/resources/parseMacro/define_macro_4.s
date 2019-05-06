;from s2smsdisasm macros.asm
RepeatLast
.macro RepeatLast
    .db $E7, \1>>8
.endm