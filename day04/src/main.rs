use std::io;
use std::fs;
use std::collections::HashMap;
use regex::Regex;

type Callback = fn(&str) -> bool;

fn is_valid_byr(byr: &str) -> bool {
    let i = byr.parse::<i32>().unwrap();
    i <= 2002 && i >= 1920
}

fn is_valid_iyr(iyr: &str) -> bool {
    let i = iyr.parse::<i32>().unwrap();
    i <= 2020 && i >= 2010
}

fn is_valid_eyr(eyr: &str) -> bool {
    let i = eyr.parse::<i32>().unwrap();
    i <= 2030 && i >= 2020
}

fn is_valid_hgt(hgt: &str) -> bool {
    let cm_regex: Regex = Regex::new(r"^(\d+)cm$").unwrap();
    match cm_regex.find(hgt) {
        Some(hgt_in_cm) => {
            let s = String::from(hgt_in_cm.as_str());
            let i = s[0..(s.len() - 2)].parse::<i32>().unwrap();
            i <= 193 && i >= 150
        }
        None => {
            let in_regex: Regex = Regex::new(r"^\d+in$").unwrap();
            match in_regex.find(hgt) {
                Some(in_in_cm) => {
                    let s = String::from(in_in_cm.as_str());
                    let i = s[0..(s.len() - 2)].parse::<i32>().unwrap();
                    i <= 76 && i >= 59
                }
                None => false
            }
        }
    }
}

fn is_valid_hcl(hcl: &str) -> bool {
    let hcl_regex: Regex = Regex::new(r"^#[\da-f]{6}$").unwrap();
    hcl_regex.is_match(hcl)
}

fn is_valid_ecl(ecl: &str) -> bool {
    let valid_colors: Vec<&str> = vec!["amb", "blu", "brn", "gry", "grn", "hzl", "oth"];
    valid_colors.contains(&ecl)
}

fn is_valid_pid(pid: &str) -> bool {
    let pid_regex: Regex = Regex::new(r"^[\d]{9}$").unwrap();
    pid_regex.is_match(pid)
}

fn is_valid(passport: &HashMap<&str, &str>, validations: &HashMap<&str, Callback>) -> bool {
    let valid: bool = validations.iter().all(|(k, f)| passport.contains_key(k) && f(passport.get(k).unwrap()));
    valid
}

fn main() -> io::Result<()> {
    let mut validations: HashMap<&str, Callback> = HashMap::new();
    validations.insert("byr", is_valid_byr);
    validations.insert("iyr", is_valid_iyr);
    validations.insert("eyr", is_valid_eyr);
    validations.insert("hgt", is_valid_hgt);
    validations.insert("hcl", is_valid_hcl);
    validations.insert("ecl", is_valid_ecl);
    validations.insert("pid", is_valid_pid);

    let contents = fs::read_to_string("input")?;

    let input: Vec<&str> = contents.split("\n\n").collect();

    let passports: Vec<_> = input.iter().map(|rcd| -> HashMap<&str, &str> {
        let mut hash_map: HashMap<&str, &str> = HashMap::new();
        let x3: Vec<&str> = rcd.split_whitespace().collect();
        let x4: Vec<Vec<&str>> = x3.iter().map(|x| x.split(':').collect()).collect();
        for each_key_value in x4 {
            hash_map.insert(each_key_value.first().unwrap(), each_key_value.get(1).unwrap());
        };
        hash_map
    }).collect();


    let valid_passports = passports.iter()
        .filter(|p| is_valid(p, &validations))
        .count();

    println!("number of  passports:  {:?}", passports.len());
    println!("number of valid password:  {:?}", valid_passports);
    // println!("{:?}", vec);
    Ok(())
}