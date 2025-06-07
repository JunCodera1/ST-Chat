{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = [
    pkgs.openjdk17
    pkgs.openjfx17
    pkgs.glib
    pkgs.gtk3
    pkgs.zlib
    pkgs.libGL
    pkgs.xorg.libX11
    pkgs.xorg.libXtst
    pkgs.xorg.libXrender
    pkgs.xorg.libXi
    pkgs.cairo
    pkgs.gdk-pixbuf
  ];

  shellHook = ''
    export LD_LIBRARY_PATH="${pkgs.glib.out}/lib:${pkgs.gtk3.out}/lib:${pkgs.zlib.out}/lib:${pkgs.libGL.out}/lib:${pkgs.xorg.libXtst.out}/lib:${pkgs.xorg.libX11.out}/lib:${pkgs.xorg.libXrender.out}/lib:${pkgs.xorg.libXi.out}/lib:${pkgs.cairo.out}/lib:${pkgs.gdk-pixbuf.out}/lib:$LD_LIBRARY_PATH"
    echo "JavaFX environment ready."
  '';
}
