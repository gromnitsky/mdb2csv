out := target
classes := $(out)/classes
Main-Class := com.sigwait.mdb2csv.App

java.src.path := src/main/java
java.src := $(shell find $(java.src.path) -type f -name \*.java)
java.dest := $(patsubst $(java.src.path)/%.java, $(classes)/$(java.src.path)/%.class, $(java.src))

vendor.dir := $(out)/dependency
classes: $(java.dest)
$(classes)/%.class: %.java $(out)/.dependency
	$(mkdir)
	javac $< -d $(classes)/$(java.src.path) -cp "$(vendor.dir)/*"

$(out)/.dependency: pom.xml
	mvn -B dependency:copy-dependencies
	touch $@

run: $(java.dest)
	java -cp "$(classes)/$(java.src.path):$(vendor.dir)/*" $(Main-Class)
jshell:; jshell --class-path "$(vendor.dir)/*"



jar := $(out)/main.jar
$(jar): manifest.txt $(java.dest)
	jar cvfm $(jar) $< -C $(classes)/$(java.src.path) .

$(out)/one-jar-boot/boot-manifest.mf: one-jar-boot-0.97.jar $(jar)
	$(mkdir)
	unzip -qo $< -d $(dir $@)
	rm -rf $(dir $@)/src $(dir $@)/META-INF $(dir $@)/lib
	mkdir -p $(dir $@)/main $(dir $@)/lib
	cp $(jar) $(dir $@)/main
	cp $(vendor.dir)/* $(dir $@)/lib
	echo One-Jar-Main-Class: $(Main-Class) >> $@

bundle := $(out)/$(shell adieu -pe '$$("project>artifactId, project>version").get().map(v => $$(v).text()).join`-`' < pom.xml).jar
bundle: $(bundle)
$(bundle): $(out)/one-jar-boot/boot-manifest.mf
	jar cfm $(bundle) $< -C $(dir $<) .

mkdir = @mkdir -p $(dir $@)
