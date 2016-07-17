require 'json'

input  = JSON.parse(File.read("mscoco_2014_train.json"))

output_EXIST   = { "questions" => [] }
output_EXISTa  = { "questions" => [] }
output_NEG     = { "questions" => [] }
output_OR      = { "questions" => [] }
output_YESNO   = { "questions" => [] }
output_ALL     = { "questions" => [] }
output_HOWMANY = { "questions" => [] }

input["questions"].each do |question|

  if question["question_type"] == "is there a"
     output_EXISTa["questions"] << question
  end

  if question["question_type"] == "is there"
     output_EXIST["questions"] << question
  end

  if question["question_type"] == "how many"
     output_HOWMANY["questions"] << question
  end

  if question["answer_type"] == "yes/no"
     output_YESNO["questions"] << question
  end

  if question["question"].include? " or "
     output_OR["questions"] << question
  end

  if question["question"].include? " not "
     output_NEG["questions"] << question
  end

  if question["question"].include? " all "
     output_ALL["questions"] << question
  end
end

File.write("mscoco_2014_train_ISTHERE.json",JSON.pretty_generate(output_EXIST))
File.write("mscoco_2014_train_ISTHEREA.json",JSON.pretty_generate(output_EXISTa))
File.write("mscoco_2014_train_HOWMANY.json",JSON.pretty_generate(output_HOWMANY))
File.write("mscoco_2014_train_YESNO.json",JSON.pretty_generate(output_YESNO))
File.write("mscoco_2014_train_NEG.json",JSON.pretty_generate(output_NEG))
File.write("mscoco_2014_train_OR.json",JSON.pretty_generate(output_OR))
File.write("mscoco_2014_train_ALL.json",JSON.pretty_generate(output_ALL))
