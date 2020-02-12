;=== Include MemoryMap, VectorTable, HeaderInfo ===

; == HiRom ========================= 

;.MEMORYMAP             ; Tell WLA that the SNES has ROM at locations 0000-$FFFF in every bank
;SLOTSIZE $10000        ; and that this area is $10000 bytes in size.
;DEFAULTSLOT 0          ; There is only a single slot in SNES, other consoles
;SLOT 0 $0000           ;	may have more slots per bank.
;.ENDME

;.ROMBANKSIZE $10000    ; Every ROM bank is 64 KBytes in size, also necessary.
;.ROMBANKS 4           ; 2 Mbit -- Tells WLA that you want to use 4 ROM banks.
;.DEFINE HEADER_OFF $8000
; ===========================

; == LoRom ========================= 

.MEMORYMAP              ; Tell WLA that the SNES has ROM at locations ;$8000-$FFFF in every bank
SLOTSIZE $8000          ; and that this area is $8000 bytes in size.
DEFAULTSLOT 0           ; There is only a single slot in SNES, other consoles
SLOT 0 $8000            ;       may have more slots per bank.
.ENDME

.ROMBANKSIZE $8000      ; Every ROM bank is 32 KBytes in size
.ROMBANKS 4             ; 1Mbit -- Tells WLA that you want to use 4 ROM banks.
.DEFINE HEADER_OFF $0000
; ===========================

;-------------------------------------------------------------------------------

.BANK 0 SLOT 0	; The SLOT 0 may be omitted, as SLOT 0 is the DEFAULTSLOT
; === Cartridge Header - part 1 - =====================
.ORG    $7FB0 + HEADER_OFF
.DB     "00"                        ; New Licensee Code
.DB     "SNES"                      ; ID
.ORG    $7FC0 + HEADER_OFF
.DB     "SuperFX Render Test 1"     ; Title (can't be more than 21 bytes, and should probably be a full 21 bytes)
;       "123456789012345678901"
.ORG    $7FD5 + HEADER_OFF
.DB     $20                         ; Memory Mode   ( $20 = Slow LoRom, $21 = Slow HiRom )


.BANK 0 SLOT 0
; === Cartridge Header - part 2 - =====================
.ORG    $7FD6 + HEADER_OFF
.DB     $14                   ; Contents ( $13 = GSU + ROM only, $14 = GSU + ROM and RAM, $15 = GSU + ROM, RAM and Save RAM)
.DB     $08                   ; ROM Size   ( $08 = 2 Mbit, $09 = 4 Mbit, $0A = 8Mbit, $0B = 16Mbit ... etc )
.DB     $00                   ; SRAM Size ( $00 = 0 bits, $01 = 16 kbits, $02 = 32 kbits, $03 = 64 kbits )
.DB     $00                   ; Country ( $01 = USA )
.DB     $33                   ; Licensee Code
.DB     $00                   ; Version
.DW    $FC93                  ; Checksum Complement  (not calculated ... but who cares?)
.DW    $036C                  ; Checksum


.BANK 0 SLOT 0
; === Interrupt Vector Table ====================
.ORG    $7FE4 + HEADER_OFF   ; === Native Mode ===
.DW     EmptyHandler          ; COP
.DW     EmptyHandler          ; BRK
.DW     EmptyHandler          ; ABORT
.DW     EmptyVBlank           ; NMI
.DW     $0000                 ; (Unused)
.DW     Interrupt             ; IRQ

.ORG    $7FF4 + HEADER_OFF   ; === Emulation Mode ===
.DW     EmptyHandler          ; COP
.DW     $0000                 ; (Unused)
.DW     EmptyHandler          ; ABORT
.DW     EmptyHandler          ; NMI
.DW     Main                  ; RESET
.DW     EmptyHandler          ; IRQ/BRK

; ============================================


.BANK 0 SLOT 0
.org HEADER_OFF
.SECTION "EmptyVectors" SEMIFREE

EmptyHandler:
	rep #$30
	pha

	sep #$20
	lda $4211		;clear IRQ Flag

	rep #$30
	pla
	rti

EmptyVBlank:
	rep #$30
	pha

	sep #$20
	lda $4210		;clear NMI Flag

	rep #$30
	pla
	rti

.ENDS

.EMPTYFILL $FF

;=== Include Library Routines & Macros ===
.INCLUDE "InitSNES.asm"

.DEFINE sbase			$00			; screen base register value
.DEFINE screen_base		$0000.w		; screen base address in Game Pak RAM ($400*sbase)
.DEFINE WRAM_prog		$1000		; location of SNES code to run while GSU is operational
.DEFINE WRAM_IRQ		$010C		; location of IRQ to run if GSU is operational (I think this is how the dummy vector works...)
.DEFINE bullet_cap		$0280		; maximum of 640 bullets
.DEFINE slot_size		$0C			; number of bytes per entry in bullet list
.DEFINE list_base		$8000		; bottom of bullet list in Game Pak RAM
.DEFINE next			$08			; index of NEXT field in bullet list
.DEFINE previous		$0A			; index of PREVIOUS field in bullet list

.DEFINE bullet_head_ptr		$E000	; put miscellaneous variables for GSU use at the 56 KB mark in Game Pak RAM
.DEFINE bullet_free_ptr		$E002	; so as to leave room for large framebuffers and bullet lists if desired
.DEFINE frame_count_ptr		$E004
.DEFINE random				$E006

.DEFINE random_seed		$0001.w		; should replace with frame count before starting input or something

;==============================================================================
; main
;==============================================================================

.BANK 0 SLOT 0
.ORG HEADER_OFF
.SECTION "TestCode" SEMIFREE

Main:
	InitializeSNES

	rep #$30		; A/mem = 16bit, X/Y=16bit
	lda #$2100		; set direct page to PPU bus
	tcd
	sep #$20		; A/mem = 8bit
	phk
	plb
	
	; ZERO FILL SUPER FX RAM
	stz $81				; set WRAM source address to bottom of bank...
	stz $82
	lda #$01			; ...$7F.  The stack isn't clean, sadly...
	sta $83
	ldx #$8080			; single-byte reads from WMDATA
	stx $4300
	ldx #$0000.w		; start at bottom of bank
	lda #$70			; first bank in Game Pak RAM
	stx $4302
	sta $4304
	ldx #$0000.w		; full 64 KB DMA
	stx $4305
	lda #$01
	sta $420B
	lda #$71			; second bank in Game Pak RAM
	sta $4304
	lda #$01
	sta $420B
	
	; PALETTE:
	stz $21			; start at beginning of CGRAM
	stz $22			; black backdrop
	stz $22
	lda #$10		; colour #1 = indigo
	sta $22
	lda #$74
	sta $22
	lda #$7B		; colour #2 = purple
	sta $22
	lda #$7D
	sta $22
	lda #$FF		; colour #3 = white
	sta $22
	sta $22
	
	lda #%11100000	; set constant colour to black
	sta $32
	
	; MAP
	lda #$80		; increment on VMDATAH write
	sta $15
	ldx #$3040		; start at $3040 (skip two rows = 64 words)
	stx $16
	ldx #$1801		; word writes to VMDATA
	stx $4300
	ldx #map		; tilemap address
	lda #:map		; and bank
	stx $4302
	sta $4304
	ldx #(endmap-map)	; size of tilemap
	stx $4305
	lda #$01
	sta $420B
	
	lda #$30		; BG1 map is at $3000
	sta $07
	
	; BG setup
	stz $05			; Mode 0
	lda #$01		; send BG1 to main screen
	sta $2C
	
	; set up bullet list and pattern data:
	lda #$70		; set data bank to low Game Pak RAM
	pha
	plb
	rep #$20		; 16-bit A/mem
	
	stz list_base+next				; store zero in NEXT field in first slot
	lda #(list_base+slot_size)		; load address of second slot in list
	sta list_base+previous			; store address of second slot in PREVIOUS field in first slot
	
	ldy #(bullet_cap-1)				; load bullet slot counter
	ldx #(list_base+slot_size+next)	; load offset of NEXT field in second slot
build_bullet_list:
	txa								; duplicate index in accumulator
	sec
	sbc #(next+slot_size).w			; obtain base address of next slot (ie: lower in memory, since the list is backwards)
	sta.w $0000,x					; and store in NEXT field in current slot
	clc
	adc #(slot_size+slot_size).w	; obtain base address of previous slot (ie: higher in memory)
	inx								; advance to PREVIOUS field in current slot (assumed two bytes past NEXT field)
	inx
	sta.w $0000,x					; and store
	adc #next.w						; get NEXT slot in previous (ie: next-higher) slot
	tax								; into X
	dey								; decrement slot count
	bne build_bullet_list			; and branch if not done
	
	stz bullet_head_ptr				; store zero in bullet list head pointer
	sec								; accumulator should contain pointer to NEXT field in 'slot' immediately above top of list
	sbc #(slot_size+next).w			; obtain base address of slot at top of list
	sta bullet_free_ptr				; assign to free bullet pointer
	stz frame_count_ptr				; start frame count at zero
	
	lda #random_seed	; HACK - maybe require the user to start the program, and count frames until then?
	sta random
	
	sep #$20			; reset A to 8-bit
	lda #$00			; reset data bank to zero
	pha
	plb
	
	; move S-CPU code to WRAM
	ldx #WRAM_prog		; set WRAM destination address
	stx $81
	stz $83				; in bank $7E
	ldx #$8000			; single-byte writes to WMDATA
	stx $4300
	ldx #RAMcode		; code address
	lda #:RAMcode		; and bank
	stx $4302
	sta $4304
	ldx #(endRAMcode-RAMcode)	; size of code
	stx $4305
	lda #$01
	sta $420B
	
	ldx #WRAM_IRQ		; set WRAM destination address
	stx $81
	stx $010C			; and point the dummy IRQ vector at the same place
	ldx #dummyIRQ		; code address
	lda #:dummyIRQ		; and bank
	stx $4302
	sta $4304
	ldx #(end_dummyIRQ-dummyIRQ)	; size of code
	stx $4305
	lda #$01
	sta $420B
	
	; reset DP
	lda #$00		; set direct page to bottom of shadow RAM
	xba
	lda #$00
	tcd
	
	jmp WRAM_prog
	
RAMcode:
	stz $3034		; set PBR to zero
	stz $303C		; set RAMBR to zero (for bank $70)
	lda #sbase		; set SCBR to sbase
	sta $3038
	lda #$38		; set SCMR to 192-line 2bpp with GSU ROM and RAM access
	sta $303A
	stz $3037		; set CFGR to standard multiply and no IRQ masking
	lda #$01		; set CLSR to 21 MHz
	sta $3039
	;lda #$03		; set X to 3
	;sta $3002
	ldx #SuperFX	; pick up location of GSU code
	stx $301E		; start GSU
	
	; IRQ setup
	ldx #$00FF		; dot number for interrupt
	stx $4207		; set H-timer
	ldx #$00D0		; scanline number for interrupt
	stx $4209		; set V-timer
	
	;lda $4211
	lda #$30		; enable H/V-IRQ
	sta $4200
	
waiting:
	wai				; wait for interrupt
	bra waiting
endRAMcode:

;==============================================================================

dummyIRQ:
	pha
	lda $4211
	stz $2121		; change background colour to indicate a missed frame
	lda #$1F
	sta $2122
	stz $2122
	pla
	rti
	
endVBlank:
	ldx #SuperFX	; pick up location of GSU code
	stx $301E		; start GSU
	
	stz $2121
	stz $2122
	stz $2122
	
	lda #$0F		; main screen turn on
	sta $2100
	
	rep #$30
	plx
	pla
	rti
end_dummyIRQ:
	
;==============================================================================

Interrupt:
	rep #$20
	pha
	
	sep #$20
	lda $4211		; acknowledge (raster?) IRQ
	lda $3031		; check GSU IRQ bit
	and #$80
	beq VBlank		; go to VBlank code if the GSU wasn't the IRQ source
	
	rep #$20		; if it was, just leave; there'll be a raster IRQ soon enough,
	pla				; and since it's in ROM it will know the GSU has stopped
	rti
	
VBlank:
	rep #$10
	phx
	
	lda.b #$80;.db $A9, $80	; lda #$80		; force blank
	sta $2100
	
	; get new graphics from Super FX RAM
	;lda #$80		; increment on VMDATAH write
	sta $2115
	lda.b #$08;.db $A9, $08	; lda #$08		; start at tile #1
	sta $2116
	stz $2117
	ldx #$1801		; word writes to VMDATA
	stx $4300
	ldx #screen_base	; tileset address
	lda.b #$70;.db $A9, $70	; lda #$70		; and bank
	stx $4302
	sta $4304
	ldx #$2A00		; size of tileset
	stx $4305
	lda.b #$01;.db $A9, $01	; lda #$01
	sta $420B
	
	jmp WRAM_IRQ+endVBlank-dummyIRQ	; have to leave the ROM or the GSU will kick us out
	
.ENDS

;==============================================================================
; Super FX code
;==============================================================================

.DEFINE X_0					$6F80	; middle of screen, minus one half pixel
.DEFINE Y_0					$5C80	; middle of screen, minus three and a half pixels
.DEFINE U_basic				$00C0;$0149	; reference speed of basic bullets
.DEFINE U_extra				$0080;$00DB	; reference speed of extra bullets
.DEFINE left_boundary		$00FD	; first pixel has three more columns of pixels to the right
.DEFINE right_boundary		$00E2	; first pixel has two more columns of pixels to the left
.DEFINE top_boundary		$00FA	; bullets are 7 pixels high; if first pixel is here, the bottom line is on the screen
.DEFINE bottom_boundary		$00BA	; if first pixel is this low, the bottom line is off the screen
.DEFINE bullet_height		$07		; used to determine whether a bullet needs bottom-of-frame checking or deletion
.DEFINE buffer_wordsize		$1500	; 224x192@2bpp = 5376 words

.DEFINE sineb				$41		; HiROM area - need access to whole bank
.DEFINE graphicb			$00		; LoROM area - need labels to be correct

; RANDOM NUMBER GENERATOR
; algorithm and shift triplet from http://b2d-f9r.blogspot.ca/2010/08/16-bit-xorshift-rng.html
; length: 23 bytes

.MACRO Random_Number_R1
	.db $21, $10 	; move R0, R1	; copy random number into accumulator
	.db $50			; add R0		; shift left 5 bits
	.db $50			; add R0
	.db $50			; add R0
	.db $50			; add R0
	.db $50			; add R0
	.db $3D, $C1	; xor R1		; exclusive-OR with old value
	.db $20, $11	; move R1, R0	; copy result to R1
	.db $C0			; hib			; shift right 9 bits
	.db $03			; lsr
	.db $3D, $C1	; xor R1		; exclusive-OR with old value
	.db $20, $11	; move R1, R0	; copy result to R1
	.db $9E			; lob			; shift left 8 bits
	.db $4D			; swap
	.db $3D, $C1	; xor R1		; exclusive-OR with old value
	.db $20, $11	; move R1, R0	; copy result to R1
.ENDM

; CARTESIAN VELOCITY CALCULATOR
; given speed in R6 and angle in R14, calculate 8.7 fixed point x and y velocity components in R3 and R4 respectively
; clobbers R0, R6 and R14
; length: 30 bytes
; NOTE: this version expends extra effort during bullet generation to speed up bullet processing slightly; see alt version below for details.

.MACRO Bullet_Aim
	.db $EF				; getb						; pick up sine value
	.db $DE				; inc R14
	.db $3D, $EF		; getbh
	.db $9F				; fmult						; and multiply it with the speed
	.db $3D, $50		; adc R0					; shift left twice
	.db $14				; to R4
	.db $50				; add R0					; to obtain vertical velocity in 8.7 fixed point in R4
	
	.db $F0, $FF, $3F	; iwt R0, #$3FFF			; rotate the angle by pi/2
	.db $1E				; to R14
	.db $5E				; add R14
	
	.db $EF				; getb						; pick up cosine value
	.db $DE				; inc R14
	.db $3D, $EF		; getbh
	.db $9F				; fmult						; and multiply it with the speed
	.db $3D, $50		; adc R0					; shift left twice
	.db $16				; to R6
	.db $50				; add R0					; and store the result in R6
	.db $F0, $00, $70	; iwt R0, #$7000			; load pixel aspect ratio
	.db $9F				; fmult						; multiply by raw horizontal velocity
	.db $13				; to R3
	.db $3D, $50		; adc R0					; shift left and store the result (horizontal velocity in 8.7 fixed point) in R3
.ENDM

; CARTESIAN VELOCITY CALCULATOR
; given 3.8 fixed-point speed in R6 and 7.8 fixed point angle in R14, calculate 3.5 fixed point x and y velocity components in R3 and R4 respectively
; clobbers R0, R6 and R14
; length: 23 bytes (?)
; NOTE: LDB is 6 cycles, LDW is 7.  In high-speed mode, this could be 8 and 11 cycles.
; This still doesn't justify using 8-bit velocity, because three shifts and a sign extension are required to line up with the byte boundary.

; .MACRO Bullet_Aim
	; getb						; pick up sine value
	; inc R14
	; getbh
	; fmult						; and multiply it with the speed
	; to R4
	; asr							; shift right while preserving sign to obtain vertical velocity in signed 3.5 fixed point in R4
	
	; iwt R0, #$3FFF				; rotate the angle by pi/2
	; to R14
	; add R14
	
	; getb						; pick up cosine value
	; inc R14
	; getbh
	; to R6
	; fmult						; multiply it with the speed and store the result in R6
	; iwt R0, #$7000				; load pixel aspect ratio
	; to R3
	; fmult						; multiply by raw horizontal velocity to obtain horizontal velocity in signed 3.5 fixed point in R3
; .ENDM

; BULLET LIST UPDATE ROUTINE
; given free slot in R9, list head in R10, and x and y velocity in R3 and R4, update bullet list with new active bullet
; clobbers R0, R11, updates R9 and R10
; length: 36 bytes
; NOTE: list format is U X V Y NEXT PREVIOUS in accordance with the requirements of the bullet processing loop.
; NOTE: it may be desirable to pass X_0 and Y_0 as parameters in the general case.

.MACRO Bullet_Shoot
	.db $29, $1B 	; move R11, R9				; assign free slot address to RAM pointer (this is the new bullet list head)
	.db $BB			; from R11
	.db $3E, $58	; add #$08					; advance past bullet position and velocity (8 bytes) to NEXT field, putting temp pointer in R0
	.db $19			; to R9						; load free slot address
	.db $40			; ldw (R0)					; from NEXT field in old free slot (ie: move the free slot one place down the list)
	.db $BA			; from R10					; store old bullet head address
	.db $30			; stw (R0)					; in NEXT field in old free slot, which is the new bullet list head
	;.db $20, $BA	; moves R0, R10				; check old bullet head address for zero value, indicating null pointer
	;.db $09			; beq +						; if null, don't bother writing to PREVIOUS field in old head (who cares; framebuffer gets cleared)
	;.db $05		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
	.db $2A			; with R10					; add 10 to old bullet head address to get PREVIOUS field (branch computed from here)
	.db $3E, $5A	; add #$0A
	.db $BB			; from R11					; store new bullet head address
	.db $3A			; stw (R10)					; in PREVIOUS field in old head
+	.db $2B, $1A 	; move R10, R11				; assign new bullet head address to bullet head address pointer
	
	.db $B3			; from R3						; store x velocity at beginning of new bullet head slot
	.db $3B			; stw (R11)
	.db $DB			; inc R11						; advance to next word
	.db $DB			; inc R11
	.db $F0			; iwt R0, #X_0				; get shot origin x-coordinate
	.dw X_0
	.db $53			; add R3						; move one frame away from it (ie: add x-velocity)
	.db $3B			; stw (R11)					; and store the result in the free slot
	.db $DB			; inc R11						; advance to next word
	.db $DB			; inc R11
	.db $B4			; from R4						; store y velocity
	.db $3B			; stw (R11)
	.db $DB			; inc R11
	.db $DB			; inc R11
	.db $F0			; iwt R0, #Y_0				; get shot origin y-coordinate
	.dw Y_0
	.db $54			; add R4						; move once
	.db $3B			; stw (R11)					; and store in free slot
.ENDM

.BANK 0 SLOT 0
.ORG HEADER_OFF+$03CF			; make sure CACHE is executed right before a 16-byte boundary ($8400 for convenience)
.SECTION "GSUcode" FORCE

SuperFX:
	; persistent variables:
	; random seed (temporary; should store in RAM)
	; player position (reserve?  Don't need right yet as there's no player)
	; RAM pointer
	; bullet list head
	; free bullet slot
	; flash list head (?  Only necessary when handling firing)
	; free flash slot (?  '')

	; REGISTER MAP:
	; R0 - accumulator
	; R1 - X
	; R2 - Y
	; R3 - free
	; R4 - free (don't use LMULT if you want the value to persist; also, you can't send the result of FMULT here)
	; R5 - free
	; R6 - FMULT/LMULT operand
	; R7 - player X position (yes, I do need player position when shooting, if aiming is to be a thing)
	; R8 - player Y position
	; R9 - free bullet
	; R10 - bullet list head
	; R11 - RAM pointer (don't use LINK if you want the value to persist)
	; R12 - LOOP counter
	; R13 - LOOP address
	; R14 - ROM address
	; R15 - program counter
	
	;iwt R11, #player_position	; load player position to R7/R8
	;to R7
	;ldw (R11)
	;inc R11
	;inc R11
	;to R8
	;ldw (R11)
	
	
	;.db $A0, $02		; ibt R0, #$02					; load 00010 to accumulator
	.db $A0, $00
	.db $3D, $4E		; cmode							; assign value in accumulator to plot option register
	
	.db $FB				; iwt R11, #bullet_head_ptr		; load bullet list head pointer to R10
	.dw bullet_head_ptr
	.db $1A				; to R10
	.db $4B				; ldw (R11)
	
	.db $FB				; iwt R11, #bullet_free_ptr		; load free bullet pointer to R9
	.dw bullet_free_ptr
	.db $19				; to R9
	.db $4B				; ldw (R11)
	
	
	.db $FB				; iwt R11, #frame_count_ptr		; load frame count to accumulator
	.dw frame_count_ptr
	.db $4B				; ldb (R11)
	
	.db $3E, $73		; and #$03						; mask off bits 2-7 of frame count (so it cycles 0-1-2-3 forever)
	.db $09				; beq shooting					; go to bullet generator code if count is [a multiple of] 4
	.db $07		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
	.db $D0				; inc R0						; increment count (relative address computed from here)
	
	.db $3D, $3B		; stb (R11)						; store it back
	.db $FF				; iwt R15, #done_shooting		; and jump past bullet generator code
	.dw done_shooting
	.db $01				; nop							; flush pipeline
	
	
shooting:
	;inc R0												; increment count (already in pipeline)
	.db $3D, $3B		; stb (R11)						; and store it back
	
	.db $A0, sineb		; ibt R0, #sineb				; change ROM bank to sine data
	.db $3F, $DF		; romb
	
	.db $F2				; iwt R2, #random				; load random seed pointer to R2
	.dw random
	.db $11				; to R1							; load random seed to R1
	.db $42				; ldw (R2)
	
	.db $F5				;iwt R5, #U_basic				; load variable component of bullet speed
	.dw U_basic
	
	.db $AC, $0B		; ibt R12, #$0B					; fire 12 bullets
	.db $FD				; iwt R13, #shooting_loop
	.dw shooting_loop
	
	.db $02				; cache - everything past here goes in the instruction cache for subsequent passes
	
shooting_loop:		; REGS:  R1: random, R2: rand pointer, R5: variable speed, R7/R8: player position, R9: free bullet, R10: bullet head, R12/R13: loop
		.db $20, $B9	; moves R0, R9					; check if the free bullet pointer is zero
		.db $08			; bne free_bullet				; if not, there are free bullets (ie: bullet list base cannot be $0000)
		.db $05		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		.db $01			; nop							; flush pipeline (relative address computed from here)
		.db $FF			; iwt R15, #done_shooting		; if the pointer is not valid, jump past bullet generator code
		.dw done_shooting
		.db $01			; nop							; flush pipeline
	
free_bullet:
		; GENERATE RANDOM BULLET SPEED:
		Random_Number_R1								; cycle random number in R1, leaving result in R0 as well (28 bytes)
		.db $03			; lsr							; make positive
		;.db $F6		; iwt R6, #U_basic				; load variable speed in 8.8 fixed point into R6
		;.dw U_basic
		.db $25, $16	; move R6, R5					; put variable speed in R6
		.db $9F			; fmult							; multiply R0 with R6, result goes in R0
		.db $3D, $50	; adc R0						; shift left with carry to obtain random component of speed in 8.8 fixed point
		.db $F6			; iwt R6, #U_basic				; load base speed
		.dw U_basic
		.db $16			; to R6
		.db $56			; add R6						; add to accumulator and store the result in R6
		; GENERATE RANDOM BULLET ANGLE:
		Random_Number_R1								; cycle random number in R1 again, leaving result in R0
		.db $03			; lsr							; take upper 15 bits of random number
		.db $1E			; to R14						; and use as index into sine wave (ie: as an angle)
		.db $50			; add R0						; (left shift without carry, balancing LSR)
		
	    Bullet_Aim										; get 8.7 fixed point x and y velocity components in R3 and R4 (30 bytes)
		
		Bullet_Shoot									; add bullet to list and populate its parameters (36 bytes)
		
		.db $3C			; loop							; decrement shot count and branch back if not zero
		.db $01			; nop							; flush pipeline (because I'm too lazy to take the first byte out of Random_Number_R1)
	
	.db $F0				; iwt R0, #U_basic				; check if R5 has been changed
	.dw U_basic
	.db $65				; sub R5
	.db $08				; bne done_shooting				; if so, this was the second go-around, so skip the loopback
	.db $08		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
	
	.db $01				; nop
	.db $F5				; iwt R5, #U_extra				; otherwise, load a different value to R5 (note the requirement that U_basic != U_extra)
	.dw U_extra
	.db $AC, $0A		; ibt R12, #$0A					; fire 9 bullets (plus one for the LOOP below this instruction)
	.db $3C				; loop
	.db $01				; nop
	
done_shooting:
	.db $B1				; from R1
	.db $32				; stw (R2)						; store random number back to RAM
	
	
	; CLEAR FRAMEBUFFER:
	.db $FC				; iwt R12, #$1500				; plot 5376 slivers
	.dw buffer_wordsize
	.db $FD				; iwt R13, #clear_framebuffer
	.dw clear_framebuffer
	.db $A0, $00		; ibt R0, #$00
	.db $FB				; iwt R11, #screen_base
	.dw screen_base
clear_framebuffer:
		.db $3B			; stw (R11)						; two bytes = one sliver
		.db $DB			; inc R11
		.db $3C			; loop
		.db $DB			; inc R11						; have to increment twice because word writes
	
	
	; PREPARE TO MOVE AND DRAW BULLETS:
	.db $FB				; iwt R11, #bullet_head_ptr		; store bullet head pointer in RAM
	.dw bullet_head_ptr
	.db $BA				; from R10
	.db $3B				; stw (R11)
	.db $FB				; iwt R11, #bullet_free_ptr		; store free bullet pointer in RAM
	.dw bullet_free_ptr
	.db $B9				; from R9
	.db $3B				; stw (R11)
	.db $2A, $1B 		; move R11, R10					; put bullet list head address in RAM pointer
	.db $F5				; iwt R5, #left_boundary		; load comparison values for range out
	.dw left_boundary
	.db $F6				; iwt R6, #right_boundary
	.dw right_boundary
	.db $F9				; iwt R9, #top_boundary
	.dw top_boundary
	.db $FA				; iwt R10, #bottom_boundary
	.dw bottom_boundary
	;.db $A0, graphicb	; ibt R0, #graphicb				; change ROM bank to bullet data
	;.db $3F, $DF		; romb
	; R1/R2 - plot coordinates, R3/R4 - velocity, R5/R6 - horizontal boundaries, R7/R8 - player position, R9/R10 - vertical boundaries, R12/R13 - loop
	.db $A4, $03		; ibt R4, #$03		; load colours (don't need two velocity registers because they are only used once and don't overlap)
	.db $AC, $01		; ibt R12, #$01
	.db $AE, $02		; ibt R14, #$02		; goes in R0 later, but still need for off bottom routine since R0 is occupied
	
	; define start position for drawing loop:
	;.db $FD			; iwt R13, #Start	; the bottom edge case handling code restores R13 after use, and nothing else touches it
	;.dw Start
	.db $AD, $06		; ibt R13, #$06		; load carriage return value
	.db $A8, $05		; ibt R8, #$05		; cheating - I don't need a player position yet, and I can save a couple cycles by having this value
	
	
		.db $13				; to R3							; get x-velocity in R3
move_and_draw:
		.db $4B				; ldw (R11)
		.db $DB				; inc R11						; advance to next word
		.db $DB				; inc R11
		.db $4B				; ldw (R11)						; get x-position in R0
		.db $53				; add R3						; update
		.db $3B				; stw (R11)						; store back
		.db $DB				; inc R11						; advance to next word
		.db $DB				; inc R11
		
		.db $C0				; hib
		.db $20, $11		; move R1, R0
		.db $66				; sub R6
		
		.db $13				; to R3							; get y-velocity in R3 (do above BPL offside_x because otherwise the branch is too long)
		.db $4B				; ldw (R11)						; - doesn't affect flags
		.db $0A				; bpl offside_x					; this isn't a bug because x out of range means bullet is gone; skipping y move is okay.
		.db $7E		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		
		.db $DB				; inc R11						; advance to next word (half of this gets done if branch is taken)
		.db $DB				; inc R11						; (and the other half gets done by pipeline code if branching back)
in_range_x:
		.db $4B				; ldw (R11)						; get y-position in R0
		.db $53				; add R3						; update
		.db $3B				; stw (R11)						; store back
		.db $DB				; inc R11						; advance RAM pointer to NEXT pointer
		.db $DB				; inc R11						; because y-position takes a while to store and PLOT can cause a wait state
		
		.db $C0				; hib
		.db $20, $12		; move R2, R0
		.db $6A				; sub R10
		.db $0A				; bpl offside_y
		.db $7A		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		
;in_range_y:
		; 70 cycles including TO R3
		; DRAW BULLET:
			; R0 = $02, R4 = $03, R8 = $05, R12 = $01, R13 = $06
			.db $BC			; from R12		; top line = 2 x colour #1
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $A0, $02	; ibt R0, #$02	; load final colour (tied for most-used, saves 6 cycles for 4 net cycles saved)
			.db $D2			; inc R2		; second line = 4 x colour #2
			.db $21			; with R1
			.db $64			; sub R4
second_line:
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $D2			; inc R2		; third line = colours #1, #2, #3, #3, #2, #1
			.db $21			; with R1
			.db $68			; sub R8
third_line:
			.db $BC			; from R12
			.db $4E			; color
			.db $4C			; plot
			.db $4E			; color
			.db $4C			; plot
			.db $B4			; from R4
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $4E			; color
			.db $4C			; plot
			.db $BC			; from R12
			.db $4E			; color
			.db $4C			; plot
			.db $D2			; inc R2		; middle line = colour #1, 4 x colour #3, colour #1
			.db $21			; with R1
			.db $6D			; sub R13
fourth_line:
			.db $4C			; plot
			.db $B4			; from R4
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $BC			; from R12
			.db $4E			; color
			.db $4C			; plot
			.db $D2			; inc R2		; fifth line = colours #1, #2, #3, #3, #2, #1
			.db $21			; with R1
			.db $6D			; sub R13
fifth_line:
			.db $4C			; plot
			.db $4E			; color
			.db $4C			; plot
			.db $B4			; from R4
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $4E			; color
			.db $4C			; plot
			.db $BC			; from R12
			.db $4E			; color
			.db $4C			; plot
			.db $D2			; inc R2		; sixth line = 4 x colour #2
			.db $21			; with R1
			.db $68			; sub R8
sixth_line:
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $D2			; inc R2		; last line = 2 x colour #1
			.db $21			; with R1
			.db $64			; sub R4
			.db $BC			; from R12
			.db $4E			; color
last_line:
			.db $4C			; plot
			.db $4C			; plot
			; 78 cycles since draw start, 148 since bullet start including TO R3
finished_bullet:
		.db $1B				; to R11						; update RAM pointer with address of next bullet
		.db $4B				; ldw (R11)
		.db $20, $BB		; moves R0, R11					; check if next bullet pointer is zero
		.db $08				; bne move_and_draw				; branch back to move/draw routine start if NEXT was not zero
		.db $93		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		.db $13				; to R3							; load first byte of move/draw routine
	.db $3D, $4C			; rpix							; flush pixel caches
	.db $00					; stop							; halt and issue Super NES IRQ
	
off_top:
			.db $22			; with R2
			.db $95			; sex							; need to increment past $0000 for this to work
			;.db $A0, $02			;ibt R0, #$02			; load final colour
			.db $D2			; inc R2
			.db $09, $B0	; beq second_line		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $E1			; dec R1
			.db $D2			; inc R2
			.db $09, $B4	; beq third_line		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $E1			; dec R1
			.db $D2			; inc R2
			.db $BC			; from R12
			.db $09, $C0	; beq fourth_line		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $4E			; color
			.db $D2			; inc R2
			.db $09, $C9	; beq fifth_line		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $01			; nop
			.db $D2			; inc R2
			.db $09, $D4	; beq sixth_line		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $D1			; inc R1
			.db $D2			; inc R2
			.db $09, $DA	; beq last_line		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $D1			; inc R1
			
			.db $05			; bra finished_bullet			; handle last line
			.db $D9		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		
offside_x:
		.db $B1				; from R1
		.db $65				; sub R5
		.db $0A				; bpl in_range_x
		.db $80		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		.db $DB				; inc R11			; load pipeline for branch
		;.db $2B				; with R11
		;.db $FF				; iwt R15, in_range_x
		;.dw in_range_x
		;.db $DB				; inc R11			; load pipeline for jump
;range_out_x:
		;.db $3E, $53		; add #$03
		.db $DB				; inc R11			; if the bullet is off the right or left side, R11 is on Y, so advance to NEXT
		.db $DB				; inc R11
		.db $05				; bra bullet_delete
		.db $77		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		
offside_y:
		.db $B2				; from R2
		.db $69				; sub R9
		.db $A0, $02		;ibt R0, #$02			; load final colour (in case BPL off_top is taken - this doesn't affect flags)
		;.db $22				; with R2
		.db $0A				; bpl off_top
		.db $D4		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		;.db $95				; sex				; prepare for incrementing R2 and checking for 0
		;.db $22				; with R2
		;.db $9E				; lob				; reverse sign extension, because R2 now needs to be 8-bit
		.db $B2				; from R2
		.db $6A				; sub R10
		.db $3E, $66		; sub #bullet_height-1
		.db $0A				; bpl bullet_delete
		.db $6B		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		
		; DRAW BULLET (with extra check to ensure pixels are not drawn off the bottom of the screen):
			.db $BC			; from R12		; top line = 2 x colour #1
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $D2			; inc R2		; second line = 4 x colour #2
			.db $F0, $C0, $00	; iwt R0, $00C0		; load compare value to accumulator (meaning I can't use it for a colour, so leave R14 set)
			.db $3F, $62	; cmp R2
			.db $09, $5C	; beq finished_bullet_edge		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $21			; with R1
			.db $64			; sub R4
			.db $BE			; from R14
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $D2			; inc R2		; third line = colours #1, #2, #3, #3, #2, #1
			.db $3F, $62	; cmp R2
			.db $09, $4F	; beq finished_bullet_edge		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $21			; with R1
			.db $68			; sub R8
			.db $BC			; from R12
			.db $4E			; color
			.db $4C			; plot
			.db $BE			; from R14
			.db $4E			; color
			.db $4C			; plot
			.db $B4			; from R4
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $BE			; from R14
			.db $4E			; color
			.db $4C			; plot
			.db $BC			; from R12
			.db $4E			; color
			.db $4C			; plot
			.db $D2			; inc R2		; middle line = colour #1, 4 x colour #3, colour #1
			.db $3F, $62	; cmp R2
			.db $09, $38	; beq finished_bullet_edge		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $21			; with R1
			.db $6D			; sub R13
			.db $4C			; plot
			.db $B4			; from R4
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $BC			; from R12
			.db $4E			; color
			.db $4C			; plot
			.db $D2			; inc R2		; fifth line = colours #1, #2, #3, #3, #2, #1
			.db $3F, $62	; cmp R2
			.db $09, $27	; beq finished_bullet_edge		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $21			; with R1
			.db $6D			; sub R13
			.db $4C			; plot
			.db $BE			; from R14
			.db $4E			; color
			.db $4C			; plot
			.db $B4			; from R4
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $BE			; from R14
			.db $4E			; color
			.db $4C			; plot
			.db $BC			; from R12
			.db $4E			; color
			.db $4C			; plot
			.db $D2			; inc R2		; sixth line = 4 x colour #2
			.db $3F, $62	; cmp R2
			.db $09, $12	; beq finished_bullet_edge		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $21			; with R1
			.db $68			; sub R8
			.db $BE			; from R14
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $4C			; plot
			.db $D2			; inc R2		; last line = 2 x colour #1
			.db $62			; sub R2
			.db $09, $06	; beq finished_bullet_edge		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
			.db $21			; with R1
			.db $64			; sub R4
			.db $BC			; from R12
			.db $4E			; color
			.db $4C			; plot
			.db $4C			; plot
			
finished_bullet_edge:
		.db $FF				; iwt R15, #finished_bullet		; go to standard finished_bullet section
		.dw finished_bullet
		
	; DELETE BULLET:
bullet_delete:
		.db $11				; to R1							; put NEXT in R1
		.db $4B				; ldw (R11)
		.db $F2				; iwt R2, #bullet_head_ptr		; get head pointer address in R2
		.dw bullet_head_ptr
		.db $42				; ldw (R2)						; load actual pointer in accumulator
		.db $3E, $58		; add #$08						; line up with current field
		.db $6B				; sub R11						; check for match with current bullet slot
		.db $08				; bne not_head					; if unequal, we are not at the head
		.db $07		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		.db $B1				; from R1						; if we are at the head, NEXT becomes the new head pointer (if not, INC R11 resets Sreg)
		.db $32				; stw (R2)
		.db $DB				; inc R11						; advance to PREVIOUS field (because otherwise later code won't know where R11 is)
		.db $DB				; inc R11
		.db $05				; bra done_not_head				; branch past not_head
		.db $08		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		.db $01				; nop							; flush pipeline
not_head:						; if we are not at the head,
		.db $DB				; inc R11						; advance to PREVIOUS field
		.db $DB				; inc R11
		.db $4B				; ldw (R11)						; load PREVIOUS to accumulator
		.db $3E, $58		; add #$08						; advance to NEXT field in PREVIOUS slot
		.db $B1				; from R1						; store current bullet's NEXT in previous bullet's NEXT field
		.db $30				; stw (R0)
done_not_head:
		.db $20, $B1		; moves R0, R1					; check NEXT for zero value
		.db $09				; beq finish_frame				; if it's zero, we're at the end of the list
		.db $1B		; !!!!!! MANUALLY COMPUTED RELATIVE ADDRESS !!!!!!
		.db $01				; nop
		.db $4B				; ldw (R11)						; load PREVIOUS to accumulator
		.db $21				; with R1						; advance NEXT pointer to PREVIOUS field
		.db $3E, $5A		; add #$0A
		.db $31				; stw (R1)						; store PREVIOUS to next bullet's PREVIOUS slot

		.db $EB				; dec R11						; go back to NEXT field
		.db $EB				; dec R11
		.db $F2				; iwt R2, #bullet_free_ptr		; get free bullet pointer address in R2
		.dw bullet_free_ptr
		.db $42				; ldw (R2)						; get actual free bullet pointer in accumulator
		.db $3B				; stw (R11)						; and stick it in the NEXT field of the current slot
		.db $2B				; with R11						; go back to the beginning of the current slot
		.db $3E, $68		; sub #$08
		.db $BB				; from R11						; and store the current slot address as the new free bullet pointer
		.db $32				; stw (R2)
	
		.db $21				; with R1						; reset NEXT to beginning of slot
		.db $3E, $6A		; sub #$0A
		.db $21, $1B		; move R11, R1					; and update pointer with address of next bullet (already checked if zero; it's not)
		.db $FF				; iwt R15, #move_and_draw		; jump back to move/draw routine start
		.dw move_and_draw
		.db $13				; to R3							; load first byte of move/draw routine
	
finish_frame:
	.db $EB					; dec R11						; go back to NEXT field
	.db $EB					; dec R11
	.db $F2					; iwt R2, #bullet_free_ptr		; get free bullet pointer address in R2
	.dw bullet_free_ptr
	.db $42					; ldw (R2)						; get actual free bullet pointer in accumulator
	.db $3B					; stw (R11)						; and stick it in the NEXT field of the current slot
	.db $2B					; with R11						; go back to the beginning of the current slot
	.db $3E, $68			; sub #$08
	.db $BB					; from R11						; and store the current slot address as the new free bullet pointer
	.db $32					; stw (R2)
	
	.db $3D, $4C			; rpix							; flush pixel caches
	.db $00					; stop							; halt and issue Super NES IRQ
	
.ENDS

;==============================================================================

.BANK 0 SLOT 0
.ORG 0
.SECTION "MapData" SEMIFREE

	map:
		.incbin "Data\224x192_base1.map";
	endmap:

.ENDS

.BANK 0 SLOT 0
.ORG 0
.SECTION "GraphicsData" SEMIFREE

	bullet_even:
		.incbin "Data\FXtest_bullet_even.dat";
	bullet_odd:
		.incbin "Data\FXtest_bullet_odd.dat";

.ENDS

;==============================================================================

.BANK 2 SLOT 0
.ORG 0
.SECTION "SineData0" FORCE

	.incbin "Data\sine16_0.dat";

.ENDS

.BANK 3 SLOT 0
.ORG 0
.SECTION "SineData1" FORCE

	.incbin "Data\sine16_1.dat";

.ENDS

;==============================================================================

; .BANK 4 SLOT 0
; .ORG 0
; .SECTION "Random0" FORCE

	; .incbin "Data\random0.dat";

; .ENDS

; .BANK 5 SLOT 0
; .ORG 0
; .SECTION "Random1" FORCE

	; .incbin "Data\random1.dat";

; .ENDS

;==============================================================================






; DUAL-PIXEL BLITTING ASSUMING EVEN PIXEL COUNTS AND NO GAPS:

	; to R12		; pixel count goes in the LOOP index register (R13 needs to be loaded with #Start at some point)
	; getb		; get pixel pair count for first line
	; inc R14		; increment ROM address, triggering a buffer load

; Start:
	; getc		; get pixel data (two pixels per byte) from ROM buffer
	; inc R14		; and increment ROM address
	; plot		; plot first pixel and increment X-counter in R1
	; loop		; decrement pixel pair count; if not zero, go to address in R13, ie: "Start"
	; plot		; plot pixel and increment X-counter in R1 (since the GSU is pipelined, this byte gets executed regardless)

	; getb		; get carriage return X-component (goes in R0)
	; inc R14		; increment ROM address
	; with R1		; update X-coordinate
	; sub R0		; by subtracting carriage return X-component
	; inc R2		; increment Y-coordinate
	; to R12		; update LOOP index register
	; getb		; with pixel pair count for next line
	; inc R14		; increment ROM address
	; loop		; decrement pixel count and branch to Start if not zero
	; nop			; dummy fill pipeline (nothing else to do before GETC, and the ROM buffer isn't ready anyway)


