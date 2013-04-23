Lenskit-Graphchi-Native is a series of bindings for using Lenskit with the C++ version of Graphchi.

To use Lenskit-Graphchi-Native you must have

    * GCC (For compiling Graphchi)
    * Java >= 6
    * Maven
    * Mercurial (Optional)
    * At the time of writing this Graphchi is only runs on unix systems, this may change.

To setup this module, first download Graphchi from `www.code.google.com/p/graphchi`. This can be done with with

    hg clone https://code.google.com/p/graphchi

Then navigate to Graphchi's directory and build it with

    bash install.sh

Now you can create a maven project and add lenskit-graphchi-native as a dependency. To actually use it, make sure to define the path to graphchi
with `-Dgraphchi.location=path/to/graphchi`

For more information on creating a maven project to run lenskit, see the documentation of lenskit.