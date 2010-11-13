#!/usr/bin/env ruby
cnf_dir = ARGV[0]

CLASS_PATH        = "'lib/*'"
SOLVER_MAIN_CLASS = "com.anjlab.sat3.Program"

def section(text)
  puts "*" * 42
  puts "* #{text}"
end

if cnf_dir.nil?
  puts "USAGE: ./batch-run.rb path-to-cnf-dir"
  exit
end

unless File.directory? cnf_dir
  abort "Invalid CNF folder '#{cnf_dir}'. Please pass valid folder name containing *.cnf files as argument to this batch."
  exit 1
end

section "Solving instancies"

Dir[cnf_dir + "/*.cnf"].each do |cnf| 
  next if File.exists?(cnf + "-result.txt")

  pid = fork do
    # child
    Process.setpriority(Process::PRIO_PROCESS, 0, 19)
    exec("java -cp #{CLASS_PATH} #{SOLVER_MAIN_CLASS} #{cnf}")
  end
  # parent
  Process.waitpid(pid)
end

section "Agregating results"

exec "java -cp #{CLASS_PATH} com.anjlab.sat3.ResultsAggregator #{cnf_dir}"