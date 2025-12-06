-- module Main where

import Data.List.Split

nextInvalid :: Int -> Int
nextInvalid code
    | mod (length strCode) 2 == 1 = halfAdvance code
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

