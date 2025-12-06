module Main where

import Data.List.Split
import Data.List
import qualified Data.Set as Set

nextInvalid :: Int -> Int
nextInvalid code
    | mod (length strCode) 2 == 1 = nextInvalid (10 ^ (digits code))
    | otherwise = if (repeat < code) then (halfAdvance code) else repeat

    where
        strCode = show code
        half = div (digits code) 2
        repeat = dup (firstHalf code)

digits :: Int -> Int
digits n = length (show n)

firstHalf :: Int -> Int
firstHalf n = read (take half (show n))
    where half = div (digits n) 2

halfAdvance :: Int -> Int
halfAdvance n = read ((show successor) ++ (show successor)) :: Int
    where successor = (firstHalf n) + 1

dup :: Int -> Int
dup n = read ((show n) ++ (show n)) :: Int

codeTotals :: Int -> Int -> Int
codeTotals start end
    | end < next = 0
    | otherwise = next + (codeTotals (next+1) end)
    where next = nextInvalid start

start :: String -> Int
start range = read (head (splitOn "-" range))

end :: String -> Int
end range = read (last (splitOn "-" range))

sumFromRange :: String -> Int
sumFromRange rangeStr = codeTotals (start rangeStr) (end rangeStr)

divisors :: Int -> [Int]
divisors x = filter (\y -> (mod x y) == 0) [1..(x-1)]

toPattern :: Int -> Int -> Int
toPattern n divisor = read (repeatStr startPattern (div (digits n) divisor)) :: Int
    where startPattern = take divisor (show n)

repeatStr :: String -> Int -> String
repeatStr _ 0 = ""
repeatStr str n = str ++ repeatStr str (n - 1)

isWithin :: Int -> Int -> Int -> Bool
isWithin start end n = n >= start && n <= end

patterns :: Int -> [Int]
patterns n = map (toPattern n) (divisors (digits n))

dedup :: [Int] -> [Int]
dedup = Set.toList . Set.fromList

rangePatterns :: Int -> Int -> [Int]
rangePatterns start end = foldr union [] pts
    where pts = map (\n -> dedup (filter (isWithin start end) (patterns n))) [start..end]


rangeStrPatternTotal :: String -> Int
rangeStrPatternTotal range = foldr (+) 0 (rangePatterns (start range) (end range))

main :: IO ()
main = do
    contents <- readFile "input.in"
    let rangeList = splitOn "," (init contents)
    let start = 94888325
    let end = 95016472
    putStrLn (show (rangePatterns start end))
 --   putStrLn (show (map rangeStrPatternTotal rangeList))
 --   putStrLn (show (foldr (+) 0 (map rangeStrPatternTotal rangeList)))
