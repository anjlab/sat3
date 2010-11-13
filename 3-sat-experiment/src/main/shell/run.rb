#!/usr/bin/env ruby

CLASS_PATH        = "'lib/*'"
SOLVER_MAIN_CLASS = "com.anjlab.sat3.Program"

exec "java -cp #{CLASS_PATH} #{SOLVER_MAIN_CLASS} #{ARGV.join(' ')}"