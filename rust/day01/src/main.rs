use std::collections::HashMap;
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

    let mut contains = HashMap::new();
    let seen2 = &seen.clone();

    for each_number in seen {
        contains
            .entry(each_number.parse::<i32>().unwrap())
            .or_insert(true);
    }

    let mut i = 0;

    'outer: for m in seen2 {
        let m_ = m.parse::<i32>().unwrap();
        let mut j = 0;
        for &n in seen2 {
            if i < j {
                let n_ = n.parse::<i32>().unwrap();
                match contains.get(&(2020 - m_ - n_)) {
                    Some(&true) => {
                        println!(
                            "found {}, {}, {}, {}, {}",
                            m,
                            i,
                            n,
                            j,
                            m_ * n_ * (2020 - m_ - n_)
                        );
                        break 'outer;
                    }
                    _ => (),
                }
            }
            j += 1;
        }
        i += 1;
    }
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
