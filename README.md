## Example for Creating jar

```
javac -cp "lib/*:/Applications/NetLogo 6.1.1/Java/*" -d classes $(find . -name "*.java")
jar cvfm clips.jar manifest.txt -C classes .
```

## Signing Library for MacOS

```
codesign -s "Anibal Vasquez: Universidad de Sevilla - MULCIA" --force --deep --verbose libCLIPSJNI.jnilib
```