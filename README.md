# rat16-compiler
Built a compiler that translates Pascal-like source code ("Rat16 Language") into x86 assembly language. Implementation is ~1200 lines of code using Java. The first component is a lexical analyzer to generate tokens and lexemes from an input file containing source code. The second component is a syntax analyzer, which constructs a parse tree to determine syntactical correctness of the code. The third component introduces a symbol table, for which every lexeme is placed along with a memory address for the identifiers. This also contains procedures to translate the instructions into their assembly language counterparts.
<br />
<br />
<br />
## Instructions
<br />
1. Have a Rat16 source code file available to feed into the compiler (see one of the “InputFile” examples in this repo).
<br />
2. The user should know the filepath for the input file on their computer, and enter it when prompted (i.e. Documents\InputTest1.txt).
<br />
3. The program will automatically create an output file (.txt) during runtime, so please do not pre-create one on your own.
<br />
<br />

![commandprompt](https://user-images.githubusercontent.com/22629266/41748676-b93143d8-7567-11e8-9203-cb2242763b0a.PNG)
<br />
<br />
<br />
## Example Input File

![input](https://user-images.githubusercontent.com/22629266/41748888-973c11b2-7568-11e8-9620-54a80cc14cb6.PNG)
<br />
<br />
<br />
## Example of Generated Output File

![output](https://user-images.githubusercontent.com/22629266/41748903-a3c7207a-7568-11e8-84e5-32989e032646.PNG)
