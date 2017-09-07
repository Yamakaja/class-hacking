# Extending final classes

This is a demonstration repository about how
final classes can be extended. The usefulness of this
technique is questionable at best, use at your own risk!

*Why would anybody even want to extend final classes?*

Don't ask [me](https://twitter.com/AgentK20/status/861640304566427651)

## Explanation

### Preparation

The following requires access to an instance of the Unsafe class, which can
be obtained like this:

```java
Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
theUnsafe.setAccessible(true);
Unsafe unsafe = theUnsafe.get(null);
```

### The process itself

First, we need to clear the final bit in the Klass' access modifiers
(A good data overview can be found [here](https://gist.github.com/0x277F/33b14fe2d8fc29735a2873fcd04b48ea)),
for that we need to obtain the class pointer first, which is conveniently
placed in the header of every object. There is just one more issue we have
to account for: [Compressed OOPs](https://wiki.openjdk.java.net/display/HotSpot/CompressedOops)
might cause the pointer to be stored in a different format.

*Obtaining the `Klass*`*

```java
Object target = ""; // Instance of the class that you want to remove the final modifier of
long klassPointer = unsafe.arrayIndexScale(Object[].class) == 4 ? (unsafe.getInt(target, 8L) & 0xFFFFFFFFL) << 3 : unsafe.getLong(target, 8L);
```

Now that we have the Klass pointer, we just have to edit the modifiers:

```java
unsafe.putInt(klassPointer + MODIFIER_OFFSET, unsafe.getInt(klassPointer + MODIFIER_OFFSET) & ~Modifier.FINAL);
unsafe.putInt(klassPointer + ACCESS_FLAG_OFFSET, unsafe.getInt(klassPointer + ACCESS_FLAG_OFFSET) & ~Modifier.FINAL);
```

(`MODIFIER_OFFSET = 152` and `ACCESS_FLAG_OFFSET = 156`)

And there we go, (in this case) String is no longer final!

### Usage

Now we still can't do anything with what we've achieved. The compiler
wont compile classes that extend a final class, and I can't do anything
about that. But wait! ASM and dynamic class generation step in to save
the day!

For an example please see this repository ;)

## Issues

When extending crucial JVM-own classes like java.lang.String you will
most likely run into JVM crashes, you might have better luck with other
classes though.
