10 PRINT Lets concatonate some files shall we.
20 PRINT Do you have a file name to store the end product?(yes or no)
30 INPUT $answer
40 IF answer$ = yes
50 PRINT What is file name?
60 INPUT $fileName
70 ELSE 
80 PRINT Then your file will be named file.txt
90 END
100 PRINT Let us begin. Enter a filename and hit Enter. 
101 PRINT Enter -1 when finished.
110 KITTY fileName$
120 PRINT Your file is created. 