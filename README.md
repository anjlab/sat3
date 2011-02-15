# 1. What this project is about

This project is reference implementation of [Romanov's Polynomial Algorithm](http://romvf.wordpress.com/)
for [3-SAT Problem](http://en.wikipedia.org/wiki/Boolean_satisfiability_problem#3-satisfiability). 
Algorithm implemented in pure Java with command line interface. 
Current version is single-threaded implementation.

# 2. How to run experiments

__Note__: Java 1.6 should be installed on your machine and Java 
binaries should be in your system PATH.

We support [DIMACS CNF file format](http://logic.pdmi.ras.ru/~basolver/dimacs.html)
as input files. Examples can be found [here](http://www.cs.ubc.ca/~hoos/SATLIB/benchm.html).
Input files can contain k-SAT instances, we convert them to 3-SAT on the fly. 

Note that k-SAT to 3-SAT reduction leads to an increase in the 
number of variables and clauses.

As a result we generate two files:

1.  [input-file-name]-__results.txt__ - this file contains formula 
     classification result (SAT or UNSAT) and additional info (time
     measurements and satisfying set if formula is SAT). 

     See [How to read output files](https://github.com/anjlab/sat3/wiki/How-to-read-output-files)
     wiki page.
     
2.  [input-file-name]-__hss-0.png__ - this file contains graphical 
    representation of basic graph (see [Romanov's paper for reference](http://arxiv.org/abs/1011.3944)).
    
    Red colored path represents HS route which is joint satisfying set 
    for the formula.


See [Solving-article-example.cnf](https://github.com/anjlab/sat3/wiki/Solving-article-example.cnf)
wiki page for sample outputs.
  
## Step by step instructions to get your first results:

For first try we recommend formulas with variables count < 75 and number
of clauses ~ 100 (you can find them in downloaded package). They can be 
solved within few minutes. 

Note that it took about 14 hours for this reference implementation to 
solve satisfiable 3-SAT instances with variable count = 398 and
number of clauses = 1040 (flat50-115 from 
["Flat" Graph Colouring set](http://www.cs.ubc.ca/~hoos/SATLIB/benchm.html)).

1. Download and extract [package](https://github.com/downloads/anjlab/sat3/3-sat-experiment-2.0.0-PRE-bin.zip)
2. Unzip package to any folder __[target_folder]__
3. Run console and __`cd`__ to __[target_folder]__
4. In console run following command:

   on windows

        solve examples\uf50-01000.cnf
       
   on linux/mac os x (__ruby is required__)
    
        ./solve examples/uf50-01000.cnf
      
5. By default output files will be created in the same folder as input 
   file

Refer to [Command line tools](https://github.com/anjlab/sat3/wiki/Command-line-tools)
wiki page for more options.

# 3. License
(LGPL version 3)

Copyright (c) 2010 AnjLab

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.