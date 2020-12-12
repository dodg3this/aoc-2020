use std::io;
use std::fs;
use std::collections::HashMap;
use std::cell::Cell;
use std::io::Error;

type Instruction = fn(&VM, i32);

struct VM {
    program: Vec<(Instruction, i32)>,
    program_counter: Cell<usize>,
    executed: Cell<HashMap<usize, bool>>,
    accumulator: Cell<i32>
}

fn main() -> io::Result<()> {
    let contents = fs::read_to_string("input")?;

    let input: Vec<&str> = contents.split("\n").collect();

    let mut  vm = VM::new(input)?;
    loop {
        let (_, cont) = vm.cycle(true);
        if !cont {
            break;
        }
    }
    println!("{:?}", vm.accumulator.get());
    Ok(())
}

impl VM {
    pub fn cycle(&mut self, terminate_on_repeat: bool) -> (bool, bool) {
        let pc: usize = self.program_counter.get();

        match self.executed.get_mut().get(&pc) {
            None => {}
            Some(found) => {
                if *found && terminate_on_repeat{
                    return  (true, false)
                }
            }
        }
        self.executed.get_mut().insert(pc, true);
        self.program_counter.set(self.program_counter.get() + 1);

        if self.program_counter.get() == self.program.len() {
            return (false, false)
        }
        self.program[pc].0(self, self.program[pc].1);
        return (false, true)
    }

    fn new(args: Vec<&str>) -> Result<VM, Error> {
        let mut program: Vec<(Instruction, i32)> = Vec::with_capacity(args.len());
        for s in args {
            let parts: Vec<&str> = s.split(" ").collect();
            if parts.len() != 2 {
                println!("failed to parse {}", s);
                return Err(io::Error::new(io::ErrorKind::Other, "failed to parse"))
            }
            let value = parts[1].parse::<i32>().unwrap();
            // println!("{}", value);
            let instruction: Instruction;
            let command = parts[0];
            if command == "acc" {
                instruction = |vm, value| {
                    vm.accumulator.set(vm.accumulator.get() + value);
                }
            } else if command == "jmp" {
                instruction = |vm, value| {
                    vm.program_counter.set((vm.program_counter.get() as i32 + value - 1) as usize);
                }
            } else {
                instruction = |_, _| {};
            }
            program.push((instruction, value));
        };
        Ok(VM {
            program,
            program_counter: Cell::from(0),
            executed: Default::default(),
            accumulator: Cell::from(0)
        })
    }
}