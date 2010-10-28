Since RAP 1.4 M3, we use a new JavaScript compressor to create the compressed
JavaScript file client.js (see bug 327451 [1]).  The new compressor is written
in Java and based on the YUI Compressor [2].  It can be checked out from
Github [3].

This compressor collects source files from the org.eclipse.rap.rwt.q07 project,
compresses and concatenates them.  Up on successful build the client.js in
org.eclipse.rap.rwt.q07/resources is replaced with the new version.  This file
is optimized for size and execution speed and thus hardly human readable.  For
debugging, use the debug-mode in RAP which uses the unchanged JavaScript-files.

[1] Bug 327451: [releng] Replace qooxdoo generator with a Java-based JS compressor
    https://bugs.eclipse.org/bugs/show_bug.cgi?id=327451
[2] YUI Compressor
    http://yuilibrary.com/projects/yuicompressor/
[3] The new compressor on Github
    http://github.com/ralfstx/rap-clientbuilder
