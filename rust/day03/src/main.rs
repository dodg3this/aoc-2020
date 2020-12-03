use std::env;
use std::error::Error;
use std::fs;
use std::process;

fn main() {
    let args: Vec<String> = env::args().collect();

    let config = Config::new(&args).unwrap_or_else(|err| {
        println!("problem parsing arguemnts: {}", err);
        process::exit(1);
    });

    println!("Searching for {}", config.query);
    println!("In file {}", config.filename);

    if let Err(e) = run(config) {
        println!("Application error: {}", e);
        process::exit(1);
    }
}

struct Slope {
    down: usize,
    right: usize,
}

fn run(config: Config) -> Result<(), Box<dyn Error>> {
    let contents = fs::read_to_string(config.filename)?;

    let seen: Vec<&str> = contents.split('\n').collect();

    let slopes = vec![Slope { down: 1, right: 1 },
                      Slope { down: 1, right: 3 },
                      Slope { down: 1, right: 5 },
                      Slope { down: 1, right: 7 },
                      Slope { down: 2, right: 1 }];

    let trees = slopes.into_iter().map(|s| number_of_trees(&seen.clone(), s));

    let count = trees.into_iter().fold(1, |acc, x| -> usize {
        println!("number of trees found: {}", x);
        acc * x
    });

    println!("total count: {}", count);

    Ok(())
}

fn number_of_trees(seen: &Vec<&str>, slope: Slope) -> usize {
    let rows_count: usize = seen.len();

    let mut i: usize = 0;
    let mut count: usize = 0;

    while i * slope.down < rows_count {
        let row = seen[i * slope.down];
        let position = (i * slope.right) % row.len();
        // println!("searching tree in {} {} at {}, {}", row, row.len(), i * down, position);

        if row.chars().nth(position).unwrap() == '#' {
            // println!("found");
            count += 1
        }
        i += 1;
    }
    count
}

struct Config {
    query: String,
    filename: String,
}

impl Config {
    fn new(args: &[String]) -> Result<Config, &'static str> {
        if args.len() < 3 {
            return Err("not enough arguments");
        }
        let query = args[1].clone();
        let filename = args[2].clone();

        Ok(Config { query, filename })
    }
}
