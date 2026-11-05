parser grammar MordorParser;
options { tokenVocab=MordorLexer; }

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
