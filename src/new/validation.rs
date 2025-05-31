use lazy_static::lazy_static;
use regex::Regex;

lazy_static! {
    pub static ref GROUP_ID_REGEX: Regex =
        Regex::new(r"^[a-zA-Z][a-zA-Z0-9]*(\.[a-zA-Z][a-zA-Z0-9]*)*$").unwrap();
    pub static ref VERSION_REGEX: Regex = Regex::new(r"^\d+\.\d+\.\d+$").unwrap();
}

pub const VALID_JAVA_VERSIONS: &[&str] = &["1.8", "11", "17", "21"];
pub const VALID_BUILD_ENGINES: &[&str] = &["maven", "gradle"];
