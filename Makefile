ifeq ($(origin JAVA_HOME), undefined)
  JAVA_HOME=/usr
endif

ifeq ($(origin NETLOGO), undefined)
  NETLOGO=/Applications/NetLogo 6.1.1/Java # Mac Directory
endif

ifeq ($(origin JARSPATH), undefined)
  JARSPATH=lib/*
endif

ifneq (,$(findstring CYGWIN,$(shell uname -s)))
  COLON=\;
  JAVA_HOME := `cygpath -up "$(JAVA_HOME)"`
else
  COLON=:
endif

ifeq ($(origin CLIPSJNI), undefined)
  CLIPSJNI=./CLIPSJNI
endif

VERSION=-1.0.0
JAVAC=$(JAVA_HOME)/bin/javac
SRCS=$(shell find . -name "*.java")

clips.jar clips.jar.pack.gz: $(SRCS) manifest.txt Makefile $(JARS)
	mkdir -p classes
	$(JAVAC) -cp "$(JARSPATH)$(COLON)$(NETLOGO)/*" -d classes $(shell find . -name "*.java")
	jar cmf manifest.txt clips.jar -C classes .
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip clips.jar.pack.gz clips.jar
	# Crear zip
	$(MAKE) clips.zip

clips.zip:
	rm -rf clips
	mkdir clips
	cp -rp clips.jar clips.jar.pack.gz README.md LICENSE CLIPSJNI clips
	zip -rv clips.zip clips
	rm -rf clips

clips$(VERSION).zip:
	rm -rf clips$(VERSION)
	mkdir clips$(VERSION)
	$(MAKE) clips.jar
	mv clips.jar clips$(VERSION).jar
	cp -rp clips$(VERSION).jar CLIPSJNI clips$(VERSION)
	zip -rv clips$(VERSION).zip clips$(VERSION)
	rm -rf clips$(VERSION)


.PHONY : clean
clean:
	rm clips.jar clips.jar.pack.gz clips.zip
