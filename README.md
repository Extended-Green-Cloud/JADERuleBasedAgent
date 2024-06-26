# JADER Rule-Based Agent

The library extends the standard JADE agent platform (https://jade.tilab.com/) by introducing rule-based agent behaviours.
It combines:
1. **Rule-Based Expert System** (where initial RETE algorithm and Rule-Based Engine were taken from [Easy Rules](https://github.com/j-easy/easy-rules) library)
2. **Expression Languages** ([MVEL2](https://github.com/mvel/mvel))
Within the library, the developer can find a set of methods and tools that are needed in order to implement RES agents.

To include the library, it is enough to add the Maven dependency:

Maven:
```
<dependency>
    <groupId>io.github.extended-green-cloud</groupId>
    <artifactId>jrba</artifactId>
    <version>1.2</version>
</dependency>
```

Gradle:
```
implementation 'io.github.extended-green-cloud:jrba:1.2'
```
