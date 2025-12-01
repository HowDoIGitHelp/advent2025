use std::fs::File;
use std::path::Path;
use std::io::{self,BufRead,BufReader};


fn main() -> io::Result<()> {
    let file = File::open(Path::new("input.in"))?;

    let reader = BufReader::new(file);
    let mut dial_position = 2000050;
    let mut zeroes = 0;

    for line_result in reader.lines() {
        let line = line_result?;
        let direction = &line[0..1];
        let distance: i32 = line[1..].parse().expect("invalid distance");
        let new_position;
        if direction == "L" {
            new_position = dial_position - distance;
            if dial_position % 100 == 0 {
                zeroes -= 1;
            }
            if new_position % 100 == 0 {
                zeroes += 1;
            }
            zeroes += (dial_position / 100) - (new_position / 100);
            
        } else {
            new_position = dial_position + distance;
            zeroes += (new_position / 100) - (dial_position / 100);
        }

        
        dial_position = new_position;
        println!("position: {dial_position}");
        println!("zeroes: {zeroes}");
    }
    println!("{zeroes}");
    Ok(())
}
