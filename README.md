# building from git clone is currently broken with Makefile.in missing

# to build directly from source use this .tar.gz for the source in releases

# https://github.com/waifu-core/waifu-core/releases/download/02-22-2024/Waifu-Core-SOURCE-CODE-02-22-2024.tar

Waifu-Core staging tree 24.x
===========================



How do I build the software?
----------------------------

The most troublefree and reproducable method of building the repository is via the depends method:

    LINUX

    git clone https://github.com/waifu-core/waifu-Core
    cd waifu-core
    cd depends
    make -j12 HOST=x86_64-pc-linux-gnu  # -j12 for 12 cores adjust here
    cd ..
    ./autogen.sh
    CONFIG_SITE=$PWD/depends/x86_64-pc-linux-gnu/share/config.site ./configure --prefix=$PWD/waifu-shared-linux --disable-tests --disable-bench --disable-fuzz-binary
    make -j12 				# -j12 for 12 cores adjust here
    make -j12 install 		        # -j12 for 12 cores adjust here
    files will be placed into waifu-shared-linux folder

    WINDOWS 64-bit (ubuntu 22.04 build only see bitcoin/doc/build-windows.md)

    git clone https://github.com/waifu-io/waifu-core
    cd waifu-core
    cd depends
    make -j12 HOST=x86_64-w64-mingw32	# -j12 for 12 cores adjust here
    cd ..
    ./autogen.sh # not required when building from tarball
    CONFIG_SITE=$PWD/depends/x86_64-w64-mingw32/share/config.site ./configure --prefix=$PWD/waifu-shared-windows  --disable-fuzz-binary --disable-bench
    make -j12 				# -j12 for 12 cores adjust here
    make -j12 install   		# -j12 for 12 cores adjust here
    make -j12 deploy			# for setup.exe adjust 12 cores to your core count requires nsis see doc/build-windows.md for deps

![s1](https://github.com/c4pt000/Waifu-Core/releases/download/picture/first-attempt.png)

License
-------

Waifu-Core is released under the terms of the MIT license. See [COPYING](COPYING) for more information or see https://opensource.org/licenses/MIT.


