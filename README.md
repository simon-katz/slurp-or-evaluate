# slurp-or-evaluate

A Clojure library that allows you to easily save and restore values
to and from file, intended to be used as part of a REPL-based
development workflow.

There are two main uses:
- Save time by avoiding repeated re-computation of things that take a long
time to compute.
- Save values obtained from online sources, allowing you to use those values
when offline.

## Usage

### Basics

For a Leiningen project, use the following dependency:

```
[com.nomistech/slurp-or-evaluate "0.1.2"]
```

Add the following to your `ns` declaration:

```
(:require [com.nomistech.slurp-or-evaluate :refer :all])
```

Use as follows:

```
(def-expensive foo ...my-expensive-computation...)
```

This is the same as `def`, except:
- If `def-expensive` has a saved value, it will use that instead of evaluating
`...my-expensive-computation...`.
- If `def-expensive` does not have a saved value, it will save the result of
evaluating `...my-expensive-computation...` to file for future use.

You can also have a doc string as follows:

```
(def-expensive foo "my doc string" ...my-expensive-computation...)
```

#### Storage Directory

slurp-or-evaluate stores data in the directory `_slurp-or-evaluate-store/`.

You may wish to exclude that directory from version control
(e.g. add it to your `.gitignore` file).

#### Var Names

The name of the var being defined must be a valid filename on
your system, because
slurp-or-evaluate uses the name of the var as the name
of the file in which to store the saved value (within the
`_slurp-or-evaluate-store/` directory).

#### Removing a Saved Value

To remove slurp-or-evaluate's saved value for a var, you can either remove
its file from the `_slurp-or-evaluate-store/` directory or you can use
`def-expensive-replacing` to force an update.

A call of `def-expensive-replacing` would probably only exist temporarily
— the idea is that you would change a call of `def-expensive` to a call
of `def-expensive-replacing`, evaluate it, and then change it back.

#### Changing the Storage Directory

You can change where slurp-or-evaluate stores data with:
```
(alter-var-root #'slurp-or-evaluate-store-dir (constantly "my-store-dir"))
```

## License

Copyright © 2016 Simon Katz

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
