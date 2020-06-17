import org.bzdev.gio.*;
import org.bzdev.graphs.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;

public class HilbertCurve {
    Path2D path = new Path2D.Double();
    double x;
    double y;
    double stepsize;

    public Path2D getPath() {return path;}

    public HilbertCurve(int max, double width) {
	x = 0.0;
	y = 1024.0;
	path.moveTo(0.0, y);
	stepsize = width;
	for (int i = 0; i < max-1; i++) {
	    stepsize /= 2.0;
	}
	drawA(0, max, 0);
    }

    private int drawA(int depth, int max, int angle) {
	if (depth == max) return angle;
	depth++;
	angle -= 90;
	angle = drawB(depth, max, angle);
	forward(angle);
	angle += 90;
	angle = drawA(depth, max, angle);
	forward(angle);
	angle = drawA(depth, max, angle);
	angle += 90;
	forward(angle);
	angle = drawB(depth, max, angle);
	angle -= 90;
	return angle;
    }

    private int drawB(int depth, int max, int angle) {
	if (depth == max) return angle;
	depth++;
	angle += 90;
	angle = drawA(depth, max, angle);
	forward(angle);
	angle -= 90;
	angle = drawB(depth, max, angle);
	forward(angle);
	angle = drawB(depth, max, angle);
	angle -= 90;
	forward(angle);
	angle = drawA(depth, max, angle);
	angle += 90;
	return angle;
    }

    private void forward(int angle) {
	while (angle < 0) {
	    angle += 360;
	}
	angle = angle % 360;

	switch (angle) {
	case 0:
	    x += stepsize;
	    break;
	case 90:
	    y += stepsize;
	    break;
	case 180:
	    x -= stepsize;
	    break;
	case 270:
	    y -= stepsize;
	    break;
	default:
	    System.err.println("bad angle: " + angle);
	    System.exit(1);
	}
	path.lineTo(x, y);
    }

    // original color was 0x544741, original level = 3, original depth = 6

    public static void main(String argv[]) throws Exception {
	String file = argv[0];
	int red = 45, green = 47, blue = 41;
	if (argv[1].startsWith("0x") || argv[1].startsWith("0X")) {
	    red = Integer.parseInt(argv[1], 2, 4, 16);
	    green = Integer.parseInt(argv[1], 4, 6, 16);
	    blue = Integer.parseInt(argv[1], 6, 8, 16);
	} else {
	    System.err.println("parsing color failed: using default");
	}

	int level = Integer.parseInt(argv[2]);
	boolean brighter = (level > 0);
	level = Math.abs(level);

	int depth = Integer.parseInt(argv[3]);


	FileOutputStream os = new FileOutputStream(file);
	OutputStreamGraphics osg = OutputStreamGraphics
	    .newInstance(os, 3840, 2160, "png", false); 
	Graph graph = new Graph(osg);
	graph.setOffsets(25, 25, 25, 75);
	HilbertCurve hc = new HilbertCurve(6, 1024.0);
	Path2D path  = hc.getPath();
	Rectangle2D bounds = path.getBounds2D();
	graph.setRanges(bounds.getMinX(), bounds.getMaxX(),
			bounds.getMinY(), bounds.getMaxY());
	Graphics2D g2d = graph.createGraphics();

	Color bg = new Color(red, green, blue);
	g2d.setColor(bg);
	g2d.setStroke(new BasicStroke(1.0F));
	g2d.drawRect(0, 0, 3840, 2160);
	g2d.fillRect(0, 0, 3840, 2160);
	g2d.setStroke(new BasicStroke(3.0F));
	Color fg = bg;
	if (brighter) {
	    for (int i = 0; i < level; i++) {
		fg = fg.brighter();
	    }
	} else {
	    for (int i = 0; i < level; i++) {
		fg = fg.darker();
	    }
	}
	g2d.setColor(fg);
	// g2d.setColor(Color.BLACK);
	graph.draw(g2d, path);
	graph.write();
    }
    
}
