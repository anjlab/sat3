TODO Draft

Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem
Copyright (C) 2010 AnjLab
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
See LICENSE.txt file or visit http://www.gnu.org/copyleft/lesser.html for details.
Version: 1.0.3

usage: com.anjlab.sat3.Program [OPTIONS] <PATH>
                               Where <PATH> is a path to file containing
                               N-SAT formula instance in DIMACS CNF or
                               Romanov SKT file format.
 -a,--disable-assertions            Disables program self-check during
                                    execution.
 -c,--create-skt                    Convert input formula to Romanov SKT
                                    file format.
 -e,--evaluate-formula <filename>   Evaluate formula using variable values
                                    from this file.
 -h,--help                          Prints this help message.
 -i,--hss-image-output <filename>   File name where firsth resulting
                                    hyperstructure image will be written
                                    (if built any). Defaults to
                                    <PATH>-hss-0.png
 -o,--output <filename>             File name where resulting HSS route
                                    will be written (if found any).
                                    Defaults to <PATH>-results.txt
 -p,--use-pretty-print              If specified, program will print
                                    detailed information about formulas
                                    including triplet values.
                                    Useful when studying how algorithm
                                    works (especially if variables count
                                    less than 20).
                                    Disabled by default.
 -u,--use-abc-var-names             If specified, program will use ABC
                                    names for variables (like 'a', 'b',
                                    ..., 'z' instead of 'x1', 'x2', etc.)
                                    during formula output.
                                    Disabled by default. Forced disabled
                                    if variables count more than 26.
