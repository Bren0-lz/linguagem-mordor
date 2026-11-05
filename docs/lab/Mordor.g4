grammar Mordor;

program: HARDWARE IN LBRACE resourceDeclaration* RBRACE
         globalDeclaration* functionDeclaration* SETUP block LOOP block EOF;
resourceDeclaration: resourceType ID ASSIGN pin (COLON expression)? SEMI;
resourceType: INPUT | OUTPUT | INPUT_PULLUP | ANALOG_INPUT | PWM_OUTPUT;
pin: (DIGITAL_PIN | ANALOG_PIN) LPAREN INT_LITERAL RPAREN;
globalDeclaration: CONST dataType ID ASSIGN expression SEMI
                 | dataType ID (ASSIGN expression)? SEMI;
localDeclaration: CONST? dataType ID ASSIGN expression SEMI;
integerType: (INT_TYPE | BYTE_TYPE) (LT INT_LITERAL GT)?;
elementType: integerType | FLOAT | BOOL_TYPE | LEVEL_TYPE;
dataType: elementType | TIME_TYPE (LT MILLIS GT)? | TEXT_TYPE
        | OPTIONAL_BYTE_TYPE | LBRACK elementType SEMI INT_LITERAL RBRACK;
returnType: dataType | VOID;
parameter: (dataType | resourceType) ID;
functionDeclaration: FUNCTION returnType ID LPAREN
                     (parameter (COMMA parameter)*)? RPAREN block;
block: IN LBRACE (localDeclaration | statement)* RBRACE;
statement: assignment SEMI | action SEMI | SERIAL_BEGIN INT_LITERAL SEMI
         | ifStatement | switchStatement | whileStatement | forStatement
         | doStatement | repeatStatement | BREAK SEMI | CONTINUE SEMI
         | RETURN expression? SEMI;
assignment: ID (LBRACK expression RBRACK)? ASSIGN expression;
action: ACTION ((SERIAL_PRINT | SERIAL_PRINTLN | SERIAL_WRITE | DELAY) expression
              | DIGITAL_WRITE expression TO ID | ANALOG_WRITE ID expression | call);
ifStatement: IF expression (TO ID)? block (ELSE block)?;
switchStatement: SWITCH expression IN LBRACE
                 (CASE expression block)+ (ELSE block)? RBRACE;
whileStatement: WHILE expression block;
forStatement: FOR LPAREN forInitializer? SEMI expression? SEMI forUpdate? RPAREN block;
forInitializer: dataType ID ASSIGN expression | assignment | ACTION call;
forUpdate: assignment | ACTION call;
doStatement: DO block WHILE expression SEMI;
repeatStatement: REPEAT expression block;

// Níveis explícitos: comparações não se encadeiam; binários associam à esquerda.
expression: orExpression;
orExpression: andExpression (OR andExpression)*;
andExpression: comparisonExpression (AND comparisonExpression)*;
comparisonExpression: additiveExpression ((LT | LE | GT | GE | EQ | NEQ) additiveExpression)?;
additiveExpression: multiplicativeExpression ((PLUS | MINUS) multiplicativeExpression)*;
multiplicativeExpression: unaryExpression ((STAR | SLASH | PERCENT) unaryExpression)*;
unaryExpression: (NOT | MINUS) unaryExpression | postfixExpression;
postfixExpression: primaryExpression (LBRACK expression RBRACK)*;
primaryExpression: INT_LITERAL | FLOAT_LITERAL | STRING_LITERAL | TRUE | FALSE | HIGH | LOW
                 | call | ID | LPAREN expression RPAREN
                 | LBRACK expression (COMMA expression)* RBRACK
                 | (integerType | FLOAT) LPAREN expression RPAREN
                 | TIME_TYPE LPAREN INT_LITERAL COMMA INT_LITERAL RPAREN
                 | SERIAL_READ | MILLIS | (DIGITAL_READ | ANALOG_READ) ID
                 | bitExpression;
call: ID LPAREN (expression (COMMA expression)*)? RPAREN;
bitExpression: BIT_NOT LT INT_LITERAL GT LPAREN expression RPAREN
             | (BIT_AND | BIT_OR | BIT_XOR | SHIFT_LEFT | SHIFT_RIGHT)
               LT INT_LITERAL GT LPAREN expression COMMA expression RPAREN;

// Alternativas canonicamente equivalentes: gramática portátil para o ANTLR Lab.
// A normalização do texto de palavras/IDs é feita no front-end Java.
HARDWARE: 'lûg' | 'lu\u0302g';
SETUP: 'ash_durb';
LOOP: 'durbatulûk' | 'durbatulu\u0302k';
CONST: 'krimp';
FUNCTION: 'durb';
RETURN: 'thrakul';
INT_TYPE: 'ashum';
BYTE_TYPE: 'ashuk';
BOOL_TYPE: 'ashburz';
LEVEL_TYPE: 'burzash';
TIME_TYPE: 'nazgum';
TEXT_TYPE: 'nazg_ul';
OPTIONAL_BYTE_TYPE: 'ashuk_burz';
VOID: 'durb_burz';
TRUE: 'ash';
FALSE: 'burzum';
HIGH: 'burz_ash';
LOW: 'búrz' | 'bu\u0301rz';
IF: 'gimb';
ELSE: 'gimb_burz';
SWITCH: 'gimbul';
CASE: 'gimb_ash';
WHILE: 'durb_gimb';
REPEAT: 'durb_ash';
DO: 'durb_at';
FOR: 'durb_nazg';
BREAK: 'krimp_durb';
CONTINUE: 'thrak_durb';
AND: 'agh';
OR: 'agh_ash';
NOT: 'agh_burz';
ACTION: 'thrak';
TO: 'ul';
DIGITAL_WRITE: 'thrak_burzash';
ANALOG_WRITE: 'thrak_ashuk';
DELAY: 'krimp_nazg';
DIGITAL_READ: 'gimb_burzash';
ANALOG_READ: 'gimb_ashum';
SERIAL_PRINT: 'thrak_nazg';
SERIAL_PRINTLN: 'thrak_nazgul';
SERIAL_WRITE: 'thrak_ash';
SERIAL_BEGIN: 'ash_nazg';
SERIAL_READ: 'gimbatul';
MILLIS: 'gimb_nazgum';
DIGITAL_PIN: 'nazg';
ANALOG_PIN: 'nazg_ashum';
INPUT: 'gimbat';
INPUT_PULLUP: 'gimbat_ash';
OUTPUT: 'thrakat';
ANALOG_INPUT: 'gimbat_ashum';
PWM_OUTPUT: 'thrakat_ashuk';
BIT_AND: 'aghuk';
BIT_OR: 'aghuk_ash';
BIT_XOR: 'aghuk_burz';
BIT_NOT: 'burzuk';
SHIFT_LEFT: 'thrakuk';
SHIFT_RIGHT: 'gimbuk';
FLOAT: 'ashum_nazg';
IN: 'ishi';

EQ: '=='; NEQ: '!='; LE: '<='; GE: '>=';
ASSIGN: '='; LT: '<'; GT: '>';
PLUS: '+'; MINUS: '-'; STAR: '*'; SLASH: '/'; PERCENT: '%';
COLON: ':'; SEMI: ';'; COMMA: ',';
LPAREN: '('; RPAREN: ')'; LBRACE: '{'; RBRACE: '}';
LBRACK: '['; RBRACK: ']';

FLOAT_LITERAL: DECIMAL ('.' DIGIT+ EXPONENT? | EXPONENT);
INT_LITERAL: '0b' [01]+ | '0x' [0-9a-fA-F]+ | DECIMAL;
// Longest match impede decompor um candidato inválido em tokens válidos.
INVALID_NUMBER: (DIGIT | '.' DIGIT) ([\p{L}\p{M}\p{N}_.] | [eE] [+-])*;
STRING_LITERAL: '"' (ESCAPE | ~["\\\r\n])* '"';
INVALID_STRING: '"' ('\\' ~[\r\n] | ~["\\\r\n])* ('"' | '\\'? ('\r' '\n'? | '\n' | EOF));
INVALID_IDENTIFIER: '__' [\p{L}\p{M}\p{Nd}_]*;
ID: [\p{L}_] [\p{L}\p{M}\p{Nd}_]*;
LINE_COMMENT: '//' ~[\r\n]* -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;
INVALID_COMMENT: '/*' (~[*] | '*'+ ~[*/])* '*'* EOF;
WS: [ \t\r\n]+ -> skip;
ERROR_CHAR: .;
fragment DECIMAL: '0' | [1-9] DIGIT*;
fragment DIGIT: [0-9];
fragment EXPONENT: [eE] [+-]? DIGIT+;
fragment ESCAPE: '\\' [nrt"\\];
