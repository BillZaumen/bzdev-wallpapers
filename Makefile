
DESTDIR =

DOCDIR = $(DESTDIR)/usr/share/doc/bzdev-wallpapers

WALLPAPER_DIR = $(DESTDIR)/usr/share/backgrounds
GNOME_PROPS = \
	$(DESTDIR)/usr/share/gnome-background-properties


JAVAC = javac -p /usr/share/bzdev --add-modules org.bzdev.graphics

JAVA = java -p /usr/share/bzdev --add-modules org.bzdev.graphics

IMAGES = BZDev-Hilbert_Curve_Gray_Brown-by_Bill_Zaumen.png \
	BZDev-Hilbert_Curve_Dark_Blue-by_Bill_Zaumen.png

all: $(IMAGES)

HilbertCurve.class: HilbertCurve.java
	$(JAVAC) HilbertCurve.java

BZDev-Hilbert_Curve_Gray_Brown-by_Bill_Zaumen.png: HilbertCurve.java Makefile
	$(MAKE) HilbertCurve.class
	$(JAVA) HilbertCurve $@ 0x544741 1 6

BZDev-Hilbert_Curve_Dark_Blue-by_Bill_Zaumen.png: HilbertCurve.java Makefile
	$(MAKE) HilbertCurve.class
	$(JAVA) HilbertCurve $@ 0x00134e 2 6

VERSION = 1.0.0

package: bzdev-wallpapers_$(VERSION)_all.deb

install: $(IMAGES) copyright changelog bzdev-wallpapers.xml
	install -d $(DOCDIR)
	install -m 0644 copyright $(DOCDIR)
	gzip -9 -n < changelog > changelog.gz
	install -m 0644 changelog.gz $(DOCDIR)
	rm changelog.gz
	install -d $(WALLPAPER_DIR)
	install -d $(GNOME_PROPS)
	for i in $(IMAGES); \
	do install -m 0644 $$i $(WALLPAPER_DIR) ; \
	done
	install -m 0644 bzdev-wallpapers.xml $(GNOME_PROPS)

bzdev-wallpapers_$(VERSION)_all.deb: $(IMAGES) copyright control changelog \
		bzdev-wallpapers.xml
	mkdir -p bzdev-wallpapers
	mkdir -p bzdev-wallpapers/DEBIAN
	$(MAKE) install DESTDIR=bzdev-wallpapers
	cp control bzdev-wallpapers/DEBIAN
	fakeroot dpkg-deb --build bzdev-wallpapers
	mv bzdev-wallpapers.deb bzdev-wallpapers_$(VERSION)_all.deb
	rm -rf bzdev-wallpapers
