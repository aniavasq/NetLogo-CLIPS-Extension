## Example for Creating jar

```
javac -cp "lib/*:/Applications/NetLogo 6.1.1/Java/*" -d classes $(find . -name "*.java")
jar cvfm clips.jar manifest.txt -C classes .
```