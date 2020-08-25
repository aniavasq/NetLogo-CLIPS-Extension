# NetLogo CLIPS extension

This package contains the NetLogo CLIPS extension. This allows you to run CLIPS code inside of the NetLogo environment. There is no need to install CLIPS because the extension includes the CLIPS JNI that contains all the CLIPS functionality. The CLIPS version compatible with this extension is the v6.3.

## Building

It's required to have installed JDK7 or a newer version in order to build the extension. Also, Git and Make are required.

1. Clone the NetLogo CLIPS extension repository

   ```
   git clone https://github.com/aniavasq/NetLogo-CLIPS-Extension.git
   ```

2. Enter to the `NetLogo-CLIPS-Extension` folder

   ```
   cd NetLogo-CLIPS-Extension
   ```

3. And build the extension

   ```
   make
   ```

4. If compilation succeeds, a `clips.zip` that contains the `.jar` file and the dependencies will be generated. The `clips.zip` needs to be unzipped in the root of the NetLogo extensions directory or any of the compatible directories that NetLogo uses for search extension, **[check here](https://ccl.northwestern.edu/netlogo/docs/extensions.html#using-extensions)**.

   ```
   unzip clips.zip -d /path/to/NetLogo/extension/directory
   ```


## Using

You must declare that you're using this extension in your NetLogo code with:

```
extensions [
  clips
  ; other extensions...
]
```

The workflow of this extension is very similar to the workflow of a CLIPS program, but first you have to create a CLIPS environment. After that you can run any code with the `eval` reporter or assert facts with `assert-string`.

```
observer> clips:create-env "myenv"
observer> show clips:assert-string "myenv" "(a-fact)"
observer: "<Fact-1>"
observer> show (clips:find-fact "myenv" "a-fact")
observer: "<Fact-1>"
observer> show (clips:eval "myenv" "(do-for-fact ((?f a-fact)) TRUE (retract ?f))")
observer: ""
observer> show (clips:find-fact "myenv" "a-fact")
observer: "null"
observer> clips:clear "myenv"
observer> clips:destroy "myenv"
```

Any CLIPS errors will be reported in NetLogo as "Extension exceptions". For more information about CLIPS functionality checkout the **[CLISP Reference Manual](http://clipsrules.sourceforge.net/documentation/v630/bpg.pdf)**

## Primitives

### `clips:create-env`

```
clips:create-env <environment-name>
```

Creates a CLIPS environment, this is a thread that will be running CLIPS code. It takes as parameter a string that represents the environment name. Multiple environments can be created and you need to specify the environment name for each one, in that case they will be running separately and will have different fact-lists and agendas.

```
observer> clips:create-env "env1"
observer> show clips:assert-string "env1" "(myfact)"
observer: "<Fact-1>"

observer> clips:create-env "env2"
observer> show clips:assert-string "env2" "(myfact)"
observer: "<Fact-1>"
```

### `clips:load`

```
clips:load <environment-name> <filename>
```

Loads a `.clp` file that contains CLIPS code to a CLIPS environment. It takes as parameters: the string that represents the environment name and the filename. CLIPS code files must be located in a directory named `rules` in the same directory that the running `.nlogo` file is placed.

```
observer> show clips:load "env" "production_rules.clp"
observer: "/absolute/path/to/the/file/proeduction_rules.clp"
```

### `clips:reset`

```
clips:reset <environment-name>
```

Resets a CLIPS environment. It takes as parameter the string that represents the environment. Removes all activations from the agenda, all facts from the fact-list and all instances of user-defined classes, then assigns global variables their initial values, asserts all facts  listed in `deffacts` statements into the fact-list, creates all instances listed in `definstances` statements, sets the current module to the MAIN module and automatically focuses on the same module.

```
observer> clips:reset "env"
```

### `clips:run`

```
clips:run <environment-name> [<run-steps>]
```

Starts execution of the rules in a given environment. It takes as parameters: the string that represents the environment name and as optional the number of run steps. If the optional second argument is positive, execution will cease after the specified number of rule firings or when the agenda contains no rule activations. If there is no second argument or is a negative integer, execution will cease when the agenda contains no rule activations.

```
observer> clips:run "env" 1
```

### `clips:assert-string`

```
clips:assert-string <environment-name> <string-expression>
```

It will add a fact to the fact-list of a given environment. It takes as parameters: the string that represents the environment name and a string expression representing a fact. The `string-expression` must be expressed in either ordered or `deftemplate` format. Only one fact may be asserted with each `assert-string` statement.

```
observer> clips:assert-string "env" "(a-fact)"
observer: "<Fact-1>"
observer> clips:assert-string "env" "(my-template (slot-val 1))"
observer: "<Fact-2>"
```

### `clips:find-fact`

```
clips:find-fact <environment-name> <fact-set-template> [<variable>] [<condition>]
```

Applies a query to each fact-set which matches the template. It takes as parameters: the string that represents the environment name, the fact set template name; and as optional paratemers: the variable name that will be used in the query condition and the query condition itself. If a fact satisfies the query, then the function is immediately terminated, and the fact is returned by the reporter. If multiple facts satifies the query, the first fact is returned by the reporter.

```
observer> show (clips:find-fact "env" "a-fact")
observer: "<Fact-1>"
observer> show clips:assert-string "env" "(person (name \"Jhon\") (age 23))"
observer: "<Fact-2>"
observer> show clips:assert-string "env" "(person (name \"Mary\") (age 18))"
observer: "<Fact-3>"
observer> show (clips:find-fact "env" "person" "?p" "(> ?p:age 21)")
observer: "<Fact-2>"
```

### `clips:get-slot-value`

```
clips:get-slot-value <environment-name> <fact-set-template> <slot-name> [<variable>] [<condition>]
```

Returns the value of the specified slot from the specified fact. It takes as parameters: the string that represents the environment name, the fact set template name, the slot name from the template; and as optional paratemers: the variable name that will be used in the query condition and the query condition itself.

```
observer> show (clips:get-slot-value "env" "person" "age" "?p" "(> ?p:age 21)")
observer: "23"
```

### `clips:eval`

```
clips:eval <environment-name> <eval-string>
```

Injects CLIPS code into an environment. Multi-line code is not supported. It takes as parameters: the string that represents the environment name and the string to eval.

```
observer> show (clips:eval "env" "(any-factp ((?f a-fact)) TRUE)")
observer: "TRUE"
```

### `clips:clear`

```
clips:clear <environment-name>
```

Clears a CLIPS environment. Removes all constructs and all associated data structures (such as facts and instances) from the CLIPS environment.

```
observer> clips:clear "env"
```

### `clips:destroy`

```
clips:destroy <environment-name>
```

Destroys the CLIPS environment.

```
observer> clips:destroy "env"
```
