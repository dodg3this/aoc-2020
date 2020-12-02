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

fn run(config: Config) -> Result<(), Box<dyn Error>> {
    let contents = fs::read_to_string(config.filename)?;

    let seen: Vec<&str> = contents.split('\n').collect();
    let mut valid_passwords_a = 0;
    let mut valid_passwords_b = 0;

    for each_entry in seen {
        let split: Vec<&str> = each_entry.split(" ").collect();
        if split.len() < 3 {
            return Err(From::from("invalid record"));
            // return Err(Box::new("invalid record"));
        }

        let password = split[2];
        let char = split[1].char_indices().next().unwrap().1;
        let len_spec: Vec<&str> = split[0].split("-").collect();
        let min = len_spec[0].parse::<usize>().unwrap();
        let max = len_spec[1].parse::<usize>().unwrap();

        let mut count = 0;
        for each_char in password.chars() {
            if each_char == char {
                count += 1;
            }
        }

        if min <= count && count <= max {
            valid_passwords_a += 1;
        }

        if (password.chars().nth(min - 1 ).unwrap() == char) ^ (password.chars().nth(max - 1).unwrap() == char) {
            valid_passwords_b += 1;
        }

    }
    println!("valid passwords a: {}", valid_passwords_a);
    println!("valid passwords b: {}", valid_passwords_b);

    Ok(())
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
