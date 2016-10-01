# Mycraft

Mycraft is a simple [Minecraft][] clone written in Java, using [LWJGL][]. It is [Mitchell Kember](https://github.com/mk12)'s final summative project for the ICS2O course.

A modern build system on top of Mitchell's project has been added to this fork (you can now build with SBT instead of just in NetBeans); see notes on building below.


[Minecraft]: http://minecraft.net
[LWJGL]: http://lwjgl.org

## Building

### SBT

You can build and run simply using [SBT](http://www.scala-sbt.org/) by doing `sbt run` from the directory, after you have installed `sbt` on your `PATH`.

The library dependencies under `lib/` have been left in this repo; if you are forking this project yourself you may consider removing them straight away since `sbt` will download them regardless.

### NetBeans

Note, the `com` package was moved from `src/` to `src/main/java/`; this may break the build, but I haven't tested it - if so, just try to create a link from `src/main/java/com` to `src/com`, or preferably, fix the NetBeans build and submit a pull request.

Mycraft is developed using [NetBeans][]. Before building Mycraft, go to the properties window of the project, click the **Run** node, and in the VM Options, change the last item of the LWJGL directory path to  your operating system (`windows`, `macosx`, or `linux`).

[NetBeans]: http://netbeans.org

## License

"Minecraft" is an official trademark of Mojang AB. This work is not formally related to, endorsed by, or affiliated with Minecraft or Mojang AB.

© 2012 Mitchell Kember

Mycraft is available under the MIT License; see [LICENSE](LICENSE.md) for details.
