# advent-of-code-2020
Solving daily advent coding challenges from [Advent-of-code](https://adventofcode.com) in [Clojure](https://clojure.org/)

## Running
Prerequisites:
- Java/JDK
- [Clojure Deps and CLI](https://clojure.org/guides/getting_started)
### CLI
Tools.deps and the CLI will take care of everything, like fetching dependencies. 
The only thing left to do is, executing the alias 'run':
 `clj -A:run`

```
$ clj -A:run
Please specify a day/exercise to execute, the following are available:
1   find-two-entries              Find the two entries that sum to 2020; what do you get if you multiply them together?
1b  find-three-entries            Part two: In your expense report, what is the product of the three entries that sum to 2020?
2   count-invalid-pws             How many passwords are valid according to their policies?
2b  invalid-pws-by-pos            Part-two: How many passwords are valid according to position-based-policies?
3   part-one                      Starting at the top-left corner of your map and following a slope of right 3 and down 1, how many trees would you encounter?
3b  part-two-multiple-slopes      What do you get if you multiply together the number of trees encountered on each of the listed slopes?
4   count-invalid-passports       Count the number of valid passports - those that have all required fields. Treat cid as optional. In your batch file, how many passports are valid?
4b  passports-with-valid-fields   Count the number of valid passports - those that have all required fields and valid values. Continue to treat cid as optional. In your batch file, how many passports are valid?
5   highest-seat                  As a sanity check, look through your list of boarding passes. What is the highest seat ID on a boarding pass?
5b  missing-seat                  What is the ID of your seat?

clj -A:run 1
964875
```

### REPL

For an interactive code exploration the alias `clj -A:socket-repl` can be used.
Then connect your favorite IDE (e.g. IntelliJ with Cursive) and start exploring.

Especially for exploration but also as documentation, there are [Rich comments](https://betweentwoparens.com/rich-comment-blocks#rich-comment) at the bottom of the files. 

## Testing

Unfortunately there are no tests yet.
