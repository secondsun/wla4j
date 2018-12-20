;from s2smsdisasm macros.asm

.macro wait_1s
    ld    b, Time_1Second
-:  ei
    halt
    djnz  -
.endm